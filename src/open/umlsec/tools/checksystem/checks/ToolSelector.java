/**
 * 
 */
package open.umlsec.tools.checksystem.checks;

import java.util.Iterator;
import java.util.Vector;

import open.umlsec.tools.checksystem.SingleToolResult;
import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.collaborations.Collaboration;
import org.omg.uml.behavioralelements.collaborations.CollaborationClass;
import org.omg.uml.behavioralelements.commonbehavior.CommonBehaviorPackage;
import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.ComponentInstanceClass;
import org.omg.uml.behavioralelements.commonbehavior.Link;
import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateMachineClass;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.DependencyClass;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.UmlClassClass;

import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.tools.berechtigungen.ToolBerechtigungen;
import tum.umlsec.viki.tools.berechtigungen2.ToolBerechtigungen2;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStatic;
import tum.umlsec.viki.tools.permission.PermissionChecks;
import tum.umlsec.viki.tools.sequenceanalyser.ToolSequenceAnalyser;
import tum.umlsec.viki.tools.sequencediagram.ToolSequenceDiagramLocal;
import tum.umlsec.viki.tools.statechartexport.ToolExport;


/**
 * @author ska
 *
 */
public class ToolSelector {
	
	private static final String CONSTRAINT_FAIR_EXCHANGE = 		"fair exchange"; 
	private static final String CONSTRAINT_SECURE_DEPENDENCY = 	"secure dependency"; 
	private static final String CONSTRAINT_SECURE_LINKS = 		"secure links"; 
	private static final String CONSTRAINT_GUARDED_ACCESS = 	"guarded access"; 
	private static final String CONSTRAINT_PROVABLE = 			"provable"; 
	@SuppressWarnings("unused")
	private static final String CONSTRAINT_NO_DOWN_FLOW = 		"No down-flow";
	
	
	private static final int CID_NO_SUBCOMMAND 		= -1;
	
	private Vector<ToolDescriptor> toolList;

	/* create the tool signatures for the automatic tool selection */
	private ToolSignature toolCheckStatic = 	new ToolSignature(
								SystemVerificationLoader.TOOL_IDX_CHECKSTATIC);
	private ToolSignature toolActivity = 		new ToolSignature(
								SystemVerificationLoader.TOOL_IDX_ACTIVITY);
	private ToolSignature toolSequence = 		new ToolSignature(
									SystemVerificationLoader.TOOL_IDX_SEQUENCE);
	private ToolSignature toolStatechart = 		new ToolSignature(
								SystemVerificationLoader.TOOL_IDX_STATECHART);
	private ToolSignature toolSubSys = 			new ToolSignature(
									SystemVerificationLoader.TOOL_IDX_SUBSYSTEM);
	private ToolSignature toolPermActivity =	new ToolSignature(
						SystemVerificationLoader.TOOL_IDX_PERMISSION_ANALYSER_A);
	private ToolSignature toolPermSequence =	new ToolSignature(
						SystemVerificationLoader.TOOL_IDX_PERMISSION_ANALYSER_S);
	private ToolSignature toolCryptoState =		new ToolSignature(
						SystemVerificationLoader.TOOL_IDX_CRYPTO_FOL_ANALYSER_ST);
	private ToolSignature toolCryptoSeq =		new ToolSignature(
						SystemVerificationLoader.TOOL_IDX_CRYPTO_FOL_ANALYSER_SE);
	private ToolSignature toolCryptoAttack =	new ToolSignature(
						SystemVerificationLoader.TOOL_IDX_CRYPTO_ATTACK_GENERATOR);
	private ToolSignature toolActivityRbac =	new ToolSignature(
						SystemVerificationLoader.TOOL_IDX_ACTIVITY_RBAC_ANALYSER);
	private ToolSignature toolAuthFOL =			new ToolSignature(
					SystemVerificationLoader.TOOL_IDX_AUTHENTICITY_FOL_ANALYSER);
//	private ToolSignature toolCheckSafe =		new ToolSignature(
//									SystemVerificationLoader.TOOL_IDX_UMLSAFE);
	private ToolSignature toolumlsecNotationAnalyser = 	new ToolSignature(
								SystemVerificationLoader.TOOL_IDX_UMLSECNOTATIONANALYSER);
        private ToolSignature toolumlseChNotationAnalyser = 	new ToolSignature(
								SystemVerificationLoader.TOOL_IDX_UMLSECHNOTATIONANALYSER);
	private ToolSignature toolumlseChStaticCheck = 	new ToolSignature(
								SystemVerificationLoader.TOOL_IDX_UMLSECHSTATICCHECK);
	private ToolSignature toolRbacToJava = new ToolSignature(
								SystemVerificationLoader.TOOL_IDX_RBAC_TO_JAVA_GENERATOR);
	private ToolSignature toolRbacToAspectJ = new ToolSignature(
								SystemVerificationLoader.TOOL_IDX_RBAC_TO_ASPECTJ_GENERATOR);

