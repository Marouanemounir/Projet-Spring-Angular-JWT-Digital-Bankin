# Digital Banking Backend

Spring Boot 3.x REST API for digital banking operations with JWT authentication.

## Technology Stack

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**
- **Lombok**
- **Swagger OpenAPI**

## Project Setup

### Prerequisites

- Java 21 JDK installed
- Maven 3.8+
- MySQL 8.0+ (or H2 for development)
- Git

### Installation

```bash
# Clone the repository
git clone <repo-url>
cd backend

# Install dependencies
mvn clean install

# Run tests
mvn test

# Build
mvn clean package
```

### Configuration

Create `.env` file in project root:

```env
DB_URL=jdbc:mysql://localhost:3306/digital_bank?createDatabaseIfNotExist=true
DB_USERNAME=root
DB_PASSWORD=yourpassword
JWT_SECRET=your-256-bit-secret-key
OPENAI_API_KEY=sk-your-key
TELEGRAM_BOT_TOKEN=your-telegram-token
SERVER_PORT=8085
APP_PROFILE=dev
```

### Running the Application

**Development (H2 In-Memory):**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**With MySQL:**
```bash
# Create database
mysql -u root -p
CREATE DATABASE digital_bank;

# Run application
mvn spring-boot:run
```

**Production Build:**
```bash
mvn clean package -DskipTests -Pprod
java -jar target/digital-banking-app.jar --spring.profiles.active=prod
```

### Accessing the Application

- **API Base URL**: `http://localhost:8085`
- **Swagger UI**: `http://localhost:8085/swagger-ui/index.html`
- **H2 Console** (dev only): `http://localhost:8085/h2-console`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /api/auth/me` - Get current user info
- `POST /api/auth/change-password` - Change password

### Customers
- `GET /api/customers` - List all customers
- `GET /api/customers/search?keyword=&page=0&size=10` - Search customers
- `GET /api/customers/{id}` - Get customer details
- `POST /api/customers` - Create customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer
- `GET /api/customers/{id}/accounts` - Get customer accounts

### Bank Accounts
- `GET /api/accounts` - List all accounts
- `GET /api/accounts/search?keyword=&page=0&size=10` - Search accounts
- `GET /api/accounts/{accountId}` - Get account details
- `POST /api/accounts/current` - Create current account
- `POST /api/accounts/saving` - Create saving account
- `GET /api/accounts/{accountId}/operations` - Get operations
- `GET /api/accounts/{accountId}/pageOperations?page=0&size=5` - Paginated operations
- `POST /api/accounts/debit` - Debit operation
- `POST /api/accounts/credit` - Credit operation
- `POST /api/accounts/transfer` - Transfer money

### Dashboard
- `GET /api/dashboard/stats` - Overall statistics
- `GET /api/dashboard/monthly-operations` - Monthly stats
- `GET /api/dashboard/account-types` - Account type distribution
- `GET /api/dashboard/account-statuses` - Account status distribution

### Chatbot
- `POST /api/chat` - Send message to chatbot
- `GET /api/chat/health` - Check chatbot health

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/ma/abdelali/digitalbanking/
│   │   │   ├── entities/             # JPA entities
│   │   │   ├── enums/                # Business enums
│   │   │   ├── repositories/         # Data access layer
│   │   │   ├── dtos/                 # Data transfer objects
│   │   │   ├── services/             # Business logic
│   │   │   ├── web/controllers/      # REST endpoints
│   │   │   ├── security/             # JWT & auth
│   │   │   ├── exceptions/           # Exception handling
│   │   │   ├── config/               # Spring configuration
│   │   │   ├── audit/                # Audit logging
│   │   │   └── DigitalBankingApplication.java
│   │   └── resources/
│   │       ├── application.yml       # Main config
│   │       └── application-{profile}.yml
│   └── test/
│       ├── java/                     # Unit & integration tests
│       └── resources/
├── pom.xml                           # Maven configuration
├── Dockerfile                        # Docker image
└── README.md
```

## Development Workflow

### Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=CustomerServiceTest

# With coverage
mvn clean test jacoco:report
```

### Database Migrations

Hibernate automatically creates/updates schema with `ddl-auto: update` setting.

For production, change to `validate` to prevent accidental migrations.

### Adding New Features

1. Create entity in `entities/`
2. Create repository in `repositories/`
3. Create DTOs in `dtos/`
4. Create service interface in `services/`
5. Create service implementation in `services/impl/`
6. Create controller in `web/controllers/`
7. Add tests in `src/test/`

## Security

### JWT Configuration

- Secret key: `app.jwt.secret` (256+ bits recommended)
- Expiration: `app.jwt.expiration` (milliseconds)
- Algorithm: HS512

### Password Encryption

Passwords are encrypted using BCrypt with strength 10.

### Role-Based Access Control

- **ROLE_ADMIN**: Full system access
- **ROLE_MANAGER**: Account and customer management
- **ROLE_USER**: Personal operations

## Docker Deployment

```bash
# Build Docker image
docker build -t digital-banking-backend .

# Run container
docker run -p 8085:8085 \
  -e DB_URL=jdbc:mysql://mysql:3306/digital_bank \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e JWT_SECRET=your-secret \
  digital-banking-backend
```

## Troubleshooting

### Compilation Errors
```bash
# Clean build cache
mvn clean

# Rebuild
mvn compile
```

### Database Connection Issues
- Check MySQL is running
- Verify credentials in .env
- Check database exists
- Firewall settings for port 3306

### Port Already in Use
```bash
# Change port in application.yml or .env
SERVER_PORT=8086
```

### JWT Token Errors
- Token may be expired (24-hour expiration)
- Log in again to get new token
- Verify Authorization header format: `Bearer <token>`

## Performance Tips

- Use pagination for large datasets
- Index frequently searched columns
- Enable connection pooling
- Use caching for read-heavy operations
- Monitor database query performance

## Logging

Logging is configured in `application.yml`:
- **DEV**: DEBUG level for detailed logs
- **PROD**: INFO level for performance

Logs are printed to console. Add file logging in production.

## Contributing

1. Create feature branch
2. Implement changes
3. Write/update tests
4. Submit pull request

## License

MIT License - See LICENSE file

## Support

For issues or questions, open an issue on GitHub or contact the development team.

---

**Last Updated**: 2026
**Maintained By**: Marouane Mounir 
