package org.pathvisio.xmlrpc;

import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.pathvisio.core.Engine;
import org.pathvisio.core.model.BatikImageExporter;
import org.pathvisio.core.model.GpmlFormat;
import org.pathvisio.core.model.ImageExporter;
import org.pathvisio.core.model.MappFormat;
import org.pathvisio.core.model.RasterImageExporter;
import org.pathvisio.core.preferences.PreferenceManager;

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