	public ToolSelector () {
		
		/* configure signatures */
		toolCheckStatic.setConstraint(CONSTRAINT_SECURE_DEPENDENCY, ToolCheckStatic.CID_SECURE_DEPENDENCY);
		toolCheckStatic.addStereotype("secrecy", ToolCheckStatic.CID_SECURE_DEPENDENCY);
		toolCheckStatic.addTag("secret", ToolCheckStatic.CID_SECURE_DEPENDENCY);
		
		toolCheckStatic.setConstraint(CONSTRAINT_SECURE_LINKS, ToolCheckStatic.CID_SECURE_LINKS);
		toolCheckStatic.addStereotype("secrecy", ToolCheckStatic.CID_SECURE_LINKS);
		toolCheckStatic.addStereotype("encrypted", ToolCheckStatic.CID_SECURE_LINKS);
		
		toolCheckStatic.setConstraint(CONSTRAINT_GUARDED_ACCESS, ToolCheckStatic.CID_GUARDED_ACCESS);
		toolCheckStatic.addStereotype("guarded", ToolCheckStatic.CID_GUARDED_ACCESS);
		toolCheckStatic.addTag("guard", ToolCheckStatic.CID_GUARDED_ACCESS);
		
		toolCheckStatic.setConstraint(CONSTRAINT_FAIR_EXCHANGE, ToolCheckStatic.CID_FAIR_EXCHANGE);
		toolCheckStatic.addTag("start", ToolCheckStatic.CID_FAIR_EXCHANGE);
		toolCheckStatic.addTag("stop", ToolCheckStatic.CID_FAIR_EXCHANGE);
		
		toolCheckStatic.setConstraint(CONSTRAINT_PROVABLE, ToolCheckStatic.CID_PROVABLE);
		toolCheckStatic.addTag("action", ToolCheckStatic.CID_PROVABLE);
		toolCheckStatic.addTag("cert", ToolCheckStatic.CID_PROVABLE);
		
		toolPermActivity.addTag("id", ToolBerechtigungen.CID_BERECHTIGUNGEN_INIT1);
		toolPermActivity.addTag("user", ToolBerechtigungen.CID_BERECHTIGUNGEN_INIT1);
		toolPermActivity.addTag("permission", ToolBerechtigungen.CID_BERECHTIGUNGEN_INIT1);

		toolPermSequence.addTag("delegation", PermissionChecks.CID_COMMAND01);
		toolPermSequence.addTag("permission", PermissionChecks.CID_COMMAND01);
		toolPermSequence.addTag("delegation", PermissionChecks.CID_COMMAND02);
		toolPermSequence.addTag("permission", PermissionChecks.CID_COMMAND02);

		toolCryptoState.addTag("initial knowledge", ToolExport.CID_COMMAND01);
		toolCryptoState.addTag("secret", ToolExport.CID_COMMAND01);
		toolCryptoState.addTag("initial knowledge", ToolExport.CID_RUNESETHEO);
		toolCryptoState.addTag("secret", ToolExport.CID_RUNESETHEO);
		toolCryptoState.addTag("initial knowledge", ToolExport.CID_RESULTONLY);
		toolCryptoState.addTag("secret", ToolExport.CID_RESULTONLY);

		toolCryptoSeq.addTag("initial knowledge", ToolSequenceAnalyser.CID_CHECKVARS);
		toolCryptoSeq.addTag("guard_notation", ToolSequenceAnalyser.CID_CHECKVARS);
		toolCryptoSeq.addTag("secret", ToolSequenceAnalyser.CID_CHECKVARS);
		toolCryptoSeq.addTag("notation", ToolSequenceAnalyser.CID_CHECKVARS);
		toolCryptoSeq.addTag("initial knowledge", ToolSequenceAnalyser.CID_DISPLAYTPTP);
		toolCryptoSeq.addTag("guard_notation", ToolSequenceAnalyser.CID_DISPLAYTPTP);
		toolCryptoSeq.addTag("secret", ToolSequenceAnalyser.CID_DISPLAYTPTP);
		toolCryptoSeq.addTag("notation", ToolSequenceAnalyser.CID_DISPLAYTPTP);
		toolCryptoSeq.addTag("initial knowledge", ToolSequenceAnalyser.CID_MESSAGES);
		toolCryptoSeq.addTag("guard_notation", ToolSequenceAnalyser.CID_MESSAGES);
		toolCryptoSeq.addTag("secret", ToolSequenceAnalyser.CID_MESSAGES);
		toolCryptoSeq.addTag("notation", ToolSequenceAnalyser.CID_MESSAGES);
		toolCryptoSeq.addTag("initial knowledge", ToolSequenceAnalyser.CID_RUNESETHEO);
		toolCryptoSeq.addTag("guard_notation", ToolSequenceAnalyser.CID_RUNESETHEO);
		toolCryptoSeq.addTag("secret", ToolSequenceAnalyser.CID_RUNESETHEO);
		toolCryptoSeq.addTag("notation", ToolSequenceAnalyser.CID_RUNESETHEO);
		toolCryptoSeq.addTag("initial knowledge", ToolSequenceAnalyser.CID_RUNESETHEO_STATUS);
		toolCryptoSeq.addTag("guard_notation", ToolSequenceAnalyser.CID_RUNESETHEO_STATUS);
		toolCryptoSeq.addTag("secret", ToolSequenceAnalyser.CID_RUNESETHEO_STATUS);
		toolCryptoSeq.addTag("notation", ToolSequenceAnalyser.CID_RUNESETHEO_STATUS);
		toolCryptoSeq.addTag("initial knowledge", ToolSequenceAnalyser.CID_RUNSPASS);
		toolCryptoSeq.addTag("guard_notation", ToolSequenceAnalyser.CID_RUNSPASS);
		toolCryptoSeq.addTag("secret", ToolSequenceAnalyser.CID_RUNSPASS);
		toolCryptoSeq.addTag("notation", ToolSequenceAnalyser.CID_RUNSPASS);
		toolCryptoSeq.addTag("initial knowledge", ToolSequenceAnalyser.CID_RUNSPASS_STATUS);
		toolCryptoSeq.addTag("guard_notation", ToolSequenceAnalyser.CID_RUNSPASS_STATUS);
		toolCryptoSeq.addTag("secret", ToolSequenceAnalyser.CID_RUNSPASS_STATUS);
		toolCryptoSeq.addTag("notation", ToolSequenceAnalyser.CID_RUNSPASS_STATUS);
		toolCryptoSeq.addTag("initial knowledge", ToolSequenceAnalyser.CID_SAVETPTP);
		toolCryptoSeq.addTag("guard_notation", ToolSequenceAnalyser.CID_SAVETPTP);
		toolCryptoSeq.addTag("secret", ToolSequenceAnalyser.CID_SAVETPTP);
		toolCryptoSeq.addTag("notation", ToolSequenceAnalyser.CID_SAVETPTP);
		 
		toolCryptoAttack.addTag("initial knowledge", CID_NO_SUBCOMMAND);
		toolCryptoAttack.addTag("secret", CID_NO_SUBCOMMAND);

		toolActivityRbac.addTag("protected", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		toolActivityRbac.addTag("role", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		toolActivityRbac.addTag("right", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		
		toolAuthFOL.addTag("Condition", ToolSequenceDiagramLocal.CID_SEQUENCE_PARSER);
		toolAuthFOL.addTag("Predicate", ToolSequenceDiagramLocal.CID_SEQUENCE_PARSER);

		toolRbacToJava.addTag("protected", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		toolRbacToJava.addTag("role", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		toolRbacToJava.addTag("right", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		
		toolRbacToAspectJ.addTag("protected", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		toolRbacToAspectJ.addTag("role", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		toolRbacToAspectJ.addTag("right", ToolBerechtigungen2.CID_BERECHTIGUNGEN_INIT3);
		
		toolList = new Vector<ToolDescriptor>();
	}
	
	public boolean addTool (ToolDescriptor tool) {
		boolean bAdded = false;
		if (toolList != null)
		{
			if (!(toolList.contains(tool))){
				toolList.add(tool);
				bAdded = true;
			}
		}
		return bAdded;
	}

        /* Calls the method to remove ToolDescriptor from toolList */
        public void removeTool (String refTool){
            ToolDescriptor toolDesc = null;
            if (this.getTool(refTool) != null){
                toolDesc = this.getTool(refTool);
                this.removeTool(toolDesc);
            }
        }

        /* Calls the method to remove ToolDescriptor from toolList */
        public void removeTool (IVikiToolBase refTool){
            ToolDescriptor toolDesc = null;
            if (this.getTool(refTool.getToolName()) != null){
                toolDesc = this.getTool(refTool.getToolName());
                this.removeTool(toolDesc);
            }
        }

        /* Remove a ToolDescriptor from toolList */
        public boolean removeTool (ToolDescriptor tool) {
		boolean bRemoved = false;
		if (toolList != null)
		{
			if (toolList.contains(tool)){
				toolList.remove(tool);
				bRemoved = true;
			}
		}
		return bRemoved;
	}
	
	public Vector<ToolDescriptor> getToolList () {
		return toolList;
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasTool(ToolDescriptor tool){
		boolean bFound = false;
		
		for (Iterator i = (Iterator) toolList.iterator(); i.hasNext();){
			ToolDescriptor myTool = (ToolDescriptor) i.next();
			
			if (myTool.getToolReference().getToolName().equals(
					tool.getToolReference().getToolName())){
				bFound = true;
				break;
			}
		}
		return bFound;
	}
	
	public ToolDescriptor getTool(String toolName){
		ToolDescriptor myTool = null;
		for (int i = 0; i< toolList.size(); i++){
			ToolDescriptor tool = (ToolDescriptor) toolList.get(i);
			
			if (tool.getToolReference().getToolName().equals(toolName)){
				myTool = tool;
				break;
			}
		}
		return myTool;
	}
	
	@SuppressWarnings("unchecked")
	public void searchToolsBySignature(IMdrContainer mdr){
		ToolDescriptor 				toolDesc = null;
		
		ActivityGraphsPackage 		activityPackage;
		ActivityGraph 				graph;
		CommonBehaviorPackage 		commonPackage;
		IMdrContainer				mdrContainer;
		UmlPackage 					root ;
		CorePackage 				corePackage;
		DependencyClass 			dependencyClass;
		
		
		/* clear all results first */
		this.clearAll();
		
		mdrContainer = mdr;
		if (!(mdrContainer.isEmpty())){
			root = mdrContainer.getUmlPackage();
			corePackage = root.getCore();
			commonPackage= root.getCommonBehavior();
			dependencyClass = corePackage.getDependency();
			
			/* check all links*/
			ComponentInstanceClass componentInstClasses = commonPackage.getComponentInstance();
			for(Iterator itComp = componentInstClasses.refAllOfClass().iterator(); 
			itComp.hasNext();){
				ComponentInstance componentinst = (ComponentInstance)itComp.next();

				//System.out.println("Namespace: " + componentinst.getNodeInstance().getNamespace().getName());	
				for (Iterator it6 =(componentinst.getNodeInstance().getLinkEnd()).iterator(); it6.hasNext();){
					LinkEnd linkEnd_S = (LinkEnd) it6.next();
					Link link_S = linkEnd_S.getLink();

					for(Iterator itTags = link_S.getTaggedValue().iterator();itTags.hasNext();){
						TaggedValue tag = (TaggedValue)itTags.next();
						String tagName = tag.getType().getName();
						
						lookForCheckStatic(tagName, null);
						lookForCryptoState(tagName, null);
						lookForCryptoSeq(tagName, null);
						lookForPermSequence(tagName, null);
//						System.out.println("Tag: " + tagName);
					}

					for(Iterator itSt = link_S.getStereotype().iterator();itSt.hasNext();){
						Stereotype stereotype = (Stereotype)itSt.next();
						String stereotypeName = stereotype.getName();
						
						lookForCheckStatic(null, stereotypeName);
						lookForCryptoState(null, stereotypeName);
						lookForCryptoSeq(null, stereotypeName);
						lookForPermSequence(null, stereotypeName);
//						System.out.println("Stereotype: " + stereotypeName);
					}
					for (Iterator ix = componentinst.getNamespace().getStereotype().iterator();
					ix.hasNext();){
						Stereotype stereotype = (Stereotype) ix.next();
						String toolName = stereotype.getName();
						
						lookForCheckStatic(null, toolName);
						lookForCryptoState(null, toolName);
						lookForCryptoSeq(null, toolName);
						lookForPermSequence(null, toolName);
					}
					
				}
			}
			
			/* check all dependencies*/
			for (Iterator iter1 = dependencyClass.refAllOfClass().iterator(); iter1.hasNext();) {
				Dependency dependency_S = (Dependency)iter1.next();

				for(Iterator itTags = dependency_S.getTaggedValue().iterator();itTags.hasNext();){
					TaggedValue tag = (TaggedValue)itTags.next();
					String tagName = tag.getType().getName();
					
					lookForCheckStatic(tagName, null);
					lookForCryptoState(tagName, null);
					lookForCryptoSeq(tagName, null);
					lookForPermSequence(tagName, null);
//					System.out.println("Tag: " + tagName);
				}

				for(Iterator itSt = dependency_S.getStereotype().iterator();itSt.hasNext();){
					Stereotype stereotype = (Stereotype)itSt.next();
					String stereotypeName = stereotype.getName();
					
					lookForCheckStatic(null, stereotypeName);
					lookForCryptoState(null, stereotypeName);
					lookForCryptoSeq(null, stereotypeName);
					lookForPermSequence(null, stereotypeName);
//					System.out.println("Stereotype: " + stereotypeName);
				}
			}
			
			
			/* check all classes */
			UmlClassClass umlClassClass = corePackage.getUmlClass();
			for (Iterator it = umlClassClass.refAllOfClass().iterator(); it.hasNext();) {
				// all information about the Class can be accessed through the UmlClass class here
				UmlClass umlClass = (UmlClass) it.next();
				for(Iterator itTags = umlClass.getTaggedValue().iterator();itTags.hasNext();){
					TaggedValue tag = (TaggedValue)itTags.next();
					String tagName = tag.getType().getName();
					
					lookForCheckStatic(tagName, null);
					lookForCryptoState(tagName, null);
					lookForCryptoSeq(tagName, null);
					lookForPermSequence(tagName, null);
//					System.out.println("Tag: " + tagName);
				}
				for(Iterator itSt = umlClass.getStereotype().iterator();itSt.hasNext();){
					Stereotype stereotype = (Stereotype)itSt.next();
					String stereotypeName = stereotype.getName();
					
					lookForCheckStatic(null, stereotypeName);
					lookForCryptoState(null, stereotypeName);
					lookForCryptoSeq(null, stereotypeName);
					lookForPermSequence(null, stereotypeName);
//					System.out.println("Stereotype: " + stereotypeName);
				}
				
				/* packages that the class belongs to */
				for (Iterator x = umlClass.getNamespace().getStereotype().iterator();x.hasNext();){
					Stereotype stereotype = (Stereotype) x.next();
					String toolName = stereotype.getName();
					
					lookForCheckStatic(null, toolName);
					lookForCryptoState(null, toolName);
					lookForCryptoSeq(null, toolName);
					lookForPermSequence(null, toolName);
				}
				
			}
			/* activity diagrams */
			activityPackage=(ActivityGraphsPackage)root.getActivityGraphs();

			for (Iterator itGraph = 
				activityPackage.getActivityGraph().refAllOfClass().iterator(); 
																	itGraph.hasNext(); ) {
				graph = (ActivityGraph)itGraph.next();
				for(Iterator itTags = graph.getTaggedValue().iterator();itTags.hasNext();){
					TaggedValue tag = (TaggedValue)itTags.next();
					String tagName = tag.getType().getName();
					
					lookForCheckStatic(tagName, null);
					lookForCryptoState(tagName, null);
					lookForCryptoSeq(tagName, null);
					lookForPermSequence(tagName, null);
					lookForActivityRbac(tagName, null);
				}
				for(Iterator itSt = graph.getStereotype().iterator();itSt.hasNext();){
					Stereotype stereotype = (Stereotype)itSt.next();
					String stereotypeName = stereotype.getName();
					
					lookForCheckStatic(null, stereotypeName);
					lookForCryptoState(null, stereotypeName);
					lookForCryptoSeq(null, stereotypeName);
					lookForPermSequence(null, stereotypeName);
//					System.out.println("Stereotype: " + stereotypeName);
				}
				
				/* check activities */
				for (Iterator it_act_I_3 = graph.getTransitions().iterator(); it_act_I_3.hasNext();)
				{
					Transition tmp_transition = (Transition)it_act_I_3.next();
					StateVertex tmp = (StateVertex)tmp_transition.getTarget();
					if (tmp.getClass().toString().equals(
							"class org.omg.uml.behavioralelements.activitygraphs.ActionState$Impl"))
					{
						for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); 
												it_taggedValueIt.hasNext();)
						{
							TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
							String tagName = tmp_taggedValue.getType().getName();						
							lookForPermActivity(tagName, null);
							//System.out.println("Tag: " + tagName);
						}
					}
				}
			}
			
			/* sequence diagrams */
			CollaborationClass collab = root.getCollaborations().getCollaboration();
			for (Iterator itSeq = collab.refAllOfClass().iterator(); itSeq.hasNext();) 
			{
				Collaboration c = (Collaboration)itSeq.next();
				for(Iterator itTags = c.getTaggedValue().iterator();itTags.hasNext();){
					TaggedValue tag = (TaggedValue)itTags.next();
					String tagName = tag.getType().getName();
					
					lookForCheckStatic(tagName, null);
					lookForCryptoState(tagName, null);
					lookForCryptoSeq(tagName, null);
					lookForPermSequence(tagName, null);
//					System.out.println("Tag: " + tagName);
				}
				for(Iterator itSt = c.getStereotype().iterator();itSt.hasNext();){
					Stereotype stereotype = (Stereotype)itSt.next();
					String stereotypeName = stereotype.getName();
					
					lookForCheckStatic(null, stereotypeName);
					lookForCryptoState(null, stereotypeName);
					lookForCryptoSeq(null, stereotypeName);
					lookForPermSequence(null, stereotypeName);
//					System.out.println("Stereotype: " + stereotypeName);
				}
				
			}
			
			/* tags belonging to the model */
			TaggedValueClass taVaCl = corePackage.getTaggedValue();
			for (Iterator itTags = taVaCl.refAllOfClass().iterator(); itTags.hasNext();) {
				TaggedValue tag = (TaggedValue)itTags.next();
				String tagName = tag.getType().getName();
				
				lookForCheckStatic(tagName, null);
				lookForCryptoSeq(tagName, null);
				lookForAuthFOL(tagName, null);
				lookForActivityRbac(tagName, null);
//				System.out.println("Tag: " + tagName);
			}

			
			/* Statecharts */
			StateMachineClass stpackage = root.getStateMachines().getStateMachine();
			for (Iterator itState = stpackage.refAllOfClass().iterator();
			itState.hasNext();) 
			{
				StateMachine st= (StateMachine)itState.next();
				for(Iterator itTags = st.getTaggedValue().iterator();itTags.hasNext();){
					TaggedValue tag = (TaggedValue)itTags.next();
					String tagName = tag.getType().getName();
					
					lookForCheckStatic(tagName, null);
					lookForCryptoState(tagName, null);
					lookForCryptoSeq(tagName, null);
					lookForPermSequence(tagName, null);
//					System.out.println("Tag: " + tagName);
				}
				for(Iterator itSt = st.getStereotype().iterator();itSt.hasNext();){
					Stereotype stereotype = (Stereotype)itSt.next();
					String stereotypeName = stereotype.getName();
					
					lookForCheckStatic(null, stereotypeName);
					lookForCryptoState(null, stereotypeName);
					lookForCryptoSeq(null, stereotypeName);
					lookForPermSequence(null, stereotypeName);
//					System.out.println("Stereotype: " + stereotypeName);
				}
			}
		}


        //Automatically adding our new Notation/Tools with all its commands
        toolDesc = new ToolDescriptor();

        IVikiToolBase refTool = SystemVerificationLoader.getTool(SystemVerificationLoader.TOOL_IDX_UMLSECNOTATIONANALYSER);
        toolDesc.setToolReference(refTool);

        for (Iterator<CommandDescriptor> it = refTool.getConsole().getConsoleCommands(); it.hasNext();){
            CommandDescriptor cmd = it.next();
            toolDesc.addCommand(new Command(cmd.getId(),cmd.getName()));

            SystemVerificationLoader.logger.trace("Tool \""+refTool.getToolName()+"\" adding command \""+cmd.getName()+"\"");
        }
        SystemVerificationLoader.logger.trace("Adding tool \""+refTool.getToolName()+"\"");
        this.addTool(toolDesc);

        toolDesc = new ToolDescriptor();
        refTool = SystemVerificationLoader.getTool(SystemVerificationLoader.TOOL_IDX_UMLSECHNOTATIONANALYSER);
        toolDesc.setToolReference(refTool);

        for (Iterator<CommandDescriptor> it = refTool.getConsole().getConsoleCommands(); it.hasNext();){
            CommandDescriptor cmd = it.next();
            toolDesc.addCommand(new Command(cmd.getId(),cmd.getName()));

            SystemVerificationLoader.logger.trace("Tool \""+refTool.getToolName()+"\" adding command \""+cmd.getName()+"\"");
        }
        SystemVerificationLoader.logger.trace("Adding tool \""+refTool.getToolName()+"\"");
        this.addTool(toolDesc);
            
            
	}

	@SuppressWarnings("unchecked")
	public void searchTools(IMdrContainer mdr){
		ToolDescriptor 				toolDesc = null;
		
		ActivityGraphsPackage 		activityPackage;
		ActivityGraph 				graph;
		CommonBehaviorPackage 		commonPackage;
		IMdrContainer				mdrContainer;
		UmlPackage 					root ;
		CorePackage 				corePackage;
		
		mdrContainer = mdr;
		if (!(mdrContainer.isEmpty())){
			root = mdrContainer.getUmlPackage();
			corePackage = root.getCore();
			commonPackage= root.getCommonBehavior();
			
			/* packages that do not belong to a dedicated use case */
		     ComponentInstanceClass componentInstClasses = commonPackage.getComponentInstance();
		     for(Iterator itComp = componentInstClasses.refAllOfClass().iterator(); 
		     														itComp.hasNext();){
		    	 ComponentInstance componentinst = (ComponentInstance)itComp.next();
		    	 
    			 //System.out.println("Namespace: " + componentinst.getNodeInstance().getNamespace().getName());	
		    	 for (Iterator ix = componentinst.getNamespace().getStereotype().iterator();
		    	 															ix.hasNext();){
		    		Stereotype stereotype = (Stereotype) ix.next();
					String toolName = stereotype.getName();
                                        
		    		try {
						if (toolName.equals("secure links")){
							IVikiToolBase refTool = SystemVerificationLoader.getTool(
									SystemVerificationLoader.TOOL_IDX_CHECKSTATIC);
							if (this.getTool(refTool.getToolName()) != null){
								toolDesc = this.getTool(refTool.getToolName());
								if (!(toolDesc.hasCommandId(ToolCheckStatic.CID_SECURE_LINKS))){
									toolDesc.addCommandId(ToolCheckStatic.CID_SECURE_LINKS);
									toolDesc.addCommandName(toolName);
									toolDesc.addCommandResult(new SingleToolResult());
								}
							} else {
								toolDesc = new ToolDescriptor();	
								toolDesc.setToolReference(SystemVerificationLoader.getTool(
										SystemVerificationLoader.TOOL_IDX_CHECKSTATIC));
								
								toolDesc.addCommandId(ToolCheckStatic.CID_SECURE_LINKS);
								toolDesc.addCommandName(toolName);
								toolDesc.addCommandResult(new SingleToolResult());
								this.addTool(toolDesc);
							}
								
						}
		    		} catch (Exception e) {
						
		    		}
		    	 }
		      }
			
			/* packages that are bound to a dedicated use case */
			UmlClassClass umlClassClass = corePackage.getUmlClass();
			for (Iterator it = umlClassClass.refAllOfClass().iterator();
																it.hasNext();) {
				// all information about the Class can be accessed through the UmlClass class here
				UmlClass umlClass = (UmlClass) it.next();
				
				//System.out.println("Namespace: " + umlClass.getNamespace().getName());
				for (Iterator x = umlClass.getNamespace().getStereotype().iterator();x.hasNext();){
					Stereotype stereotype = (Stereotype) x.next();
					String toolName = stereotype.getName();
					try {
						if (toolName.equals("secure dependency")){
							IVikiToolBase refTool = SystemVerificationLoader.getTool(
									SystemVerificationLoader.TOOL_IDX_CHECKSTATIC);
							if (this.getTool(refTool.getToolName()) != null){
								toolDesc = this.getTool(refTool.getToolName());
								if (!(toolDesc.hasCommandId(ToolCheckStatic.CID_SECURE_DEPENDENCY))){
									toolDesc.addCommandId(ToolCheckStatic.CID_SECURE_DEPENDENCY);
									toolDesc.addCommandName(toolName);
									toolDesc.addCommandResult(new SingleToolResult());
								}
							} else {
								toolDesc = new ToolDescriptor();	
								toolDesc.setToolReference(SystemVerificationLoader.getTool(
										SystemVerificationLoader.TOOL_IDX_CHECKSTATIC));
								
								toolDesc.addCommandId(ToolCheckStatic.CID_SECURE_DEPENDENCY);
								toolDesc.addCommandName(toolName);
								toolDesc.addCommandResult(new SingleToolResult());
								this.addTool(toolDesc);
							}
						}
					} catch (Exception e) {
						System.out.println("open.umlsec.tools.checksystem.checks.ToolSelector: " 
								+ e.toString() + ": " + e.getMessage());
					}
				}
			}
			
			/* activity diagrams */
			activityPackage=(ActivityGraphsPackage)root.getActivityGraphs();

			for (Iterator itGraph = 
				activityPackage.getActivityGraph().refAllOfClass().iterator(); 
																	itGraph.hasNext(); ) {

				graph = (ActivityGraph)itGraph.next();
				
				for (Iterator i = graph.getStereotype().iterator();i.hasNext();) {
					Stereotype stereotype = (Stereotype) i.next();
					String toolName = stereotype.getName();
					
					try {
						if (toolName.equals("fair exchange")){
							IVikiToolBase refTool = SystemVerificationLoader.getTool(
									SystemVerificationLoader.TOOL_IDX_CHECKSTATIC);
							if (this.getTool(refTool.getToolName()) != null){
								toolDesc = this.getTool(refTool.getToolName());
								if (!(toolDesc.hasCommandId(ToolCheckStatic.CID_FAIR_EXCHANGE))){
									toolDesc.addCommandName(toolName);
									toolDesc.addCommandId(ToolCheckStatic.CID_FAIR_EXCHANGE);
									toolDesc.addCommandResult(new SingleToolResult());
								}
							} else {
								toolDesc = new ToolDescriptor();	
								toolDesc.setToolReference(SystemVerificationLoader.getTool(
										SystemVerificationLoader.TOOL_IDX_CHECKSTATIC));
								
								toolDesc.addCommandId(ToolCheckStatic.CID_FAIR_EXCHANGE);
								toolDesc.addCommandName(toolName);
								toolDesc.addCommandResult(new SingleToolResult());
								this.addTool(toolDesc);
							}
							
						} else if (toolName.equals("provable")){
							IVikiToolBase refTool = SystemVerificationLoader.getTool(
									SystemVerificationLoader.TOOL_IDX_CHECKSTATIC);
							if (this.getTool(refTool.getToolName()) != null){
								toolDesc = this.getTool(refTool.getToolName());
								if (!(toolDesc.hasCommandId(ToolCheckStatic.CID_PROVABLE))){
									toolDesc.addCommandId(ToolCheckStatic.CID_PROVABLE);
									toolDesc.addCommandName(toolName);
									toolDesc.addCommandResult(new SingleToolResult());
								}
							} else {
								toolDesc = new ToolDescriptor();	
								toolDesc.setToolReference(SystemVerificationLoader.getTool(
										SystemVerificationLoader.TOOL_IDX_CHECKSTATIC));
								
								toolDesc.addCommandId(ToolCheckStatic.CID_PROVABLE);
								toolDesc.addCommandName(toolName);
								toolDesc.addCommandResult(new SingleToolResult());
								this.addTool(toolDesc);
							}
						}
					} catch (Exception e) {
						System.out.println("open.umlsec.tools.checksystem.checks.ToolSelector: " 
								+ e.toString() + ": " + e.getMessage());
					}
				}
			}
		}
		
	}
	
	public void clearAll(){
		
		/* clear all signature status */
		toolCheckStatic.resetQueryResult();
		toolActivity.resetQueryResult();
		toolActivityRbac.resetQueryResult();
		toolAuthFOL.resetQueryResult();
		toolCryptoAttack.resetQueryResult();
		toolCryptoSeq.resetQueryResult();
		toolCryptoState.resetQueryResult();
		toolPermActivity.resetQueryResult();
		toolSequence.resetQueryResult();
		toolStatechart.resetQueryResult();
		toolSubSys.resetQueryResult();
		toolPermSequence.resetQueryResult();
		toolumlsecNotationAnalyser.resetQueryResult(); //New Tool
        toolumlseChNotationAnalyser.resetQueryResult(); //New Tool
        toolumlseChStaticCheck.resetQueryResult(); //New Tool
        toolRbacToJava.resetQueryResult();
        toolRbacToAspectJ.resetQueryResult();

		toolList.clear();
	}
	
	public String toString() {
		return toolList.toString();
	}

	public void addTool(IVikiToolBase refTool, Vector<Integer>	Ids){
		lookForSubCommands(refTool, Ids);
	}
	
	/* private methods */
	private boolean lookForCheckStatic(String tag, String stereotype){
		Vector<Integer>	Ids			= null;
		boolean 		bFound 		= false;
		
		
		if (tag != null){
			bFound = toolCheckStatic.hasTag(tag);			
		}
		
		if (stereotype != null){
			/* check weather the stereotype is equal to the name of the constraint*/
			/* this will then trigger the direct selection of the tool */
			//System.out.println("Stereotype: " + stereotype);
			bFound = toolCheckStatic.hasConstraint(stereotype);
			if (bFound != true){
				bFound = toolCheckStatic.hasStereotype(stereotype);			
			}
//			bFound = toolCheckStatic.hasStereotype(stereotype);			
		}
		
		if (bFound == true){
			
			Ids = toolCheckStatic.getQueryResult();
			
			IVikiToolBase refTool = SystemVerificationLoader.getTool(
					SystemVerificationLoader.TOOL_IDX_CHECKSTATIC);
			
			lookForSubCommands(refTool,	Ids);			
			
		}
		
		return bFound;
	}
	
	@SuppressWarnings("unused")
	private boolean lookForSequence(){
		boolean bFound = false;
		
		return bFound;
	}
	
	@SuppressWarnings("unused")
	private boolean lookForStatechart(){
		boolean bFound = false;
		
		return bFound;
	}
	
	private boolean lookForPermActivity(String tag, String stereotype){
		Vector<Integer>	Ids			= null;
		boolean 		bFound 		= false;
		
		if (tag != null){
			bFound = toolPermActivity.hasTag(tag);			
		}
		
		if (stereotype != null){
			bFound = toolPermActivity.hasStereotype(stereotype);			
		}
		
		if (bFound == true){
			
			Ids = toolPermActivity.getQueryResult();
			
			IVikiToolBase refTool = SystemVerificationLoader.getTool(
					SystemVerificationLoader.TOOL_IDX_PERMISSION_ANALYSER_A);
			
			lookForSubCommands(refTool,	Ids);			
			
		}
		
		return bFound;
	}
	
	private boolean lookForPermSequence(String tag, String stereotype){
		Vector<Integer>	Ids			= null;
		boolean 		bFound 		= false;
		
		if (tag != null){
			bFound = toolPermSequence.hasTag(tag);			
		}
		
		if (stereotype != null){
			bFound = toolPermSequence.hasStereotype(stereotype);			
		}
		
		if (bFound == true){
			
			Ids = toolPermSequence.getQueryResult();
			
			IVikiToolBase refTool = SystemVerificationLoader.getTool(
					SystemVerificationLoader.TOOL_IDX_PERMISSION_ANALYSER_S);
			
			lookForSubCommands(refTool,	Ids);			
			
		}
		
		return bFound;
	}
	
	private boolean lookForCryptoState(String tag, String stereotype){
		Vector<Integer>	Ids			= null;
		boolean 		bFound 		= false;
		
		if (tag != null){
			bFound = toolCryptoState.hasTag(tag);			
		}
		
		if (stereotype != null){
			bFound = toolCryptoState.hasStereotype(stereotype);			
		}
		
		if (bFound == true){
			
			Ids = toolCryptoState.getQueryResult();
			
			IVikiToolBase refTool = SystemVerificationLoader.getTool(
					SystemVerificationLoader.TOOL_IDX_CRYPTO_FOL_ANALYSER_ST);
			
			lookForSubCommands(refTool,	Ids);			
		}
		
		return bFound;
	}
	
	private boolean lookForCryptoSeq(String tag, String stereotype){
		Vector<Integer>	Ids			= null;
		boolean 		bFound 		= false;
		
		if (tag != null){
			bFound = toolCryptoSeq.hasTag(tag);			
		}
		
		if (stereotype != null){
			bFound = toolCryptoSeq.hasStereotype(stereotype);			
		}
		
		if (bFound == true){
			
			Ids = toolCryptoSeq.getQueryResult();
			
			IVikiToolBase refTool = SystemVerificationLoader.getTool(
					SystemVerificationLoader.TOOL_IDX_CRYPTO_FOL_ANALYSER_SE);
			
			lookForSubCommands(refTool,	Ids);			
		}
		
		return bFound;
	}
	
	@SuppressWarnings("unused")
	private boolean lookForCryptoAttack(){
		boolean bFound = false;
		
		return bFound;
	}
	
	private boolean lookForActivityRbac(String tag, String stereotype){
		Vector<Integer>	Ids			= null;
		boolean 		bFound 		= false;
		
		if (tag != null){
			bFound = toolActivityRbac.hasTag(tag);			
		}
		
		if (stereotype != null){
			bFound = toolActivityRbac.hasStereotype(stereotype);			
		}
		
		if (bFound == true){
			
			Ids = toolActivityRbac.getQueryResult();
			
			IVikiToolBase refTool = SystemVerificationLoader.getTool(
					SystemVerificationLoader.TOOL_IDX_ACTIVITY_RBAC_ANALYSER);
			
			lookForSubCommands(refTool,	Ids);			
		}
		
		return bFound;
	}
	
	private boolean lookForAuthFOL(String tag, String stereotype){
		Vector<Integer>	Ids			= null;
		boolean 		bFound 		= false;
		
		if (tag != null){
			bFound = toolAuthFOL.hasTag(tag);			
		}
		
		if (stereotype != null){
			bFound = toolAuthFOL.hasStereotype(stereotype);			
		}
		
		if (bFound == true){
			
			Ids = toolAuthFOL.getQueryResult();
			
			IVikiToolBase refTool = SystemVerificationLoader.getTool(
					SystemVerificationLoader.TOOL_IDX_AUTHENTICITY_FOL_ANALYSER);
			
			lookForSubCommands(refTool,	Ids);			
		}
		
		return bFound;
	}

	@SuppressWarnings("unchecked")
	private void lookForSubCommands(IVikiToolBase refTool, Vector<Integer>	Ids){
		ToolDescriptor 	toolDesc 	= null;
		for (int i = 1; i < Ids.size(); i++){
			int id = Ids.elementAt(i);
			if (this.getTool(refTool.getToolName()) != null){
				toolDesc = this.getTool(refTool.getToolName());
				if (!(toolDesc.hasCommandId(id))){
					for (Iterator<CommandDescriptor> it = 
						refTool.getConsole().getConsoleCommands(); it.hasNext();){
						CommandDescriptor cmd = it.next();
						if (cmd.getId() == id){
							toolDesc.addCommand(new Command(id,cmd.getName()));
							break;
						}
					}
				}
			} else {
				toolDesc = new ToolDescriptor();	
				toolDesc.setToolReference(refTool);
				
				toolDesc.addCommandId(id);
				for (Iterator<CommandDescriptor> it = 
					refTool.getConsole().getConsoleCommands(); it.hasNext();){
					CommandDescriptor cmd = it.next();
					if (cmd.getId() == id){
						toolDesc.addCommand(new Command(id,cmd.getName()));
						break;
					}
				}
				this.addTool(toolDesc);
			}
		}
	}
	
}