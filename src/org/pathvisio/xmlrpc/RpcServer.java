package org.pathvisio.xmlrpc;

import org.apache.xmlrpc.*;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

/**
 * Create a server and a xml server, attribute the handler and start the server.
 * This could be always started using the same port or at least this should 
 * send back the port so it can be used when we call the handlers.
 * @author anwesha, magali
 *
 */
public class RpcServer implements XmlRpcHandlerMapping { 
	
	private int port = getDefaultPort();
	
	private WebServer webServer;
	
	public int startServer(int port) {
		if(this.port != port) {
			this.port = port;
		}
		  try {
			 System.out.println("Attempting to start Javaserver - XML-RPC Server...");

			 webServer = new WebServer(port);
		     XmlRpcServer xmlserver = webServer.getXmlRpcServer();
		     PropertyHandlerMapping phm = new PropertyHandlerMapping();
			 phm.addHandler("MakePgexHandler",MakePgexHandler.class);
			 phm.addHandler("VisXmlHandler", VisualizationHandler.class);
			 phm.addHandler("StatExportHandler", StatExportHandler.class);
			 xmlserver.setHandlerMapping(phm);
			 webServer.start();
			 System.out.println("Server Started successfully on port "+ port);
			 System.out.println("Accepting requests. (Halt program to stop.)");
		  } 
		  catch (Exception exception) {
			 System.err.println("JavaServer: " + exception);
		  }
		return port;
	 }
	
	public void shutdown() {
		webServer.shutdown();
	}

	@Override
	public XmlRpcHandler getHandler(String arg0)
			throws XmlRpcNoSuchHandlerException, XmlRpcException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getDefaultPort() {
		return 2501;
	}
 }