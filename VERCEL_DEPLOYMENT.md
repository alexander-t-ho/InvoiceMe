# Vercel Frontend Deployment - Status

## ✅ Frontend Deployed!

**Frontend URL:** https://frontend-94efmyh3e-alexander-hos-projects.vercel.app

## 📋 Next Steps

### 1. Get Your Railway Backend URL

1. Go to Railway Dashboard:
   https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff

2. Click on your **backend** service

3. Go to **Settings** → **Networking**

4. **Copy the backend URL** (e.g., `https://your-backend.railway.app`)

### 2. Set Environment Variable in Vercel

**Option A: Via Vercel Dashboard (Recommended)**
1. Go to: https://vercel.com/alexander-hos-projects/frontend/settings/environment-variables
2. Click **"Add New"**
3. Add:
   - **Key**: `NEXT_PUBLIC_API_URL`
   - **Value**: `https://your-backend.railway.app/api/v1` (replace with your actual backend URL)
   - **Environment**: Select **Production**, **Preview**, and **Development**
4. Click **"Save"**
5. **Redeploy** the frontend (go to Deployments → Latest → Redeploy)

**Option B: Via CLI**
```bash
cd frontend
vercel env add NEXT_PUBLIC_API_URL production
# Enter: https://your-backend.railway.app/api/v1
# Repeat for preview and development if needed
vercel --prod  # Redeploy
```

### 3. Update CORS in Railway

1. Go back to Railway Dashboard
2. Click on your **backend** service
3. Go to **Variables** tab
4. Update `CORS_ALLOWED_ORIGINS`:
   ```
   https://frontend-94efmyh3e-alexander-hos-projects.vercel.app
   ```
   Or if you have a custom domain:
   ```
   https://frontend-94efmyh3e-alexander-hos-projects.vercel.app,https://your-custom-domain.com
   ```
5. Railway will auto-redeploy

### 4. Verify Deployment

1. **Frontend**: Visit https://frontend-94efmyh3e-alexander-hos-projects.vercel.app
2. **Backend Health**: `curl https://your-backend.railway.app/actuator/health`
3. **Test Login**: Try logging in on the frontend

## 🔗 Quick Links

- **Vercel Dashboard**: https://vercel.com/alexander-hos-projects/frontend
- **Railway Dashboard**: https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff
- **Frontend URL**: https://frontend-94efmyh3e-alexander-hos-projects.vercel.app

## ⚠️ Important

After setting `NEXT_PUBLIC_API_URL`, you **must redeploy** the frontend for the change to take effect!

