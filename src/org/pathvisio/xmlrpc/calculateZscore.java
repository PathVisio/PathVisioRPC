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
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.statistics.StatisticsResult;
import org.pathvisio.statistics.ZScoreCalculator;

public class calculateZscore implements XmlRpcHandlerMapping {
	
	public String calcZscore (String gexFile, String dbFile, String pathDir, String exprZ) throws IDMapperException, ClassNotFoundException, IOException{
		PreferenceManager.init();
		File pwDir = new File(pathDir);
		GexManager gex = new GexManager();
		gex.setCurrentGex(gexFile,false);
			
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		IDMapper gdb = BridgeDb.connect("idmapper-pgdb:" + dbFile);
		gex.getCachedData().setMapper(gdb);
		Criterion crit = new Criterion();
		List<String> al;
		al = gex.getCurrentGex().getSampleNames();
		
		String error = crit.setExpression(exprZ,al);
		
		if (error != null) throw new IllegalStateException(error);
		ProgressKeeper pk = new ProgressKeeper();
		ZScoreCalculator zsc = new ZScoreCalculator(crit, pwDir, gex.getCachedData(), gdb, pk);
		StatisticsResult statresalt = zsc.calculateAlternative();
		System.out.println(statresalt.getCriterion().toString());
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