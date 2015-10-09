package tum.umlsec.viki.tools.dynaviki.model.promela;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import java_cup.runtime.Symbol;
import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.tools.dynaviki.model.ExceptionBadModel;
import tum.umlsec.viki.tools.dynaviki.model.ExceptionScannerSyntaxError;
import tum.umlsec.viki.tools.dynaviki.model.Intruder;
import tum.umlsec.viki.tools.dynaviki.model.MD_Association;
import tum.umlsec.viki.tools.dynaviki.model.MD_AssociationEnd;
import tum.umlsec.viki.tools.dynaviki.model.MD_Attribute;
import tum.umlsec.viki.tools.dynaviki.model.MD_Class;
import tum.umlsec.viki.tools.dynaviki.model.MD_ComponentInstance;
import tum.umlsec.viki.tools.dynaviki.model.MD_Effect;
import tum.umlsec.viki.tools.dynaviki.model.MD_Guard;
import tum.umlsec.viki.tools.dynaviki.model.MD_Link;
import tum.umlsec.viki.tools.dynaviki.model.MD_LinkEnd;
import tum.umlsec.viki.tools.dynaviki.model.MD_NodeInstance;
import tum.umlsec.viki.tools.dynaviki.model.MD_Object;
import tum.umlsec.viki.tools.dynaviki.model.MD_Operation;
import tum.umlsec.viki.tools.dynaviki.model.MD_State;
import tum.umlsec.viki.tools.dynaviki.model.MD_Stereotype;
import tum.umlsec.viki.tools.dynaviki.model.MD_Transition;
import tum.umlsec.viki.tools.dynaviki.model.MD_Trigger;
import tum.umlsec.viki.tools.dynaviki.model.MD_TriggerParameter;
import tum.umlsec.viki.tools.dynaviki.model.ModelRoot;
import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGEdgeApplyKey;
import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGGraph;
import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGVertex;
import tum.umlsec.viki.tools.dynaviki.model.objectgraph.OGGraph;
import tum.umlsec.viki.tools.dynaviki.model.scanner.Lexer;
import tum.umlsec.viki.tools.dynaviki.model.scanner.SyntaxNode;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_BASE;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_GuardElse;
import tum.umlsec.viki.tools.dynaviki.model.scanner.parser;
import tum.umlsec.viki.tools.dynaviki.model.symtable.S_Literal;
import tum.umlsec.viki.tools.dynaviki.model.symtable.SymbolTable;


/**
 * @author pasha
 */
public class PromelaTranslator {
	public static final String RV_CURRENT_STATE_ID = "rv_currentStateId";
	public static final String RV_CURRENT_EVENT = "rv_currentEvent";
	public static final String RV_CURRENT_MESSAGE = "rv_currentMessage";
	public static final String RV_CURRENT_SIMPLE_VAR = "rv_currentSimpleVar";
	public static final String RV_OBJECT_TERMINATED = "rv_objectTerminated";
	public static final String RV_THIS_ID = "rv_thisId";
	public static final String RV_CHANNEL_IN = "rv_channelIn";
	public static final String RV_CHANNEL_OUT = "rv_channelOut";
	public static final String RV_SENDER = "rv_sender";
	public static final String RV_MAIN_LOOP = "rv_main_loop";
	public static final String RV_END_MAIN_LOOP = "rv_end_main_loop";
	public static final String RV_INTRUDER_TMP_MESSAGE = "rv_tmpMessage";
	public static final String RV_INTRUDER_LAST_READ_MESSAGE = "rv_lastReadMessage";
	public static final String RV_INTRUDER_TMP_VALUE_SENDER = "rv_tmpValueSender";
	public static final String RV_INTRUDER_TMP_VALUE_RECEIVER = "rv_tmpValueReceiver";
	public static final String RV_INTRUDER_TMP_VALUE_EVENT = "rv_tmpValueEvent";
	public static final String RV_INTRUDER_CLASS_NAME = "Intruder";
	
	
	
	
	
	public PromelaTranslator(ModelRoot _modelRoot, ILogOutput _log) {
		modelRoot = _modelRoot;
		log = _log;
	}

	String asString(int _value) {
		return (new Integer(_value)).toString(); 
	}
	

	private String giveClassProcName(String _class) {
		return "Class" + _class;
	}
	
	private String giveInputChannelNameInInit(MD_Object _object) {
		return _object.getClassModel().getName() + "_" + _object.getName() + "_Input";
	}
	
	private String giveOutputChannelNameInInit(MD_Object _object) {
		return _object.getClassModel().getName() + "_" + _object.getName() + "_Output";
	}
	
	private String giveToChannelNameInIntruder(MD_Object _object) {
		return _object.getClassModel().getName() + "_" + _object.getName() + "_to";
	}
	
	private String giveFromChannelNameInIntruder(MD_Object _object) {
		return _object.getClassModel().getName() + "_" + _object.getName() + "_from";
	}
	
	

	private String writeNiceComment(String _commentBody) {
		StringBuffer _sb = new StringBuffer("/********************************************************************************\\\n");
		_sb.append("*  ");
		_sb.append(_commentBody);
		
		int limit = 78 - _commentBody.length();
		if(limit > 0) {
			for(int i = 0; i < limit; i++) {
				_sb.append(" ");
			}
			_sb.append("*");
		}
		_sb.append("\n");
		_sb.append("\\********************************************************************************/\n");
		
		return new String(_sb); 
	}
	
	protected boolean isReservedVariableName(String variableName) {
		final String [] reservedVariableNames = {
							RV_CURRENT_STATE_ID, 
							RV_CURRENT_EVENT, 
							RV_CURRENT_MESSAGE,
							RV_CURRENT_SIMPLE_VAR, 
							RV_OBJECT_TERMINATED,
							RV_THIS_ID, 
							RV_CHANNEL_IN,
							RV_CHANNEL_OUT,
							RV_SENDER,
							RV_MAIN_LOOP,
							RV_END_MAIN_LOOP,
							RV_INTRUDER_TMP_MESSAGE,
							RV_INTRUDER_LAST_READ_MESSAGE,
							RV_INTRUDER_TMP_VALUE_SENDER,
							RV_INTRUDER_TMP_VALUE_RECEIVER,
							RV_INTRUDER_TMP_VALUE_EVENT,
							RV_INTRUDER_CLASS_NAME,
						};
		
		for (int i = 0; i < reservedVariableNames.length; i++) {
			if(reservedVariableNames[i].compareToIgnoreCase(variableName) == 0) {
				return true;
			}
		}
		return false;
	}
	
	private void Parse(MD_Guard _guard) {
		if(_guard == null) {
			return;
		}
		
		boolean _eatThisExpression = false;
		if(_guard.isUmlsecLanguage()) {
			_eatThisExpression = true;
		} else {
			if(translationSettings.eatNonUmlsecExpressions()) {
				_eatThisExpression = true;
				IssueWarning(TranslationSettings.WarningExpressionNotUmlsecParsed, _guard.getExpression());
			} else {
				IssueWarning(TranslationSettings.WarningExpressionNotUmlsecSkipped, _guard.getExpression());
			}
		}
				
		if(_eatThisExpression) {
			Symbol parse_tree = null;
			parser parser_obj = null;
			String _expression = _guard.getExpression();

			try {
				parser_obj = new parser(new Lexer(new StringReader(_expression)));
				parse_tree = parser_obj.parse();
				_guard.addExpressionTree((SyntaxNode)(parse_tree.value));
			} catch (ExceptionScannerSyntaxError ex) {
				log.appendLogLn("Error in expression:\n" + _expression);
			} catch (Exception x) {
				throw new ExceptionProgrammLogicError("Exception:: " + x.toString());
			}
		}
	}

