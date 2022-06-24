package org.cpswt.hla;

import hla.rti.*;
import static org.mockito.Mockito.*;
import static org.cpswt.hla.InteractionRootInterface.ClassAndPropertyName;

import org.mockito.invocation.InvocationOnMock;
import org.portico.impl.hla13.types.DoubleTime;

import java.util.*;

public class RTIAmbassadorProxy1 {


    static class ReflectedAttributeImpl implements ReflectedAttributes {

        private final SuppliedAttributes _suppliedAttributes;

        public ReflectedAttributeImpl(SuppliedAttributes suppliedAttributes) {
            _suppliedAttributes = suppliedAttributes;
        }

        public int getAttributeHandle(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getHandle(offset);
        }

        public int getOrderType(int offset) throws ArrayIndexOutOfBounds {
            return 0; // DUMMY VALUE -- NOT USED
        }

        public Region getRegion(int offset) throws ArrayIndexOutOfBounds {
            return null; // DUMMY VALUE -- NOT USED
        }

        public int getTransportType(int offset) throws ArrayIndexOutOfBounds {
            return 0; // DUMMY VALUE -=- NOT USED
        }

        public byte[] getValue(int offset) throws ArrayIndexOutOfBounds {
            return  _suppliedAttributes.getValue(offset);
        }

        public int getValueLength(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getValueLength(offset);
        }

        public byte[] getValueReference(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getValueReference(offset);
        }

        public int size() {
            return _suppliedAttributes.size();
        }
    }

    static class EventRetractionHandleImpl implements EventRetractionHandle { }



    private final RTIambassador rtiambassador;
    public RTIambassador getRTIAmbassador(){
        return rtiambassador;
    }


    private static final HashMap<String, Integer> interactionClassNameHandleMap;
    static{
        interactionClassNameHandleMap = new HashMap<>();
        int value = 0;
        interactionClassNameHandleMap.put("InteractionRoot", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimLog", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimLog.HighPrio", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimulationControl", value++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.SimulationControl.SimEnd", value);
    }
    public HashMap<String, Integer> getInteractionClassNameHandleMap(){
        return interactionClassNameHandleMap;
    }


    private static final HashMap<ObjectRootInterface.ClassAndPropertyName, Integer> objectClassAndPropertyNameHandleMap;
    private static final HashMap<ClassAndPropertyName, Integer> interactionClassAndPropertyNameHandleMap;
    public HashMap<ObjectRootInterface.ClassAndPropertyName, Integer> getObjectClassAndPropertyNameHandleMap() {
        return objectClassAndPropertyNameHandleMap;
    }
    public HashMap<ClassAndPropertyName, Integer> getInteractionClassAndPropertyNameHandleMap() {
        return interactionClassAndPropertyNameHandleMap;
    }
    static{
        interactionClassAndPropertyNameHandleMap = new HashMap<>();
        objectClassAndPropertyNameHandleMap = new HashMap<>();
        int value = 0;
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "actualLogicalGenerationTime"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "federateFilter"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "federateSequence"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "Comment"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "FedName"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "Time"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "FederateId"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "FederateType"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "IsLateJoiner"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "FederateId"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "FederateType"),
                value++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateResignInteraction", "IsLateJoiner"),
                value++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateHandle"), value++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateHost"), value++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.FederateObject", "FederateType"), value
        );
    }


    private static final HashMap<String, Integer> objectClassNameHandleMap;
    public HashMap<String, Integer> getObjectClassNameHandleMap(){
        return objectClassNameHandleMap;
    }
    static{
        objectClassNameHandleMap = new HashMap<>();
        int value = 0;
        objectClassNameHandleMap.put("ObjectRoot", value++);
        objectClassNameHandleMap.put("ObjectRoot.FederateObject", value);
    }


    private int currentClassHandle = 0;
    public void setCurrentClassHandle(int classHandle) {
        currentClassHandle = classHandle;
    }
    public int getCurrentClassHandle() {
        return currentClassHandle;
    }


    private int uniqueObjectHandle = 0;
    private int currentObjectHandle = 0;

    public void setCurrentObjectHandle(int objectHandle) {
        currentObjectHandle = objectHandle;
    }
    public void setCurrentObjectHandle() {
        currentObjectHandle = uniqueObjectHandle++;
    }
    public int getCurrentObjectHandle() {
        return currentObjectHandle;
    }



    private SuppliedAttributes currentSuppliedAttributes = null;

    public void setCurrentSuppliedAttributes(SuppliedAttributes suppliedAttributes) {
        currentSuppliedAttributes = suppliedAttributes;
    }
    public SuppliedAttributes getCurrentSuppliedAttributes() {
        return currentSuppliedAttributes;
    }
    public ReflectedAttributes getCurrentReflectedAttributes() {
        return new ReflectedAttributeImpl(getCurrentSuppliedAttributes());
    }

    private LogicalTime currentLogicalTime = null;
    public void setCurrentLogicalTime(LogicalTime logicalTime) {
        currentLogicalTime = logicalTime;
    }
    public LogicalTime getCurrentLogicalTime() {
        return currentLogicalTime;
    }
    public DoubleTime getCurrentDoubleTime() {
        LogicalTime logicalTime = getCurrentLogicalTime();
        return logicalTime instanceof DoubleTime ? (DoubleTime)logicalTime : null;
    }


    public RTIAmbassadorProxy1(){
        // change to static

        //add the all the whens.
        rtiambassador = mock(RTIambassador.class);
        try {
            when(rtiambassador.getInteractionClassHandle(anyString())).thenAnswer(
                    (InvocationOnMock invocationOnMock) ->
                            interactionClassNameHandleMap.get((String)invocationOnMock.getArgument(0))
            );
            when(rtiambassador.getParameterHandle(anyString(), anyInt())).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        String parameterName = invocationOnMock.getArgument(0);
                        int classHandle = invocationOnMock.getArgument(1);
                        String className = InteractionRoot.get_hla_class_name(classHandle);
                        ClassAndPropertyName key = new ClassAndPropertyName(className, parameterName);
                        return interactionClassAndPropertyNameHandleMap.get(key);
                    }
            );
            when(rtiambassador.getAttributeHandle(anyString(), anyInt())).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        String attributeName = invocationOnMock.getArgument(0);
                        int classHandle = invocationOnMock.getArgument(1);
                        String className = ObjectRoot.get_hla_class_name(classHandle);
                        ObjectRootInterface.ClassAndPropertyName key =
                                new ObjectRootInterface.ClassAndPropertyName(className, attributeName);
                        return objectClassAndPropertyNameHandleMap.get(key);
                    }
            );
            when(rtiambassador.registerObjectInstance(anyInt())).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        int classHandle = invocationOnMock.getArgument(0);
                        setCurrentClassHandle(classHandle);
                        setCurrentObjectHandle();
                        return getCurrentClassHandle();
                    }
            );
            when(rtiambassador.updateAttributeValues(
                    anyInt(), any(SuppliedAttributes.class), nullable(byte[].class), any(LogicalTime.class)
            )).thenAnswer(
                    (InvocationOnMock invocationMock) -> {
                        int objectHandle = invocationMock.getArgument(0);
                        SuppliedAttributes suppliedAttributes = invocationMock.getArgument(1);
                        LogicalTime logicalTime = invocationMock.getArgument(3);

                        setCurrentObjectHandle(objectHandle);
                        setCurrentSuppliedAttributes(suppliedAttributes);
                        setCurrentLogicalTime(logicalTime);

                        return new EventRetractionHandleImpl(); // DUMMY EventRestractionHandle
                    }
            );
        } catch(Exception e) {}

    }
}





