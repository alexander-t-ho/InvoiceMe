# Deployment Guide: InvoiceMe

This guide covers deployment of InvoiceMe to production environments.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Production Deployment](#production-deployment)
4. [Docker Deployment](#docker-deployment)
5. [Cloud Deployment](#cloud-deployment)
6. [Environment Variables](#environment-variables)
7. [Database Setup](#database-setup)
8. [Monitoring & Logging](#monitoring--logging)
9. [Troubleshooting](#troubleshooting)

---

## Prerequisites

- **Java 17+** (for backend)
- **Node.js 18+** (for frontend)
- **Docker & Docker Compose** (for containerized deployment)
- **PostgreSQL 15+** (for production database)
- **Git** (for version control)

---

## Local Development Setup

### Quick Start

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd InvoiceMe
   ```

2. **Start with Docker Compose**:
   ```bash
   docker-compose up -d
   ```

3. **Access the application**:
   - Frontend: http://localhost:3001
   - Backend API: http://localhost:8081
   - Swagger UI: http://localhost:8081/swagger-ui.html
   - H2 Console: http://localhost:8081/h2-console (dev only)

### Manual Setup

#### Backend

```bash
cd backend
export JAVA_HOME=$(brew --prefix openjdk@17)  # macOS
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew bootRun
```

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

---

## Production Deployment

### Docker Deployment (Recommended)

#### 1. Prepare Environment Variables

Create `.env.prod` file (copy from `.env.prod.example`):

```bash
cp .env.prod.example .env.prod
# Edit .env.prod with your production values
```

**Required Variables**:
- `POSTGRES_PASSWORD` - Strong database password
- `JWT_SECRET` - Secure 256-bit secret key (minimum 32 characters)
- `NEXT_PUBLIC_API_URL` - Public API URL

#### 2. Build and Start Services

```bash
# Build images
docker-compose -f docker-compose.prod.yml build

# Start services
docker-compose -f docker-compose.prod.yml up -d

# Check logs
docker-compose -f docker-compose.prod.yml logs -f

# Check health
curl http://localhost:8081/actuator/health
```

#### 3. Verify Deployment

- Backend health: `http://localhost:8081/actuator/health`
- Frontend: `http://localhost:3001`
- API docs: `http://localhost:8081/swagger-ui.html`

---

## Cloud Deployment

### AWS Deployment

#### Architecture
- **Backend**: ECS (Fargate) or EC2
- **Frontend**: S3 + CloudFront
- **Database**: RDS PostgreSQL
- **Load Balancer**: Application Load Balancer

#### Steps

1. **Create RDS PostgreSQL Instance**:
   ```bash
   aws rds create-db-instance \
     --db-instance-identifier invoiceme-db \
     --db-instance-class db.t3.micro \
     --engine postgres \
     --master-username invoiceme \
     --master-user-password <secure-password> \
     --allocated-storage 20
   ```

2. **Build and Push Docker Images**:
   ```bash
   # Backend
   docker build -t invoiceme-backend:latest ./backend
   docker tag invoiceme-backend:latest <ecr-repo>/invoiceme-backend:latest
   docker push <ecr-repo>/invoiceme-backend:latest

   # Frontend
   docker build -t invoiceme-frontend:latest ./frontend
   docker tag invoiceme-frontend:latest <ecr-repo>/invoiceme-frontend:latest
   docker push <ecr-repo>/invoiceme-frontend:latest
   ```

3. **Deploy to ECS**:
   - Create ECS task definitions
   - Create ECS services
   - Configure load balancer
   - Set environment variables

4. **Deploy Frontend to S3 + CloudFront**:
   ```bash
   # Build frontend
   cd frontend
   npm run build
   npm run export  # If using static export

   # Upload to S3
   aws s3 sync out/ s3://<bucket-name>/
   
   # Configure CloudFront distribution
   ```

### Azure Deployment

#### Architecture
- **Backend**: App Service or Container Instances
- **Frontend**: Static Web Apps or App Service
- **Database**: Azure Database for PostgreSQL

#### Steps

1. **Create Azure Database for PostgreSQL**:
   ```bash
   az postgres server create \
     --resource-group invoiceme-rg \
     --name invoiceme-db \
     --location eastus \
     --admin-user invoiceme \
     --admin-password <secure-password> \
     --sku-name B_Gen5_1
   ```

2. **Deploy Backend to App Service**:
   ```bash
   az webapp create \
     --resource-group invoiceme-rg \
     --plan invoiceme-plan \
     --name invoiceme-backend \
     --deployment-container-image-name <acr>/invoiceme-backend:latest
   ```

3. **Configure Environment Variables**:
   ```bash
   az webapp config appsettings set \
     --resource-group invoiceme-rg \
     --name invoiceme-backend \
     --settings \
       SPRING_PROFILES_ACTIVE=prod \
       SPRING_DATASOURCE_URL=jdbc:postgresql://<db-host>:5432/invoiceme \
       JWT_SECRET=<jwt-secret>
   ```

---

## Environment Variables

### Backend Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` | Yes |
| `SPRING_DATASOURCE_URL` | Database URL | - | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database username | `invoiceme` | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - | Yes |
| `JWT_SECRET` | JWT secret key | - | Yes |
| `JWT_EXPIRATION` | JWT expiration (ms) | `86400000` | No |
| `SERVER_PORT` | Server port | `8081` | No |

### Frontend Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `NEXT_PUBLIC_API_URL` | Backend API URL | - | Yes |
| `NODE_ENV` | Node environment | `production` | No |

---

## Database Setup

### Initial Schema

The application uses JPA/Hibernate with `ddl-auto: validate` in production. The schema is automatically created from entity definitions.

### Database Indexes

The following indexes are automatically created:

- `idx_customers_email` - Customer email lookups
- `idx_invoices_customer_id` - Invoice filtering by customer
- `idx_invoices_status` - Invoice filtering by status
- `idx_invoices_customer_status` - Composite index for common queries
- `idx_payments_invoice_id` - Payment lookups by invoice
- `idx_invoice_line_items_invoice_id` - Line item lookups
- `idx_users_username` - User authentication
- `idx_users_email` - User email lookups

### Database Migrations

For production, consider using Flyway or Liquibase for schema versioning:

1. **Flyway Setup**:
   ```xml
   <!-- Add to build.gradle.kts -->
   implementation("org.flywaydb:flyway-core")
   ```

2. **Create Migration Files**:
   ```
   backend/src/main/resources/db/migration/
   ├── V1__Initial_schema.sql
   └── V2__Add_indexes.sql
   ```

---

## Monitoring & Logging

### Health Checks

- **Backend Health**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Prometheus**: `GET /actuator/prometheus`

### Logging

**Backend Logs**:
- Location: `logs/invoiceme-backend.log`
- Rotation: 10MB max, 30 days retention
- Format: Structured JSON (production)

**Frontend Logs**:
- Console logs in development
- Error tracking: Consider Sentry integration

### Monitoring Tools

- **Spring Boot Actuator**: Built-in metrics and health
- **Prometheus**: Metrics collection
- **Grafana**: Visualization (optional)
- **CloudWatch** (AWS) or **Application Insights** (Azure): Cloud monitoring

---

## Troubleshooting

### Common Issues

#### 1. Database Connection Failed

**Symptoms**: Backend fails to start, connection errors

**Solutions**:
- Verify database is running: `docker ps`
- Check connection string in environment variables
- Verify network connectivity
- Check firewall rules

#### 2. JWT Authentication Fails

**Symptoms**: 401 Unauthorized errors

**Solutions**:
- Verify `JWT_SECRET` is set correctly
- Ensure secret is at least 32 characters
- Check token expiration settings

#### 3. Frontend Cannot Connect to Backend

**Symptoms**: API calls fail, CORS errors

**Solutions**:
- Verify `NEXT_PUBLIC_API_URL` is correct
- Check CORS configuration in backend
- Verify backend is accessible from frontend

#### 4. High Memory Usage

**Symptoms**: Container restarts, OOM errors

**Solutions**:
- Increase container memory limits
- Review database connection pool settings
- Check for memory leaks in application

### Debug Commands

```bash
# Check container logs
docker-compose -f docker-compose.prod.yml logs -f backend
docker-compose -f docker-compose.prod.yml logs -f frontend

# Check container status
docker-compose -f docker-compose.prod.yml ps

# Restart services
docker-compose -f docker-compose.prod.yml restart

# Access container shell
docker exec -it invoiceme-backend-prod sh
```

---

## Security Checklist

- [ ] Change default passwords
- [ ] Use strong JWT secret (32+ characters)
- [ ] Enable HTTPS in production
- [ ] Configure CORS properly
- [ ] Set up database backups
- [ ] Enable database SSL/TLS
- [ ] Configure firewall rules
- [ ] Set up rate limiting (optional)
- [ ] Enable security headers
- [ ] Regular security updates

---

## Backup & Recovery

### Database Backups

```bash
# Manual backup
docker exec invoiceme-postgres-prod pg_dump -U invoiceme invoiceme > backup.sql

# Restore
docker exec -i invoiceme-postgres-prod psql -U invoiceme invoiceme < backup.sql
```

### Automated Backups

Set up cron job or cloud backup service:

```bash
# Daily backup script
0 2 * * * docker exec invoiceme-postgres-prod pg_dump -U invoiceme invoiceme | gzip > /backups/invoiceme-$(date +\%Y\%m\%d).sql.gz
```

---

## Performance Optimization

### Backend

- Database indexes are automatically created
- Connection pooling configured (HikariCP)
- Query optimization with batch operations
- Response compression enabled

### Frontend

- Code splitting (Next.js automatic)
- Image optimization
- Static asset caching
- Bundle size optimization

### Monitoring

- API response times: Target < 200ms
- Database query performance: Monitor slow queries
- Memory usage: Monitor container metrics
- CPU usage: Monitor container metrics

---

## Support

For issues or questions:
- Check logs: `docker-compose logs -f`
- Review documentation: `docs/`
- Check GitHub issues
- Contact development team

---

**Last Updated**: 2025-11-08  
**Version**: 1.0

