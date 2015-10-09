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
 * läd und verwaltet die Inahlete des BSI Katalog<br>
 * Diese werden für die Pattern benötigt
 *
 */
public class BsiContent {
	private HashMap<String, HashMap<String, String>> gefaerdungsKatalog;
	private HashMap<String, HashMap<String, String>> maßnahmenKatalog;
	private String gDir ="bin"+ File.separator +"tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "BSI" + File.separator + "G" + File.separator;
	private String mDir ="bin"+ File.separator +"tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "BSI" + File.separator + "M" + File.separator;
	private static Logger logger=Logger.getLogger("riskFinder.BsiContent");
	
	public BsiContent(){
		gefaerdungsKatalog = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> hoehereGewalt = new HashMap<String, String>();
		HashMap<String, String> organisatorischeMaengel = new HashMap<String, String>();
		HashMap<String, String> menschlicheFehlhandlung = new HashMap<String, String>();
		HashMap<String, String> technischesVersagen = new HashMap<String, String>();
		HashMap<String, String> vorsaetzlicheHandlungen = new HashMap<String, String>();
		HashMap<String, String> gDatenschutz = new HashMap<String, String>();

		hoehereGewalt = getContentFromFile(new File(gDir + "G1.txt"), hoehereGewalt);
		organisatorischeMaengel = getContentFromFile(new File(gDir + "G2.txt"), organisatorischeMaengel);
		menschlicheFehlhandlung = getContentFromFile(new File(gDir + "G3.txt"), menschlicheFehlhandlung);
		technischesVersagen = getContentFromFile(new File(gDir + "G4.txt"), technischesVersagen);
		vorsaetzlicheHandlungen = getContentFromFile(new File(gDir + "G5.txt"), vorsaetzlicheHandlungen);
		gDatenschutz = getContentFromFile(new File(gDir + "G6.txt"), gDatenschutz);
		
		gefaerdungsKatalog.put( "G 1 Höhere Gewalt", hoehereGewalt );
		gefaerdungsKatalog.put( "G 2 Organisatorische Mängel", organisatorischeMaengel );
		gefaerdungsKatalog.put( "G 3 Menschliche Fehlhandlung", menschlicheFehlhandlung );
		gefaerdungsKatalog.put( "G 4 Technisches Versagen", technischesVersagen );
		gefaerdungsKatalog.put( "G 5 Vorsätzliche Handlungen", vorsaetzlicheHandlungen );
		gefaerdungsKatalog.put( "G 6 Datenschutz", gDatenschutz );
		
		maßnahmenKatalog = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> infratruktur = new HashMap<String, String>();
		HashMap<String, String> organisation = new HashMap<String, String>();
		HashMap<String, String> personal = new HashMap<String, String>();
		HashMap<String, String> hardwareUndSoftware = new HashMap<String, String>();
		HashMap<String, String> kommunikation = new HashMap<String, String>();
		HashMap<String, String> notfallvorsorge = new HashMap<String, String>();
		HashMap<String, String> mDatenschutz = new HashMap<String, String>();

		infratruktur = getContentFromFile(new File(mDir + "M1.txt"), infratruktur);
		organisation = getContentFromFile(new File(mDir + "M2.txt"), organisation);
		personal = getContentFromFile(new File(mDir + "M3.txt"), personal);
		hardwareUndSoftware = getContentFromFile(new File(mDir + "M4.txt"), hardwareUndSoftware);
		kommunikation = getContentFromFile(new File(mDir + "M5.txt"), kommunikation);
		notfallvorsorge = getContentFromFile(new File(mDir + "M6.txt"), notfallvorsorge);
		mDatenschutz = getContentFromFile(new File(mDir + "M7.txt"), mDatenschutz);
				
		maßnahmenKatalog.put("M 1 Infrastruktur", infratruktur);
		maßnahmenKatalog.put("M 2 Organisation", organisation);
		maßnahmenKatalog.put("M 3 Personal", personal);
		maßnahmenKatalog.put("M 4 Hardware und Software", hardwareUndSoftware);
		maßnahmenKatalog.put("M 5 Kommunikation", kommunikation);
		maßnahmenKatalog.put("M 6 Notfallvorsorge", notfallvorsorge);
		maßnahmenKatalog.put("M 7 Datenschutz", mDatenschutz);

	}

	private HashMap<String, String> getContentFromFile (File contentFile, HashMap<String, String> aHashMap){
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
			        	aktline = aktline.replaceAll("	", " ");
			        	int indexFirstSpace = aktline.indexOf(" ");
			        	int indexSecondSpace = aktline.indexOf(" ", indexFirstSpace + 1);
			        	//key is the Number and Value is the text
						aHashMap.put(aktline.substring(0, indexSecondSpace), aktline.substring(indexSecondSpace +1));
		        	}
		        	aktline = inFile.readLine();
		        }
		        inFile.close();
	        } catch (IOException e) {
	        	logger.fatal("getContentFromFile io error");
			}
		}
		return aHashMap;
	}
	
	public HashMap<String, HashMap<String, String>> getGefaehrdungsKatalog(){
		/**
		 * gibt den Katalog für Gefährdungen zurück
		 * @return HashMap<String, HashMap<String, String>><br>
		 * Gefahrenkapitel, Hashmap mit Nummer, Bezeichnung
		 */
		return gefaerdungsKatalog;
	}
	
	public HashMap<String, HashMap<String, String>> getMassnahmenKatalog(){
		/**
		 * gibt den Katalog für Maßnahen zurück
		 * @return HashMap<String, HashMap<String, String>><br>
		 * Maßnahmenkapitel, Hashmap mit Nummer, Bezeichnung
		 */
		return maßnahmenKatalog;
	}
}	
	
	

