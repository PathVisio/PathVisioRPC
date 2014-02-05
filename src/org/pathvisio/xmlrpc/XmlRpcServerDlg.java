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
	private JFrame parentFrame;
	private JDialog serverDlg;
	private JButton startServer;
	private JButton stopServer;
	private JButton helpServer;
	private JTextField portTxt;
	private RpcServer server;
	private int portAddNum = 7777;

	/**
	 * @param frame
	 */
	public XmlRpcServerDlg(JFrame frame) {
		setParentFrame(frame);
	}

	protected void createAndShowGUI() {
		setServerDlg(new JDialog(this.getParentFrame()));
		JPanel pnlServer = new JPanel();

		setPortTxt(new JTextField(6));
		setStartServer(new JButton("Start"));
		setStopServer(new JButton("Stop"));
		getStopServer().setEnabled(false);
		setHelpServer(new JButton("?"));

		pnlServer.add(this.getPortTxt());
		pnlServer.add(this.getStartServer());
		pnlServer.add(this.getStopServer());
		pnlServer.add(this.getHelpServer());

		this.getPortTxt().addActionListener(this);
		this.getStartServer().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String portAdd = XmlRpcServerDlg.this.getPortTxt().getText();
				XmlRpcServerDlg.this.getPortTxt().setEditable(false);
				if (portAdd.length() > 0) {
					XmlRpcServerDlg.this.setPortAddNum(Integer.parseInt(portAdd));
				}
				try {
					XmlRpcServerDlg.this.setServer(new RpcServer());
					XmlRpcServerDlg.this.getServer()
					.startServer(XmlRpcServerDlg.this.getPortAddNum());
					JOptionPane.showMessageDialog(
							XmlRpcServerDlg.this.getParentFrame().getComponent(0),
							"Server started succesfully on port "
									+ XmlRpcServerDlg.this.getPortTxt().getText());
					XmlRpcServerDlg.this.getStartServer().setEnabled(false);
					XmlRpcServerDlg.this.getStopServer().setEnabled(true);
				} catch (Exception exception) {
					System.err.println("JavaServer: " + exception);
				}
			}
		});

		this.getStopServer().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				XmlRpcServerDlg.this.getServer().shutdown();
				JOptionPane.showMessageDialog(getParentFrame().getComponent(0),
						"Server stopped succesfully");
				getPortTxt().setEditable(true);
				getStartServer().setEnabled(true);
				getStopServer().setEnabled(false);
			}
		});

		getHelpServer().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
						getParentFrame().getComponent(0),
						""
								+ "Text Area : Enter the port on which you want to start the server. \n"
								+ "Start Button : Starts the xmlrpc server.You can then call pathvisio\n"
								+ " functions from the scripting language of your choice.\n Stop Button :"
								+ " Stops the xmlrpc server.");
			}
		});
		getHelpServer().setToolTipText("Help about this dialog");

		getServerDlg().setTitle("XmlRpc Server Configuration");
		getServerDlg().add(pnlServer);
		getServerDlg().pack();
		getServerDlg().setSize(300, 50);
		getServerDlg().setLocationRelativeTo(getParentFrame());
		getServerDlg().setVisible(true);
	}

	/**
	 * Launches the PathVisioRPC server as a PathVisio plugin
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String portAdd = getPortTxt().getText();
		if (portAdd.length() > 0) {
			setPortAddNum(Integer.parseInt(portAdd));
		}
		try {
			setServer(new RpcServer());
			getServer().startServer(getPortAddNum());
		} catch (Exception exception) {
			System.err.println("JavaServer: " + exception);
		}
	}

	/**
	 * @return the parentFrame
	 */
	public JFrame getParentFrame() {
		return this.parentFrame;
	}

	/**
	 * @param parentFrame the parentFrame to set
	 */
	public void setParentFrame(JFrame parentFrame) {
		this.parentFrame = parentFrame;
	}

	/**
	 * @return the serverDlg
	 */
	public JDialog getServerDlg() {
		return this.serverDlg;
	}

	/**
	 * @param serverDlg the serverDlg to set
	 */
	public void setServerDlg(JDialog serverDlg) {
		this.serverDlg = serverDlg;
	}

	/**
	 * @return the portTxt
	 */
	public JTextField getPortTxt() {
		return this.portTxt;
	}

	/**
	 * @param portTxt the portTxt to set
	 */
	public void setPortTxt(JTextField portTxt) {
		this.portTxt = portTxt;
	}

	/**
	 * @return the startServer
	 */
	public JButton getStartServer() {
		return this.startServer;
	}

	/**
	 * @param startServer the startServer to set
	 */
	public void setStartServer(JButton startServer) {
		this.startServer = startServer;
	}

	/**
	 * @return the stopServer
	 */
	public JButton getStopServer() {
		return this.stopServer;
	}

	/**
	 * @param stopServer the stopServer to set
	 */
	public void setStopServer(JButton stopServer) {
		this.stopServer = stopServer;
	}

	/**
	 * @return the helpServer
	 */
	public JButton getHelpServer() {
		return this.helpServer;
	}

	/**
	 * @param helpServer the helpServer to set
	 */
	public void setHelpServer(JButton helpServer) {
		this.helpServer = helpServer;
	}

	/**
	 * @return the portAddNum
	 */
	public int getPortAddNum() {
		return this.portAddNum;
	}

	/**
	 * @param portAddNum the portAddNum to set
	 */
	public void setPortAddNum(int portAddNum) {
		this.portAddNum = portAddNum;
	}

	/**
	 * @return the server
	 */
	public RpcServer getServer() {
		return this.server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(RpcServer server) {
		this.server = server;
	}
}
