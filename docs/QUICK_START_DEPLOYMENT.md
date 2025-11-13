# Quick Start Deployment Guide

This is a condensed guide for quickly deploying InvoiceMe to production. For detailed instructions, see [Production Deployment Guide](./PRODUCTION_DEPLOYMENT.md).

## Prerequisites Checklist

- [ ] GitHub repository with code
- [ ] Vercel account (free)
- [ ] Railway or Render account (free)
- [ ] Domain name (optional)

## 5-Minute Deployment

### Step 1: Database (2 minutes)

**Railway:**
1. Go to [railway.app](https://railway.app) → New Project → Provision PostgreSQL
2. Copy database connection details

**Render:**
1. Go to [render.com](https://render.com) → New + → PostgreSQL
2. Copy database connection details

### Step 2: Backend (2 minutes)

**Railway:**
1. In same project → New + → GitHub Repo → Select InvoiceMe
2. Set Root Directory: `backend`
3. Add environment variables:
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=jdbc:postgresql://[host]:5432/[db]
   SPRING_DATASOURCE_USERNAME=[user]
   SPRING_DATASOURCE_PASSWORD=[password]
   JWT_SECRET=[generate: openssl rand -base64 32]
   CORS_ALLOWED_ORIGINS=https://yourdomain.com
   ```
4. Deploy → Copy backend URL

**Render:**
1. New + → Web Service → Connect GitHub → Select InvoiceMe
2. Settings:
   - Root Directory: `backend`
   - Build: `./gradlew build -x test`
   - Start: `java -jar build/libs/*.jar`
3. Add environment variables (same as Railway)
4. Deploy → Copy backend URL

### Step 3: Frontend (1 minute)

**Vercel:**
1. Go to [vercel.com](https://vercel.com) → Add New → Project
2. Import InvoiceMe repository
3. Settings:
   - Root Directory: `frontend`
   - Framework: Next.js (auto)
4. Environment Variable:
   ```
   NEXT_PUBLIC_API_URL=https://your-backend-url.com/api/v1
   ```
5. Deploy → Copy frontend URL

### Step 4: Connect (Optional)

1. Update `CORS_ALLOWED_ORIGINS` in backend with frontend URL
2. Update `NEXT_PUBLIC_API_URL` in Vercel with backend URL
3. Redeploy both services

## Verify

1. Backend: `https://your-backend-url.com/actuator/health` → `{"status":"UP"}`
2. Frontend: Visit URL → Should load
3. Test login → Should work

## Common Issues

- **CORS errors**: Update `CORS_ALLOWED_ORIGINS` in backend
- **Can't connect**: Check `NEXT_PUBLIC_API_URL` in Vercel
- **Database error**: Verify connection string format

## Next Steps

- [ ] Set up domain (see [Production Deployment Guide](./PRODUCTION_DEPLOYMENT.md#phase-4-domain-and-ssl-setup))
- [ ] Configure monitoring (see [Monitoring Guide](./MONITORING.md))
- [ ] Set up backups

---

**Need help?** See [Troubleshooting Guide](./TROUBLESHOOTING.md)

