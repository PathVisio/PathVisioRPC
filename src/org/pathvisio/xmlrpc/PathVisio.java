// PathVisioRPC : the XML-RPC interface for PathVisio
// PathVisio, a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2014 BiGCaT Bioinformatics
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
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.data.DataException;

/**
 * The Common handler class for all PathVisioRPC functions. This handler class
 * handles the Remote Procedure Calls from the client programming language.
 * 
 * These functions can be accessed through many programming languages with an
 * XML-RPC module or library. The way to call the function would vary according
 * to the language used. </br>Functions available in this version are : <li>
 * Creating and editing pathways</li> <li>Visualizing Data on pathways</li> <li>
 * Performing pathway statistics</li> <li>Exports pathways in various formats</li>
 * <li>
 * Help Functions</li>
 * 
 * @author anwesha
 * 
 */

public class PathVisio {

	private static List<String> functionNames = new ArrayList<String>();

	String error = " as result directory was not found/assigned.";

	PathwayGpml path = new PathwayGpml();
	DataImport dat = new DataImport();
	VizMaker visxml = new VizMaker();
	StatExport stat = new StatExport();

	/**
	 * Test the server client connection
	 * 
	 * @return "it works" if the server client connection is working
	 */
	public String test() {
		return "it works!";
	}

	/**
	 * Function to display all PathVisioRPC functions that can be called by the
	 * client
	 * 
	 * @return List of all available methods
	 */
	public List<String> getAllFunctions() {
		for (Method method : PathVisio.class.getDeclaredMethods()) {
			String name = method.getName();
			functionNames.add(name);
		}
		return functionNames;
	}

	/**
	 * Function to display the different types of DataNodes that can be drawn
	 * using PathVisio
	 * 
	 * @return List of types of DataNodes that can be drawn
	 */
	public List<String> getDataNodeTypes() {
		return path.getDatanodetypes();
	}

	/**
	 * Function to display the types of basic interactions that can be drawn
	 * using PathVisio
	 * 
	 * @return List of types of Basic Interactions that can be drawn
	 */
	public List<String> getInteractionTypes() {
		return path.getLinetypes();
	}

	/**
	 * Functions to display the types of MIM interactions that can be drawn
	 * using PathVisio
	 * 
	 * @return List of types of MIM Interactions that can be drawn
	 */
	public String[] getMIMInteractionTypes() {
		return PathwayGpml.getMIMTypes();
	}

	/**
	 * Function to display all loaded id mappers
	 * 
	 * @return List of loaded idmappers
	 * @throws ClassNotFoundException
	 */
	public List<String> listMappers() throws ClassNotFoundException {
		List<String> gdbs = dat.listLoadedGdbs();

		if (gdbs.size() == 0) {
			gdbs.add("No idmappers are loaded currently!");
			gdbs.add("Download Bridge Files from Bridgedb.org and load them");
		}
		return gdbs;

	}

	/**
	 * Function to delete all loaded idmappers
	 * 
	 * @return String "All mappers removed!"
	 * @throws ClassNotFoundException
	 */
	public String removeMappers() throws ClassNotFoundException {
		dat.removeLoadedGdbs();
		return "All mappers removed!";

	}