	private void Parse(MD_Effect _effect) {
		if(_effect == null) {
			return ;
		}
		
		boolean _eatThisExpression = false;
		if(_effect.isUmlsecLanguage()) {
			_eatThisExpression = true;
		} else {
			if(translationSettings.eatNonUmlsecExpressions()) {
				_eatThisExpression = true;
				IssueWarning(TranslationSettings.WarningExpressionNotUmlsecParsed, _effect.getExpression());
			} else {
				IssueWarning(TranslationSettings.WarningExpressionNotUmlsecSkipped, _effect.getExpression());
			}
		}				
		if(_eatThisExpression) {

			Symbol parse_tree = null;
			parser parser_obj = null;
			String _expression = _effect.getExpression();

			try {
				parser_obj = new parser(new Lexer(new StringReader(_expression)));
				parse_tree = parser_obj.parse();
				
				_effect.addExpressionTree((SyntaxNode)(parse_tree.value));
			} catch (ExceptionScannerSyntaxError ex) {
				log.appendLogLn("Error in expression:\n" + _expression);
			} catch (Exception x) {
				throw new ExceptionProgrammLogicError("Exception:: " + x.toString());
			}
				
		}
	}


/**
 * parses initial value of the attribute
 * 
 * @param _attribute
 */
	private void Parse(MD_Attribute _attribute) {
		if(!_attribute.hasInitialValue()) {
			return;
		}
			
		boolean _eatThisExpression = false;
		if(_attribute.isInitialValueUmlsecLanguage()) {
			_eatThisExpression = true;
		} else {
			if(translationSettings.eatNonUmlsecInitialValues()) {
				_eatThisExpression = true;
				IssueWarning(TranslationSettings.WarningInitialValueNotUmlsecParsed, _attribute.getInitialValueExpressionRaw());
			} else {
				IssueWarning(TranslationSettings.WarningInitialValueNotUmlsecSkipped, _attribute.getInitialValueExpressionRaw());
			}
		}				
				
		if(_eatThisExpression) {
			Symbol parse_tree = null;
			parser parser_obj = null;
			String _expression = _attribute.getInitialValueExpressionRaw();

			try {
				parser_obj = new parser(new Lexer(new StringReader(_expression)));
				parse_tree = parser_obj.parse();
				
				_attribute.addExpressionTree((SyntaxNode)(parse_tree.value));
			} catch (ExceptionScannerSyntaxError ex) {
				log.appendLogLn("Error in expression:\n" + _expression);
			} catch (Exception x) {
				throw new ExceptionProgrammLogicError("Exception:: " + x.toString());
			}
		}
	}

	
	public void pass01_parseExpressions() {
		modelRoot.prepareGeneration();
		
// parse all expressions in the model
		 for (Iterator _iter = modelRoot.getTransitionsAll().iterator(); _iter.hasNext();) {
			 MD_Transition _transition = (MD_Transition) _iter.next();
			 
			 String _ttt = _transition.getName();
			 
			 Parse(_transition.getGuardModel());
			 Parse(_transition.getEffectModel());			
		 }		
				
		 for(Iterator _iter = modelRoot.getClassesAll().iterator(); _iter.hasNext(); ) {
			 MD_Class _class = (MD_Class) _iter.next();
		
			 for (Iterator _it01 = _class.getAttributes().iterator(); _it01.hasNext();) {
				 Parse((MD_Attribute) _it01.next());
			 }			
		 }
	}

	public void pass02_buildTypeGraph() {
		VisitorInitialiseAll _visitor01 = new VisitorInitialiseAll();
		VisitorBuildTypeGraph _visitor02 = new VisitorBuildTypeGraph(dataFormatGraph);

//		go through all expressions and build the Data Format Graph
		 for (Iterator _iter = modelRoot.getTransitionsAll().iterator(); _iter.hasNext();) {
			MD_Transition _transition = (MD_Transition) _iter.next();
			if(_transition.getGuardProcessed()) {
				_transition.getGuardModel().getExpressionTree().traverseBottomUp(_visitor01);
				_transition.getGuardModel().getExpressionTree().traverseBottomUp(_visitor02);
			}
			if(_transition.getEffectProcessed()) {
				_transition.getEffectModel().getExpressionTree().traverseBottomUp(_visitor01);
				_transition.getEffectModel().getExpressionTree().traverseBottomUp(_visitor02);
			}
	 	}		
				
//		now we go through all intial values of the variables and do the same
	 	for(Iterator _iter = modelRoot.getClassesAll().iterator(); _iter.hasNext(); ) {
		 	MD_Class _class = (MD_Class) _iter.next();
		 	for (Iterator _it01 = _class.getAttributes().iterator(); _it01.hasNext();) {
				MD_Attribute _attribute = (MD_Attribute) _it01.next();
				if(_attribute.getInitialValueExpressionTree() != null) {
					_attribute.getInitialValueExpressionTree().traverseBottomUp(_visitor01);
					_attribute.getInitialValueExpressionTree().traverseBottomUp(_visitor02);
				}
		 	}			
	 	}
	 	
		modelRoot.visualiseDataFormatGraph(dataFormatGraph);			
		buildConstantTable();
	}
	
	public void pass03_detectVariables() {
		VisitorInitialiseAndMatchVariables _visitor;

//		go through all guards and effects and recognise their vars
		 for (Iterator _iter = modelRoot.getTransitionsAll().iterator(); _iter.hasNext();) {
			MD_Transition _transition = (MD_Transition) _iter.next();
			_visitor = new VisitorInitialiseAndMatchVariables(_transition.getStateMachineModel().getClassModel(), allLiterals, true, true, true);
			 
			if(_transition.getGuardProcessed()) {
				_transition.getGuardModel().getExpressionTree().traverseBottomUp(_visitor);
			}
			if(_transition.getEffectProcessed()) {
			   _transition.getEffectModel().getExpressionTree().traverseBottomUp(_visitor);
			}
		 }		
				
//		now we go through all intial values of the variables and do the same
		 for(Iterator _iter = modelRoot.getClassesAll().iterator(); _iter.hasNext(); ) {
			MD_Class _class = (MD_Class) _iter.next();
			_visitor = new VisitorInitialiseAndMatchVariables(_class, allLiterals, true, false, true);
			 for (Iterator _it01 = _class.getAttributes().iterator(); _it01.hasNext();) {
			 	MD_Attribute _attribute = (MD_Attribute) _it01.next();
				if(_attribute.getInitialValueExpressionTree() != null) {
					_attribute.getInitialValueExpressionTree().traverseBottomUp(_visitor);
				}
			 }			
		 }
		modelRoot.visualiseDataFormatGraph(dataFormatGraph);
	}
	
	public void pass04_compileInitialValue() {
		VisitorCompileInitialValue _visitor = new VisitorCompileInitialValue(dataFormatGraph, allLiterals);
		
		for(Iterator _iter = modelRoot.getClassesAll().iterator(); _iter.hasNext(); ) {
		   MD_Class _class = (MD_Class) _iter.next();
			for (Iterator _it01 = _class.getAttributes().iterator(); _it01.hasNext();) {
			   MD_Attribute _attribute = (MD_Attribute) _it01.next();
			   if(_attribute.getInitialValueExpressionTree() != null) {
				   _attribute.getInitialValueExpressionTree().traverseBottomUp(_visitor);
			   }
			}			
		}
	}
	
	
	
	public void pass05_findWritableVars() {
		VisitorFindWritableVariables _visitor;
		
// go through all transitions		
		 for (Iterator _iter = modelRoot.getTransitionsAll().iterator(); _iter.hasNext();) {
			MD_Transition _transition = (MD_Transition) _iter.next();
			
// vars assigned to in effects are writable
			_visitor = new VisitorFindWritableVariables(_transition.getStateMachineModel().getClassModel());
			if(_transition.getEffectProcessed()) {
			   _transition.getEffectModel().getExpressionTree().traverseBottomUp(_visitor);
			}
			
// and all variables which accept values in triggers are writable
			MD_Trigger _trigger = _transition.getTrigger();
			if(_trigger != null) {
				for (Iterator _it01 = _trigger.getParameters().iterator(); _it01.hasNext();) {
					MD_TriggerParameter _parameter = (MD_TriggerParameter) _it01.next();
					MD_Attribute _attribute = _transition.getStateMachineModel().getClassModel().getAttribute(_parameter.getName());
					if(_attribute != null) {
						_attribute.setConstant(false);
					}
				}
			}
		 }
	}
	
	
	public void pass06_findSimpleVariables() {
// first we go through all triggers - all vars assigned to are complex
		for (Iterator _iter = modelRoot.getTransitionsAll().iterator(); _iter.hasNext();) {
	  		MD_Transition _transition = (MD_Transition) _iter.next();
	  		MD_Trigger _trigger = _transition.getTrigger();
	  		if(_trigger != null) {
				for (Iterator _it01 = _trigger.getParameters().iterator(); _it01.hasNext();) {
					MD_TriggerParameter _parameter = (MD_TriggerParameter) _it01.next();
					MD_Attribute _attribute = _transition.getStateMachineModel().getClassModel().getAttribute(_parameter.getName());
					if(_attribute != null) {
						_attribute.setVariableComplexity(EnumExpressionComplexity.Complex);
					}
				}
	  		}
		}
		
// and then we loop through all expressions until there are no more changes
// we dont look at guards, only at effects
		boolean _changesMade = true;
		while(_changesMade) {
			_changesMade = false;
			for (Iterator _iter = modelRoot.getTransitionsAll().iterator(); _iter.hasNext();) {
			   	MD_Transition _transition = (MD_Transition) _iter.next();
			   	if(_transition.getEffectProcessed()) {
					VisitorFindSimpleVariables _visitor =  new VisitorFindSimpleVariables(_transition.getStateMachineModel().getClassModel());
					_transition.getEffectModel().getExpressionTree().traverseBottomUp(_visitor);
					if(_visitor.isChagesMade()) {
						_changesMade = true;		
					}
			   	}
		   	}
		}
	}
	

	public void pass07_compileEffects() {
		for (Iterator _iter = modelRoot.getTransitionsAll().iterator(); _iter.hasNext();) {
			MD_Transition _transition = (MD_Transition) _iter.next();
			if(_transition.getEffectProcessed()) {
				VisitorCompileEffect _visitor = new VisitorCompileEffect(modelRoot, _transition.getStateMachineModel().getClassModel());
				_transition.getEffectModel().getExpressionTree().traverseBottomUp(_visitor);
			}			
		}
	}

