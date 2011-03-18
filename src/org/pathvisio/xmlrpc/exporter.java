package org.pathvisio.xmlrpc;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.jdom.Document;
import org.pathvisio.Engine;
import org.pathvisio.gex.GexManager;
import org.pathvisio.model.ConverterException;
import org.pathvisio.plugins.HtmlExporter;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.visualization.Visualization;
import org.pathvisio.visualization.VisualizationManager;


public class exporter {
	@SuppressWarnings("static-access")
public String exportInfo() throws ClassNotFoundException, IDMapperException, ConverterException, IOException{
	PreferenceManager.init();
	Class.forName("org.bridgedb.rdb.IDMapperRdb");
	IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + "/home/rai/Desktop/maastricht/result_server/Hs_Derby_20090720.bridge");
	Engine engine = new Engine ();
	GexManager gexMgr = new GexManager();
	gexMgr.setCurrentGex("/home/rai/Desktop/maastricht/result_server/feb_21.txt.pgex",false);
	gexMgr.getCachedData().setMapper(gdb);
	VisualizationManager visMgr = new VisualizationManager(engine, gexMgr);
	HtmlExporter htmlexpo = new HtmlExporter(gdb, visMgr, gexMgr);
	List<File> pwFiles = new ArrayList<File>() ;
	pwFiles.add(new File("/home/rai/Desktop/maastricht/result_server/pathways/WP143_39785.gpml"));
	pwFiles.add(new File("/home/rai/Desktop/maastricht/result_server/pathways/WP528_41084.gpml"));
	pwFiles.add(new File("/home/rai/Desktop/maastricht/result_server/pathways/WP1946_42206.gpml"));
	File htmlPath = new File("/home/rai/Desktop/maastricht/result_server/exporter_results");
	htmlexpo.exportAll(pwFiles, htmlPath, gdb, visMgr, gexMgr);
	//return "It works check results in exporter results";
	return visMgr.getActiveVisualization().toString();
	}
}
