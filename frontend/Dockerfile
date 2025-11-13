FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:18-alpine
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package*.json ./
RUN npm ci --only=production
# Expose port 3000 (default) but allow override via PORT env var
EXPOSE 3000
# Use PORT environment variable if set, otherwise use 3000
CMD ["sh", "-c", "PORT=${PORT:-3000} npm start"]


