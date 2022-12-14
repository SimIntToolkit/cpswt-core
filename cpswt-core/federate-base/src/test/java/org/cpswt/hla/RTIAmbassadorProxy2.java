package org.cpswt.hla;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.EventRetractionHandle;
import hla.rti.FederateAmbassador;
import hla.rti.LogicalTime;
import hla.rti.MobileFederateServices;
import hla.rti.RTIambassador;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import hla.rti.Region;
import hla.rti.SuppliedAttributes;
import hla.rti.SuppliedParameters;
import org.mockito.invocation.InvocationOnMock;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.cpswt.hla.InteractionRootInterface.ClassAndPropertyName;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
public class RTIAmbassadorProxy2 {


    static class ReceivedInteractionImpl implements ReceivedInteraction {

        private final SuppliedParameters _suppliedParameters;

        public ReceivedInteractionImpl(SuppliedParameters suppliedParameters) {
            _suppliedParameters = suppliedParameters;
        }
        @Override
        public int getOrderType() {
            return 0;
        }

        @Override
        public int getParameterHandle(int i) throws ArrayIndexOutOfBounds {
            return _suppliedParameters.getHandle(i);
        }

        @Override
        public Region getRegion() {
            return null;
        }

        @Override
        public int getTransportType() {
            return 0;
        }

        @Override
        public byte[] getValue(int i) throws ArrayIndexOutOfBounds {
            return _suppliedParameters.getValue(i);
        }

        @Override
        public int getValueLength(int i) throws ArrayIndexOutOfBounds {
            return _suppliedParameters.getValueLength(i);
        }

        @Override
        public byte[] getValueReference(int i) throws ArrayIndexOutOfBounds {
            return _suppliedParameters.getValueReference(i);
        }

        @Override
        public int size() {
            return _suppliedParameters.size();
        }
    }

    static class ReflectedAttributeImpl implements ReflectedAttributes {

        private final SuppliedAttributes _suppliedAttributes;

        public ReflectedAttributeImpl(SuppliedAttributes suppliedAttributes) {
            _suppliedAttributes = suppliedAttributes;
        }

        @Override
        public int getAttributeHandle(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getHandle(offset);
        }

        @Override
        public int getOrderType(int offset) {
            return 0; // DUMMY VALUE -- NOT USED
        }

        @Override
        public Region getRegion(int offset) {
            return null; // DUMMY VALUE -- NOT USED
        }

        @Override
        public int getTransportType(int offset) {
            return 0; // DUMMY VALUE -=- NOT USED
        }

        @Override
        public byte[] getValue(int offset) throws ArrayIndexOutOfBounds {
            return  _suppliedAttributes.getValue(offset);
        }

        @Override
        public int getValueLength(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getValueLength(offset);
        }

        @Override
        public byte[] getValueReference(int offset) throws ArrayIndexOutOfBounds {
            return _suppliedAttributes.getValueReference(offset);
        }

        @Override
        public int size() {
            return _suppliedAttributes.size();
        }
    }

    static class EventRetractionHandleImpl implements EventRetractionHandle { }



    private final RTIambassador rtiambassador;
    public RTIambassador getRTIAmbassador(){
        return rtiambassador;
    }

    private static final RTIAmbassadorProxy2 rtiAmbassadorProxy1 = new RTIAmbassadorProxy2();
    public static RTIAmbassadorProxy2 get_instance() {
        return rtiAmbassadorProxy1;
    }

    private static int uniqueNo = 0;

    private static final HashMap<String, Integer> interactionClassNameHandleMap = new HashMap<>();
    static{
        interactionClassNameHandleMap.put("InteractionRoot", uniqueNo++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot", uniqueNo++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.EmbeddedMessaging", uniqueNo++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.EmbeddedMessaging.OmnetFederate", uniqueNo++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.EmbeddedMessaging.Receiver", uniqueNo++);
        interactionClassNameHandleMap.put("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", uniqueNo++);
    }
    public HashMap<String, Integer> getInteractionClassNameHandleMap(){
        return interactionClassNameHandleMap;
    }

