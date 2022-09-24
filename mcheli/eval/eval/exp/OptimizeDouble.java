package mcheli.eval.eval.exp;



public class OptimizeDouble
  extends OptimizeObject
{
  public OptimizeDouble() {}
  

  protected boolean isTrue(AbstractExpression x)
  {
    return x.evalDouble() != 0.0D;
  }
  
  protected AbstractExpression toConst(AbstractExpression exp) {
    try {
      double val = exp.evalDouble();
      return NumberExpression.create(exp, Double.toString(val));
    } catch (Exception e) {}
    return exp;
  }
}
