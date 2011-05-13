package org.pathvisio.xmlrpc;
import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.desktop.visualization.ColorRule;
import org.pathvisio.desktop.visualization.ColorSet;


public class RuleVis {
	
	//For reading expression dataset
	public static final int TYPE_GEX = 1;
	
public ColorSet createRule(int rcount, String gexFileName, String colrNames, String expressions) throws IDMapperException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, IOException{
		
		PreferenceManager.init();
		GexManager gex = new GexManager();
		String gexFile = gexFileName;
		gex.setCurrentGex(gexFile,false);

		// Creating new colorset
		ColorSet rcs = new ColorSet("cs"+rcount);
		ColorRule cr = new ColorRule(); //Creating and setting colorrule
		
		String[] colrs = colrNames.split(",");
		String[] expr = expressions.split(",");
		
		java.lang.reflect.Field field = Class.forName("java.awt.Color").getField(colrs[rcount]);
		Color color = (Color)field.get(null);
		cr.setColor(color);
		List<String> al;
		al = gex.getCurrentGex().getSampleNames();
		String msg = cr.setExpression(expr[rcount],al);
		if (msg != null) throw new IOException(msg);
		rcs.addRule(cr);
		
		return rcs;//message for R terminal
		}

}
