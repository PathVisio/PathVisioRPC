package org.pathvisio.xmlrpc;

import java.io.File;

import javax.jws.WebMethod;

import org.pathvisio.core.Engine;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.StaticProperty;

public class PathwayFunctions
{
	private static Engine engine;
	
	public static void setEngine (Engine value)
	{
		engine = value;
	}
	
    @WebMethod
    public String test() {
        return "It works!";
    }
    
	@WebMethod
	public boolean newPathway()
	{
		engine.newPathway();
		return true;
	}
	
	@WebMethod
	public boolean openPathway (String filename) throws ConverterException
	{
		engine.openPathway(new File(filename));
		return true;
	}
	
	@WebMethod
	public boolean exportPathway (String filename) throws ConverterException
	{
		engine.exportPathway(new File(filename), engine.getActivePathway());
		return true;
	}
	
	@WebMethod
	public boolean setFlux (String id, double value)
	{
		PathwayElement elt = engine.getActivePathway().getElementById(id);
		elt.setLineThickness(value);
		return true;
	}

	@WebMethod
	public boolean setProperty (String id, String key, String value)
	{
		PathwayElement elt = engine.getActivePathway().getElementById(id);

		StaticProperty prop = StaticProperty.valueOf(key);
		elt.setStaticProperty(prop, value);
		return true;
	}

	@WebMethod
	public boolean setProperty (String id, String key, double value)
	{
		PathwayElement elt = engine.getActivePathway().getElementById(id);

		StaticProperty prop = StaticProperty.valueOf(key);
		elt.setStaticProperty(prop, value);
		return true;
	}

	@WebMethod
	public String getProperty (String id, String key)
	{
		PathwayElement elt = engine.getActivePathway().getElementById(id);
		StaticProperty prop = StaticProperty.valueOf(key);
		return "" + elt.getStaticProperty(prop);
	}

	@WebMethod
	public String addElement (String type)
	{
		ObjectType ot = ObjectType.valueOf(type);
		PathwayElement elt = PathwayElement.createPathwayElement(ot);
		Pathway pwy = engine.getActivePathway();
		pwy.add(elt);
		elt.setGeneratedGraphId();
		return elt.getGraphId();
	}
	
	@WebMethod
	public boolean removeElement (String id)
	{
		Pathway pwy = engine.getActivePathway();
		PathwayElement elt = pwy.getElementById(id);
		pwy.remove(elt);
		return true;
	}

}
