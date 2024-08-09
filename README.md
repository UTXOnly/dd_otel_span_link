## span_link


### Setup

All commands should be run from project root directory.

#### Download Datadog Java tracer


```bash
wget -O dd-java-agent.jar 'https://dtdg.co/latest-java-tracer'
```

#### Build package with Gradle

```bash
./gradlew build
```

#### Run with

```bash
export DD_TRACE_OTEL_ENABLED=true
java -javaagent:./dd-java-agent.jar -jar app/build/libs/app-all.jar
```

#### Validating

If successful, you should see info like below:

```bash
❯ export DD_TRACE_AGENT_PORT=8126 export DD_TRACE_OTEL_ENABLED=truejava -javaagent:./dd-java-agent.jar -jar app/build/libs/app-all.jar
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
12:19:06.658 [main] INFO span_link.App -- Datadog trace created and activated.
12:19:06.660 [main] INFO span_link.App -- Datadog Trace ID: 6295141240485812009
12:19:06.661 [main] INFO span_link.App -- Datadog Span ID: 1046369609715290236
12:19:06.673 [main] INFO span_link.App -- OpenTelemetry Trace ID: 66b6417a00000000575cd59aea119729
12:19:06.673 [main] INFO span_link.App -- OpenTelemetry Span ID: 3f62f5503eb742ba
12:19:06.673 [main] INFO span_link.App -- Created simple OTEL span with Trace ID: 66b6417a00000000575cd59aea119729
[dd.trace 2024-08-09 12:19:06:690 -0400] [dd-telemetry] WARN datadog.telemetry.TelemetryService - Both OpenTracing and OpenTelemetry integrations are enabled but mutually exclusive. Tracing performance can be degraded.
[dd.trace 2024-08-09 12:19:06:691 -0400] [dd-telemetry] WARN datadog.telemetry.TelemetryService - Both OpenTracing and OpenTelemetry integrations are enabled but mutually exclusive. Tracing performance can be degraded.
[dd.trace 2024-08-09 12:19:06:693 -0400] [dd-telemetry] WARN datadog.telemetry.TelemetryRouter - Got FAILURE sending telemetry request to http://localhost:8126/telemetry/proxy/api/v2/apmtelemetry.
[dd.trace 2024-08-09 12:19:06:693 -0400] [dd-telemetry] INFO datadog.telemetry.TelemetryRouter - Agent Telemetry endpoint failed. Telemetry will be sent to Intake.
[dd.trace 2024-08-09 12:19:06:890 -0400] [dd-task-scheduler] INFO datadog.trace.agent.core.StatusLogger - DATADOG TRACER CONFIGURATION {"version":"1.38.0~60ddc9e0d7","os_name":"Mac OS X","os_version":"14.4.1","architecture":"aarch64","lang":"jvm","lang_version":"21.0.2","jvm_vendor":"Microsoft","jvm_version":"21.0.2+13-LTS","java_class_version":"65.0","http_nonProxyHosts":"null","http_proxyHost":"null","enabled":true,"service":"app-all","agent_url":"http://localhost:8126","agent_error":false,"debug":false,"trace_propagation_style_extract":["datadog","tracecontext"],"trace_propagation_style_inject":["datadog","tracecontext"],"analytics_enabled":false,"priority_sampling_enabled":true,"logs_correlation_enabled":true,"profiling_enabled":false,"remote_config_enabled":true,"debugger_enabled":false,"debugger_exception_enabled":false,"appsec_enabled":"ENABLED_INACTIVE","rasp_enabled":false,"telemetry_enabled":true,"telemetry_dependency_collection_enabled":true,"telemetry_log_collection_enabled":false,"dd_version":"","health_checks_enabled":true,"configuration_file":"no config file present","runtime_id":"e03f93bd-42bb-42d6-99cb-4912a0f505b6","logging_settings":{"levelInBrackets":false,"dateTimeFormat":"'[dd.trace 'yyyy-MM-dd HH:mm:ss:SSS Z']'","logFile":"System.err","configurationFile":"simplelogger.properties","showShortLogName":false,"showDateTime":true,"showLogName":true,"showThreadName":true,"defaultLogLevel":"INFO","warnLevelString":"WARN","embedException":false},"cws_enabled":false,"cws_tls_refresh":5000,"datadog_profiler_enabled":false,"datadog_profiler_safe":true,"datadog_profiler_enabled_overridden":false,"data_streams_enabled":false}
[dd.trace 2024-08-09 12:19:06:949 -0400] [dd-telemetry] INFO datadog.telemetry.TelemetryRouter - Agent Telemetry endpoint is now available. Telemetry will be sent to Agent.
```


<img width="1207" alt="otel_span3" src="https://github.com/user-attachments/assets/bef0c0b9-5855-479f-8fad-16e626ffce9d">



<img width="1217" alt="otel_span2" src="https://github.com/user-attachments/assets/f1bb9364-12ae-4ef4-94b3-e3d4ccc4c3f0">

