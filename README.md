# Codebox

A LeetCode-style platform for automatically judging algorithmic solutions. Users submit code, the system compiles it and runs it against test cases in an isolated container, and returns a verdict.

> **Status: work in progress.** The REST API, authentication and problem management are implemented. The asynchronous judging pipeline (RabbitMQ + sandboxed workers) is under development. See [Roadmap](#roadmap).

## Tech stack

- **Java 21**, **Spring Boot** (Web, Data JPA, Security, Validation)
- **PostgreSQL 16**
- **JWT** authentication (Spring Security, stateless)
- **RabbitMQ** (provisioned, used by the upcoming judging pipeline)
- **Docker** / **Docker Compose**
- **springdoc-openapi** (Swagger UI)

## Current features

- User registration and login with JWT
- Role-based model (`USER`, `ADMIN`)
- Problems: create and fetch by id or slug
- Request validation and centralized error handling (`ProblemDetail` responses)
- Full local environment started with a single `docker compose up`




## Getting started

### Requirements

- Docker with Docker Compose

### Run

```bash
git clone https://github.com/xFloras/Codebox
cd Codebox
docker compose up -d --build
```

Services:

| Service    | URL / port                                        |
|------------|---------------------------------------------------|
| API        | http://localhost:8080                             |
| Swagger UI | http://localhost:8080/swagger-ui/index.html       |
| PostgreSQL | localhost:5432                                    |
| RabbitMQ   | localhost:5672 (management UI: http://localhost:15672) |

### Configuration

Defaults for local development are defined in `docker-compose.yml` and `application.yml`. Override them with environment variables (for example in a `.env`, which can be made from `example.env`):

| Variable            | Default            |
|---------------------|--------------------|
| `POSTGRES_DB`       | `codebox_db`       |
| `POSTGRES_USER`     | `codebox_user`     |
| `POSTGRES_PASSWORD` | `codebox_password` |
| `RABBITMQ_USER`     | `guest`            |
| `RABBITMQ_PASSWORD` | `guest`            |
| `APP_PORT`          | `8080`             |

The defaults are demo values only. Set your own values, including the JWT signing key, for any non-local use.

### Run the API from the IDE

Start only the infrastructure and run the Spring Boot application locally:

```bash
docker compose up -d postgres rabbitmq
```

## API overview

Full, interactive documentation is available in Swagger UI.

| Method | Path                     | Description                         | Access (target) |
|--------|--------------------------|-------------------------------------|-----------------|
| POST   | `/api/auth/signup`           | Register a new user                 | public          |
| POST   | `/api/auth/login`            | Log in, returns a JWT               | public          |
| POST   | `/api/problems`          | Create a problem                    | admin           |
| GET    | `/api/problems/{slug}`   | Get a problem by slug               | public          |
| GET    | `/api/problems/{id}`     | Get a problem by id                 | public          |

### Example

```bash
# register
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"test123","email":"test@example.com","password":"testowe123"}'

# log in
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test123","password":"testowe123"}'

# create a problem (token from the login response)
curl -X POST http://localhost:8080/api/problems \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
        "slug": "two-sum",
        "title": "Two Sum",
        "description": "Return indices of two numbers that add up to target.",
        "difficulty": "EASY",
        "timeLimitMs": 2000,
        "memoryLimitMb": 256
      }'
```

## Security notes

- Passwords are stored as hashes (BCrypt); the role of a new account is always assigned server-side.
- Running untrusted code is the hardest part of this project. The planned sandbox uses per-submission containers with resource limits, no network and a hard timeout. For production use, stronger isolation (for example gVisor or Firecracker) would be required.

## Roadmap

- [x] Project setup, Docker Compose (PostgreSQL, RabbitMQ, application)
- [x] JWT authentication and registration
- [x] Problem entity, create and fetch endpoints
- [ ] Admin-only access for problem management
- [ ] Test cases and submissions (entities, endpoints, statuses)
- [ ] RabbitMQ producer and worker (dead-letter queue, retries)
- [ ] Sandboxed code execution with TLE/MLE limits
- [ ] Support for multiple languages
- [ ] Integration tests (Testcontainers)
- [ ] Problem listing with filtering and pagination

## License

To be decided.
