package tum.umlsec.viki.tools.dynaviki.model.scanner;



attr "tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGVertex" dfgVertex with USE_BASE;
attr "tum.umlsec.viki.tools.dynaviki.model.TreeNodeScannerExpression" treeViewNode with USE_BASE;



attr "int" expressionComplexity with USE_BASE;

attr "int" variableType with USE_Variable;
attr "tum.umlsec.viki.tools.dynaviki.model.MD_AssociationEnd" associationEndModel with USE_Variable;
attr "tum.umlsec.viki.tools.dynaviki.model.MD_Attribute" attributeModel with USE_Variable;


attr "String" initialValueExpression with USE_BASE;
attr "int" initialValueComplexity with USE_BASE;



attr "String" compiledExpression with USE_BASE;




USE_BASE ::= 
	{USE_GuardVirt} |
	{USE_EffectBase} |
	{USE_CompareOperator} |
	{USE_ParameterList} |
	{USE_RTerm}




USE_EffectBase ::=
	{USE_FunctionCall} |
	{USE_Assignment} |
	{USE_EffectList}
		USE_EffectBase:		effect1
		USE_EffectBase:		effect2




USE_GuardVirt ::=
		{USE_Guard}
			USE_RTerm:			lTerm
			USE_CompareOperator:		op
			USE_RTerm:			rTerm
	|
		{USE_GuardElse}
			
	
USE_FunctionCall ::=
	USE_RTerm:			callTarget
	String:				functionName
	USE_ParameterList:		parList
	
	
USE_Assignment ::=
	USE_Variable:			variable
	USE_RTerm:			value
				
				
USE_CompareOperator ::=	{USE_CompareOperatorEqual} | {USE_CompareOperatorNotEqual}


USE_ParameterList	::=	
			{USE_ParameterListCont}
				USE_RTerm:		param
				USE_ParameterList:	paramList
		|
			{USE_ParameterListEnd}
				USE_RTerm:		param
				
		
USE_RTerm	::=		
			{USE_Concatenation}
				USE_RTerm:			lTerm
				USE_RTerm:			rTerm
		|	
			{USE_Select}
				USE_RTerm:			term
				"int":				index
		|
			{USE_Variable}
				String:				name
		|
			{USE_ApplyKey}	
				USE_RTerm:			term
				USE_RTerm:			key
		|
			{USE_SenderOf}
				USE_Variable:			variable
		|
			{USE_PublicKeyOf}
				USE_RTerm:			term
		|
			{USE_SecretKeyOf}
				USE_RTerm:			term
		|
			{USE_SymmetricKeyOf}
				USE_RTerm:			term
		|
			{USE_NonceOf}
				USE_RTerm:			term	
		|
			{USE_This}
				


