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
import org.pathvisio.visualization.VisualizationMethod;
import org.pathvisio.visualization.VisualizationMethodProvider;
import org.pathvisio.visualization.VisualizationMethodRegistry;
import org.pathvisio.visualization.plugins.ColorByExpression;
import org.pathvisio.visualization.plugins.DataNodeLabel;
import org.pathvisio.visualization.plugins.TextByExpression;


public class exporter {
	@SuppressWarnings("static-access")
public String exportInfo() throws ClassNotFoundException, IDMapperException, ConverterException, IOException{
	PreferenceManager.init();
	
	Class.forName("org.bridgedb.rdb.IDMapperRdb");
	IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + "/home/rai/Desktop/maastricht/result_server/Hs_Derby_20090720.bridge");
	Engine engine = new Engine ();
	final GexManager gexMgr = new GexManager();
	final VisualizationManager visMgr = new VisualizationManager(engine, gexMgr);
	VisualizationMethodRegistry reg = visMgr.getVisualizationMethodRegistry();
	
	reg.registerMethod(
			ColorByExpression.class.toString(),
			new VisualizationMethodProvider() {
				public VisualizationMethod create() {
					return new ColorByExpression(gexMgr, 
							visMgr.getColorSetManager());
				}
		}
	);
	reg.registerMethod(
			TextByExpression.class.toString(),
			new VisualizationMethodProvider() {
				public VisualizationMethod create() {
					return new TextByExpression(gexMgr);
				}
		}
	);
	reg.registerMethod(
			DataNodeLabel.class.toString(),
			new VisualizationMethodProvider() {
				public VisualizationMethod create() {
					return new DataNodeLabel();
				}
		}
	);

	gexMgr.setCurrentGex("/home/rai/Desktop/maastricht/result_server/feb_21.txt.pgex",false);
	gexMgr.getCachedData().setMapper(gdb);

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
