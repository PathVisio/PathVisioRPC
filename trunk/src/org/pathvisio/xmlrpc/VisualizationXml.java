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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.pathvisio.core.Engine;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.data.DataException;
import org.pathvisio.data.ISample;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.visualization.ColorGradient;
import org.pathvisio.desktop.visualization.ColorGradient.ColorValuePair;
import org.pathvisio.desktop.visualization.ColorRule;
import org.pathvisio.desktop.visualization.ColorSet;
import org.pathvisio.desktop.visualization.ColorSetManager;
import org.pathvisio.desktop.visualization.Visualization;
import org.pathvisio.desktop.visualization.VisualizationManager;
import org.pathvisio.visualization.plugins.ColorByExpression;
import org.pathvisio.visualization.plugins.DataNodeLabel;

/**
 * Creates and saves visualizations, i.e Color Rules and Color Gradients for
 * visualizing data and saves them in an XML file which is associated with the
 * pgex file.
 * 
 * @author anwesha
 * 
 */
public class VisualizationXml {

	protected String createVisualizationNode(String gexFile, String gsample,
			String gcolors, String gvalues, String rsample, String rcolors,
			String rexpressions) throws IDMapperException, ConverterException,
			IOException, SecurityException, NoSuchFieldException,
			ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, DataException {
		PreferenceManager.init();

		// Create engine to load pathway
		Engine engine = new Engine();

		// Initiate GexManager for data
		GexManager gex = new GexManager();
		gex.setCurrentGex(gexFile, false);

		File xmlFile = new File(gex.getCurrentGex().getDbName() + ".xml");
		xmlFile.delete();

		// Inititiate VisualizationManager for visualization
		VisualizationManager visman = new VisualizationManager(engine, gex);
		ColorSetManager colsetmgr = visman.getColorSetManager();

		// Creating Visualisation Methods
		ColorByExpression cbe = new ColorByExpression(gex, colsetmgr);

		if (gsample.contains(";")) {
			String[] sample1 = gsample.split(";");
			String[] colorNames1 = gcolors.split(";");
			String[] values1 = gvalues.split(";");
			for (int gcount = 0; gcount < sample1.length; gcount = gcount + 1) {
				GradientVis grevis = new GradientVis();
				ColorSet cs1 = grevis.createGradient(gcount,
						colorNames1[gcount], values1[gcount]);
				colsetmgr.addColorSet(cs1);
				ISample s1 = gex.getCurrentGex().findSample(sample1[gcount]);
				cbe.addUseSample(s1);
				cbe.getConfiguredSample(s1).setColorSet(cs1);
			}
		} else {
			if (gsample.length() > 1) {
				ColorSet cs1 = new ColorSet("colorsetNode");
				ColorGradient cg = new ColorGradient();// Creating and setting
														// gradient
				String[] cols = gcolors.split(",");
				String[] valis = gvalues.split(",");
				int vals[] = new int[valis.length];
				for (int k = 0; k < valis.length; k = k + 1) {
					vals[k] = Integer.parseInt(valis[k]);
				}
				for (int n = 0; n < cols.length; n = n + 1) {
					java.lang.reflect.Field field = Class.forName(
							"java.awt.Color").getField(cols[n]);
					Color color = (Color) field.get(null);
					cg.addColorValuePair(new ColorValuePair(color, vals[n]));
				}
				cs1.setGradient(cg);
				colsetmgr.addColorSet(cs1);
				ISample s1 = gex.getCurrentGex().findSample(gsample);
				cbe.addUseSample(s1);
				cbe.getConfiguredSample(s1).setColorSet(cs1);
			}
		}

		if (rsample.contains(";")) {
			String[] sample2 = rsample.split(";");
			String[] colrNames1 = rcolors.split(";");
			String[] expr = rexpressions.split(";");
			for (int rcount = 0; rcount < sample2.length; rcount = rcount + 1) {
				RuleVis rulvis = new RuleVis();
				ColorSet cs2 = rulvis.createRule(rcount, gexFile,
						colrNames1[rcount], expr[rcount]);
				colsetmgr.addColorSet(cs2);
				ISample s2 = gex.getCurrentGex().findSample(sample2[rcount]);
				cbe.addUseSample(s2);
				cbe.getConfiguredSample(s2).setColorSet(cs2);
			}
		} else {
			if (rsample.length() > 1) {
				ColorSet cs2 = new ColorSet("colorset-1");
				ColorRule cr = new ColorRule(); // Creating and setting
												// colorrule
				java.lang.reflect.Field feld = Class.forName("java.awt.Color")
						.getField(rcolors);
				Color colour = (Color) feld.get(null);
				cr.setColor(colour);
				List<String> al;
				al = gex.getCurrentGex().getSampleNames();
				System.out.println(al);
				String msg = cr.setExpression(rexpressions, al);
				if (msg != null)
					throw new IOException(msg);
				cs2.addRule(cr);
				colsetmgr.addColorSet(cs2);
				ISample s2 = gex.getCurrentGex().findSample(rsample);
				cbe.addUseSample(s2);
				cbe.getConfiguredSample(s2).setColorSet(cs2);
			}
		}
		DataNodeLabel dnl = new DataNodeLabel();

		// Creating visualization and adding visualizationmethod
		Visualization vis = new Visualization("Visualization");
		vis.addMethod(cbe);
		vis.addMethod(dnl);
		// vis.setShowLegend(true);
		visman.addVisualization(vis);
		visman.saveXML();

		return "Visualization created!";
	}

