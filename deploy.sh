#!/bin/bash

# InvoiceMe Quick Deployment Helper Script
# This script helps prepare for deployment by generating secrets and validating configuration

set -e

echo "🚀 InvoiceMe Production Deployment Helper"
echo "=========================================="
echo ""

# Generate JWT Secret
echo "📝 Generating JWT Secret..."
JWT_SECRET=$(openssl rand -base64 32)
echo "✅ JWT Secret generated:"
echo "   $JWT_SECRET"
echo ""
echo "⚠️  IMPORTANT: Save this secret securely! You'll need it for backend deployment."
echo ""

# Check if backend builds
echo "🔨 Verifying backend build..."
cd backend
if ./gradlew build -x test > /dev/null 2>&1; then
    echo "✅ Backend builds successfully"
    JAR_FILE=$(find build/libs -name "*-SNAPSHOT.jar" -not -name "*-plain.jar" | head -1)
    if [ -n "$JAR_FILE" ]; then
        echo "   JAR file: $JAR_FILE"
        echo "   Size: $(du -h "$JAR_FILE" | cut -f1)"
    fi
else
    echo "❌ Backend build failed. Please fix errors before deploying."
    exit 1
fi
cd ..

echo ""

# Check if frontend builds
echo "🔨 Verifying frontend build..."
cd frontend
if npm run build > /dev/null 2>&1; then
    echo "✅ Frontend builds successfully"
else
    echo "❌ Frontend build failed. Please fix errors before deploying."
    exit 1
fi
cd ..

echo ""
echo "✅ All checks passed!"
echo ""
echo "📋 Next Steps:"
echo "1. Set up database on Railway or Render"
echo "2. Deploy backend with the JWT secret above"
echo "3. Deploy frontend to Vercel"
echo "4. Configure CORS and API URLs"
echo ""
echo "📚 See docs/QUICK_START_DEPLOYMENT.md for detailed instructions"
echo ""

