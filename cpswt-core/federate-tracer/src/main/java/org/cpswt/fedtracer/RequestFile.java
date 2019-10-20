package org.cpswt.fedtracer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentracing.propagation.TextMap;

public class RequestFile implements TextMap {

    private final HashMap<String, String> headers ;
    RequestFile(String tracerID,Boolean root){
        this.headers = new HashMap<>();
        String traceIDString = tracerID+":"+tracerID+":0:1";
//        if (root == Boolean.TRUE){
//            traceIDString = tracerID+":"+"0"+":0:1";
//
//        }

        this.headers.put("uber-trace-id",traceIDString);
    }

    RequestFile(){
        this.headers = new HashMap<>();
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        // this is used to extract the values for creating context
        return headers.entrySet().iterator();
    }

    @Override
    public void put(String key, String value) {
        // This is going to create a map and value from injection
        this.headers.put(key,value);
        System.out.println("k= "+key+" v= "+value);

        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this.headers);
            System.out.println(jsonResult);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
//
//    public HashMap<String,String> getContext(){
//        return this.headers;
//    }
}
