package HelloWorld_Java_Tutorial;

import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;

import org.cpswt.hla.*;
import org.cpswt.config.FederateConfig;

public class SourceBase extends SynchronizedFederate {

    private SubscribedInteractionFilter _subscribedInteractionFilter = new SubscribedInteractionFilter();

    // constructor
    public SourceBase(FederateConfig params) throws Exception {
        super(params);

        super.createLRC();
        super.joinFederation();

        super.enableTimeConstrained();
        super.enableTimeRegulation(super.getLookAhead());
        super.enableAsynchronousDelivery();

        // interaction pubsub

        Ping.publish(super.getLRC());

    }

    public Ping create_Ping() {
        Ping interaction = new Ping();
        interaction.set_sourceFed(getFederateId());
        interaction.set_originFed(getFederateId());
        return interaction;
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
