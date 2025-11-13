# InvoiceMe - AI-Assisted Full-Stack ERP Invoicing System

## Project Overview

InvoiceMe is an AI-assisted full-stack ERP invoicing system designed to demonstrate mastery of modern software architecture principles:
- **Domain-Driven Design (DDD)**
- **Command Query Responsibility Segregation (CQRS)**
- **Vertical Slice Architecture (VSA)**
- **Clean Architecture**

The system implements core business domains: **Customers**, **Invoices**, and **Payments**.

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.2+ (Java 17+)
- **Build Tool**: Gradle 8.5+
- **Database**: 
  - Development: H2 (in-memory)
  - Production: PostgreSQL 15+
- **ORM**: Spring Data JPA with Hibernate
- **API Documentation**: SpringDoc OpenAPI (Swagger)

### Frontend
- **Framework**: Next.js 14+ (App Router) with TypeScript
- **State Management**: React Query (TanStack Query)
- **UI Library**: Tailwind CSS
- **Form Handling**: React Hook Form + Zod validation
- **HTTP Client**: Axios

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Production**: Vercel (frontend) + Railway/Render (backend)

## Prerequisites

- **Java 17+** (for backend)
- **Node.js 18+** and **npm** (for frontend)
- **Docker** and **Docker Compose** (for local development with PostgreSQL)
- **Gradle 8.5+** (or use Gradle Wrapper)

## Quick Start

### Option 1: Local Development (H2 Database)

#### Backend
```bash
cd backend
./gradlew bootRun
```

Backend will be available at: http://localhost:8080
- API Docs (Swagger): http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend will be available at: http://localhost:3000

### Option 2: Docker Compose (PostgreSQL)

```bash
# Start all services (PostgreSQL, Backend, Frontend)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes (clean database)
docker-compose down -v
```

Services:
- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **PostgreSQL**: localhost:5432

## Project Structure

```
InvoiceMe/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/java/com/invoiceme/
│   │   │   ├── domain/         # Domain entities and value objects
│   │   │   ├── application/    # Use cases (Commands/Queries)
│   │   │   ├── infrastructure/ # Persistence, external services
│   │   │   └── api/            # REST controllers
│   │   └── test/               # Tests
│   ├── build.gradle.kts
│   └── Dockerfile
├── frontend/                   # Next.js frontend
│   ├── app/                    # Next.js App Router
│   ├── components/             # React components
│   ├── lib/                    # Utilities, API client
│   ├── types/                  # TypeScript types
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml          # Docker Compose configuration
├── .gitignore
└── README.md
```

## Development Workflow

### Phase 1: Foundation ✅ (Current)
- [x] Project structure setup
- [x] Backend Spring Boot configuration
- [x] Frontend Next.js configuration
- [x] Docker Compose setup

### Phase 2: Domain Model (Next)
- Domain entities (Customer, Invoice, Payment)
- Business logic implementation
- Value objects

### Phase 3-4: CQRS Implementation
- Commands (write operations)
- Queries (read operations)

### Phase 5: REST API
- API endpoints
- DTOs and validation
- Swagger documentation

### Phase 6-7: Frontend
- UI components
- Feature implementation
- Integration with backend

See [PRD-00-Master.md](./PRD-00-Master.md) for complete phase breakdown.

## Environment Variables

### Backend
- `DATABASE_URL` - Database connection URL (production)
- `DATABASE_USERNAME` - Database username (production)
- `DATABASE_PASSWORD` - Database password (production)
- `JWT_SECRET` - JWT secret key (for authentication)
- `JWT_EXPIRATION` - JWT expiration time

### Frontend
- `NEXT_PUBLIC_API_URL` - Backend API URL (default: http://localhost:8080/api/v1)

## Testing

### Backend
```bash
cd backend
./gradlew test
```

### Frontend
```bash
cd frontend
npm test
```

## API Documentation

Once the backend is running, access Swagger UI at:
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/api-docs (OpenAPI JSON)

## Database Access

### H2 Console (Development)
1. Start backend
2. Navigate to: http://localhost:8080/h2-console
3. JDBC URL: `jdbc:h2:mem:invoiceme`
4. Username: `sa`
5. Password: (leave empty)

### PostgreSQL (Docker)
```bash
# Connect to PostgreSQL container
docker-compose exec postgres psql -U invoiceme -d invoiceme
```

## Troubleshooting

### Backend won't start
- Check Java version: `java -version` (should be 17+)
- Check port 8080 is not in use
- Check H2 database is accessible

### Frontend won't start
- Check Node.js version: `node -v` (should be 18+)
- Run `npm install` to install dependencies
- Check port 3000 is not in use

### Docker issues
- Ensure Docker is running
- Check Docker Compose version: `docker-compose --version`
- Try rebuilding: `docker-compose build --no-cache`

## Contributing

1. Follow the architectural principles (DDD, CQRS, VSA)
2. Write tests for new features
3. Update documentation as needed
4. Follow conventional commit messages

## Production Deployment

For production deployment, see the comprehensive guides:

- [Quick Start Deployment](./docs/QUICK_START_DEPLOYMENT.md) - 5-minute deployment guide
- [Production Deployment Guide](./docs/PRODUCTION_DEPLOYMENT.md) - Complete step-by-step guide
- [Environment Variables](./docs/PRODUCTION_ENV_VARS.md) - Production environment configuration
- [Troubleshooting](./docs/TROUBLESHOOTING.md) - Common issues and solutions
- [Monitoring](./docs/MONITORING.md) - Monitoring and logging setup
- [Rollback Procedures](./docs/ROLLBACK.md) - How to rollback deployments

### Quick Deploy

1. **Database**: Set up PostgreSQL on Railway/Render/Neon
2. **Backend**: Deploy to Railway or Render
3. **Frontend**: Deploy to Vercel
4. **Domain**: Configure DNS and SSL (optional)

See [Quick Start Deployment Guide](./docs/QUICK_START_DEPLOYMENT.md) for details.

## Documentation

- [PRD-00-Master.md](./PRD-00-Master.md) - Master PRD with overview
- [PRD-01-Backend.md](./PRD-01-Backend.md) - Backend implementation details
- [PRD-02-Frontend.md](./PRD-02-Frontend.md) - Frontend implementation details
- [PRD-03-Testing.md](./PRD-03-Testing.md) - Testing strategy
- [PRD-04-Infrastructure.md](./PRD-04-Infrastructure.md) - Infrastructure and deployment

## License

This project is part of an assessment/demonstration.

---

**Status**: Phase 1 Complete ✅  
**Next**: Phase 2 - Domain Model & Core Entities


