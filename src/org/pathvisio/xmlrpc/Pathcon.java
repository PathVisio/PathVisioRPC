package org.pathvisio.xmlrpc;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import java.io.*;
import org.pathvisio.gex.GexManager;
import org.pathvisio.plugins.gex.GexTxtImporter;
import org.pathvisio.plugins.gex.ImportInformation;
import org.pathvisio.preferences.PreferenceManager;

public class Pathcon implements XmlRpcHandlerMapping { 
	public String pgex(String filename) throws IOException, IDMapperException, ClassNotFoundException {
	PreferenceManager.init();
    GexManager gexManager = new GexManager();
    ImportInformation info = new ImportInformation();
    info.setTxtFile(new File(filename));
    //settings
    DataSource ds = BioDataSource.ENSEMBL_HUMAN;
    info.setSyscodeFixed(true);
    info.setDataSource(ds);
    String output = "/home/rai/Desktop/maastricht/result_server/feb_21.txt";
	info.setGexName("" + output);
	String dotBridgeFile = "/home/rai/Desktop/maastricht/path_scripts/Hs_Derby_20090720.bridge";
	Class.forName("org.bridgedb.rdb.IDMapperRdb");
	IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dotBridgeFile);
    GexTxtImporter.importFromTxt(info, null, gdb, gexManager);
    String reportfile = "/home/rai/Desktop/maastricht/result_server/feb_21_error.txt";
    FileWriter fw = new FileWriter (reportfile);
    fw.write("Writing file: " + info.getGexName() + "\n");
    fw.write("Errors: " + info.getErrorList().size() + "\n");
    fw.write("Data rows: " + info.getDataRowsImported() + "\n");
    fw.write("Mapped ok: " + info.getRowsMapped() + "\n");
    fw.close();
	return null;	
	}
 public static void main (String [] args) {
  try {
	 System.out.println("Attempting to start Pgex XML-RPC Server...");
     WebServer server = new WebServer(8080);
     XmlRpcServer xmlserver = server.getXmlRpcServer();
     PropertyHandlerMapping phm = new PropertyHandlerMapping();
     phm.addHandler("PgexTest",Pathcon.class);
     xmlserver.setHandlerMapping(phm);
     server.start();
     System.out.println("Started successfully.");
     System.out.println("Accepting requests. (Halt program to stop.)");
   } catch (Exception exception) {
     System.err.println("Pathcon: " + exception);
   }
  }

@Override
public XmlRpcHandler getHandler(String arg0)
		throws XmlRpcNoSuchHandlerException, XmlRpcException {
	// TODO Auto-generated method stub
	return null;
}
 }