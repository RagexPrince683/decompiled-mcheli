package mcheli.eval.eval.srch;

import mcheli.eval.eval.exp.AbstractExpression;
import mcheli.eval.eval.exp.Col1Expression;
import mcheli.eval.eval.exp.Col2Expression;
import mcheli.eval.eval.exp.Col3Expression;
import mcheli.eval.eval.exp.FunctionExpression;
import mcheli.eval.eval.exp.WordExpression;

public abstract interface Search
{
  public abstract boolean end();
  
  public abstract void search(AbstractExpression paramAbstractExpression);
  
  public abstract void search0(WordExpression paramWordExpression);
  
  public abstract boolean search1_begin(Col1Expression paramCol1Expression);
  
  public abstract void search1_end(Col1Expression paramCol1Expression);
  
  public abstract boolean search2_begin(Col2Expression paramCol2Expression);
  
  public abstract boolean search2_2(Col2Expression paramCol2Expression);
  
  public abstract void search2_end(Col2Expression paramCol2Expression);
  
  public abstract boolean search3_begin(Col3Expression paramCol3Expression);
  
  public abstract boolean search3_2(Col3Expression paramCol3Expression);
  
  public abstract boolean search3_3(Col3Expression paramCol3Expression);
  
  public abstract void search3_end(Col3Expression paramCol3Expression);
  
  public abstract boolean searchFunc_begin(FunctionExpression paramFunctionExpression);
  
  public abstract boolean searchFunc_2(FunctionExpression paramFunctionExpression);
  
  public abstract void searchFunc_end(FunctionExpression paramFunctionExpression);
}
