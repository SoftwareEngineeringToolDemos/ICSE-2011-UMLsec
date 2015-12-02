/*
 * Generated by classgen, version 1.3
 * 15/01/04 14:52
 */
package tum.umlsec.viki.tools.dynaviki.model.scanner;

public abstract class USE_BASE implements SyntaxNode {

  private tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGVertex dfgVertex;
  private tum.umlsec.viki.tools.dynaviki.model.TreeNodeScannerExpression treeViewNode;
  private int expressionComplexity;
  private String initialValueExpression;
  private int initialValueComplexity;
  private String compiledExpression;
  private SyntaxNode parent;

  public tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGVertex getDfgVertex() {
    return dfgVertex;
  }

  public void setDfgVertex(tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGVertex dfgVertex) {
    this.dfgVertex = dfgVertex;
  }

  public tum.umlsec.viki.tools.dynaviki.model.TreeNodeScannerExpression getTreeViewNode() {
    return treeViewNode;
  }

  public void setTreeViewNode(tum.umlsec.viki.tools.dynaviki.model.TreeNodeScannerExpression treeViewNode) {
    this.treeViewNode = treeViewNode;
  }

  public int getExpressionComplexity() {
    return expressionComplexity;
  }

  public void setExpressionComplexity(int expressionComplexity) {
    this.expressionComplexity = expressionComplexity;
  }

  public String getInitialValueExpression() {
    return initialValueExpression;
  }

  public void setInitialValueExpression(String initialValueExpression) {
    this.initialValueExpression = initialValueExpression;
  }

  public int getInitialValueComplexity() {
    return initialValueComplexity;
  }

  public void setInitialValueComplexity(int initialValueComplexity) {
    this.initialValueComplexity = initialValueComplexity;
  }

  public String getCompiledExpression() {
    return compiledExpression;
  }

  public void setCompiledExpression(String compiledExpression) {
    this.compiledExpression = compiledExpression;
  }

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