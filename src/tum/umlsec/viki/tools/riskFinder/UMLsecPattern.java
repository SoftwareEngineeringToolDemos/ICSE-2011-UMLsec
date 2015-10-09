package tum.umlsec.viki.tools.riskFinder;

import java.util.Vector;

/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de<br>
 * 
 * bildet Schablone für Sicherheitspattern
 *
 */
public class UMLsecPattern {

	//name of the pattern
	private String name ="";
	//typ of the pattern
	private String typ ="";
	// List of Keywords
	private Vector<String> keywords = new Vector<String>();
	// solution proposals
	private Vector<String> solutions = new Vector<String>();
	// risks
	private Vector<String> risks = new Vector<String>();
	// riskFactor
	private int riskFactor = 0;
	
	public UMLsecPattern(){
		
	}
	
	public UMLsecPattern(	String newName, 
							String newTyp,
							Vector<String> newKeywords, 
							Vector<String> newSolutions,
							Vector<String> newRisks,
							int newRiskFactor
							){

		name = newName;
		typ = newTyp;
		keywords = newKeywords;
		solutions = newSolutions;
		risks = newRisks;
		riskFactor = newRiskFactor;
	}
	
	public Vector<String> getSolutions(){
		return solutions;
	}
	
	public Vector<String> getKeywords(){
		return keywords;
	}
	
	public String getTyp(){
		return typ;
	}
	
	public String getName(){
		return name;
	}
	
	public Vector<String> getRiks(){
		return risks;
	}
	
	public int getRiskFactor(){
		return riskFactor;
	}
	
}
