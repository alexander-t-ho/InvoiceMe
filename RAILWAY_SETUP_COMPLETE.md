# Railway Backend Setup - CLI Progress

## ✅ Completed Steps

1. **Project Linked**: Successfully linked to Railway project `69471b6e-ddd9-450e-919e-33d83cba9bff`
2. **Backend Service Created**: Service named "backend" is created and linked
3. **Service ID**: `45230ea9-c888-4496-8424-49674a4b0900`

## 📋 Next Steps (Use Railway Dashboard)

Since the Railway CLI version doesn't support `variables set` command directly, you'll need to complete the setup in the Railway dashboard:

### Step 1: Open Railway Dashboard
```bash
railway open
```
Or visit: https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff

### Step 2: Create PostgreSQL Database

1. In Railway dashboard, click **"New +"** → **"Database"** → **"Add PostgreSQL"**
2. Wait for database to provision
3. Click on the PostgreSQL service
4. Go to **"Variables"** tab
5. **Copy these values**:
   - `PGHOST`
   - `PGPORT`
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

### Step 3: Configure Backend Service

1. Click on the **"backend"** service
2. Go to **"Settings"** tab
3. Set **"Root Directory"** to: `backend`
4. Go to **"Build"** section
5. Set **"Builder"** to: `NIXPACKS`

### Step 4: Set Environment Variables

In the backend service, go to **"Variables"** tab and add:

```
SPRING_PROFILES_ACTIVE=prod
```

```
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]
```
*(Replace with values from Step 2)*

```
SPRING_DATASOURCE_USERNAME=[PGUSER]
```
*(Replace with value from Step 2)*

```
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]
```
*(Replace with value from Step 2)*

```
JWT_SECRET=4IEDy759pC20qDqiazZxSE63QwTU9Ng3ydioZevyNFY=
```

```
JWT_EXPIRATION=86400000
```

```
CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app
```
*(Update this after Vercel deployment)*

```
PORT=8081
```

### Step 5: Deploy

1. Railway will auto-deploy when you push to main
2. Or click **"Deploy"** in the dashboard
3. Wait for deployment (2-3 minutes)
4. Go to **"Settings"** → **"Networking"**
5. Click **"Generate Domain"** to get public URL
6. **Copy the backend URL**

### Step 6: Verify

Test the backend:
```bash
curl https://your-backend.railway.app/actuator/health
```

Should return: `{"status":"UP"}`

## 🔑 Important Information

- **JWT Secret**: `4IEDy759pC20qDqiazZxSE63QwTU9Ng3ydioZevyNFY=`
- **Project ID**: `69471b6e-ddd9-450e-919e-33d83cba9bff`
- **Service ID**: `45230ea9-c888-4496-8424-49674a4b0900`
- **Dashboard**: https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff

## 📖 Full Instructions

See `DEPLOYMENT_STEPS.md` for complete deployment guide including Vercel setup.

