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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.pathvisio.core.model.BatikImageExporter;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.model.ImageExporter;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.RasterImageExporter;
import org.pathvisio.core.model.ShapeType;
import org.pathvisio.core.model.StaticProperty;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.view.MIMShapes;
import org.pathvisio.htmlexport.plugin.HtmlExporter;

/**
 * Functions to create, edit and export Biological pathways and Add, annotate
 * and search for PathwayElements.
 * 
 * @author anwesha
 */
public class PathwayGpml {

	DataImport dat = new DataImport();
	private PathwayElement mappInfo = null;
	private PathwayElement infoBox = null;
	private PathwayElement pwyelement = null;
	private PathwayElement datanode = null;
	private PathwayElement line = null;
	static String separator = System.getProperty("file.separator");
	static String newline = System.getProperty("line.separator");

	// static IDMapperStack loadedGdbs = new IDMapperStack();

	protected String createResultDir() {
		final File homeDir = new File(System.getProperty("user.home"));
		final File resultDir = new File(homeDir, "PathVisioRPC-Results");
		boolean exists = resultDir.exists();
		if (!exists) {
			new File(homeDir, "PathVisioRPC-Results").mkdir();
		}

		return resultDir.getAbsolutePath();
	}

	protected String createPathway(String pathwayname, String pathwayauthor,
			String organism, String resultdir) {
		Pathway pathway = new Pathway();

		this.mappInfo = PathwayElement
				.createPathwayElement(ObjectType.MAPPINFO);
		this.mappInfo
		.setStaticProperty(StaticProperty.MAPINFONAME, pathwayname);
		this.mappInfo.setStaticProperty(StaticProperty.AUTHOR, pathwayauthor);
		this.mappInfo.setStaticProperty(StaticProperty.ORGANISM, organism);
		pathway.add(this.mappInfo);

		this.infoBox = PathwayElement.createPathwayElement(ObjectType.INFOBOX);
		pathway.add(this.infoBox);
		if (resultdir.length() == 0) {
			resultdir = createResultDir();
		} else {
			if (!(new File(resultdir).exists())) {
				resultdir = createResultDir();
			}
		}
		File pathwayfile = new File(resultdir + separator + pathwayname
				+ ".gpml");
		try {
			pathway.writeToXml(pathwayfile, true);
		} catch (ConverterException e) {
			e.printStackTrace();
		}
		return resultdir;
	}

	protected Pathway openPathway(String pathwayfilepath) {
		MIMShapes.registerShapes();
		final Pathway pathway = new Pathway();
		try {
			pathway.readFromXml(new File(pathwayfilepath), true);
		} catch (ConverterException e) {
			e.printStackTrace();
		}
		return pathway;
	}

