package org.cpswt.hla;

import hla.rti.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cpswt.utils.CpswtUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * FederateResignInteraction
 */
public class FederateResignInteraction extends C2WInteractionRoot {
    private static final String OBJECT_CLASS_NAME = "InteractionRoot.C2WInteractionRoot.FederateResignInteraction";
    private static final String OBJECT_SIMPLE_CLASS_NAME = "FederateResignInteraction";
    private static final Logger logger = LogManager.getLogger(FederateResignInteraction.class);

    public FederateResignInteraction() {}

    private static int _FederateId_handle;
    private static int _FederateType_handle;
    private static int _IsLateJoiner_handle;

    public static int get_FederateId_handle() {
        return _FederateId_handle;
    }

    public static int get_FederateType_handle() {
        return _FederateType_handle;
    }

    public static int get_IsLateJoiner_handle() {
        return _IsLateJoiner_handle;
    }

    private static boolean _isInitialized = false;
    private static int _handle;

    public static int get_handle() {
        return _handle;
    }

    public static String get_class_name() {
        return OBJECT_CLASS_NAME;
    }

    public static String get_simple_class_name() {
        return OBJECT_SIMPLE_CLASS_NAME;
    }

    private static Set<String> _datamemberNames = new HashSet<String>();
    private static Set<String> _allDatamemberNames = new HashSet<String>();

    public static Set<String> get_parameter_names() {
        return new HashSet<String>(_datamemberNames);
    }

    public static Set<String> get_all_parameter_names() {
        return new HashSet<String>(_allDatamemberNames);
    }

    static {
        _classNameSet.add(OBJECT_CLASS_NAME);
        _classNameClassMap.put(OBJECT_CLASS_NAME, FederateResignInteraction.class);

        _datamemberClassNameSetMap.put(OBJECT_CLASS_NAME, _datamemberNames);
        _allDatamemberClassNameSetMap.put(OBJECT_CLASS_NAME, _allDatamemberNames);

        _datamemberNames.add("FederateId");
        _datamemberNames.add("FederateType");
        _datamemberNames.add("IsLateJoiner");

        _allDatamemberNames.add("actualLogicalGenerationTime");
        _allDatamemberNames.add("federateFilter");
        _allDatamemberNames.add("originFed");
        _allDatamemberNames.add("sourceFed");

        _allDatamemberNames.add("FederateId");
        _allDatamemberNames.add("FederateType");
        _allDatamemberNames.add("IsLateJoiner");

        _datamemberTypeMap.put("FederateId", "String");
        _datamemberTypeMap.put("FederateType", "String");
        _datamemberTypeMap.put("IsLateJoiner", "boolean");
    }

    private static String initErrorMessage = "Error:  " + OBJECT_CLASS_NAME + ":  could not initialize:  ";

