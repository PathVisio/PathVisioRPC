package org.pathvisio.xmlrpc;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;

/**
 * Launch Server by creating an RpcServer object that uses a fixed port, that can be given as 
 * parameter.
 * @author anwesha, magali
 */
public class JavaServer implements XmlRpcHandlerMapping { 
	
	public static void main (String [] args) {
		int port = 2501;
		if ( args.length > 0 ){
		 	 port = Integer.parseInt(args[0]);
		}
		 RpcServer service = new RpcServer();
		 service.startServer(port);
	}
	
	@Override
	public XmlRpcHandler getHandler(String arg0)
			throws XmlRpcNoSuchHandlerException, XmlRpcException {
		return null;
	}
}