	protected Pathway openPathwayByURI(String uri) {
		String link = "http://www.wikipathways.org//wpi/wpi.php?action=downloadFile&type=gpml&"
				+ "pwTitle=Pathway:" + uri;
		Pathway pathway = new Pathway();
		MIMShapes.registerShapes();
		try {
			pathway.readFromXml(new URL(link).openStream(), false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ConverterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pathway;
	}

	protected void addGraphIds(Pathway pathway) {
		for (PathwayElement pe : pathway.getDataObjects()) {
			String id = pe.getGraphId();
			if (id == null || "".equals(id)) {
				if (pe.getObjectType() == ObjectType.LINE
						|| pe.getObjectType() == ObjectType.DATANODE) {
					StringBuilder builder = new StringBuilder();
					builder.append(pe.getMStartX());
					builder.append(pe.getMStartY());
					builder.append(pe.getMEndX());
					builder.append(pe.getMEndY());

					String newId;
					int i = 1;
					do {
						newId = "id"
								+ Integer
								.toHexString((builder.toString() + ("_" + i))
										.hashCode());
						i++;
					} while (pathway.getGraphIds().contains(newId));
					pe.setGraphId(newId);
				}

				if (pe.getObjectType() == ObjectType.MAPPINFO) {
					StringBuilder builder = new StringBuilder();
					builder.append(pe.getAuthor());
					builder.append(pe.getOrganism());
					builder.append(pe.getMapInfoName());

					String newId;
					int i = 1;
					do {
						newId = "id"
								+ Integer
								.toHexString((builder.toString() + ("_" + i))
										.hashCode());
						i++;
					} while (pathway.getGraphIds().contains(newId));
					pe.setGraphId(newId);
				}

			}
		}
	}

	protected void savePathway(Pathway pathway, String resultdir) {
		if (resultdir.length() == 0) {
			resultdir = createResultDir();
		} else {
			if (!(new File(resultdir).exists())) {
				resultdir = createResultDir();
			}
		}
		pathway.fixReferences();

		try {
			pathway.writeToXml(pathway.getSourceFile(), true);
		} catch (ConverterException e) {
			e.printStackTrace();
		}
	}

	protected String exportPathway(Pathway pathway, String filetype,
			String resultdir) throws ConverterException, IOException,
			IDMapperException {
		PreferenceManager.init();
		if (resultdir.length() == 0) {
			resultdir = createResultDir();
		} else {
			if (!(new File(resultdir).exists())) {
				resultdir = createResultDir();
			}
		}
		String pathwayname = pathway.getMappInfo().getMapInfoName();
		File pathwayfile = new File(resultdir + separator + pathwayname + "."
				+ filetype);
		createPathwayImage(pathway, filetype, pathwayfile);
		return resultdir;
	}

	protected byte[] exportPathwayByte(Pathway pathway, String filetype)
			throws ConverterException, IOException, IDMapperException {
		PreferenceManager.init();
		File pathwayfile = new File(pathway.getMappInfo().getMapInfoName()
				+ "." + filetype);
		createPathwayImage(pathway, filetype, pathwayfile);
		FileInputStream fin = new FileInputStream(pathwayfile);
		byte fileContent[] = new byte[(int) pathwayfile.length()];
		fin.read(fileContent);
		pathwayfile.delete();
		return fileContent;
	}

	protected void createPathwayImage(Pathway pathway, String filetype,
			File pathwayfile) {
		if (filetype.equalsIgnoreCase("svg")) {
			try {
				new BatikImageExporter(ImageExporter.TYPE_SVG).doExport(
						pathwayfile, pathway);
			} catch (ConverterException e) {
				e.printStackTrace();
			}
		}

		if (filetype.equalsIgnoreCase("png")) {
			try {
				new RasterImageExporter(ImageExporter.TYPE_PNG).doExport(
						pathwayfile, pathway);
			} catch (ConverterException e) {
				e.printStackTrace();
			}
		}

		if (filetype.equalsIgnoreCase("pdf")) {
			try {
				new BatikImageExporter(ImageExporter.TYPE_PDF).doExport(
						pathwayfile, pathway);
			} catch (ConverterException e) {
				e.printStackTrace();
			}
		}

		if (filetype.equalsIgnoreCase("tiff")) {
			try {
				new BatikImageExporter(ImageExporter.TYPE_TIFF).doExport(
						pathwayfile, pathway);
			} catch (ConverterException e) {
				e.printStackTrace();
			}
		}

	}

	protected String createPathwayHtml(Pathway pathway, String dbdirectory,
			String resultdir) {
		PreferenceManager.init();
		if (resultdir.length() == 0) {
			resultdir = createResultDir();
		} else {
			if (!(new File(resultdir).exists())) {
				resultdir = createResultDir();
			}
		}
		File output = new File(resultdir + PathwayGpml.separator
				+ pathway.getMappInfo().getMapInfoName());
		output.mkdirs();
		dat.idmapperLoader(dbdirectory);
		try {
			HtmlExporter exporter = new HtmlExporter(
dat.getLoadedGdbs(), null,
					null);
			exporter.doExport(pathway, pathway.getMappInfo().getMapInfoName(),
					output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultdir;
	}

	protected void datanodeLayout(Pathway pathway) {
		int i = 1;
		int x = 100;
		int y = 100;
		List<PathwayElement> dataobjects = pathway.getDataObjects();
		for (int index = 0; index < dataobjects.size(); index++) {
			PathwayElement pwyele = dataobjects.get(index);
			if (pwyele.getObjectType() == ObjectType.DATANODE) {
				if (i <= 9) {
					pwyele.setMCenterX(x);
					pwyele.setMCenterY(y);
					i++;
				} else {
					i = 1;
					x = x + 160;
					y = 100;
					pwyele.setMCenterX(x);
					pwyele.setMCenterY(y);
				}
				y = y + 80;
			}
		}
	}

	protected String addDataNode(Pathway pathway, String datanodename,
			String datanodetype, String id, String source, String resultdir) {

		// create new Datanode
		datanode = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		datanode.setDataNodeType(datanodetype);
		datanode.setGraphId(pathway.getUniqueGraphId());

		// Graphics
		if (datanodetype.equalsIgnoreCase("metabolite")) {
			datanode.setColor(Color.BLUE);
		}
		if (datanodetype.equalsIgnoreCase("pathway")) {
			datanode.setColor(new Color(20, 150, 30));
			datanode.setMFontSize(12);
			datanode.setBold(true);
			datanode.setShapeType(ShapeType.NONE);
		}
		datanode.setTextLabel(datanodename);
		datanode.setMWidth(datanodename.length() + 110);
		datanode.setMHeight(20);

		/*
		 * Not suitable if datanodename is long
		 */
		// datanode.setInitialSize();

		// add datanode to pathway
		pathway.add(datanode);

		// layout all datanodes
		datanodeLayout(pathway);

		// save Pathway
		savePathway(pathway, resultdir);

		// annotate Datanode
		if (id.length() > 0 && source.length() > 0) {
			annotateElement(pathway, datanodename, id, source, resultdir);
		}

		String identifier = datanode.getGraphId();

		return identifier;
	}

	protected String addLineByID(Pathway pathway, String linename,
			String node1, String node2, String starttype, String endtype,
			String resultdir) throws ConverterException {

		// create new Line
		line = PathwayElement.createPathwayElement(ObjectType.LINE);

		// get datanodes
		PathwayElement firstnode = pathway.getElementById(node1);
		PathwayElement secondnode = pathway.getElementById(node2);

		addLineGraphics(pathway, firstnode, secondnode, linename, endtype,
				starttype, resultdir);
		return line.getGraphId();
	}

	protected String addLine(Pathway pathway, String linename, String node1,
			String node2, String starttype, String endtype, String resultdir) {

		getElementbyName(pathway, node1);
		PathwayElement firstnode = pwyelement;
		getElementbyName(pathway, node2);
		PathwayElement secondnode = pwyelement;

		addLineGraphics(pathway, firstnode, secondnode, linename, starttype,
				endtype, resultdir);
		return line.getGraphId();

	}

	private void addLineGraphics(Pathway pathway, PathwayElement startnode,
			PathwayElement endnode, String linename, String starttype,
			String endtype, String resultdir) {

		// create new Line
		line = PathwayElement.createPathwayElement(ObjectType.LINE);
		line.setGraphId(pathway.getUniqueGraphId());
		if (startnode.getMCenterX() < endnode.getMCenterX()) {
			line.setMStartY(startnode.getMCenterY());
			line.setMStartX((startnode.getMCenterX() + startnode.getMWidth() / 2));
			line.setMEndY(endnode.getMCenterY());
			line.setMEndX((endnode.getMCenterX() - endnode.getMWidth() / 2));
		} else {
			line.setMStartX(startnode.getMCenterX());
			line.setMStartY((startnode.getMCenterY() + startnode.getMHeight() / 2));
			line.setMEndX(endnode.getMCenterX());
			line.setMEndY((endnode.getMCenterY() - endnode.getMHeight() / 2));
		}
		line.getMStart().linkTo(startnode);
		line.getMEnd().linkTo(endnode);

		// register MIM shapes
		MIMShapes.registerShapes();
		// set arrows
		line.setEndLineType(LineType.fromName(endtype));
		line.setStartLineType(LineType.fromName(starttype));

		// set textlabel
		line.setTextLabel(linename);

		// add Line to Pathway
		pathway.add(line);

		// save Pathway
		savePathway(pathway, resultdir);

	}

	protected List<String> getDatanodetypes() {
		List<String> datanodelist = new ArrayList<String>();
		for (String s : DataNodeType.getNames()) {
			datanodelist.add(s);
		}
		return datanodelist;
	}

	protected static String[] getMIMTypes() {
		String[] mimtypes = { "mim-necessary-stimulation", "mim-binding",
				"mim-conversion", "mim-stimulation", "mim-modification",
				"mim-catalysis", "mim-inhibition", "mim-cleavage",
				"mim-covalent-bond", "mim-branching-left",
				"mim-branching-right", "mim-transcription-translation",
		"mim-gap" };
		return mimtypes;
	}

	protected List<String> getLinetypes() {
		List<String> arrowlist = new ArrayList<String>();
		String[] arrows = LineType.getVisibleNames();
		String[] mims = getMIMTypes();
		for (String s : arrows) {
			arrowlist.add(s);
		}
		for (String m : mims) {
			arrowlist.add(m);
		}
		return arrowlist;
	}

	protected List<String> getGraphIDs(Pathway pathway, String elementname) {
		List<String> elementlist = new ArrayList<String>();
		List<PathwayElement> dataobjects = pathway.getDataObjects();
		for (int index = 0; index < dataobjects.size(); index++) {
			PathwayElement pwyele = dataobjects.get(index);
			if (pwyele.getTextLabel().equalsIgnoreCase(elementname)) {
				elementlist.add(pwyele.getGraphId());
			}
		}
		if (elementlist.isEmpty()) {
			elementlist.add("Element not found in the pathway!");
		}
		return elementlist;
	}

	private void getElementbyName(Pathway pathway, String elementname) {
		List<PathwayElement> dataobjects = pathway.getDataObjects();
		for (int index = 0; index < dataobjects.size(); index++) {
			PathwayElement pwyele = dataobjects.get(index);
			if (pwyele.getTextLabel().equalsIgnoreCase(elementname)) {
				pwyelement = pwyele;
			}
		}
	}

	protected String annotateElement(Pathway pathway, String elementname,
			String id, String source, String resultdir) {

		// get datanode
		getElementbyName(pathway, elementname);

		// Xref
		if (source.length() > 1) {
			pwyelement.setDataSource(DataSource.getByFullName(source));
		}
		if (id.length() > 1) {
			pwyelement.setElementID(id);
		}
		pathway.add(pwyelement);

		// save Pathway
		savePathway(pathway, resultdir);

		return pwyelement.getGraphId();
	}

	protected void removeElement(Pathway pathway, String elementname,
			String resultdir) {

		// get element
		getElementbyName(pathway, elementname);

		// remove datanode
		pathway.remove(pwyelement);

		// save Pathway
		savePathway(pathway, resultdir);

	}

	protected void removeElementById(Pathway pathway, String elementid,
			String resultdir) throws ConverterException {

		// get element
		pwyelement = pathway.getElementById(elementid);

		// remove datanode
		pathway.remove(pwyelement);

		// save Pathway
		savePathway(pathway, resultdir);

	}
}