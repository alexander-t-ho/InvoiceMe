# Port Configuration

## Updated Ports

Due to port conflicts, the following ports are now configured:

- **Backend**: `8081` (was 8080)
- **Frontend**: `3001` (was 3000)

## Access URLs

- **Frontend**: http://localhost:3001
- **Backend API**: http://localhost:8081/api/v1
- **Backend Health**: http://localhost:8081/actuator/health

## Configuration Files Updated

1. **Backend**:
   - `backend/src/main/resources/application.yml` - Server port set to 8081
   - `backend/src/main/java/com/invoiceme/infrastructure/security/SecurityConfig.java` - CORS updated for port 3001

2. **Frontend**:
   - `frontend/package.json` - Dev script updated to use port 3001
   - `frontend/lib/api/client.ts` - API base URL updated to port 8081

## Environment Variables

You can override these using environment variables:

### Frontend
```bash
# Set API URL
export NEXT_PUBLIC_API_URL=http://localhost:8081/api/v1

# Run on different port
npm run dev -- -p 3001
```

### Backend
```bash
# Set server port
export SERVER_PORT=8081

# Or in application.yml
server:
  port: 8081
```

## Starting Servers

### Backend
```bash
cd backend
export JAVA_HOME=$(brew --prefix openjdk@17)
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew bootRun
```

### Frontend
```bash
cd frontend
npm run dev
```

Both will now use the new ports automatically.

