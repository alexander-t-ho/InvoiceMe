# Monitoring and Logging Guide

This guide covers monitoring, logging, and alerting for InvoiceMe in production.

## Table of Contents

1. [Health Checks](#health-checks)
2. [Application Monitoring](#application-monitoring)
3. [Logging](#logging)
4. [Uptime Monitoring](#uptime-monitoring)
5. [Error Tracking](#error-tracking)
6. [Performance Monitoring](#performance-monitoring)

---

## Health Checks

### Backend Health Endpoint

The backend exposes a health check endpoint via Spring Boot Actuator:

**Endpoint**: `GET /actuator/health`

**Response**:
```json
{
  "status": "UP"
}
```

**Usage**:
```bash
# Check backend health
curl https://your-backend-url.com/actuator/health
```

### Setting Up Health Check Monitoring

#### Railway
- Health checks are automatically configured
- Railway monitors the `/actuator/health` endpoint
- Service will restart if health check fails

#### Render
- Configure health check path: `/actuator/health`
- Render will monitor this endpoint
- Service will restart if health check fails

#### UptimeRobot (Free)
1. Go to [uptimerobot.com](https://uptimerobot.com)
2. Create account
3. Add new monitor:
   - **Type**: HTTP(s)
   - **URL**: `https://your-backend-url.com/actuator/health`
   - **Interval**: 5 minutes
   - **Alert Contacts**: Add email/SMS

#### Pingdom
1. Go to [pingdom.com](https://pingdom.com)
2. Create account
3. Add new check:
   - **URL**: `https://your-backend-url.com/actuator/health`
   - **Check interval**: 1 minute
   - **Alert threshold**: 2 consecutive failures

---

## Application Monitoring

### Spring Boot Actuator Metrics

The backend exposes various metrics via Actuator:

**Available Endpoints**:
- `/actuator/health` - Health status
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus format metrics
- `/actuator/info` - Application information

**Access Metrics**:
```bash
# List all metrics
curl https://your-backend-url.com/actuator/metrics

# Get specific metric
curl https://your-backend-url.com/actuator/metrics/jvm.memory.used

# Prometheus format
curl https://your-backend-url.com/actuator/prometheus
```

### Key Metrics to Monitor

1. **JVM Metrics**
   - `jvm.memory.used` - Memory usage
   - `jvm.memory.max` - Maximum memory
   - `jvm.gc.pause` - Garbage collection pauses

2. **HTTP Metrics**
   - `http.server.requests` - Request count and duration
   - `http.server.errors` - Error count

3. **Database Metrics**
   - `hikari.connections.active` - Active database connections
   - `hikari.connections.idle` - Idle connections
   - `hikari.connections.pending` - Pending connections

4. **Custom Metrics**
   - Add custom metrics as needed for business logic

---

## Logging

### Backend Logging

**Log Location**: 
- Railway/Render: Available in platform dashboard
- Local: `logs/invoiceme-backend.log`

**Log Levels** (Production):
- `root`: INFO
- `com.invoiceme`: INFO
- `org.springframework.web`: INFO
- `org.hibernate`: WARN

**Log Format**:
```
2025-11-11 14:30:45 [http-nio-8081-exec-1] INFO  com.invoiceme.api.CustomerController - Customer created: abc-123
```

**Accessing Logs**:

#### Railway
1. Go to your service dashboard
2. Click "View Logs"
3. Logs are streamed in real-time
4. Can filter by log level

#### Render
1. Go to your service dashboard
2. Click "Logs" tab
3. Logs are streamed in real-time
4. Can download logs

### Frontend Logging

**Vercel Logs**:
1. Go to your Vercel project
2. Click on a deployment
3. View "Build Logs" and "Runtime Logs"
4. Check "Functions" tab for serverless function logs

**Browser Logs**:
- Check browser DevTools > Console
- Monitor for JavaScript errors
- Check Network tab for failed requests

### Log Aggregation (Optional)

For centralized logging, consider:

1. **Logtail** (Free tier available)
   - Easy integration
   - Real-time log streaming
   - Search and filtering

2. **Papertrail**
   - Simple log aggregation
   - Search and alerting
   - Free tier available

3. **Datadog** (Paid)
   - Full observability platform
   - Logs, metrics, traces
   - Advanced features

---

## Uptime Monitoring

### Recommended Setup

1. **Primary Monitor**: UptimeRobot (Free)
   - Monitor backend health endpoint
   - Monitor frontend URL
   - Email/SMS alerts

2. **Secondary Monitor**: Pingdom (Optional)
   - More frequent checks
   - Advanced alerting
   - Performance monitoring

### Monitoring Checklist

- [ ] Backend health endpoint monitored
- [ ] Frontend URL monitored
- [ ] Alert contacts configured
- [ ] Alert thresholds set appropriately
- [ ] Test alerts to verify they work

---

## Error Tracking

### Sentry Integration (Optional)

Sentry provides error tracking and monitoring:

1. **Create Sentry Account**
   - Go to [sentry.io](https://sentry.io)
   - Create free account
   - Create new project

2. **Backend Integration**
   ```gradle
   // Add to build.gradle.kts
   implementation("io.sentry:sentry-spring-boot-starter:6.34.0")
   ```
   
   ```yaml
   # application-prod.yml
   sentry:
     dsn: ${SENTRY_DSN}
     environment: production
   ```

3. **Frontend Integration**
   ```bash
   npm install @sentry/nextjs
   ```
   
   ```bash
   npx @sentry/wizard@latest -i nextjs
   ```

### Error Monitoring Best Practices

1. **Set Up Alerts**
   - Alert on error rate spikes
   - Alert on critical errors
   - Configure alert thresholds

2. **Error Grouping**
   - Group similar errors
   - Ignore known non-critical errors
   - Focus on new/unresolved errors

3. **Context**
   - Add user context to errors
   - Include request information
   - Add custom tags

---

## Performance Monitoring

### Key Performance Indicators (KPIs)

1. **Response Times**
   - API endpoint response times
   - Database query times
   - Page load times

2. **Throughput**
   - Requests per second
   - Database queries per second
   - Concurrent users

3. **Error Rates**
   - 4xx error rate
   - 5xx error rate
   - Database error rate

4. **Resource Usage**
   - CPU usage
   - Memory usage
   - Database connections

### Monitoring Tools

#### Platform Built-in
- **Railway**: Metrics dashboard
- **Render**: Metrics dashboard
- **Vercel**: Analytics dashboard

#### External Tools
- **Google Analytics**: Frontend analytics
- **New Relic**: Full-stack monitoring (paid)
- **Datadog**: Comprehensive monitoring (paid)

---

## Alerting

### Recommended Alerts

1. **Critical Alerts** (Immediate)
   - Service down
   - Database connection failed
   - High error rate (>5%)
   - Memory usage >90%

2. **Warning Alerts** (Within 1 hour)
   - Slow response times (>2s)
   - High CPU usage (>80%)
   - Database connection pool >80%
   - Disk space >80%

3. **Info Alerts** (Daily summary)
   - Daily error count
   - Daily request count
   - Performance summary

### Setting Up Alerts

#### UptimeRobot
- Configure alert contacts
- Set alert thresholds
- Choose notification channels

#### Platform Alerts
- **Railway**: Configure in service settings
- **Render**: Configure in service settings
- **Vercel**: Configure in project settings

---

## Dashboard Setup

### Recommended Dashboards

1. **Health Dashboard**
   - Service status
   - Health check results
   - Uptime percentage

2. **Performance Dashboard**
   - Response times
   - Request rates
   - Error rates

3. **Resource Dashboard**
   - CPU usage
   - Memory usage
   - Database connections

### Creating Dashboards

#### Grafana (Optional)
1. Set up Grafana instance
2. Configure Prometheus data source
3. Create dashboards using Actuator metrics
4. Set up alerts

#### Platform Dashboards
- Use built-in dashboards in Railway/Render/Vercel
- Customize as needed
- Export metrics if needed

---

## Best Practices

1. **Monitor Proactively**
   - Set up monitoring before issues occur
   - Review metrics regularly
   - Adjust thresholds based on actual usage

2. **Alert Appropriately**
   - Don't alert on noise
   - Set meaningful thresholds
   - Use different channels for different severity

3. **Log Strategically**
   - Log important events
   - Use appropriate log levels
   - Don't log sensitive information

4. **Review Regularly**
   - Weekly metric review
   - Monthly performance analysis
   - Quarterly capacity planning

---

## Quick Reference

### Health Check
```bash
curl https://your-backend-url.com/actuator/health
```

### Metrics
```bash
curl https://your-backend-url.com/actuator/metrics
curl https://your-backend-url.com/actuator/prometheus
```

### Logs
- Railway: Service dashboard > View Logs
- Render: Service dashboard > Logs tab
- Vercel: Deployment > Logs

---

**Last Updated**: 2025-11-11  
**Version**: 1.0

