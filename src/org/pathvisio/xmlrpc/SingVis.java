package org.pathvisio.xmlrpc;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.bridgedb.IDMapperException;
import org.pathvisio.Engine;
import org.pathvisio.gex.GexManager;
import org.pathvisio.gex.Sample;
import org.pathvisio.model.ConverterException;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.visualization.VisualizationManager;
import org.pathvisio.visualization.Visualization;
import org.pathvisio.visualization.colorset.ColorGradient;
import org.pathvisio.visualization.colorset.ColorRule;
import org.pathvisio.visualization.colorset.ColorGradient.ColorValuePair;
import org.pathvisio.visualization.colorset.ColorSet;
import org.pathvisio.visualization.colorset.ColorSetManager;
import org.pathvisio.visualization.plugins.ColorByExpression;
import org.pathvisio.visualization.plugins.DataNodeLabel;


public class SingVis implements XmlRpcHandlerMapping {
	
	//For reading expression dataset
	public static final int TYPE_GEX = 1;
	
	public String createVisualization(String gexFile , String colNames, String values, String colr, String xpression, String Gsam, String Rsam) throws IDMapperException, ConverterException, IOException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		
		// setup
		PreferenceManager.init();
		Engine engine = new Engine ();//Create engine to load pathway
		GexManager gex = new GexManager();
		gex.setCurrentGex(gexFile,false);

		File xmlFile = new File(gex.getCurrentGex().getDbName() + ".xml");
		xmlFile.delete();
		
		VisualizationManager visman = new VisualizationManager(engine, gex);//Creating visualizationmanager and adding visualization,colorsetmanager and visualizationmethodregistry
		ColorSetManager colsetmgr = visman.getColorSetManager();
		ColorByExpression cbe = new ColorByExpression(gex, colsetmgr);//Creating Visualisation Method -- Colour by expression
		// Creating new colorset 
		if(Gsam != null){
			ColorSet cs1 = new ColorSet("colorset");
			ColorGradient cg = new ColorGradient();//Creating and setting gradient
			String[] cols = colNames.split(",");
			String[] valis = values.split(",");
			int vals[] = new int[valis.length];
			for (int k = 0; k < valis.length; k = k+1) {
				vals[k] = Integer.parseInt(valis[k]);
					}
			for (int n = 0; n < cols.length; n = n+1){
				java.lang.reflect.Field field = Class.forName("java.awt.Color").getField(cols[0]);
				Color color = (Color)field.get(null);
				cg.addColorValuePair(new ColorValuePair(color,vals[n]));
					}
			cs1.setGradient(cg);
			colsetmgr.addColorSet(cs1);
			Sample s1 = gex.getCurrentGex().findSample(Gsam);
			cbe.addUseSample(s1);
			cbe.getConfiguredSample(s1).setColorSet(cs1);
			}
		
		 if(Rsam != null){
			 ColorSet cs2 = new ColorSet("colorset-1");
			 ColorRule cr = new ColorRule(); //Creating and setting colorrule
			 java.lang.reflect.Field feld = Class.forName("java.awt.Color").getField(colr);
			 Color colour = (Color)feld.get(null);
			 cr.setColor(colour);
			 List<String> al;
			 al = gex.getCurrentGex().getSampleNames();
			 String msg = cr.setExpression(xpression,al);
			 if (msg != null) throw new IOException(msg);
			 cs2.addRule(cr);
			 colsetmgr.addColorSet(cs2);
			 Sample s2 = gex.getCurrentGex().findSample(Rsam);
			 cbe.addUseSample(s2);
			 cbe.getConfiguredSample(s2).setColorSet(cs2);
		 	}
		
		DataNodeLabel dnl = new DataNodeLabel();
		
		Visualization vis = new Visualization("Visualization");//Creating visualization and adding visualizationmethod
		vis.addMethod(cbe);
		vis.addMethod(dnl);
		vis.setShowLegend(true);
		visman.addVisualization(vis);
		visman.saveXML();//Saving visualizationmanager
		return "works check xml file in result_server folder";//message for R terminal
	}

	@Override
	public XmlRpcHandler getHandler(String arg0)
				throws XmlRpcNoSuchHandlerException, XmlRpcException {
		// TODO Auto-generated method stub
		return null;
	}
}
