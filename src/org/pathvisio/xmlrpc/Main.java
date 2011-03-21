package org.pathvisio.xmlrpc;

import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.pathvisio.Engine;
import org.pathvisio.model.BatikImageExporter;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.ImageExporter;
import org.pathvisio.model.MappFormat;
import org.pathvisio.model.RasterImageExporter;
import org.pathvisio.preferences.PreferenceManager;

public class Main
{
	public static void main(String[] args) throws XmlRpcException, IOException
	{
		PreferenceManager.init();
		Engine engine = new Engine();
		engine.addPathwayExporter(new MappFormat());
		engine.addPathwayExporter(new GpmlFormat());
		engine.addPathwayExporter(new BatikImageExporter(ImageExporter.TYPE_PDF));
		engine.addPathwayExporter(new BatikImageExporter(ImageExporter.TYPE_SVG));
		engine.addPathwayExporter(new RasterImageExporter(ImageExporter.TYPE_PNG));
		Server server = new Server(engine);
		server.init(server.getDefaultPort());
	}
}
