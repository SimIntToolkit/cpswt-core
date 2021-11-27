package org.cpswt.hla;

import org.junit.Test;
import org.junit.Assert;
import static org.mockito.Mockito.*;

import org.cpswt.hla.InteractionRoot_p.C2WInteractionRoot_p.SimulationControl_p.SimEnd;
import org.cpswt.hla.ObjectRoot_p.FederateObject;

import hla.rti.RTIambassador;

public class JsonTest {

    @Test
    public void interactionJsonTest() {

        SimEnd simEnd1 = new SimEnd();
        simEnd1.set_originFed("Federate1");
        simEnd1.set_sourceFed("Federate2");
        simEnd1.set_actualLogicalGenerationTime(5.0);
        simEnd1.set_federateFilter("Filter1");

        String jsonString = simEnd1.toJson();

        SimEnd simEnd2 = (SimEnd)InteractionRoot.fromJson(jsonString);

        Assert.assertEquals(simEnd1.get_originFed(), simEnd2.get_originFed());
        Assert.assertEquals(simEnd1.get_sourceFed(), simEnd2.get_sourceFed());
        Assert.assertEquals(simEnd1.get_actualLogicalGenerationTime(), simEnd2.get_actualLogicalGenerationTime());
        Assert.assertEquals(simEnd1.get_federateFilter(), simEnd2.get_federateFilter());
    }

    @Test
    public void objectJsonTest() {

        int classHandle = 42;
        int objectHandle = 50;

        RTIambassador rtiambassador = mock(RTIambassador.class);
        try {
            when(rtiambassador.registerObjectInstance(anyInt(), anyString())).thenReturn(objectHandle);
            when(rtiambassador.getObjectClassHandle(anyString())).thenReturn(classHandle);
        } catch(Exception e) {}

        FederateObject.publish_object(rtiambassador);

        FederateObject.publish_FederateType();
        FederateObject.publish_FederateHost();
        FederateObject.publish_FederateHandle();

        FederateObject.subscribe_FederateType();
        FederateObject.subscribe_FederateHost();
        FederateObject.subscribe_FederateHandle();

        FederateObject federateObject1 = new FederateObject();

        federateObject1.set_FederateType("FederateType1");
        federateObject1.set_FederateHost("FederateHost1");
        federateObject1.set_FederateHandle(20);

        try {
            federateObject1.registerObject(rtiambassador, "MyObject1");
        } catch (Exception e) {}

        String jsonString = federateObject1.toJson();

        federateObject1.unregisterObject(rtiambassador);

        FederateObject federateObject2 =
                (FederateObject)ObjectRoot.discover(FederateObject.get_class_handle(), objectHandle);

        FederateObject.fromJson(jsonString);

        Assert.assertEquals(federateObject1.get_FederateType(), federateObject2.get_FederateType());
        Assert.assertEquals(federateObject1.get_FederateHost(), federateObject2.get_FederateHost());
        Assert.assertEquals(federateObject1.get_FederateHandle(), federateObject2.get_FederateHandle());
    }
}
