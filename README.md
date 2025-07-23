# Spring Boot Application

A comprehensive Spring Boot application with user management, authentication, and RESTful APIs.

## 📚 Documentation

This project includes comprehensive documentation covering all aspects of the application:

### 🔗 Quick Links

- **[API Documentation](./API_DOCUMENTATION.md)** - Complete REST API reference with examples
- **[Component Documentation](./COMPONENT_DOCUMENTATION.md)** - Detailed component architecture and usage
- **[Developer Guide](./DEVELOPER_GUIDE.md)** - Setup instructions, workflows, and best practices

### 📖 What's Included

#### API Documentation
- Complete REST endpoint reference
- Authentication and security
- Request/response examples
- Error handling
- Rate limiting
- Client libraries (JavaScript/TypeScript)

#### Component Documentation
- Controllers, Services, Repositories
- Security components (JWT, filters)
- Data models and validation
- Exception handling
- Configuration classes
- Utility components

#### Developer Guide
- Environment setup
- Project structure
- Development workflow
- Testing strategies
- Deployment instructions
- Performance optimization
- Troubleshooting guide

## 🚀 Quick Start

```bash
# Clone the repository
git clone <repository-url>
cd springBoot

# Set up environment
cp .env.example .env
# Edit .env with your configuration

# Build and run
mvn clean install
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🏗️ Architecture

```
┌─────────────────────────────────────┐
│            Presentation Layer       │
│         (Controllers, DTOs)         │
├─────────────────────────────────────┤
│             Service Layer           │
│        (Business Logic)             │
├─────────────────────────────────────┤
│           Repository Layer          │
│         (Data Access)               │
├─────────────────────────────────────┤
│            Entity Layer             │
│         (Domain Models)             │
└─────────────────────────────────────┘
```

## 🔧 Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL 8.0 / PostgreSQL 13+
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, Mockito, TestContainers
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Build**: Maven
- **Containerization**: Docker

## 🔒 Security Features

- JWT-based authentication
- Role-based access control
- Password encryption (BCrypt)
- Rate limiting
- CORS configuration
- Security headers

## 📊 API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - User logout

### User Management
- `GET /api/v1/users` - List users (Admin)
- `GET /api/v1/users/{id}` - Get user by ID
- `POST /api/v1/users` - Create user (Admin)
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user (Admin)

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test categories
mvn test -Dtest="*UnitTest"
mvn test -Dtest="*IntegrationTest"

# Generate coverage report
mvn jacoco:report
```

## 🐳 Docker Support

```bash
# Build and run with Docker Compose
docker-compose up -d

# Build Docker image
docker build -t spring-boot-app .

# Run container
docker run -p 8080:8080 spring-boot-app
```

## 📈 Monitoring

The application includes Spring Boot Actuator endpoints:

- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Metrics
- `/actuator/prometheus` - Prometheus metrics

## 🔍 API Documentation (Swagger)

When running locally, visit:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For detailed documentation and examples, please refer to:

- [API Documentation](./API_DOCUMENTATION.md) for REST API usage
- [Component Documentation](./COMPONENT_DOCUMENTATION.md) for architecture details  
- [Developer Guide](./DEVELOPER_GUIDE.md) for setup and development

---

*This project demonstrates modern Spring Boot development practices with comprehensive documentation, testing, and deployment strategies.*
