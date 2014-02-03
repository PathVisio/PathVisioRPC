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
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog box for PathVisioRPC Server set up.
 * 
 * @author anwesha
 * 
 */
public class XmlRpcServerDlg implements ActionListener {
	private final JFrame parentFrame;
	private JDialog serverDlg;
	private JButton startServer;
	private JButton stopServer;
	private JButton helpServer;
	private JTextField portTxt;
	private RpcServer server;
	int portAddNum = 7777;

	public XmlRpcServerDlg(JFrame frame) {
		this.parentFrame = frame;
	}

	protected void createAndShowGUI() {
		serverDlg = new JDialog(parentFrame);
		JPanel pnlServer = new JPanel();

		portTxt = new JTextField(6);
		startServer = new JButton("Start");
		stopServer = new JButton("Stop");
		stopServer.setEnabled(false);
		helpServer = new JButton("?");

		pnlServer.add(portTxt);
		pnlServer.add(startServer);
		pnlServer.add(stopServer);
		pnlServer.add(helpServer);

		portTxt.addActionListener(this);
		startServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String portAdd = portTxt.getText();
				portTxt.setEditable(false);
				if (portAdd.length() > 0) {
					portAddNum = Integer.parseInt(portAdd);
				}
				try {
					server = new RpcServer();
					server.startServer(portAddNum);
					JOptionPane.showMessageDialog(
							parentFrame.getComponent(0),
							"Server started succesfully on port "
									+ portTxt.getText());
					startServer.setEnabled(false);
					stopServer.setEnabled(true);
				} catch (Exception exception) {
					System.err.println("JavaServer: " + exception);
				}
			}
		});

		stopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				server.shutdown();
				JOptionPane.showMessageDialog(parentFrame.getComponent(0),
						"Server stopped succesfully");
				portTxt.setEditable(true);
				startServer.setEnabled(true);
				stopServer.setEnabled(false);
			}
		});

		helpServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
						parentFrame.getComponent(0),
						""
								+ "Text Area : Enter the port on which you want to start the server. \n"
								+ "Start Button : Starts the xmlrpc server.You can then call pathvisio\n"
								+ " functions from the scripting language of your choice.\n Stop Button :"
								+ " Stops the xmlrpc server.");
			}
		});
		helpServer.setToolTipText("Help about this dialog");

		serverDlg.setTitle("XmlRpc Server Configuration");
		serverDlg.add(pnlServer);
		serverDlg.pack();
		serverDlg.setSize(300, 50);
		serverDlg.setLocationRelativeTo(parentFrame);
		serverDlg.setVisible(true);
	}

	/**
	 * Launches the PathVisioRPC server as a PathVisio plugin
	 */
	public void actionPerformed(ActionEvent arg0) {
		String portAdd = portTxt.getText();
		if (portAdd.length() > 0) {
			portAddNum = Integer.parseInt(portAdd);
		}
		try {
			server = new RpcServer();
			server.startServer(portAddNum);
		} catch (Exception exception) {
			System.err.println("JavaServer: " + exception);
		}
	}
}
