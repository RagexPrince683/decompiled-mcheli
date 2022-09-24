package mcheli.eval.eval.var;

public abstract interface Variable
{
  public abstract void setValue(Object paramObject1, Object paramObject2);
  
  public abstract Object getObject(Object paramObject);
  
  public abstract long evalLong(Object paramObject);
  
  public abstract double evalDouble(Object paramObject);
  
  public abstract Object getObject(Object paramObject, int paramInt);
  
  public abstract void setValue(Object paramObject1, int paramInt, Object paramObject2);
  
  public abstract Object getObject(Object paramObject, String paramString);
  
  public abstract void setValue(Object paramObject1, String paramString, Object paramObject2);
}
