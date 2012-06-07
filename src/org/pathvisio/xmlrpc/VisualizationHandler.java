package org.pathvisio.xmlrpc;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.pathvisio.core.Engine;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.gex.Sample;
import org.pathvisio.desktop.visualization.ColorRule;
import org.pathvisio.desktop.visualization.ColorSet;
import org.pathvisio.desktop.visualization.ColorSetManager;
import org.pathvisio.desktop.visualization.Visualization;
import org.pathvisio.desktop.visualization.VisualizationManager;
import org.pathvisio.visualization.plugins.ColorByExpression;
import org.pathvisio.visualization.plugins.DataNodeLabel;

/**
 * 
 * @author anwesha, magali
 *
 */
public class VisualizationHandlermagali {
	
	/**
	 * Create the colorSets and apply them as methods.
	 * A color set can contain one gradient or one or several color rules applied to the same 
	 * variables and consistent. 
	 * 
	 * @param gColorSet	is a string representing a list of unique sample names (file headers), 
	 * on which a gradient is applied. Sample names are separated by ";" 
	 * @param gColor is a string representing a list of list of colors: 2 to 3 colors are applied to 
	 * each gradient. The first separator ";" splits the groups of colors belonging to the same 
	 * gradient and the second "," splits the colors for each given gradient.
	 * @param gValue is a string representing a list of list of values corresponding to gColor and 
	 * is organized the same way.
	 * 
	 * @param rColorSet	is a string representing a list of unique ids. These color sets may contain
	 * one or several color rules. The color set separator is ";". 
	 * @param rColor is a string representing a list of list of colors: for each rule, only one color
	 * is set but many rules may be defined for one color set. The first separator ";" splits the 
	 * groups of colors belonging to the same color set the second "," splits the colors for each 
	 * given rule.
	 * @param rExpr is a string representing a list of list of boolean expression corresponding to 
	 * rColor and is organized the same way. 
	 */
	public String createVisualization(String gexFile , 
			String gColorSet, String gColor, String gValue,
			String rColorSet, String rColor, String rExpr)
					throws IDMapperException, ConverterException, IOException, SecurityException, 
					NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, 
					IllegalAccessException {
		
		// Initialization
		PreferenceManager.init();
		
		//Create engine to load pathway
		Engine engine = new Engine ();
		GexManager gex = new GexManager();
		gex.setCurrentGex(gexFile,false);

		File xmlFile = new File(gex.getCurrentGex().getDbName() + ".xml");
		xmlFile.delete();
		
		// Creating visualization manager and adding visualization, colorset manager and 
		// visualization method registry
		VisualizationManager visman = new VisualizationManager(engine, gex);
		ColorSetManager colsetmgr = visman.getColorSetManager();

		//Creating Visualisation Method -- Color by expression
		ColorByExpression cbe = new ColorByExpression(gex, colsetmgr);		

		// Define Gradients and add to the ColorByExpression method
		String[] gSample;
		String[] gColorNames;
		String[] gValues;
		
		if(gColorSet.contains(";")) {
			gSample = gColorSet.split(";");
			gColorNames = gColor.split(";");
			gValues = gValue.split(";");
		} else {
			gSample = new String[1];
			gSample[0] = gColorSet;
			gColorNames = new String[1];
			gColorNames[0] = gColor;
			gValues = new String[1];
			gValues[0] = gValue;
		}
		
		for(int gcount = 0; gcount < gSample.length; gcount = gcount+1 ){
			GradientVis grevis = new GradientVis();
			ColorSet csG = grevis.createGradient(gcount, gColorNames[gcount], 
					gValues[gcount], gSample[gcount]);
			colsetmgr.addColorSet(csG);
			Sample sG = gex.getCurrentGex().findSample(gSample[gcount]);
			cbe.addUseSample(sG);
			cbe.getConfiguredSample(sG).setColorSet(csG);
		}

		// Define color rules and add to the ColorByExpression method
		String[] rSample;
		String[] rColorNames; 
		String[] rExprs; 
		
		if(rColorSet.contains(";")) {
			rSample = rColorSet.split(";");
			rColorNames = rColor.split(";");
			rExprs = rExpr.split(";");
		} else {
			rSample = new String[1];
			rSample[0] = rColorSet;
			rColorNames = new String[1];
			rColorNames[0] = rColor;
			rExprs = new String[1];
			rExprs[0] = rExpr;			
		}
		
		for(int rcount = 0; rcount < rSample.length; rcount = rcount+1) {
			
			ColorSet csR = new ColorSet(rSample[rcount]);		
			String[] cols; 
			String[] exprs; 
			
			if(rColorNames[rcount].contains(",")) {
				cols = rColorNames[rcount].split(",");
				exprs = rExprs[rcount].split(",");
			} else {
				cols = new String[1];
				cols[0] = rColorNames[rcount];
				exprs = new String[1];
				exprs[0] = rExprs[rcount];		
			}
		
			if (cols.length != exprs.length) {
				throw new IllegalArgumentException("color and expression lists are not of the " +
						"same length for color set " + rColorNames[rcount]);
			} else {
				for (int i = 0; i < cols.length; i++) {
					//Creating and setting colorrule
					ColorRule cr = new ColorRule(); 
					Color color = Color.decode(cols[i]);
					cr.setColor(color);
					List<String> al;
					al = gex.getCurrentGex().getSampleNames();
					String msg = cr.setExpression(exprs[i],al);
					if (msg != null) throw new IOException(msg);
					csR.addRule(cr);
				}
			}
			
			colsetmgr.addColorSet(csR);	
			Sample sR = createSample(cbe, rExprs[rcount], gex); 
			cbe.addUseSample(sR);
			cbe.getConfiguredSample(sR).setColorSet(csR);
		}
		
		// Add the visualization methods
		DataNodeLabel dnl = new DataNodeLabel();
		
		//Creating visualization and adding visualizationmethod
		Visualization vis = new Visualization("Visualization");		
		vis.addMethod(cbe);
		vis.addMethod(dnl);
		vis.setShowLegend(true);	
		visman.addVisualization(vis);
		visman.saveXML();
		
		return "Visualization created!";
	}
	
