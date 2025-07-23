# Spring Boot API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [REST Endpoints](#rest-endpoints)
4. [Data Models](#data-models)
5. [Error Handling](#error-handling)
6. [Configuration](#configuration)
7. [Usage Examples](#usage-examples)

## Overview

This document provides comprehensive documentation for the Spring Boot application APIs, functions, and components.

### Base URL
```
http://localhost:8080/api/v1
```

### Content Type
All API endpoints accept and return JSON data unless otherwise specified.

## Authentication

### JWT Authentication

#### POST /auth/login
Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

**Example Usage:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "password": "password123"}'
```

#### POST /auth/refresh
Refresh JWT token.

**Headers:**
- `Authorization: Bearer <token>`

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

## REST Endpoints

### User Management

#### GET /users
Retrieve all users (Admin only).

**Headers:**
- `Authorization: Bearer <token>`

**Query Parameters:**
- `page` (integer, optional): Page number (default: 0)
- `size` (integer, optional): Page size (default: 20)
- `sort` (string, optional): Sort field (default: "id")

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "createdAt": "2023-01-15T10:30:00Z",
      "updatedAt": "2023-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### GET /users/{id}
Retrieve user by ID.

**Path Parameters:**
- `id` (integer, required): User ID

**Headers:**
- `Authorization: Bearer <token>`

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2023-01-15T10:30:00Z",
  "updatedAt": "2023-01-15T10:30:00Z"
}
```

#### POST /users
Create a new user.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "id": 2,
  "username": "jane_doe",
  "email": "jane@example.com",
  "createdAt": "2023-01-15T11:00:00Z",
  "updatedAt": "2023-01-15T11:00:00Z"
}
```

#### PUT /users/{id}
Update user by ID.

**Path Parameters:**
- `id` (integer, required): User ID

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "username": "string",
  "email": "string"
}
```

#### DELETE /users/{id}
Delete user by ID.

**Path Parameters:**
- `id` (integer, required): User ID

**Headers:**
- `Authorization: Bearer <token>`

**Response:** HTTP 204 No Content

## Data Models

### User
```json
{
  "id": "integer",
  "username": "string (3-50 characters, alphanumeric)",
  "email": "string (valid email format)",
  "password": "string (minimum 8 characters)",
  "createdAt": "datetime (ISO 8601)",
  "updatedAt": "datetime (ISO 8601)"
}
```

### Authentication Response
```json
{
  "token": "string (JWT token)",
  "expiresIn": "integer (seconds)",
  "user": "User object"
}
```

### Error Response
```json
{
  "error": "string",
  "message": "string",
  "timestamp": "datetime (ISO 8601)",
  "path": "string"
}
```

## Error Handling

### HTTP Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Request successful, no content returned
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource conflict (e.g., duplicate username)
- `422 Unprocessable Entity` - Validation errors
- `500 Internal Server Error` - Server error

### Error Response Format

All errors return a consistent JSON structure:

```json
{
  "error": "VALIDATION_ERROR",
  "message": "Username must be between 3 and 50 characters",
  "timestamp": "2023-01-15T12:00:00Z",
  "path": "/api/v1/users"
}
```

### Common Error Types

- `VALIDATION_ERROR` - Input validation failed
- `AUTHENTICATION_ERROR` - Invalid credentials
- `AUTHORIZATION_ERROR` - Insufficient permissions
- `RESOURCE_NOT_FOUND` - Requested resource does not exist
- `DUPLICATE_RESOURCE` - Resource already exists

## Configuration

### Environment Variables

- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 3306)
- `DB_NAME` - Database name
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `JWT_EXPIRATION` - JWT expiration time in seconds (default: 3600)

### Application Properties

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:3600}

# Server Configuration
server.port=8080
```

## Usage Examples

### Complete User Management Flow

#### 1. User Registration
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "securePassword123"
  }'
```

#### 2. User Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "securePassword123"
  }'
```

#### 3. Access Protected Resource
```bash
# Save token from login response
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8080/api/v1/users/1 \
  -H "Authorization: Bearer $TOKEN"
```

#### 4. Update User Information
```bash
curl -X PUT http://localhost:8080/api/v1/users/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com"
  }'
```

### Pagination Example

```bash
# Get users with pagination
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=10&sort=username" \
  -H "Authorization: Bearer $TOKEN"
```

### Error Handling Example

```bash
# Invalid request - missing required fields
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab"
  }'

# Response:
# {
#   "error": "VALIDATION_ERROR",
#   "message": "Username must be between 3 and 50 characters",
#   "timestamp": "2023-01-15T12:00:00Z",
#   "path": "/api/v1/users"
# }
```

### JavaScript/TypeScript Client Example

```typescript
interface User {
  id: number;
  username: string;
  email: string;
  createdAt: string;
  updatedAt: string;
}

interface AuthResponse {
  token: string;
  expiresIn: number;
  user: User;
}

class ApiClient {
  private baseUrl = 'http://localhost:8080/api/v1';
  private token: string | null = null;

  async login(username: string, password: string): Promise<AuthResponse> {
    const response = await fetch(`${this.baseUrl}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      throw new Error(`Login failed: ${response.statusText}`);
    }

    const authResponse: AuthResponse = await response.json();
    this.token = authResponse.token;
    return authResponse;
  }

  async getUsers(page = 0, size = 20): Promise<User[]> {
    const response = await fetch(
      `${this.baseUrl}/users?page=${page}&size=${size}`,
      {
        headers: {
          'Authorization': `Bearer ${this.token}`,
        },
      }
    );

    if (!response.ok) {
      throw new Error(`Failed to get users: ${response.statusText}`);
    }

    const data = await response.json();
    return data.content;
  }

  async createUser(userData: Omit<User, 'id' | 'createdAt' | 'updatedAt'>): Promise<User> {
    const response = await fetch(`${this.baseUrl}/users`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.token}`,
      },
      body: JSON.stringify(userData),
    });

    if (!response.ok) {
      throw new Error(`Failed to create user: ${response.statusText}`);
    }

    return response.json();
  }
}

// Usage example
const client = new ApiClient();

async function example() {
  try {
    // Login
    const authResponse = await client.login('john_doe', 'password123');
    console.log('Logged in successfully:', authResponse.user);

    // Get users
    const users = await client.getUsers(0, 10);
    console.log('Users:', users);

    // Create new user
    const newUser = await client.createUser({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });
    console.log('Created user:', newUser);
  } catch (error) {
    console.error('API Error:', error);
  }
}
```

## Rate Limiting

### Default Limits
- **Authentication endpoints**: 5 requests per minute per IP
- **User management endpoints**: 100 requests per hour per user
- **Read operations**: 1000 requests per hour per user

### Headers
Rate limit information is included in response headers:
- `X-RateLimit-Limit`: Maximum requests allowed
- `X-RateLimit-Remaining`: Remaining requests in current window
- `X-RateLimit-Reset`: Time when the rate limit resets (Unix timestamp)

## Testing

### Unit Testing Examples

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateUser() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpected(jsonPath("$.email").value("test@example.com"));
    }
}
```

### Integration Testing with TestContainers

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Test
    void shouldPerformFullUserLifecycle() {
        // Test complete user CRUD operations
    }
}
```

---

*This documentation is automatically generated and updated. Last updated: ${new Date().toISOString()}*