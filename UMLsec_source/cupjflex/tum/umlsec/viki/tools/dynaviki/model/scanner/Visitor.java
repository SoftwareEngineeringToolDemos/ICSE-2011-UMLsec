/*
 * Generated by classgen, version 1.3
 * 15/01/04 14:52
 */
package tum.umlsec.viki.tools.dynaviki.model.scanner;

public interface Visitor {

  public void visit(USE_FunctionCall uSE_FunctionCall);
  public void visit(USE_Assignment uSE_Assignment);
  public void visit(USE_BASE uSE_BASE);
  public void visit(USE_EffectBase uSE_EffectBase);
  public void visit(USE_EffectList uSE_EffectList);
  public void visit(USE_GuardVirt uSE_GuardVirt);
  public void visit(USE_Guard uSE_Guard);
  public void visit(USE_GuardElse uSE_GuardElse);
  public void visit(USE_CompareOperator uSE_CompareOperator);
  public void visit(USE_CompareOperatorEqual uSE_CompareOperatorEqual);
  public void visit(USE_CompareOperatorNotEqual uSE_CompareOperatorNotEqual);
  public void visit(USE_ParameterList uSE_ParameterList);
  public void visit(USE_ParameterListCont uSE_ParameterListCont);
  public void visit(USE_ParameterListEnd uSE_ParameterListEnd);
  public void visit(USE_RTerm uSE_RTerm);
  public void visit(USE_Concatenation uSE_Concatenation);
  public void visit(USE_Select uSE_Select);
  public void visit(USE_Variable uSE_Variable);
  public void visit(USE_ApplyKey uSE_ApplyKey);
  public void visit(USE_SenderOf uSE_SenderOf);
  public void visit(USE_PublicKeyOf uSE_PublicKeyOf);
  public void visit(USE_SecretKeyOf uSE_SecretKeyOf);
  public void visit(USE_SymmetricKeyOf uSE_SymmetricKeyOf);
  public void visit(USE_NonceOf uSE_NonceOf);
  public void visit(USE_This uSE_This);

}
