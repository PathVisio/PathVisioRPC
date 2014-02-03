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
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;

/**
 * Launches the PathVisioRPC Server on a port, which can be given as a
 * parameter.
 * 
 * @author anwesha
 * @author magali
 */
public class JavaServer implements XmlRpcHandlerMapping {

	/**
	 * Function to Launch the PathVisioRPC server
	 * 
	 * @param args
	 *            the port for the PathVisioRPC server to start on
	 */
	public static void main(String[] args) {
		int port = 7777;
		if (args.length > 0) {
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