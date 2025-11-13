# Rollback Procedures

This guide explains how to rollback deployments if issues occur in production.

## Table of Contents

1. [Frontend Rollback (Vercel)](#frontend-rollback-vercel)
2. [Backend Rollback (Railway/Render)](#backend-rollback-railwayrender)
3. [Database Rollback](#database-rollback)
4. [Emergency Procedures](#emergency-procedures)

---

## Frontend Rollback (Vercel)

### Automatic Rollback

Vercel automatically keeps previous deployments. To rollback:

1. **Via Vercel Dashboard**
   - Go to your Vercel project
   - Click on "Deployments" tab
   - Find the previous working deployment
   - Click the "..." menu
   - Select "Promote to Production"

2. **Via Vercel CLI**
   ```bash
   # Install Vercel CLI if not already installed
   npm i -g vercel
   
   # Login to Vercel
   vercel login
   
   # List deployments
   vercel ls
   
   # Promote previous deployment
   vercel promote <deployment-url>
   ```

### Manual Rollback

If automatic rollback doesn't work:

1. **Revert Git Commit**
   ```bash
   # Find the commit hash of last working version
   git log
   
   # Revert to that commit
   git revert <commit-hash>
   
   # Push to trigger new deployment
   git push origin main
   ```

2. **Quick Fix Deployment**
   - Make minimal fix in code
   - Commit and push
   - Vercel will auto-deploy

---

## Backend Rollback (Railway/Render)

### Railway Rollback

1. **Via Railway Dashboard**
   - Go to your Railway project
   - Click on your backend service
   - Go to "Deployments" tab
   - Find previous working deployment
   - Click "Redeploy" on that deployment

2. **Via Railway CLI**
   ```bash
   # Install Railway CLI
   npm i -g @railway/cli
   
   # Login
   railway login
   
   # List deployments
   railway deployments
   
   # Rollback to specific deployment
   railway rollback <deployment-id>
   ```

### Render Rollback

1. **Via Render Dashboard**
   - Go to your Render dashboard
   - Click on your backend service
   - Go to "Events" tab
   - Find previous successful deployment
   - Click "Manual Deploy" > "Deploy latest commit"
   - Or use "Rollback" option if available

2. **Via Git Revert**
   ```bash
   # Revert to previous commit
   git revert <commit-hash>
   git push origin main
   # Render will auto-deploy
   ```

---

## Database Rollback

### Restore from Backup

1. **Railway Database Backup**
   ```bash
   # Railway provides automatic backups
   # Go to PostgreSQL service > Backups
   # Download backup file
   # Restore using:
   psql -h <host> -U <user> -d <database> < backup.sql
   ```

2. **Render Database Backup**
   ```bash
   # Render provides daily backups
   # Go to Database > Backups
   # Download backup
   # Restore using:
   pg_restore -h <host> -U <user> -d <database> backup.dump
   ```

3. **Manual Backup Restore**
   ```bash
   # If you have manual backup
   # Connect to database
   psql -h <host> -U <user> -d <database>
   
   # Drop and recreate database (CAUTION: Data loss)
   DROP DATABASE invoiceme;
   CREATE DATABASE invoiceme;
   
   # Restore from backup
   \q
   psql -h <host> -U <user> -d invoiceme < backup.sql
   ```

### Schema Rollback

If only schema changes need rollback:

1. **Identify Migration to Rollback**
   - Check migration history
   - Identify problematic migration

2. **Manual Schema Fix**
   ```sql
   -- Connect to database
   psql -h <host> -U <user> -d invoiceme
   
   -- Manually revert schema changes
   -- Example: Drop added column
   ALTER TABLE table_name DROP COLUMN column_name;
   ```

---

## Emergency Procedures

### Complete System Rollback

If entire system needs rollback:

1. **Stop Services**
   - Pause Railway/Render backend service
   - Pause Vercel frontend (if possible)

2. **Restore Database**
   - Restore from most recent backup
   - Verify data integrity

3. **Rollback Backend**
   - Deploy previous working backend version
   - Verify backend health

4. **Rollback Frontend**
   - Promote previous frontend deployment
   - Verify frontend works

5. **Verify System**
   - Test all critical features
   - Verify data integrity
   - Check logs for errors

### Partial Rollback

If only one component needs rollback:

1. **Identify Component**
   - Determine if frontend, backend, or database issue

2. **Rollback Component**
   - Follow component-specific rollback procedure

3. **Verify Fix**
   - Test affected functionality
   - Monitor logs

### Data Recovery

If data corruption occurs:

1. **Stop Writes**
   - Pause backend service to prevent further corruption

2. **Assess Damage**
   - Identify affected tables/records
   - Determine backup to use

3. **Restore Data**
   - Restore from backup
   - Or manually fix corrupted data

4. **Verify Integrity**
   - Check data consistency
   - Verify relationships

5. **Resume Service**
   - Start backend service
   - Monitor for issues

---

## Prevention

### Before Deployment

1. **Test in Staging**
   - Always test changes in staging first
   - Verify all features work

2. **Create Backup**
   - Backup database before major changes
   - Document current deployment version

3. **Review Changes**
   - Code review all changes
   - Check for breaking changes

### During Deployment

1. **Monitor Closely**
   - Watch deployment logs
   - Monitor health checks
   - Check error rates

2. **Gradual Rollout**
   - Deploy to small percentage first
   - Gradually increase if stable

3. **Have Rollback Plan**
   - Know rollback procedure
   - Have backup ready
   - Test rollback procedure

### After Deployment

1. **Verify Functionality**
   - Test critical features
   - Monitor error logs
   - Check performance metrics

2. **Keep Monitoring**
   - Monitor for 24-48 hours
   - Watch for issues
   - Be ready to rollback

---

## Rollback Checklist

Before rolling back:

- [ ] Identify issue and root cause
- [ ] Determine rollback scope (full/partial)
- [ ] Verify backup is available
- [ ] Document current state
- [ ] Notify team/stakeholders
- [ ] Execute rollback procedure
- [ ] Verify rollback success
- [ ] Test critical functionality
- [ ] Monitor for issues
- [ ] Document lessons learned

---

## Quick Reference

### Vercel
```bash
# Promote previous deployment
vercel promote <deployment-url>
```

### Railway
```bash
# Rollback to previous deployment
railway rollback <deployment-id>
```

### Render
- Use dashboard: Service > Events > Rollback

### Database
```bash
# Restore from backup
psql -h <host> -U <user> -d <database> < backup.sql
```

---

**Last Updated**: 2025-11-11  
**Version**: 1.0

