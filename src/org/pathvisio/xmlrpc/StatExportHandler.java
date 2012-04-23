package org.pathvisio.xmlrpc;

import java.io.File;
import java.util.List;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.construct.DataDerby;
import org.pathvisio.core.Engine;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.FileUtils;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.gex.SimpleGex;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.desktop.visualization.Visualization;
import org.pathvisio.desktop.visualization.VisualizationManager;
import org.pathvisio.gexplugin.GexPlugin;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.htmlexport.statistics.StatisticsExporter;
import org.pathvisio.statistics.StatisticsPlugin;
import org.pathvisio.statistics.StatisticsResult;
import org.pathvisio.statistics.ZScoreCalculator;
import org.pathvisio.visualization.plugins.VisualizationPlugin;

/**
 * 
 * @author anwesha, magali
 *
 */
public class StatExportHandler {
	
	// call locally from R or GenePattern
	public String xportInfo (String gexFile, String dbFile, String pathDir, String exprZ, String output) throws Exception {
		return xportInfo(gexFile, dbFile, pathDir, exprZ, output, false, "");
	}
	
	// call from a web portal
	public String xportInfo (String gexFile, String dbFile, String pathDir, String exprZ, String output, boolean fromWeb, String webAddress) throws Exception {
		File gdbFile = new File(dbFile);
		File gexfile = new File(gexFile);
		File inPath = new File(pathDir);
		File outPath = new File(output);
		
		// reference name for vis created by VisualizationXml.java
		String visName = "Visualization"; 
		String crit = exprZ;
		
		BioDataSource.init();
		List<File> gpmlFiles = FileUtils.getFiles(inPath, "gpml", true);
		
		if(gpmlFiles.size() == 0) {
			Logger.log.error("No GPML files found in " + inPath);
		}
		
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		IDMapper gdb = BridgeDb.connect ("idmapper-pgdb:" + gdbFile);
		
		Engine engine = new Engine();
		PvDesktop pvDesktop = new PvDesktop(new SwingEngine(engine), null);

		// start the main plugins visualization, gex and statistics
		VisualizationPlugin visplugin = new VisualizationPlugin();
		visplugin.init(pvDesktop);

		GexPlugin gexplugin = new GexPlugin();
		gexplugin.init(pvDesktop);

		StatisticsPlugin statplugin = new StatisticsPlugin();
		statplugin.init(pvDesktop);

		SimpleGex gex = new SimpleGex("" + gexfile, false, new DataDerby());

		pvDesktop.getGexManager().setCurrentGex(gex);
		GexManager gexMgr = pvDesktop.getGexManager();
		VisualizationManager visMgr = pvDesktop.getVisualizationManager();

		for(Visualization v : pvDesktop.getVisualizationManager().getVisualizations()) {
			if(v.getName().equals(visName)) {
				pvDesktop.getVisualizationManager().setActiveVisualization(v);
			}
		}
		
		Criterion criteria = new Criterion ();
		criteria.setExpression(crit, gex.getSampleNames());

		ZScoreCalculator zsc = new ZScoreCalculator(criteria,inPath,gexMgr.getCachedData(),gdb,null);
		StatisticsResult result = zsc.calculateMappFinder();
		StatisticsExporter exporter = new StatisticsExporter(gdb, visMgr,gex);

		exporter.export(outPath, result, pvDesktop.getVisualizationManager().getActiveVisualization(),
				fromWeb, webAddress, null);
		
		return "Results exported!";
	}
}
