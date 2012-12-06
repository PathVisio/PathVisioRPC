package org.pathvisio.xmlrpc;

import java.io.File;
import java.util.List;
import org.bridgedb.DataSource;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.StaticProperty;
import org.pathvisio.core.preferences.PreferenceManager;

/**
 * Create , open and save Pathway files(gpml and svg)
 * add, edit and remove Datanodes and Lines 
 * over xmlrpc calls
 * @author anwesha
 */
public class PathwayHandler {
	
	public String testPathwayHandler(){
		return "it works!";
	}
	
	private PathwayElement mappInfo = null;
	private PathwayElement infoBox = null;
	private PathwayElement pwyelement = null;
	private PathwayElement datanode = null;
	private PathwayElement line = null;
	
	
	public String createPathway(String pathwayauthor, String pathwayname){
		PreferenceManager.init();
		Pathway pathway = new Pathway();
		
		mappInfo = PathwayElement.createPathwayElement(ObjectType.MAPPINFO);
		mappInfo.setStaticProperty(StaticProperty.MAPINFONAME, pathwayname);
		mappInfo.setStaticProperty(StaticProperty.AUTHOR, pathwayauthor);
		pathway.add (mappInfo);
		
		infoBox = PathwayElement.createPathwayElement(ObjectType.INFOBOX);
		pathway.add (infoBox);
		
		File pathwayfile = new File (pathwayname+".gpml");
		try {
			pathway.writeToXml(pathwayfile, true);
		} catch (ConverterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathwayname+ " GPML file created in working directory";
	}
	
	public Pathway openPathway(String pathwayname){
		PreferenceManager.init();
		Pathway pathway = new Pathway();
		File gpmlfile= new File(pathwayname+".gpml"); 
		try {
			pathway.readFromXml(gpmlfile, true);
		} catch (ConverterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathway;
	}
	
	public void savePathway(String pathwayname, Pathway pathway){
		PreferenceManager.init();
		File pathwayfile = new File (pathwayname+".gpml");
		try {
			pathway.writeToXml(pathwayfile, true);
		} catch (ConverterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String createPathwayImage(String pathwayname){
		PreferenceManager.init();
		Pathway pathway = openPathway(pathwayname);
		File pathwayfile = new File (pathwayname+".svg");
		try {
			pathway.writeToSvg(pathwayfile);
		} catch (ConverterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathwayname+" image (svg) file created in working directory";
	}
	
	public void datanodeLayout(Pathway pathway){
		int i = 1;
		int x = 100;
		int y = 100;
		List<PathwayElement> dataobjects = pathway.getDataObjects();
		for(int index = 0 ; index < dataobjects.size(); index ++){
			PathwayElement pwyele = dataobjects.get(index);
			if(pwyele.getObjectType() == ObjectType.DATANODE){
				if(i <= 9){
					pwyele.setMCenterX(x);
					pwyele.setMCenterY(y);
					i++;
				}else{
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
		
	public String addDataNode(String pathwayname, String datanodename, 
			String datanodetype){
				
		//open Pathway
		Pathway pathway = openPathway(pathwayname);
		
		//create new Datanode
		datanode = PathwayElement.createPathwayElement(ObjectType.DATANODE);
				
		datanode.setDataNodeType(datanodetype);
		datanode.setTextLabel(datanodename);
				
		//Graphics
		datanode.setInitialSize();
		
		
		//add datanode to pathway
		pathway.add(datanode);
		
		//layout all datanodes
		datanodeLayout(pathway);
		
		//save Pathway
		savePathway(pathwayname, pathway);
		
		return datanodename + " added to "+pathwayname;
	}
	
	public String addLine(String pathwayname, String linename, String node1, String node2, String starttype, String endtype){
		
		//open Pathway
		Pathway pathway = openPathway(pathwayname);
				
		//create new Line
		line = PathwayElement.createPathwayElement(ObjectType.LINE);
		
		//get datanodes
		getElementbyName(pathway, node1);
		PathwayElement firstnode = pwyelement;
		getElementbyName(pathway, node2);
		PathwayElement secondnode = pwyelement;
		
		//set position
		if(firstnode.getMCenterX() < secondnode.getMCenterX()){
			line.setMStartY(firstnode.getMCenterY());
			line.setMStartX((firstnode.getMCenterX()+firstnode.getMWidth()/2));
			line.setMEndY(secondnode.getMCenterY());
			line.setMEndX((secondnode.getMCenterX()-secondnode.getMWidth()/2));
		}else{
			line.setMStartX(firstnode.getMCenterX());
			line.setMStartY((firstnode.getMCenterY()+firstnode.getMHeight()/2));
			line.setMEndX(secondnode.getMCenterX());
			line.setMEndY((secondnode.getMCenterY()-secondnode.getMHeight()/2));
		}
		//set arrows
		line.setEndLineType(LineType.fromName(endtype));
		line.setStartLineType(LineType.fromName(starttype));
		
		//set textlabel
		line.setTextLabel(linename);
		
		//add Line to Pathway
		pathway.add(line);
		
		//save Pathway
		savePathway(pathwayname, pathway);
			
		return linename+" added to "+pathwayname;
	}
	
	public void getElementbyName(Pathway pathway, String datanodename){
		List<PathwayElement> dataobjects = pathway.getDataObjects();
		for(int index = 0 ; index < dataobjects.size(); index ++){
			PathwayElement pwyele = dataobjects.get(index);
			if(pwyele.getTextLabel().equalsIgnoreCase(datanodename)){
				pwyelement = pwyele;
			}
		}
	}
	
	public String annotateElement(String pathwayname, String datanodename, String datanodeid, String datanodesource){
		
		//open Pathway
		Pathway pathway = openPathway(pathwayname);
	
		//get datanode 
		getElementbyName(pathway, datanodename);
					
		//Xref
		if(datanodesource.length() > 1){
			pwyelement.setDataSource(DataSource.getByFullName(datanodesource));
			}
		if(datanodeid.length() > 1){
			pwyelement.setGeneID(datanodeid);
			}	
		pathway.add(pwyelement);
		
		//save Pathway
		savePathway(pathwayname, pathway);		
		
		return datanodename +" in "+pathwayname+" annotated";
	}
	
	public String removeElement(String pathwayname, String elementname){
		
		//open Pathway
		Pathway pathway = openPathway(pathwayname);
		
		//get element
		getElementbyName(pathway, elementname);
		
		//remove datanode
		//pwyelement = pathway.getElementById(pathwayelementname);
		pathway.remove(pwyelement);
		
		//save Pathway
		savePathway(pathwayname, pathway);
				
		return elementname+" in "+pathwayname+" removed";
	}
	
}