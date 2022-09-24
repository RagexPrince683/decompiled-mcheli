package mcheli.eval.eval.exp;



public class OptimizeLong
  extends OptimizeObject
{
  public OptimizeLong() {}
  

  protected boolean isTrue(AbstractExpression x)
  {
    return x.evalLong() != 0L;
  }
  
  protected AbstractExpression toConst(AbstractExpression exp) {
    try {
      long val = exp.evalLong();
      return NumberExpression.create(exp, Long.toString(val));
    } catch (Exception e) {}
    return exp;
  }
}
