/*
 * Generated by classgen, version 1.3
 * 15/01/04 14:52
 */
package tum.umlsec.viki.tools.dynaviki.model.scanner;

public class USE_SymmetricKeyOf extends USE_RTerm {

  private USE_RTerm term;

  public USE_SymmetricKeyOf (USE_RTerm term) {
    this.term = term;
    if (term != null) term.setParent(this);
  }

  public USE_RTerm getTerm() {
    return term;
  }

  public void setTerm(USE_RTerm term) {
    this.term = term;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (term != null) term.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (term != null) term.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (term != null) term.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("USE_SymmetricKeyOf(\n");
      if (term != null)
        buffer.append(term.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [USE_SymmetricKeyOf]");
    return buffer.toString();
  }
}
