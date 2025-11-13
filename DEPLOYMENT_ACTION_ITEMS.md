# 🚀 Production Deployment - Action Items

## ✅ Completed
- [x] Code committed and pushed
- [x] Deployment documentation created
- [x] Railway configuration files ready
- [x] Vercel configuration ready

## 📋 Next Steps - Do These Now

### 1. Railway Backend Setup (5 minutes)

**Open Railway Dashboard:**
https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff

**A. Create PostgreSQL Database:**
1. Click **"New +"** → **"Database"** → **"Add PostgreSQL"**
2. Wait 1-2 minutes for provisioning
3. Click on PostgreSQL service → **"Variables"** tab
4. **Copy these values** (you'll need them):
   - `PGHOST`
   - `PGPORT` (usually 5432)
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

**B. Configure Backend Service:**
1. Click on **"backend"** service (or create: **"New +"** → **"GitHub Repo"** → Select InvoiceMe)
2. **Settings** → **General**:
   - **Root Directory**: `backend`
3. **Settings** → **Build**:
   - **Builder**: `NIXPACKS`

**C. Set Environment Variables:**
In backend service → **"Variables"** tab, add:

```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]
SPRING_DATASOURCE_USERNAME=[PGUSER]
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]
JWT_SECRET=4IEDy759pC20qDqiazZxSE63QwTU9Ng3ydioZevyNFY=
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app
PORT=8081
```

*(Replace [PGHOST], [PGPORT], [PGDATABASE], [PGUSER], [PGPASSWORD] with values from step A)*

**D. Deploy:**
1. Click **"Deploy"** or wait for auto-deploy
2. Wait 2-3 minutes
3. **Settings** → **Networking** → **Generate Domain** (if needed)
4. **Copy backend URL** (e.g., `https://your-backend.railway.app`)

**E. Verify:**
```bash
curl https://your-backend.railway.app/actuator/health
```
Should return: `{"status":"UP"}`

---

### 2. Vercel Frontend Setup (3 minutes)

**Open Vercel Dashboard:**
https://vercel.com/alexander-hos-projects/frontend/settings

**A. Fix Project Settings:**
1. **General** → **Root Directory**: Set to `frontend`
2. **General** → **Framework Preset**: Set to `Next.js`
3. **Build & Development Settings**:
   - Build Command: `npm run build` (should auto-detect)
   - Output Directory: Leave empty (Next.js uses `.next` by default)
4. Click **Save**

**B. Set Environment Variable:**
1. Go to **"Environment Variables"** tab
2. Add:
   - **Name**: `NEXT_PUBLIC_API_URL`
   - **Value**: `https://your-backend.railway.app/api/v1`
   - **Environment**: Production (and Preview/Development if desired)
   - *(Replace with your actual Railway backend URL from step 1.D)*

**C. Deploy:**
1. Go to **"Deployments"** tab
2. Click **"Redeploy"** on latest deployment, OR
3. Push to main branch to trigger auto-deploy
4. **Copy frontend URL** (e.g., `https://invoice-me.vercel.app`)

---

### 3. Connect Services (2 minutes)

**Update CORS in Railway:**
1. Go back to Railway backend service
2. **Variables** tab → Update:
   ```
   CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app
   ```
   *(Replace with your actual Vercel frontend URL from step 2.C)*
3. Railway will auto-redeploy

---

### 4. Test Deployment

1. **Backend Health Check:**
   ```bash
   curl https://your-backend.railway.app/actuator/health
   ```

2. **Frontend:**
   - Visit your Vercel URL
   - Should see login page
   - Test login functionality

3. **End-to-End:**
   - Login with admin credentials
   - Verify data loads correctly
   - Test creating/editing invoices

---

## 🔑 Important Values

- **JWT Secret**: `4IEDy759pC20qDqiazZxSE63QwTU9Ng3ydioZevyNFY=`
- **Railway Project ID**: `69471b6e-ddd9-450e-919e-33d83cba9bff`
- **Backend Port**: `8081`

---

## 📚 Reference Documents

- **Quick Guide**: `DEPLOY_NOW.md`
- **Status**: `DEPLOYMENT_STATUS.md`
- **Environment Variables**: `docs/PRODUCTION_ENV_VARS.md`
- **Full Guide**: `docs/PRODUCTION_DEPLOYMENT.md`

---

## ⚠️ Troubleshooting

**Backend won't start:**
- Check all environment variables are set
- Verify database connection string format
- Check Railway deployment logs

**Frontend can't connect:**
- Verify `NEXT_PUBLIC_API_URL` is set correctly
- Check CORS configuration in backend
- Ensure backend URL uses HTTPS

**CORS errors:**
- Update `CORS_ALLOWED_ORIGINS` in Railway
- Include both `https://your-domain.vercel.app` and `https://www.your-domain.vercel.app` if using custom domain