	public void pass08_compileTriggers() {
	}

	public void pass09_compileGuards() {
		for (Iterator _iter = modelRoot.getTransitionsAll().iterator(); _iter.hasNext();) {
			MD_Transition _transition = (MD_Transition) _iter.next();
			if(_transition.getGuardProcessed()) {
				VisitorCompileGuard _visitor = new VisitorCompileGuard(/*modelRoot, _transition.getStateMachineModel().getClassModel()*/);
				_visitor.initialise();
				_transition.getGuardModel().getExpressionTree().traverseBottomUp(_visitor);
			}
		}
	}

	public void pass10_buildObjectGraph() {
//		objectGraph = new OGGraph(modelRoot);
//		
//		for (Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
//			MD_Object _object = (MD_Object)_iter.next();
//			
//			objectGraph.addVertex(_object);
//		}
	}
	
	public void pass11_mapAssociationsToLinks() {
		for (Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			MD_Object _object = (MD_Object)_iter.next();
			String _objectName = _object.getName();
			MD_Class _class = _object.getClassModel();
			
			for (Iterator _iter01 = _class.getAssociationEnds().iterator(); _iter01.hasNext();) {
				MD_AssociationEnd _associationEnd = (MD_AssociationEnd)_iter01.next();
				MD_Association _association = _associationEnd.getAssociationModel();
				MD_AssociationEnd _anotherAssociationEnd = _association.getAnotherEnd(_associationEnd);
				
				
				String _anotherEndName = _anotherAssociationEnd.getName();
				if(_anotherEndName.length() == 0) {
					continue;
				}
				
// find appropriate object among all linked to this one
				boolean _matched = false; 
				for (Iterator _iter02 = _object.getLinkEnds().iterator(); _iter02.hasNext();) {
					
					
					Object x = _iter02.next();
					
					MD_LinkEnd _linkEnd = (MD_LinkEnd)x;
					MD_Link _link = _linkEnd.getLinkModel();
					MD_LinkEnd _anotherLinkEnd = _link.getAnotherEnd(_linkEnd);
					
					MD_Class _classAcrossTheAssociation = _anotherAssociationEnd.getAttachedClassModel();
					MD_Class _classAcrossTheLink = _anotherLinkEnd.getAttachedObjectModel().getClassModel();
					
					String _name1 = _classAcrossTheAssociation.getName();
					String _name2 = _classAcrossTheLink.getName();
					
					if(_classAcrossTheAssociation == _classAcrossTheLink) {
						if(_anotherLinkEnd.isMatchedToAssociation() == false) {
							_anotherLinkEnd.setMatchedToAssociation(true);
							_matched = true;
							_object.mapAssociationEndToLinkEnd(_anotherAssociationEnd, _anotherLinkEnd);
							break;
						}
					}
				}
				
				if(_matched == false) {
					throw new ExceptionBadModel("Can't map an association to a link");
				}				
			}
		}
	}
	
	public void pass12_mapLinkLogicalToPhysical() {
		Vector _physicalLinkStereotypes = new Vector();
		for (Iterator _iter = modelRoot.getLinksAll().iterator(); _iter.hasNext();) {
			MD_Link _logicalLink = (MD_Link)_iter.next();
			if(_logicalLink.getLinkEnd1().getAttachedObjectType() != _logicalLink.getLinkEnd2().getAttachedObjectType()) {
				throw new ExceptionBadModel("Link connecting objects of different types");
			}
			if(_logicalLink.getLinkEnd1().getAttachedObjectType() != MD_LinkEnd.LET_OBJECTS) {
				continue;
			}
			MD_Object _object1 = _logicalLink.getLinkEnd1().getAttachedObjectModel();
			MD_Object _object2 = _logicalLink.getLinkEnd2().getAttachedObjectModel();
			
			MD_ComponentInstance _componentInstance1 = _object1.getComponentInstanceModel();
			MD_ComponentInstance _componentInstance2 = _object2.getComponentInstanceModel();
			
			MD_NodeInstance _nodeInstance1 = _componentInstance1.getNodeInstanceModel();
			MD_NodeInstance _nodeInstance2 = _componentInstance2.getNodeInstanceModel();
			
			if(_nodeInstance1 == _nodeInstance2) {
				continue;
			}
			
// find the correspondent physical link
			MD_Link _mappedPhysicalLink = null;
			for (Iterator _iter01 = modelRoot.getLinksAll().iterator(); _iter01.hasNext(); ) {
				MD_Link _physicalLink = (MD_Link)_iter01.next();
				if(_physicalLink.getLinkEnd1().getAttachedObjectType() != MD_LinkEnd.LET_NODEINSTANCES) {
					continue;
				}
				
				if((_nodeInstance1 == _physicalLink.getLinkEnd1().getAttachedNodeInstanceModel() && 
					_nodeInstance2 == _physicalLink.getLinkEnd2().getAttachedNodeInstanceModel()) ||
				   (_nodeInstance1 == _physicalLink.getLinkEnd2().getAttachedNodeInstanceModel() && 
					_nodeInstance2 == _physicalLink.getLinkEnd1().getAttachedNodeInstanceModel())
				) {
					_mappedPhysicalLink = _physicalLink;
					break;
				}
			}
			if(_mappedPhysicalLink == null) {
				throw new ExceptionBadModel("Cannot map logical link between objects to a link between node instances");
			}
								
// we collect all the stereotypes which would describe the media
			for (Iterator _iter01 = _mappedPhysicalLink.getStereotypes().iterator(); _iter01.hasNext();) {
				MD_Stereotype _stereotype = (MD_Stereotype)_iter01.next();
				modelRoot.getIntruder().processPhysicalLinkStereotype(_stereotype);
			}
		}
	}
	
	public void pass13() {
	}
	
	public void pass14_searchSecretAttribute() {
		secretAttribute = null;
		for (Iterator _iter = modelRoot.getClassesAll().iterator(); _iter.hasNext();) {
			MD_Class _class = (MD_Class)_iter.next();
			for (Iterator _iter01 = _class.getAttributes().iterator(); _iter01.hasNext();) {
				MD_Attribute _attribute = (MD_Attribute)_iter01.next();
				if(_attribute.isStereotypedAsSecrecy()) {
					secretAttribute = _attribute;
					break;
				}
			}
			if(secretAttribute != null) {
				break;
			}
		}
		if(secretAttribute == null) {
			throw new ExceptionBadModel("No secrecy attribute found");
		}
	}

	public void pass15() {
		
	}

