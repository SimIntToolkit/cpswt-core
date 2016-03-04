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

package c2w.coa;

/**
 * Represents a ProbabilisticChoice element in the sequence graph. One and only
 * one subsequent branch is executed. The choice is made randomly and different
 * runs of the experiment will result in randomly different branch selections.
 * 
 * @author Himanshu Neema
 */
public class COAProbabilisticChoice extends COANode {

	private boolean _isDecisionPoint = false;

	public COAProbabilisticChoice(String nodeName, String uniqueID,
			boolean isDecisionPoint) {
		super(nodeName, uniqueID, NODE_TYPE.NODE_PROBABILISTIC_CHOICE);

		this._isDecisionPoint = isDecisionPoint;
	}

	@Override
	public String toString() {
		return super.toString() + ", DecisionPoint: " + _isDecisionPoint;
	}

	public boolean getIsDecisionPoint() {
		return _isDecisionPoint;
	}
}
