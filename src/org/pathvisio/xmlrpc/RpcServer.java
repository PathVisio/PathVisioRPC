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

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

/**
 * Creates a server and a xml server, assigns the handler and starts the server.
 * This could be always started using the same port (default 7777) or a port can
 * given as an argument. The port is returned in order to call the handlers.
 * 
 * @author anwesha
 * @author magali
 */

public class RpcServer implements XmlRpcHandlerMapping {

	private int port = getDefaultPort();

	private WebServer webServer;

	/**
	 * @param port
	 * @return
	 */
	public int startServer(final int port) {
		if (this.port != port) {
			this.port = port;
		}
		try {
			System.out.println("Starting PathVisioRPC Server...");

			this.webServer = new WebServer(port);
			XmlRpcServer xmlserver = this.webServer.getXmlRpcServer();
			PropertyHandlerMapping phm = new PropertyHandlerMapping();
			phm.addHandler("PathVisio", PathVisio.class);
			xmlserver.setHandlerMapping(phm);
			this.webServer.start();
			System.out.println("Server Started successfully on port " + port);
			System.out.println("Accepting requests ...");
		} catch (Exception exception) {
			this.webServer.log(exception);
			System.err.println("JavaServer: " + exception);
		}
		return port;
	}

	/**
	 * 
	 */
	public void shutdown() {
		try {
			this.webServer.shutdown();
			System.out.println("Server Stopped successfully ");
		} catch (Exception e) {
			this.webServer.log(e);
			System.err.println("JavaServer: " + e);
		}

	}

	@Override
	public XmlRpcHandler getHandler(String arg0)
			throws XmlRpcNoSuchHandlerException, XmlRpcException {
		return null;
	}

	/**
	 * Sets the default port for the PathVisioRPC server to start on
	 * The default port is 7777
	 * 
	 * @return the default port
	 */
	public int getDefaultPort() {
		return 7777;
	}
}