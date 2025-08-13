#!/usr/bin/env bash

set -eo pipefail

# Source environment variables from .env file
if [ -f "../.env" ]; then
    source ../.env
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

VERSION=${VERSION:-v1}
CLUSTER_NAME=${CLUSTER_NAME:-otel}

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
echo "Cluster name: $CLUSTER_NAME"
echo "Version: $VERSION"

# Create kind cluster
echo "Creating kind cluster: $CLUSTER_NAME"
kind create cluster --name=$CLUSTER_NAME --config ./kind/multi-node.yaml
kubectl create namespace opentelemetry

# Deploy cert-manager (required by OpenTelemetry operator)
echo "Deploying cert-manager..."
helm repo add jetstack https://charts.jetstack.io --force-update
helm upgrade --install cert-manager jetstack/cert-manager --namespace cert-manager --create-namespace --set crds.enabled=true

# Deploy Prometheus operator CRDs (required by OpenTelemetry operator)
echo "Deploying Prometheus operator CRDs..."
kubectl apply --server-side -f https://raw.githubusercontent.com/prometheus-operator/prometheus-operator/v0.80.1/example/prometheus-operator-crd/monitoring.coreos.com_servicemonitors.yaml
kubectl apply --server-side -f https://raw.githubusercontent.com/prometheus-operator/prometheus-operator/v0.80.1/example/prometheus-operator-crd/monitoring.coreos.com_podmonitors.yaml

# Deploy OpenTelemetry operator
echo "Deploying OpenTelemetry operator..."
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
helm upgrade --install opentelemetry-operator open-telemetry/opentelemetry-operator --set manager.extraArgs="{--enable-go-instrumentation}" --set "manager.collectorImage.repository=otel/opentelemetry-collector-k8s" --namespace opentelemetry --create-namespace

# Deploy Perses operator
echo "Deploying Perses operator..."
kustomize build 'github.com/perses/perses-operator/config/default?ref=a324bdf0142c98271cfa5a17e91ae4eaf461bbe8' | kubectl apply -f -
kubectl apply -f ./perses/perses.yaml

# Deploy infrastructure
echo "Deploying MySQL..."
helm install my-mysql bitnami/mysql \
    --set auth.rootPassword=mysecretPassword \
    --set auth.database=todo \
    --set auth.username=todo \
    --set auth.password=mysecretPassword

echo "Deploying PostgreSQL..."
helm install pg oci://registry-1.docker.io/bitnamicharts/postgresql \
    --set global.postgresql.auth.postgresPassword=password \
    --set global.postgresql.auth.database=todo

echo "Deploying Prometheus..."
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm upgrade --install prometheus prometheus-community/prometheus --values ./prometheus/values.yaml

echo "Deploying Jaeger..."
helm repo add jaegertracing https://jaegertracing.github.io/helm-charts
helm upgrade --install jaeger jaegertracing/jaeger --values ./jaeger/values.yaml

echo "Deploying OpenSearch..."
helm repo add opensearch https://opensearch-project.github.io/helm-charts
helm upgrade --install opensearch opensearch/opensearch -f ./opensearch/values.yaml
helm upgrade --install opensearch-dashboards opensearch/opensearch-dashboards -f ./opensearch/dashboard-values.yaml

# Create Dash0 secrets for OpenTelemetry collector
echo "Creating Dash0 secrets..."
kubectl create secret generic dash0-secrets \
    --from-literal=dash0-authorization-token="$DASH0_AUTH_TOKEN" \
    --from-literal=dash0-grpc-hostname="$DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME" \
    --from-literal=dash0-grpc-port="$DASH0_ENDPOINT_OTLP_GRPC_PORT" \
    --namespace=opentelemetry

# Deploy OpenTelemetry collectors
echo "Deploying OpenTelemetry collectors..."
helm upgrade --install otel-collector-daemonset open-telemetry/opentelemetry-collector --namespace opentelemetry -f ./collector/daemonset-values.yaml
helm upgrade --install otel-collector-deployment open-telemetry/opentelemetry-collector --namespace opentelemetry -f ./collector/deployment-values.yaml

# Build and load images
echo "Building Docker images..."
docker build -f ./services/todo-go/Dockerfile -t todo-go:$VERSION ./services/todo-go
docker build -f ./services/todo-java/Dockerfile -t todo-java:$VERSION ./services/todo-java
docker build -f ./services/frontend/Dockerfile -t todo-frontend:$VERSION ./services/frontend

echo "Loading images into kind cluster..."
kind load docker-image --name $CLUSTER_NAME todo-go:$VERSION
kind load docker-image --name $CLUSTER_NAME todo-java:$VERSION
kind load docker-image --name $CLUSTER_NAME todo-frontend:$VERSION

# Wait for OpenTelemetry operator to be ready
echo "Waiting for OpenTelemetry operator to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/opentelemetry-operator -n opentelemetry

echo "Waiting for webhook to be ready..."
kubectl wait --for=condition=ready --timeout=300s pod -l app.kubernetes.io/name=opentelemetry-operator -n opentelemetry
sleep 30

# Apply instrumentation
echo "Applying instrumentation..."
kubectl apply -f ./instrumentations/instrumentation.yaml

# Deploy applications
echo "Deploying applications..."
kubectl apply -f ./services/todo-go/manifests/
kubectl apply -f ./services/todo-java/manifests/
kubectl apply -f ./services/frontend/manifests/

echo "Observability in Platform Engineering Course demo deployment complete!"
echo "Cluster name: $CLUSTER_NAME"
echo ""
echo "To delete the cluster, run: kind delete cluster --name=$CLUSTER_NAME"