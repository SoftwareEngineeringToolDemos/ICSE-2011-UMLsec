package tum.umlsec.viki.tools.permission.prologGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.commonbehavior.CommonBehaviorPackage;
import org.omg.uml.behavioralelements.commonbehavior.Instance;
import org.omg.uml.behavioralelements.commonbehavior.Stimulus;
import org.omg.uml.behavioralelements.commonbehavior.StimulusClass;
import org.omg.uml.foundation.core.CorePackage;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;

/**
 * @author Markus
 *
 */
public class PermissionPrologGenerator
{
    private Hashtable mdrObjects  = new Hashtable();
    private HashSet seqMessages = new HashSet();

    
    public boolean check(IMdrContainer _mdrContainer, 
                         ITextOutput _textOutput, 
                         CommandParameterDescriptor fileParam) 
    {

        Properties            props         = new Properties();
        Vector                lines         = new Vector();
        ITextOutput           textOutput    = _textOutput;
        Hashtable             objects       = new Hashtable();
        Hashtable             stimuli       = new Hashtable();
        
        UmlPackage            root          = _mdrContainer.getUmlPackage();
        CorePackage           corePackage   = root.getCore();
        CommonBehaviorPackage commonPackage = root.getCommonBehavior();
         
	boolean dump = true;

   	lines.add("/*======= prolog code for dynamic check =======*/");				

        StimulusClass stmClass = commonPackage.getStimulus();
 	for(Iterator it = stmClass.refAllOfClass().iterator(); it.hasNext();)
        {
            Stimulus stimulus = (Stimulus) it.next();
            Instance sender   = (Instance) stimulus.getSender();
            Instance receiver = (Instance) stimulus.getReceiver();
            
            if ( !objects.containsKey(sender) )
            {
                objects.put(sender, new ObjectDefinition(sender));
                //lines.add("creating: " + sender.getName());
            }

            if ( !objects.containsKey(receiver) )
            {
                objects.put(receiver, new ObjectDefinition(receiver));
            }
 
            stimuli.put(stimulus.getName(), new StimulusDefinition(stimulus) );
            

        }
        
        lines.add("");
        lines.add("");
        lines.add("/*");
        lines.add(" *  permissions (static) ");
        lines.add(" */ ");
        for(Iterator iterObjects = objects.values().iterator(); iterObjects.hasNext();)
        {
            ObjectDefinition def = (ObjectDefinition) iterObjects.next();
            lines.addAll( def.prologPermissions() );
        }

        
        lines.add("");
        lines.add("");
        lines.add("/*");
        lines.add(" *  delegation (static) ");
        lines.add(" */ ");            
        for(Iterator iterObjects = objects.values().iterator(); iterObjects.hasNext();)
        {
            ObjectDefinition def = (ObjectDefinition) iterObjects.next();
            lines.addAll(def.prologDelegation());
        }

        
        lines.add("");
        lines.add("");
        lines.add("/*");
        lines.add(" *  class mapping ");
        lines.add(" */ ");            
        for(Iterator iterObjects = objects.values().iterator(); iterObjects.hasNext();)
        {
            ObjectDefinition def = (ObjectDefinition) iterObjects.next();
            lines.add(def.prologClassmapping());
        }

        
        lines.add("");
        lines.add("");
        lines.add("/*");
        lines.add(" *  class needing no permissions ");
        lines.add(" */ ");
        
        Collection collStim   = stimuli.values();
        Collection collNoPerm = new HashSet(); 
        for(Iterator iterStim = collStim.iterator(); iterStim.hasNext();)
        {
            StimulusDefinition def = (StimulusDefinition) iterStim.next();
            collNoPerm.addAll(def.prologNoPerm());
        }
        lines.addAll(collNoPerm);        
        
        lines.add("");
        lines.add("");
        lines.add("/*");
        lines.add(" *  messages (dynamic) ");
        lines.add(" */ ");            

        int i = 0;        
        while (!stimuli.isEmpty() && !(i > 20))
        {            
            if ( stimuli.containsKey( String.valueOf(i) ) )
            {            
                StimulusDefinition def = 
                    (StimulusDefinition) stimuli.remove( String.valueOf(i) );
                
                lines.add("");
                lines.addAll( def.prologMessage(i) );
                
            }
            i++;
        }

        /*
         * creating prolog clauses
         */
        lines.add("");
        lines.add("/*");
        lines.add(" * helpclauses: addEntry( List1, List2, ListOut )  ");
        lines.add(" */");
        lines.add("");
        lines.add("addEntry( [] , EntryList, EntryList ).");
        lines.add("addEntry( EntryList, [] , EntryList ).");
        lines.add("addEntry( [X|L1] , L2 , [X|L3] ) :- addEntry(L1, L2, L3).");
        lines.add("");
        lines.add(":- dynamic hasCert/2.");
        lines.add(":- dynamic usedCert/1.");
        lines.add("");
        lines.add("/*");
        lines.add(" *  helpclauses: isElement( Element , List )");
        lines.add(" */");
        lines.add("");
        lines.add("isElement( Element , List ):- addEntry( _ , [Element|_], List).");
        lines.add("");
        lines.add("/*");
        lines.add(" *  helpclauses: allElements( ElementList , List )");
        lines.add(" */");
        lines.add("");
        lines.add("allElements( [], _).");
        lines.add("");
        lines.add("allElements( [First|Rest], List):-");
        lines.add("	isElement(First, List),");
        lines.add("	allElements( Rest, List).");
        lines.add("");
        lines.add("/*");
        lines.add(" *   caller has all permissions needed for one message");
        lines.add(" */");
        lines.add("");
        lines.add("hasAllPermissions(_, _, _,_ , []).");
        lines.add("");
        lines.add("hasAllPermissions(Step, Sender, Object, M, [P_First|P_Rest]):-");
        lines.add("	permission(Sender, P_First),");
        lines.add("	hasAllPermissions(Step, Sender, Object, M, P_Rest).");
        lines.add("");
        lines.add("hasAllPermissions(Step, Sender, Object, M, [P_First|P_Rest]):-");
        lines.add("	hasCert(Sender, cert(E, D, P_First, _, V, Sq)),");
        lines.add("	canUseCert(Step, Sender, cert(E, D, P_First, _, V, Sq)),");
        lines.add("	hasAllPermissions(Step, Sender, Object, M,  P_Rest).");
        lines.add("");
        lines.add("hasAllPermissions(_, Sender, Object, Message, _):-");
        lines.add("	isOf(Sender, Class),");
	lines.add("	noPermission(Message, Object, Class).");
        lines.add("");
        lines.add("/*");
        lines.add(" *   object can make use of a cert; cert is marked after being used");
        lines.add(" *   certificate will be marked as used, if sequence number != -1, so that ");
        lines.add(" *   reusing is not possible.");
        lines.add(" */");
        lines.add("");
        lines.add("canUseCert(Step, Sender, cert(E, Sender, P, _, V, Sq)):-");
        lines.add("	permission(E, P), (Step =< V; V == -1) ,");
        lines.add("");
        lines.add("	((not(usedCert(cert(E, Sender, P, _, V, Sq))),");
        lines.add("	assert(usedCert(cert(E, Sender, P, _, V, Sq))));");
        lines.add("	Sq == -1).");
        lines.add("");
        lines.add("canUseCert(Step, Sender, cert(E, null, P, Class, V, Sq)):-");
        lines.add("	permission(E, P),	");
        lines.add("	delegate(E, P, DelegationList),");
        lines.add("	isElement(Class, DelegationList),");
        lines.add("	isOf(Sender, ClassX),");
        lines.add("	isElement(ClassX, [Class]),");
        lines.add("	(Step =< V; V == -1),");
        lines.add("	((not(usedCert(cert(E, null, P, Class, V, Sq))),");
        lines.add("	assert(usedCert(cert(E, null, P, Class, V, Sq))));");
        lines.add("	Sq == -1).");
        lines.add("");
        lines.add("/*");
        lines.add(" *  one specific certificate may be sent.");
        lines.add(" */");
        lines.add("");
        lines.add("sendCert(Sender, Receiver, Cert):-			");
        lines.add("	hasCert(Sender, Cert),");
        lines.add("	assert(hasCert(Receiver, Cert)),");
        lines.add("	write(Sender), write(' can send '), write(Cert), ");
        lines.add("     write(' to '), writeln(Receiver).");
        lines.add("");
        lines.add("sendCert(Sender, Receiver, cert(Sender, null, P, Class, V, Sq)):-");
        lines.add("	permission(Sender, P),");
        lines.add("	delegate(Sender, P, DelegationList),");
        lines.add("	isElement(Class, DelegationList),");
        lines.add("	assert(hasCert(Receiver, cert(Sender, null, P, Class, V, Sq))),");
        lines.add("	write(Sender), write(' is able to create '), ");
        lines.add("	write(cert(Sender, null, P, Class, V, Sq)),");
        lines.add("	write(' and to send it to '), writeln(Receiver).");
        lines.add("");
        lines.add("sendCert(Sender, Receiver, cert(Sender, Delegat, P, C, V, Sq)):-");
        lines.add("	permission(Sender, P),");
        lines.add("	delegate(Sender, P, _),	");
        lines.add("	assert(hasCert(Receiver, cert(Sender, Delegat, P, C, V, Sq))),");
        lines.add("	write(Sender), write(' is able to create '), ");
        lines.add("	write(cert(Sender, Delegat, P, C, V, Sq)),");
        lines.add("	write(' and to send it to '), writeln(Receiver).");
        lines.add("");
        lines.add("/*");
        lines.add(" *  all certificates in one message will be sent as far as possible");
        lines.add(" */");
        lines.add("");
        lines.add("sendAllCerts(_, _, _, []).");
        lines.add("");
        lines.add("sendAllCerts(Step, From, To, [F_Cert|Rest]):-");
        lines.add("	sendCert(From, To, F_Cert),");
        lines.add("	sendAllCerts(Step, From, To, Rest).");
        lines.add("");
        lines.add("sendAllCerts(Step, From, To, [F_Cert|Rest]):-");
        lines.add("	not(sendCert(From, To, F_Cert)),");
        lines.add("	sendAllCerts(Step, From, To, Rest),");
        lines.add("	write('ERROR: '),");
        lines.add("	write(From), write(' is not able to send '),");
        lines.add("	write(F_Cert), write(' to '), ");
        lines.add("	writeln(To).");
        lines.add("");
        lines.add("/*");
        lines.add(" *  program");
        lines.add(" */ ");
        lines.add("run(Steps):- run(0,Steps).");
        lines.add("");
        lines.add("run(Step, MaxSteps):- Step < MaxSteps,");
        lines.add("	msg(Step, Sender, Receiver, Name , P_List, C_List),");
        lines.add("");
        lines.add("	hasAllPermissions(Step, Sender, Receiver, Name, P_List),");
        lines.add("	sendAllCerts(Step, Sender, Receiver, C_List),	");
        lines.add("");
        lines.add("	write(Sender), write(' can send message '), ");
        lines.add("	write(Name), write(' to '), writeln(Receiver),");
        lines.add("	IPlus1 is Step + 1, run(IPlus1, MaxSteps).");
        lines.add("");
        lines.add("run(Step, MaxSteps):- Step < MaxSteps,");
        lines.add("	msg(Step, Sender, Receiver, Name , P_List, C_List),");
        lines.add("	not(hasAllPermissions(Step, Sender, Receiver, Name, P_List)),");
        lines.add("	sendAllCerts(Step, Sender, Receiver, C_List),	");
        lines.add("	write('ERROR: '),");
        lines.add("	write(Sender), write(' can not send message '), ");
        lines.add("	write(Name), write(' to '), writeln(Receiver),");
        lines.add("	IPlus1 is Step + 1, run(IPlus1, MaxSteps).");
        lines.add("");
        lines.add("run(Step, MaxSteps):- Step < MaxSteps,");
        lines.add("	msg(Step, Sender, Receiver, Name , P_List, C_List),");
        lines.add("	hasAllPermissions(Step, Sender, Receiver, Name, P_List),");
        lines.add("	not(sendAllCerts(Step, Sender, Receiver, C_List)),");
        lines.add("	write(Sender), write(' can send message '), ");
        lines.add("	write(Name), write(' to '), writeln(Receiver),");
        lines.add("	IPlus1 is Step + 1, run(IPlus1, MaxSteps).");
        lines.add("");
        lines.add("run(Step, MaxSteps):- Step < MaxSteps,");
        lines.add("	msg(Step, Sender, Receiver, Name , P_List, C_List),");
        lines.add("	not(hasAllPermissions(Step, Sender, Receiver, Name, P_List)),");
        lines.add("	not(sendAllCerts(Step, Sender, Receiver, C_List)),");
        lines.add("	write('ERROR: '),");
        lines.add("	write(Sender), write(' can not send message '), ");
        lines.add("	write(Name), write(' to '), writeln(Receiver),");
        lines.add("	IPlus1 is Step + 1, run(IPlus1, MaxSteps).");
        lines.add("");
        lines.add("run(Step, MaxSteps):-");
        lines.add("	Step < MaxSteps,");
        lines.add("    not(msg(Step, _, _, _, _,_ )),");
        lines.add("	IPlus1 is Step + 1, run(IPlus1, MaxSteps).");
        lines.add("");
        lines.add("run(MaxSteps, MaxSteps).");
        lines.add("");
        lines.add("run:-run(0,12).        ");

        for(int j = 0; j < lines.size(); j++)
        {
            textOutput.writeLn( (String) lines.get(j));
        }
        
        /*
         * saving to file
         */
        boolean error = false;
        if (fileParam != null && fileParam.getType() == fileParam.TypeFile){
            File file = fileParam.getAsFile();
            try
            {
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                
                for(int j = 0; j < lines.size(); j++)
                {
                bw.write((String) lines.get(j));
                bw.write("\n");
                }
                
                bw.close();
            }
            catch(java.io.IOException ioe)
            {
                 textOutput.writeLn("/** cannot save to file" +  
                                    fileParam.getAsFile()     + 
                                    "! **/");
                error = true;
            }
        }
        if (!error)
        {
            textOutput.writeLn("/** saved to file "  +  
                               fileParam.getAsFile() + 
                               " **/");
            if(fileParam.getAsFile().toString().startsWith("p")) // JS083021
            { textOutput.writeLn("/** evaluation begins **/");
              try { Vector output = new Vector();
                    String line = null;
                    String command = "/home/umlsec/UMLsec/applications/viki/bin/run-prolog.sh " + fileParam.getAsFile();
                    Process p = Runtime.getRuntime().exec(command);
                    BufferedReader lsOut = new BufferedReader(new InputStreamReader (p.getInputStream()));
                    while ( (line = lsOut.readLine()) != null)
                    { textOutput.writeLn(line); }
              } catch (Exception e) { textOutput.writeLn("Failure: " + e); }
              textOutput.writeLn("/** evaluation ends **/");
          }
        }
        return dump;				
    }
}