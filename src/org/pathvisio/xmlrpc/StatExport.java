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

import java.io.File;
import java.util.List;

import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.construct.DataDerby;
import org.pathvisio.core.Engine;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.FileUtils;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.gex.SimpleGex;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.desktop.visualization.Visualization;
import org.pathvisio.desktop.visualization.VisualizationManager;
import org.pathvisio.gexplugin.GexPlugin;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.htmlexport.plugin.HtmlExporter;
import org.pathvisio.htmlexport.statistics.StatisticsExporter;
import org.pathvisio.statistics.StatisticsPlugin;
import org.pathvisio.statistics.StatisticsResult;
import org.pathvisio.statistics.ZScoreCalculator;
import org.pathvisio.visualization.plugins.VisualizationPlugin;

/**
 * Performs pathway statistics and exports results which include data visualized
 * pathways images, backpages and a hyperlinked file containing a list of
 * pathways sorted according to their z score.
 * 
 * @author anwesha
 * @author magali
 * 
 */

public class StatExport {

	PathwayGpml path = new PathwayGpml();

	/**
	 * Call locally from R or GenePattern
	 * 
	 * @param gexFile
	 * @param dbFile
	 * @param pathDir
	 * @param exprZ
	 * @param output
	 * @return
	 * @throws Exception
	 */
	protected String calculatePathwayStatistics(String gexfile, String dbDir,
			String pathdir, String exprz, String resultdir) throws Exception {
		return xportInfo(gexfile, dbDir, pathdir, exprz, resultdir, false, "");
	}

	/**
	 * Call from a web portal
	 * 
	 * @param gexFile
	 * @param dbFile
	 * @param pathDir
	 * @param exprZ
	 * @param output
	 * @param fromWeb
	 * @param webAddress
	 * @return
	 * @throws Exception
	 */
	protected String xportInfo(String gexfile, String dbDir, String pathdir,
			String exprz, String resultdir, boolean fromWeb, String webAddress) {

		PathwayGpml path = new PathwayGpml();
		if (resultdir.length() == 0) {
			resultdir = path.createResultDir();
		} else {
			if (!(new File(resultdir).exists())) {
				resultdir = path.createResultDir();
			}
		}

		File gexFile = new File(gexfile);
		File inPath = new File(pathdir);
		File outPath = new File(resultdir);
		String visName = "Visualization";
		PreferenceManager.init();
		BioDataSource.init();

		try {
			List<File> gpmlFiles = FileUtils.getFiles(inPath, "gpml", true);
			if (gpmlFiles.size() == 0)
				Logger.log.error("No GPML files found in " + inPath);

			path.loadGdbs(dbDir);

			Engine engine = new Engine();
			PvDesktop pvDesktop = new PvDesktop(new SwingEngine(engine), null);

			VisualizationPlugin visPlugin = new VisualizationPlugin();
			visPlugin.init(pvDesktop);

			GexPlugin gexPlugin = new GexPlugin();
			gexPlugin.init(pvDesktop);

			StatisticsPlugin plugin = new StatisticsPlugin();
			plugin.init(pvDesktop);

			SimpleGex gex = new SimpleGex("" + gexFile, false, new DataDerby());
			pvDesktop.getGexManager().setCurrentGex(gex);

			for (Visualization v : pvDesktop.getVisualizationManager()
					.getVisualizations()) {
				if (v.getName().equals(visName)) {
					pvDesktop.getVisualizationManager().setActiveVisualization(
							v);
				}
			}

			Criterion criteria = new Criterion();
			criteria.setExpression(exprz, gex.getSampleNames());

			ZScoreCalculator zsc = new ZScoreCalculator(criteria, inPath,
					pvDesktop.getGexManager().getCachedData(),
					PathwayGpml.loadedGdbs, null);
			StatisticsResult result = zsc.calculateMappFinder();

			StatisticsExporter exporter = new StatisticsExporter(
					PathwayGpml.loadedGdbs,
					pvDesktop.getVisualizationManager(), pvDesktop
							.getGexManager().getCurrentGex());
			exporter.export(outPath, result, pvDesktop
					.getVisualizationManager().getActiveVisualization(),
					fromWeb, webAddress, null);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return resultdir;
	}

	protected void visualizeData(String gexname, String dbDir, Pathway pathway,
			File output) throws Exception {

		File gexfile = new File(gexname);

		String visName = "Visualization";
		PreferenceManager.init();
		BioDataSource.init();

		path.loadGdbs(dbDir);

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

		for (Visualization v : pvDesktop.getVisualizationManager()
				.getVisualizations()) {
			if (v.getName().equals(visName)) {
				pvDesktop.getVisualizationManager().setActiveVisualization(v);
			}
		}

		gexMgr.getCachedData().setMapper(PathwayGpml.loadedGdbs);

		HtmlExporter exporter = new HtmlExporter(PathwayGpml.loadedGdbs,
				visMgr, gexMgr);
		exporter.doExport(pathway, pathway.getMappInfo().getMapInfoName(),
				output);
	}

}
