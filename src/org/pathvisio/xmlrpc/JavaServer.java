package org.pathvisio.xmlrpc;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

public class JavaServer implements XmlRpcHandlerMapping { 

	public String message(){
		return "It works!";
	}
 public int square(int x) {
    return (x*x);
 }
 public int sum(int a,int b){
	 return(a+b);
 }
 public String showMessage(String message, String name) {
	 String mesg = "No communication";
	 String msg = "R says hello server says hi";
	 if(message.equals("hello")){
		 mesg = msg ;
	 }
	 if(name.equals("rai")){
		 mesg = msg + " rai";
	 }
     return mesg;
 }

 public static void main (String [] args) {
  try {
	 System.out.println("Attempting to start Javaserver - XML-RPC Server...");
     WebServer server = new WebServer(2501);
     XmlRpcServer xmlserver = server.getXmlRpcServer();
     PropertyHandlerMapping phm = new PropertyHandlerMapping();
     phm.addHandler("Server", JavaServer.class);
     phm.addHandler("Pathways", PathwayFunctions.class);
     phm.addHandler("VisualHandler", SingVis.class);
	 phm.addHandler("PgexHandler",makePgex.class);
	 phm.addHandler("ZscoreHandler", calculateZscore.class);
	 phm.addHandler("xportHandler", exporter.class);
     xmlserver.setHandlerMapping(phm);
     server.start();
     System.out.println("Started successfully.");
     System.out.println("Accepting requests. (Halt program to stop.)");
   } catch (Exception exception) {
     System.err.println("JavaServer: " + exception);
   }
  }

@Override
public XmlRpcHandler getHandler(String arg0)
		throws XmlRpcNoSuchHandlerException, XmlRpcException {
	// TODO Auto-generated method stub
	return null;
}
 }