#!/bin/bash
set -e

echo "🚀 Building and deploying Billing App..."

# Build the docker image
echo "🔨 Building Docker image..."
docker compose build

# Run the stack
echo "🔥 Starting services..."
docker compose up -d

echo "✅ Deployment complete!"
echo "📜 Logs (API):"
docker compose logs -f billing-api
