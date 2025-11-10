# Phase 8: Authentication Testing Guide

## Quick Test

### Automated Backend Tests
```bash
./test-auth.sh
```

This will test:
1. User registration
2. Login with valid credentials
3. Protected endpoint access (without token - should fail)
4. Protected endpoint access (with token - should succeed)
5. Login with invalid credentials (should fail)

---

## Manual Frontend Testing

### Step 1: Access the Application

1. Open browser: **http://localhost:3001**
2. **Expected**: You should be automatically redirected to `/login`

### Step 2: Test Login

1. **If you haven't registered yet**, first register a user:
   ```bash
   curl -X POST http://localhost:8081/api/v1/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "username": "testuser",
       "email": "test@example.com",
       "password": "password123"
     }'
   ```

2. On the login page, enter:
   - **Username**: `testuser`
   - **Password**: `password123`

3. Click "Login"
4. **Expected**: 
   - Success toast notification
   - Redirect to dashboard (`/`)
   - Navigation bar shows username and logout button

### Step 3: Test Protected Pages

1. Navigate to different pages:
   - Dashboard (`/`)
   - Customers (`/customers`)
   - Invoices (`/invoices`)
   - Payments (`/payments`)

2. **Expected**: All pages load successfully

### Step 4: Test Logout

1. Click "Logout" button in navigation bar
2. **Expected**:
   - Redirected to `/login`
   - Token cleared from localStorage
   - Cannot access protected pages

### Step 5: Test Protected Route Redirect

1. While logged out, try to access: `http://localhost:3001/customers`
2. **Expected**: Automatically redirected to `/login`

### Step 6: Test Invalid Credentials

1. On login page, enter:
   - **Username**: `testuser`
   - **Password**: `wrongpassword`

2. Click "Login"
3. **Expected**: Error toast with "Invalid username or password"

### Step 7: Test Token Persistence

1. Login successfully
2. Refresh the page (F5)
3. **Expected**: 
   - Still logged in
   - Token persisted in localStorage
   - No redirect to login

### Step 8: Test API Token Injection

1. Open browser DevTools (F12)
2. Go to Network tab
3. Login and navigate to Customers page
4. Check the API request to `/api/v1/customers`
5. **Expected**: 
   - Request headers include: `Authorization: Bearer <token>`
   - Request succeeds (200 status)

### Step 9: Test 401 Auto-Logout

1. Login successfully
2. Open DevTools → Application → Local Storage
3. Delete the `auth_token` key
4. Try to navigate to a protected page or make an API call
5. **Expected**: 
   - Automatically redirected to `/login`
   - Auth state cleared

---

## Browser Console Checks

Open DevTools (F12) → Console:

- ✅ No authentication-related errors
- ✅ No CORS errors
- ✅ Token stored in localStorage (`auth_token`, `auth_user`)
- ✅ API requests include `Authorization` header

---

## Network Tab Checks

Open DevTools (F12) → Network:

1. **Login Request**:
   - URL: `POST /api/v1/auth/login`
   - Status: 200 OK
   - Response includes `token`, `username`, `email`

2. **Protected API Requests**:
   - Headers include: `Authorization: Bearer <token>`
   - Status: 200 OK (not 401)

3. **Unauthenticated Requests**:
   - Status: 401 Unauthorized
   - Auto-redirect to `/login`

---

## Test Checklist

### Backend API Tests
- [ ] Register new user succeeds
- [ ] Login with valid credentials returns token
- [ ] Login with invalid credentials fails (400/401)
- [ ] Protected endpoint without token returns 401
- [ ] Protected endpoint with valid token succeeds (200)
- [ ] Protected endpoint with invalid token returns 401

### Frontend Tests
- [ ] Login page loads
- [ ] Login with valid credentials redirects to dashboard
- [ ] Login with invalid credentials shows error
- [ ] Protected pages redirect to login when not authenticated
- [ ] Protected pages accessible when authenticated
- [ ] Logout clears session and redirects to login
- [ ] Token persists across page refreshes
- [ ] API calls include auth token
- [ ] 401 errors trigger auto-logout
- [ ] Navigation shows username and logout button

---

## Common Issues & Solutions

### Issue: "Invalid username or password" on valid credentials
**Solution**: 
- Check backend is running: `curl http://localhost:8081/actuator/health`
- Verify user exists: Check database or register again
- Check backend logs for errors

### Issue: 401 errors on all API calls
**Solution**:
- Verify token is in localStorage: `localStorage.getItem('auth_token')`
- Check token format in Network tab
- Verify backend JWT secret matches configuration
- Check CORS settings

### Issue: Redirect loop
**Solution**:
- Clear localStorage: `localStorage.clear()`
- Check ProtectedRoute component logic
- Verify AuthContext is properly initialized

### Issue: Token not persisting
**Solution**:
- Check browser localStorage is enabled
- Verify AuthContext loads token on mount
- Check for localStorage errors in console

---

## Success Criteria

✅ All backend API tests pass
✅ Login/logout flow works
✅ Protected routes redirect correctly
✅ Token injection works
✅ 401 auto-logout works
✅ Token persistence works
✅ No console errors
✅ No CORS errors

---

**Happy Testing! 🎉**

