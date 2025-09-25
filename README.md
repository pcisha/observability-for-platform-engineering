![This tutorial is courtesy of Dash0](./images/dash0-logo.png)

# Observability for Platform Engineering

This repository contains the hands-on demos and exercises focused on equipping platform engineers with foundational knowledge and practical skills to set up, manage, and maintain observability tools and practices using OpenTelemetry and open standards.

## Prerequisites

Before running any demos, configure your Dash0 credentials:

1. Copy the environment template:
   ```bash
   cp .env.template .env
   ```

2. Edit `.env` with your Dash0 credentials:
   ```bash
   # Get these values from your Dash0 organization settings
   DASH0_AUTH_TOKEN="your_actual_auth_token"
   DASH0_DATASET="default"
   ```

3. Update endpoints if needed (defaults are for EU region):
   ```bash
   DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME="ingress.eu-west-1.aws.dash0.com"
   DASH0_ENDPOINT_OTLP_GRPC_PORT="4317"
   ```

# Demos

## [Module 3 - Platform Observability with Perses and Dash0](module-3/)

Complete observability stack with modern visualization tools. Features OpenTelemetry collector integration with Dash0 cloud platform, Perses dashboards for platform infrastructure metrics, and distributed tracing with Jaeger. Includes a Spring Boot platform request service with custom metrics, structured logging, and database integration.

## [Module 5 - Kubernetes Observability Stack with OpenTelemetry](module-5/)

Production-ready Kubernetes observability with OpenTelemetry auto-instrumentation. Features OpenTelemetry Operator for automatic instrumentation of Go and Java applications, complete telemetry pipeline with Prometheus, Jaeger, and OpenSearch, and Perses dashboard-as-code capabilities. Includes multi-service demo applications with PostgreSQL and MySQL backends.