	public void pass16() {
		modelRoot.getIntruder().completeInitialisation();
		buildCode();
	}

	
	
	
	
	
	
	
	
	
	private void buildCode() {
		StringBuffer code = new StringBuffer(); 
		
// generate definitions for data types
		code.append(writeNiceComment("data type definition"));
		code.append("#define TYPE_STATEID byte\n");				// TODO customise from settings
		code.append("#define TYPE_MSGTYPE byte\n");				// TODO customise from settings
		code.append("#define TYPE_DATAVAL byte\n");				// TODO customise from settings
		code.append("#define TYPE_EVENT byte\n");				// TODO customise from settings
		code.append("\n");
		code.append("\n");
			
			
			
// message types			
   		code.append(writeNiceComment("message types"));
   		code.append("#define MT_GARBAGE 1\n");
   		
   		maxMessageParameterCount = 1;
   		for(Iterator _it = dataFormatGraph.getAllVertexes().iterator(); _it.hasNext(); ) {
   			DFGVertex _vertex = (DFGVertex)_it.next();

			code.append("#define ");
   			code.append(_vertex.getAsMessageType());
   			code.append(" ");
   			code.append(asString(_vertex.getMessageTypeId())); 
   			code.append("\n");
   			
   			if(_vertex.getParameterCount() > maxMessageParameterCount) {
				maxMessageParameterCount = _vertex.getParameterCount();
			}
   		}
   		code.append("\ntypedef MSG {\n");
		code.append("\tTYPE_MSGTYPE messageType;\n");
		for(int i = 1; i <= maxMessageParameterCount; i ++) {
			code.append("\tTYPE_DATAVAL param" + asString(i) + ";\n");
		}
   		code.append("}\n\n;");
   		
   		maxTweakMessageCount = maxMessageParameterCount - 1;
   		   		 


////////////////////////
//		we collect a vector of all Objects IDs because we need it all the time
		Vector _objectIds = getObjectsIds();  

////////////////////////
//	data values
		code.append(allLiteralsDefinition);

//////////////////////////////////		
// macros	
		code.append(writeNiceComment("macros"));
		StringBuffer _closingBraces = new StringBuffer();

// NonceOf
		if(dataFormatGraph.isUsesNonceOf()) {
//			code.append("inline GetNonceOf(x) {\n");
//			code.append("}\n");
			
			_closingBraces.setLength(0);
			code.append("#define NonceOf(x)\t");
			for (Iterator _iter = _objectIds.iterator(); _iter.hasNext();) {
				String _objId = (String) _iter.next();
				code.append("(x == DV_" + _objId + "_id -> DV_" + _objId + "_nonce:\t\\\n\t\t\t\t");
				_closingBraces.append(")");
			}
			code.append("DV_GARBAGE");
			code.append(_closingBraces);
			code.append("\n");
			
			
		}

// PublicKeyOf, SecretKeyOf
		if(dataFormatGraph.isUsesPublicKeyOf() || dataFormatGraph.isUsesSecretKeyOf()) {
			_closingBraces.setLength(0);
			code.append("#define PublicKeyOf(x)\t");
	   		for (Iterator _iter = _objectIds.iterator(); _iter.hasNext();) {
		   		String _objId = (String) _iter.next();
		   		code.append("(x == DV_" + _objId + "_id -> DV_" + _objId + "_pk:\t\\\n\t\t\t\t");
			   	_closingBraces.append(")");
			}
			code.append("DV_GARBAGE");
			code.append(_closingBraces);
			code.append("\n");

			_closingBraces.setLength(0);
			code.append("#define SecretKeyOf(x)\t");
			for (Iterator _iter = _objectIds.iterator(); _iter.hasNext();) {
				String _objId = (String) _iter.next();
				code.append("(x == DV_" + _objId + "_id -> DV_" + _objId + "_sk:\t\\\n\t\t\t\t");
				_closingBraces.append(")");
			}
			code.append("DV_GARBAGE");
			code.append(_closingBraces);
			code.append("\n");
   		}

//		SymmetricKeyOf
			 if(dataFormatGraph.isUsesNonceOf()) {
				 _closingBraces.setLength(0);
				 code.append("#define SymmetricKeyOf(x)\t");
				 for (Iterator _iter = _objectIds.iterator(); _iter.hasNext();) {
					 String _objId = (String) _iter.next();
					 code.append("(x == DV_" + _objId + "_id -> DV_" + _objId + "_k:\t\\\n\t\t\t\t");
					 _closingBraces.append(")");
				 }
				 code.append("DV_GARBAGE");
				 code.append(_closingBraces);
				 code.append("\n");
			 }

// 		InverseKey
		_closingBraces.setLength(0);
		code.append("#define InverseKey(x)\t\t");
		for (Iterator _iter = _objectIds.iterator(); _iter.hasNext();) {
			String _objId = (String) _iter.next();
			if(dataFormatGraph.isUsesPublicKeyOf() || dataFormatGraph.isUsesSecretKeyOf()) {
				code.append("(x == DV_" + _objId + "_pk -> DV_" + _objId + "_sk:\t\\\n\t\t\t\t");
				_closingBraces.append(")");
				code.append("(x == DV_" + _objId + "_sk -> DV_" + _objId + "_pk:\t\\\n\t\t\t\t");
				_closingBraces.append(")");
			}
			if(dataFormatGraph.isUsesSymmetricKeyOf()) {
				code.append("(x == DV_" + _objId + "_k -> DV_" + _objId + "_k:\t\\\n\t\t\t\t");
				_closingBraces.append(")");
			}
		}
		code.append("DV_GARBAGE");
		code.append(_closingBraces);
		code.append("\n");

		code.append("\n");
		code.append("\n");


//////////////////////////////////		
//		 helper functions	
		code.append(writeNiceComment("helper functions"));
	
//		AssignComplexToComplex
			code.append("inline AssignComplexToComplex(target, source) { atomic {\n");
			code.append("\ttarget.messageType = source.messageType;\n");
			for(int i = 1; i <= maxMessageParameterCount; i ++) {
				code.append("\ttarget.param" + asString(i) + " = source.param" + asString(i) + ";\n");
			}
			code.append("}}\n");
			code.append("\n");
		
//		AssignComplexToSimple
			code.append("inline AssignComplexToSimple(target, source) { atomic {\n");
			code.append("if\n");
				code.append("\t:: source.messageType == MT_v -> target = source.param1;\n");
				code.append("\t:: else -> target = DV_GARBAGE;\n");
			code.append("fi;\n");
			code.append("}}\n");
			code.append("\n");
		
//		AssignSimpleToComplex
			code.append("inline AssignSimpleToComplex(target, source) { atomic {\n");
				code.append("target.messageType = MT_v;\n");
				code.append("target.param1 = source;\n");
			code.append("}}\n");
			code.append("\n");
		
		
		
		
		
// 	ApplyKey		
		code.append("inline ApplyKey(message, key) { atomic {\n");
		code.append("if\n");
			code.append("\t:: message.messageType == MT_GARBAGE -> ;\n");
			
			for(Iterator _it = dataFormatGraph.getAllVertexes().iterator(); _it.hasNext(); ) {
				DFGVertex _vertex = (DFGVertex) _it.next();
				
				DFGEdgeApplyKey _edgeIncomingApplyKey = _vertex.GetIncomingApplyKeyEdge();
				DFGEdgeApplyKey _edgeOutgoingApplyKey = _vertex.GetOutgoingApplyKeyEdge();
				
				
				code.append("\t:: message.messageType == " + _vertex.getAsMessageType() + " -> ");
				if(_edgeIncomingApplyKey == null) {
					if(_edgeOutgoingApplyKey == null) {
						code.append("message.messageType = MT_GARBAGE;\n");
					} else {
						code.append("message.messageType = " + _edgeOutgoingApplyKey.getTargetVertex().getAsMessageType() + "; ");
						code.append("message.param" + _edgeOutgoingApplyKey.getTargetVertex().getParameterCount() + " = key;\n");
					}
				} else {
					code.append("\n\t\tif\n");
						code.append("\t\t\t:: message.param" + _vertex.getParameterCount() + " == InverseKey(key) -> message.messageType = " + _edgeIncomingApplyKey.getSourceVertex().getAsMessageType() + ";\n");
						if(_edgeOutgoingApplyKey == null) {
							code.append("\t\t\t:: else -> message.messageType = MT_GARBAGE;\n");					
						} else {
							code.append("\t\t\t:: else -> message.messageType = " + _edgeOutgoingApplyKey.getTargetVertex().getAsMessageType() + "; ");
							code.append("message.param" + _edgeOutgoingApplyKey.getTargetVertex().getParameterCount() + " = key;\n");
						}
					
					code.append("\t\tfi;\n");
				}
			}
			// TODO we probably do not need the next line
			code.append("\t:: else -> assert(0);\n");
		code.append("fi;\n");
		code.append("}}\n");
		code.append("\n");
		
		
// ReadMessage	
		code.append("inline ReadMessage(channel, message) { atomic {\n");
		code.append("channel? message.messageType;\n");
		code.append("if\n");
			code.append("\t:: (message.messageType == MT_GARBAGE) -> ;\n");
			
			for(Iterator _it = dataFormatGraph.getAllVertexes().iterator(); _it.hasNext(); ) {
				DFGVertex _vertex = (DFGVertex) _it.next();
				
				code.append("\t:: (message.messageType == " + _vertex.getAsMessageType() + ") -> ");
				for(int i = 1; i <= _vertex.getParameterCount(); i++) {
					code.append("channel? message.param" + i + "; ");
				}
				code.append("\n");				
			}

			code.append("\t:: else -> assert(0);\n");
		code.append("fi;\n");
		code.append("}}\n");
		code.append("\n");



// SendMessage	
		code.append("inline SendMessage(channel, message) { atomic {\n");
		code.append("channel! message.messageType;\n");
		code.append("if\n");
			code.append("\t:: (message.messageType == MT_GARBAGE) -> ;\n");
			
			for(Iterator _it = dataFormatGraph.getAllVertexes().iterator(); _it.hasNext(); ) {
				DFGVertex _vertex = (DFGVertex) _it.next();
				
				code.append("\t:: (message.messageType == " + _vertex.getAsMessageType() + ") -> ");
				for(int i = 1; i <= _vertex.getParameterCount(); i++) {
					code.append("channel! message.param" + i + "; ");
				}
				code.append("\n");				
			}
			code.append("\t:: else -> assert(0);\n");
		code.append("fi;\n");
		code.append("}}\n");
		code.append("\n");
		code.append("\n");



//////////////////////////////////		
//			   event IDs	
		code.append(writeNiceComment("event IDs"));
		int _eventCounter = 1;
		for (Iterator iter = modelRoot.getClassesAll().iterator(); iter.hasNext();) {
			MD_Class _class = (MD_Class)iter.next();
			for (Iterator iter01 = _class.getOperations().iterator(); iter01.hasNext();) {
				MD_Operation _operation = (MD_Operation)iter01.next();
				code.append("#define " + _operation.getPromelaEventName() + " " + _eventCounter + "\n");
				allEventIDs.add(_operation.getPromelaEventName());
				_eventCounter ++;
			}
		}
		code.append("\n");
		code.append("\n");

		
// generate promela code for all classes which have statecharts		
		for (Iterator _iter = modelRoot.getClassesAll().iterator(); _iter.hasNext();) {
			MD_Class _class = (MD_Class) _iter.next();
			if(_class.getStateMachine() == null) {
				continue;
			}
			code.append(eatClass(_class));
		}



///////////////////////////////////////////////
//		
//		code.append(writeNiceComment("Intruder knowledge"));
		for (Iterator _iter = allLiterals.getLiterals().iterator(); _iter.hasNext();) {
			S_Literal _literal = (S_Literal)_iter.next();
			if(_literal.isKnownToIntruder() == false) {
				code.append("bit known_" + _literal.getName() + " = false;\n");
			}
		}
		code.append("\n");
		


///////////////////////////////////////////////
//		
		code.append(writeNiceComment("helper functions for Intruder"));
		
//		SendSomeNodeId
		code.append("inline SendSomeNodeId(chOut) { atomic {\n");
		code.append("if\n");
		for (Iterator _iter = _objectIds.iterator(); _iter.hasNext();) {
			String _objId = (String) _iter.next();
			code.append("::\tchOut!DV_" + _objId + "_id;\n");
		}
		code.append("fi;\n");
		code.append("}}\n");
		code.append("\n");
		code.append("\n");
		

//		GetKnownValue
			 code.append("inline GetKnownValue(x) { atomic {\n");
			 code.append("if\n");
		
			 for (Iterator _iter = allLiterals.getLiterals().iterator(); _iter.hasNext();) {
				 S_Literal _literal = (S_Literal)_iter.next();
				 code.append("::\t");
				 if(_literal.isKnownToIntruder() == false) {
					 code.append("known_" + _literal.getName() + " == true -> ");
				 }
				 code.append("x = " + _literal.getName() + ";\n");
			 }
			 code.append("fi;\n");
			 code.append("}}\n");
			 code.append("\n");
			 code.append("\n");
			 

//		LearnValue
			 code.append("inline LearnValue(x) { atomic {\n");
			 code.append("if\n");
		
			 for (Iterator _iter = allLiterals.getLiterals().iterator(); _iter.hasNext();) {
				 S_Literal _literal = (S_Literal)_iter.next();
				 if(_literal.isKnownToIntruder() == false) {
					 code.append("::\tx == " + _literal.getName() + " -> known_" + _literal.getName() + " = true;\n");
				 }
			 }
		
			 code.append("::\telse -> ;\n");
			 code.append("fi;\n");
			 code.append("}}\n");
			 code.append("\n");
			 code.append("\n");
		
		
		
//		TweakMessage
		code.append("inline TweakMessage(x) { atomic {\n");
		code.append("if\n");
		
		for (Iterator _iter = allLiterals.getLiterals().iterator(); _iter.hasNext();) {
			S_Literal _literal = (S_Literal)_iter.next();
			if(_literal.isKey()) {
				code.append("::\t");
				if(_literal.isKnownToIntruder() == false) {
					code.append("known_" + _literal.getName() + " == true -> ");
				}
				code.append("ApplyKey(x, " + _literal.getName() + ");\n");
			}
		}
		
		code.append("\n");
		code.append("::\ttrue -> ;\n");
		code.append("fi;\n");
		code.append("}}\n");
		code.append("\n");
		code.append("\n");




//		SendSomeMessage
		code.append("inline SendSomeMessage(cho) { atomic {\n");
		code.append("/* send event ID */\n");		
		code.append("if\n");
		for (Iterator _iter = allEventIDs.iterator(); _iter.hasNext();) {
			String _eventId = (String)_iter.next();
			code.append("::\t cho!" + _eventId + ";\n");			
		}
		code.append("fi;\n");
		code.append("\n");
		
		code.append("/* either start the message 'from scratch' or use existing as starting point */\n");	
		code.append("if\n");
		for(int _cnt = 0; _cnt < modelRoot.getIntruder().getSavedMessageCount(); _cnt ++) {
			String _storedMsg = RV_INTRUDER_TMP_MESSAGE + "[" + _cnt + "]";
			code.append("::\t" + _storedMsg + ".messageType != MT_GARBAGE ->\n");
			code.append("\t" + RV_INTRUDER_LAST_READ_MESSAGE + ".messageType = " + _storedMsg + ".messageType;\n");			
			for(int _cnt01 = 1; _cnt01 <= maxMessageParameterCount; _cnt01 ++) {
				code.append("\t" + RV_INTRUDER_LAST_READ_MESSAGE + ".param" + _cnt01 + " = " + _storedMsg + ".param" + _cnt01 + ";\n");
			}
		}
		code.append("::\t" + RV_INTRUDER_LAST_READ_MESSAGE + ".messageType = MT_v;\n");
		code.append("\tGetKnownValue(" + RV_INTRUDER_LAST_READ_MESSAGE + ".param1);\n");
		code.append("fi;\n");
		code.append("\n");

		for(int _cnt = 0; _cnt < maxTweakMessageCount; _cnt ++) {
			code.append("TweakMessage(" + RV_INTRUDER_LAST_READ_MESSAGE + ");\n");
		} 
		code.append("\n");

		code.append("SendMessage(cho, " + RV_INTRUDER_LAST_READ_MESSAGE + ");\n");		

		code.append("}}\n");
		code.append("\n");
		code.append("\n");


//		AnalyseMessage
		code.append("inline AnalyseMessage(message, storage) { atomic {\n");
		
		code.append("if\n");
		for(int _cnt = 0; _cnt < modelRoot.getIntruder().getSavedMessageCount(); _cnt ++) {
			code.append("::\tstorage[" + _cnt + "].messageType = message.messageType;\n");
			for(int _cnt01 = 1; _cnt01 <= maxMessageParameterCount; _cnt01 ++) {
				code.append("\tstorage[" + _cnt + "].param" + _cnt01 + " = message.param" + _cnt01 + ";\n");
			}
		}
		code.append("::\telse -> ;\n");
		code.append("fi;\n");
		
		for(int _cnt = 0; _cnt < maxTweakMessageCount; _cnt ++) {
			code.append("TweakMessage(message);\n");
		} 

		code.append("if\n");
		code.append("::\tmessage.messageType == MT_v -> LearnValue(message.param1);\n");
		code.append("::\ttrue -> ;\n");
		code.append("fi;\n");
		
		code.append("}}\n");
		code.append("\n");
		code.append("\n");
		


// generate intruder
		code.append(generateIntruder());
		
// build main procedure

		code.append("\n");
   		code.append("init {\n");
   		
// build i/o channels for each class
		for (Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			MD_Object _object = (MD_Object)_iter.next();
			
			// TODO channel size customizable
			code.append("chan " + giveInputChannelNameInInit(_object) + " = [1] of {int};\n");
			code.append("chan " + giveOutputChannelNameInInit(_object) + " = [1] of {int};\n");
		}

		code.append("\n"); 
		code.append("\n"); 

//		instantiate objects
		for (Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			MD_Object _object = (MD_Object)_iter.next();
			

			code.append("run " + giveClassProcName(_object.getClassModel().getName()) + "(DV_" + _object.getName() + "_id, ");
			code.append(giveInputChannelNameInInit(_object) + ", ");
			code.append(giveOutputChannelNameInInit(_object));
			

// add IDs to referenced objects
			for(Iterator _iter01 = _object.getClassModel().getAssociationEnds().iterator(); _iter01.hasNext();) {
				MD_AssociationEnd _associationEnd = (MD_AssociationEnd)_iter01.next();
	   			MD_Association _association = _associationEnd.getAssociationModel();
	   			MD_AssociationEnd _anotherAssociationEnd = _association.getAnotherEnd(_associationEnd);
	   			
				if(_anotherAssociationEnd.getName().length() == 0) {
					continue;
	   			}
	   			
	   			MD_LinkEnd _anotherLinkEnd =  _object.getMappedLinkEnd(_anotherAssociationEnd);	   			
				MD_Object _anotherObject =  _anotherLinkEnd.getAttachedObjectModel();
				String _anotherObjectName = _anotherObject.getName(); 
				
				code.append(", DV_" + _anotherObjectName + "_id");			
			}
			code.append(");\n");
		}
		
		code.append("\n");
		
//		instantiate intruder
		code.append("run Intruder(DV_Intruder_id");
		for (Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			MD_Object _object = (MD_Object)_iter.next();
			code.append(", " + giveInputChannelNameInInit(_object) + ", " + giveOutputChannelNameInInit(_object));
		}
		code.append(");\n");



		code.append("\n");
		
		code.append("}\n\n");


//////////////////////////////////////////////////////////
//
		String _val = secretAttribute.getInitialValueExpressionCompiled();
		if(_val == null || _val.length() == 0) {
			throw new ExceptionBadModel("Secrecy attribute not initialised.");
		}
		if(secretAttribute.isConstant() == false) {
			throw new ExceptionBadModel("Secrecy attribute is not constant");
		}

		code.append(writeNiceComment("Never claim"));
		code.append("never {\n");
		code.append("T0_init:\n");
		code.append("\tif\n");
		code.append("\t:: known_" + _val + " == true -> goto accept_all;\n");
		code.append("\t:: (1) -> goto T0_init;\n");
		code.append("\tfi;\n");
			
		code.append("accept_all:\n");
		code.append("\tskip\n");
		code.append("}\n");

		modelRoot.setPromelaCode(code.toString());
	}



