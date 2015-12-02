package tum.umlsec.viki.tools.checkstatic.checks;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;

import tum.umlsec.viki.framework.ITextOutput;

import com.sun.xml.tree.XmlDocument;


public class XMLDOMParser
{
	ITextOutput textOutput;
	XmlDocument document;
	String attackerName;
	AttackerBean attacker = new AttackerBean();
	//int number=0;
	public XMLDOMParser(ITextOutput output)
	{
    textOutput = output;
	}

	public XMLDOMParser(){
	}

	public HashMap loadXMLFile(File fileName)
	{

		//FileInputStream inStream;
		//String xmlDocumentPath = "file:" + new File(filename).getAbsolutePath();
		HashMap hmap= new HashMap();
		try {
			FileReader reader = new FileReader(fileName);
			//load a mapping file
			Mapping mapping = new Mapping();
			String resourceRoot = System.getProperty("tum.umlsec.viki.resourceRoot");
			mapping.loadMapping(resourceRoot+File.separator+"mapping.xml");

			Unmarshaller unmarshaller = new Unmarshaller(AttackerBean.class);
			unmarshaller.setMapping(mapping);

			attacker = (AttackerBean)unmarshaller.unmarshal(reader);
			reader.close();
		 	attackerName = attacker.getName();
      textOutput.writeLn("==========================Attacker Name..." + attackerName );

	        //List l= new ArrayList();
	        List beans = attacker.getBeans();
	    		Iterator iter = beans.iterator();
	    		while (iter.hasNext()) {
	    		 //number++;
           List l= new ArrayList();
		   		Bean bean = (Bean) iter.next();
		   		String stereotype_Link = bean.getStereotype();
		   	l.add(bean.getThreat1());
		   	l.add(bean.getThreat2());
		   	l.add(bean.getThreat3());
		   	l.add(bean.getThreat4());
		   	hmap.put(stereotype_Link,l);
		 	} //end while

		}//end try
		catch(Exception e) {
      textOutput.writeLn("Fehler beim Parsen der XML-Datei: " + e);
		}

		return hmap;
	}

     //public int getNumberLoop (){return number;}
		 public String getAttackerName(){return attackerName;}
}