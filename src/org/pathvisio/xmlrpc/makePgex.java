package org.pathvisio.xmlrpc;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
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

public class makePgex implements XmlRpcHandlerMapping { 
	public String msg(){
		return "hello!";
	}
	public String createPgex(String inputFile, String dbFile) throws IOException, IDMapperException, ClassNotFoundException {
	PreferenceManager.init();
    GexManager gexManager = new GexManager();
    ImportInformation info = new ImportInformation();
    info.setTxtFile(new File(inputFile));//the comparison file
    //settings
    DataSource ds = BioDataSource.ENSEMBL_MOUSE;
    info.setSyscodeFixed(true);
    info.setDataSource(ds);
    info.setGexName("" + inputFile);
	Class.forName("org.bridgedb.rdb.IDMapperRdb");
	IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
    GexTxtImporter.importFromTxt(info, null, gdb, gexManager);
    FileWriter fw = new FileWriter (inputFile + "error.txt");
    fw.write("Writing file: " + info.getGexName() + "\n");
    fw.write("Errors: " + info.getErrorList().size() + "\n");
    fw.write("Data rows: " + info.getDataRowsImported() + "\n");
    fw.write("Mapped ok: " + info.getRowsMapped() + "\n");
    fw.close();
	return "pgex made!";	
	}
	
@Override
public XmlRpcHandler getHandler(String arg0)
		throws XmlRpcNoSuchHandlerException, XmlRpcException {
	// TODO Auto-generated method stub
	return null;
}
 }