	private Vector getObjectsIds() {
		Vector _ret = new Vector(); 
		for (Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			_ret.add(((MD_Object)_iter.next()).getName());
		}
		_ret.add(RV_INTRUDER_CLASS_NAME);
		return _ret;
	}



	private void buildConstantTable() {
		StringBuffer _sb = new StringBuffer();

		_sb.append(writeNiceComment("data values"));
		_sb.append("#define DV_GARBAGE 0\n");
		
		int _runningDataValue = 1;
		Vector _objectIds = getObjectsIds(); 
		
// for each object we define its ID and then all constructs necessary (keys, nonce)
		for(Iterator _iter = _objectIds.iterator(); _iter.hasNext();) {
			String _objId = (String) _iter.next();
			String _dataValue;
		
			_dataValue = "DV_" + _objId + "_id";
			_sb.append("#define " + _dataValue + " " + asString(_runningDataValue ++) + "\n");
			allLiterals.addLiteral(_dataValue, true, false);
			
			if(dataFormatGraph.isUsesNonceOf()) {
				_dataValue = "DV_" + _objId + "_nonce";
				_sb.append("#define " + _dataValue + " " + asString(_runningDataValue ++) + "\n");
				if(_objId.compareTo(RV_INTRUDER_CLASS_NAME) == 0) {
					allLiterals.addLiteral(_dataValue, true, false);
				} else {
					allLiterals.addLiteral(_dataValue, false, false);
				}
			}
			if(dataFormatGraph.isUsesPublicKeyOf() || dataFormatGraph.isUsesSecretKeyOf()) {
				_dataValue = "DV_" + _objId + "_pk";
				_sb.append("#define " + _dataValue + " " + asString(_runningDataValue ++) + "\n");
				allLiterals.addLiteral(_dataValue, true, true);
				
				_dataValue = "DV_" + _objId + "_sk";
				_sb.append("#define " + _dataValue + " " + asString(_runningDataValue ++) + "\n");
				if(_objId.compareTo(RV_INTRUDER_CLASS_NAME) == 0) {
					allLiterals.addLiteral(_dataValue, true, true);
				} else {
					allLiterals.addLiteral(_dataValue, false, true);
				}
			}
			if(dataFormatGraph.isUsesSymmetricKeyOf()) {
				_dataValue = "DV_" + _objId + "_k";
				_sb.append("#define " + _dataValue + " " + asString(_runningDataValue ++) + "\n");
				if(_objId.compareTo(RV_INTRUDER_CLASS_NAME) == 0) {
					allLiterals.addLiteral(_dataValue, true, true);
				} else {
					allLiterals.addLiteral(_dataValue, false, true);
				}
			}
		}
		
		_sb.append("\n");   		
		_sb.append("\n");
		
		allLiteralsDefinition = _sb.toString();
	}



	
	protected String eatClass(MD_Class _class) {
		if(_class.getStateMachine() == null) {
			return "";
		}
		
		StringBuffer _sb = new StringBuffer();
		_sb.append(writeNiceComment("Class " + _class.getName()));
		
		
//		---------------------------------------------------------------------------------------------		
//		state IDs
//		we want to put the initial state at the first position in the list and give it ID of 0
		int _stateId = 1;
		int _uniquity = 0;
		Hashtable _allStateNames = new Hashtable();
		String _stateIdListCode = "";
		String _oneStateId;
		String _initialStateName = "";		
		for (Iterator _iter = _class.getStateMachine().getStates().iterator(); _iter.hasNext(); ) {
			MD_State _state = (MD_State) _iter.next();
			
			_oneStateId = "SID_" + _class.getName() + "_" + _state.getName();
			if(_allStateNames.containsKey(_oneStateId)) {
				_oneStateId += asString(_uniquity ++);
			}
			_allStateNames.put(_oneStateId, "");
			_state.setPromelaStateIdName(_oneStateId);
			if(_state.isInitial()) {
				_initialStateName = _oneStateId;
				_stateIdListCode = "#define " + _oneStateId + "\t0\n" + _stateIdListCode;  
			} else {
				_stateIdListCode += "#define " + _oneStateId + "\t" + asString(_stateId ++) + "\n";  
			}
		}
		_sb.append(_stateIdListCode + "\n");
		
		
		
// ---------------------------------------------------------------------------------------------		
// prepare the symbol table
// first it includes all the literals
// and below we will add all links

//		_class.setSymbolTable(new SymbolTable(allLiterals));
		
		
		Hashtable _allAssociationNames = new Hashtable();


// ---------------------------------------------------------------------------------------------		
// procedure header
		_sb.append("proctype " + giveClassProcName(_class.getName()) + "(TYPE_DATAVAL " + RV_THIS_ID + "; chan " + RV_CHANNEL_IN + "; chan " + RV_CHANNEL_OUT);
		for(Iterator _iter01 = _class.getAssociationEnds().iterator(); _iter01.hasNext();) {
			MD_AssociationEnd _associationEnd = (MD_AssociationEnd)_iter01.next();
			MD_Association _association = _associationEnd.getAssociationModel();
			MD_AssociationEnd _anotherAssociationEnd = _association.getAnotherEnd(_associationEnd);
						
			if(_anotherAssociationEnd.getName().length() == 0) {
				continue;
			}
			
			_sb.append("; TYPE_DATAVAL " + _anotherAssociationEnd.getName());			
			if(_allAssociationNames.containsKey(_anotherAssociationEnd.getName())) {
				throw new ExceptionBadModel("Class '" + _class.getName() + "' has two associations with same peer name '" + _anotherAssociationEnd.getName() + "'");
			}
			_allAssociationNames.put(_anotherAssociationEnd.getName(), "");
			
			
//			_class.getSymbolTable().addVariable(_anotherEnd.getName());
			
		}
		_sb.append(") {\n\n");

// ---------------------------------------------------------------------------------------------		
// internal promela variables
		_sb.append("/* internal promela variables */\n");
		_sb.append("TYPE_STATEID\t" + RV_CURRENT_STATE_ID + " = " + _initialStateName + ";\n");
		_sb.append("TYPE_EVENT\t" + RV_CURRENT_EVENT + ";\n");
		_sb.append("TYPE_DATAVAL\t" + RV_SENDER + ";\n");
		_sb.append("TYPE_DATAVAL\t" + RV_CURRENT_SIMPLE_VAR + ";\n");
		_sb.append("MSG\t" + RV_CURRENT_MESSAGE + ";\n");
		_sb.append("bool\t\t" + RV_OBJECT_TERMINATED + " = 0;\n\n");
		
		
// variables declaration
   		_sb.append("/* class variables */\n");

		Hashtable _allVariableNames = new Hashtable();
		for (Iterator _iter = _class.getAttributes().iterator(); _iter.hasNext();) {
			MD_Attribute _attribute = (MD_Attribute) _iter.next();
			
			if(isReservedVariableName(_attribute.getName())) {
				throw new ExceptionBadModel("Class '" + _class.getName() + "' uses reserved variable name '" + _attribute.getName() + "'");
			}
			if(_allAssociationNames.containsKey(_attribute.getName())) {
				throw new ExceptionBadModel("Class '" + _class.getName() + "' has attribute and association with same (peer) name '" + _attribute.getName() + "'");
			}
			if(_allVariableNames.containsKey(_attribute.getName())) {
				throw new ExceptionBadModel("Class '" + _class.getName() + "' has two attributes with same name '" + _attribute.getName() + "'");
			}
			
			if(_attribute.isConstant()) {
				declareConstantVariable(_sb, _attribute);
			} else {
				declareWritebleVariable(_sb, _attribute);
			}

			
			_allVariableNames.put(_attribute.getName(), "");			
		}		
		_sb.append("\n");
		
		
// ================== COMPLETION
		
		
		_sb.append(RV_MAIN_LOOP + ":\n");
		_sb.append("/* run completion */\n");
		_sb.append("do\n");
		
//	loop through all states
		for (Iterator iter = _class.getStateMachine().getStates().iterator(); iter.hasNext();) {
			MD_State _state = (MD_State)iter.next();
			int _outgoingTransitionCount = 0;
			MD_Transition _lastFoundTransition = null;
			 
// check whether we have more then one outgoing "completion" transactions
// if YES, we create another ::if sublevel
			for(Iterator _it00 = _state.getOutgoingTransactions(); _it00.hasNext(); ) {
				MD_Transition _transition = (MD_Transition)_it00.next();
				if(_transition.getTrigger() == null) {
					_outgoingTransitionCount ++;
					_lastFoundTransition = _transition;
				}
			}		
			
// do nothing if no outgoing completion transtions			
			if(_outgoingTransitionCount == 0) {
				continue;
			}

			_sb.append(":: (" + RV_CURRENT_STATE_ID + " == " + _state.getPromelaStateIdName() + ") ->\n");
			if(_outgoingTransitionCount == 1) {
				if(_lastFoundTransition.getGuardProcessed()) {
					_sb.append(((USE_BASE)_lastFoundTransition.getGuardModel().getExpressionTree()).getCompiledExpression());
				}
				if(_lastFoundTransition.getEffectProcessed()) {
					_sb.append(((USE_BASE)_lastFoundTransition.getEffectModel().getExpressionTree()).getCompiledExpression());
				}
				_sb.append("\t" + RV_CURRENT_STATE_ID + " = " + _lastFoundTransition.getTargetState().getPromelaStateIdName() + ";\n");
			} else if(_outgoingTransitionCount > 1) {
				_sb.append("if\n");
								
				boolean _elsePresent = false;								
				for(Iterator _it00 = _state.getOutgoingTransactions(); _it00.hasNext(); ) {
					MD_Transition _transition = (MD_Transition)_it00.next();
					if(_transition.getTrigger() == null) {
						_sb.append("::");
						if(_transition.getGuardProcessed()) {
							_sb.append(((USE_BASE)_transition.getGuardModel().getExpressionTree()).getCompiledExpression());
							if(_transition.getGuardModel().getExpressionTree() instanceof USE_GuardElse) {
								_elsePresent = true;
							}
						}
						if(_transition.getEffectProcessed()) {
							_sb.append(((USE_BASE)_transition.getEffectModel().getExpressionTree()).getCompiledExpression());
						}
						_sb.append("\t" + RV_CURRENT_STATE_ID + " = " + _transition.getTargetState().getPromelaStateIdName() + ";\n");
					}
				}		
				
				if(!_elsePresent) {
					_sb.append(":: else-> ;\n");
				}
				_sb.append("fi;\n");
			}
		}

		_sb.append(":: else -> break;\n");
		_sb.append("od;\n\n");
		
		
// ==================		

//		check for termination
		_sb.append("/* check for termination */\n");
		_sb.append("if\n");
		_sb.append(":: (" + RV_OBJECT_TERMINATED + " == 1) -> goto " + RV_END_MAIN_LOOP + ";\n");
		_sb.append(":: else -> ;\n");
		_sb.append("fi;\n\n");

//		read the next event
		_sb.append("/* read the next event */\n");
		_sb.append(RV_CHANNEL_IN + "?" + RV_SENDER + ";\n");
		_sb.append(RV_CHANNEL_IN + "?_;\n");
		_sb.append(RV_CHANNEL_IN + "?" + RV_CURRENT_EVENT + ";\n");
		_sb.append("ReadMessage(" + RV_CHANNEL_IN + ", " + RV_CURRENT_MESSAGE + ");\n\n");



// ================== execute event
   		_sb.append("/* execute the event */\n");
		_sb.append("\n");
		_sb.append("if\n");
				
//		loop through all states
		for (Iterator iter = _class.getStateMachine().getStates().iterator(); iter.hasNext();) {
			MD_State _state = (MD_State)iter.next();
			Hashtable _triggersWithGuardFromThisState = new Hashtable();
			
//	   loop through all "triggered" transitions
			for(Iterator _it00 = _state.getOutgoingTransactions(); _it00.hasNext(); ) {
				MD_Transition _transition = (MD_Transition)_it00.next();
				MD_Trigger _trigger = _transition.getTrigger();
				if(_trigger == null) {
					continue;
				}
				
// we do not allow two different transitions with same trigger from one single state



//				MD_Guard _guard = _transition.getGuardModel();
				boolean _guardProcessed = _transition.getGuardProcessed();


				
				if(_guardProcessed) {
					if(_triggersWithGuardFromThisState.containsKey(_trigger)) {
						throw new ExceptionBadModel("Unsupported: two different transitions with same trigger from one single state");
					}
					_triggersWithGuardFromThisState.put(_trigger, null);
				}
				
				_sb.append(":: (" + RV_CURRENT_STATE_ID + " == " + _state.getPromelaStateIdName() + " && " +
						RV_CURRENT_EVENT + " == " + _trigger.getAssociatedOperation().getPromelaEventName() + ") ->\n");
						
//	   copy the acquired variable to the class attribute			
//	   (only when the trigger has parameters)
				for (Iterator iter01 = _trigger.getParameters().iterator(); iter01.hasNext();) {
					MD_TriggerParameter _triggerParameter = (MD_TriggerParameter)iter01.next();
					String _tpn = _triggerParameter.getName();
					MD_Attribute _xat = _triggerParameter.getAssociatedAtribute();
					
					_sb.append("\tAssignComplexToComplex(" + _triggerParameter.getAssociatedAtribute().getName()  + ", " + RV_CURRENT_MESSAGE + ");\n");
				}
				
// guard				
				if(_guardProcessed) {
				   _sb.append(((USE_BASE)_transition.getGuardModel().getExpressionTree()).getCompiledExpression());
				}
				
// effect				
				if(_transition.getEffectProcessed()) {
					_sb.append(((USE_BASE)_transition.getEffectModel().getExpressionTree()).getCompiledExpression());
				}
				
// new state				
				_sb.append("\t" + RV_CURRENT_STATE_ID + " = " + _transition.getTargetState().getPromelaStateIdName() + ";\n");
			}		
		}
				
		_sb.append(":: else -> ;\n");
		_sb.append("fi;\n");
		_sb.append("\n");
   		
// check for termination again
		_sb.append("/* wrap the loop */\n");
		_sb.append(RV_END_MAIN_LOOP + ":\n");
		_sb.append("if\n");
		_sb.append(":: (" + RV_OBJECT_TERMINATED + " == 0) -> goto " + RV_MAIN_LOOP + ";\n");
		_sb.append(":: else-> ;\n");
		_sb.append("fi;\n\n");

		_sb.append("}\n\n");
		
		_class.setPromelaCode(_sb.toString());
		return _sb.toString();
	}


/**
 * here we can either:
 * 		* read from any input channel and forward it to the appropriate output channel
 * 		* send something to any output channel
 * 
 * @param _sb
 * @param _intruder
 */
	void intruderLoopWrite(StringBuffer _sb, Intruder _intruder) {
		_sb.append("do\n");
		for(Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			MD_Object _object = (MD_Object)_iter.next();
// read something from this object and forward it
			if(objectCountForIntruder == 2) {
				_sb.append("::\t" + giveFromChannelNameInIntruder(_object) + "?" + RV_INTRUDER_TMP_VALUE_SENDER + ";\n");
				_sb.append("\t" + giveFromChannelNameInIntruder(_object) + "?" + RV_INTRUDER_TMP_VALUE_RECEIVER + ";\n");
				_sb.append("\t" + giveFromChannelNameInIntruder(_object) + "?" + RV_INTRUDER_TMP_VALUE_EVENT + ";\n");

				_sb.append("\t" + giveToChannelNameInIntruder(getAnotherObject(_object)) + "!" + RV_INTRUDER_TMP_VALUE_SENDER + ";\n");
				_sb.append("\t" + giveToChannelNameInIntruder(getAnotherObject(_object)) + "!" + RV_INTRUDER_TMP_VALUE_RECEIVER + ";\n");
				_sb.append("\t" + giveToChannelNameInIntruder(getAnotherObject(_object)) + "!" + RV_INTRUDER_TMP_VALUE_EVENT + ";\n");

				_sb.append("\tReadMessage(" + giveFromChannelNameInIntruder(_object) + ", " + RV_INTRUDER_LAST_READ_MESSAGE + ");\n");
				_sb.append("\tSendMessage(" + giveToChannelNameInIntruder(getAnotherObject(_object)) + ", " + RV_INTRUDER_LAST_READ_MESSAGE + ");\n");
				if(_intruder.canRead()) {
					_sb.append("\tAnalyseMessage(" + RV_INTRUDER_LAST_READ_MESSAGE + ", " + RV_INTRUDER_TMP_MESSAGE + ");\n");
				}
				
				_sb.append("\n");
			} else {
				throw new ExceptionProgrammLogicError("not implemented");
			}
// send something to this object
			_sb.append("::\tSendSomeNodeId(" + giveToChannelNameInIntruder(_object) + ");\n");
			_sb.append("\tSendSomeNodeId(" + giveToChannelNameInIntruder(_object) + ");\n");
			_sb.append("\tSendSomeMessage(" + giveToChannelNameInIntruder(_object) + ");\n");
			_sb.append("\n");
		}
		_sb.append("od;\n");
	}

