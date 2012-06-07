package org.pathvisio.xmlrpc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;


public class XmlRpcPlugin implements Plugin, ActionListener
{
	private PvDesktop desktop;
	private JButton startServer;
	private JTextField portTxt;
	private RpcServer server;
	int portAddNum = 2501;
			
	@Override
	public void init(PvDesktop aDesktop)
	{
		desktop = aDesktop;
		JPanel pnlServer = new JPanel();
		portTxt = new JTextField (6);
		startServer = new JButton ("XMLRPC Start!");
		pnlServer.add(portTxt);
		pnlServer.add (startServer);
		desktop.getSwingEngine().getApplicationPanel().addToToolbar(pnlServer);
		portTxt.addActionListener(this);
		startServer.addActionListener(this);
     }
	
	@Override
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
	}

}