    private static final HashMap<ClassAndPropertyName, Integer> interactionClassAndPropertyNameHandleMap =
            new HashMap<>();
    static {
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "actualLogicalGenerationTime"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "federateFilter"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot", "federateSequence"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "Comment"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "FedName"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.SimLog", "Time"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.EmbeddedMessaging", "command"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.EmbeddedMessaging", "hlaClassName"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.EmbeddedMessaging", "messagingJson"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "FederateId"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "FederateType"),
                uniqueNo++
        );
        interactionClassAndPropertyNameHandleMap.put(
                new ClassAndPropertyName("InteractionRoot.C2WInteractionRoot.FederateJoinInteraction", "isLateJoiner"),
                uniqueNo++
        );
    }
    public HashMap<ClassAndPropertyName, Integer> getInteractionClassAndPropertyNameHandleMap() {
        return interactionClassAndPropertyNameHandleMap;
    }

    public static class SentInteractionData {
        private final int _interactionClassHandle;
        private final SuppliedParameters _suppliedParameters;
        private final LogicalTime _logicalTime;

        public SentInteractionData(
                int interactionClassHandle, SuppliedParameters suppliedParameters, LogicalTime logicalTime
        ) {
            _interactionClassHandle = interactionClassHandle;
            _suppliedParameters = suppliedParameters;
            _logicalTime = logicalTime;
        }

        public int getInteractionClassHandle() {
            return _interactionClassHandle;
        }

        public SuppliedParameters getSuppliedParameters() {
            return _suppliedParameters;
        }

        public ReceivedInteraction getReceivedInteraction() {
            return _suppliedParameters == null ? null : new ReceivedInteractionImpl(_suppliedParameters);
        }

        public LogicalTime getLogicalTime() {
            return _logicalTime;
        }

        public DoubleTime getDoubleTime() {
            return _logicalTime != null && _logicalTime instanceof DoubleTime ? (DoubleTime)_logicalTime : null;
        }
    }

    private final List<SentInteractionData> _sentInteractionDataList = new ArrayList<>();
    public List<SentInteractionData> getSentInteractionDataList() {
        return _sentInteractionDataList;
    }


    private static final HashMap<String, Integer> objectClassNameHandleMap = new HashMap<>();
    static {
        objectClassNameHandleMap.put("ObjectRoot", uniqueNo++);
        objectClassNameHandleMap.put("ObjectRoot.TestObject", uniqueNo);
    }
    public HashMap<String, Integer> getObjectClassNameHandleMap(){
        return objectClassNameHandleMap;
    }

    private static final
    HashMap<ObjectRootInterface.ClassAndPropertyName, Integer> objectClassAndPropertyNameHandleMap = new HashMap<>();

    static{
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "BooleanValue1"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "BooleanValue2"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "ByteValue"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "CharValue"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "DoubleValue"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "FloatValue"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "IntValue"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "LongValue"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "ShortValue"), uniqueNo++
        );
        objectClassAndPropertyNameHandleMap.put(
                new ObjectRootInterface.ClassAndPropertyName("ObjectRoot.TestObject", "StringValue"), uniqueNo++
        );
    }
    public HashMap<ObjectRootInterface.ClassAndPropertyName, Integer> getObjectClassAndPropertyNameHandleMap() {
        return objectClassAndPropertyNameHandleMap;
    }


    public static class RegisteredObjectData {
        private final int _objectClassHandle;
        private final int _objectHandle;

        public RegisteredObjectData(int objectClassHandle, int objectHandle) {
            _objectClassHandle = objectClassHandle;
            _objectHandle = objectHandle;
        }

        public int getObjectClassHandle() {
            return _objectClassHandle;
        }

        public int getObjectHandle() {
            return _objectHandle;
        }
    }

    private final List<RegisteredObjectData> _registeredObjectDataList = new ArrayList<>();
    public List<RegisteredObjectData> getRegisteredObjectDataList() {
        return _registeredObjectDataList;
    }

    public static class UpdatedObjectData {
        private final int _objectHandle;
        private final SuppliedAttributes _suppliedAttributes;
        private final LogicalTime _logicalTime;

        public UpdatedObjectData(int objectHandle, SuppliedAttributes suppliedAttributes, LogicalTime logicalTime) {
            _objectHandle = objectHandle;
            _suppliedAttributes = suppliedAttributes;
            _logicalTime = logicalTime;
        }

        public int getObjectHandle() {
            return _objectHandle;
        }

        public SuppliedAttributes getSuppliedAttributes() {
            return _suppliedAttributes;
        }

        public ReflectedAttributes getReflectedAttributes() {
            return _suppliedAttributes == null ? null : new ReflectedAttributeImpl(_suppliedAttributes);
        }

        public LogicalTime getLogicalTime() {
            return _logicalTime;
        }
    }

    private final List<UpdatedObjectData> _updatedObjectDataList = new ArrayList<>();
    public List<UpdatedObjectData> getUpdatedObjectDataList() {
        return _updatedObjectDataList;
    }

    public void clear() {
        getSentInteractionDataList().clear();
        getRegisteredObjectDataList().clear();
        getUpdatedObjectDataList().clear();
    }

    Set<FederateAmbassador> federateAmbassadorList = new HashSet<>();
    boolean timeConstrainedRequestOutstanding = false;
    boolean timeRegulationRequestOutstanding = false;

    LogicalTime defaultLogicalTime = new DoubleTime(0);

    public RTIAmbassadorProxy2() {
        // change to static

        //add the all the whens.
        rtiambassador = mock(RTIambassador.class);
        try {
            doAnswer(invocation -> {
                timeConstrainedRequestOutstanding = true;
                return null;
            }).when(rtiambassador).enableTimeConstrained();
            doAnswer(invocation -> {
                timeRegulationRequestOutstanding = true;
                return null;
            }).when(rtiambassador).enableTimeRegulation(any(DoubleTime.class), any(DoubleTimeInterval.class));
            when(rtiambassador.getInteractionClassHandle(anyString())).thenAnswer(
                    (InvocationOnMock invocationOnMock) ->
                            interactionClassNameHandleMap.get((String)invocationOnMock.getArgument(0))
            );
            when(rtiambassador.getObjectClassHandle(anyString())).thenAnswer(
                    (InvocationOnMock invocationOnMock) ->
                            objectClassNameHandleMap.get((String)invocationOnMock.getArgument(0))
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
            when(rtiambassador.joinFederationExecution(
                    anyString(), anyString(), any(FederateAmbassador.class), nullable(MobileFederateServices.class))
            ).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        FederateAmbassador federateAmbassador = invocationOnMock.getArgument(2);
                        federateAmbassadorList.add(federateAmbassador);
                        return 0;
                    }
            );
            when(rtiambassador.registerObjectInstance(anyInt())).thenAnswer(
                    (InvocationOnMock invocationOnMock) -> {
                        int classHandle = invocationOnMock.getArgument(0);
                        RegisteredObjectData registeredObject = new RegisteredObjectData(classHandle, uniqueNo++);
                        _registeredObjectDataList.add(registeredObject);
                        return registeredObject.getObjectHandle();
                    }
            );
            doAnswer(invocation -> {
                int interactionClassHandle = invocation.getArgument(0);
                SuppliedParameters suppliedParameters = invocation.getArgument(1);
                SentInteractionData sentInteraction = new SentInteractionData(interactionClassHandle, suppliedParameters, null);
                _sentInteractionDataList.add(sentInteraction);
                return null;
            }).when(rtiambassador).sendInteraction(anyInt(), any(SuppliedParameters.class), nullable(byte[].class));
            when(rtiambassador.sendInteraction(
                    anyInt(), any(SuppliedParameters.class), nullable(byte[].class), any(LogicalTime.class)
            )).thenAnswer(
                    (InvocationOnMock invocationMock) -> {
                        int interactionClassHandle = invocationMock.getArgument(0);
                        SuppliedParameters suppliedParameters = invocationMock.getArgument(1);
                        LogicalTime logicalTime = invocationMock.getArgument(3);

                        SentInteractionData sentInteractionData = new SentInteractionData(
                                interactionClassHandle, suppliedParameters, logicalTime
                        );
                        _sentInteractionDataList.add(sentInteractionData);

                        return new EventRetractionHandleImpl(); // DUMMY EventRetractionHandle
                    }
            );
            doAnswer(invocation -> {
                if (timeConstrainedRequestOutstanding) {
                    timeConstrainedRequestOutstanding = false;
                    for (FederateAmbassador federateAmbassador : federateAmbassadorList) {
                        federateAmbassador.timeConstrainedEnabled(defaultLogicalTime);
                    }
                }
                if (timeRegulationRequestOutstanding) {
                    timeRegulationRequestOutstanding = false;
                    for (FederateAmbassador federateAmbassador : federateAmbassadorList) {
                        federateAmbassador.timeRegulationEnabled(defaultLogicalTime);
                    }
                }
                return null;
            }).when(rtiambassador).tick();
            when(rtiambassador.updateAttributeValues(
                    anyInt(), any(SuppliedAttributes.class), nullable(byte[].class), any(LogicalTime.class)
            )).thenAnswer(
                    (InvocationOnMock invocationMock) -> {
                        int objectHandle = invocationMock.getArgument(0);
                        SuppliedAttributes suppliedAttributes = invocationMock.getArgument(1);
                        LogicalTime logicalTime = invocationMock.getArgument(3);

                        UpdatedObjectData updatedObject = new UpdatedObjectData(objectHandle, suppliedAttributes, logicalTime);
                        _updatedObjectDataList.add(updatedObject);

                        return new EventRetractionHandleImpl(); // DUMMY EventRetractionHandle
                    }
            );
        } catch(Exception ignored) {}

    }
}





