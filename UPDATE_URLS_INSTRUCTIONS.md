# Update Railway and Vercel URLs via CLI

## Quick Update Script

I've created a script to update both URLs. You'll need your Railway backend URL.

### Option 1: Use the Script (Easiest)

```bash
# Get your Railway backend URL first (see below)
# Then run:
./update-urls.sh https://your-backend.railway.app
```

### Option 2: Manual CLI Commands

#### Step 1: Get Railway Backend URL

**Via Railway Dashboard:**
1. Go to: https://railway.app/project/69471b6e-ddd9-450e-919e-33d83cba9bff
2. Click on **backend** service
3. Go to **Settings** → **Networking**
4. Copy the domain URL (e.g., `https://your-backend.railway.app`)

**Or via Railway CLI (if authenticated):**
```bash
cd backend
railway domain
```

#### Step 2: Update Vercel NEXT_PUBLIC_API_URL

```bash
cd frontend

# Remove existing variable
vercel env rm NEXT_PUBLIC_API_URL production --yes

# Add new variable (replace with your Railway backend URL)
echo "https://your-backend.railway.app/api/v1" | vercel env add NEXT_PUBLIC_API_URL production

# Also add for preview and development
echo "https://your-backend.railway.app/api/v1" | vercel env add NEXT_PUBLIC_API_URL preview
echo "https://your-backend.railway.app/api/v1" | vercel env add NEXT_PUBLIC_API_URL development

# Redeploy
vercel --prod
```

#### Step 3: Update Railway CORS_ALLOWED_ORIGINS

**First, authenticate Railway CLI:**
```bash
cd backend
railway login
railway link
```

**Then update CORS:**
```bash
railway variables set "CORS_ALLOWED_ORIGINS=https://frontend-94efmyh3e-alexander-hos-projects.vercel.app"
```

## Current URLs

- **Vercel Frontend**: https://frontend-94efmyh3e-alexander-hos-projects.vercel.app
- **Railway Backend**: (You need to provide this)

## After Updating

1. **Vercel** will need a redeploy (the script does this)
2. **Railway** will auto-redeploy when CORS is updated
3. **Test** both services are connected

