package edu.vanderbilt.vuisis.cpswt.fedtracer;
//Author :Yogesh Barve

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.*;
import io.opentracing.propagation.Format;

import io.opentracing.Tracer;

public class FederateTracer {
    private final RequestFile tracerContext;
    protected Tracer tracer;
    private final Boolean isRoot;

    public FederateTracer(String FederateName, String  federationTraceID, Boolean root){
        this.tracer = init(FederateName);
        tracerContext = new RequestFile(federationTraceID,root);
        isRoot =root;
    }

    public static JaegerTracer init(String service) {
        Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv()
                .withType(ConstSampler.TYPE)
                .withParam(1);

        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv()
                .withLogSpans(true);

        Configuration config = new Configuration(service)
                .withSampler(samplerConfig)
                .withReporter(reporterConfig);

        return config.getTracer();
    }

    public Scope startFederateSpan(String operationName) {

        Tracer.SpanBuilder spanBuilder;
        try {
            SpanContext parentSpanCtx = tracer.extract(Format.Builtin.TEXT_MAP, this.tracerContext);

//            SpanContext parentSpanCtx = this.tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(headers));

            if (parentSpanCtx == null) {
                spanBuilder = tracer.buildSpan(operationName);
            } else {
                if (isRoot){
                    spanBuilder = tracer.buildSpan(operationName).addReference(References.FOLLOWS_FROM,parentSpanCtx);

                }
                else
                    spanBuilder = tracer.buildSpan(operationName).asChildOf(parentSpanCtx);
            }
        } catch (IllegalArgumentException e) {
            spanBuilder = tracer.buildSpan(operationName);
        }
        return tracer.scopeManager().activate(spanBuilder.start());
//        return spanBuilder.start();
//        return spanBuilder.startActive(true);

    }

//    public Scope startChildSpan(Span rootSpan, String operationName){
//        Tracer.SpanBuilder spanBuilder;
//        spanBuilder = this.tracer.buildSpan(operationName).asChildOf(rootSpan);
//        return spanBuilder.startActive(true);
//    }

    public static FederateTracer initFedTracer(String federateName, String tracerID, Boolean isFedMgr){
        System.out.println("entered the init fedtracer");

        return new FederateTracer(federateName,tracerID,isFedMgr == Boolean.TRUE);
    }

//    public Scope activateSpan(Span span){
//        return this.tracer.
////        return this.tracer.scopeManager().activate(span);
//    }

}
