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
	
	public String createVisualization(String gexFile, String col1, int val1, String col2, int val2, String col3, String expr, String sam1, String sam2) throws IDMapperException, ConverterException, IOException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		
		// setup
		PreferenceManager.init();
		Engine engine = new Engine ();//Create engine to load pathway
		GexManager gex = new GexManager();
		gex.setCurrentGex(gexFile,false);

		File xmlFile = new File(gex.getCurrentGex().getDbName() + ".xml");
		xmlFile.delete();
		
		VisualizationManager visman = new VisualizationManager(engine, gex);//Creating visualizationmanager and adding visualization,colorsetmanager and visualizationmethodregistry
		ColorSetManager colsetmgr = visman.getColorSetManager();

		// Creating new colorset 
		ColorSet cs1 = new ColorSet("colorset");
		ColorGradient cg = new ColorGradient();//Creating and setting gradient
		Color color1, color2, color3;
		java.lang.reflect.Field field1 = Class.forName("java.awt.Color").getField(col1);
		color1 = (Color)field1.get(null);
		java.lang.reflect.Field field2 = Class.forName("java.awt.Color").getField(col2);
		color2 = (Color)field2.get(null);
		cg.addColorValuePair(new ColorValuePair(color1,val1));
		cg.addColorValuePair(new ColorValuePair(color2,val2));
		cs1.setGradient(cg);
		
		ColorSet cs2 = new ColorSet("colorset-1");
		ColorRule cr = new ColorRule(); //Creating and setting colorrule
		java.lang.reflect.Field field3 = Class.forName("java.awt.Color").getField(col2);
		color3 = (Color)field3.get(null);
		cr.setColor(color3);
		List<String> al;
		al = gex.getCurrentGex().getSampleNames();
		String msg = cr.setExpression(expr,al);
		if (msg != null) throw new IOException(msg);
		cs2.addRule(cr);
		
		
		//TODO: check if these are both necessary?
		DataNodeLabel dnl = new DataNodeLabel();
		colsetmgr.addColorSet(cs1);//Creating coloursetmanager object and adding colourset to it
		colsetmgr.addColorSet(cs2);
		ColorByExpression cbe = new ColorByExpression(gex, colsetmgr);//Creating Visualisation Method -- Colour by expression

		Sample s1 = gex.getCurrentGex().findSample(sam1);
		Sample s2 = gex.getCurrentGex().findSample(sam2);
		//if (sample == null) throw new IOException("Wrong sample name");
		cbe.addUseSample(s1);
		cbe.addUseSample(s2);
		
		cbe.getConfiguredSample(s1).setColorSet(cs1);
		cbe.getConfiguredSample(s2).setColorSet(cs2);

		Visualization vis = new Visualization("Vis1");//Creating visualization and adding visualizationmethod
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
