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
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.FileUtils;
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

	private static IDMapperStack loadedGdbs = new IDMapperStack();

	/**
	 * @param inputFile
	 *            full path of file containing data
	 * @param dbDir
	 *            full path of directory containing the annotation databases to
	 *            be used
	 * @param resultdir
	 *            full path of directory where the results saved
	 * @param resultdirectorypath
	 * @return full path of directory where results are saved
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IDMapperException
	 */
	protected String createPgex(String inputFile, String syscode,
			String sysColNum, String idColNum, String dbDir, String resultdir)
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
			int syscolnum = Integer.parseInt(sysColNum);
			syscolnum = syscolnum - 1;
			int idcolnum = Integer.parseInt(idColNum);
			idcolnum = idcolnum - 1;
			info.setSyscodeFixed(false);
			info.setSysodeColumn(syscolnum);
			info.setIdColumn(idcolnum);
		}

		String inputfile = new File(inputFile).getName();
		info.setGexName(resultdir + PathwayGpml.separator + inputfile);

		idmapperLoader(dbDir);

		GexTxtImporter.importFromTxt(info, null, getLoadedGdbs(), gexManager);

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

	protected IDMapper loadGdb(String dbfile) throws ClassNotFoundException,
	IDMapperException {

		File dbFile = new File(dbfile);
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
		getLoadedGdbs().addIDMapper(gdb);
		return getLoadedGdbs();
	}

	protected List<String> listLoadedGdbs() throws ClassNotFoundException {
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		List<String> gdbList = new ArrayList<String>();
		for (IDMapper gdb : getLoadedGdbs().getMappers()) {
			gdbList.add(gdb.toString());
		}

		return gdbList;
	}

	protected void removeLoadedGdbs() throws ClassNotFoundException {
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		for (IDMapper gdb : getLoadedGdbs().getMappers()) {
			getLoadedGdbs().removeIDMapper(gdb);
		}

	}

	protected void removeGdb(String dbfile) throws ClassNotFoundException,
	IDMapperException {

		File dbFile = new File(dbfile);
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
		getLoadedGdbs().removeIDMapper(gdb);

	}

	protected IDMapper loadGdbs(String dbDir) throws ClassNotFoundException,
	IDMapperException {
		File dbDirectory = new File(dbDir);
		List<File> bridgeFiles = FileUtils
				.getFiles(dbDirectory, "bridge", true);
		if (bridgeFiles.size() != 0) {
			for (File dbFile : bridgeFiles) {
				Class.forName("org.bridgedb.rdb.IDMapperRdb");
				IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
				getLoadedGdbs().addIDMapper(gdb);
			}
		}
		return getLoadedGdbs();
	}

	protected void idmapperLoader(String dbDir) {
		File dbFile = new File(dbDir);
		if (dbFile.exists()) {
			try {
				if (dbFile.isDirectory()) {
					loadGdbs(dbDir);
				} else {
					loadGdb(dbDir);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the loadedGdbs
	 */
	public static IDMapperStack getLoadedGdbs() {
		return loadedGdbs;
	}

	/**
	 * @param loadedGdbs the loadedGdbs to set
	 */
	public static void setLoadedGdbs(IDMapperStack loadedGdbs) {
		DataImport.loadedGdbs = loadedGdbs;
	}
}