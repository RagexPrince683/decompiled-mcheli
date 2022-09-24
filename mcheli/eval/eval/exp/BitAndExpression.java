package mcheli.eval.eval.exp;

import mcheli.eval.eval.oper.Operator;



public class BitAndExpression
  extends Col2Expression
{
  public BitAndExpression()
  {
    setOperator("&");
  }
  
  protected BitAndExpression(BitAndExpression from, ShareExpValue s) {
    super(from, s);
  }
  
  public AbstractExpression dup(ShareExpValue s) {
    return new BitAndExpression(this, s);
  }
  
  protected long operateLong(long vl, long vr) {
    return vl & vr;
  }
  
  protected double operateDouble(double vl, double vr) {
    return (int)vl & (int)vr;
  }
  
  protected Object operateObject(Object vl, Object vr) {
    return this.share.oper.bitAnd(vl, vr);
  }
}
