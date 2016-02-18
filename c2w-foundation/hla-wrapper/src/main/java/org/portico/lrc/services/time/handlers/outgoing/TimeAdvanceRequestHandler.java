/*
 *   Copyright 2008 The Portico Project
 *
 *   This file is part of portico.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL) 
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *   
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package org.portico.lrc.services.time.handlers.outgoing;

import java.sql.Date;
import java.util.Map;

import org.portico.lrc.LRCMessageHandler;
import org.portico.lrc.compat.JFederationTimeAlreadyPassed;
import org.portico.lrc.services.time.data.TimeStatus;
import org.portico.lrc.services.time.msg.TimeAdvanceRequest;
import org.portico.utils.messaging.MessageContext;
import org.portico.utils.messaging.MessageHandler;

@MessageHandler(modules="lrc-base",
                keywords={"lrc13","lrcjava1","lrc1516"},
                sinks="outgoing",
                messages=TimeAdvanceRequest.class)
public class TimeAdvanceRequestHandler extends LRCMessageHandler
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void initialize( Map<String,Object> properties )
	{
		super.initialize( properties );
	}
	
	public void process( MessageContext context ) throws Exception
	{
		// basic state validity checks
		lrcState.checkJoined();          // FederateNotExecutionMemeber
		lrcState.checkAdvancing();       // TimeAdvanceAlreadyInProgress
		lrcState.checkTimeRegulation();  // EnableTimeRegulationPending
		lrcState.checkTimeConstrained(); // EnableTimeConstrainedPending
		lrcState.checkSave();            // SaveInProgress
		lrcState.checkRestore();         // RestoreInProgress

		TimeAdvanceRequest request = context.getRequest( TimeAdvanceRequest.class, this );
		double time = request.getTime();
		TimeStatus ourStatus = timeStatus();

		if( logger.isDebugEnabled() )
			logger.debug( "REQUEST Time advance request for ["+moniker()+"] to ["+time+"]" );

		// check that the time is valid
		if(request.isTara()) {
			// Himanshu: The fix makes sure that when exceptions aren't
			// unintentionally thrown due to minute difference between
			// requested and current times when NextEventRequestAvailable
			// is used
			if( time < ourStatus.getCurrentTime() )
			{
				// Reset requested time to current time
				System.out.println("TimeAdvanceRequestAvailableHandler: Requested time to advance is less than current federate time... not aborting");
				time = ourStatus.getCurrentTime();
			}
		} else {
			if( time <= ourStatus.getCurrentTime() )
			{
				// requested time is less than current time, exception
				throw new JFederationTimeAlreadyPassed( "TAR: Time " + time + " has already passed" );
			}
		}

		// set the status
		ourStatus.timeAdvanceRequested( time );

		// notify everyone else
		if( logger.isInfoEnabled() )
		{
			logger.info( "PENDING Requested time advance for ["+moniker()+"] to ["+time+
			             "], waiting for grant..." );
		}                             

		connection.broadcast( request );
		context.success();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
