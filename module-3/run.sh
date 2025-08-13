#!/usr/bin/env bash

set -eo pipefail

# Source environment variables from .env file
if [ -f "../.env" ]; then
    source ../.env
    # Export variables so they're available to Docker Compose
    export DASH0_AUTH_TOKEN
    export DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME
    export DASH0_ENDPOINT_OTLP_GRPC_PORT
    export DASH0_DATASET
    export OPENTELEMETRY_COLLECTOR_CONTAINER_IMAGE
    export OPENTELEMETRY_COLLECTOR_LOG_LEVEL
else
    echo "Error: .env file not found in repository root."
    echo ""
    echo "Please create a .env file from the template:"
    echo "  cp .env.template .env"
    echo ""
    echo "Then edit the .env file and configure your Dash0 settings:"
    echo "  - DASH0_AUTH_TOKEN"
    echo "  - DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME" 
    echo "  - DASH0_ENDPOINT_OTLP_GRPC_PORT"
    echo "  - DASH0_DATASET"
    echo ""
    exit 1
fi

echo "Starting Observability in Platform Engineering Course demo..."

# Check required environment variables
if [ -z "$DASH0_AUTH_TOKEN" ]; then
    echo "Error: DASH0_AUTH_TOKEN not set in .env file"
    exit 1
fi

if [ -z "$DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME" ]; then
    echo "Error: DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME not set in .env file"
    exit 1
fi

if [ -z "$DASH0_ENDPOINT_OTLP_GRPC_PORT" ]; then
    echo "Error: DASH0_ENDPOINT_OTLP_GRPC_PORT not set in .env file"
    exit 1
fi

if [ -z "$DASH0_DATASET" ]; then
    echo "Error: DASH0_DATASET not set in .env file"
    exit 1
fi

echo "Environment variables loaded successfully"
echo "Dataset: $DASH0_DATASET"
echo "Endpoint: $DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME:$DASH0_ENDPOINT_OTLP_GRPC_PORT"

# Start the application stack
echo "Starting Docker Compose stack..."
docker-compose up -d

echo "Observability in Platform Engineering Course demo started successfully!"
echo ""
echo "Services available at:"
echo "- Platform Service API: http://localhost:8080"
echo "- Prometheus: http://localhost:9090"
echo "- Perses: http://localhost:3000"
echo "- Jaeger: http://localhost:16686"
echo ""
echo "To stop the demo, run: docker-compose down"
echo "To view logs, run: docker-compose logs -f"