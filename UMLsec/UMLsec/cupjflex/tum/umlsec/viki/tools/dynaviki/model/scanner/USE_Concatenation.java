/*
 * Generated by classgen, version 1.3
 * 15/01/04 14:52
 */
package tum.umlsec.viki.tools.dynaviki.model.scanner;

public class USE_Concatenation extends USE_RTerm {

  private USE_RTerm lTerm;
  private USE_RTerm rTerm;

  public USE_Concatenation (USE_RTerm lTerm, USE_RTerm rTerm) {
    this.lTerm = lTerm;
    if (lTerm != null) lTerm.setParent(this);
    this.rTerm = rTerm;
    if (rTerm != null) rTerm.setParent(this);
  }

  public USE_RTerm getLTerm() {
    return lTerm;
  }

  public void setLTerm(USE_RTerm lTerm) {
    this.lTerm = lTerm;
  }

  public USE_RTerm getRTerm() {
    return rTerm;
  }

  public void setRTerm(USE_RTerm rTerm) {
    this.rTerm = rTerm;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (lTerm != null) lTerm.accept(visitor);
    if (rTerm != null) rTerm.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (lTerm != null) lTerm.traverseTopDown(visitor);
    if (rTerm != null) rTerm.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (lTerm != null) lTerm.traverseBottomUp(visitor);
    if (rTerm != null) rTerm.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("USE_Concatenation(\n");
      if (lTerm != null)
        buffer.append(lTerm.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (rTerm != null)
        buffer.append(rTerm.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [USE_Concatenation]");
    return buffer.toString();
  }
}