# Platform Request Service

A demo service for platform engineering observability, simulating a platform team receiving and processing feature requests from development teams.

## Quick Start

### 1. Configure Dash0 (Optional)

To send telemetry data to Dash0:

1. Copy the environment template:
   ```bash
   cp .env.template .env
   ```

2. Edit `.env` with your Dash0 credentials:
   ```bash
   # Get these from your Dash0 organization settings
   DASH0_AUTH_TOKEN="your_auth_token_here"
   DASH0_DATASET="default"
   DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME="ingress.eu-west-1.aws.dash0.com"
   DASH0_ENDPOINT_OTLP_GRPC_PORT="4317"
   ```

### 2. Start the Stack

```bash
docker compose up --build
```

This starts:
- **Platform Service** (port 8080) - Main application
- **PostgreSQL** (port 5432) - Database
- **OpenTelemetry Collector** (ports 4317/4318) - Telemetry aggregation
- **Prometheus** (port 9090) - Metrics storage
- **Perses** (port 3000) - Modern dashboards and visualization
- **Jaeger** (port 16686) - Distributed tracing
- **OpenSearch** (port 9200) - Log storage
- **OpenSearch Dashboards** (port 5601) - Log visualization

**With Dash0 configured**, telemetry data is automatically sent to both local tools and Dash0.

## API Endpoints

### Create Platform Request
```bash
POST /requests
```

**Request Body** (all fields optional):
```json
{
  "type": "dashboard | new_environment | custom_pipeline | debug_help",
  "urgency": "low | medium | high", 
  "team": "payments | recommendations | search | devops",
  "title": "Short description",
  "description": "Detailed description"
}
```

**Query Parameters**:
- `latency_ms` - Simulate specific latency (for testing)
- `error=true` - Simulate error response (for testing)

**Response**:
```json
{
  "id": "rq-12345",
  "team": "payments",
  "type": "new_environment", 
  "urgency": "high",
  "platform_response": "delivered | rejected | received | needs_info",
  "time_to_response_ms": 1875,
  "comment": "Funny platform team response"
}
```

### Health Checks
- `GET /healthz` - Health check
- `GET /readyz` - Readiness check

## Example Usage

```bash
# Basic request
curl -X POST localhost:8080/requests \
  -H 'Content-Type: application/json' \
  -d '{"team":"payments","type":"new_environment","urgency":"high"}'

# Test slow response
curl -X POST 'localhost:8080/requests?latency_ms=5000' \
  -H 'Content-Type: application/json' \
  -d '{}'

# Test error condition  
curl -X POST 'localhost:8080/requests?error=true' \
  -H 'Content-Type: application/json' \
  -d '{}'
```

## Observability

### Metrics
- `platform_requests_total` - Counter of total requests by team, type, urgency, response
- `time_to_initial_response_seconds` - Histogram of response times

### Traces
- Span: `platform.request.create` with request attributes

### Logs
- Structured JSON logs with trace correlation
- Success: `request_processed` with details
- Error: `request_failed` with context
