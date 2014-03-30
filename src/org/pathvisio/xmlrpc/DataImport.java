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
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.FileUtils;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.htmlexport.plugin.HtmlExporter;

/**
 * Loads identifier mapping bridge files & Imports and converts tab delimited
 * data files to the PathVisio .pgex file format
 * 
 * @author anwesha
 */

public class DataImport {

	PathwayGpml path = new PathwayGpml();

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
	 */
	protected String createPgex(String inputFile, String idColNum,
			String syscode, String sysColNum, String gexFileName,
			String dbDir, String resultdir)
	{
		PreferenceManager.init();
		GexManager gexManager = new GexManager();
		ImportInformation info = new ImportInformation();
		String inputfileName = new File(inputFile).getName();
		String gexfileName = "";
		resultdir = path.checkResultDir(resultdir);

		try {
			info.setTxtFile(new File(inputFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!idColNum.isEmpty()) {
			int idcolnum = Integer.parseInt(idColNum);
			idcolnum = idcolnum - 1;
			info.setIdColumn(idcolnum);
			System.out.print("Id Column: " + info.getColNames()[idcolnum]);
		}
		if (!sysColNum.isEmpty()) {
			int syscolnum = Integer.parseInt(sysColNum);
			syscolnum = syscolnum - 1;
			info.setSyscodeFixed(false);
			info.setSysodeColumn(syscolnum);
			System.out
			.print("Syscode column: " + info.getColNames()[syscolnum]);
		} else {
			if (syscode != null && !syscode.isEmpty()) {
				info.setSyscodeFixed(true);
				info.setDataSource(DataSource.getBySystemCode(syscode));
			} else {
				System.err
				.print("Neither Syscode or syscode column was specified!");
			}
		}
		if (!gexFileName.isEmpty()) {
			gexfileName = resultdir + PathwayGpml.separator + gexFileName;
		} else {
			gexfileName = resultdir + PathwayGpml.separator + inputfileName;
		}
		info.setGexName(gexfileName);
		idmapperLoader(dbDir);

		GexTxtImporter.importFromTxt(info, null, getLoadedGdbs(), gexManager);

		FileWriter fwerror;
		try {
			fwerror = new FileWriter(gexfileName + "error.txt");
			fwerror.write("Writing file: " + info.getGexName()
					+ PathwayGpml.newline);
			fwerror.write("Errors: " + info.getErrorList().size()
					+ PathwayGpml.newline);
			fwerror.write("Data rows: " + info.getDataRowsImported()
					+ PathwayGpml.newline);
			fwerror.write("Mapped ok: " + info.getRowsMapped()
					+ PathwayGpml.newline);
			fwerror.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultdir;
	}

	protected String createPathwayHtml(Pathway pathway, String dbdirectory,
			String resultdir) {
		PreferenceManager.init();

		resultdir = path.checkResultDir(resultdir);
		File output = new File(resultdir + PathwayGpml.separator
				+ pathway.getMappInfo().getMapInfoName());
		output.mkdirs();
		idmapperLoader(dbdirectory);
		try {
			HtmlExporter exporter = new HtmlExporter(
getLoadedGdbs(), null,
					null);
			exporter.doExport(pathway, pathway.getMappInfo().getMapInfoName(),
					output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultdir;
	}

	protected void idmapperLoader(String dbDir) {
		removeLoadedGdbs();
		if (dbDir.contains(";")) {
			String dbFiles[] = dbDir.split(";");
			for (String dbFile : dbFiles) {
				loadGdb(dbFile);
			}
		} else {
			File dbFile = new File(dbDir);
			if (dbFile.exists()) {
				if (dbFile.isDirectory()) {
					loadGdbs(dbDir);
				} else {
					loadGdb(dbDir);
				}
			}
		}
	}


	protected IDMapper loadGdb(String dbfile) {

		File dbFile = new File(dbfile);
		IDMapper gdb;
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
			gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
			getLoadedGdbs().addIDMapper(gdb);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IDMapperException e) {
			e.printStackTrace();
		}
		return getLoadedGdbs();
	}

	protected IDMapper loadGdbs(String dbDir) {
		File dbDirectory = new File(dbDir);
		List<File> bridgeFiles = FileUtils
				.getFiles(dbDirectory, "bridge", true);
		if (bridgeFiles.size() != 0) {
			for (File dbFile : bridgeFiles) {
				try {
					Class.forName("org.bridgedb.rdb.IDMapperRdb");
					IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
					getLoadedGdbs().addIDMapper(gdb);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IDMapperException e) {
					e.printStackTrace();
				}
			}
		}
		return getLoadedGdbs();
	}

	protected List<String> listLoadedGdbs() {
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> gdbList = new ArrayList<String>();
		for (IDMapper gdb : getLoadedGdbs().getMappers()) {
			gdbList.add(gdb.toString());
		}

		return gdbList;
	}

	protected void removeGdb(String dbfile) {

		File dbFile = new File(dbfile);
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IDMapper gdb = null;
		try {
			gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
		} catch (IDMapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getLoadedGdbs().removeIDMapper(gdb);

	}

	protected void removeLoadedGdbs() {
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (IDMapper gdb : getLoadedGdbs().getMappers()) {
			getLoadedGdbs().removeIDMapper(gdb);
		}

	}

	/**
	 * @return the loadedGdbs
	 */
	public IDMapperStack getLoadedGdbs() {
		return loadedGdbs;
	}

	/**
	 * @param loadedGdbs the loadedGdbs to set
	 */
	public void setLoadedGdbs(IDMapperStack loadedGdbs) {
		DataImport.loadedGdbs = loadedGdbs;
	}
}