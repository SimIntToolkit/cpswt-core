/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 */

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
