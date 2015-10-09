package tum.umlsec.viki.tools.riskFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;

/**
 * Klasse zum Testen des Algorithmus <br>
 * Validierung
 */
public class TestRiskfinder {
	
	public static Document securityRepository;
	public static XMLHelper xmlhelper;
	public static RepositoryHelper repohelper;
	public static File repositoryFile;
	public static File normTextFile;
	public static File secTextFile;
	public static File maxsecTextFile;
	public static RiskHelper riskhelper;
	public static WortschatzHelper wortschatz;
	public static Vector<UMLsecPattern> UMLsecPatternVector;
	public static Vector<String> normWordVector;
	public static Vector<String> secWordVector;
	public static Vector<String> maxsecWordVector;
	public static Vector<String> synonymResult;
	
	public static void main(String[] args) {
		boolean on = false;
		int trefferNormM = 0;
		int trefferSecM = 0;
		int trefferNormO = 0;
		int trefferSecO = 0;
		int treffermaxSecM = 0;
		int treffermaxSecO = 0;
		int anzahlWorteNorm = 0;
		int anzahlWorteSec = 0;
		int anzahlWorteSecMax = 0;
		
		repohelper = new RepositoryHelper();
		xmlhelper = new XMLHelper();
		wortschatz = new WortschatzHelper();
		normWordVector = new Vector<String>();
		secWordVector = new Vector<String>();
		maxsecWordVector = new Vector<String>();
		synonymResult = new Vector<String>();
		
		// load security repository
		repositoryFile = new File ("bin"+ File.separator +"tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "repository" + File.separator + "securityRepository.xml");
		securityRepository = xmlhelper.parseXMLFile(repositoryFile);
		UMLsecPatternVector = repohelper.getAllUMLsecPatterns(repositoryFile);
		
		//load files
		normTextFile = new File ("bin"+ File.separator +"tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "examples" + File.separator + "normText.txt");
		secTextFile = new File ("bin"+ File.separator +"tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "examples" + File.separator + "secText.txt");
		maxsecTextFile = new File ("bin"+ File.separator +"tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "examples" + File.separator + "maxsecText.txt");
		
		//count words
		anzahlWorteNorm =countWords(normTextFile);
		anzahlWorteSec = countWords(secTextFile);
		anzahlWorteSecMax = countWords(maxsecTextFile);
		
		//load text into vektor		
		normWordVector = getWorteFromFile(normTextFile, normWordVector, anzahlWorteNorm);
		secWordVector = getWorteFromFile(secTextFile, secWordVector, anzahlWorteSec);
		maxsecWordVector = getWorteFromFile(maxsecTextFile, maxsecWordVector, anzahlWorteSecMax);
		
		// Normaler Text ohne Wortschatz
		for (int i=0; i<normWordVector.size(); i++){
			//wortmenge erweitern durch synonym und wortstamm
			synonymResult.add(normWordVector.get(i));
			if (on == true)
			synonymResult.addAll(askWortschatz(normWordVector.get(i)));
			
		}
		//algorithmus		
		trefferNormO = runAlgo(synonymResult, trefferNormO);
		synonymResult.removeAllElements();		
		
		// Normaler Text mit Wortschatz
		for (int i=0; i<normWordVector.size(); i++){
			//wortmenge erweitern durch synonym und wortstamm
			synonymResult.add(normWordVector.get(i));
			if (on == false)
			synonymResult.addAll(askWortschatz(normWordVector.get(i)));
			
		}
		//algorithmus		
		trefferNormM = runAlgo(synonymResult, trefferNormM);
		synonymResult.removeAllElements();		
//-----------------		
		// Security Text ohne Wortschatz
		for (int i=0; i<secWordVector.size(); i++){
			//wortmenge erweitern durch synonym und wortstamm
			synonymResult.add(secWordVector.get(i));
			if (on == true)
			synonymResult.addAll(askWortschatz(secWordVector.get(i)));
						
		}
		//algorithmus
		trefferSecO = runAlgo(synonymResult, trefferSecO);
		synonymResult.removeAllElements();
	//-----------	
		// Security Text mit Wortschatz
		for (int i=0; i<secWordVector.size(); i++){
			//wortmenge erweitern durch synonym und wortstamm
			synonymResult.add(secWordVector.get(i));
			if (on == true)
			synonymResult.addAll(askWortschatz(secWordVector.get(i)));
						
		}
		//algorithmus
		trefferSecM = runAlgo(synonymResult, trefferSecM);
		synonymResult.removeAllElements();		
//----------------
		//max Security Text ohne Wortschatz
		for (int i=0; i<maxsecWordVector.size(); i++){
			//wortmenge erweitern durch synonym und wortstamm
			synonymResult.add(maxsecWordVector.get(i));
			if (on == true)
			synonymResult.addAll(askWortschatz(maxsecWordVector.get(i)));
						
		}
		//algorithmus
		treffermaxSecO = runAlgo(synonymResult, treffermaxSecO);
		synonymResult.removeAllElements();
	//---------
		//max Security Text mit Wortschatz
		for (int i=0; i<maxsecWordVector.size(); i++){
			//wortmenge erweitern durch synonym und wortstamm
			synonymResult.add(maxsecWordVector.get(i));
			if (on == true)
			synonymResult.addAll(askWortschatz(maxsecWordVector.get(i)));
						
		}
		//algorithmus
		treffermaxSecM = runAlgo(synonymResult, treffermaxSecM);
		synonymResult.removeAllElements();				
		
		JOptionPane.showMessageDialog(null, "Ergebnisse der Validierung:\n\n" +
				"- - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
				"Anzahl Worte in Normal-Text: " + anzahlWorteNorm +"\n" + 
				"Treffer in Normal-Text mit Wortschatz: " + trefferNormM +"\n"+
				"Treffer in Normal-Text ohne Wortschatz: " + trefferNormO +"\n" +
				"- - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
				"- - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
				"Anzahl Worte in Secure-Text: " + anzahlWorteSec +"\n" +
				"Treffer in Secure-Text mit Wortschatz: " + trefferSecM + "\n" +
				"Treffer in Secure-Text ohne Wortschatz: " + trefferSecO + "\n" + 
				"- - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
				"- - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
				"Anzahl Worte in max Secure-Text: " + anzahlWorteSecMax +"\n" +
				"Treffer in Secure-Text mit Wortschatz: " + treffermaxSecM + "\n" +
				"Treffer in Secure-Text ohne Wortschatz: " + treffermaxSecO + "\n" + 
				"- - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
		
		
		System.out.println("Ergebnisse der Validierung:\n");
		
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		System.out.println("Anzahl Worte in Normal-Text: " + anzahlWorteNorm);
		System.out.println("Treffer in Normal-Text mit Wortschatz: " + trefferNormM);
		System.out.println("Treffer in Normal-Text ohne Wortschatz: " + trefferNormO);

		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		System.out.println("Anzahl Worte in Secure-Text: " + anzahlWorteSec);		
		System.out.println("Treffer in Secure-Text mit Wortschatz: " + trefferSecM);
		System.out.println("Treffer in Secure-Text ohne Wortschatz: " + trefferSecO);
		
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		System.out.println("Anzahl Worte in maxSecure-Text: " + anzahlWorteSecMax);
		System.out.println("Treffer in max Secure-Text mit Wortschatz: " + treffermaxSecM);
		System.out.println("Treffer in max Secure-Text ohne Wortschatz: " + treffermaxSecO);
		
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - -");
	}
	
	private static int runAlgo(Vector<String> syno, int treffer) {
		Vector<String> keywordsPattern;
		Vector<String> hitsVektor = new Vector<String>();
		
		// für jedes wort...
		for (int sy = 0; sy < syno.size(); sy++ ){
			//stopwords rausfiltern...
			if(notInBlacklist(syno.get(sy), getStopwords())){
				
				for (int p = 0; p < UMLsecPatternVector.size(); p++){
				//... und jedem wort im keyVector des Pattern....
				keywordsPattern = UMLsecPatternVector.get(p).getKeywords();
				
					for (int co = 0; co < keywordsPattern.size(); co++ ){
					//...vergleichen... 
						
						if (syno.get(sy).toLowerCase().equalsIgnoreCase(keywordsPattern.get(co).toLowerCase())){				
						// ... dann treffer 
						if(!hitsVektor.contains(syno.get(sy).toLowerCase())){
							hitsVektor.add(syno.get(sy).toLowerCase());
							treffer++;
						}
						}
					}
				keywordsPattern=null;
				}
			}
		}		
		return treffer;
	}	
	
	
	public static Vector<String> askWortschatz(String word){
		Vector<String> foundWords = new Vector<String>();
		
		
		foundWords.add(word);
		// Synonyms and Cooccurrences from Leipzig
		// good feature, but not necessary
		wortschatz.askWortschatz(word);   

		//add to Vector. empty till wortschatz is not working
		foundWords.addAll(wortschatz.getSynonyms());
		foundWords.addAll(wortschatz.getCooccurrences());
		
		return foundWords;
	}
	
	private static Vector<String> getStopwords(){
		Vector<String> blacklist = new Vector<String>();
		String dir ="bin"+ File.separator + "tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "stopwords" + File.separator ;
		String aktline = "";
		BufferedReader inFile = null;
		try {
			//ger
        	inFile = new BufferedReader (new FileReader (new File(dir + "stopwords.ger")));
			aktline = inFile.readLine();
	        while (aktline!= null)
	        {
	        	blacklist.add(aktline);
	        	aktline = inFile.readLine();
	        }
	        inFile.close();
        } catch (IOException e) {
        	System.out.println("stopwords io error");
		}
        return blacklist;
	}
	
	private static boolean notInBlacklist(String word, Vector<String> blacklist){
		
        for (int b=0; b < blacklist.size(); b++){
			if (blacklist.get(b).equalsIgnoreCase(word)){
				return false;
			}
		}
		return true;
	}

	private static Vector<String> getWorteFromFile(File textFile,Vector<String> wortVektor, int anzahlWorte) {
		
		String aktline = "";
	    try {
	        BufferedReader inFile = new BufferedReader (new FileReader (textFile));
			aktline = inFile.readLine();

	        while (aktline!= null)
	        {
	        	if (!aktline.equalsIgnoreCase("")){
	        		String [] newArray = aktline.split(" ");
	        		anzahlWorte = anzahlWorte + newArray.length;
	        		for (int i = 0; i< newArray.length; i++){
	        			wortVektor.add(newArray[i]);
	        		}
	        		
	        	}
	        	aktline = inFile.readLine();
	        }
	        inFile.close();
	    } catch (IOException e) {
	    	System.out.println("lese io error");
		}
	    return wortVektor;
	}
	
	private static int countWords(File textFile) {
		int anzahl = 0;
		String aktline = "";
	    try {
	        BufferedReader inFile = new BufferedReader (new FileReader (textFile));
			aktline = inFile.readLine();

	        while (aktline!= null)
	        {
	        	if (!aktline.equalsIgnoreCase("")){
	        		String [] newArray = aktline.split(" ");
	        		anzahl = anzahl + newArray.length;	        		
	        	}
	        	aktline = inFile.readLine();
	        }
	        inFile.close();
	    } catch (IOException e) {
	    	System.out.println("lese io error");
		}
	    return anzahl;
	}
}
