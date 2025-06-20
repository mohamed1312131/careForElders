# Step 1: Build the Angular app
FROM node:18-alpine AS builder
WORKDIR /app
COPY . .
RUN npm install && npm run build

# Step 2: Serve it using Nginx
FROM nginx:alpine
COPY --from=builder /app/dist/modernize /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
