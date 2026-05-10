package ma.abdelali.digitalbanking.services;

import ma.abdelali.digitalbanking.dtos.AuthResponse;
import ma.abdelali.digitalbanking.dtos.ChangePasswordRequest;
import ma.abdelali.digitalbanking.dtos.LoginRequest;
import ma.abdelali.digitalbanking.dtos.RegisterRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse authenticate(Authentication authentication);
    void changePassword(Long userId, ChangePasswordRequest request);
}
