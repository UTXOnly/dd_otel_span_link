package span_link;

import datadog.trace.api.DDTags;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentracing.util.GlobalTracer;

public class App {

    private static final Tracer otelTracer;

    static {
        // Set up OpenTelemetry SDK with OTLP exporter
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317") // Adjust the endpoint as needed
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .build();

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();

        otelTracer = openTelemetrySdk.getTracer("example-tracer");
    }

    public static void main(String[] args) {

        // Start a Datadog trace
        io.opentracing.Span datadogSpan = startDatadogTrace();

        // Link the Datadog span with OpenTelemetry
        linkSpanToDatadog(datadogSpan);

        // Close Datadog span
        datadogSpan.finish();
    }

    private static io.opentracing.Span startDatadogTrace() {
        // Start a Datadog trace and span
        io.opentracing.Span datadogSpan = GlobalTracer.get().buildSpan("datadog-span")
                .withTag(DDTags.SERVICE_NAME, "DD_PARENT_SERVICE")
                .withTag(DDTags.RESOURCE_NAME, "DD_TEST_RESOURCE")
                .start();

        // Simulate some work under the Datadog trace
        System.out.println("Datadog trace created.");

        return datadogSpan;
    }

    private static void linkSpanToDatadog(io.opentracing.Span datadogSpan) {
        // Start a new OpenTelemetry span
        Span currentSpan = otelTracer.spanBuilder("otel-span-with-link")
                .startSpan();

        try (Scope scope = currentSpan.makeCurrent()) {
            // Link the current span with the Datadog span
            SpanContext datadogSpanContext = Span.current().getSpanContext();
            currentSpan.setAttribute("dd.trace_id", datadogSpanContext.getTraceId());
            currentSpan.setAttribute("dd.span_id", datadogSpanContext.getSpanId());

            System.out.println("Datdog Trace ID: " + datadogSpanContext.getTraceId());
            System.out.println("Datadog Span ID: " + datadogSpanContext.getSpanId());

            System.out.println("OpenTelemetry Trace ID: " + currentSpan.getSpanContext().getTraceId());
            System.out.println("OpenTelemetry Span ID: " + currentSpan.getSpanContext().getSpanId());
        } finally {
            // End the OpenTelemetry span
            currentSpan.end();
        }
    }
}
