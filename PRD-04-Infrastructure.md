# Infrastructure PRD: InvoiceMe — Infrastructure & Deployment

## Document Purpose

This PRD details infrastructure setup, deployment configuration, and DevOps practices for InvoiceMe. This document covers Phases 1 and 10.

**Related Documents**: 
- PRD-00-Master.md (overview)
- PRD-01-Backend.md (backend requirements)
- PRD-02-Frontend.md (frontend requirements)

---

## Phase 1: Project Foundation & Infrastructure Setup

**Duration**: 1-2 days  
**Goal**: Establish project structure, build configuration, and development environment

---

### Project Structure

```
InvoiceMe/
├── backend/
│   ├── src/
│   │   ├── main/java/com/invoiceme/
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   ├── infrastructure/
│   │   │   └── api/
│   │   └── test/
│   ├── build.gradle (or pom.xml)
│   └── application.yml
├── frontend/
│   ├── app/
│   ├── components/
│   ├── lib/
│   ├── types/
│   ├── package.json
│   └── next.config.js
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

### Backend Setup

#### Spring Boot Project Initialization
- Use Spring Initializr or create manually
- Dependencies:
  - Spring Web
  - Spring Data JPA
  - PostgreSQL Driver
  - H2 Database (for dev)
  - Spring Security
  - Validation
  - Lombok (optional)
  - SpringDoc OpenAPI

#### Build Configuration
**Gradle** (`build.gradle.kts`):
- Java 17+
- Spring Boot 3.2+
- JUnit 5
- Testcontainers

#### Application Configuration
**`application.yml`**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:invoiceme
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  h2:
    console:
      enabled: true

server:
  port: 8080
```

**`application-prod.yml`**:
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
```

---

### Frontend Setup

#### Next.js Project Initialization
```bash
npx create-next-app@latest frontend --typescript --tailwind --app
```

#### Dependencies
- `react-query` (TanStack Query)
- `axios` or `fetch` wrapper
- `react-hook-form`
- `zod`
- `shadcn/ui` components
- `vitest` and `@testing-library/react`

#### Configuration
**`next.config.js`**:
- API base URL configuration
- Environment variables

**`.env.local`**:
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

---

### Docker Compose Setup

**`docker-compose.yml`**:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: invoiceme
      POSTGRES_USER: invoiceme
      POSTGRES_PASSWORD: invoiceme
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/invoiceme
      SPRING_DATASOURCE_USERNAME: invoiceme
      SPRING_DATASOURCE_PASSWORD: invoiceme
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "3000:3000"
    environment:
      NEXT_PUBLIC_API_URL: http://localhost:8080/api/v1
    depends_on:
      - backend

volumes:
  postgres_data:
```

---

### Database Schema

**Location**: `backend/src/main/resources/db/migration/` (Flyway) or `schema.sql`

```sql
-- Customers
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Invoices
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'SENT', 'PAID')),
    issue_date DATE NOT NULL,
    due_date DATE,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Invoice Line Items
CREATE TABLE invoice_line_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    line_order INTEGER NOT NULL
);

-- Payments
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Users (for authentication)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_invoice_line_items_invoice_id ON invoice_line_items(invoice_id);
CREATE INDEX idx_customers_email ON customers(email);
```

---

### Database Migration Strategy

**Option 1: Flyway**
- Create migration files: `V1__Initial_schema.sql`
- Flyway runs migrations on application startup

**Option 2: Liquibase**
- Create changelog files
- Liquibase manages schema versioning

**Option 3: JPA DDL**
- Use `spring.jpa.hibernate.ddl-auto=update` for development only
- Use migrations for production

---

### Success Criteria

- ✅ Both backend and frontend projects start successfully
- ✅ Database connection established (H2 for dev, PostgreSQL via Docker)
- ✅ Docker Compose environment runs locally
- ✅ Project structure follows Clean Architecture
- ✅ README with setup instructions

---

## Phase 10: Performance Optimization & Deployment Preparation

**Duration**: 2-3 days  
**Goal**: Optimize performance and prepare for production deployment

---

### Performance Optimization

#### Backend Optimization

**Database Optimization**:
- ✅ Add indexes (see schema above)
- ✅ Optimize queries (use projections, avoid N+1 queries)
- ✅ Implement pagination for list queries
- ✅ Use database connection pooling

**API Optimization**:
- ✅ Profile endpoints (use Spring Boot Actuator)
- ✅ Optimize slow queries
- ✅ Implement caching (optional: Redis)
- ✅ Verify API latency < 200ms

**Code Optimization**:
- ✅ Use lazy loading for JPA relationships where appropriate
- ✅ Batch operations for bulk inserts
- ✅ Optimize DTO mapping (use MapStruct)

---

#### Frontend Optimization

**Bundle Optimization**:
- ✅ Code splitting (Next.js automatic)
- ✅ Lazy loading for routes
- ✅ Tree shaking
- ✅ Optimize images (Next.js Image component)

**Runtime Optimization**:
- ✅ Memoization for expensive components
- ✅ Optimize React Query cache settings
- ✅ Debounce search/filter inputs
- ✅ Virtual scrolling for large lists (optional)

