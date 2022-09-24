package mcheli.eval.eval;

public abstract class Rule
{
  public Rule() {}
  
  public abstract Expression parse(String paramString);
}
