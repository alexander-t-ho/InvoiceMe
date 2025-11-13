# Production Environment Variables Documentation

This document describes all environment variables required for production deployment of InvoiceMe.

## Backend Environment Variables

### Required Variables

| Variable | Description | Example | Notes |
|----------|-------------|---------|-------|
| `SPRING_PROFILES_ACTIVE` | Spring Boot profile | `prod` | Must be set to `prod` for production |
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://host:5432/invoiceme` | Full JDBC URL including host, port, and database name |
| `SPRING_DATASOURCE_USERNAME` | Database username | `invoiceme` | Database user with appropriate permissions |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `SecurePassword123!` | Strong password (16+ characters recommended) |
| `JWT_SECRET` | JWT signing secret | `base64-encoded-32-char-minimum-string` | **CRITICAL**: Must be at least 32 characters. Generate using `openssl rand -base64 32` |

### Optional Variables

| Variable | Description | Default | Notes |
|----------|-------------|---------|-------|
| `SERVER_PORT` | Backend server port | `8081` | Usually set automatically by hosting platform |
| `JWT_EXPIRATION` | JWT token expiration (ms) | `86400000` | 24 hours in milliseconds |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed origins | `localhost:3000,3001,3002,3003` | Production domains (e.g., `https://yourdomain.com,https://www.yourdomain.com`) |

## Frontend Environment Variables

### Required Variables

| Variable | Description | Example | Notes |
|----------|-------------|---------|-------|
| `NEXT_PUBLIC_API_URL` | Backend API base URL | `https://api.yourdomain.com/api/v1` | Must include protocol (https://) and `/api/v1` path. Set in Vercel dashboard. |

### Optional Variables

| Variable | Description | Default | Notes |
|----------|-------------|---------|-------|
| `NODE_ENV` | Node.js environment | `production` | Automatically set by Vercel in production |

## Platform-Specific Configuration

### Railway

Railway automatically provides:
- `PORT` - Automatically set by Railway
- Database connection variables if using Railway PostgreSQL addon

**Setting Variables:**
1. Go to your Railway project
2. Select your backend service
3. Go to "Variables" tab
4. Add each variable from the Backend section above

### Render

Render automatically provides:
- `PORT` - Automatically set by Render
- Database connection variables if using Render PostgreSQL

**Setting Variables:**
1. Go to your Render dashboard
2. Select your backend service
3. Go to "Environment" tab
4. Add each variable from the Backend section above

### Vercel

**Setting Variables:**
1. Go to your Vercel project
2. Go to "Settings" > "Environment Variables"
3. Add `NEXT_PUBLIC_API_URL` for Production environment
4. Optionally add for Preview and Development environments

**Important:** 
- Variables prefixed with `NEXT_PUBLIC_` are exposed to the browser
- Only include non-sensitive variables with this prefix
- Never include secrets or API keys in `NEXT_PUBLIC_` variables

## Security Best Practices

1. **JWT Secret Generation:**
   ```bash
   # Generate a secure JWT secret
   openssl rand -base64 32
   ```

2. **Database Password:**
   - Use a strong password (16+ characters)
   - Include uppercase, lowercase, numbers, and special characters
   - Never reuse passwords from other services

3. **Environment Variable Storage:**
   - Never commit `.env.production` to version control
   - Use platform-specific secret management (Railway/Render/Vercel)
   - Rotate secrets periodically
   - Use different secrets for staging and production

4. **Access Control:**
   - Limit who has access to production environment variables
   - Use platform RBAC features to restrict access
   - Audit access logs regularly

## Verification Checklist

Before deploying to production, verify:

- [ ] All required environment variables are set
- [ ] JWT_SECRET is at least 32 characters and randomly generated
- [ ] Database password is strong and unique
- [ ] NEXT_PUBLIC_API_URL points to production backend (HTTPS)
- [ ] No development/localhost URLs in production variables
- [ ] All secrets are stored securely (not in code or config files)
- [ ] Different secrets used for staging vs production

## Troubleshooting

### Backend won't start
- Check that all required environment variables are set
- Verify database connection string format
- Ensure JWT_SECRET is set and valid

### Frontend can't connect to backend
- Verify `NEXT_PUBLIC_API_URL` is set correctly
- Check that URL uses HTTPS in production
- Ensure CORS is configured to allow your frontend domain

### Authentication fails
- Verify JWT_SECRET matches between token generation and validation
- Check JWT_EXPIRATION is set appropriately
- Ensure tokens are being sent in Authorization header

## Example Configuration

### Railway Backend
```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.USERNAME}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PASSWORD}}
JWT_SECRET=<generated-secret>
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

### Render Backend
```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=<from-render-postgres-service>
SPRING_DATASOURCE_USERNAME=<from-render-postgres-service>
SPRING_DATASOURCE_PASSWORD=<from-render-postgres-service>
JWT_SECRET=<generated-secret>
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

### Vercel Frontend
```
NEXT_PUBLIC_API_URL=https://your-backend.railway.app/api/v1
```

## Related Documentation

- [Production Deployment Guide](./PRODUCTION_DEPLOYMENT.md)
- [Security Configuration](../backend/src/main/resources/application-prod.yml)
- [Troubleshooting Guide](./TROUBLESHOOTING.md)

