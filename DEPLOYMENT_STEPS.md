# Production Deployment Steps - Railway + Vercel

## Generated JWT Secret
**Use this for your backend deployment:**
```
4IEDy759pC20qDqiazZxSE63QwTU9Ng3ydioZevyNFY=
```

---

## Step 1: Railway Backend Setup

### 1.1 Create PostgreSQL Database

1. Go to Railway Dashboard: https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff
2. Click **"New +"** → **"Database"** → **"Add PostgreSQL"**
3. Wait for database to provision (1-2 minutes)
4. Click on the PostgreSQL service
5. Go to **"Variables"** tab
6. **Copy these values** (you'll need them):
   - `PGHOST`
   - `PGPORT` (usually 5432)
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

### 1.2 Add Backend Service

1. In the same Railway project, click **"New +"** → **"GitHub Repo"**
2. Select your **InvoiceMe** repository
3. Railway will detect it's a Java project
4. **Configure the service:**
   - Click on the newly created service
   - Go to **"Settings"** tab
   - Set **"Root Directory"** to: `backend`
   - Go to **"Build"** section
   - Set **"Builder"** to: `NIXPACKS` (if not already set)

### 1.3 Set Environment Variables

1. In your backend service, go to **"Variables"** tab
2. Click **"New Variable"** and add each of these:

```
SPRING_PROFILES_ACTIVE=prod
```

```
SPRING_DATASOURCE_URL=jdbc:postgresql://[PGHOST]:[PGPORT]/[PGDATABASE]
```
*(Replace [PGHOST], [PGPORT], [PGDATABASE] with values from step 1.1)*

```
SPRING_DATASOURCE_USERNAME=[PGUSER]
```
*(Replace [PGUSER] with value from step 1.1)*

```
SPRING_DATASOURCE_PASSWORD=[PGPASSWORD]
```
*(Replace [PGPASSWORD] with value from step 1.1)*

```
JWT_SECRET=4IEDy759pC20qDqiazZxSE63QwTU9Ng3ydioZevyNFY=
```

```
JWT_EXPIRATION=86400000
```

```
CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app
```
*(We'll update this after Vercel deployment with the actual URL)*

```
PORT=8081
```

### 1.4 Deploy Backend

1. Railway will automatically deploy when you push to main
2. Or click **"Deploy"** button in the service
3. Wait for deployment to complete (2-3 minutes)
4. Go to **"Settings"** → **"Networking"**
5. Click **"Generate Domain"** to get a public URL
6. **Copy the backend URL** (e.g., `https://your-backend.railway.app`)

### 1.5 Verify Backend

Test the backend health endpoint:
```bash
curl https://your-backend.railway.app/actuator/health
```

Should return: `{"status":"UP"}`

---

## Step 2: Vercel Frontend Setup

### 2.1 Import Project

1. Go to Vercel Dashboard: https://vercel.com
2. Sign in with GitHub (if not already)
3. Click **"Add New"** → **"Project"**
4. Find and select your **InvoiceMe** repository
5. Click **"Import"**

### 2.2 Configure Project

1. **Root Directory**: Set to `frontend`
2. **Framework Preset**: Next.js (should auto-detect)
3. **Build Command**: `cd frontend && npm run build` (auto-filled)
4. **Output Directory**: `frontend/.next` (auto-filled)
5. **Install Command**: `cd frontend && npm install` (auto-filled)

### 2.3 Set Environment Variable

1. In the project configuration, scroll to **"Environment Variables"**
2. Click **"Add"**
3. Add this variable:
   - **Key**: `NEXT_PUBLIC_API_URL`
   - **Value**: `https://your-backend.railway.app/api/v1`
     *(Replace with your actual Railway backend URL from step 1.4)*
   - **Environment**: Select **Production** (and optionally Preview/Development)

### 2.4 Deploy

1. Click **"Deploy"** button
2. Wait for build to complete (2-3 minutes)
3. Once deployed, **copy the frontend URL** (e.g., `https://your-app.vercel.app`)

---

## Step 3: Connect Frontend and Backend

### 3.1 Update CORS in Railway

1. Go back to Railway dashboard
2. Open your backend service
3. Go to **"Variables"** tab
4. Find `CORS_ALLOWED_ORIGINS` variable
5. Click **"Edit"**
6. Update the value to your Vercel URL:
   ```
   https://your-app.vercel.app
   ```
   *(Replace with your actual Vercel URL from step 2.4)*
7. Railway will automatically redeploy

### 3.2 Verify Connection

1. Visit your Vercel frontend URL
2. You should see the login page
3. Try registering a new admin user or logging in

---

## Step 4: Create Admin User

Since there's no default admin, you need to create one:

### Option 1: Via API (using curl)

```bash
curl -X POST https://your-backend.railway.app/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "your-secure-password"
  }'
```

### Option 2: Via Frontend

1. Visit your Vercel frontend URL
2. If there's a registration option, use it
3. Otherwise, use the API method above

---

## Verification Checklist

- [ ] Backend health check returns `{"status":"UP"}`
- [ ] Frontend loads without errors
- [ ] Can register/login as admin
- [ ] No CORS errors in browser console
- [ ] API calls work from frontend

---

## Troubleshooting

### Backend won't start
- Check Railway logs: Service → "Deployments" → Click latest deployment → "View Logs"
- Verify all environment variables are set correctly
- Check database connection string format

### CORS errors
- Verify `CORS_ALLOWED_ORIGINS` includes your Vercel URL (no trailing slash)
- Make sure backend has been redeployed after updating CORS

### Frontend can't connect
- Verify `NEXT_PUBLIC_API_URL` is set correctly in Vercel
- Check that URL uses HTTPS
- Ensure URL ends with `/api/v1`

### Database connection errors
- Verify JDBC URL format: `jdbc:postgresql://host:port/database`
- Check that database variables are correct
- Ensure database is running in Railway

---

## URLs Summary

After deployment, you'll have:
- **Backend**: `https://your-backend.railway.app`
- **Frontend**: `https://your-app.vercel.app`
- **API Base**: `https://your-backend.railway.app/api/v1`

---

## Next Steps

- [ ] Set up custom domain (optional)
- [ ] Configure monitoring
- [ ] Set up database backups
- [ ] Review security settings