	/**
	 * Function to display GpraphIDs for the element in the pathway
	 * 
	 * @param pathwayfilepath
	 * @param elementname
	 * @return List of GraphIDs for all the occurrences of that element in the
	 *         pathway
	 * @throws ConverterException
	 */
	public List<String> getGraphIDs(String pathwayfilepath, String elementname)
			throws ConverterException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathway(pathwayfilepath);
		return path.getGraphIDs(pathway, elementname);
	}

	/**
	 * Function to set default colors
	 * 
	 * @param nC
	 *            Color for when gene meets no set criteria
	 * @param nD
	 *            Color for when the data is not found
	 * 
	 * @return String "Colors set!"
	 * 
	 * @throws ConverterException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * 
	 */
	public String setDefaultColours(String nC, String nD)
			throws ConverterException, SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			ClassNotFoundException, IllegalAccessException {
		VizMaker visxml = new VizMaker();
		visxml.setDefaultColors(nC, nD);
		return "Colors set!";
	}

	/**
	 * Creates a new empty pathway GPML file and saves it in the result
	 * directory
	 * 
	 * @param pathwayname
	 *            Name of pathway
	 * @param pathwayauthor
	 *            Name of author
	 * @param organism
	 *            Name of organism
	 * @param resultdirectory
	 *            Absolute path of the result directory
	 * @return Pathway file name and disk location
	 */
	public String createPathway(String pathwayname, String pathwayauthor,
			String organism, String resultdirectory) {
		PathwayGpml path = new PathwayGpml();
		String resultdir = path.createPathway(pathwayname, pathwayauthor,
				organism, resultdirectory);
		if (!resultdir.equalsIgnoreCase(resultdirectory)) {
			resultdir = resultdir + this.error;
		}
		return pathwayname + " pathway GPML file created in " + resultdir;
	}

	/**
	 * Adds a DataNode to a pathway GPML file saved on disk and saves it in the
	 * result directory
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param datanodename
	 *            Name of the DataNode
	 * @param datanodetype
	 *            Biological Type of the DataNode, "GeneProduct", "Metabolite",
	 *            "Protein", "RNA", "Pathway" or "Unknown"
	 * @param datanodeid
	 *            Identifier for the DataNode
	 * @param datanodesource
	 *            DataSource for the DataNode
	 * @return A Unique GraphID of the DataNode
	 */
	public String addDataNode(String pathwayfilepath, String datanodename,
			String datanodetype, String datanodeid, String datanodesource) {

		PathwayGpml path = new PathwayGpml();
		Pathway pathway = new Pathway();
		pathway = path.openPathway(pathwayfilepath);
		String resultdirectory = new File(pathwayfilepath).getParent();
		String identifier = path.addDataNode(pathway, datanodename,
				datanodetype, datanodeid, datanodesource, resultdirectory);
		String pathwayname = new File(pathwayfilepath).getName();
		return datanodename + "; ID : " + identifier + " added to "
		+ pathwayname;
	}

	/**
	 * Adds a DataNode to a Pathway GPML file downloaded by it's Wikipathwas ID
	 * and saves it in the result directory
	 * 
	 * @param uri
	 *            A Wikipathways ID for the pathway
	 * @param datanodename
	 *            Name of the DataNode
	 * @param datanodetype
	 *            Biological Type of the DataNode
	 * @param datanodeid
	 *            Identifier for the DataNode
	 * @param datanodesource
	 *            DataSource for the DataNode
	 * @param resultdirectory
	 * @param reference
	 * @param comment
	 * @return A Unique GraphID of the DataNode added
	 */
	public String addDataNodeByURI(String uri, String datanodename,
			String datanodetype, String datanodeid, String datanodesource,
			String resultdirectory, String reference, String comment) {
		if (reference.length() == 0) {
			reference = "";
		}
		if (comment.length() == 0) {
			comment = "";
		}
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);

		String identifier = path.addDataNode(pathway, datanodename,
				datanodetype, datanodeid, datanodesource, resultdirectory);
		String pathwayname = pathway.getMappInfo().getMapInfoName();
		return datanodename + "; ID : " + identifier + " added to "
		+ pathwayname;
	}

	/**
	 * Adds an Interaction to a Pathway GPML saved on disk, connecting two
	 * DataNodes specified by the names of the two DataNodes and saves the
	 * changed pathway GPML file in the result directory
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param linename
	 *            Name of the Interaction
	 * @param startdatanode
	 *            Name of the DataNode where the Interaction starts
	 * @param enddatanode
	 *            Name of the DataNode where the Interaction ends
	 * @param startarrowtype
	 *            Type of the arrow at the start of the Interaction
	 * @param endarrowtype
	 *            Type of the arrow at the end of the Line
	 * @return A Unique GraphID for the Line added
	 */
	public String addInteraction(String pathwayfilepath, String linename,
			String startdatanode, String enddatanode, String startarrowtype,
			String endarrowtype) {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathway(pathwayfilepath);
		String resultdirectory = new File(pathwayfilepath).getParent();
		String identifier = path.addLine(pathway, linename, startdatanode,
				enddatanode, startarrowtype, endarrowtype, resultdirectory);
		return linename + "; ID : " + identifier + " added to "
		+ pathway.getMappInfo().getMapInfoName();
	}

	/**
	 * Adds a Line to a Pathway GPML downloaded from Wikipathways by it's
	 * Wikipathways ID, connecting two DataNodes specified by the names of the
	 * two DataNodes and saves the changed pathway GPML file in the result
	 * directory
	 * 
	 * @param uri
	 *            A Wikipathways ID for the pathway
	 * @param linename
	 *            Name of the Line
	 * @param startdatanode
	 *            Name of the DataNode where the Line starts
	 * @param enddatanode
	 *            Name of the DataNode where the Line ends
	 * @param startarrowtype
	 *            Type of the arrow at the start of the line
	 * @param endarrowtype
	 *            Type of the arrow at the end of the Line
	 * @param lineid
	 *            Identifier for the Line
	 * @param linesource
	 *            DataSource for the Line
	 * @param resultdirectory
	 * @return A Unique GraphID for the Line added
	 * @throws ConverterException
	 */
	public String addInteractionByURI(String uri, String linename,
			String startdatanode, String enddatanode, String startarrowtype,
			String endarrowtype, String lineid, String linesource,
			String resultdirectory) throws ConverterException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);
		String identifier = path.addLine(pathway, linename, startdatanode,
				enddatanode, startarrowtype, endarrowtype, resultdirectory);
		return linename + "; ID : " + identifier + " added to "
		+ pathway.getMappInfo().getMapInfoName();
	}

	/**
	 * Adds a Line to a Pathway GPML saved on disk, connecting two DataNodes
	 * specified by the unique Graph IDs of the two DataNodes and saves the
	 * changed pathway GPML file in the result directory
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param linename
	 *            Name of the Line
	 * @param startdatanodegraphid
	 *            Name of the DataNode where the Line starts
	 * @param enddatanodegraphid
	 *            Name of the DataNode where the Line ends
	 * @param startarrowtype
	 *            Type of the arrow at the start of the line
	 * @param endarrowtype
	 *            Type of the arrow at the end of the Line
	 * @return A Unique GraphID for the Line added
	 * @throws ConverterException
	 */
	public String addInteractionByID(String pathwayfilepath, String linename,
			String startdatanodegraphid, String enddatanodegraphid,
			String startarrowtype, String endarrowtype)
					throws ConverterException {

		PathwayGpml path = new PathwayGpml();
		Pathway pathway = new Pathway();
		path.openPathway(pathwayfilepath);
		String resultdirectory = new File(pathwayfilepath).getParent();
		String identifier = path.addLineByID(pathway, linename,
				startdatanodegraphid, enddatanodegraphid, startarrowtype,
				endarrowtype, resultdirectory);
		return linename + "; ID : " + identifier + " added to "
		+ pathway.getMappInfo().getMapInfoName();
	}

	/**
	 * Adds a Line to a Pathway GPML downloaded from Wikipathways by it's
	 * Wikipathways ID, connecting two DataNodes specified by the unique Graph
	 * IDs of the two DataNodes and saves the changed pathway GPML file in the
	 * result directory
	 * 
	 * @param uri
	 *            A Wikipathways ID for the pathway
	 * @param linename
	 *            Name of the Line
	 * @param startdatanodegraphid
	 *            Name of the DataNode where the Line starts
	 * @param enddatanodegraphid
	 *            Name of the DataNode where the Line ends
	 * @param startarrowtype
	 *            Type of the arrow at the start of the line
	 * @param endarrowtype
	 *            Type of the arrow at the end of the Line
	 * @param lineid
	 *            Identifier for the Line
	 * @param linesource
	 *            DataSource for the Line
	 * @param resultdirectory
	 * @return A Unique GraphID for the Line added
	 * @throws ConverterException
	 */
	public String addInteractionByIDByURI(String uri, String linename,
			String startdatanodegraphid, String enddatanodegraphid,
			String startarrowtype, String endarrowtype, String lineid,
			String linesource, String resultdirectory)
					throws ConverterException {

		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);
		String identifier = path.addLineByID(pathway, linename,
				startdatanodegraphid, enddatanodegraphid, startarrowtype,
				endarrowtype, resultdirectory);
		return linename + "; ID : " + identifier + " added to "
		+ pathway.getMappInfo().getMapInfoName();
	}

	/**
	 * Annotates DataNodes in a pathway GPML file saved on disk, with an
	 * identifier and data source and saves the pathway GPML file in the result
	 * directory Annotation can also be added while adding DataNodes and Lines
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param elementname
	 *            Name of the DataNode
	 * @param elementid
	 *            Identifier for the DataNode
	 * @param elementsource
	 *            DataSource of the identifier for the DataNode
	 * @return A Unique GraphID of the DataNode annotated
	 * @throws ConverterException
	 */
	public String annotateElement(String pathwayfilepath, String elementname,
			String elementid, String elementsource) throws ConverterException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = new Pathway();
		path.openPathway(pathwayfilepath);
		String resultdirectory = new File(pathwayfilepath).getParent();
		String identifier = path.annotateElement(pathway, elementname,
				elementid, elementsource, resultdirectory);
		return elementname + " with ID:" + identifier + " in "
		+ pathway.getMappInfo().getMapInfoName()
		+ " has been annotated";
	}

	/**
	 * Annotates DataNodes in a Wikipathways pathway downloaded by it's
	 * Wikipathways ID, with an identifier and data source and saves the pathway
	 * GPML file in the result directory. Annotation can also be added while
	 * adding DataNodes and Lines.
	 * 
	 * @param uri
	 *            A wikipathways ID for the pathway
	 * @param elementname
	 *            Name of the DataNode
	 * @param elementid
	 *            Identifier for the DataNode
	 * @param elementsource
	 *            DataSource of the identifier for the DataNode
	 * @param resultdirectory
	 *            Absolute path of the directory where the pathway GPML file
	 *            should be saved
	 * @return A Unique GraphID of the DataNode annotated
	 * @throws ConverterException
	 */
	public String annotateElementByURI(String uri, String elementname,
			String elementid, String elementsource, String resultdirectory)
					throws ConverterException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = new Pathway();
		path.openPathwayByURI(uri);
		String identifier = path.annotateElement(pathway, elementname,
				elementid, elementsource, resultdirectory);
		return elementname + " with ID:" + identifier + " in "
		+ pathway.getMappInfo().getMapInfoName()
		+ " has been annotated";
	}

	/**
	 * Removes a DataNode or Line by it's name from a pathway GPML saved on disk
	 * and saves it in the result directory. Removes <b>all</b> DataNodes and
	 * Lines with that name.
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param elementname
	 *            Name of the element (DataNode or Line) to be removed pathway
	 *            GPML file should be saved
	 * @return Name of the element removed and the pathway it was removed from.
	 */
	public String removeElement(String pathwayfilepath, String elementname) {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathway(pathwayfilepath);
		String resultdirectory = new File(pathwayfilepath).getParent();
		path.removeElement(pathway, elementname, resultdirectory);
		return elementname + " in " + pathway.getMappInfo().getMapInfoName()
				+ " removed";
	}

	/**
	 * Removes a DataNode or Line by it's name from a Wikipathways pathway GPML
	 * file downloaded using it's Wikipathways ID and saves it in the result
	 * directory. Removes <b>all</b> DataNodes and Lines with that name.
	 * 
	 * @param uri
	 *            A Wikipathways ID for the pathway
	 * @param elementname
	 *            Name of the element (DataNode or Line) to be removed
	 * @param resultdirectory
	 *            Absolute path of the directory where the pathway GPML file
	 *            should be saved
	 * @return A pathway GPML file is saved in the result directory
	 * @throws ConverterException
	 */
	public String removeElementByURI(String uri, String elementname,
			String resultdirectory) throws ConverterException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);
		path.removeElement(pathway, elementname, resultdirectory);
		return elementname + " in " + pathway.getMappInfo().getMapInfoName()
				+ " removed";
	}

	/**
	 * Removes a DataNode or Line by it's unique Graph ID from a pathway GPML
	 * file saved on disk and saves it in the result directory
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param elementgraphid
	 *            Unique Graph ID of the DataNode or Line to be removed
	 * @return A pathway GPML file is saved in the resultdirectory
	 * @throws ConverterException
	 */
	public String removeElementById(String pathwayfilepath,
			String elementgraphid) throws ConverterException {

		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathway(pathwayfilepath);
		String resultdirectory = new File(pathwayfilepath).getParent();
		path.removeElementById(pathway, elementgraphid, resultdirectory);
		return elementgraphid + " in " + pathway.getMappInfo().getMapInfoName()
				+ " removed";
	}

	/**
	 * Removes a DataNode or Line by it's unique Graph ID from a pathway GPML
	 * file downloaded from Wikipathways using it's Wikipathways ID and saves it
	 * in the result directory
	 * 
	 * @param uri
	 *            A Wikipathways ID for the pathway
	 * @param elementgraphid
	 *            Unique Graph ID of the DataNode or Line to be removed
	 * @param resultdirectory
	 *            Absolute path of the directory where the pathway GPML file
	 *            should be saved
	 * @return A pathway GPML file is saved in the resultdirectory
	 * @throws ConverterException
	 */
	public String removeElementByIdByURI(String uri, String elementgraphid,
			String resultdirectory) throws ConverterException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);
		path.removeElementById(pathway, elementgraphid, resultdirectory);
		return elementgraphid + " in " + pathway.getMappInfo().getMapInfoName()
				+ " removed";
	}

	/**
	 * Imports tab delimited data and creates a PGEX file
	 * 
	 * @param inputfilepath
	 *            Absolute path of the tab delimited input file
	 * @param SysCode
	 *            System Code of the Database for identifier mapping
	 * @param SysColNum
	 *            Colum Number of the System Code column
	 * @param IdColNum
	 * @param dbDirectory
	 *            Absolute path of the folder containing the annotation
	 *            databases to be used
	 * @param resultdirectorypath
	 *            Absolute path of the directory to save results in
	 * @return Creates a PGEX file in the result directory
	 * @throws IOException
	 * @throws IDMapperException
	 * @throws ClassNotFoundException
	 */
	public String importData(String inputfilepath, String SysCode,
			String SysColNum, String IdColNum, String dbDirectory,
			String resultdirectorypath) throws IOException, IDMapperException,
			ClassNotFoundException {
		// DataImport data = new DataImport();

		// arrays are counted from zero

		String resultdir = dat.createPgex(inputfilepath, SysCode,
				SysColNum,
				IdColNum, dbDirectory, resultdirectorypath);
		if (!(resultdir.equalsIgnoreCase(resultdirectorypath))) {
			resultdir = resultdir + this.error;
		}
		String inputfile = new File(inputfilepath).getName();
		return inputfile + " imported & " + inputfile + ".pgex created in"
		+ resultdir;
	}

	/**
	 * Imports tab delimited data and creates a PGEX file
	 * 
	 * @param inputfilepath
	 *            Absolute path of the tab delimited input file
	 * @param SysCode
	 *            System Code of the Database for identifier mapping
	 * @param SysColNum
	 *            Column Number of the System Code column
	 * @param IdColNum
	 *            Column Number of the Identifier Column
	 * @param dbDirectory
	 *            Absolute path of the folder containing the annotation
	 *            databases to be used
	 * @param resultdirectorypath
	 *            Absolute path of the directory to save results in
	 * @return Creates a PGEX file in the result directory
	 * @throws IOException
	 * @throws IDMapperException
	 * @throws ClassNotFoundException
	 */
	// public String importSingleData(String inputfilepath, String IdColNum,
	// String SysCode, String SysColNum, String dbDirectory,
	// String resultdirectorypath)
	// throws IOException, IDMapperException, ClassNotFoundException {
	// DataImport data = new DataImport();
	//
	// // arrays are counted from zero
	//
	// String resultdir = data.createPgexSingleBridge(inputfilepath, SysCode,
	// SysColNum, IdColNum, dbDirectory, resultdirectorypath);
	// if (!(resultdir.equalsIgnoreCase(resultdirectorypath))) {
	// resultdir = resultdir + this.error;
	// }
	// String inputfile = new File(inputfilepath).getName();
	// return inputfile + " imported & " + inputfile + ".pgex created in"
	// + resultdir;
	// }

	/**
	 * Create a Visualization XML file for DataNodes Contains color gradients
	 * and rules
	 * 
	 * @param gexfilepath
	 *            Absolute path of the PGEX file
	 * @param gsample
	 *            Samples on which the color gradients should be applied samples
	 *            should be separated by ";"
	 * @param gcolors
	 *            Colors for the color gradients the colors for the same
	 *            gradient should be separated by "," and colors for the next
	 *            gradient should be given after a ";"
	 * @param gvalues
	 *            Values corresponding to the colors of the color gradient the
	 *            values for the same gradient should be separated by "," and
	 *            values for the next gradient should be given after a ";"
	 * @param rcolors
	 *            Colors for the color rules colors should be separated by a ";"
	 * @param rexpressions
	 *            Expressions for the color rules expressions should be
	 *            separated with ";"
	 * @return An XML file containing the visualization is saved in the same
	 *         directory as the PGEX file
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IDMapperException
	 * @throws ConverterException
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws DataException
	 */
	public String createVisualization(String gexfilepath, String gsample,
			String gcolors, String gvalues, String rexpressions, String rcolors)
					throws SecurityException,
					IllegalArgumentException, IDMapperException, ConverterException,
					IOException, NoSuchFieldException, ClassNotFoundException,
					IllegalAccessException, DataException {
		VizMaker visxml = new VizMaker();
		visxml.createVisualizationNode(gexfilepath, gsample, gcolors, gvalues,
				rexpressions, rcolors);
		return gexfilepath + ".xml-visualization file created!";
	}

	/**
	 * Create a Visualization XML file for DataNodes Contains color gradients
	 * and rules
	 * 
	 * @param gexfilepath
	 *            Absolute path of the PGEX file
	 * @param gsample
	 *            Samples on which the color gradients should be applied samples
	 *            should be separated by ";"
	 * @param gcolors
	 *            Colors for the color gradients the colors for the same
	 *            gradient should be separated by "," and colors for the next
	 *            gradient should be given after a ";" Colors should be in
	 *            hexadecimal format
	 * @param gvalues
	 *            Values corresponding to the colors of the color gradient the
	 *            values for the same gradient should be separated by "," and
	 *            values for the next gradient should be given after a ";"
	 * @param rsample
	 *            Samples on which the color rules should be applied samples
	 *            should be separated with ";"
	 * @param rcolors
	 *            Colors for the color rules colors should be separated by a ";"
	 *            Colors should be in hexadecimal format
	 * @param rexpressions
	 *            Expressions for the color rules expressions should be
	 *            separated with ";"
	 * @return An XML file containing the visualization is saved in the same
	 *         directory as the PGEX file
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IDMapperException
	 * @throws ConverterException
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws DataException
	 */
	// public String createVisualizationByHex(String gexfilepath, String
	// gsample,
	// String gcolors, String gvalues, String rsample, String rcolors,
	// String rexpressions) throws SecurityException,
	// IllegalArgumentException, IDMapperException, ConverterException,
	// IOException, NoSuchFieldException, ClassNotFoundException,
	// IllegalAccessException, DataException {
	// VizMaker visxml = new VizMaker();
	// visxml.createVisualizationNodeByHex(gexfilepath, gsample, gcolors,
	// gvalues,
	// rsample, rcolors, rexpressions);
	// return gexfilepath + ".xml-visualization file created!";
	// }

	/**
	 * Data is visualized on the pathway GPML file saved on disk and the results
	 * are saved on the result directory
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param gexfilepath
	 *            Absolute path of the PGEX file
	 * @param dbdirectory
	 *            Absolute path of the folder containing the annotation
	 *            databases
	 * @param resultdirectory
	 *            Absolute path of the directory where the results should be
	 *            saved
	 * @return Data is visualized and the html pages for the pathway and it's
	 *         back pages are saved in the result directory
	 * @throws ConverterException
	 */
	public String visualizeData(String pathwayfilepath, String gexfilepath,
			String dbdirectory, String resultdirectory)
					throws ConverterException {

		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathway(pathwayfilepath);
		String resultDir = resultdirectory;
		if (resultdirectory.length() == 0) {
			resultDir = path.createResultDir();
		} else {
			if (!(new File(resultdirectory).exists())) {
				resultDir = path.createResultDir();
			}
		}
		File output = new File(resultDir + PathwayGpml.separator
				+ pathway.getMappInfo().getMapInfoName());
		output.mkdirs();
		if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
			resultDir = resultDir + this.error;
		}
		StatExport vis = new StatExport();
		try {
			vis.visualizeData(gexfilepath, dbdirectory, pathway, output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "Data Visualized on " + pathway.getMappInfo().getMapInfoName()
				+ ", " + "results in " + resultDir;
	}

	/**
	 * Data is visualized on the pathway GPML downloaded from Wikipathways using
	 * it's Wikipathways ID and the results and saved in the result directory
	 * 
	 * @param uri
	 *            A Wikipathways ID for a pathway
	 * @param gexfilepath
	 *            Absolute path of the PGEX file
	 * @param dbdirectory
	 *            Absolute path of the folder containing the annotation
	 *            databases
	 * @param resultdirectory
	 *            Absolute path of the directory where the results should be
	 *            saved
	 * @return Data is visualized and the html pages for the pathway and it's
	 *         back pages are saved in the result directory
	 */
	public String visualizeDataByURI(String uri, String gexfilepath,
			String dbdirectory, String resultdirectory) {

		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);
		String resultDir = resultdirectory;
		if (resultdirectory.length() == 0) {
			resultDir = path.createResultDir();
		} else {
			if (!(new File(resultdirectory).exists())) {
				resultDir = path.createResultDir();
			}
		}
		File output = new File(resultdirectory + PathwayGpml.separator
				+ pathway.getMappInfo().getMapInfoName());
		output.mkdirs();
		String error = "";
		if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
			resultDir = resultDir + error;
		}
		StatExport vis = new StatExport();
		try {
			vis.visualizeData(gexfilepath, dbdirectory, pathway, output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Data Visualized on " + pathway.getMappInfo().getMapInfoName()
				+ ", " + "results in " + resultDir;
	}

	/**
	 * Exports the image of a pathway GPML saved on disk and saves it in
	 * the result directory
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param exportfiletype
	 *            The image format to export the pathway in. Possible formats
	 *            "png", "svg", "tiff" or "pdf".
	 * @param resultdirectory
	 *            Absolute path of the directory where the image should be saved
	 * @return Pathway image created in the result directory
	 * @throws ConverterException
	 * @throws IOException
	 * @throws IDMapperException
	 */
	public String exportPathway(String pathwayfilepath, String exportfiletype,
			String resultdirectory) throws ConverterException, IOException,
			IDMapperException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathway(pathwayfilepath);
		String resultDir = path.exportPathway(pathway, exportfiletype,
				resultdirectory);
		if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
			resultDir = resultDir + this.error;
		}
		return pathway.getMappInfo().getMapInfoName() + "." + exportfiletype
				+ " file created in " + resultDir;
	}

	/**
	 * Exports a pathway image of a Wikipathways pathway, provided it's
	 * WikiPathways ID in png, svg or pdf formats
	 * 
	 * @param uri
	 *            Wikipathways ID of the pathway
	 * @param exportfiletype
	 *             The image format to export the pathway in. Possible formats
	 *            "png", "svg", "tiff" or "pdf".
	 * @param resultdirectory
	 *            Absolute path of the directory where the image should be saved
	 * @return Pathway image created in result directory
	 * @throws ConverterException
	 * @throws IOException
	 * @throws IDMapperException
	 */
	public String exportPathwayFromURI(String uri, String exportfiletype,
			String resultdirectory) throws ConverterException, IOException,
			IDMapperException {
		PreferenceManager.init();
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);
		String resultDir = path.exportPathway(pathway, exportfiletype,
				resultdirectory);
		String error = "";
		if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
			resultDir = resultDir + error;
		}
		return pathway.getMappInfo().getMapInfoName() + "." + exportfiletype
				+ " file created in " + resultDir;
	}

	/**
	 * Exports a byte array of the image of a pathway GPML file saved on disk
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param filetype
	 *            Type of image "png", "svg", "tiff" or "pdf"
	 * @return Byte Array
	 * @throws ConverterException
	 * @throws IOException
	 * @throws IDMapperException
	 */
	public byte[] exportPathwayByte(String pathwayfilepath, String filetype)
			throws ConverterException, IOException, IDMapperException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathway(pathwayfilepath);
		return path.exportPathwayByte(pathway, filetype);
	}

	/**
	 * Exports a byte array of the image of a Wikipathways pathway downloaded by
	 * it's Wikipathways ID
	 * 
	 * @param uri
	 *            Wikipathways ID of the pathay
	 * @param exportfiletype
	 *            Type of image required ("png","svg", "tiff", or "pdf")
	 * @return Byte Array of the pathway image
	 * @throws ConverterException
	 * @throws IOException
	 * @throws IDMapperException
	 */
	public byte[] exportPathwayByteFromURI(String uri, String exportfiletype)
			throws ConverterException, IOException, IDMapperException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);
		return path.exportPathwayByte(pathway, exportfiletype);
	}

	/**
	 * Exports a pathway GPML file along with backpages in html format
	 * 
	 * @param pathwayfilepath
	 *            Absolute path of the pathway GPML file
	 * @param dbdirectory
	 *            Absolute path of the folder containing the annotation
	 *            databases
	 * @param resultdirectory
	 *            Absolute path of the directory where the html folder should be
	 *            saved
	 * @return Html folder containing pathway and backpages created in result
	 *         directory
	 * @throws ConverterException
	 * @throws IOException
	 * @throws IDMapperException
	 */
	public String exportPathwayHtml(String pathwayfilepath, String dbdirectory,
			String resultdirectory) throws ConverterException, IOException,
			IDMapperException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathway(pathwayfilepath);
		String resultDir = path.createPathwayHtml(pathway, dbdirectory,
				resultdirectory);
		if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
			resultDir = resultDir + this.error;
		}
		return pathway.getMappInfo().getMapInfoName() + "." + "html"
		+ " file created in " + resultDir;
	}

	/**
	 * Exports a Wikipathways pathway file along with backpages in html format,
	 * provided it's WikiPathways ID
	 * 
	 * @param uri
	 *            Wikipathways ID of the pathway
	 * @param dbdirectory
	 *            Absolute path of the folder containing the annotation
	 *            databases
	 * @param resultdirectory
	 *            Absolute path of the directory where the html folder should be saved
	 * @return Html folder containing pathway and backpages created in result directory
	 * @throws ConverterException
	 * @throws IOException
	 * @throws IDMapperException
	 */
	public String exportPathwayHtmlFromURI(String uri, String dbdirectory,
			String resultdirectory) throws ConverterException, IOException,
			IDMapperException {
		PathwayGpml path = new PathwayGpml();
		Pathway pathway = path.openPathwayByURI(uri);
		String resultDir = path.createPathwayHtml(pathway, dbdirectory,
				resultdirectory);
		if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
			resultDir = resultDir + this.error;
		}
		return pathway.getMappInfo().getMapInfoName() + "." + "html"
		+ " file created in " + resultDir;
	}

	/**
	 * Calculate Pathway statistics - over representation analysis And exports
	 * results as a hyperlinked html page containing a list of all the pathways
	 * ranked according to their z score, a folder of images and a folder
	 * containing backpages
	 * 
	 * 
	 * @param pathwaydirectory
	 *            Absolute path of the directory which contains the pathways
	 * @param gexfilepath
	 *            Absolute path of the PGEX file
	 * @param dbdirectory
	 *            Absolute path of the folder containing the annotation
	 *            databases
	 * @param criteria
	 *            The criteria based on which the Zscore should be calculated
	 * @param resultdirectory
	 *            Absolute path of the directory where the results should be
	 *            saved
	 * @return String stating that results have been exported
	 */
	public String calculatePathwayStatistics(String pathwaydirectory,
			String gexfilepath, String dbdirectory, String criteria,
			String resultdirectory) {
		StatExport stat = new StatExport();
		String resultDir = "";
		try {
			dat.removeLoadedGdbs();
			resultDir = stat.calculatePathwayStatistics(gexfilepath,
					dbdirectory, pathwaydirectory, criteria, resultdirectory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
			resultDir = resultDir + this.error;
		}

		return "Results exported to " + resultDir;
	}

	/**
	 * Exports a list of Pathways ranked according to Z score
	 * 
	 * @param pathwaydirectory
	 *            Absolute path of the directory which contains the pathways
	 * @param gexfilepath
	 *            Absolute path of the PGEX file
	 * @param dbdirectory
	 *            Absolute path of the folder containing the annotation
	 *            databases
	 * @param criteria
	 *            The criteria based on which the Zscore should be calculated
	 * @param resultdirectory
	 *            Absolute path of the directory where the results should be
	 *            saved
	 * @return String stating the result directory
	 */
	public String getPathwayStatList(String pathwaydirectory,
			String gexfilepath, String dbdirectory, String criteria,
			String resultdirectory) {
		StatExport stat = new StatExport();
		String resultDir = "";
		try {
			dat.removeLoadedGdbs();
			resultDir = stat.getPathwayStatList(gexfilepath,
					dbdirectory, pathwaydirectory, criteria, resultdirectory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
			resultDir = resultDir + this.error;
		}

		return "Pathway Statistics List exported to " + resultDir;
	}

	/**
	 * Calculate Pathway statistics - overrepresentation analysis
	 * 
	 * @param pathwaydirectory
	 *            Absolute path of the directory which contains the pathways
	 * @param gexfilepath
	 *            Absolute path of the PGEX file
	 * @param dbfilepath
	 *            Absolute path of the bridge file
	 * @param criteria
	 *            The criteria based on which the Zscore should be calculated
	 * @param resultdirectory
	 *            Absolute path of the directory where the results should be
	 *            saved
	 * @return String stating that results have been exported
	 */
	// public String calculatePathwayStatisticsByBridge(String pathwaydirectory,
	// String gexfilepath, String dbfilepath, String criteria,
	// String resultdirectory) {
	// // StatExport stat = new StatExport();
	// String resultDir = "";
	// try {
	// path.removeLoadedGdbs();
	// resultDir = stat.calculatePathwayStatisticsByBridge(gexfilepath,
	// dbfilepath, pathwaydirectory, criteria, resultdirectory);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// if (!(resultDir.equalsIgnoreCase(resultdirectory))) {
	// resultDir = resultDir + this.error;
	// }
	//
	// return "Results exported to " + resultDir;
	// }

}