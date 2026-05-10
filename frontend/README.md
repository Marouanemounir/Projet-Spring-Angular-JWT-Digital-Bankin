# Digital Banking Frontend

Angular 18 frontend for Digital Banking application with JWT authentication.

## Technology Stack

- **Angular 18**
- **TypeScript 5.5**
- **Bootstrap 5**
- **RxJS**
- **Chart.js** (ng2-charts)
- **Node.js 18+**
- **npm**

## Project Setup

### Prerequisites

- Node.js 18+ installed
- npm 9+ installed
- Angular CLI 18+

### Installation

```bash
# Install Angular CLI globally (optional)
npm install -g @angular/cli

# Install project dependencies
npm install
```

### Running the Application

**Development Server:**
```bash
npm start
# or
ng serve --open
```

Application runs on `http://localhost:4200`

**Build for Production:**
```bash
npm run build:prod
# or
ng build --configuration production
```

Output: `dist/frontend/`

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── guards/            # Route guards
│   │   │   ├── interceptors/      # HTTP interceptors
│   │   │   └── services/          # Core services
│   │   ├── features/
│   │   │   ├── auth/              # Authentication
│   │   │   ├── dashboard/         # Dashboard
│   │   │   ├── customers/         # Customers management
│   │   │   ├── accounts/          # Account management
│   │   │   └── chatbot/           # AI Chatbot
│   │   ├── layout/
│   │   │   └── navbar/            # Navigation
│   │   ├── shared/
│   │   │   ├── components/        # Shared components
│   │   │   └── pipes/             # Custom pipes
│   │   ├── app.routes.ts          # Main routing
│   │   └── app.component.ts       # Root component
│   ├── styles.css                 # Global styles
│   ├── index.html                 # HTML entry point
│   └── main.ts                    # Application bootstrap
├── angular.json                   # Angular CLI config
├── tsconfig.json                  # TypeScript config
├── package.json                   # Dependencies
├── Dockerfile                     # Docker image
└── README.md
```

## Key Features

### Authentication
- JWT-based authentication
- Login & Register pages
- Auth guard for protected routes
- JWT interceptor for API requests
- Error handling interceptor

### Services
- **AuthService**: Handles authentication and user state
- **HttpClient**: API communication via Angular HttpClient
- JWT token management with localStorage

### Components
- **Navbar**: Navigation with login/logout
- **Login**: User authentication
- **Register**: New user registration
- **Dashboard**: Statistics and metrics
- **Customers**: Customer management (placeholder)
- **Accounts**: Account management (placeholder)
- **Chatbot**: AI assistant (placeholder)

### Security
- JWT token stored in localStorage
- Authorization header automatically added via interceptor
- Automatic logout on 401 response
- Protected routes with AuthGuard

## Configuration

### Backend API URL

Backend API URL is hardcoded in services:
```
http://localhost:8085/api
```

To change, update the `apiUrl` in service files.

### CORS

Backend must be configured with CORS for `http://localhost:4200`

## Testing

```bash
# Run unit tests
npm test

# Run tests with coverage
npm run test:coverage

# Run e2e tests
npm run e2e
```

## Building for Docker

```bash
# Build Docker image
docker build -t digital-banking-frontend .

# Run container
docker run -p 4200:80 digital-banking-frontend
```

Application runs on `http://localhost:4200`

## Development Workflow

### Adding New Features

1. Create route in appropriate feature folder
2. Create component with `ng generate`
3. Add route to feature routes file
4. Create service for API calls in `core/services/`
5. Create HTTP interceptor if needed
6. Add Angular Material icons if needed

### Code Style

- Use standalone components (Angular 14+)
- Use functional interceptors
- Prefer functional programming with RxJS
- Use signals for state management (Angular 17+)
- Follow Angular style guide

### Naming Conventions

- Components: `feature.component.ts`
- Services: `feature.service.ts`
- Routes: `feature.routes.ts`
- Interfaces: `feature.model.ts`

## Troubleshooting

### Port Already in Use

```bash
# Change port
ng serve --port 4300
```

### Dependencies Issues

```bash
# Clear node_modules and reinstall
rm -rf node_modules
npm install
```

### Backend Connection Error

- Verify backend is running on `http://localhost:8085`
- Check CORS configuration in backend
- Check browser console for actual error

### Build Errors

```bash
# Clean build
ng build --configuration production --aot
```

## Performance Tips

- Use OnPush change detection strategy
- Lazy load feature modules
- Use trackBy in *ngFor
- Unsubscribe from observables
- Use async pipe in templates
- Implement virtual scrolling for large lists

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Create feature branch
2. Implement changes
3. Write tests
4. Submit pull request

## License

MIT License

## Support

For issues or questions, contact the development team or open an issue on GitHub.

---

**Last Updated**: 2024
**Maintained By**: Digital Banking Team
