
package tum.umlsec.viki.tools.sequence2prolog;

/**
 * @author gurvanov
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


import java.io.File;
import java.io.FileReader;

public class FileBuilder {
	private StringBuffer body;
	private int indentCounter=0;
	private int SpaceCounter=0;

	public FileBuilder(){

			body= new StringBuffer("");

	}

	public void appendFromFile(String  ImportFileName){
		
		File file;
		FileReader in = null;
		try{
			file= new File(ImportFileName);
			in = new FileReader(file);
			char[] buffer = new char[1024];
			int len;
			while((len = in.read(buffer)) != -1){
				String s = new String(buffer,0,len);
				body.append(s);
			}
		
		}catch(Exception e){
			body.append("File:"+ImportFileName);
			e.printStackTrace();
		}
		finally{//close in
			try{
				if(in!=null){
					in.close();
				}
			}catch(Exception e){
				body.append("File:"+ImportFileName);
				e.printStackTrace();
			}
		}

	}
	
	
	public void append(String str){
			body.append(str);
	}
	public void append(StringBuffer sb){
			body.append(sb);
	}	
	
	public void append(FileBuilder exp){
			body.append(exp.toString());
	}
	
	public void appendln(String str){
			newline();		
			appendInd();
			appendSp();
			body.append(str);
		
	}	
	

	public void newline(){
		body.append(System.getProperty("line.separator"));
	}
	private void appendSp(){
		body.append(repeat(' ',SpaceCounter));
		
	}
	
	public void popSp(){
	}
	
	public void pushSp(int rep){
			//TestCode:body.append("\npush"+indentCounter+"\\t");
		SpaceCounter= SpaceCounter+rep;
		
	}
	
	public void popSp(int rep){
		
		SpaceCounter=SpaceCounter-rep;
		//Testcode:body.append("pop"+indentCounter+"\\t");
	
	}
	public void appendInd(){
			//TestCode:body.append("\npush"+indentCounter+"\\t");
		body.append(repeat('\t',indentCounter));
		
	}
	public void pushInd(){
			//TestCode:body.append("\npush"+indentCounter+"\\t");
		body.append(repeat('\t',indentCounter));
		indentCounter++;
		
	}
	
	public void popInd(){
		
		indentCounter--;
		//Testcode:body.append("pop"+indentCounter+"\\t");
	
	}
	
	
	
	/**
	* Produce a String of a given repeating character.
	* @param c the character to repeat
    * @param count the number of times to repeat
	* @return String, e.g. repeat('*',4) returns "****"
	*/
	public final static String repeat (char c, int count){
		char[] s = new char[count];
		for ( int i=0; i < count; i++ ){
			s[i] = c;
		}
		return new String(s).intern();
	} // end repeat
	 
	public String toString(){
		return body.toString();
	}
	public boolean isEmpty(){
		return body.length()==0;
	}


}
