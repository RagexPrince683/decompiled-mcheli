package mcheli.eval.eval.func;

public abstract interface Function
{
  public abstract long evalLong(Object paramObject, String paramString, Long[] paramArrayOfLong)
    throws Throwable;
  
  public abstract double evalDouble(Object paramObject, String paramString, Double[] paramArrayOfDouble)
    throws Throwable;
  
  public abstract Object evalObject(Object paramObject, String paramString, Object[] paramArrayOfObject)
    throws Throwable;
}
