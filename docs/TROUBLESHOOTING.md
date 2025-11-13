# Troubleshooting Guide

Common issues and solutions for InvoiceMe production deployment.

## Table of Contents

1. [Backend Issues](#backend-issues)
2. [Frontend Issues](#frontend-issues)
3. [Database Issues](#database-issues)
4. [Authentication Issues](#authentication-issues)
5. [Network and Connectivity](#network-and-connectivity)
6. [Performance Issues](#performance-issues)

---

## Backend Issues

### Backend Won't Start

**Symptoms**: 
- Deployment fails
- Service crashes immediately after start
- Health check fails

**Diagnosis**:
1. Check logs in Railway/Render dashboard
2. Look for error messages in application logs
3. Verify environment variables are set

**Solutions**:

1. **Missing Environment Variables**
   ```bash
   # Verify all required variables are set:
   - SPRING_PROFILES_ACTIVE=prod
   - SPRING_DATASOURCE_URL
   - SPRING_DATASOURCE_USERNAME
   - SPRING_DATASOURCE_PASSWORD
   - JWT_SECRET
   ```

2. **Database Connection Failed**
   - Verify database URL format: `jdbc:postgresql://host:port/database`
   - Check database credentials
   - Ensure database is accessible from backend service
   - For Render: Verify database is in same region

3. **Port Configuration**
   - Railway/Render set PORT automatically
   - Backend should use `${PORT:-8081}` in Dockerfile
   - Check application-prod.yml uses `${SERVER_PORT:8081}`

4. **Build Failures**
   - Check Gradle build logs
   - Verify Java 17 is available
   - Check for dependency issues

### Backend Returns 500 Errors

**Symptoms**: API calls return 500 Internal Server Error

**Diagnosis**:
1. Check backend logs for stack traces
2. Verify database connectivity
3. Check for null pointer exceptions

**Solutions**:

1. **Database Connection Pool Exhausted**
   - Increase `maximum-pool-size` in application-prod.yml
   - Check for connection leaks
   - Verify connection timeout settings

2. **JWT Secret Issues**
   - Verify JWT_SECRET is set and valid
   - Ensure secret is at least 32 characters
   - Check for special characters that need escaping

3. **Missing Dependencies**
   - Verify all required services are running
   - Check for missing environment variables

---

## Frontend Issues

### Frontend Build Fails

**Symptoms**: 
- Vercel deployment fails
- Build errors in logs

**Solutions**:

1. **TypeScript Errors**
   ```bash
   # Run locally to see errors
   cd frontend
   npm run build
   ```
   - Fix TypeScript errors
   - Check for type mismatches

2. **Missing Environment Variables**
   - Verify `NEXT_PUBLIC_API_URL` is set in Vercel
   - Check variable is set for Production environment
   - Redeploy after adding variables

3. **Dependency Issues**
   ```bash
   # Clear cache and reinstall
   rm -rf node_modules package-lock.json
   npm install
   ```

### Frontend Can't Connect to Backend

**Symptoms**: 
- API calls fail
- Network errors in browser console
- CORS errors

**Solutions**:

1. **Incorrect API URL**
   - Verify `NEXT_PUBLIC_API_URL` in Vercel settings
   - Ensure URL includes `/api/v1`
   - Check URL uses HTTPS in production

2. **CORS Errors**
   - Verify `CORS_ALLOWED_ORIGINS` includes frontend domain
   - Check backend CORS configuration
   - Ensure frontend domain matches exactly (including https://)

3. **Backend Not Accessible**
   - Verify backend URL is accessible in browser
   - Check backend health endpoint
   - Verify backend is deployed and running

### Blank Page or White Screen

**Symptoms**: Frontend loads but shows blank page

**Solutions**:

1. **Check Browser Console**
   - Look for JavaScript errors
   - Check for failed API calls
   - Verify authentication state

2. **Environment Variables**
   - Verify `NEXT_PUBLIC_API_URL` is set
   - Check for undefined variables
   - Ensure variables are available at build time

3. **Build Issues**
   - Check Vercel build logs
   - Verify all dependencies are installed
   - Check for missing files

---

## Database Issues

### Database Connection Failed

**Symptoms**: 
- Backend can't connect to database
- Connection timeout errors
- Authentication failed errors

**Solutions**:

1. **Connection String Format**
   - Verify JDBC URL format: `jdbc:postgresql://host:port/database`
   - Check for typos in host, port, or database name
   - Ensure no extra spaces or characters

2. **Credentials**
   - Verify username and password are correct
   - Check for special characters that need URL encoding
   - Ensure credentials match database settings

3. **Network Access**
   - For Render: Ensure database allows connections from backend
   - Check firewall rules
   - Verify database is in same region as backend

4. **Database Not Running**
   - Check database service status
   - Verify database is provisioned
   - Check database logs for errors

### Database Migration Issues

**Symptoms**: 
- Schema not created
- Tables missing
- Migration errors

**Solutions**:

1. **Hibernate DDL Auto**
   - Verify `ddl-auto: validate` in production
   - For initial setup, temporarily use `update`
   - Switch back to `validate` after schema is created

2. **Schema Creation**
   - Check application logs for schema creation
   - Verify database user has CREATE privileges
   - Check for permission errors

---

## Authentication Issues

### Login Fails

**Symptoms**: 
- Can't log in
- 401 Unauthorized errors
- Token generation fails

**Solutions**:

1. **JWT Secret**
   - Verify `JWT_SECRET` is set
   - Ensure secret is at least 32 characters
   - Check for special characters

2. **User Credentials**
   - Verify user exists in database
   - Check password is correct
   - Verify user is active

3. **Token Expiration**
   - Check `JWT_EXPIRATION` setting
   - Verify token is not expired
   - Check system clock is correct

### 403 Forbidden Errors

**Symptoms**: Authenticated requests return 403

**Solutions**:

1. **CORS Configuration**
   - Verify frontend domain is in `CORS_ALLOWED_ORIGINS`
   - Check CORS headers in response
   - Ensure credentials are included in requests

2. **Authorization**
   - Verify user has required permissions
   - Check role-based access control
   - Verify JWT token is valid

---

## Network and Connectivity

### SSL Certificate Issues

**Symptoms**: 
- HTTPS not working
- Certificate errors
- Mixed content warnings

**Solutions**:

1. **DNS Propagation**
   - Wait for DNS to propagate (up to 48 hours)
   - Verify DNS records are correct
   - Use DNS checker tools

2. **Certificate Provisioning**
   - Vercel/Railway/Render auto-provision SSL
   - Check certificate status in dashboard
   - Wait for certificate to be issued

3. **Mixed Content**
   - Ensure all URLs use HTTPS
   - Check for hardcoded HTTP URLs
   - Verify API URL uses HTTPS

### Timeout Errors

**Symptoms**: 
- Requests timeout
- Slow response times
- Connection timeouts

**Solutions**:

1. **Database Queries**
   - Check for slow queries
   - Verify database indexes
   - Optimize query performance

2. **Network Latency**
   - Check region selection
   - Verify services are in same region
   - Consider CDN for static assets

3. **Resource Limits**
   - Check platform resource limits
   - Verify service tier allows required resources
   - Monitor memory and CPU usage

---

## Performance Issues

### Slow Page Loads

**Symptoms**: 
- Pages load slowly
- High Time to First Byte (TTFB)
- Slow API responses

**Solutions**:

1. **Frontend Optimization**
   - Check bundle size
   - Verify code splitting is working
   - Check for large dependencies

2. **API Performance**
   - Monitor API response times
   - Check database query performance
   - Verify caching is enabled

3. **CDN Configuration**
   - Verify static assets are cached
   - Check CDN settings in Vercel
   - Ensure proper cache headers

### High Memory Usage

**Symptoms**: 
- Service crashes
- Out of memory errors
- Slow performance

**Solutions**:

1. **Database Connection Pool**
   - Reduce `maximum-pool-size` if too high
   - Check for connection leaks
   - Monitor connection pool usage

2. **Application Memory**
   - Check for memory leaks
   - Verify garbage collection settings
   - Monitor heap usage

3. **Platform Limits**
   - Check platform memory limits
   - Upgrade service tier if needed
   - Optimize application code

---

## Getting Help

If you're still experiencing issues:

1. **Check Logs**
   - Backend: Railway/Render dashboard logs
   - Frontend: Vercel deployment logs
   - Database: Platform database logs

2. **Review Documentation**
   - [Production Deployment Guide](./PRODUCTION_DEPLOYMENT.md)
   - [Environment Variables](./PRODUCTION_ENV_VARS.md)
   - Platform-specific documentation

3. **Common Commands**
   ```bash
   # Check backend health
   curl https://your-backend-url.com/actuator/health
   
   # Check frontend
   curl https://your-frontend-url.com
   
   # Test database connection (from backend logs)
   # Look for connection success/failure messages
   ```

---

**Last Updated**: 2025-11-11  
**Version**: 1.0

