package mcheli.eval.eval.exp;

import mcheli.eval.eval.oper.Operator;



public class BitOrExpression
  extends Col2Expression
{
  public BitOrExpression()
  {
    setOperator("|");
  }
  
  protected BitOrExpression(BitOrExpression from, ShareExpValue s) {
    super(from, s);
  }
  
  public AbstractExpression dup(ShareExpValue s) {
    return new BitOrExpression(this, s);
  }
  
  protected long operateLong(long vl, long vr) {
    return vl | vr;
  }
  
  protected double operateDouble(double vl, double vr) {
    return (int)vl | (int)vr;
  }
  
  protected Object operateObject(Object vl, Object vr) {
    return this.share.oper.bitOr(vl, vr);
  }
}
