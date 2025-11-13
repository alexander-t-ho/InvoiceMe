# Deploy to Production - Step by Step

## Railway Project ID
`69471b6e-ddd9-450e-919e-33d83cba9bff`

## Step 1: Railway Backend Deployment

### 1.1 Link Railway CLI (if not already linked)
```bash
railway login
railway link 69471b6e-ddd9-450e-919e-33d83cba9bff
```

### 1.2 Create PostgreSQL Database
1. Go to: https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff
2. Click **"New +"** → **"Database"** → **"Add PostgreSQL"**
3. Wait for provisioning (1-2 minutes)
4. Click on PostgreSQL service → **"Variables"** tab
5. Copy these values:
   - `PGHOST`
   - `PGPORT` (usually 5432)
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

### 1.3 Configure Backend Service
1. In Railway dashboard, click on **"backend"** service (or create it: **"New +"** → **"GitHub Repo"**)
2. **Settings** → **General**:
   - Set **Root Directory**: `backend`
3. **Settings** → **Build**:
   - Set **Builder**: `NIXPACKS`

### 1.4 Set Environment Variables
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

*(Replace [PGHOST], [PGPORT], [PGDATABASE], [PGUSER], [PGPASSWORD] with actual values from Step 1.2)*

### 1.5 Deploy Backend
1. Railway will auto-deploy on push to main, OR
2. Click **"Deploy"** button in dashboard
3. Wait for deployment (2-3 minutes)
4. Go to **"Settings"** → **"Networking"**
5. Click **"Generate Domain"** if needed
6. **Copy the backend URL** (e.g., `https://your-backend.railway.app`)

### 1.6 Verify Backend
```bash
curl https://your-backend.railway.app/actuator/health
```
Should return: `{"status":"UP"}`

---

## Step 2: Vercel Frontend Deployment

### 2.1 Import Project to Vercel
1. Go to: https://vercel.com
2. Sign in with GitHub
3. Click **"Add New"** → **"Project"**
4. Import your **InvoiceMe** repository
5. Configure:
   - **Root Directory**: `frontend`
   - **Framework Preset**: Next.js (auto-detected)
   - **Build Command**: `npm run build` (auto)
   - **Output Directory**: `.next` (auto)

### 2.2 Set Environment Variable
In Vercel project → **"Settings"** → **"Environment Variables"**, add:

```
NEXT_PUBLIC_API_URL=https://your-backend.railway.app/api/v1
```

*(Replace with your actual Railway backend URL from Step 1.5)*

### 2.3 Deploy
1. Click **"Deploy"**
2. Wait for build to complete (1-2 minutes)
3. **Copy the frontend URL** (e.g., `https://invoice-me.vercel.app`)

---

## Step 3: Connect Frontend and Backend

### 3.1 Update CORS in Backend
1. Go back to Railway backend service
2. **Variables** tab → Update:
   ```
   CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app
   ```
   *(Replace with your actual Vercel frontend URL)*
3. Railway will auto-redeploy

### 3.2 Verify Frontend
1. Visit your Vercel URL
2. Should see login page
3. Test login functionality

---

## Step 4: Test Production Deployment

1. **Backend Health**: `https://your-backend.railway.app/actuator/health`
2. **Frontend**: Visit Vercel URL
3. **Login**: Test with admin credentials
4. **API Calls**: Verify data loads correctly

---

## Troubleshooting

### Backend won't start
- Check all environment variables are set correctly
- Verify database connection string format
- Check Railway deployment logs

### Frontend can't connect to backend
- Verify `NEXT_PUBLIC_API_URL` is set correctly
- Check CORS configuration in backend
- Ensure backend URL uses HTTPS

### CORS errors
- Update `CORS_ALLOWED_ORIGINS` in Railway backend
- Include both `https://your-domain.vercel.app` and `https://www.your-domain.vercel.app` if using custom domain

---

## Next Steps

- [ ] Set up custom domain
- [ ] Configure SSL certificates
- [ ] Set up monitoring
- [ ] Configure backups