	// /**
	// * Function to create visualization for lines
	// * @param gexFile
	// * @param lsample
	// * @param poscol
	// * @param negcol
	// * @param minthick
	// * @param maxthick
	// * @param mindata
	// * @param maxdata
	// * @return
	// * @throws DataException
	// * @throws IllegalArgumentException
	// * @throws SecurityException
	// * @throws IllegalAccessException
	// * @throws NoSuchFieldException
	// * @throws ClassNotFoundException
	// * @throws IOException
	// */
	// public String createVisualizationLine(String gexFile ,
	// String lsample, String poscol, String negcol,
	// String minthick, String maxthick,
	// String mindata, String maxdata)
	// throws DataException, IllegalArgumentException,
	// SecurityException, IllegalAccessException, NoSuchFieldException,
	// ClassNotFoundException, IOException
	// {
	// PreferenceManager.init();
	//
	// //Create engine to load pathway
	// Engine engine = new Engine ();
	//
	// //Initiate GexManager for data
	// GexManager gexmgr = new GexManager();
	// gexmgr.setCurrentGex(gexFile,false);
	//
	// File xmlFile = new File(gexmgr.getCurrentGex().getDbName() + ".xml");
	// xmlFile.delete();
	//
	// //Inititiate VisualizationManager for visualization
	// VisualizationManager vismgr = new VisualizationManager(engine, gexmgr);
	// ColorSetManager colsetmgr = vismgr.getColorSetManager();
	//
	// //Creating Visualisation Methods
	// ColorByLine cbl = new ColorByLine(gexmgr, colsetmgr);
	//
	// if(lsample.length() > 1){
	// ColorSet cs1 = new ColorSet("colorsetEdge");
	// cbl.setPosColor((Color)(Class.forName("java.awt.Color").getField(poscol)).get(null));
	// cbl.setNegColor((Color)(Class.forName("java.awt.Color").getField(negcol)).get(null));
	// cbl.setMinData(mindata);
	// cbl.setMaxData(maxdata);
	// cbl.setMinThickness(minthick);
	// cbl.setMaxThickness(maxthick);
	// colsetmgr.addColorSet(cs1);
	// System.out.println(gexmgr.getCurrentGex().getSampleNames());
	// ISample s1 = gexmgr.getCurrentGex().findSample(lsample);
	// // System.out.println(s1.getName());
	// cbl.addUseSample(s1);
	// cbl.getConfiguredSample(s1).setColorSet(cs1);
	// // cbl.addUseSample(s1);
	// // cbl.getConfiguredSample(s1).setColorSet(cs1);
	// }
	//
	//
	// LineLabel ll = new LineLabel();
	//
	// //Creating visualization and adding visualizationmethod
	// Visualization vis = new Visualization("Visualization");
	// vis.addMethod(cbl);
	// vis.addMethod(ll);
	// // vis.setShowLegend(true);
	// vismgr.addVisualization(vis);
	// vismgr.saveXML();
	//
	// return "Visualization created!";
	// }
}
