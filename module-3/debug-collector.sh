#!/bin/bash

# Run a OpenTelemetry Collector locally with debug exporter at verbosity level detailed.
docker run -p 4317:4317 -p 4318:4318 --rm otel/opentelemetry-collector --config=/etc/otelcol/config.yaml --config="yaml:exporters::debug::verbosity: detailed"