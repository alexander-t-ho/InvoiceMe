# Railway Backend Setup - Next Steps

## ✅ Completed via CLI

1. **Project Linked**: ✅
2. **Backend Service Created**: ✅ (Service ID: `45230ea9-c888-4496-8424-49674a4b0900`)
3. **Environment Variables Set**:
   - ✅ `JWT_SECRET=4IEDy759pC20qDqiazZxSE63QwTU9Ng3ydioZevyNFY=`
   - ✅ `JWT_EXPIRATION=86400000`
   - ✅ `PORT=8081`
   - ✅ `CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app`
   - ⚠️ `SPRING_PROFILES_ACTIVE=prod` (may need to set again)

## 📋 Remaining Steps (Use Railway Dashboard)

### Step 1: Create PostgreSQL Database

**Option A: Via Dashboard**
1. Go to: https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff
2. Click **"New +"** → **"Database"** → **"Add PostgreSQL"**
3. Wait for provisioning (1-2 minutes)

**Option B: Via CLI** (if database service exists)
```bash
cd backend
railway service [postgres-service-name]
railway variables
```

### Step 2: Get Database Connection Variables

1. Click on the PostgreSQL service in Railway dashboard
2. Go to **"Variables"** tab
3. **Copy these values**:
   - `PGHOST`
   - `PGPORT` (usually 5432)
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

### Step 3: Set Database Environment Variables

In the **backend** service, go to **"Variables"** tab and add:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]
```
*(Replace [PGHOST], [PGPORT], [PGDATABASE] with actual values)*

```
SPRING_DATASOURCE_USERNAME=[PGUSER]
```
*(Replace [PGUSER] with actual value)*

```
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]
```
*(Replace [PGPASSWORD] with actual value)*

Also verify:
```
SPRING_PROFILES_ACTIVE=prod
```

### Step 4: Configure Backend Service

1. Click on **"backend"** service
2. Go to **"Settings"** tab
3. Set **"Root Directory"** to: `backend`
4. Go to **"Build"** section
5. Set **"Builder"** to: `NIXPACKS`

### Step 5: Deploy

1. Railway will auto-deploy when you push to main
2. Or click **"Deploy"** button in the dashboard
3. Wait for deployment (2-3 minutes)
4. Go to **"Settings"** → **"Networking"**
5. Click **"Generate Domain"** to get public URL
6. **Copy the backend URL** (e.g., `https://your-backend.railway.app`)

### Step 6: Verify Deployment

```bash
curl https://your-backend.railway.app/actuator/health
```

Should return: `{"status":"UP"}`

## 🔗 Quick Links

- **Dashboard**: https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff
- **Project ID**: `69471b6e-ddd9-450e-919e-33d83cba9bff`
- **Service ID**: `45230ea9-c888-4496-8424-49674a4b0900`

## 📝 Notes

- After Vercel deployment, update `CORS_ALLOWED_ORIGINS` with actual Vercel URL
- Database connection string format: `jdbc:postgresql://host:port/database`
- Railway auto-detects Java/Gradle projects but verify NIXPACKS builder is selected

