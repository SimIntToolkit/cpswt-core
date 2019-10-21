package org.cpswt.fedtracer;


import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.*;
import io.opentracing.propagation.Format;

import io.opentracing.Tracer;

public class FederateTracer {
    private RequestFile tracerContext;
    protected Tracer tracer;
    private Boolean isRoot;

    public FederateTracer(String FederateName, String  federationTraceID, Boolean root){
        this.tracer = this.init(FederateName);
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
            SpanContext parentSpanCtx = this.tracer.extract(Format.Builtin.TEXT_MAP, this.tracerContext);

//            SpanContext parentSpanCtx = this.tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(headers));

            if (parentSpanCtx == null) {
                spanBuilder = this.tracer.buildSpan(operationName);
            } else {
                if (isRoot){
                    spanBuilder = this.tracer.buildSpan(operationName).addReference(References.FOLLOWS_FROM,parentSpanCtx);

                }
                else
                    spanBuilder = this.tracer.buildSpan(operationName).asChildOf(parentSpanCtx);
            }
        } catch (IllegalArgumentException e) {
            spanBuilder = this.tracer.buildSpan(operationName);
        }
//        return tracer.scopeManager().activate(spanBuilder.start());
//        return spanBuilder.start();
        return spanBuilder.startActive(true);

    }

//    public Scope startChildSpan(Span rootSpan, String operationName){
//        Tracer.SpanBuilder spanBuilder;
//        spanBuilder = this.tracer.buildSpan(operationName).asChildOf(rootSpan);
//        return spanBuilder.startActive(true);
//    }

    public static FederateTracer initFedTracer(String federateName, String tracerID, Boolean isFedMgr){
        System.out.println("entered the init fedtracer");

        if (isFedMgr == Boolean.TRUE){
            return new FederateTracer(federateName,tracerID,true);

        }
        else {
            return new FederateTracer(federateName, tracerID, false);
        }

    }

//    public Scope activateSpan(Span span){
//        return this.tracer.
////        return this.tracer.scopeManager().activate(span);
//    }

}
