#!/bin/bash

# Script to update Railway CORS and Vercel API URL
# Usage: ./update-urls.sh <railway-backend-url>

set -e

RAILWAY_BACKEND_URL="${1}"
VERCEL_FRONTEND_URL="https://frontend-94efmyh3e-alexander-hos-projects.vercel.app"

if [ -z "$RAILWAY_BACKEND_URL" ]; then
    echo "❌ Error: Railway backend URL is required"
    echo "Usage: ./update-urls.sh <railway-backend-url>"
    echo "Example: ./update-urls.sh https://your-backend.railway.app"
    exit 1
fi

# Remove trailing slash if present
RAILWAY_BACKEND_URL="${RAILWAY_BACKEND_URL%/}"
VERCEL_API_URL="${RAILWAY_BACKEND_URL}/api/v1"

echo "🚀 Updating URLs..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Railway Backend: $RAILWAY_BACKEND_URL"
echo "Vercel Frontend: $VERCEL_FRONTEND_URL"
echo "Vercel API URL:  $VERCEL_API_URL"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Update Vercel NEXT_PUBLIC_API_URL
echo "📝 Updating Vercel NEXT_PUBLIC_API_URL..."
cd frontend

# Remove existing variable for production
echo "Removing existing NEXT_PUBLIC_API_URL for production..."
vercel env rm NEXT_PUBLIC_API_URL production --yes 2>&1 || echo "Variable not found or already removed"

# Add new variable
echo "Adding new NEXT_PUBLIC_API_URL: $VERCEL_API_URL"
echo "$VERCEL_API_URL" | vercel env add NEXT_PUBLIC_API_URL production

# Also add for preview and development
echo "Adding for preview environment..."
echo "$VERCEL_API_URL" | vercel env add NEXT_PUBLIC_API_URL preview

echo "Adding for development environment..."
echo "$VERCEL_API_URL" | vercel env add NEXT_PUBLIC_API_URL development

echo "✅ Vercel environment variables updated!"
echo ""

# Update Railway CORS
echo "📝 Updating Railway CORS_ALLOWED_ORIGINS..."
cd ../backend

# Check if Railway is linked
if ! railway status &>/dev/null; then
    echo "⚠️  Railway CLI not authenticated. Please run:"
    echo "   cd backend"
    echo "   railway login"
    echo "   railway link"
    echo ""
    echo "Then run this command manually:"
    echo "   railway variables set CORS_ALLOWED_ORIGINS='$VERCEL_FRONTEND_URL'"
else
    echo "Setting CORS_ALLOWED_ORIGINS to: $VERCEL_FRONTEND_URL"
    railway variables set "CORS_ALLOWED_ORIGINS=$VERCEL_FRONTEND_URL"
    echo "✅ Railway CORS updated!"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Updates complete!"
echo ""
echo "Next steps:"
echo "1. Redeploy Vercel frontend:"
echo "   cd frontend && vercel --prod"
echo ""
echo "2. Railway will auto-redeploy when CORS is updated"
echo ""
echo "3. Test the deployment:"
echo "   Frontend: $VERCEL_FRONTEND_URL"
echo "   Backend:  $RAILWAY_BACKEND_URL/actuator/health"

