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
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.pathvisio.desktop.visualization.ColorGradient;
import org.pathvisio.desktop.visualization.ColorGradient.ColorValuePair;
import org.pathvisio.desktop.visualization.ColorSet;

/**
 * Creates Color Gradients for Visualizing data on DataNodes.
 * 
 * @author anwesha
 * 
 */
public class GradientVis {

	VizMaker visxml = new VizMaker();
	/**
	 * @param gcount
	 *            integer indicating color set number
	 * @param colorNames
	 *            names of colours in this gradient
	 * @param values
	 *            values associated with colours in the gradient
	 * @return Color Gradient the colour gradient/s created
	 * @throws IDMapperException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	protected ColorSet createGradient(int gcount, String colorNames,
			String values) throws IDMapperException, SecurityException,
			NoSuchFieldException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException, IOException {

		ColorSet gcs = new ColorSet("c" + gcount);
		ColorGradient cg = new ColorGradient();

		String[] cols = colorNames.split(",");
		String[] valus = values.split(",");
		int vals[] = new int[valus.length];
		for (int i = 0; i < valus.length; i++) {
			vals[i] = Integer.parseInt(valus[i]);
		}
		for (int i = 0; i < cols.length; i = i + 1) {
			// java.lang.reflect.Field field = Class.forName("java.awt.Color")
			// .getField(cols[i]);
			// Color color = (Color) field.get(null);
			Color color = visxml.getColors(cols[i]);
			cg.addColorValuePair(new ColorValuePair(color, vals[i]));
		}
		gcs.setGradient(cg);

		return gcs;
	}

}