# Production Deployment Guide

This guide provides step-by-step instructions for deploying InvoiceMe to production using Vercel (frontend) and Railway/Render (backend).

## Prerequisites

- GitHub account with repository access
- Vercel account (free tier available)
- Railway or Render account (free tier available)
- Domain name (optional but recommended)
- Basic understanding of environment variables

## Table of Contents

1. [Phase 1: Database Setup](#phase-1-database-setup)
2. [Phase 2: Backend Deployment](#phase-2-backend-deployment)
3. [Phase 3: Frontend Deployment](#phase-3-frontend-deployment)
4. [Phase 4: Domain and SSL Setup](#phase-4-domain-and-ssl-setup)
5. [Phase 5: Verification](#phase-5-verification)
6. [Troubleshooting](#troubleshooting)

---

## Phase 1: Database Setup

### Option A: Railway PostgreSQL

1. **Create Railway Account**
   - Go to [railway.app](https://railway.app)
   - Sign up with GitHub

2. **Create New Project**
   - Click "New Project"
   - Select "Provision PostgreSQL"
   - Railway will automatically create a PostgreSQL database

3. **Get Database Credentials**
   - Click on the PostgreSQL service
   - Go to "Variables" tab
   - Note the following variables:
     - `DATABASE_URL` (full connection string)
     - `PGHOST`
     - `PGPORT`
     - `PGUSER`
     - `PGPASSWORD`
     - `PGDATABASE`

4. **Format Connection String for Spring Boot**
   - Railway provides `DATABASE_URL` in format: `postgresql://user:password@host:port/database`
   - Convert to JDBC format: `jdbc:postgresql://host:port/database`
   - Example: `jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway`

### Option B: Render PostgreSQL

1. **Create Render Account**
   - Go to [render.com](https://render.com)
   - Sign up with GitHub

2. **Create PostgreSQL Database**
   - Click "New +" > "PostgreSQL"
   - Choose a name (e.g., `invoiceme-db`)
   - Select region closest to your users
   - Choose free tier or paid plan
   - Click "Create Database"

3. **Get Database Credentials**
   - Go to your database dashboard
   - Find "Internal Database URL" or "External Database URL"
   - Format: `postgresql://user:password@host:port/database`
   - Convert to JDBC: `jdbc:postgresql://host:port/database`

### Option C: Neon.tech PostgreSQL (Serverless)

1. **Create Neon Account**
   - Go to [neon.tech](https://neon.tech)
   - Sign up with GitHub

2. **Create Project**
   - Create a new project
   - Note the connection string
   - Convert to JDBC format as above

---

## Phase 2: Backend Deployment

### Option A: Railway Backend Deployment

1. **Add Backend Service**
   - In your Railway project, click "New +" > "GitHub Repo"
   - Select your InvoiceMe repository
   - Railway will detect it's a Java/Gradle project

2. **Configure Service**
   - **Root Directory**: `backend`
   - **Build Command**: `./gradlew build -x test`
   - **Start Command**: `java -jar build/libs/invoiceme-backend-0.0.1-SNAPSHOT.jar`
   - **Alternative**: `java -jar build/libs/*-SNAPSHOT.jar` (matches any SNAPSHOT jar)
   - **Port**: Railway will auto-detect, but ensure it's set to 8081

3. **Set Environment Variables**
   Go to "Variables" tab and add:
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=jdbc:postgresql://[your-db-host]:5432/[database-name]
   SPRING_DATASOURCE_USERNAME=[db-username]
   SPRING_DATASOURCE_PASSWORD=[db-password]
   JWT_SECRET=[generate-using-openssl-rand-base64-32]
   JWT_EXPIRATION=86400000
   CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
   ```

4. **Link PostgreSQL Service**
   - If using Railway PostgreSQL, click "Variables" tab
   - Click "Reference Variable"
   - Select your PostgreSQL service
   - Reference: `DATABASE_URL`, `PGUSER`, `PGPASSWORD`, etc.
   - Update `SPRING_DATASOURCE_URL` to use referenced variables

5. **Deploy**
   - Railway will automatically deploy on push to main branch
   - Or click "Deploy" to trigger manual deployment
   - Wait for deployment to complete
   - Note the generated URL (e.g., `https://your-backend.railway.app`)

6. **Verify Deployment**
   - Check health endpoint: `https://your-backend.railway.app/actuator/health`
   - Should return: `{"status":"UP"}`

### Option B: Render Backend Deployment

1. **Create Web Service**
   - Go to Render dashboard
   - Click "New +" > "Web Service"
   - Connect your GitHub repository
   - Select the repository

2. **Configure Service**
   - **Name**: `invoiceme-backend`
   - **Region**: Choose closest to your users
   - **Branch**: `main`
   - **Root Directory**: `backend`
   - **Runtime**: `Java`
   - **Build Command**: `./gradlew build -x test`
   - **Start Command**: `java -jar build/libs/invoiceme-backend-0.0.1-SNAPSHOT.jar`
   - **Alternative**: `java -jar build/libs/*-SNAPSHOT.jar` (matches any SNAPSHOT jar)

3. **Set Environment Variables**
   Go to "Environment" tab and add:
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=jdbc:postgresql://[your-db-host]:5432/[database-name]
   SPRING_DATASOURCE_USERNAME=[db-username]
   SPRING_DATASOURCE_PASSWORD=[db-password]
   JWT_SECRET=[generate-using-openssl-rand-base64-32]
   JWT_EXPIRATION=86400000
   CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
   ```

4. **Link Database**
   - If using Render PostgreSQL, go to "Environment" tab
   - Click "Link Database"
   - Select your PostgreSQL database
   - Render will automatically add connection variables

5. **Deploy**
   - Click "Create Web Service"
   - Render will build and deploy
   - Note the generated URL (e.g., `https://invoiceme-backend.onrender.com`)

6. **Verify Deployment**
   - Check health endpoint: `https://your-backend.onrender.com/actuator/health`
   - Should return: `{"status":"UP"}`

---

## Phase 3: Frontend Deployment (Vercel)

1. **Create Vercel Account**
   - Go to [vercel.com](https://vercel.com)
   - Sign up with GitHub

2. **Import Project**
   - Click "Add New..." > "Project"
   - Import your GitHub repository
   - Select the InvoiceMe repository

3. **Configure Project**
   - **Framework Preset**: Next.js (auto-detected)
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build` (default)
   - **Output Directory**: `.next` (default)
   - **Install Command**: `npm install` (default)

4. **Set Environment Variables**
   Go to "Environment Variables" and add:
   ```
   NEXT_PUBLIC_API_URL=https://your-backend-url.com/api/v1
   ```
   - Replace `your-backend-url.com` with your actual backend URL from Phase 2
   - Make sure to include `/api/v1` at the end
   - Select "Production" environment (and optionally Preview/Development)

5. **Deploy**
   - Click "Deploy"
   - Vercel will build and deploy your frontend
   - Wait for deployment to complete
   - Note the generated URL (e.g., `https://invoice-me.vercel.app`)

6. **Verify Deployment**
   - Visit your Vercel URL
   - Check browser console for errors
   - Try logging in to verify API connectivity

---

## Phase 4: Domain and SSL Setup

### 4.1 Register Domain (if needed)

1. **Choose Domain Registrar**
   - Popular options: Namecheap, Google Domains, Cloudflare, GoDaddy
   - Register your desired domain (e.g., `yourdomain.com`)

2. **Note DNS Settings**
   - You'll need to configure DNS records in the next step

### 4.2 Configure DNS for Frontend (Vercel)

1. **Add Domain to Vercel**
   - Go to your Vercel project
   - Go to "Settings" > "Domains"
   - Click "Add Domain"
   - Enter your domain (e.g., `yourdomain.com`)
   - Vercel will show DNS records to add

2. **Configure DNS Records**
   - Go to your domain registrar's DNS settings
   - Add the DNS records Vercel provides:
     - For root domain: A record or CNAME
     - For www: CNAME record pointing to Vercel
   - Wait for DNS propagation (can take up to 48 hours, usually < 1 hour)

3. **SSL Certificate**
   - Vercel automatically provisions SSL certificates via Let's Encrypt
   - SSL will be active once DNS propagates

### 4.3 Configure DNS for Backend (Optional)

If you want a custom domain for your API:

1. **Railway Custom Domain**
   - Go to your Railway service
   - Go to "Settings" > "Networking"
   - Add custom domain (e.g., `api.yourdomain.com`)
   - Configure DNS CNAME record pointing to Railway's domain
   - Railway will automatically provision SSL

2. **Render Custom Domain**
   - Go to your Render service
   - Go to "Settings" > "Custom Domains"
   - Add custom domain (e.g., `api.yourdomain.com`)
   - Configure DNS CNAME record pointing to Render's domain
   - Render will automatically provision SSL

3. **Update Frontend Environment Variable**
   - Update `NEXT_PUBLIC_API_URL` in Vercel to use your custom API domain
   - Redeploy frontend if needed

---

## Phase 5: Verification

### 5.1 Health Checks

1. **Backend Health**
   ```bash
   curl https://your-backend-url.com/actuator/health
   ```
   Should return: `{"status":"UP"}`

2. **Frontend Accessibility**
   - Visit your frontend URL
   - Should load without errors

### 5.2 Functional Testing

1. **Authentication**
   - Try logging in
   - Verify JWT tokens are generated
   - Check that authenticated requests work

2. **API Connectivity**
   - Open browser DevTools > Network tab
   - Verify API calls are going to correct backend URL
   - Check for CORS errors

3. **Database Operations**
   - Create a test customer
   - Create a test invoice
   - Verify data persists

### 5.3 Security Verification

1. **HTTPS**
   - Verify all URLs use HTTPS
   - Check SSL certificate is valid

2. **CORS**
   - Verify CORS is configured correctly
   - Test from your frontend domain

3. **Authentication**
   - Verify unauthenticated requests are rejected
   - Test JWT token expiration

---

## Troubleshooting

### Backend Won't Start

**Symptoms**: Deployment fails or service crashes

**Solutions**:
1. Check logs in Railway/Render dashboard
2. Verify all environment variables are set
3. Check database connection string format
4. Verify JWT_SECRET is set and valid
5. Check port configuration (should be 8081 or use PORT env var)

### Frontend Can't Connect to Backend

**Symptoms**: API calls fail, CORS errors

**Solutions**:
1. Verify `NEXT_PUBLIC_API_URL` is set correctly in Vercel
2. Check backend URL is accessible (try in browser)
3. Verify CORS_ALLOWED_ORIGINS includes your frontend domain
4. Check backend logs for CORS errors
5. Ensure backend is using HTTPS

### Database Connection Failed

**Symptoms**: Backend starts but can't connect to database

**Solutions**:
1. Verify database credentials are correct
2. Check database connection string format (JDBC format)
3. Verify database is accessible from backend service
4. Check firewall/network settings
5. For Render: Ensure database is in same region as backend

### SSL Certificate Issues

**Symptoms**: HTTPS not working, certificate errors

**Solutions**:
1. Wait for DNS propagation (can take up to 48 hours)
2. Verify DNS records are correct
3. Check SSL certificate status in platform dashboard
4. Try accessing via HTTP first, then HTTPS

### Environment Variables Not Working

**Symptoms**: App uses wrong values or defaults

**Solutions**:
1. Verify variables are set in correct environment (Production)
2. Check variable names match exactly (case-sensitive)
3. Redeploy after changing environment variables
4. For Vercel: Ensure `NEXT_PUBLIC_` prefix for client-side variables

---

## Next Steps

After successful deployment:

1. **Set up Monitoring** (see [Monitoring Guide](./MONITORING.md))
2. **Configure Backups** (see [Backup Guide](./BACKUP.md))
3. **Set up CI/CD** (see [CI/CD Guide](./CICD.md))
4. **Create Admin User** (first user registration)
5. **Test All Features** thoroughly

---

## Support

For issues or questions:
- Check [Troubleshooting Guide](./TROUBLESHOOTING.md)
- Review platform-specific documentation:
  - [Vercel Docs](https://vercel.com/docs)
  - [Railway Docs](https://docs.railway.app)
  - [Render Docs](https://render.com/docs)
- Check application logs in platform dashboards

---

**Last Updated**: 2025-11-11  
**Version**: 1.0

