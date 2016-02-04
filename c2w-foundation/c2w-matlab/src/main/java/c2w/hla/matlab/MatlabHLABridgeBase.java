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

import hla.rti.ReceivedInteraction;
import hla.rti.SuppliedParameters;

import java.util.ArrayList;

/**
 * This is an abstract base class for providing a bridge between MATLAB and
 * HLA-RTI. The derived class should be automatically generated from the
 * Federation Object Model (FOM).
 * 
 * @author Himanshu Neema
 */
public abstract class MatlabHLABridgeBase {
    protected static ArrayList<Integer> knownInteractionHandles = new ArrayList<Integer>();

    protected static ArrayList<Integer> knownParameterHandles = new ArrayList<Integer>();

    protected MatlabHLABridgeBase() {
        // Prevent object construction
    }

    protected static boolean verifyParameterHandle(int parameterHandle) {
        return knownParameterHandles.contains(parameterHandle);
    }

    protected static boolean verifyInteractionHandle(int interactionHandle) {
        return knownInteractionHandles.contains(interactionHandle);
    }

    protected static boolean verifyMatlabInteraction(MatlabInteraction mi) {
        // Check if given interaction and parameters are known
        if (!MatlabHLABridgeBase.verifyInteractionHandle(mi
                .getInteractionHandle())) {
            return false;
        }
        for (MatlabInteractionParameter parameter : mi
                .getMatlabInteractionParams()) {
            if (!MatlabHLABridgeBase.verifyParameterHandle(parameter
                    .getParameterHandle())) {
                return false;
            }
        }
        return true;
    }

    protected abstract void initializeHLAFOM();

    protected abstract SuppliedParameters convertMatlabInteractionToHLAInteraction(
            MatlabInteraction mi);

    protected abstract MatlabInteraction convertHLAInteractionToMatlabInteraction(
            ReceivedInteraction hlaInteraction, int interactionHandle);
}
