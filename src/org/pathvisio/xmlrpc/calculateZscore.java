package org.pathvisio.xmlrpc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
//import org.bridgedb.Xref;
//import org.bridgedb.bio.BioDataSource;
import org.pathvisio.gex.CachedData;
import org.pathvisio.gex.GexManager;
import org.pathvisio.plugins.statistics.StatisticsResult;
import org.pathvisio.plugins.statistics.ZScoreCalculator;
import org.pathvisio.preferences.PreferenceManager;
import org.pathvisio.visualization.colorset.Criterion;

public class calculateZscore implements XmlRpcHandlerMapping {
	
	public String calcZscore (String pathDir, String gexFile, String dbFile, String exprZ) throws IDMapperException, ClassNotFoundException, IOException{
		PreferenceManager.init();
		File pwDir = new File(pathDir);
		GexManager gex = new GexManager();
		gex.setCurrentGex(gexFile,false);
		CachedData cd = gex.getCachedData();
		
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
		cd.setMapper(gdb);
		
		Criterion crit = new Criterion();
		List<String> al;
		al = gex.getCurrentGex().getSampleNames();
		
		String error = crit.setExpression(exprZ,al);
		
		if (error != null) throw new IllegalStateException(error);
		
		ZScoreCalculator zsc = new ZScoreCalculator(crit, pwDir, cd, gdb, null);
		StatisticsResult statresalt = zsc.calculateMappFinder();
		statresalt.save(new File(pathDir + "Zscore_results_new.txt"));
		return ("It works check Zscore_results file in result_server folder!!");
		}
	
	@Override
	public XmlRpcHandler getHandler(String arg0)
				throws XmlRpcNoSuchHandlerException, XmlRpcException {
		// TODO Auto-generated method stub
		return null;
	}

}