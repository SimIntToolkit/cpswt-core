package HelloWorld_Java_Tutorial;

import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;

import org.cpswt.hla.*;
import org.cpswt.config.FederateConfig;

public class SinkBase extends SynchronizedFederate {

    private SubscribedInteractionFilter _subscribedInteractionFilter = new SubscribedInteractionFilter();

    // constructor
    public SinkBase(FederateConfig params) throws Exception {
        super(params);

        super.createLRC();
        super.joinFederation();

        super.enableTimeConstrained();
        super.enableTimeRegulation(super.getLookAhead());
        super.enableAsynchronousDelivery();

        // interaction pubsub
        Ping.subscribe(super.getLRC());
        _subscribedInteractionFilter.setFedFilters(
                Ping.get_handle(),
                SubscribedInteractionFilter.OriginFedFilter.ORIGIN_FILTER_DISABLED,
                SubscribedInteractionFilter.SourceFedFilter.SOURCE_FILTER_DISABLED
        );

        // object pubsub
        PingCount.publish_RunningCount();
        PingCount.publish_SinkName();
        PingCount.publish(super.getLRC());
    }

    @Override
    public void receiveInteraction(
            int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag
    ) {
        InteractionRoot interactionRoot = InteractionRoot.create_interaction(interactionClass, theInteraction);
        if (interactionRoot instanceof C2WInteractionRoot) {

            C2WInteractionRoot c2wInteractionRoot = (C2WInteractionRoot) interactionRoot;

            // Filter interaction if src/origin fed requirements (if any) are not met
            if (_subscribedInteractionFilter.filterC2WInteraction(getFederateId(), c2wInteractionRoot)) {
                return;
            }
        }

        super.receiveInteraction(interactionClass, theInteraction, userSuppliedTag);
    }

    @Override
    public void receiveInteraction(
            int interactionClass,
            ReceivedInteraction theInteraction,
            byte[] userSuppliedTag,
            LogicalTime theTime,
            EventRetractionHandle retractionHandle
    ) {
        InteractionRoot interactionRoot = InteractionRoot.create_interaction(interactionClass, theInteraction, theTime);
        if (interactionRoot instanceof C2WInteractionRoot) {

            C2WInteractionRoot c2wInteractionRoot = (C2WInteractionRoot) interactionRoot;

            // Filter interaction if src/origin fed requirements (if any) are not met
            if (_subscribedInteractionFilter.filterC2WInteraction(getFederateId(), c2wInteractionRoot)) {
                return;
            }
        }

        super.receiveInteraction(interactionClass, theInteraction, userSuppliedTag, theTime, retractionHandle);
    }
}