	void intruderLoopDelete(StringBuffer _sb, Intruder _intruder) {
		_sb.append("do\n");
		for(Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			MD_Object _object = (MD_Object)_iter.next();
// read something from this object and forward it
			if(objectCountForIntruder == 2) {
				_sb.append("::\t" + giveFromChannelNameInIntruder(_object) + "?" + RV_INTRUDER_TMP_VALUE_SENDER + ";\n");
				_sb.append("\t" + giveFromChannelNameInIntruder(_object) + "?" + RV_INTRUDER_TMP_VALUE_RECEIVER + ";\n");
				_sb.append("\t" + giveFromChannelNameInIntruder(_object) + "?" + RV_INTRUDER_TMP_VALUE_EVENT + ";\n");
				_sb.append("\tReadMessage(" + giveFromChannelNameInIntruder(_object) + ", " + RV_INTRUDER_LAST_READ_MESSAGE + ");\n");

// either send it further or not
				_sb.append("\tif\n");
				_sb.append("\t::\t" + giveToChannelNameInIntruder(getAnotherObject(_object)) + "!" + RV_INTRUDER_TMP_VALUE_SENDER + ";\n");
				_sb.append("\t\t" + giveToChannelNameInIntruder(getAnotherObject(_object)) + "!" + RV_INTRUDER_TMP_VALUE_RECEIVER + ";\n");
				_sb.append("\t\t" + giveToChannelNameInIntruder(getAnotherObject(_object)) + "!" + RV_INTRUDER_TMP_VALUE_EVENT + ";\n");
				_sb.append("\t\tSendMessage(" + giveFromChannelNameInIntruder(_object) + ", " + RV_INTRUDER_LAST_READ_MESSAGE + ");\n");
				_sb.append("\t:: true -> ;\n");
				_sb.append("\tfi;\n");
				
				if(_intruder.canRead()) {
					_sb.append("\tAnalyseMessage(" + RV_INTRUDER_LAST_READ_MESSAGE + ", " + RV_INTRUDER_LAST_READ_MESSAGE + ");\n");
				}
				
			} else {
				throw new ExceptionProgrammLogicError("not implemented");
			}
		}
		_sb.append("od;\n");
	}
	
