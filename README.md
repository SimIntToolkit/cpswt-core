# CPSWT Java Core

The core implementation of the Cyber-Physical Systems Wind Tunnel (CPSWT), including integration of Java federates,
the federation manager, and COA management.

Any Java-based federate will use the classes in this repository in order to execute.

The primary projects relating to CPSWT functionality are as follows.  Any projects not listed are under development
or are not currently being used.

## cpswt-core/root:

This project contains the Java base classes for Java classes that implement HLA interaction and object classes in CPSWT.
These Java base classes, **InteractionRoot** and **ObjectRoot**, actually contain all of the functionality needed to implement *any* HLA interaction or object class.
As such, they allow Java-based federates to use *dynamic messaging*:  this enables the federates to manipulate any HLA interaction or object class instance using only
the InteractionRoot or ObjectRoot Java class, respectively.  That is, an HLA interaction or object instance can be manipulated *without* an explicit corresponding Java class.

## cpswt-core/base-events:
CPSWT comes with a set of built-in HLA interaction and object classes that are derived from InteractionRoot and ObjectRoot.  These are (indentation shows inheritance):

HLA Interactions:

        InteractionRoot
          C2WInteractionRoot
            ActionBase
            EmbeddedMessaging
            FederateJoinInteraction
            FederateResignInteraction
            OutcomeBase
            SimLog
              VeryLowPrio
              LowPrio
              MediumPrio
              HighPrio
            SimulationControl
              SimPause
              SimResume
              SimEnd

HLA Objects:

        ObjectRoot
          FederateObject


base-events contains the explicit corresponding Java class implementations for these HLA classes.

**EmbeddedMessaging** is a very important class as it allows the use of embedded messaging.  Embedded messaging is a
level  of indirection in the sending of interactions and object updates, i.e. the embedded message interaction can
contain another interaction, or an object update, within it.  In particular, this allows object-updates to
be propagated through a simulated network in a network federate.

## cpswt-core/coa

This project contains the Java classes necessary to implement **Courses of Action (COA)**, i.e. behavior that is designed
into a federation via its federation model.

## cpswt-core/config

This project contains Java classes that read federate configuration files

## cpswt-core/federate-base

This project contain Java classes that implement behavior that all federates must have.  Among them are:

**SynchronizedFederate** -- the base class of all Java federates that are instantiated directly from their
representation in a CPSWT model.

**InteractionMappingBase** and **InteractionMappingManager** -- classes that provide base functionality for **Mapper**
federates.

## cpswt-core/federation-manager

This project contains the Java classes that implement the FederationManager, which:

* Is executed first for any federation
* Waits for all federates to join before proceeding with federate execution by using HLA synchronization points.

## cpswt-core/utils

This project contains various utility classes used by other projects.

## jenkins support
Ref: [youtube video](https://www.youtube.com/watch?v=6YZvp2GwT0A&t=457s)
