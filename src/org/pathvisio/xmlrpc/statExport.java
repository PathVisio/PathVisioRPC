package org.pathvisio.xmlrpc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.construct.DataDerby;
import org.pathvisio.Engine;
import org.pathvisio.debug.Logger;
import org.pathvisio.gex.GexManager;
import org.pathvisio.gex.SimpleGex;
import org.pathvisio.gui.swing.PvDesktop;
import org.pathvisio.gui.swing.SwingEngine;
import org.pathvisio.model.ConverterException;
import org.pathvisio.plugin.PluginManager;
import org.pathvisio.plugins.statistics.StatisticsExporter;
import org.pathvisio.plugins.statistics.StatisticsResult;
import org.pathvisio.plugins.statistics.ZScoreCalculator;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.util.FileUtils;
import org.pathvisio.visualization.Visualization;
import org.pathvisio.visualization.VisualizationManager;
import org.pathvisio.visualization.colorset.Criterion;

public class statExport {
	public String xportInfo (String gexFile, String dbFile, String pathDir, String exprZ, String output) throws IDMapperException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, ConverterException{
		File gdbFile = new File(dbFile);
		File gexfile = new File(gexFile);
		File inPath = new File(pathDir);
		File outPath = new File(output);
		String visName = "Visualization";
		List<String> plugins = new ArrayList<String>();
		plugins.add("/home/rai/workspace/pathvisio/pathvisio/visplugins.jar");
		String crit = exprZ;
		
		PreferenceManager.init();
		BioDataSource.init();
		List<File> gpmlFiles = FileUtils.getFiles(inPath, "gpml", true);
		if(gpmlFiles.size() == 0) Logger.log.error("No GPML files found in " + inPath);
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		IDMapper gdb = BridgeDb.connect ("idmapper-pgdb:" + gdbFile);
		Engine engine = new Engine();
		PvDesktop pvDesktop = new PvDesktop(new SwingEngine(engine));
		PluginManager pluginManager = new PluginManager(plugins, pvDesktop);
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
		exporter.export(outPath, result, pvDesktop.getVisualizationManager().getActiveVisualization());
		return "works zsc!!";
		}
	}


