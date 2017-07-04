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

package org.cpswt.gui.coa;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.cpswt.coa.COAAction;
import org.cpswt.coa.COAEdge;
import org.cpswt.coa.COAGraph;
import org.cpswt.coa.COAOutcome;
import org.cpswt.coa.COAEdge.EDGE_TYPE;

/**
 * This is the main application frame for display the COA simulation progress.
 * 
 * @author Himanshu Neema
 */
public class COASim extends JFrame {
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(COASim.class.getName());

	private static final int WIN_SIZEX = 1120;
	private static final int WIN_SIZEY = 450;

	private JTabbedPane tabbedPane;

	private COAMainPanel mainPanel;

	private COAGraph _coaGraph;

	public COASim(COAGraph coaGraph) {
		this._coaGraph = coaGraph;
		init();
	}

	public void close() {
		System.exit(0);
	}

	/**
	 * Initialize components
	 */
	private void init() {
		setTitle("COA Simulation Display");

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				confirmYesNoBeforeExit();
			}
		});

		initMainPanel();

		tabbedPane = createTabbedPane();
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(new MenuBar(this), BorderLayout.NORTH);

		setSize(WIN_SIZEX, WIN_SIZEY);
	}

	private void initMainPanel() {
		mainPanel = new COAMainPanel(_coaGraph);
	}

	private JTabbedPane createTabbedPane() {
		JTabbedPane aTabbedPane = new JTabbedPane();
		aTabbedPane.add("Main", mainPanel);
		aTabbedPane.setPreferredSize(new Dimension((int) (WIN_SIZEX * 0.95),
				(int) (WIN_SIZEY * 0.95)));
		return aTabbedPane;
	}

	public void repaint() {
		Component[] widgets = getContentPane().getComponents();
		for (int i = 0; i < widgets.length; i++)
			widgets[i].repaint();
		super.repaint();
	}

	/**
	 * Confirm before exit
	 */
	public void confirmYesNoBeforeExit() {
		int choice = JOptionPane.showConfirmDialog(this, "Exit COA Display?",
				"Confirm exit", JOptionPane.YES_NO_OPTION);

		if (choice == JOptionPane.YES_OPTION) {
			setVisible(false);
		}
	}

	public static void main(String[] args) throws Exception {

		COAAction a1 = new COAAction("StartNodeAttack", "A1",
				"InteractionRoot.C2WInteractionRoot.ActionBase.OmnetCommand.StartNodeAttack");
		COAOutcome o1 = new COAOutcome("ReportESOC2ADOC", "O1",
				"InteractionRoot.C2WInteractionRoot.ESOCReportToADOC");
		COAEdge edge = new COAEdge(EDGE_TYPE.EDGE_COAFLOW, a1, o1, "F1", null);
		COAGraph graph = new COAGraph();
		graph.addNode(a1);
		graph.addNode(o1);
		graph.addEdge(edge);

		COASim sim = new COASim(graph);
		sim.setVisible(true);
	}
}
