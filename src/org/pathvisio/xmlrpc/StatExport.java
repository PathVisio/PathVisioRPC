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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.construct.DataDerby;
import org.pathvisio.core.Engine;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.FileUtils;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.gex.SimpleGex;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.desktop.visualization.Visualization;
import org.pathvisio.gexplugin.GexPlugin;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.htmlexport.statistics.StatisticsExporter;
import org.pathvisio.statistics.Column;
import org.pathvisio.statistics.StatisticsPathwayResult;
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

@SuppressWarnings("deprecation")
public class StatExport {

	PathwayGpml path = new PathwayGpml();
	DataImport dat = new DataImport();


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
		String resultDir = statExportHtml(gexfile, dbDir, pathdir, exprz,
				resultdir, false, "");
		return resultDir;
	}

	protected StatisticsResult getPathwayStatList(String gexfile,
			String dbfile,
			String pathwaydir, String exprZ, String resultdir) {
		resultdir = path.checkResultDir(resultdir);
		File gexFile = new File(gexfile);
		File inPath = new File(pathwaydir);
		PreferenceManager.init();
		BioDataSource.init();
		StatisticsResult result = null;
		try {
			List<File> gpmlFiles = FileUtils.getFiles(inPath, "gpml", true);
			if (gpmlFiles.size() == 0) {
				Logger.log.error("No GPML files found in " + inPath);
			}

			dat.idmapperLoader(dbfile);
			Engine engine = new Engine();
			PvDesktop pvDesktop = new PvDesktop(new SwingEngine(engine), null);

			GexPlugin gexPlugin = new GexPlugin();
			gexPlugin.init(pvDesktop);

			VisualizationPlugin visPlugin = new VisualizationPlugin();
			visPlugin.init(pvDesktop);

			StatisticsPlugin plugin = new StatisticsPlugin();
			plugin.init(pvDesktop);

			SimpleGex gex = new SimpleGex("" + gexFile, false, new DataDerby());
			pvDesktop.getGexManager().setCurrentGex(gex);

			Criterion criteria = new Criterion();
			criteria.setExpression(exprZ, gex.getSampleNames());
			ZScoreCalculator zsc = new ZScoreCalculator(criteria, inPath,
					pvDesktop.getGexManager().getCachedData(),
					dat.getLoadedGdbs(), null);
			result = zsc.calculateMappFinder();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return result;
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
	protected String statExportHtml(String gexfile, String dbDir,
			String pathdir, String exprz, String resultdir, boolean fromWeb,
			String webAddress) {

		File gexFile = new File(gexfile);
		File inPath = new File(pathdir);
		File outPath = new File(resultdir);
		String visName = "Visualization";
		PreferenceManager.init();
		BioDataSource.init();

		try {
			List<File> gpmlFiles = FileUtils.getFiles(inPath, "gpml", true);
			if (gpmlFiles.size() == 0) {
				Logger.log.error("No GPML files found in " + inPath);
			}
			// path.loadGdbs(dbDir);

			dat.idmapperLoader(dbDir);

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
					dat.getLoadedGdbs(), null);
			StatisticsResult result = zsc.calculateMappFinder();

			StatisticsExporter exporter = new StatisticsExporter(
					dat.getLoadedGdbs(), pvDesktop.getVisualizationManager(),
					pvDesktop.getGexManager().getCurrentGex());
			exporter.export(outPath, result, pvDesktop
					.getVisualizationManager().getActiveVisualization(),
					fromWeb, webAddress, null);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return resultdir;
	}

	protected String createStatFile(StatisticsResult result, String resultdir) {
		File outPath = new File(resultdir + File.separator
				+ "PathwayStatList.txt");
		try {
			outPath.createNewFile();

			FileWriter writer = new FileWriter(outPath);
			writer.write("Pathway" + "\t" + "Positive[r]" + "\t"
					+ "Measured[n]" + "\t" + "Total" + "\t" + "Z Score"
					+ "\n");
			for(StatisticsPathwayResult r : result.getPathwayResults()) {
				writer.write(r.getProperty(Column.PATHWAY_NAME) + "\t"
						+ r.getProperty(Column.R) + "\t"
						+ r.getProperty(Column.N) + "\t"
						+ r.getProperty(Column.TOTAL) + "\t" + r.getZScore()
						+ "\n");
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultdir;
	}

}
