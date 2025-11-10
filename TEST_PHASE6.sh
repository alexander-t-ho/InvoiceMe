#!/bin/bash

echo "🧪 Testing Phase 6: Frontend Foundation"
echo "========================================"
echo ""

# Check if files exist
echo "📁 Checking file structure..."
FILES=(
  "frontend/lib/api/client.ts"
  "frontend/lib/api/customers.ts"
  "frontend/lib/api/invoices.ts"
  "frontend/lib/api/payments.ts"
  "frontend/lib/services/CustomerService.ts"
  "frontend/lib/services/InvoiceService.ts"
  "frontend/lib/services/PaymentService.ts"
  "frontend/types/api.ts"
  "frontend/app/providers.tsx"
  "frontend/components/layout/navbar.tsx"
  "frontend/components/layout/main-layout.tsx"
)

MISSING=0
for file in "${FILES[@]}"; do
  if [ -f "$file" ]; then
    echo "  ✅ $file"
  else
    echo "  ❌ $file (missing)"
    MISSING=$((MISSING + 1))
  fi
done

echo ""
if [ $MISSING -eq 0 ]; then
  echo "✅ All required files exist"
else
  echo "❌ $MISSING file(s) missing"
fi

echo ""
echo "📦 Checking dependencies..."
cd frontend
if npm list @tanstack/react-query axios react-hook-form zod > /dev/null 2>&1; then
  echo "  ✅ Core dependencies installed"
else
  echo "  ❌ Some dependencies missing"
fi

if npm list @radix-ui/react-slot lucide-react > /dev/null 2>&1; then
  echo "  ✅ UI dependencies installed"
else
  echo "  ❌ Some UI dependencies missing"
fi

echo ""
echo "🎨 Checking UI components..."
if [ -d "components/ui" ] && [ "$(ls -A components/ui/*.tsx 2>/dev/null | wc -l)" -gt 5 ]; then
  echo "  ✅ UI components installed ($(ls components/ui/*.tsx 2>/dev/null | wc -l) components)"
else
  echo "  ❌ UI components missing or incomplete"
fi

echo ""
echo "✅ Phase 6 setup verification complete!"
