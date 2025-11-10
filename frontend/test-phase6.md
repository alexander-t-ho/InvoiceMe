# Phase 6 Testing Guide

## Quick Test Checklist

### 1. Verify Frontend Compiles
```bash
cd frontend
npm run build
```

### 2. Verify TypeScript Types
```bash
cd frontend
npx tsc --noEmit --skipLibCheck
```

### 3. Start Backend
```bash
cd backend
export JAVA_HOME=$(brew --prefix openjdk@17)
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew bootRun
```

### 4. Start Frontend (in another terminal)
```bash
cd frontend
npm run dev
```

### 5. Test API Connection
Open browser console at http://localhost:3000 and run:
```javascript
// Test API client
fetch('http://localhost:8080/api/v1/customers?page=0&size=5')
  .then(r => r.json())
  .then(console.log)
  .catch(console.error)
```

### 6. Verify Components
- Navigate to http://localhost:3000
- Check that dashboard loads
- Verify navigation works
- Check that UI components render

## Expected Results

✅ Frontend compiles without errors
✅ TypeScript types are correct
✅ Backend API is accessible
✅ Frontend can connect to backend
✅ Dashboard page renders
✅ Navigation works
✅ UI components display correctly

