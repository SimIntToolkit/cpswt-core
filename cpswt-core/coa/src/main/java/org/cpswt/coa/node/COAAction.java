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

package org.cpswt.coa.node;

import org.cpswt.coa.enums.COANodeType;
import org.cpswt.coa.node.COANode;

import java.util.HashMap;

/**
 * Represents an action element in the sequence graph.
 */
public class COAAction extends COANode {

	private String interactionClassName;
	private HashMap<String, String> nameValueParamPairs = new HashMap<String, String>();

	public COAAction(String nodeName, String uniqueID, String interactionClassName) {
		super(nodeName, uniqueID, COANodeType.Action);

		this.interactionClassName = interactionClassName;
	}

	@Override
	public String toString() {
		return String.format("%s, Interaction: %s", super.toString(), interactionClassName);
	}

	public String getInteractionClassName() {
		return interactionClassName;
	}
	public HashMap<String, String> getNameValueParamPairs() {
		return nameValueParamPairs;
	}

	public void addNameValueParamPair(String name, String value) {
		nameValueParamPairs.put(name, value);
	}
}
