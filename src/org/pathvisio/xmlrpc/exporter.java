package org.pathvisio.xmlrpc;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.pathvisio.Engine;
import org.pathvisio.gex.GexManager;
import org.pathvisio.model.ConverterException;
import org.pathvisio.plugins.HtmlExporter;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.visualization.VisualizationManager;
import org.pathvisio.visualization.VisualizationMethod;
import org.pathvisio.visualization.VisualizationMethodProvider;
import org.pathvisio.visualization.VisualizationMethodRegistry;
import org.pathvisio.visualization.plugins.ColorByExpression;
import org.pathvisio.visualization.plugins.DataNodeLabel;
import org.pathvisio.visualization.plugins.TextByExpression;


public class exporter {
	@SuppressWarnings("static-access")
public String exportInfo(String gexFileName, String dbFileName, String pathDirName, String outputDirName) throws ClassNotFoundException, IDMapperException, ConverterException, IOException{
	PreferenceManager.init();
	
	Class.forName("org.bridgedb.rdb.IDMapperRdb");
	String dbFile = dbFileName;
	IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
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
	String gexFile = gexFileName;
	gexMgr.setCurrentGex(gexFile,false);
	gexMgr.getCachedData().setMapper(gdb);

	HtmlExporter htmlexpo = new HtmlExporter(gdb, visMgr, gexMgr);
	String pathDir = pathDirName;
	File pathwayDir = new File(pathDir);
	String[] pathways = pathwayDir.list();
	List<File> pwFiles = new ArrayList<File>();
	for(int i = 0 ; i < pathways.length; i++)
	{
		pwFiles.add	(new File(pathDir+"/"+pathways[i]));
		}
	String outputDir = outputDirName;
	File htmlPath = new File(outputDir);
	htmlexpo.exportAll(pwFiles, htmlPath, gdb, visMgr, gexMgr);
	return "It works check results in exporter results";
	}
}