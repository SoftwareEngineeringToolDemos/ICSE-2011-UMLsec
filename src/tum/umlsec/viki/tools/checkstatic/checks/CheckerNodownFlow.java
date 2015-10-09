/**
 * Created by JCreator.
 * User: shasha meng
 * Date: Jan 22, 2003
 * Time: 8:59:27 PM
 * To change this template use Options | File Templates.
 */
package tum.umlsec.viki.tools.checkstatic.checks;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

public class CheckerNodownFlow extends StaticCheckerBase {


	boolean bedingen1;
	boolean bedingen2;
	HashMap obj_Val = new HashMap();
//	  defines a list for the names of all the classes
	ArrayList list_objname=new ArrayList();
	ArrayList list_all=new ArrayList();

	CorePackage corePackage ;
	ActivityGraphsPackage activityPackage;
	StateMachinesPackage stateMachines;
	TransitionClass transitionClasses;
	ITextOutput textOutput;
//	  initial

	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
		textOutput = _textOutput;
		List slist = new ArrayList();

//						   global declarations
								bedingen1=true;
								bedingen2=true;
								boolean b = true;
								ArrayList startList = new ArrayList();
								org.omg.uml.UmlPackage root ;


//						   replacement for the init function
								root = _mdrContainer.getUmlPackage();
								corePackage = root.getCore();

		StateMachinesPackage stateMachines = (StateMachinesPackage)root.getStateMachines();
				_textOutput.writeLn("=======running CheckerNoDownFlow...");
			  _textOutput.writeLn("======= All Tagged Values");
			  TaggedValueClass tagvalueClasses=(TaggedValueClass)corePackage.getTaggedValue();

			  for(Iterator it_Tag_V= tagvalueClasses.refAllOfClass().iterator(); it_Tag_V.hasNext();) {
				  TaggedValue tagValue=(TaggedValue)it_Tag_V.next();
//		  list all tagged values of the tagged type "start"
				  if((tagValue.getType()).getTagType().equals("high")||(tagValue.getType()).getTagType().equals("secret")){
					  for(Iterator it_tagVa_A = (tagValue.getDataValue()).iterator(); it_tagVa_A.hasNext();){
						  String tagValue_Da_A=(String)it_tagVa_A.next();
              if (tagValue_Da_A!=null&&tagValue_Da_A.length()!=0) _textOutput.writeLn("TaggedValue (Data) of "+ (tagValue.getType()).getTagType()+" is " + tagValue_Da_A);
						  if (tagValue_Da_A!=null) {
							  slist.add(tagValue_Da_A);
						  }
					  }
				  }
			  }

			TransitionClass transitionClasses = (TransitionClass)stateMachines.getTransition();
			for(Iterator itt = transitionClasses.refAllOfClass().iterator();itt.hasNext();){
				Transition t=(Transition)itt.next();
				try{

				String nameT=t.getTrigger().getName();
//				String ef = t.getEffect().getScript().getBody();
				String name=nameT.substring(0, nameT.indexOf('('));
         // _textOutput.writeLn(name);
				Map nmap = new HashMap();
				if (slist.contains(name)){
					for(Iterator itin=t.getSource().getIncoming().iterator(); itin.hasNext();){
						Transition tin=(Transition)itin.next();
						String nameTin=tin.getTrigger().getName();
						String ef_in = tin.getEffect().getScript().getBody();
						String name_in=nameTin.substring(0, nameTin.indexOf('('));
						if (!slist.contains(name_in)){
               //_textOutput.writeLn("Hi");
							nmap.put(name_in,ef_in);

						}
					}
           //_textOutput.writeLn(nmap);
					for(Iterator itout=t.getTarget().getOutgoing().iterator();itout.hasNext();){
                //_textOutput.writeLn("step2.");
						Transition tout=(Transition)itout.next();
             //_textOutput.writeLn("step3.");
						String nameTout=tout.getTrigger().getName();
             //_textOutput.writeLn("step4.");
						String ef_out = tout.getEffect().getScript().getBody();
						String name_out=nameTout.substring(0, nameTout.indexOf('('));
             //_textOutput.writeLn(name_out);
					if (nmap.isEmpty()){

					} else if (nmap.containsKey(name_out)){
             // _textOutput.writeLn("Hi, I am here now.");
						String st=(String)nmap.get(name_out);
						if (!st.equals(ef_out)){
              _textOutput.writeLn("The output of a non-high or non-secret message "+ name_out+" depends on high or secret information.");
							b = false;
						}

					} else{
						//nmap.put(name,ef);
					}
					}
				}
				}catch (java.lang.NullPointerException nx){
					System.out.println("java.lang.NullPointerException with t.getTigger " + nx.getMessage());
				}
			}
			if (!b)
			{
        //_textOutput.writeLn("The UML model violates the requirement " + "\<" + "\<" + "no down-flow" + "\>" + "\>");
       _textOutput.writeLn("The UML model violates the requirements of the stereotype no down-flow.");
			 }
			else
			{
        //_textOutput.writeLn("The UML model satisfies the requirement " + "\<" + "\<" + "no down-flow" + "\>" + "\>");
       _textOutput.writeLn("The UML model satisfies the requirements of the stereotype no down-flow.");
			 }
		return b;
	}


}
