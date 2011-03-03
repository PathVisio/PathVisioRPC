package org.pathvisio.xmlrpc;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
//import java.util.List;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import org.bridgedb.IDMapperException;
import org.pathvisio.Engine;
import org.pathvisio.gex.GexManager;
import org.pathvisio.gex.Sample;
import org.pathvisio.model.ConverterException;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.visualization.VisualizationManager;
import org.pathvisio.visualization.Visualization;
import org.pathvisio.visualization.colorset.ColorGradient;
//import org.pathvisio.visualization.colorset.ColorRule;
//import org.pathvisio.visualization.colorset.ColorRule;
import org.pathvisio.visualization.colorset.ColorSet;
import org.pathvisio.visualization.colorset.ColorSetManager;
import org.pathvisio.visualization.plugins.ColorByExpression;
import org.pathvisio.visualization.plugins.DataNodeLabel;
//import org.pathvisio.visualization.plugins.TextByExpression;

public class SingVis implements XmlRpcHandlerMapping {
	
	//Check if xmlrpc works
	public String message(){
		return "It works!";
	}
	
	//For reading expression dataset
	public static final int TYPE_GEX = 1;
	
	public String createVisualization() throws IDMapperException, ConverterException, IOException {
		
		// setup
		PreferenceManager.init();
		Engine engine = new Engine ();//Create engine to load pathway
		GexManager gex = new GexManager();
		gex.setCurrentGex("/home/rai/Desktop/maastricht/result_server/feb_21.txt.pgex",false);

		File xmlFile = new File(gex.getCurrentGex().getDbName() + ".xml");
		xmlFile.delete();
		
		VisualizationManager visman = new VisualizationManager(engine, gex);//Creating visualizationmanager and adding visualization,colorsetmanager and visualizationmethodregistry
		ColorSetManager colsetmgr = visman.getColorSetManager();

		// Creating new colorset 
		ColorSet cs = new ColorSet(colsetmgr);
		ColorGradient cg = new ColorGradient(cs);//Creating and setting gradient
		cg.addColorValuePair(cg.new ColorValuePair(Color.red,2));
		cg.addColorValuePair(cg.new ColorValuePair(Color.yellow,-2));
		/*ColorRule cr = new ColorRule(cs); //Creating and setting colorrule
		cr.setColor(Color.green);
		cr.setExpression("");*/
		//TODO: check if these are both necessary?
		DataNodeLabel dnl = new DataNodeLabel();
		cs.addObject(cg);
		cs.setGradient(cg);
		
		colsetmgr.addColorSet(cs);//Creating coloursetmanager object and adding colourset to it

		ColorByExpression cbe = new ColorByExpression(gex, colsetmgr);//Creating Visualisation Method -- Colour by expression
		Sample sample = gex.getCurrentGex().findSample("logFC");
		if (sample == null) throw new IOException("Wrong sample name");
		cbe.addUseSample(sample);
		cbe.setSingleColorSet(cs);
		
		Visualization vis = new Visualization("Vis1");//Creating visualization and adding visualizationmethod
		vis.addMethod(cbe);
		vis.addMethod(dnl);
		vis.setShowLegend(true);
		visman.addVisualization(vis);
		visman.saveXML();//Saving visualizationmanager
		
		return "works check xml file in home folder";//message for R terminal
	}
	
	public static void main (String [] args) {
		try {
			System.out.println("Attempting to start SingleVisualization XML-RPC Server...");
			WebServer server = new WebServer(8081);
			XmlRpcServer xmlserver = server.getXmlRpcServer();
			PropertyHandlerMapping phm = new PropertyHandlerMapping();
			phm.addHandler("Handler", SingVis.class);
			xmlserver.setHandlerMapping(phm);
			server.start();
			System.out.println("Started successfully.");
			System.out.println("Accepting requests. (Halt program to stop.)");
		} catch (Exception exception) {
			System.err.println("SingVis: " + exception);
		}
	}

	@Override
	public XmlRpcHandler getHandler(String arg0)
				throws XmlRpcNoSuchHandlerException, XmlRpcException {
		// TODO Auto-generated method stub
		return null;
	}
}
