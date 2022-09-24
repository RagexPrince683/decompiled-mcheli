package mcheli.eval.eval;

import mcheli.eval.eval.exp.AbstractExpression;
import mcheli.eval.eval.exp.BitNotExpression;
import mcheli.eval.eval.exp.DivExpression;
import mcheli.eval.eval.exp.FieldExpression;
import mcheli.eval.eval.exp.IfExpression;
import mcheli.eval.eval.exp.IncBeforeExpression;
import mcheli.eval.eval.exp.LetExpression;
import mcheli.eval.eval.exp.LetMinusExpression;
import mcheli.eval.eval.exp.LetPlusExpression;
import mcheli.eval.eval.exp.LetPowerExpression;
import mcheli.eval.eval.exp.LetShiftRightExpression;
import mcheli.eval.eval.exp.ModExpression;
import mcheli.eval.eval.lex.LexFactory;
import mcheli.eval.eval.rule.AbstractRule;
import mcheli.eval.eval.rule.Col1AfterRule;
import mcheli.eval.eval.rule.Col2Rule;
import mcheli.eval.eval.rule.IfRule;
import mcheli.eval.eval.rule.ShareRuleValue;

public class ExpRuleFactory
{
  private static ExpRuleFactory me;
  protected Rule rule;
  protected AbstractRule topRule;
  protected LexFactory defaultLexFactory;
  
  public static ExpRuleFactory getInstance()
  {
    if (me == null) {
      me = new ExpRuleFactory();
    }
    return me;
  }
  







  public static Rule getDefaultRule()
  {
    return getInstance().getRule();
  }
  








  public static Rule getJavaRule()
  {
    return mcheli.eval.eval.rule.JavaRuleFactory.getInstance().getRule();
  }
  







  public ExpRuleFactory()
  {
    ShareRuleValue share = new ShareRuleValue();
    share.lexFactory = getLexFactory();
    init(share);
    this.rule = share;
  }
  




  public Rule getRule()
  {
    return this.rule;
  }
  













  protected void init(ShareRuleValue share)
  {
    AbstractRule rule = null;
    
    rule = add(rule, createCommaRule(share));
    rule = add(rule, createLetRule(share));
    rule = add(rule, createIfRule(share));
    rule = add(rule, createOrRule(share));
    rule = add(rule, createAndRule(share));
    rule = add(rule, createBitOrRule(share));
    rule = add(rule, createBitXorRule(share));
    rule = add(rule, createBitAndRule(share));
    rule = add(rule, createEqualRule(share));
    rule = add(rule, createGreaterRule(share));
    rule = add(rule, createShiftRule(share));
    rule = add(rule, createPlusRule(share));
    rule = add(rule, createMultRule(share));
    rule = add(rule, createSignRule(share));
    rule = add(rule, createPowerRule(share));
    rule = add(rule, createCol1AfterRule(share));
    rule = add(rule, createPrimaryRule(share));
    this.topRule.initPriority(1);
    share.topRule = this.topRule;
    

    initFuncArgRule(share);
  }
  




  protected void initFuncArgRule(ShareRuleValue share)
  {
    AbstractRule argRule = share.funcArgRule = createFuncArgRule(share);
    
    String[] a_opes = argRule.getOperators();
    String[] t_opes = this.topRule.getOperators();
    
    boolean match = false;
    boolean b = false;
    for (int i = 0; i < a_opes.length; i++) {
      for (int j = 0; j < t_opes.length; j++)
        if (a_opes[i].equals(t_opes[j])) {
          match = true;
          b = true;
        }
      if(b)
    	  break;
    }
    if (match)
    {
      argRule.nextRule = this.topRule.nextRule;
    }
    else {
      argRule.nextRule = this.topRule;
    }
    argRule.prio = this.topRule.prio;
  }
  








  protected final AbstractRule add(AbstractRule rule, AbstractRule r)
  {
    if (r == null)
      return rule;
    if (this.topRule == null) {
      this.topRule = r;
    }
    if (rule != null) {
      rule.nextRule = r;
    }
    return r;
  }
  





