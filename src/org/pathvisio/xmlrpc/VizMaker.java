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
import java.util.Arrays;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.pathvisio.core.Engine;
import org.pathvisio.core.preferences.GlobalPreference;
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
public class VizMaker {


	protected void setDefaultColors(String noCritMet, String noDatFound)
			throws SecurityException, IllegalArgumentException {
		PreferenceManager.init();
		if(!noCritMet.isEmpty()) {
			Color color = getColors(noCritMet);
			PreferenceManager.getCurrent().setColor(GlobalPreference.COLOR_NO_CRIT_MET, color);
		}
		if (!noDatFound.isEmpty()) {
			Color color = getColors(noDatFound);
			PreferenceManager.getCurrent().setColor(
					GlobalPreference.COLOR_NO_DATA_FOUND, color);
		}
	}

	protected String createVisualizationNode(String gexFile, String gsample,
			String gcolors, String gvalues, String rexpressions, String rcolors)
					throws IDMapperException, IOException, SecurityException,
					NoSuchFieldException,
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
			if (gsample.length() > 2) {
				ColorSet cs1 = new ColorSet("colorsetNodeByHex");
				ColorGradient cg = new ColorGradient();// Creating and setting
				// gradient
				String[] cols = gcolors.split(",");
				String[] valis = gvalues.split(",");
				int vals[] = new int[valis.length];
				for (int k = 0; k < valis.length; k = k + 1) {
					vals[k] = Integer.parseInt(valis[k]);
				}
				for (int n = 0; n < cols.length; n = n + 1) {
					Color color = getColors(cols[n]);
					cg.addColorValuePair(new ColorValuePair(color, vals[n]));
				}
				cs1.setGradient(cg);
				colsetmgr.addColorSet(cs1);
				ISample s1 = gex.getCurrentGex().findSample(gsample);
				cbe.addUseSample(s1);
				cbe.getConfiguredSample(s1).setColorSet(cs1);
			}
		}

		if (rexpressions.contains(";")) {
			// System.out.println(sample);
			List<String> al = gex.getCurrentGex().getSampleNames();
			System.out.println(al);
			String[] sample2 = rexpressions.split(";");
			System.out.println(Arrays.toString(sample2));
			String[] colrNames1 = rcolors.split(";");
			String[] expr = rexpressions.split(";");
			for (int rcount = 0; rcount < sample2.length; rcount = rcount + 1) {
				RuleVis rulvis = new RuleVis();
				ColorSet cs2 = rulvis.createRule(rcount, gexFile,
						expr[rcount],
						colrNames1[rcount]);
				int startIndex = expr[rcount].indexOf("[");
				int endIndex = expr[rcount].indexOf("]");
				String sample = expr[rcount]
						.substring(startIndex + 1, endIndex);
				colsetmgr.addColorSet(cs2);
				ISample s2 = gex.getCurrentGex().findSample(sample);
				cbe.addUseSample(s2);
				cbe.getConfiguredSample(s2).setColorSet(cs2);
			}
		} else {
			if (rexpressions.length() > 2) {
				ColorSet cs2 = new ColorSet("colorsetByHex-1");
				ColorRule cr = new ColorRule(); // Creating and setting
				// colorrule
				Color color = getColors(rcolors);
				cr.setColor(color);
				List<String> al;
				al = gex.getCurrentGex().getSampleNames();
				// System.out.println(al);
				String msg = cr.setExpression(rexpressions, al);
				if (msg != null)
					throw new IOException(msg);
				cs2.addRule(cr);
				colsetmgr.addColorSet(cs2);
				int startIndex = rexpressions.indexOf("[");
				int endIndex = rexpressions.indexOf("]");
				String sample = rexpressions
						.substring(startIndex + 1, endIndex);
				ISample s2 = gex.getCurrentGex().findSample(sample);
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

	protected Color getColors(String codeName) {
		Color clr = Color.WHITE;
		if (codeName.startsWith("#")) {
			clr = Color.decode(codeName);
		} else {
			java.lang.reflect.Field field;
			try {
				field = Class.forName("java.awt.Color").getField(codeName);
				clr = (Color) field.get(null);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return clr;

	}
}