    protected static void init(RTIambassador rti) {
        if (_isInitialized) return;
        _isInitialized = true;

        C2WInteractionRoot.init(rti);

        boolean isNotInitialized = true;
        while (isNotInitialized) {
            try {
                _handle = rti.getInteractionClassHandle(OBJECT_CLASS_NAME);
                isNotInitialized = false;
                logger.trace("{} initialized", OBJECT_SIMPLE_CLASS_NAME);
            } catch (FederateNotExecutionMember f) {
                logger.error("{} Federate Not Execution Member", initErrorMessage);
                logger.error(f);
                return;
            } catch (NameNotFound n) {
                logger.error("{} Name Not Found", initErrorMessage);
                logger.error(n);
                return;
            } catch (Exception e) {
                logger.error(e);
                CpswtUtils.sleepDefault();
            }
        }

        _classNameHandleMap.put(OBJECT_CLASS_NAME, get_handle());
        _classHandleNameMap.put(get_handle(), OBJECT_CLASS_NAME);
        _classHandleSimpleNameMap.put(get_handle(), OBJECT_SIMPLE_CLASS_NAME);

        isNotInitialized = true;
        while (isNotInitialized) {
            try {

                _FederateId_handle = rti.getParameterHandle("FederateId", get_handle());
                _FederateType_handle = rti.getParameterHandle("FederateType", get_handle());
                _IsLateJoiner_handle = rti.getParameterHandle("IsLateJoiner", get_handle());
                isNotInitialized = false;
            } catch (FederateNotExecutionMember f) {
                logger.error("{} Federate Not Execution Member", initErrorMessage);
                logger.error(f);
                return;
            } catch (InteractionClassNotDefined i) {
                logger.error("{} Interaction Class Not Defined", initErrorMessage);
                logger.error(i);
                return;
            } catch (NameNotFound n) {
                logger.error("{} Name Not Found", initErrorMessage);
                logger.error(n);
                return;
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private static boolean _isPublished = false;
    private static String publishErrorMessage = "Error:  " + OBJECT_CLASS_NAME + ":  could not publish:  ";

    public static void publish(RTIambassador rti) {
        if (_isPublished) return;

        init(rti);

        synchronized (rti) {
            boolean isNotPublished = true;
            while (isNotPublished) {
                try {
                    rti.publishInteractionClass(get_handle());
                    isNotPublished = false;
                } catch (FederateNotExecutionMember f) {
                    logger.error("{} Federate Not Execution Member", publishErrorMessage);
                    logger.error(f);
                    return;
                } catch (InteractionClassNotDefined i) {
                    logger.error("{} Interaction Class Not Defined", publishErrorMessage);
                    logger.error(i);
                    return;
                } catch (Exception e) {
                    logger.error(e);
                    CpswtUtils.sleepDefault();
                }
            }
        }

        _isPublished = true;
    }

    private static String unpublishErrorMessage = "Error:  " + OBJECT_CLASS_NAME + ":  could not unpublish:  ";

    public static void unpublish(RTIambassador rti) {
        if (!_isPublished) return;

        init(rti);
        synchronized (rti) {
            boolean isNotUnpublished = true;
            while (isNotUnpublished) {
                try {
                    rti.unpublishInteractionClass(get_handle());
                    isNotUnpublished = false;
                } catch (FederateNotExecutionMember f) {
                    logger.error("{} Federate Not Execution Member", unpublishErrorMessage);
                    logger.error(f);
                    return;
                } catch (InteractionClassNotDefined i) {
                    logger.error("{} Interaction Class Not Defined", unpublishErrorMessage);
                    logger.error(i);
                    return;
                } catch (InteractionClassNotPublished i) {
                    logger.error("{} Interaction Class Not Published", unpublishErrorMessage);
                    logger.error(i);
                    return;
                } catch (Exception e) {
                    logger.error(e);
                    CpswtUtils.sleepDefault();
                }
            }
        }

        _isPublished = false;
    }

    private static boolean _isSubscribed = false;
    private static String subscribeErrorMessage = "Error:  "+OBJECT_CLASS_NAME+":  could not subscribe:  ";

    public static void subscribe( RTIambassador rti ) {
        if ( _isSubscribed ) return;

        init( rti );
        synchronized( rti ) {
            boolean isNotSubscribed = true;
            while( isNotSubscribed ) {
                try {
                    rti.subscribeInteractionClass( get_handle() );
                    isNotSubscribed = false;
                } catch ( FederateNotExecutionMember f ) {
                    logger.error( "{} Federate Not Execution Member" , subscribeErrorMessage);
                    logger.error(f);
                    return;
                } catch ( InteractionClassNotDefined i ) {
                    logger.error( "{} Interaction Class Not Defined", subscribeErrorMessage );
                    logger.error(i);
                    return;
                } catch ( Exception e ) {
                    logger.error(e);
                    CpswtUtils.sleepDefault();
                }
            }
        }

        _isSubscribed = true;
    }

    private static String unsubscribeErrorMessage = "Error:  "+OBJECT_CLASS_NAME+":  could not unsubscribe:  ";
    public static void unsubscribe( RTIambassador rti ) {
        if ( !_isSubscribed ) return;

        init( rti );
        synchronized( rti ) {
            boolean isNotUnsubscribed = true;
            while( isNotUnsubscribed ) {
                try {
                    rti.unsubscribeInteractionClass( get_handle() );
                    isNotUnsubscribed = false;
                } catch ( FederateNotExecutionMember f ) {
                    logger.error("{} Federate Not Execution Member", unsubscribeErrorMessage );
                    logger.error(f);
                    return;
                } catch ( InteractionClassNotDefined i ) {
                    logger.error( "{} Interaction Class Not Defined", unsubscribeErrorMessage  );
                    logger.error(i);
                    return;
                } catch ( InteractionClassNotSubscribed i ) {
                    logger.error( "{} Interaction Class Not Subscribed", unsubscribeErrorMessage  );
                    logger.error(i);
                    return;
                } catch ( Exception e ) {
                    logger.error(e);
                    CpswtUtils.sleepDefault();
                }
            }
        }

        _isSubscribed = false;
    }

    public static boolean match( int handle ) { return handle == get_handle(); }
    public int getClassHandle() { return get_handle(); }
    public String getClassName() { return get_class_name(); }
    public String getSimpleClassName() { return get_simple_class_name(); }
    public Set< String > getParameterNames() { return get_parameter_names(); }
    public Set< String > getAllParameterNames() { return get_all_parameter_names(); }
    public void publishInteraction( RTIambassador rti ) { publish( rti ); }
    public void unpublishInteraction( RTIambassador rti ) { unpublish( rti ); }
    public void subscribeInteraction( RTIambassador rti ) { subscribe( rti ); }
    public void unsubscribeInteraction( RTIambassador rti ) { unsubscribe( rti ); }

    public String toString() {
        return String.format("%s(FederateId: %s, FederateType: %s, IsLateJoiner: %b", OBJECT_SIMPLE_CLASS_NAME,
                getFederateId(), getFederateType(), isLateJoiner());
    }

    private String federateId;
    private String federateType;
    private boolean isLateJoiner;

    public String getFederateId() {
        return federateId;
    }

    public void setFederateId(String federateId) {
        this.federateId = federateId;
    }

    public String getFederateType() {
        return federateType;
    }

    public void setFederateType(String federateType) {
        this.federateType = federateType;
    }

    public boolean isLateJoiner() {
        return isLateJoiner;
    }

    public void setLateJoiner(boolean lateJoiner) {
        isLateJoiner = lateJoiner;
    }

    public FederateResignInteraction(ReceivedInteraction datamemberMap, boolean initFlag) {
        super(datamemberMap, initFlag);
        if ( initFlag ) setParameters( datamemberMap );
    }

    public FederateResignInteraction(ReceivedInteraction datamemberMap, LogicalTime logicalTime, boolean initFlag) {
        super(datamemberMap, logicalTime, initFlag);
        if ( initFlag ) setParameters( datamemberMap );
    }

    public FederateResignInteraction(ReceivedInteraction datamemberMap) {
        this( datamemberMap, true );
    }

    public FederateResignInteraction(ReceivedInteraction datamemberMap, LogicalTime logicalTime) {
        this( datamemberMap, logicalTime, true );
    }

    public FederateResignInteraction(FederateResignInteraction federateResignInteraction) {
        super(federateResignInteraction);

        setFederateId(federateResignInteraction.getFederateId());
        setFederateType(federateResignInteraction.getFederateType());
        setLateJoiner(federateResignInteraction.isLateJoiner());
    }

    public Object getParameter( String datamemberName ) {
        if ("FederateId".equals(datamemberName)) return getFederateId();
        else if ("FederateType".equals(datamemberName)) return getFederateType();
        else if ("IsLateJoiner".equals(datamemberName)) return isLateJoiner();
        else return super.getParameter(datamemberName);
    }

    public Object getParameter( int datamemberHandle ) {
        if (get_FederateId_handle() == datamemberHandle) return getFederateId();
        else if (get_FederateType_handle() == datamemberHandle) return getFederateType();
        else if (get_IsLateJoiner_handle() == datamemberHandle) return new Boolean(isLateJoiner());
        else return super.getParameter(datamemberHandle);
    }

    protected boolean setParameterAux( int param_handle, String val ) {
        boolean retval = true;

        if (param_handle == get_FederateId_handle()) setFederateId(val);
        else if (param_handle == get_FederateType_handle()) setFederateType(val);
        else if (param_handle == get_IsLateJoiner_handle()) setLateJoiner(Boolean.parseBoolean(val));
        else retval = super.setParameterAux(param_handle, val);

        return retval;
    }

    protected boolean setParameterAux( String datamemberName, String val ) {
        boolean retval = true;

        if ("FederateId".equals(datamemberName)) setFederateId(val);
        else if ("FederateType".equals(datamemberName)) setFederateType(val);
        else if ("IsLateJoiner".equals(datamemberName)) setLateJoiner(Boolean.parseBoolean(val));
        else retval = super.setParameterAux(datamemberName, val);

        return retval;
    }

    protected boolean setParameterAux( String datamemberName, Object val ) {
        boolean retval = true;

        if ("FederateId".equals(datamemberName)) setFederateId((String) val);
        else if ("FederateType".equals(datamemberName)) setFederateType((String) val);
        else if ("IsLateJoiner".equals(datamemberName)) setLateJoiner((boolean) val);
        else retval = super.setParameterAux(datamemberName, val);

        return retval;
    }

    protected SuppliedParameters createSuppliedDatamembers() {
        SuppliedParameters datamembers = super.createSuppliedDatamembers();

        datamembers.add( get_FederateId_handle(), getFederateId().getBytes());
        datamembers.add(get_FederateType_handle(), getFederateType().getBytes());
        datamembers.add(get_IsLateJoiner_handle(), new Boolean(isLateJoiner()).toString().getBytes());

        return datamembers;
    }


    public void copyFrom( Object object ) {
        super.copyFrom( object );
        if ( object instanceof FederateResignInteraction ) {
            FederateResignInteraction data = (FederateResignInteraction) object;

            federateId = data.federateId;
            federateType = data.federateType;
            isLateJoiner = data.isLateJoiner;
        }
    }
}
