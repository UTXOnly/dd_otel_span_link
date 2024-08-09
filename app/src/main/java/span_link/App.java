package span_link;

import datadog.trace.api.DDTags;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final io.opentelemetry.api.trace.Tracer otelTracer;

    static {
        // Set up OpenTelemetry SDK with OTLP exporter and enhanced logging
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317/v1/traces") // Ensure this matches your Datadog Agent's OTLP endpoint
                .setTimeout(30, TimeUnit.SECONDS)  // Increased timeout for debugging
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter)
                        .build())
                .setResource(Resource.getDefault().toBuilder()
                        .put("service.name", "otel-service")  // Set service name for OpenTelemetry
                        .build())
                .build();

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();

        otelTracer = openTelemetrySdk.getTracer("example-tracer");

        // Enable debugging for OpenTelemetry SDK
        System.setProperty("otel.javaagent.debug", "true");
    }

    public static void main(String[] args) {

        // Start a Datadog trace and keep it active throughout the main method
        io.opentracing.Span datadogSpan = startDatadogTrace();

        // Retrieve and log the trace and span IDs from Datadog
        String datadogTraceId = retrieveAndLogDatadogTraceInfo();

        // Link the Datadog trace with an OpenTelemetry span using the same trace ID
        linkSpanToDatadog(datadogSpan);

        // Create a simple OTEL span for debugging
        createSimpleOtelSpan();

        // Close Datadog span
        datadogSpan.finish();
    }

    private static io.opentracing.Span startDatadogTrace() {
        // Start a Datadog trace and span
        io.opentracing.Tracer tracer = GlobalTracer.get();
        io.opentracing.Span datadogSpan = tracer.buildSpan("datadog-span")
                .withTag(DDTags.SERVICE_NAME, "DD_PARENT_SERVICE")
                .withTag(DDTags.RESOURCE_NAME, "DD_TEST_RESOURCE")
                .start();

        // Activate the span and keep it active for the duration of the main method
        io.opentracing.Scope datadogScope = tracer.activateSpan(datadogSpan);
        logger.info("Datadog trace created and activated.");
        return datadogSpan;
    }

    private static String retrieveAndLogDatadogTraceInfo() {
        // Access the current active span and retrieve the context
        io.opentracing.Tracer tracer = GlobalTracer.get();
        io.opentracing.Span activeSpan = tracer.activeSpan();

        if (activeSpan != null) {
            io.opentracing.SpanContext context = activeSpan.context();

            // Extract and log the trace and span IDs
            String datadogTraceId = context.toTraceId();
            String datadogSpanId = context.toSpanId();

            logger.info("Datadog Trace ID: {}", datadogTraceId);
            logger.info("Datadog Span ID: {}", datadogSpanId);

            // Return the Datadog trace ID for further use
            return datadogTraceId;
        } else {
            logger.warn("No active Datadog span found.");
            return null;
        }
    }

    private static void linkSpanToDatadog(io.opentracing.Span datadogSpan) {
        // Get the Datadog SpanContext and create a link in the OpenTelemetry span
        SpanContext otelSpanContext = convertToOtelSpanContext(datadogSpan.context());

        Span currentSpan = otelTracer.spanBuilder("otel-span-with-link")
                .setParent(Context.root().with(Span.wrap(otelSpanContext))) // Use Datadog span context as parent
                .startSpan();

        try (Scope otelScope = currentSpan.makeCurrent()) {  // Correct OpenTelemetry Scope
            // Log the trace and span IDs for OpenTelemetry
            logger.info("OpenTelemetry Trace ID: {}", currentSpan.getSpanContext().getTraceId());
            logger.info("OpenTelemetry Span ID: {}", currentSpan.getSpanContext().getSpanId());
        } finally {
            // End the OpenTelemetry span
            currentSpan.end();
        }
    }

    private static SpanContext convertToOtelSpanContext(io.opentracing.SpanContext datadogSpanContext) {
        // Convert the Datadog SpanContext to OpenTelemetry SpanContext
        return SpanContext.create(
                datadogSpanContext.toTraceId(),
                datadogSpanContext.toSpanId(),
                TraceFlags.getDefault(),
                TraceState.getDefault()
        );
    }

    private static void createSimpleOtelSpan() {
        // Create a simple OTEL span for debugging
        Span simpleSpan = otelTracer.spanBuilder("simple-otel-span").startSpan();
        logger.info("Created simple OTEL span with Trace ID: {}", simpleSpan.getSpanContext().getTraceId());
        simpleSpan.end();
    }
}
