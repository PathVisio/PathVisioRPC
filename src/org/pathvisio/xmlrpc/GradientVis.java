package org.pathvisio.xmlrpc;
import java.awt.Color;
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.pathvisio.desktop.visualization.ColorGradient;
import org.pathvisio.desktop.visualization.ColorGradient.ColorValuePair;
import org.pathvisio.desktop.visualization.ColorSet;


public class GradientVis {
	
	public ColorSet createGradient(int gcount, String colorNames , String values, String sample) 
			throws IDMapperException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, IOException {
		
		// Creating new colorset
		ColorSet gcs = new ColorSet(sample);
		ColorGradient cg = new ColorGradient();//Creating and setting gradient
		
		String[] cols = colorNames.split(",");
		String[] valus = values.split(",");
		int vals[] = new int[valus.length];
		for (int i = 0; i < valus.length; i++) {
			vals[i] = Integer.parseInt(valus[i]);
				}
		for(int i = 0; i < cols.length; i = i+1){
			// need a hexadecimal format such as 0xFF0000 (for red)
			Color color = Color.decode(cols[i]);
			cg.addColorValuePair(new ColorValuePair(color,vals[i]));
			}
		gcs.setGradient(cg);
		
		return gcs;//message for R terminal
	}

}