/*
 * Generated by classgen, version 1.3
 * 15/01/04 14:52
 */
package tum.umlsec.viki.tools.dynaviki.model.scanner;

public abstract class USE_CompareOperator extends USE_BASE implements SyntaxNode {

  private SyntaxNode parent;

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

  public abstract void accept(Visitor visitor);
  public abstract void childrenAccept(Visitor visitor);
  public abstract void traverseTopDown(Visitor visitor);
  public abstract void traverseBottomUp(Visitor visitor);
  public String toString() {
    return toString("");
  }

  public abstract String toString(String tab);
}
