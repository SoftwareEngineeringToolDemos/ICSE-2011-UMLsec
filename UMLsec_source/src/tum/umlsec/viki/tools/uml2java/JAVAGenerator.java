



package tum.umlsec.viki.tools.uml2java;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.commonbehavior.Object;
import org.omg.uml.behavioralelements.commonbehavior.Stimulus;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;
import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.jmapper.xcbf.XcbfGenerator;


/**
 * @author Ahmed
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JAVAGenerator {
	// mdr container
	private IMdrContainer container;

	// line separator 
	private String newline = System.getProperty( "line.separator" );

	// map object data to variable name of object 
	private HashMap objects = new HashMap();
	private HashMap classinfos = new HashMap();

	private HashMap c2o = new HashMap();

	// specifies the needed security requirements | sebastian
	private String bioExchangeStereotype = "";

	// a sequence diagram exists
	private boolean sequencediagramExists = false;

	ITextOutput _mainOutput;
	//sebastian
	ITextOutput mainOutput;
	int Anzahl_von_stimulus ;      
	String start_Object="";
	String start_Class="";
	String start_Stimulus="";

	/** Creates a new instance of JavaGenerator */

	/** 
	 * @param _container stores the information about elements of a sequence diagram
	 * @param _mainOutput sends status information to a console application or a message window
	 */
	public JAVAGenerator(IMdrContainer _container, ITextOutput _mainOutput) {
		//sebastian
		mainOutput = _mainOutput;
		container = _container;
		if(container.getUmlPackage() == null) {
			_mainOutput.writeLn("WARNING:\n There is no xmi file loaded!\n\n");
			return;
		}

		// check if sequence diagram exists
		if(container.getUmlPackage().getCommonBehavior().getObject().refAllOfClass().iterator().hasNext())
			sequencediagramExists = true;


		int _index;
		// initialize objects        
		_index = 1;
		//put each object of the common behaviour container in the object hash map 
		for(Iterator it = container.getUmlPackage().getCommonBehavior().getObject().refAllOfClass().iterator();it.hasNext();) {
			Object _object = (Object)it.next();
			String _classname;
			try{
				UmlClass _umlclass = (UmlClass)_object.getClassifier().iterator().next();
				_classname = _umlclass.getName();
			}
			catch(java.util.NoSuchElementException e) {
				_classname = _object.getName();
				_mainOutput.writeLn("WARNING:\n There is an object in your model that has no classifier. Java generator needs this classifier to generate Java code. Java generator has allocated a default class name that probably won�t work correctly. \n Please check your model.\n\n");
			}
			String objectName ="Object" + String.valueOf(_index); 
			objects.put(objectName, new String[]{"\"" + _object.getName() + "\"", "\"" + _classname + "\""});
			_index++;

		}


	} //ende von der JAVAGenerator Konstruktor



	/**
	 * @param _mainOutput sends status information to a console application or a message window
	 * @return true, if the java file could be generated successful, else false
	 */
	public boolean createJAVAFile(  ITextOutput _mainOutput) {

		if(!sequencediagramExists) {
			_mainOutput.write("INFORMATION:\n There is no model to generate Java code from.\n\n");
			return false; 
		}

		else {

			try {

				this.getCodeOfClasses();

				Iterator classinfosKeys = classinfos.keySet().iterator();
				//the number of objects in the SD (the number of the Files)
				int i =objects.size() ; 


				String currClassCode="";

				while (classinfosKeys.hasNext()) {

					String myKey = classinfosKeys.next().toString();

					int myKey_size = myKey.length();
					char last_char = myKey.charAt(myKey_size-1);
					currClassCode = (String)classinfos.get(myKey);  

					String class_name = this.extractClassName(last_char);

					// sebastian
					String rootPath = System.getProperty("user.home");
					if (rootPath.equals("/usr/share/tomcat4")) {
						rootPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "output";
					}		

					BufferedWriter	javaFile = new BufferedWriter(new FileWriter(new File( rootPath + System.getProperty("file.separator") + class_name + ".java")));
					//BufferedWriter	javaFile = new BufferedWriter(new FileWriter(new File( "C:\\Dokumente und Einstellungen\\Ahmed\\Desktop\\alles\\eclipse\\eclipse-SDK-3.0-win32\\eclipse\\workspace\\umlsec\\tum\\umlsec\\viki\\tools\\uml2java"+"\\" + class_name + ".java")));

					javaFile.write(currClassCode);
					javaFile.close();

				}

				//sebastian begin
				if (!bioExchangeStereotype.equals("")) {
					String rootPath = System.getProperty("user.home");
					File _fi = new File(rootPath);
					if (rootPath.equals("/usr/share/tomcat4")) {
						rootPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "output";
					}
					XcbfGenerator _xg = new XcbfGenerator(rootPath);
					System.out.println("st: " + bioExchangeStereotype);
					_xg.xcbf2Java(bioExchangeStereotype);
				}
				//sebastian end
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(sequencediagramExists){
				_mainOutput.writeLn("INFORMATION:\n Sequence Diagram found.\n");

			}


			return true;
		}


	}


	/**
	 * @param className the name of the current class
	 * @param ObjectName the name of the current object which is part of the current class
	 * @return a string which consists of the code of the current class with the following syntax:
	 * 1) package declaration
	 * 2) package imports
	 * 3) method bodies
	 */

	private String getCodeOfJavaClass(String className,String ObjectName) throws Exception{
		// sebastian
		String _srcPath = System.getProperty("user.dir") + System.getProperty("file.separator") +
		// "bin" + fileSeparator +
		"tum" + System.getProperty("file.separator") + 
		"umlsec" + System.getProperty("file.separator") + 
		"viki" + System.getProperty("file.separator") + 
		"tools" + System.getProperty("file.separator") + 
		"uml2java" + System.getProperty("file.separator");

		String _srcPathWeb = System.getProperty("user.dir") + System.getProperty("file.separator") +
		"WEB-INF" + System.getProperty("file.separator") + 
		"classes" + System.getProperty("file.separator") + 
		"tum" + System.getProperty("file.separator") + 
		"umlsec" + System.getProperty("file.separator") + 
		"viki" + System.getProperty("file.separator") + 
		"tools" + System.getProperty("file.separator") + 
		"uml2java" + System.getProperty("file.separator");

		File myFile; 
		myFile = new File(
				_srcPath + 
				"class.pattern"		
		); 
		if (!myFile.exists()) {
			myFile = new File(_srcPathWeb + "class.pattern");
		}
		// sebastian end
		FileReader reader = new FileReader(myFile);
		BufferedReader buff = new BufferedReader(reader);


		String in;
		String ret="" ;
		ret+="package tum.umlsec.viki.tools.uml2java;"+newline+newline+


		"import tum.umlsec.viki.framework.ITextOutput;"+newline+
		"import tum.umlsec.viki.framework.mdr.IMdrContainer;"+newline+
		"import tum.umlsec.viki.tools.uml2java.JAVAGenerator;"+newline+"%imports%";//sebastian



		Set methods = new HashSet();
		Vector Messages = new Vector();


		while ((in = buff.readLine())!=null) {


			ret += in.replaceAll("%classname%","class "+className)+newline+newline;

			ret = ret.replaceAll("%code%","private IMdrContainer container;"+newline+
					this.getArgumentsOfMsgs(className)+
					" private ITextOutput _mainoutput;"+newline+
					" private String newline = System.getProperty( \"line.separator\" );"+newline+newline+"%code%");


			ret = ret.replaceAll("%code%","public "+className+"(IMdrContainer container,ITextOutput _mainoutput){"+newline+
					"  this.container=container;"+newline+
					"  this._mainoutput=_mainoutput;"+newline+
					"}"+newline+"%code%");



			int i = this.getNumberOfMsg();

			for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {
				Stimulus _stimulus = (Stimulus)it.next();

				String s_name=  _stimulus.getDispatchAction().getName(); 
				String _id =_stimulus.getName();

				//the object name of the receiver of the current msg
				String receiver_curr_msg = _stimulus.getReceiver().getName();

				//the class name of the receiver of the current msg
				String ClassName1 = this.getClassName(receiver_curr_msg);

				//the object name of the sender of the current msg
				String sender_curr_msg = _stimulus.getSender().getName();

				//the class name of the sender of the current msg
				//String ClassName = this.getClassName(sender_curr_msg);

				//sebastian begin
				HashMap _tvSenderObj = this.extractTaggedValues(_stimulus.getSender());
				HashMap _tvReceiverObj = this.extractTaggedValues(_stimulus.getReceiver());

				HashMap _tvSenderClassifier = this.extractTaggedValues((UmlClass)_stimulus.getSender().getClassifier().iterator().next());
				HashMap _tvReceiverClassifier = this.extractTaggedValues((UmlClass)_stimulus.getReceiver().getClassifier().iterator().next());

				boolean e1 = _tvSenderObj.containsKey("bioExchange") && _tvSenderObj.get("bioExchange").equals("yes");
				boolean e2 = _tvSenderClassifier.containsKey("bioExchange") && _tvSenderClassifier.get("bioExchange").equals("yes");
				boolean e3 = _tvReceiverObj.containsKey("bioExchange") && _tvReceiverObj.get("bioExchange").equals("yes");
				boolean e4 = _tvReceiverClassifier.containsKey("bioExchange") && _tvReceiverClassifier.get("bioExchange").equals("yes");

				if ((e1 || e2) && (e3 || e4)) {
					Iterator _it = _stimulus.getStereotype().iterator();
					if (_it.hasNext()) {
						Stereotype _st = (Stereotype)_it.next();
						this.setBioExchangeStereotype(_st.getName());
					}
					else {
						this.setBioExchangeStereotype("");
					}

					ret = ret.replaceAll("%imports%","import xcbf.*;" + newline);
				}
				else {
					ret = ret.replaceAll("%imports%","");
				}
				//sebastian end

				int _id_i = Integer.parseInt(_id);	
				int inc_id_i = _id_i+1;


				//get guard value in the SD
				String guard = this.getValueOfGuard(container,_id_i+1);

				// get name of msg
				int j = s_name.indexOf("(");
				String s_name_1 = s_name.substring(0,j);

				//get name of the next msg
				String n_msg_name= this.getNextMsgName(_id_i);

				//the object name of the receiver of the following msg	    	
				String receiver_next_msg =this.getReceivedNextMsgName(_id_i);

				//the class name of the receiver of the following msg
				String class_name_r = this.getClassName(receiver_next_msg);

				//der Klasse name und Object name von der StartObject und Name der erste Nachricht im SD ermitteln

				if (_id_i==1){

					start_Object = sender_curr_msg;
					start_Class = this.getClassName(sender_curr_msg);
					start_Stimulus = s_name;

				}

				if (ObjectName.replaceAll("\"","").equals(receiver_curr_msg)){ 

					//the first msg
					if( _id_i==1) { 


						ret = ret.replaceAll("%code%"," public String "+ s_name_1+"("+"String key"+")"+	"{"+newline+

								"//code"+newline+newline+
								"\t"+"String result = \"\""+";"+newline+
								"\t"+"JAVAGenerator JG = new JAVAGenerator(container,_mainoutput);"+newline+
								"\t"+class_name_r+" "+receiver_next_msg+" = "+ "new  "+ class_name_r+"(container,_mainoutput);"+newline+ 
								"\t"+"String parameter_2 = JG.getListeArgumentsOfMsg("+inc_id_i+");"+newline+
								this.getArgumentsOfMsg(_id_i)+newline+					
								this.getArgumentsOfMsgthis(_id_i)+newline+
								"\t"+"boolean guard_"+inc_id_i+" ="+guard+";"+newline+newline+
								"\t"+"if("+"guard_"+inc_id_i+"){"+newline+ 


								"\t"+"\t"+"  String ergebnis ="+receiver_next_msg+"."+n_msg_name+"(parameter_2);"+newline+
								"\t"+"\t"+"result = ergebnis;"+newline+ 
								"\t"+ " }"+newline+newline+
								"\t"+ "else result =\"der Protokoll wird bei \""+"+"+"\""+s_name_1+"\""+"+"+"\" bei \""+"+"+"\""+ClassName1+"\""+"+"+ "\" abgebrochen, die Methode \""+

								"+"+"newline"+"+"+"\""+n_msg_name+"\""+"+"+"\" bei \""+"+"+"\""+class_name_r+"\""+"+"+"\" wird nicht aufgerufen. \""+";"+newline+

								"\t"+"\t"+"return result;"+newline+newline+

								" }"+newline+newline+"%code%");

					}

					else if( 1< _id_i &_id_i<i) { 

						ret = ret.replaceAll("%code%","public String "+ s_name_1+"("+"String key"+")"+	"{"+newline+

								"//code"+newline+newline+

								"\t"+"String result = \"\""+";"+newline+					
								"\t"+"JAVAGenerator JG = new JAVAGenerator(container,_mainoutput);"+newline+
								"\t"+class_name_r+" "+receiver_next_msg+" = "+ "new  "+ class_name_r+"(container,_mainoutput);"+newline+
								"\t"+"String parameter_2 = JG.getListeArgumentsOfMsg("+inc_id_i+");"+newline+
								this.getArgumentsOfNextMsg(_id_i)+newline+
								this.getArgumentsOfNextMsgthis(_id_i)+newline+newline+
								this.getArgumentsOfMsg(_id_i)+newline+
								this.getArgumentsOfMsgthis(_id_i)+newline+  			

								"\t"+"boolean guard_"+inc_id_i+" ="+guard+";"+newline+newline+
								"\t"+"if("+"guard_"+inc_id_i+"){"+newline+
								"\t"+"\t"+"  String ergebnis ="+receiver_next_msg+"."+n_msg_name+"(parameter_2);"+newline+
								"\t"+"\t"+"result = ergebnis;"+newline+ 
								"\t"+ " }"+newline+newline+
								"\t"+ "else result =\"der Protokoll wird bei \""+"+"+"\""+s_name_1+"\""+"+"+"\" bei \""+"+"+"\""+ClassName1+"\""+"+"+ "\" abgebrochen, die Methode \""+
								"+"+"newline"+"+"+"\""+n_msg_name+"\""+"+"+"\" bei \""+"+"+"\""+class_name_r+"\""+"+"+"\" wird nicht aufgerufen. \""+";"+newline+
								"\t"+"\t"+"return result;"+newline+newline+

								" }"+newline+newline+"%code%");

					}

					//the last msg
					else { 
						ret = ret.replaceAll("%code%","public String "+ s_name_1+"("+"String key"+")"+	"{"+newline+

								"//code"+newline+newline+

								"\t"+"String result = \"\""+";"+newline+
								"\t"+"JAVAGenerator JG = new JAVAGenerator(container,_mainoutput);"+newline+
								"\t"+"\t"+"String ergebnis ="+"\"der Protokoll ist erfolgreich abgeschlossen.\""+";"+newline+
								"\t"+"\t"+"result =ergebnis;"+newline+
								"\t"+"\t"+"return result;"+newline+newline+

								" }"+newline+newline+"%code%");

					}

				}	
			}

			ret = ret.replaceAll("%code%","");
		}

		return ret;
	}



	/**
	 * put java code of each object in the hash map consisting of class infos
	 */

	private void getCodeOfClasses() throws Exception{

		Iterator objectKeys = objects.keySet().iterator();
		while (objectKeys.hasNext()) {
			String myKey = objectKeys.next().toString();
			int i = myKey.length();
			char c = myKey.charAt(i-1);
			String[] currObject = (String[])objects.get(myKey);  

			String objName = currObject[0];
			String objName1 = objName.replaceAll("\"","");
			String objClass = currObject[1].replaceAll("\"","");

			c2o.put(objClass,objName);
			String cl= "classinfo"+c;

			classinfos.put(cl, this.getCodeOfJavaClass(objClass,c2o.get(objClass).toString()));


		}

	}


	/**
	 *	displays class code on standard output or a message window
	 */


	public String displayCode() throws Exception {
		String result="";
		String out="";	

		this.getCodeOfClasses();
		Iterator classinfosKeys = classinfos.keySet().iterator();

		while (classinfosKeys.hasNext()) {

			String myKey = classinfosKeys.next().toString();
			String currClassCode = (String)classinfos.get(myKey);  
			out+=currClassCode+newline;
			out+="**************************************************************************************************************"+newline;

		}

		//sebastian
		if (!bioExchangeStereotype.equals("")) {
			String rootPath = System.getProperty("user.home");
			File _fi = new File(rootPath);
			if (rootPath.equals("/usr/share/tomcat4")) {
				rootPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "output";
			}
			XcbfGenerator _xg = new XcbfGenerator(rootPath);
			_xg.xcbf2Java(bioExchangeStereotype);
			_xg.writeXcbf2display(mainOutput);
		}

		result =out;

		return result;
	}




	/**
	 * get guard of the message with id 'id'. (i:Msg(...) -> guard_i ) 
	 */

	Vector getGuad (IMdrContainer container,int id){

		Vector ve = new Vector();


		//collect all tagged values of MDR-Container
		UmlPackage root = container.getUmlPackage();
		CorePackage corePackage = root.getCore();
		TaggedValueClass taVaCl = corePackage.getTaggedValue();


		// loop over all tagged values
		for (Iterator it = taVaCl.refAllOfClass().iterator(); it.hasNext();) {

			TaggedValue va = (TaggedValue)it.next();
			TagDefinition td = va.getType();
			Iterator itDataValue = va.getDataValue().iterator();
			String guard_no = "guard_"+id;

			// Falls TagType dem Gesuchten entspricht, ermittle DataValue
			if ( td.getName().equals(guard_no))
			{
				// Falls DataValue nicht leer, f�ge es zu Vektor hinzu
				while(itDataValue.hasNext()){
					String str = (String) itDataValue.next();
					//_mainOutput.writeLn("TaggedValue: " + td.getTagType() + " - " + str);
					if (!str.equals(""))
						ve.add(str);
				}
			}
		}

		return ve;

	}



	/**
	 * for each c in the hash map objects -> objectc(objectName, ClassName)
	 * get ClassName.
	 * 
	 */

	String extractClassName (char c){
		String result = "";	
		Iterator objectKeys = objects.keySet().iterator();
		while (objectKeys.hasNext()) {
			String myKey = objectKeys.next().toString();
			int i = myKey.length();
			//get c bei objectc
			char z = myKey.charAt(i-1);
			String[] currObject = (String[])objects.get(myKey);  
			String objName = currObject[0];
			String objClass = currObject[1].replaceAll("\"","");
			if (c==z){

				result=objClass;
			}
		}

		return result;
	}


	/**
	 * get the name of the current msg i
	 */
	public String getMsgName(int i){


		String result="";
		String j = String.valueOf(i);
		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {
			Stimulus _stimulus = (Stimulus)it.next();

			String s_name=  _stimulus.getDispatchAction().getName(); 
			int size = s_name.length();
			int k = s_name.indexOf("(");
			String s_name_p = s_name.substring(k,size);
			String s_name_a = s_name.substring(0,k);
			String _id =_stimulus.getName();

			if (j.equals(_id)){

				result = s_name_a;

			}

		}

		return result;
	}




	/**
	 * get the name of following msg of the current msg i
	 */

	String  getNextMsgName(int i){

		String  result = "";
		int _id_j=0;
		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {
			Stimulus _stimulus = (Stimulus)it.next();

			String s_name=  _stimulus.getDispatchAction().getName(); 
			String _id =_stimulus.getName();

			int _id_i = Integer.parseInt(_id);	

			if (_id_i ==i){
				_id_j =_id_i+1;
			}
			for(Iterator it2 = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it2.hasNext();) {
				Stimulus _stimulus1 = (Stimulus)it2.next();
				String s_name1=  _stimulus1.getDispatchAction().getName(); 
				String _id1 =_stimulus1.getName();

				int j = s_name1.indexOf("(");
				String s_name2 = s_name1.substring(0,j);

				int _id_k = Integer.parseInt(_id1);
				if (_id_k==_id_j){

					result = s_name2;
				}
			}
		}

		return result;
	}



	/**
	 *get receiver name of the following msg  of the current msg i
	 */

	String  getReceivedNextMsgName(int i){

		String  result = "";
		int _id_j=0;
		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {
			Stimulus _stimulus = (Stimulus)it.next();

			String s_name=  _stimulus.getDispatchAction().getName(); 
			String _id =_stimulus.getName();

			int _id_i = Integer.parseInt(_id);	

			if (_id_i ==i){
				_id_j =_id_i+1;
			}
			for(Iterator it2 = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it2.hasNext();) {
				Stimulus _stimulus1 = (Stimulus)it2.next();

				String s_name1=  _stimulus1.getDispatchAction().getName(); 

				String _id1 =_stimulus1.getName();
				String s_receiver1 = _stimulus1.getReceiver().getName();

				String s_sender1 = _stimulus1.getSender().getName();

				int _id_k = Integer.parseInt(_id1);
				if (_id_k==_id_j){

					result = s_receiver1;

				}
			}

		}

		return result;
	}




	/**
	 * get the Number of Msgs
	 */

	int getNumberOfMsg(){

		int  result = 0;

		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {
			Stimulus _stimulus = (Stimulus)it.next();

			String s_name=  _stimulus.getDispatchAction().getName(); 
			String _id =_stimulus.getName();

			result++;

		}

		return result;
	}





	/**
	 * get the class name of the object name
	 */

	private String getClassName (String objectname){
		String result = "";
		Iterator objectKeys = objects.keySet().iterator();

		while (objectKeys.hasNext()) {

			String myKey = objectKeys.next().toString();
			String[] currObject = (String[])objects.get(myKey);  
			String objName = currObject[0];
			String objName1 = objName.replaceAll("\"","");

			String objClass = currObject[1].replaceAll("\"","");
			if (objName1.equals(objectname)){
				result=objClass;
			}

		}
		return result;
	}



	/**
	 * @return 0, if the parenthesis are correctly positioned in the string str 
	 */

	int testKlammer(String str){

		int klammer = 0;

		for ( int i = 0; i < str.length(); i++){

			if (str.charAt(i) == '(')
				klammer++;
			else if ( str.charAt(i) == ')')
				klammer--;

			if ( klammer < 0)
				return klammer;
		}

		return klammer;
	}	



	/**
	 * get the elements of the argument list which is stored in the message string  
	 * (a,b,c..) --> a and b and c ,....
	 */
	public Vector findArg(String message)
	{
		Vector vec = new Vector();
		String arg= "";
		int klammer1 = 0;

		for (int i = 1; i < message.length(); i++)
		{
			if (message.charAt(i)!=','){

				arg = arg+message.charAt(i);
				klammer1= this.testKlammer(arg);

			}
			else if ((message.charAt(i)==',')&(klammer1!=0)) {
				arg =arg+',';

			}
			else if (klammer1==0){
				vec.addElement(arg);
				arg="";

			}
		}
		//vec.addElement(arg);
		if (arg.endsWith(")")){
			vec.add(arg.substring(0, arg.length()-1));
		}
		else vec.addElement(arg);

		return vec;
	}




	/**
	 * i:Msg(a,b,c,..) --> (a,b,c,...)
	 * 
	 */


	public String getListeArgumentsOfMsg(int i){

		String result="";

		String j = String.valueOf(i);
		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {
			Stimulus _stimulus = (Stimulus)it.next();

			String s_name=  _stimulus.getDispatchAction().getName(); 
			int size = s_name.length();
			int k = s_name.indexOf("(");
			String s_name_p = s_name.substring(k,size);

			String _id =_stimulus.getName();

			if (j.equals(_id)){

				result = s_name_p;

			}

		}

		return result;
	}



	/**
	 * Searches for a certain pattern in the text and replaces this pattern by the string 'ersatz'. 
	 */


	public String replaceString (String text, String muster, String ersatz) {
		String result="";

		Pattern p = Pattern.compile (muster);
		Matcher m = p.matcher (text);

		StringBuffer sb = new StringBuffer();
		boolean erg = m.find ();

		while (erg) {
			m.appendReplacement (sb, ersatz);
			erg = m.find ();

		}
		m.appendTail(sb);

		result = m.replaceAll (ersatz);
		return result ;

	}




	/**
	 * splits string s after each separator 'trennzeichen'
	 */

	public Vector SplitString(String s , char trennzeichen)
	{
		Vector v= new Vector();
		String arg= "";
		int klammer1 = 0;

		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i)!= trennzeichen){
				arg = arg+s.charAt(i);
			}

			else if ((s.charAt(i)==trennzeichen)) {
				v.addElement(arg);
				arg="";
			}
		}

		v.addElement(arg);
		return v;
	}



	/**
	 * sign(s,s2) -> "sign("s","s2")"
	 */

	String sign(String s,String s2){

		return "sign("+"\""+s+"\""+","+s2+")";  
	}



	/**
	 * inv(s) -> "inv("s")"
	 */

	String inv(String s){

		return "\"inv("+"\""+s+"\""+")\"";
	}



	/**
	 * enc(s,s2) -> "enc("s","s2")"
	 */

	String enc(String s , String s2){

		String result= "enc("+s+","+"\""+s2+"\""+")";
		return result;

	}



	/**
	 * ext(sign(n,inv(k)),k) =n
	 */

	String ext(String s,String ss){

		String result="";

		if (s.startsWith("sign")){

			String sk = s.replaceAll("\"","");

			//the return value n -> arg  
			//e.g. n can be a correct clasped expression


			int start = sk.indexOf('(',3);
			String se=sk.substring(start+1); //se is the string s without "ext(sign(" i.e. starting from n

			String arg="";
			int klammer1=0;
			for (int i = 0; i < se.length(); i++)
			{	
				if ((se.charAt(i)==',')&(klammer1==0)){

					break;

				}

				else 
					arg = arg+se.charAt(i);
				klammer1= this.testKlammer(arg);

			}

			Vector w = this.getFunctionName(sk);

			int size = w.size();
			System.out.println(size);

			String l_v = (String)w.elementAt(size-1).toString().trim();

			String s1="";
			String s2 ="";
			String ss2="";

			if (l_v.equals("inv")){

				Vector v = this.getVariables(sk);

				int size2 = v.size();
				String lv =  (String)v.elementAt(size2-2).toString().trim(); 
				ss2 = ss.trim();
				System.out.println("ss2 "+ss2);

				if (lv.equals(ss2)){
					result = arg;   	        

				}  
			}

		}

		return result;
	}





	/**
	 * dec (enc (n,k),inv(k))=n       
	 */


	String dec(String s, String ss){
		String result = "";	

		if (s.startsWith("enc")){
			String sk = s.replaceAll("\"","");

			int start = sk.indexOf('(',3);
			String se=sk.substring(start+1); //se is the string s without "dec(enc("i.e. starting from n

			//compute n -> arg  	
			String arg="";
			int klammer1=0;
			for (int i = 0; i < se.length(); i++)
			{	
				if ((se.charAt(i)==',')&(klammer1==0)){

					break;
				}

				else 
					arg = arg+se.charAt(i);
				klammer1= this.testKlammer(arg);

			}


			System.out.println("arg: "+arg);

			Vector v = this.getVariables(sk); //i.e. of "enc(n, k)"

			int size = v.size();
			System.out.println(size);

			String lz = (String)v.elementAt(size-2).toString().trim(); 

			String ss2 = ss.replaceAll("\"","").trim();
			Vector ve = this.getFunctionName(ss2);

			String cs = (String)ve.elementAt(0).toString().trim();

			int size2 = ve.size();
			if (cs.equals("inv")){ 	

				Vector u = this.getVariables(ss2);
				String uv = (String)u.elementAt(0).toString().trim();

				if (lz.equals(uv)){ //return n, if in the expression: "dec(enc(n, k), inv(k))"  the two k are equal.

					result = arg;  

				}

			} 


		}

		return result;


	}





	/**
	 * equal(s,ss) -> true if s=ss, else false
	 */

	public boolean equal(String s, String ss){

		boolean result = false;
		String ss2 = ss.trim().replaceAll("\"","");


		if (s.equals(ss2)){
			result = true;
		}
		return result ;
	}




	/**
	 * snd(a::b) = b 
	 */


	public String snd (String s){
		String result = "";

		if ( s.indexOf("::") == -1 ){
			result = s.trim();
		}

		else{

			String[] k = s.split("::");
			String ergebnis = k[1].trim();
			//result =ergebnis.replaceAll("\"","");
			result = ergebnis;

		}

		return result;   
	}   


	/**
	 * fst(a::b) = a 
	 */

	public String fst(String s){
		String result = "";

		if ( s.indexOf("::") == -1 ){
			result = s;
		}

		else{

			String[] k = s.split("::");
			String ergebnis = k[0];
			result =ergebnis;

		}

		return result;   
	}   





	/**
	 * symenc(s,s2) -> "symenc("s","s2")"
	 */ 

	String symenc(String s , String s2){

		String result= "symenc("+"\""+s+"\""+","+s2+")";

		return result;

	}


	/**
	 * keyExt(s) -> "keyExt("s")"
	 */ 

	String keyExt(String s){

		return "\"keyExt("+"\""+s+"\""+")\"";

	}



	/**
	 * mac(s,s2) -> "mac("s","s2")"
	 */ 

	String mac(String s , String s2){

		String result= "mac("+"\""+s+"\""+","+s2+")";

		return result;

	} 


	/**
	 * genSessionkey(s,s2) -> "genSessionkey("s","s2")"
	 */	

	String genSessionkey(String s , String s2){

		String result= "genSessionKey("+"\""+s+"\""+","+"\""+s2+"\""+")";

		return result;

	} 


	/**
	 * sub(s,s2) -> "sub("s","s2")"
	 */	
	String sub(String s , String s2){

		String result= "sub("+"\""+s+"\""+","+"\""+s2+"\""+")";

		return result;

	} 



	/**
	 *greater(s,s2) -> "greater("s","s2")"
	 */


	String greater(String s , String s2){

		String result= "greater("+"\""+s+"\""+","+"\""+s2+"\""+")";

		return result;

	} 




	boolean compare (String s){
		boolean result =true;

		return result;

	}



	String fbz2written(String s , String s2){

		String result= "fbz2written("+"\""+s+"\""+","+"\""+s2+"\""+")";

		return result;

	} 


	/**
	 * hash(s,s2) -> "hash("s","s2")"
	 * 
	 */ 
	String hash (String s){

		return "\"hash("+"\""+s+"\""+")\"";
	}



	/**
	 * match(s,s2) -> "match("s","s2")"
	 */	
	String match(String s , String s2){

		String result= "match("+"\""+s+"\""+","+"\""+s2+"\""+")";

		return result;

	} 




	String replaceVariables2(String s){

		String result="";
		Vector v = this.getVariables2(s);

		String sr =s;
		Enumeration e = v.elements();
		while (e.hasMoreElements()){

			String sm = (String)e.nextElement().toString().trim();

			if((sm.indexOf(':')!=-1)&(sm.indexOf("_")==-1)) { 
				String sm1 = sm.replaceAll("\"","").trim();
				String sm2 = "\""+sm1+"\"".trim();

				sr = this.replaceString(sr,sm,sm2);
				v.remove(sm);

			}


			if  ((sm.indexOf(':')!=-1)&(sm.indexOf("_")!=-1)) { 
				String sm1 = sm.replaceAll("\"","").trim();
				String sm2 = "\""+sm1+"\"".trim();
				Vector ve= this.SplitString(sm2,':');


				String s1 = (String)ve.elementAt(0);
				String s3= s1.replaceAll("\"","");

				String s2 = (String)ve.elementAt(2);
				String s4= s2.replaceAll("\"","");


				String s5 = "\""+s3+"\""+"+"+"\"::\"" +"+ "+ s4;

				sr = this.replaceString(sr,sm,s5);
				v.remove(sm);
			}

			result =sr;

		}

		return result;

	}



	/**
	 * this method replaces the variable e.g.. "k_c"a through "k_ca"
	 * 
	 */

	String replaceVariables3(String s){

		String result="";
		Vector v = this.getVariables2(s);

		String sr =s;
		Enumeration e = v.elements();

		while (e.hasMoreElements()){

			String sm = (String)e.nextElement().toString().trim();

			if(!sm.endsWith("\"")&(sm.indexOf("_")==-1)) { 
				String sm1 = sm.replaceAll("\"","");
				String sm2 = "\""+sm1+"\"";

				sr = this.replaceString(sr,sm,sm2);

				v.remove(sm);
			}

			result =sr;

		}

		return result;

	}




	/**
	 *	@return a vector of variables with repetitions of some variables
	 * 
	 */


	public Vector getVariables( String msg){
		Vector vars = new Vector();

		String var = "";


		// Durchlaufe den String Zeichen f�r Zeichen
		for ( int j = 0; j < msg.length(); j++)
		{
			// Falls eine Klammer ge�ffnet wird, war der vorausgehende Teilstring ein Funktionsname
			if ( msg.charAt(j) == '(' )
				var = "";
			// Falls ein Komma oder eine schlie�ende Klammer auftaucht, handelt es sich bei dem Teilstring um ein Argument/Variable, diese wird im Vector abgelegt
			else if ( (msg.charAt(j) == ',') || (msg.charAt(j) == ')')||(msg.charAt(j) == '=' ))
			{
				var = var.trim();

				if ( !var.equals("") )
				{

					//if ( !vars.contains(var) )
					vars.add(var) ; 
					//varss.add(var);
					var = "";
				}
			}
			// Falls keiner der obigen F�lle eintritt, wird das Zeichen gemerkt und mit dem n�chsten Zeichen fortgefahren
			else
				var = var + msg.charAt(j);

		}
		vars.addElement(var);

		// R�ckgabe, Vector mit Variablen mit Doppelvorkommen	
		return vars;

	}



	/**
	 * @return a vector of variables without repetitions of any variables
	 * 
	 */

	public Vector getVariables2( String msg){
		Vector vars = new Vector();

		String var = "";


		// Durchlaufe den String Zeichen f�r Zeichen
		for ( int j = 0; j < msg.length(); j++)
		{
			// Falls eine Klammer ge�ffnet wird, war der vorausgehende Teilstring ein Funktionsname
			if ( msg.charAt(j) == '(' )
				var = "";
			// Falls ein Komma oder eine schlie�ende Klammer auftaucht, handelt es sich bei dem Teilstring um ein Argument/Variable, diese wird im Vector abgelegt
			else if ( (msg.charAt(j) == ',') || (msg.charAt(j) == ')')||(msg.charAt(j) == '=' ))
			{
				var = var.trim();

				if ( !var.equals("") )
				{

					if ( !vars.contains(var) )
						vars.add(var) ; 
					//varss.add(var);
					var = "";
				}
			}
			// Falls keiner der obigen F�lle eintritt, wird das Zeichen gemerkt und mit dem n�chsten Zeichen fortgefahren
			else
				var =var + msg.charAt(j);

		}
		if (( !vars.contains(var) )&( !var.equals("") )){

			vars.addElement(var);
		}
		// R�ckgabe, Vector mit Variablen mit Doppelvorkommen	
		return vars;

	}


	/**
	 * for all variables: variable -> "variable"
	 */

	String replaceAllVariables(String s){
		String result = "";

		Vector vec = new Vector();
		vec = this.getVariables2(s);

		String s2=s;

		for (Enumeration e = vec.elements();e.hasMoreElements();){

			String sm = (String)e.nextElement().toString().trim();
			if ((sm.indexOf(':')==-1)&(sm.indexOf('_')==-1)){
				s2 = this.replaceString(s2,sm,"\""+sm+"\"");
			}

		}



		Vector vec2 = this.getVariables2(s2);

		String s3 = this.replaceVariables2(s2);
		Vector vec3 = this.getVariables2(s3);
		String s4 = this.replaceVariables3(s3);
		Vector vec4 = this.getVariables2(s4);
		result = s4;

		return result;

	}




	/**
	 * (a+b) -> a+b
	 **/

	String delBrackets(String str){

		if ( str.startsWith("(") && str.endsWith(")") )
		{
			int klammer = 0;

			for ( int i = 0; i < str.length(); i++){

				if (str.charAt(i) == '(')
					klammer++;
				else if ( str.charAt(i) == ')')
					klammer--;

				if ( (klammer == 0) && (i < str.length()-1) )
					return str;

			}

			if ( klammer == 0 )
				return str.substring(1, str.length()-1);
			else

				return str;
		}

		else return str;

	}





	/**
	 * get the function name in the guard
	 * 
	 */	

	public Vector getFunctionName( String msg){
		Vector vars = new Vector();

		String var = "";


		// Durchlaufe den String Zeichen f�r Zeichen
		for ( int j = 0; j < msg.length(); j++)
		{

			// Falls eine Klammer ge�ffnet wird, war der vorausgehende Teilstring ein Funktionsname
			if ( msg.charAt(j) == '(' ){

				if ( !vars.contains(var.trim())&(var.indexOf('&')==-1)){	

					var = var.trim();
					vars.addElement(var);

				}

				var = "" ;
			}
			else {

				var = var+msg.charAt(j);	
				if (msg.charAt(j) == ',' ){
					var ="";	
				}
			}

		}
		return vars;

	}    





	/**
	 *  get guard value in java code
	 */


	String getValueOfGuard(IMdrContainer _container,int i){

		String result = "";
		Vector vec = this.getGuad(_container,i);

		if (!vec.isEmpty()){
			int size = vec.size();
			String s = (String)vec.elementAt(size-1);
			String guard = this.evaluationOfGuard(s,i);

			result = guard;
		}
		else result = "true";

		return result;

	}



	/**
	 *  Teile der Bedingung "guard_k" bearbeiten
	 */


	String evaluationOfGuard(String guard,int k){

		String result="";

		// call the method evaluationOfsingleGuard for each element of vec2

		if (guard.indexOf('&')!=-1){

			String u1= "true";
			Vector vec2 = this.SplitString(guard,'&');
			for (Enumeration e = vec2.elements();e.hasMoreElements();){

				String s = (String)e.nextElement().toString().trim();
				u1 =u1+"&"+ this.evaluationOfsingleGuard(s,k); 

			}

			int j = u1.length();
			String u2 = u1.substring(5,j); //remove "true&" from the u2

			result =u2;

		}


		else 
		{
			result = this.evaluationOfsingleGuard(guard,k);

		}	

		return result;

	}


	/**
	 *  ein Teil der Bedingung "guard_k" bearbeiten
	 */
	String evaluationOfsingleGuard(String guard,int k){

		String result="";


		//den klammern von dieses guard entfernen, wenn sie vorhanden sind	
		String guard1 = this.delBrackets(guard);

		Vector vec1 =  this.getVariables(guard1);

		String guard2 = guard1;
		for (Enumeration e = vec1.elements();e.hasMoreElements();){

			String s = (String)e.nextElement().toString();
			guard2 = this.replaceString(guard2,s,s.trim()).trim();

		}


		//den Variablen var durch "var" ersetzen	   

		Vector vec3 =  this.getVariables2(guard2);

		//die Variablen, die keine "_" behalten wie Init_1 ersetzen
		String guard3 = this.replaceAllVariables(guard2);

		//Msg,..durch JG.Msg,... ersetzen

		Vector vec4= this.getFunctionName(guard3);
		Enumeration enumeration =vec4.elements();
		String guard4= guard3; 	
		for (Enumeration e = vec4.elements();e.hasMoreElements();){

			String s = (String)e.nextElement().toString();

			if (s.indexOf(':')==-1){
				guard4 = this.replaceString(guard4,s,"JG."+s).trim();
			}

			if (s.indexOf(':')!=-1){
				Vector ve = this.SplitString(s,':');
				String se1 = (String)ve.elementAt(0);
				String se2 = (String)ve.elementAt(2);
				String se = "\""+se1+"\""+"+"+"\"::\""+"+"+"JG."+se2;

				guard4 = this.replaceString(guard4,s,se).trim();
			}

		}

		//die zwei Teile von "=" ermitteln lassen.	
		if (guard4.indexOf('=')!=-1){
			Vector vec5 = this.SplitString(guard4,'=');

			String E_Teil = (String)vec5.elementAt(0).toString().trim();
			String Z_Teil = (String)vec5.elementAt(1).toString().trim();

			String  guardF = "JG.equal("+E_Teil+","+Z_Teil+")"; 

			result = guardF;	
		}


		else {

			String  	 guardF2 = "JG.compare("+guard4+")";

			result = guardF2;
		}  

		return result;

	}




	/**
	 *  get the Argumnete of Msg e.g. from Msgi --> Msg_1, Msg_2,..
	 */


	String getArgumentsOfMsg (int i){

		String result = "";


		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {

			Stimulus _stimulus = (Stimulus)it.next();
			String s_name=  _stimulus.getDispatchAction().getName(); 
			String _id =_stimulus.getName();	
			int _id_i = Integer.parseInt(_id);

			int j = s_name.indexOf("(");
			String s_name_1 = s_name.substring(0,j);

			String s_name_2 = s_name.substring(j,s_name.length());


			int k = this.findArg(s_name_2).size();
			int h = this.getNumberOfMsg();

			if (_id_i ==i){

				for (int l =0;l<k;l++){	
					int m = l+1;
					result = result+"    String  "+s_name_1+"_"+m+ " = " +"JG.evaluationOfArgument((String)JG.findArg(key).elementAt("+l+")"+","+h+")"+";"+newline;
				}

			}

		}

		return result;
	}



	/**
	 *  get the Argumnete of next Msg� of the Msgi e.g. from Msg�i --> Msg�_1, Msg�_2,..
	 */


	String getArgumentsOfNextMsg (int i){

		String result ="";
		int _id_j=0;
		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {
			Stimulus _stimulus = (Stimulus)it.next();

			String s_name=  _stimulus.getDispatchAction().getName(); 
			String _id =_stimulus.getName();

			int _id_i = Integer.parseInt(_id);	

			if (_id_i ==i){
				_id_j =_id_i+1;
			}

			String s_name_2 ="";
			String s_name_p ="";
			for(Iterator it2 = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it2.hasNext();) {
				Stimulus _stimulus1 = (Stimulus)it2.next();
				String s_name1=  _stimulus1.getDispatchAction().getName(); 
				String _id1 =_stimulus1.getName();

				int j = s_name1.indexOf("(");
				String s_name2 = s_name1.substring(0,j);

				String s_name_p1 = s_name1.substring(j,s_name1.length());

				int _id_k = Integer.parseInt(_id1);
				if (_id_k==_id_j){

					s_name_2 = s_name2;
					s_name_p = s_name_p1;
				}
			}

			int k = this.findArg(s_name_p).size();

			if (_id_i ==i){

				for (int l =0;l<k;l++){	
					int m = l+1;
					result = result+"    String  "+s_name_2+"_"+m+ " = " +"(String)JG.findArg(parameter_2).elementAt("+l+")"+";"+newline;
				}

			}
		}

		return result;

	}





	String getArgumentsOfNextMsgthis (int i){

		String result ="";


		int _id_j=0;
		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {
			Stimulus _stimulus = (Stimulus)it.next();

			String s_name=  _stimulus.getDispatchAction().getName(); 
			String _id =_stimulus.getName();

			int _id_i = Integer.parseInt(_id);	

			if (_id_i ==i){
				_id_j =_id_i+1;
			}

			String s_name_2 ="";
			String s_name_p ="";
			for(Iterator it2 = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it2.hasNext();) {
				Stimulus _stimulus1 = (Stimulus)it2.next();
				String s_name1=  _stimulus1.getDispatchAction().getName(); 
				String _id1 =_stimulus1.getName();

				int j = s_name1.indexOf("(");
				String s_name2 = s_name1.substring(0,j);

				String s_name_p1 = s_name1.substring(j,s_name1.length());

				int _id_k = Integer.parseInt(_id1);
				if (_id_k==_id_j){

					s_name_2 = s_name2;
					s_name_p = s_name_p1;
				}
			}

			int k = this.findArg(s_name_p).size();

			if (_id_i ==i){

				for (int l =0;l<k;l++){	
					int m = l+1;
					result = result+"    this."+s_name_2+"_"+m+ " = "+s_name_2+"_"+m+";"+newline; 
				}

			}
		}

		return result;
	}


	/**
	 * get the Argumnete: Msg_1,...,Msg�_1,... of the Msgs in the SD 
	 */



	String getArgumentsOfMsgs (String classname){

		String result = "";


		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {

			Stimulus _stimulus = (Stimulus)it.next();
			String s_name=  _stimulus.getDispatchAction().getName(); 
			String _id =_stimulus.getName();	
			String sti_receiver = _stimulus.getReceiver().getName();

			String sti_className = this.getClassName(sti_receiver);
			int _id_i = Integer.parseInt(_id);
			int j = s_name.indexOf("(");

			// System.out.println(_stimulus.getDispatchAction().getName());
			String s_name_1 = s_name.substring(0,j);

			String s_name_2 = s_name.substring(j,s_name.length());


			int k = this.findArg(s_name_2).size();

			for (int l =0;l<k;l++){	
				int m = l+1;
				result = result+"    String  "+s_name_1+"_"+m+ ";"+newline;

			}
		}		 
		return result;


	}


	/**
	 * this.Msg_i = Msg_i
	 */


	String getArgumentsOfMsgthis (int i){

		String result = "";

		for(Iterator it = container.getUmlPackage().getCommonBehavior().getStimulus().refAllOfClass().iterator();it.hasNext();) {

			Stimulus _stimulus = (Stimulus)it.next();
			String s_name=  _stimulus.getDispatchAction().getName(); 
			String _id =_stimulus.getName();	
			int _id_i = Integer.parseInt(_id);

			int j = s_name.indexOf("(");
			String s_name_1 = s_name.substring(0,j);

			String s_name_2 = s_name.substring(j,s_name.length());


			int k = this.findArg(s_name_2).size();

			if (_id_i ==i){

				for (int l =0;l<k;l++){	
					int m = l+1;
					result = result+"    this."+s_name_1+"_"+m+ " = "+s_name_1+"_"+m+";"+newline;
				}

			}

		}


		return result;

	}





	String evaluationOfArgument(String string,int k){

		String result="";

		//hier wird rekursiv von der Methode j bis die Methode 1 die Parametern mit dem entsprechenden Werten ersetzt.

		int j=0;
		for (j=k;j>1;j--){
			String parameter = this.getListeArgumentsOfMsg(j-1);
			//f�r nicht parameterlose Methoden 
			if (!parameter.equals("()")){

				String n = this.getMsgName(j-1);

				String StimulusName = n+"_";
				Vector vec = this.findArg(parameter);
				int size1 = vec.size();
				for (int i=0;i<=size1-1;i++){
					String StimulusName_tptp = StimulusName +String.valueOf(i+1);
					String m = (String) vec.elementAt(i).toString().trim();
					string = this.replaceString(string,StimulusName_tptp,m).trim();

				}
			}

		}


		// den leer aus dem Variablen "var" entfernen.

		Vector vec1 =  this.getVariables(string);
		String string1 = string;
		for (Enumeration e = vec1.elements();e.hasMoreElements();){

			String s = (String)e.nextElement().toString();
			string1 = this.replaceString(string1,s,s.trim()).trim();

		}
		result = string1;
		return result;

	}

	//sebastian begin
	public HashMap extractTaggedValues(ModelElement _me) {
		HashMap _hm = new HashMap();
		for (Iterator iter = _me.getTaggedValue().iterator(); iter.hasNext(); ) {
			TaggedValue _tv = (TaggedValue) iter.next();
			TagDefinition _td = (TagDefinition) _tv.getType();
			Collection _cdv = _tv.getDataValue();
			String _dv = "";
			for (Iterator iter2 = _cdv.iterator(); iter2.hasNext(); ) {
				_dv += (String)iter2.next();
			}
			_hm.put(_td.getName(),_dv);
		}
		return _hm;
	}

	public void setBioExchangeStereotype(String _st) {
		if (bioExchangeStereotype.equals("integrityandsecrecy"))
			return;
		else if (_st.equals("integrityandsecrecy"))
			bioExchangeStereotype = _st;
		else if (_st.equals("") && bioExchangeStereotype.equals(""))
			bioExchangeStereotype = "unprotected";
		else if ((_st.equals("integrity") && bioExchangeStereotype.equals("secrecy")) || (_st.equals("secrecy") && bioExchangeStereotype.equals("integrity")))
			bioExchangeStereotype = "integrityandsecrecy";
		else if (!_st.equals(""))
			bioExchangeStereotype = _st;
	}
	//sebastian end


}//end of Class

