package org.pathvisio.xmlrpc;

import org.pathvisio.gui.swing.PvDesktop;
import org.pathvisio.plugin.Plugin;

public class XmlRpcPlugin implements Plugin
{
	Server server;
	
	@Override
	public void init(PvDesktop desktop)
	{
		try {
			server = new Server(desktop.getSwingEngine().getEngine());
			server.init (server.getDefaultPort());
		} 
		catch (Exception exception) 
		{
			// pass on, will be caught by plugin initialization code.
			throw new Error (exception);
		}
	}

	@Override
	public void done()
	{
		if (server != null)
			server.shutdown();	
	}
}