  protected AbstractRule createCommaRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createCommaExpression());
    return me;
  }
  
  protected AbstractExpression createCommaExpression() {
    return new mcheli.eval.eval.exp.CommaExpression();
  }
  





  protected AbstractRule createLetRule(ShareRuleValue share)
  {
    AbstractRule me = new mcheli.eval.eval.rule.Col2RightJoinRule(share);
    me.addExpression(createLetExpression());
    me.addExpression(createLetMultExpression());
    me.addExpression(createLetDivExpression());
    me.addExpression(createLetModExpression());
    me.addExpression(createLetPlusExpression());
    me.addExpression(createLetMinusExpression());
    me.addExpression(createLetShiftLeftExpression());
    me.addExpression(createLetShiftRightExpression());
    me.addExpression(createLetShiftRightLogicalExpression());
    me.addExpression(createLetAndExpression());
    me.addExpression(createLetOrExpression());
    me.addExpression(createLetXorExpression());
    me.addExpression(createLetPowerExpression());
    return me;
  }
  
  protected AbstractExpression createLetExpression() {
    return new LetExpression();
  }
  
  protected AbstractExpression createLetMultExpression() {
    return new mcheli.eval.eval.exp.LetMultExpression();
  }
  
  protected AbstractExpression createLetDivExpression() {
    return new mcheli.eval.eval.exp.LetDivExpression();
  }
  
  protected AbstractExpression createLetModExpression() {
    return new mcheli.eval.eval.exp.LetModExpression();
  }
  
  protected AbstractExpression createLetPlusExpression() {
    return new LetPlusExpression();
  }
  
  protected AbstractExpression createLetMinusExpression() {
    return new LetMinusExpression();
  }
  
  protected AbstractExpression createLetShiftLeftExpression() {
    return new mcheli.eval.eval.exp.LetShiftLeftExpression();
  }
  
  protected AbstractExpression createLetShiftRightExpression() {
    return new LetShiftRightExpression();
  }
  
  protected AbstractExpression createLetShiftRightLogicalExpression() {
    return new mcheli.eval.eval.exp.LetShiftRightLogicalExpression();
  }
  
  protected AbstractExpression createLetAndExpression() {
    return new mcheli.eval.eval.exp.LetAndExpression();
  }
  
  protected AbstractExpression createLetOrExpression() {
    return new mcheli.eval.eval.exp.LetOrExpression();
  }
  
  protected AbstractExpression createLetXorExpression() {
    return new mcheli.eval.eval.exp.LetXorExpression();
  }
  
  protected AbstractExpression createLetPowerExpression() {
    return new LetPowerExpression();
  }
  





  protected AbstractRule createIfRule(ShareRuleValue share)
  {
    IfRule me = new IfRule(share);
    me.addExpression(me.cond = createIfExpression());
    return me;
  }
  
  protected AbstractExpression createIfExpression() {
    return new IfExpression();
  }
  





  protected AbstractRule createOrRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createOrExpression());
    return me;
  }
  
  protected AbstractExpression createOrExpression() {
    return new mcheli.eval.eval.exp.OrExpression();
  }
  





  protected AbstractRule createAndRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createAndExpression());
    return me;
  }
  
  protected AbstractExpression createAndExpression() {
    return new mcheli.eval.eval.exp.AndExpression();
  }
  





  protected AbstractRule createBitOrRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createBitOrExpression());
    return me;
  }
  
  protected AbstractExpression createBitOrExpression() {
    return new mcheli.eval.eval.exp.BitOrExpression();
  }
  





  protected AbstractRule createBitXorRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createBitXorExpression());
    return me;
  }
  
  protected AbstractExpression createBitXorExpression() {
    return new mcheli.eval.eval.exp.BitXorExpression();
  }
  





  protected AbstractRule createBitAndRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createBitAndExpression());
    return me;
  }
  
  protected AbstractExpression createBitAndExpression() {
    return new mcheli.eval.eval.exp.BitAndExpression();
  }
  





  protected AbstractRule createEqualRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createEqualExpression());
    me.addExpression(createNotEqualExpression());
    return me;
  }
  
  protected AbstractExpression createEqualExpression() {
    return new mcheli.eval.eval.exp.EqualExpression();
  }
  
  protected AbstractExpression createNotEqualExpression() {
    return new mcheli.eval.eval.exp.NotEqualExpression();
  }
  





  protected AbstractRule createGreaterRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createLessThanExpression());
    me.addExpression(createLessEqualExpression());
    me.addExpression(createGreaterThanExpression());
    me.addExpression(createGreaterEqualExpression());
    return me;
  }
  
  protected AbstractExpression createLessThanExpression() {
    return new mcheli.eval.eval.exp.LessThanExpression();
  }
  
  protected AbstractExpression createLessEqualExpression() {
    return new mcheli.eval.eval.exp.LessEqualExpression();
  }
  
  protected AbstractExpression createGreaterThanExpression() {
    return new mcheli.eval.eval.exp.GreaterThanExpression();
  }
  
  protected AbstractExpression createGreaterEqualExpression() {
    return new mcheli.eval.eval.exp.GreaterEqualExpression();
  }
  





  protected AbstractRule createShiftRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createShiftLeftExpression());
    me.addExpression(createShiftRightExpression());
    me.addExpression(createShiftRightLogicalExpression());
    return me;
  }
  
  protected AbstractExpression createShiftLeftExpression() {
    return new mcheli.eval.eval.exp.ShiftLeftExpression();
  }
  
  protected AbstractExpression createShiftRightExpression() {
    return new mcheli.eval.eval.exp.ShiftRightExpression();
  }
  
  protected AbstractExpression createShiftRightLogicalExpression() {
    return new mcheli.eval.eval.exp.ShiftRightLogicalExpression();
  }
  





  protected AbstractRule createPlusRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createPlusExpression());
    me.addExpression(createMinusExpression());
    return me;
  }
  
  protected AbstractExpression createPlusExpression() {
    return new mcheli.eval.eval.exp.PlusExpression();
  }
  
  protected AbstractExpression createMinusExpression() {
    return new mcheli.eval.eval.exp.MinusExpression();
  }
  





  protected AbstractRule createMultRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createMultExpression());
    me.addExpression(createDivExpression());
    me.addExpression(createModExpression());
    return me;
  }
  
  protected AbstractExpression createMultExpression() {
    return new mcheli.eval.eval.exp.MultExpression();
  }
  
  protected AbstractExpression createDivExpression() {
    return new DivExpression();
  }
  
  protected AbstractExpression createModExpression() {
    return new ModExpression();
  }
  





  protected AbstractRule createSignRule(ShareRuleValue share)
  {
    AbstractRule me = new mcheli.eval.eval.rule.SignRule(share);
    me.addExpression(createSignPlusExpression());
    me.addExpression(createSignMinusExpression());
    me.addExpression(createBitNotExpression());
    me.addExpression(createNotExpression());
    me.addExpression(createIncBeforeExpression());
    me.addExpression(createDecBeforeExpression());
    return me;
  }
  
  protected AbstractExpression createSignPlusExpression() {
    return new mcheli.eval.eval.exp.SignPlusExpression();
  }
  
  protected AbstractExpression createSignMinusExpression() {
    return new mcheli.eval.eval.exp.SignMinusExpression();
  }
  
  protected AbstractExpression createBitNotExpression() {
    return new BitNotExpression();
  }
  
  protected AbstractExpression createNotExpression() {
    return new mcheli.eval.eval.exp.NotExpression();
  }
  
  protected AbstractExpression createIncBeforeExpression() {
    return new IncBeforeExpression();
  }
  
  protected AbstractExpression createDecBeforeExpression() {
    return new mcheli.eval.eval.exp.DecBeforeExpression();
  }
  





  protected AbstractRule createPowerRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createPowerExpression());
    return me;
  }
  
  protected AbstractExpression createPowerExpression() {
    return new mcheli.eval.eval.exp.PowerExpression();
  }
  





  protected AbstractRule createCol1AfterRule(ShareRuleValue share)
  {
    Col1AfterRule me = new Col1AfterRule(share);
    me.addExpression(me.func = createFunctionExpression());
    me.addExpression(me.array = createArrayExpression());
    me.addExpression(createIncAfterExpression());
    me.addExpression(createDecAfterExpression());
    me.addExpression(me.field = createFieldExpression());
    return me;
  }
  
  protected AbstractExpression createFunctionExpression() {
    return new mcheli.eval.eval.exp.FunctionExpression();
  }
  
  protected AbstractExpression createArrayExpression() {
    return new mcheli.eval.eval.exp.ArrayExpression();
  }
  
  protected AbstractExpression createIncAfterExpression() {
    return new mcheli.eval.eval.exp.IncAfterExpression();
  }
  
  protected AbstractExpression createDecAfterExpression() {
    return new mcheli.eval.eval.exp.DecAfterExpression();
  }
  
  protected AbstractExpression createFieldExpression() {
    return new FieldExpression();
  }
  





  protected AbstractRule createPrimaryRule(ShareRuleValue share)
  {
    AbstractRule me = new mcheli.eval.eval.rule.PrimaryRule(share);
    me.addExpression(createParenExpression());
    

    return me;
  }
  
  protected AbstractExpression createParenExpression() {
    return new mcheli.eval.eval.exp.ParenExpression();
  }
  





  protected AbstractRule createFuncArgRule(ShareRuleValue share)
  {
    AbstractRule me = new Col2Rule(share);
    me.addExpression(createFuncArgExpression());
    return me;
  }
  
  protected AbstractExpression createFuncArgExpression() {
    return new mcheli.eval.eval.exp.FuncArgExpression();
  }
  

  protected LexFactory getLexFactory()
  {
    if (this.defaultLexFactory == null) {
      this.defaultLexFactory = new LexFactory();
    }
    return this.defaultLexFactory;
  }
}
