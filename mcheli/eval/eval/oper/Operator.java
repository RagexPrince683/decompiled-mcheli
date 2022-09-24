package mcheli.eval.eval.oper;

public abstract interface Operator
{
  public abstract Object power(Object paramObject1, Object paramObject2);
  
  public abstract Object signPlus(Object paramObject);
  
  public abstract Object signMinus(Object paramObject);
  
  public abstract Object plus(Object paramObject1, Object paramObject2);
  
  public abstract Object minus(Object paramObject1, Object paramObject2);
  
  public abstract Object mult(Object paramObject1, Object paramObject2);
  
  public abstract Object div(Object paramObject1, Object paramObject2);
  
  public abstract Object mod(Object paramObject1, Object paramObject2);
  
  public abstract Object bitNot(Object paramObject);
  
  public abstract Object shiftLeft(Object paramObject1, Object paramObject2);
  
  public abstract Object shiftRight(Object paramObject1, Object paramObject2);
  
  public abstract Object shiftRightLogical(Object paramObject1, Object paramObject2);
  
  public abstract Object bitAnd(Object paramObject1, Object paramObject2);
  
  public abstract Object bitOr(Object paramObject1, Object paramObject2);
  
  public abstract Object bitXor(Object paramObject1, Object paramObject2);
  
  public abstract Object not(Object paramObject);
  
  public abstract Object equal(Object paramObject1, Object paramObject2);
  
  public abstract Object notEqual(Object paramObject1, Object paramObject2);
  
  public abstract Object lessThan(Object paramObject1, Object paramObject2);
  
  public abstract Object lessEqual(Object paramObject1, Object paramObject2);
  
  public abstract Object greaterThan(Object paramObject1, Object paramObject2);
  
  public abstract Object greaterEqual(Object paramObject1, Object paramObject2);
  
  public abstract boolean bool(Object paramObject);
  
  public abstract Object inc(Object paramObject, int paramInt);
}
