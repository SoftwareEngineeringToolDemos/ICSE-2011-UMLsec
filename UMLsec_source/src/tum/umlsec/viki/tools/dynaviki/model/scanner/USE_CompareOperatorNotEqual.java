/*
 * Generated by classgen, version 1.3
 * 15/01/04 14:52
 */
package tum.umlsec.viki.tools.dynaviki.model.scanner;

public class USE_CompareOperatorNotEqual extends USE_CompareOperator {


  public USE_CompareOperatorNotEqual () {
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
    buffer.append("USE_CompareOperatorNotEqual(\n");
    buffer.append(tab);
    buffer.append(") [USE_CompareOperatorNotEqual]");
    return buffer.toString();
  }
}
