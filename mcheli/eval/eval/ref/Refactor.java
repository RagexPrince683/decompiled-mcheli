package mcheli.eval.eval.ref;

public abstract interface Refactor
{
  public abstract String getNewName(Object paramObject, String paramString);
  
  public abstract String getNewFuncName(Object paramObject, String paramString);
}