	void intruderLoopWriteDelete(StringBuffer _sb, Intruder _intruder) {
		_sb.append("do\n");

		for(Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			MD_Object _object = (MD_Object)_iter.next();
			
// receive something from channel
			_sb.append("\t:: " + giveFromChannelNameInIntruder(_object) + "?_;\n");
			_sb.append("\t   " + giveFromChannelNameInIntruder(_object) + "?_;\n");
			_sb.append("\t   " + giveFromChannelNameInIntruder(_object) + "?_;\n");
			_sb.append("\t   ReadMessage(" + giveFromChannelNameInIntruder(_object) + ", " + RV_INTRUDER_LAST_READ_MESSAGE + "); ");
			if(_intruder.canRead()) {
				_sb.append("\t   AnalyseMessage(" + RV_INTRUDER_LAST_READ_MESSAGE + ", " + RV_INTRUDER_TMP_MESSAGE + ");");
			}
			_sb.append("\n");

// send something there
		   _sb.append("\t:: SendSomeNodeId(" + giveToChannelNameInIntruder(_object) + ");\n");
		   _sb.append("\t   SendSomeNodeId(" + giveToChannelNameInIntruder(_object) + ");\n");
		   _sb.append("\t   SendSomeMessage(" + giveToChannelNameInIntruder(_object) + ");\n");
		}
		_sb.append("od;\n");
	}
	
	
	private String generateIntruder() {
		StringBuffer _sb = new StringBuffer();
		Intruder _intruder = modelRoot.getIntruder();
// we calculate objects in the system;
// whenever there are only two, we can apply some optimisations		
		objectCountForIntruder = 0;
		
		
		_sb.append("proctype " + RV_INTRUDER_CLASS_NAME + "(TYPE_DATAVAL " + RV_THIS_ID);
		for(Iterator _iter = modelRoot.getObjectsAll().iterator(); _iter.hasNext();) {
			 MD_Object _object = (MD_Object)_iter.next();
			 _sb.append("; chan " + giveToChannelNameInIntruder(_object));
			 _sb.append("; chan " + giveFromChannelNameInIntruder(_object));
			 if(objectCountForIntruder == 0) {
			 	object1of2 = _object;
			 }
			 if(objectCountForIntruder == 1) {
				object2of2 = _object;
			 }
			 objectCountForIntruder ++;
		}
		_sb.append(") {\n");
		
		
		
		
		_sb.append("/* variables for anything */\n");

		_sb.append("TYPE_DATAVAL " + RV_INTRUDER_TMP_VALUE_SENDER + ";\n");
		_sb.append("TYPE_DATAVAL " + RV_INTRUDER_TMP_VALUE_RECEIVER + ";\n");
		_sb.append("TYPE_DATAVAL " + RV_INTRUDER_TMP_VALUE_EVENT + ";\n");
		
		_sb.append("TYPE_DATAVAL\t" + RV_CURRENT_SIMPLE_VAR + ";\n");
		
		_sb.append("MSG " + RV_INTRUDER_LAST_READ_MESSAGE + ";\n");
		
		_sb.append("MSG " + RV_INTRUDER_TMP_MESSAGE + "[" + _intruder.getSavedMessageCount() + "];\n");
		for(int _cnt = 0; _cnt < _intruder.getSavedMessageCount(); _cnt ++) {
			_sb.append(RV_INTRUDER_TMP_MESSAGE + "[" + _cnt + "].messageType = MT_GARBAGE;\n");
		}
		
// depending on the intruder capabilities, we use different Intruder algorithms
		if(_intruder.canWrite() && !_intruder.canDelete()) {
			intruderLoopWrite(_sb, _intruder);
		} else if(_intruder.canDelete() && !_intruder.canWrite()) {
			intruderLoopDelete(_sb, _intruder);
		} else if(_intruder.canWrite() && _intruder.canDelete()) {
			intruderLoopWriteDelete(_sb, _intruder);
		} else {
			throw new ExceptionProgrammLogicError("Impossible intruder type");			
		}

		_sb.append("}\n");
		
		return _sb.toString();
	}
	
	
	private void declareConstantVariable(StringBuffer _sb, MD_Attribute _attribute) {
		if(_attribute.getInitialValueExpressionTree() == null) {
			IssueWarning(TranslationSettings.WarningConstantWithoutInitialValue, _attribute.getName());
			if(translationSettings.defineConstVariables()) {
				_sb.append("#define\t\t" + _attribute.getName() + " DV_GARBAGE\n");
			} else {
				_sb.append("TYPE_DATAVAL\t" + _attribute.getName() + " = DV_GARBAGE;\n");
			}
		} else {
			if(((USE_BASE)_attribute.getInitialValueExpressionTree()).getInitialValueComplexity() == EnumExpressionComplexity.Simple) {
				if(translationSettings.defineConstVariables() && _attribute.getInitialValueExpressionCompiled().indexOf('(') == -1) {
					_sb.append("#define\t\t" + _attribute.getName() + " " + _attribute.getInitialValueExpressionCompiled() + "\n");
				} else {
					_sb.append("TYPE_DATAVAL\t" + _attribute.getName() + " = " + _attribute.getInitialValueExpressionCompiled() + ";\n");
				}
			} else if(((USE_BASE)_attribute.getInitialValueExpressionTree()).getInitialValueComplexity() == EnumExpressionComplexity.Complex) {
				throw new ExceptionProgrammLogicError("Not implemented");
			} else {
				throw new ExceptionProgrammLogicError("Undefined initial value complexity in PromelaTranslator::declareConstantVariable");
			}
		}
	}
	
