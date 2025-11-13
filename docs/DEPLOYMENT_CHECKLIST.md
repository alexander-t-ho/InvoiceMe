# Production Deployment Checklist

Use this checklist to ensure all steps are completed before going live.

## Pre-Deployment

### Code Preparation
- [ ] All tests passing
- [ ] Code reviewed and approved
- [ ] No console.log or debug statements in production code
- [ ] Environment variables documented
- [ ] Security review completed

### Configuration
- [ ] Production environment variables prepared
- [ ] JWT secret generated (32+ characters)
- [ ] Database credentials secured
- [ ] CORS origins configured
- [ ] API URLs verified

## Database Setup

- [ ] PostgreSQL database provisioned
- [ ] Database credentials saved securely
- [ ] Connection string formatted correctly (JDBC format)
- [ ] Database backups configured
- [ ] Database accessible from backend service

## Backend Deployment

### Railway
- [ ] Railway account created
- [ ] Project created
- [ ] PostgreSQL service added
- [ ] Backend service created
- [ ] Root directory set to `backend`
- [ ] Build command configured: `./gradlew build -x test`
- [ ] Start command configured: `java -jar build/libs/*.jar`
- [ ] All environment variables set
- [ ] Service deployed successfully
- [ ] Health check passing: `/actuator/health`

### Render
- [ ] Render account created
- [ ] PostgreSQL database created
- [ ] Web service created
- [ ] Root directory set to `backend`
- [ ] Build command configured
- [ ] Start command configured
- [ ] Health check path set: `/actuator/health`
- [ ] All environment variables set
- [ ] Service deployed successfully
- [ ] Health check passing

## Frontend Deployment

### Vercel
- [ ] Vercel account created
- [ ] Project imported from GitHub
- [ ] Root directory set to `frontend`
- [ ] Framework preset: Next.js
- [ ] `NEXT_PUBLIC_API_URL` environment variable set
- [ ] Production build successful
- [ ] Frontend accessible
- [ ] No console errors

## Domain and SSL

- [ ] Domain registered (if using custom domain)
- [ ] DNS records configured for frontend
- [ ] DNS records configured for backend (if using custom domain)
- [ ] SSL certificates active (automatic on Vercel/Railway/Render)
- [ ] HTTPS enforced
- [ ] All URLs use HTTPS

## Configuration Verification

- [ ] Backend CORS allows frontend domain
- [ ] Frontend API URL points to backend
- [ ] Database connection working
- [ ] JWT authentication working
- [ ] All environment variables set correctly

## Testing

### Functional Testing
- [ ] User registration works
- [ ] User login works
- [ ] Create customer works
- [ ] Create invoice works
- [ ] View invoices works
- [ ] Payment processing works (if applicable)
- [ ] All CRUD operations work

### Security Testing
- [ ] Unauthenticated requests rejected
- [ ] CORS configured correctly
- [ ] HTTPS enforced
- [ ] No sensitive data exposed
- [ ] JWT tokens working

### Performance Testing
- [ ] Page load times acceptable
- [ ] API response times acceptable
- [ ] Database queries optimized
- [ ] No memory leaks

## Monitoring Setup

- [ ] Health check monitoring configured
- [ ] Uptime monitoring set up (UptimeRobot/Pingdom)
- [ ] Error tracking configured (optional: Sentry)
- [ ] Log aggregation set up
- [ ] Alert contacts configured
- [ ] Alert thresholds set

## Documentation

- [ ] Deployment guide reviewed
- [ ] Environment variables documented
- [ ] Troubleshooting guide available
- [ ] Rollback procedure documented
- [ ] Team has access to documentation

## Post-Deployment

- [ ] Initial admin user created
- [ ] All features tested in production
- [ ] Monitoring verified
- [ ] Logs reviewed for errors
- [ ] Performance metrics baseline established
- [ ] Team notified of deployment

## Emergency Preparedness

- [ ] Rollback procedure tested
- [ ] Backup restoration tested
- [ ] Emergency contacts documented
- [ ] Incident response plan ready

## Sign-Off

- [ ] All checklist items completed
- [ ] Stakeholder approval obtained
- [ ] Go-live date confirmed
- [ ] Team ready for launch

---

**Date**: _______________  
**Deployed By**: _______________  
**Approved By**: _______________

---

**Notes**:
- Keep this checklist updated as deployment process evolves
- Review and update before each major deployment
- Document any deviations or issues encountered

