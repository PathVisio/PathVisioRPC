package org.pathvisio.xmlrpc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;


public class XmlRpcPlugin implements Plugin
{
	private PvDesktop desktop;
	//private JButton startServer;
	//private JTextField portTxt;
	//private RpcServer server;
	//int portAddNum = 2501;
			
	@Override
	public void init(PvDesktop aDesktop)
	{
		desktop = aDesktop;
		
		// register our action in the "Help" menu.
		desktop.registerMenuAction ("Plugins", rpcDlgAction);
		/*		
		JPanel pnlServer = new JPanel();
		portTxt = new JTextField (6);
		startServer = new JButton ("XMLRPC Start!");
		pnlServer.add(portTxt);
		pnlServer.add (startServer);
		desktop.getSwingEngine().getApplicationPanel().addToToolbar(pnlServer);
		portTxt.addActionListener(this);
		startServer.addActionListener(this);*/
     }
	
	private final RpcDlgAction rpcDlgAction = new RpcDlgAction();
	
	/**
	 * Open the synonym database settings dialog 
	 */
	private class RpcDlgAction extends AbstractAction
	{
		RpcDlgAction()
		{
			// The NAME property of an action is used as 
			// the label of the menu item
			putValue (NAME, "XmlRpc server setup...");
		}
		
		/**
		 *  called when the user selects the menu item
		 */
		public void actionPerformed(ActionEvent arg0) 
		{
			XmlRpcServerDlg dlg = new XmlRpcServerDlg(desktop.getFrame());
			dlg.createAndShowGUI();
		}
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub
		
	}
	
	/*@Override
	public void actionPerformed(ActionEvent arg0) {
		String portAdd = portTxt.getText();
		if (portAdd.length() > 0){
		portAddNum = Integer.parseInt(portAdd);
		}
		try {
			server = new RpcServer();
			server.startServer(portAddNum);
		}catch (Exception exception){
			 System.err.println("JavaServer: " + exception);
		}
	}
	
	@Override
	public void done()
	{
	server.shutdown();
	}*/	

}