	private void declareWritebleVariable(StringBuffer _sb, MD_Attribute _attribute) {
		if(_attribute.getVariableComplexity() == EnumExpressionComplexity.Simple) {
			if(_attribute.getInitialValueExpressionTree() == null) {
				IssueWarning(TranslationSettings.WarningVariableWithoutInitialValue, _attribute.getName());
				_sb.append("TYPE_DATAVAL\t" + _attribute.getName() + ";\n");
			} else {
				_sb.append("TYPE_DATAVAL\t" + _attribute.getName() + " = " + _attribute.getInitialValueExpressionCompiled() + ";\n");
			}
		} else if(_attribute.getVariableComplexity() == EnumExpressionComplexity.Complex) {
			if(_attribute.getInitialValueExpressionTree() == null) {
				IssueWarning(TranslationSettings.WarningVariableWithoutInitialValue, _attribute.getName());
				_sb.append("MSG\t\t" + _attribute.getName() + ";\n");
			} else {
				throw new ExceptionProgrammLogicError("Not implemented");
			}
		} else {
			throw new ExceptionProgrammLogicError("Undefined variable complexity in PromelaTranslator::declareWritebleVariable");
		}
		
	}
	

	public TranslationSettings GetTranslationSettings() {
		return translationSettings;
	}
	
	
	private void IssueWarning(int _warningId, String _msg) {
		if(translationSettings.getWarningEnabled(_warningId)) {
			log.appendLog(translationSettings.getWarningText(_warningId));
			if(_msg != null) {
				log.appendLog(": " + _msg);
			}
			log.appendLogLn();			
		}
	}
	
	MD_Object getAnotherObject(MD_Object _thisObject) {
		if(objectCountForIntruder != 2) {
			throw new ExceptionProgrammLogicError("applying two-object intruder optimisation when there are not two objects");
		}
		if(_thisObject == object1of2) {
			return object2of2;
		} else {
			return object1of2;
		}
	}


	
	ModelRoot modelRoot;

	DFGGraph dataFormatGraph = new DFGGraph();
	
	OGGraph objectGraph;
	
	ILogOutput log;
	TranslationSettings translationSettings = new TranslationSettings();
	
	SymbolTable allLiterals = new SymbolTable(null);
	String allLiteralsDefinition;

//	Vector physicalLinkStereotypes;
	
	Vector allEventIDs = new Vector();

	int maxMessageParameterCount;
	int maxTweakMessageCount;
	
	MD_Attribute secretAttribute;


// the following counter and two objects are used for optimisations of the intruder code whenever there are only two objects
	int objectCountForIntruder;
	MD_Object object1of2; 	 
   	MD_Object object2of2; 	 
}

