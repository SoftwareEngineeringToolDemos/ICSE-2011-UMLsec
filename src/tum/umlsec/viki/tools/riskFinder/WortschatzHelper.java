package tum.umlsec.viki.tools.riskFinder;

import java.util.Vector;

import tum.umlsec.viki.tools.riskFinder.wortschatz.CooccurrencesClient;
import tum.umlsec.viki.tools.riskFinder.wortschatz.SynonymsClient;
/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de<br>
 * 
 * Klasse bildet Schnittstelle zur Wortschatz Projket
 *
 */
public class WortschatzHelper {
	
	String[][] synResult = null;
	String[][] coocResult = null;
	
	public WortschatzHelper(){
		
	}

	/**
	 * liefert Ergebnis der Synonym-Anfrage
	 * @return Vector<String> result
	 */
	public Vector<String> getSynonyms(){

		Vector<String> result = new Vector<String>();
		if (synResult != null){
			for (int i = 0; i < synResult.length; i++){
				result.add(synResult[i][0]);
			}
		}
		return result;
	}

	/**
	 * liefert Ergebnis der Cooccurrences-Anfrage
	 * @return Vector<String> result
	 */
	public Vector<String> getCooccurrences(){

		Vector<String> result = new Vector<String>();
		if (coocResult != null){
			for (int i = 0; i < coocResult.length; i++){
				result.add(coocResult[i][1]);
			}
			
		}
		return result;
	}

	/**
	 * schickt searchPattern-Anfrage an deutsche Wortschatz Webservices
	 * @param String searchPattern
	 * 
	 */
	public void askWortschatz(String searchPattern){

		try {
			SynonymsClient sc = new SynonymsClient();
			CooccurrencesClient cc = new CooccurrencesClient();

			String username = "anonymous";
			String password = "anonymous";
			String corpus = "de";
			String limit = "10";
			String signifikanz = "10";
			
			//synonym 
			sc.setUsername(username);
			sc.setPassword(password);
			sc.setCorpus(corpus);
			sc.addParameter("Wort", searchPattern);
			sc.addParameter("Limit", limit);
			
			sc.execute();
			synResult = sc.getResult();
			//System.out.println("länge: " + synResult.length);
			//for (int i = 0; i < synResult.length; i++){
			//	System.out.println(synResult[i][0]);
			//}
			
			//cooccurrences
			cc.setCorpus(corpus);
			cc.setPassword(password);
			cc.setUsername(username);
			cc.addParameter("Wort", searchPattern);
			cc.addParameter("Mindestsignifikanz", signifikanz);
			cc.addParameter("Limit", limit);
			cc.execute();
			coocResult = cc.getResult();
			//System.out.println("länge: " + coocResult.length);
			//for (int i = 0; i < coocResult.length; i++){
			//	System.out.println(coocResult[i][1]);
			//}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	

}
