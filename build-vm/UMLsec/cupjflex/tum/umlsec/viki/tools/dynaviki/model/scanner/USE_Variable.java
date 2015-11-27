/*
 * Generated by classgen, version 1.3
 * 15/01/04 14:52
 */
package tum.umlsec.viki.tools.dynaviki.model.scanner;

public class USE_Variable extends USE_RTerm {

  private String name;
  private int variableType;
  private tum.umlsec.viki.tools.dynaviki.model.MD_AssociationEnd associationEndModel;
  private tum.umlsec.viki.tools.dynaviki.model.MD_Attribute attributeModel;

  public USE_Variable (String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getVariableType() {
    return variableType;
  }

  public void setVariableType(int variableType) {
    this.variableType = variableType;
  }

  public tum.umlsec.viki.tools.dynaviki.model.MD_AssociationEnd getAssociationEndModel() {
    return associationEndModel;
  }

  public void setAssociationEndModel(tum.umlsec.viki.tools.dynaviki.model.MD_AssociationEnd associationEndModel) {
    this.associationEndModel = associationEndModel;
  }

  public tum.umlsec.viki.tools.dynaviki.model.MD_Attribute getAttributeModel() {
    return attributeModel;
  }

  public void setAttributeModel(tum.umlsec.viki.tools.dynaviki.model.MD_Attribute attributeModel) {
    this.attributeModel = attributeModel;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("USE_Variable(\n");
    buffer.append("  "+tab+name);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [USE_Variable]");
    return buffer.toString();
  }
}