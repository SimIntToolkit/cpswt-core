/*
 * Copyright (c) 2008, Institute for Software Integrated Systems, Vanderbilt University
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE VANDERBILT UNIVERSITY BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE VANDERBILT
 * UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE VANDERBILT UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE VANDERBILT UNIVERSITY HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 * @author Himanshu Neema
 */

package org.cpswt.util;

import hla.rti.ReceivedInteraction;
import org.cpswt.hla.SimLog;

/**
 * This is a utility class for some commonly used methods that can be used by
 * all federates alike.
 * 
 * @author Himanshu Neema
 */
public class FedUtil {
    private static void setFieldValues(SimLog log, String fedName,
            String comment, double localTime) {
        log.set_FedName( fedName );
        log.set_Comment( comment );
        log.set_Time( localTime );
    }
    
    public static void dumpInteraction( ReceivedInteraction in ) throws Exception
    {
        System.out.println( "interaction dump");
        System.out.println( "params=" + in.size() );
        for( int i=0; i<in.size(); ++i )
        {
            System.out.println( "\t" + in.getParameterHandle(i) + "\t" + new String(in.getValue(i)) );
        }
        
    }
    
    /**
     * Returns the loaded class if loading is successful, else null.
     * @param qualifiedClassname
     * @return Class
     */
    public static Class loadClassByName(String qualifiedClassname) {
    	Class c = null;
    	try {
			c = Class.forName( qualifiedClassname );
		} catch (ClassNotFoundException e) {
			return null;
		}
		return c;
    }
}
