package mcheli.eval.eval.repl;

import mcheli.eval.eval.exp.AbstractExpression;
import mcheli.eval.eval.exp.Col1Expression;
import mcheli.eval.eval.exp.Col2Expression;
import mcheli.eval.eval.exp.Col2OpeExpression;
import mcheli.eval.eval.exp.Col3Expression;
import mcheli.eval.eval.exp.FunctionExpression;
import mcheli.eval.eval.exp.WordExpression;

public abstract interface Replace
{
  public abstract AbstractExpression replace0(WordExpression paramWordExpression);
  
  public abstract AbstractExpression replace1(Col1Expression paramCol1Expression);
  
  public abstract AbstractExpression replace2(Col2Expression paramCol2Expression);
  
  public abstract AbstractExpression replace2(Col2OpeExpression paramCol2OpeExpression);
  
  public abstract AbstractExpression replace3(Col3Expression paramCol3Expression);
  
  public abstract AbstractExpression replaceVar0(WordExpression paramWordExpression);
  
  public abstract AbstractExpression replaceVar1(Col1Expression paramCol1Expression);
  
  public abstract AbstractExpression replaceVar2(Col2Expression paramCol2Expression);
  
  public abstract AbstractExpression replaceVar2(Col2OpeExpression paramCol2OpeExpression);
  
  public abstract AbstractExpression replaceVar3(Col3Expression paramCol3Expression);
  
  public abstract AbstractExpression replaceFunc(FunctionExpression paramFunctionExpression);
  
  public abstract AbstractExpression replaceLet(Col2Expression paramCol2Expression);
}
