package mcheli.eval.eval.exp;

import mcheli.eval.eval.oper.Operator;



public class BitNotExpression
  extends Col1Expression
{
  public BitNotExpression()
  {
    setOperator("~");
  }
  
  protected BitNotExpression(BitNotExpression from, ShareExpValue s) {
    super(from, s);
  }
  
  public AbstractExpression dup(ShareExpValue s) {
    return new BitNotExpression(this, s);
  }
  
  protected long operateLong(long val) {
    return val ^ 0xFFFFFFFF;
  }
  
  protected double operateDouble(double val) {
    return (int)val ^ 0xFFFFFFFF;
  }
  
  public Object evalObject() {
    return this.share.oper.bitNot(this.exp.evalObject());
  }
}
