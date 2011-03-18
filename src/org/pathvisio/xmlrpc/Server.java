package org.pathvisio.xmlrpc;

import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import org.pathvisio.Engine;
import org.pathvisio.debug.Logger;

public class Server
{
	private final Engine engine;
	
	public Server (Engine engine)
	{
		this.engine = engine;
	}
	
	private WebServer server = null;
	private int port;
		
	public void init(int port) throws XmlRpcException, IOException
	{
		this.port = port;
		Logger.log.info("Attempting to start XML-RPC Server on port " + port);
		WebServer server = new WebServer(port);
		XmlRpcServer xmlserver = server.getXmlRpcServer();
		PropertyHandlerMapping phm = new PropertyHandlerMapping();
		PathwayFunctions.setEngine(engine);
		phm.addHandler("Pathways", PathwayFunctions.class);
		phm.addHandler("VisualHandler", SingVis.class);
		 phm.addHandler("PgexHandler",Pathcon.class);
		 phm.addHandler("ZscoreHandler", calculateZscore.class);
		xmlserver.setHandlerMapping(phm);
		server.start();
		Logger.log.info("Started successfully.");
		Logger.log.info("Accepting requests. (Halt program to stop.)");
	}
	
	public void shutdown()
	{
		server.shutdown();
	}

	public int getDefaultPort() { return 8080; }
}