	/**
	 * TODO: work around - color rule has to be linked to a variable
	 * what if I need two color rules for one variable - this is not 
	 * possible - now we use the next available variable although
	 * it might not be part of the color rule
	 */
	private Sample createSample(ColorByExpression cbe, String rExpr, GexManager gex)
			throws IDMapperException {

		// Inspired from the boolean expression parser by Martijn	
		Sample sample = new Sample(0, "noSample");
		String input = rExpr;
		String value = "";
		int token = 0;
		
		while(input.length() > 0){
			char ch = input.charAt(0);
			input = input.substring(1);
			if(ch == '['){	
				token = 1;
				value = "";
			}
			if (ch != ']' && ch != '[' && token == 1){
				value += ch;
			}
			if(ch == ']'){	
				token = 0;
				sample = gex.getCurrentGex().findSample(value);
				// if not already used as a sample
				if(cbe.getConfiguredSample(sample) == null){ 
					return sample;
				}
			}
		}
		// else, use an arbitrary sample name...
		List<Sample> allSampleList = gex.getCurrentGex().getOrderedSamples();
		int sampleNb = allSampleList.size();
		for (int i = 0 ; i<sampleNb;i++){
			sample = allSampleList.get(i);
			List<ColorByExpression.ConfiguredSample> confSampleList = cbe.getConfiguredSamples();
			int confSampleNb = confSampleList.size();
			int bool = 1;
			for (int j = 0 ; j<confSampleNb;j++){
				if(sample == confSampleList.get(j).getSample()){
					bool = bool*0; // the sample is already used
				}
			}
			if(bool == 1){
				return sample;	
			}
		}
		return sample;
	}
}
