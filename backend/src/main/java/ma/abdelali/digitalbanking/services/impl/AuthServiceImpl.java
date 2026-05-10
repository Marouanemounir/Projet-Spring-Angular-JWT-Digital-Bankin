package ma.abdelali.digitalbanking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.abdelali.digitalbanking.dtos.AuthResponse;
import ma.abdelali.digitalbanking.dtos.ChangePasswordRequest;
import ma.abdelali.digitalbanking.dtos.LoginRequest;
import ma.abdelali.digitalbanking.dtos.RegisterRequest;
import ma.abdelali.digitalbanking.entities.AppRole;
import ma.abdelali.digitalbanking.entities.AppUser;
import ma.abdelali.digitalbanking.exceptions.UserAlreadyExistsException;
import ma.abdelali.digitalbanking.exceptions.InvalidOperationException;
import ma.abdelali.digitalbanking.repositories.AppRoleRepository;
import ma.abdelali.digitalbanking.repositories.AppUserRepository;
import ma.abdelali.digitalbanking.security.JwtProvider;
import ma.abdelali.digitalbanking.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create new user
        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setEnabled(true);

        // Assign default USER role
        AppRole userRole = appRoleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new InvalidOperationException("Default ROLE_USER not found"));
        
        Set<AppRole> roles = new HashSet<>();
        roles.add(userRole);
        appUser.setRoles(roles);

        AppUser savedUser = appUserRepository.save(appUser);
        log.info("User registered: {}", savedUser.getUsername());

        // Generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        return buildAuthResponse(authentication, savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        AppUser appUser = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidOperationException("User not found"));

        log.info("User logged in: {}", request.getUsername());
        return buildAuthResponse(authentication, appUser);
    }

    @Override
    public AuthResponse authenticate(Authentication authentication) {
        AppUser appUser = appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new InvalidOperationException("User not found"));
        return buildAuthResponse(authentication, appUser);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new InvalidOperationException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), appUser.getPassword())) {
            throw new InvalidOperationException("Old password is incorrect");
        }

        // Update password
        appUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        appUserRepository.save(appUser);
        log.info("Password changed for user: {}", appUser.getUsername());
    }

    private AuthResponse buildAuthResponse(Authentication authentication, AppUser appUser) {
        String token = jwtProvider.generateToken(authentication);
        
        Set<String> roles = appUser.getRoles().stream()
                .map(AppRole::getName)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(appUser.getId())
                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .roles(roles)
                .build();
    }
}
