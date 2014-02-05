// PathVisioRPC : the XML-RPC interface for PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2013 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.xmlrpc;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;

/**
 * Creates menu item for the PathVisioRPC plugin.
 * 
 * @author Anwesha
 * 
 */
public class XmlRpcPlugin implements Plugin {
	private PvDesktop desktop;

	/**
	 * Registers the PathVisioRPC server set up menu item in the PathVisio
	 * desktop
	 */
	@Override
	public void init(PvDesktop aDesktop) {
		setDesktop(aDesktop);

		// register the action in the "Plugins" menu.
		getDesktop().registerMenuAction("Plugins", getRpcDlgAction());

	}

	private final RpcDlgAction rpcDlgAction = new RpcDlgAction();

	/**
	 * Open the synonym database settings dialog
	 */
	private class RpcDlgAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6480894885573624712L;

		RpcDlgAction() {
			// The NAME property of an action is used as
			// the label of the menu item
			putValue(NAME, "XmlRpc server setup...");
		}

		/**
		 * called when the user selects the menu item
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			XmlRpcServerDlg dlg = new XmlRpcServerDlg(getDesktop().getFrame());
			dlg.createAndShowGUI();
		}
	}

	@Override
	public void done() {
		// deregister the action in the "Plugins" menu.
		getDesktop().unregisterMenuAction("Plugins", getRpcDlgAction());

	}

	/**
	 * @return the desktop
	 */
	public PvDesktop getDesktop() {
		return this.desktop;
	}

	/**
	 * @param desktop the desktop to set
	 */
	public void setDesktop(PvDesktop desktop) {
		this.desktop = desktop;
	}

	/**
	 * @return the rpcDlgAction
	 */
	public RpcDlgAction getRpcDlgAction() {
		return this.rpcDlgAction;
	}

}
