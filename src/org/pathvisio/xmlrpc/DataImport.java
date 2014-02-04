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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.gexplugin.ImportInformation;

/**
 * Imports a tab delimited data file and converts it to the PathVisio .pgex file
 * format
 * 
 * @author anwesha
 */

public class DataImport {

	/**
	 * @param inputFile
	 *            full path of file containing data
	 * @param dbDir
	 *            full path of directory containing the annotation databases to
	 *            be used
	 * @param resultdir
	 *            full path of directory where the results saved
	 * @return full path of directory where results are saved
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IDMapperException
	 */
	protected String createPgex(String inputFile, String syscode,
			int SysColNum,
			String dbDir, String resultdir)
					throws IOException, ClassNotFoundException, IDMapperException {
		PreferenceManager.init();
		GexManager gexManager = new GexManager();
		ImportInformation info = new ImportInformation();
		PathwayGpml path = new PathwayGpml();
		if (resultdir.length() == 0) {
			resultdir = path.createResultDir();
		} else {
			if (!(new File(resultdir).exists())) {
				resultdir = path.createResultDir();
			}
		}
		info.setTxtFile(new File(inputFile));
		if (syscode != null && !syscode.isEmpty()) {
			info.setSyscodeFixed(true);
			info.setDataSource(DataSource.getBySystemCode(syscode));
		} else {
			info.setSyscodeFixed(false);
			info.setSysodeColumn(SysColNum);
		}

		String inputfile = new File(inputFile).getName();
		info.setGexName(resultdir + PathwayGpml.separator + inputfile);

		path.loadGdbs(dbDir);
		// System.out.println("code\t" + syscode);
		// System.out.println("syscode column mine\t" + SysColNum);
		// System.out.println("syscode column pro\t" + info.getSyscodeColumn());
		// System.out.println(PathwayGpml.loadedGdbs.getMappers().toString()+PathwayGpml.loadedGdbs.isConnected());


		GexTxtImporter.importFromTxt(info, null, PathwayGpml.loadedGdbs,
				gexManager);

		FileWriter fw = new FileWriter(resultdir + PathwayGpml.separator
				+ inputfile + "error.txt");
		fw.write("Writing file: " + info.getGexName() + PathwayGpml.newline);
		fw.write("Errors: " + info.getErrorList().size() + PathwayGpml.newline);
		fw.write("Data rows: " + info.getDataRowsImported()
				+ PathwayGpml.newline);
		fw.write("Mapped ok: " + info.getRowsMapped() + PathwayGpml.newline);
		fw.close();
		return resultdir;
	}
}
