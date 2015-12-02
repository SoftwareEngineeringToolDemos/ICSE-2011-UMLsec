package tum.umlsec.viki.tools.riskFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de<br>
 * Schnittstelle zur secReq Funktionen und Ergebnissen
 *
 */
public class secReqHelper {
	private static Logger logger=Logger.getLogger("riskFinder.secReqHelper");
	private HashMap<String, String> secReq = new HashMap<String, String>();
	private String dir ="bin"+ File.separator +"tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "secReq" + File.separator;
	
	public secReqHelper(){
		
	}

	/**
	 * liest Ergebnis-Datei in eine Hashmap ein
	 * @param File contentFile
	 * @param HashMap<String, String> contentHashmap. Key(word) value(percent)
	 * @return HashMap<String, String> result
	 */
	private HashMap<String, String> getContentFromSecReqFile (File contentFile, HashMap<String, String> aHashMap){

		// open file
		if (contentFile.exists() && !contentFile.isDirectory()) {
	        String aktline = "";
	        try {
		        BufferedReader inFile = new BufferedReader (new FileReader (contentFile));
				aktline = inFile.readLine();

		        while (aktline!= null)
		        {
		        	if (!aktline.equalsIgnoreCase("")){
			        	//parse line by line into HashMap
			        	aktline = aktline.trim();
			        	int index = aktline.indexOf(";");
			        	//get key (word) and risk(percent)
						aHashMap.put(aktline.substring(0, index).trim(), aktline.substring(index +1).trim());
		        	}
		        	aktline = inFile.readLine();
		        }
		        inFile.close();
	        } catch (IOException e) {
	        	logger.fatal("getContentFromSecReqFile io error");
			}
		}
		return aHashMap;
	}
	
	/**
	 * ruft getContentFromSecReqFile auf
	 * @return HashMap<String, String> 
	 */
	public HashMap<String, String> getSecReq(){
		return getContentFromSecReqFile(new File(dir + "secReq.txt"), secReq);
	}
}	
	
	

