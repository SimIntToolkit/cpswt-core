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

package c2w.hla.matlab;

import java.util.ArrayList;

/**
 * This class holds an interaction sent to or from RTI. It contains a non-null
 * parameter representing the ID of the iteraction and an array of supplied
 * interaction parameters.
 * 
 * @author Himanshu Neema
 */
public class MatlabInteraction {
    private int interactionHandle = -1;

    private ArrayList<MatlabInteractionParameter> suppliedParams = new ArrayList<MatlabInteractionParameter>();

    private MatlabInteraction(int interactionHandle) {
        this.interactionHandle = interactionHandle;
    }

    public static MatlabInteraction createMatlabInteraction(
            int interactionHandle) {
        MatlabInteraction mi = null;
        try {
            MatlabHLABridgeBase.verifyInteractionHandle(interactionHandle);
            mi = new MatlabInteraction(interactionHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mi;
    }

    public int getInteractionHandle() {
        return interactionHandle;
    }

    public ArrayList<MatlabInteractionParameter> getMatlabInteractionParams() {
        return suppliedParams;
    }
}
