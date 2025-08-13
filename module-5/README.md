# Module 5 - Kubernetes Observability Stack with OpenTelemetry

Complete observability stack on Kubernetes featuring OpenTelemetry auto-instrumentation, Prometheus, Jaeger, OpenSearch, and Perses dashboards.

## Architecture Overview

### Applications
- **frontend**: Frontend that allows for switching backend
- **todo-go**: Go REST API for todo management with PostgreSQL backend
- **todo-java**: Java Spring Boot REST API with MySQL backend

### Observability Stack
- **OpenTelemetry Operator**: Manages auto-instrumentation
- **OpenTelemetry Collector**: Daemonset and deployment modes
- **Prometheus**: Metrics storage
- **Jaeger**: Distributed tracing
- **OpenSearch**: Log aggregation
- **Perses**: Modern dashboard-as-code platform

## Quick Start

### Deploy Complete Stack
```bash
./run.sh
```

This single command:
- Creates Kind cluster
- Deploys complete observability stack
- Builds and deploys applications with auto-instrumentation
- Configures Dash0 integration

### Enable Auto-Instrumentation

Auto-instrumentation is opt-in. Add annotations to enable:

#### For Java Application:
```bash
kubectl patch deployment todo-java -p '{"spec":{"template":{"metadata":{"annotations":{"instrumentation.opentelemetry.io/inject-java":"opentelemetry/instrumentation"}}}}}'
```

#### For Go Application:
```bash
kubectl patch deployment todo-go -p '{"spec":{"template":{"metadata":{"annotations":{"instrumentation.opentelemetry.io/inject-go":"opentelemetry/instrumentation"}}}}}'
```

### Access Services

```bash
# Frontend
kubectl port-forward svc/frontend 3000:80

# Prometheus
kubectl port-forward svc/prometheus 9090:9090

# Jaeger
kubectl port-forward svc/jaeger-query 16686:16686

# OpenSearch Dashboards  
kubectl port-forward svc/opensearch-dashboards 5601:5601

# Perses
kubectl port-forward svc/perses 8080:8080
```

### Deploy Perses Dashboards
```bash
kubectl apply -f ./perses/prometheus-datasource.yaml
kubectl apply -f ./perses/jvm-dashboard.yaml
```

## Cleanup

```bash
kind delete cluster --name=otel
```