**Performance Metrics**:
- ✅ Lighthouse score > 90
- ✅ First Contentful Paint < 1.5s
- ✅ Time to Interactive < 3s

---

### Deployment Configuration

#### Dockerfiles

**Backend Dockerfile** (`backend/Dockerfile`):
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Frontend Dockerfile** (`frontend/Dockerfile`):
```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:18-alpine
WORKDIR /app
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package*.json ./
RUN npm ci --only=production
EXPOSE 3000
CMD ["npm", "start"]
```

---

#### Environment Configuration

**Environment Variables**:

**Backend**:
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION`

**Frontend**:
- `NEXT_PUBLIC_API_URL`

**Configuration Management**:
- Use environment variables for all sensitive/configurable values
- Never commit secrets to version control
- Use secrets management (AWS Secrets Manager, Azure Key Vault)

---

### Cloud Deployment Options

#### AWS Deployment

**Architecture**:
- **Backend**: ECS (Fargate) or EC2
- **Frontend**: S3 + CloudFront
- **Database**: RDS PostgreSQL
- **Load Balancer**: Application Load Balancer

**Services**:
- ECS/EKS for container orchestration
- RDS for managed PostgreSQL
- S3 for static assets
- CloudFront for CDN
- Route 53 for DNS

#### Azure Deployment

**Architecture**:
- **Backend**: App Service or Container Instances
- **Frontend**: Static Web Apps or App Service
- **Database**: Azure SQL Database or PostgreSQL
- **Load Balancer**: Application Gateway

**Services**:
- App Service for backend/frontend
- Azure Database for PostgreSQL
- Azure Blob Storage for static assets
- Azure CDN for content delivery

---

### CI/CD Pipeline

#### GitHub Actions Workflow

**`.github/workflows/ci.yml`**:
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  backend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run tests
        run: ./gradlew test
      - name: Generate coverage
        run: ./gradlew jacocoTestReport

  frontend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install dependencies
        run: cd frontend && npm ci
      - name: Run tests
        run: cd frontend && npm test
      - name: Build
        run: cd frontend && npm run build

  deploy:
    needs: [backend-test, frontend-test]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to production
        # Add deployment steps
```

---

### Monitoring & Logging

#### Logging

**Backend**:
- Use SLF4J with Logback
- Structured logging (JSON format)
- Log levels: INFO, WARN, ERROR
- Log aggregation: CloudWatch (AWS) or Application Insights (Azure)

**Frontend**:
- Console logging for development
- Error tracking: Sentry (optional)

#### Monitoring

**Metrics**:
- API response times
- Error rates
- Database connection pool usage
- Memory/CPU usage

**Tools**:
- Spring Boot Actuator (backend)
- CloudWatch (AWS) or Application Insights (Azure)
- Custom health checks

---

### Security Hardening

**Backend**:
- ✅ HTTPS only in production
- ✅ CORS properly configured
- ✅ Input validation on all endpoints
- ✅ SQL injection prevention (use parameterized queries)
- ✅ XSS prevention
- ✅ Rate limiting (optional)

**Frontend**:
- ✅ HTTPS only
- ✅ Secure token storage
- ✅ XSS prevention (React automatic)
- ✅ CSRF protection (if using sessions)

**Database**:
- ✅ Encrypted connections (SSL/TLS)
- ✅ Strong passwords
- ✅ Network isolation (VPC/private subnet)
- ✅ Regular backups

---

### Documentation

#### README Updates

**Sections**:
- Project overview
- Architecture diagram
- Setup instructions
- Development workflow
- Deployment instructions
- Environment variables
- API documentation link

#### API Documentation

- Swagger/OpenAPI accessible at `/swagger-ui.html`
- Document all endpoints
- Include request/response examples
- Authentication requirements

#### Architecture Diagram

- Create diagram showing:
  - System components
  - Data flow
  - External dependencies
  - Deployment architecture

---

### Success Criteria

- ✅ All API endpoints meet < 200ms latency requirement
- ✅ Application runs successfully in Docker containers
- ✅ Deployment documentation is complete
- ✅ CI/CD pipeline is configured and working
- ✅ Environment variables are properly configured
- ✅ Database migrations are set up
- ✅ Monitoring and logging are configured
- ✅ System is ready for cloud deployment

---

## Database Indexes (Performance)

**Required Indexes**:
- `idx_invoices_customer_id` - For filtering invoices by customer
- `idx_invoices_status` - For filtering invoices by status
- `idx_payments_invoice_id` - For loading payments for invoice
- `idx_invoice_line_items_invoice_id` - For loading line items
- `idx_customers_email` - For email uniqueness and lookups

**Additional Indexes** (if needed):
- Composite index on `(customer_id, status)` for common queries
- Index on `created_at` for date-based queries

---

## Backup & Recovery

**Database Backups**:
- Automated daily backups
- Retention: 30 days
- Test restore procedures

**Application Backups**:
- Version control (Git)
- Container images in registry
- Configuration backups

---

**Document Version**: 1.0  
**Last Updated**: [Current Date]  
**Author**: Development Team


