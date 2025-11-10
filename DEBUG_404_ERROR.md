# Debugging 404 Error

## Quick Checks

### 1. Check Browser Console
Open DevTools (F12) → Console tab and look for:
- Which specific URL is returning 404
- Is it an API call or a static resource?

### 2. Check Network Tab
Open DevTools (F12) → Network tab:
- Look for red (failed) requests
- Click on the failed request to see:
  - Request URL
  - Response status (404)
  - Response body

### 3. Common 404 Sources

#### API Endpoints
If it's an API call, check:
- Is the backend running? `curl http://localhost:8081/actuator/health`
- Is the endpoint correct? Check the Network tab for the exact URL
- Is CORS configured? Check browser console for CORS errors

#### Static Resources
If it's a static resource (CSS, JS, fonts):
- This is usually a Next.js build issue
- Try restarting the frontend: `cd frontend && npm run dev`
- Clear browser cache (Ctrl+Shift+R or Cmd+Shift+R)

---

## Test API Endpoints

Run these commands to verify API is working:

```bash
# Test customers endpoint
curl http://localhost:8081/api/v1/customers

# Test invoices endpoint
curl http://localhost:8081/api/v1/invoices

# Test health endpoint
curl http://localhost:8081/actuator/health
```

All should return JSON (not 404).

---

## Common Issues & Solutions

### Issue: API calls returning 404
**Solution**: 
- Verify backend is running on port 8081
- Check API base URL in `frontend/lib/api/client.ts`
- Verify CORS is configured in `SecurityConfig.java`

### Issue: Static resources 404
**Solution**:
- Restart frontend: `cd frontend && npm run dev`
- Clear browser cache
- Check if Next.js build completed successfully

### Issue: Page routes 404
**Solution**:
- Verify page files exist in `frontend/app/`
- Check Next.js console for route errors
- Restart frontend server

---

## Get More Details

Please provide:
1. **What URL is failing?** (from Network tab)
2. **Is it an API call or static resource?**
3. **What page were you on when it happened?**
4. **Any console errors?** (screenshot or copy/paste)

This will help identify the exact issue!

