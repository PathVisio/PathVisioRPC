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
import java.util.List;

import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.data.DataException;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.visualization.ColorRule;
import org.pathvisio.desktop.visualization.ColorSet;

/**
 * Helper class to create Color Rules for Visualizing data on DataNodes.
 * 
 * @author anwesha
 * 
 */
public class RuleVis {

	VizMaker visxml = new VizMaker();
	// For reading expression dataset
	/**
	 * 
	 */
	public static final int TYPE_GEX = 1;

	protected ColorSet createRule(int rcount, String gexFileName,
			String expressions, String colrNames) throws SecurityException,
			NoSuchFieldException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException, IOException,
			DataException {

		PreferenceManager.init();
		GexManager gex = new GexManager();
		String gexFile = gexFileName;
		gex.setCurrentGex(gexFile, false);


		ColorSet rcs = new ColorSet("cs" + rcount);
		ColorRule cr = new ColorRule();

		// java.lang.reflect.Field field = Class.forName("java.awt.Color")
		// .getField(colrNames);
		// Color color = (Color) field.get(null);
		Color color = visxml.getColors(colrNames);
		cr.setColor(color);
		List<String> al;
		al = gex.getCurrentGex().getSampleNames();
		String msg = cr.setExpression(expressions, al);
		if (msg != null)
			throw new IOException(msg);
		rcs.addRule(cr);

		return rcs;
	}

}
