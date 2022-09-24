package mcheli.eval.eval.exp;

import java.util.List;







public class FuncArgExpression
  extends Col2OpeExpression
{
  public FuncArgExpression()
  {
    setOperator(",");
  }
  
  protected FuncArgExpression(FuncArgExpression from, ShareExpValue s) {
    super(from, s);
  }
  
  public AbstractExpression dup(ShareExpValue s) {
    return new FuncArgExpression(this, s);
  }
  
  protected void evalArgsLong(List args) {
    this.expl.evalArgsLong(args);
    this.expr.evalArgsLong(args);
  }
  
  protected void evalArgsDouble(List args) {
    this.expl.evalArgsDouble(args);
    this.expr.evalArgsDouble(args);
  }
  
  protected void evalArgsObject(List args) {
    this.expl.evalArgsObject(args);
    this.expr.evalArgsObject(args);
  }
  
  protected String toStringLeftSpace() {
    return "";
  }
}
