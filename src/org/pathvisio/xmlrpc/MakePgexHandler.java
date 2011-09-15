package org.pathvisio.xmlrpc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.bio.Organism;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.gexplugin.ImportInformation;

/**
 * Create the Pgex file, using the input stat file, the gene database and 
 * the species name. Note: Species is formatted as such: "HomoSapiens" and
 * the input file has to be in TXT format (call the "setTxtFile" function) 
 * with a fixed system code.
 * 
 * @author anwesha, magali
 */

public class MakePgexHandler { 

	public String createPgex(String inputFile, String dbFile, String dsFull)
			throws IOException, IDMapperException, ClassNotFoundException {

		PreferenceManager.init();
		GexManager gexManager = new GexManager();
		ImportInformation info = new ImportInformation();
		info.setTxtFile(new File(inputFile)); 
		
		// settings
		Organism org = Organism.valueOf(dsFull);
		DataSource ds = BioDataSource.getSpeciesSpecificEnsembl(org);
		// What does that mean? Only Ensembl ID are allowed?

		info.setSyscodeFixed(true);

		info.setDataSource(ds);
		info.setGexName("" + inputFile);
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
		
		// import input file in txt format
		GexTxtImporter.importFromTxt(info, null, gdb, gexManager); 

		FileWriter fw = new FileWriter(inputFile + "error.txt");
		fw.write("Writing file: " + info.getGexName() + "\n");
		fw.write("Errors: " + info.getErrorList().size() + "\n");
		fw.write("Data rows: " + info.getDataRowsImported() + "\n");
		fw.write("Mapped ok: " + info.getRowsMapped() + "\n");
		fw.close();
		return "pgex made!";
	}
}