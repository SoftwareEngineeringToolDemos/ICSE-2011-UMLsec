package tum.umlsec.viki.tools.uml2java;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;


public class Starter{

 private IMdrContainer container;
    
 private ITextOutput _mainoutput;
 private String newline = System.getProperty( "line.separator" );

public Starter(IMdrContainer container,ITextOutput _mainoutput){
  this.container=container;
  this._mainoutput=_mainoutput;
}
 public String Start1(String key){
//code

	String result = "";
	JAVAGenerator JG = new JAVAGenerator(container,_mainoutput);
	String parameter_2 = JG.getListeArgumentsOfMsg(2);
    String  Start1_1 = JG.evaluationOfArgument((String)JG.findArg(key).elementAt(0),25);

   

	boolean guard_2 =true;

	if(guard_2){
		
	 }

	else result ="der Protokoll wird bei "+"Start1"+" bei "+"Starter"+" abgebrochen, die Methode "+newline+"Start"+" bei "+"Host"+" wird nicht aufgerufen. ";
		return result;

 }



 

 }

