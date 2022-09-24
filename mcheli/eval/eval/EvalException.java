package mcheli.eval.eval;

import mcheli.eval.eval.lex.Lex;














































































public class EvalException
  extends RuntimeException
{
  private static final long serialVersionUID = 4174576689426433715L;
  public static final int PARSE_NOT_FOUND_END_OP = 1001;
  public static final int PARSE_INVALID_OP = 1002;
  public static final int PARSE_INVALID_CHAR = 1003;
  public static final int PARSE_END_OF_STR = 1004;
  public static final int PARSE_STILL_EXIST = 1005;
  public static final int PARSE_NOT_FUNC = 1101;
  public static final int EXP_FORBIDDEN_CALL = 2001;
  public static final int EXP_NOT_VARIABLE = 2002;
  public static final int EXP_NOT_NUMBER = 2003;
  public static final int EXP_NOT_LET = 2004;
  public static final int EXP_NOT_VAR_VALUE = 2101;
  public static final int EXP_NOT_LET_VAR = 2102;
  public static final int EXP_NOT_DEF_VAR = 2103;
  public static final int EXP_NOT_DEF_OBJ = 2104;
  public static final int EXP_NOT_ARR_VALUE = 2201;
  public static final int EXP_NOT_LET_ARR = 2202;
  public static final int EXP_NOT_FLD_VALUE = 2301;
  public static final int EXP_NOT_LET_FIELD = 2302;
  public static final int EXP_FUNC_CALL_ERROR = 2401;
  protected int msg_code;
  protected String[] msg_opt;
  protected String string;
  protected int pos = -1;
  




  protected String word;
  





  public EvalException(int msg, Lex lex)
  {
    this(msg, null, lex);
  }
  










  public EvalException(int msg, String[] opt, Lex lex)
  {
    this.msg_code = msg;
    this.msg_opt = opt;
    if (lex != null) {
      this.string = lex.getString();
      this.pos = lex.getPos();
      this.word = lex.getWord();
    }
  }
  













  public EvalException(int msg, String word, String string, int pos, Throwable e)
  {
    while ((e != null) && 
      (e.getClass() == RuntimeException.class) && (e.getCause() != null)) {
      e = e.getCause();
    }
    

    if (e != null)
      super.initCause(e);
    this.msg_code = msg;
    this.string = string;
    this.pos = pos;
    this.word = word;
  }
  





  public int getErrorCode()
  {
    return this.msg_code;
  }
  





  public String[] getOption()
  {
    return this.msg_opt;
  }
  





  public String getWord()
  {
    return this.word;
  }
  





  public String getString()
  {
    return this.string;
  }
  





  public int getPos()
  {
    return this.pos;
  }
  







  public static String getErrCodeMessage(int code)
  {
    switch (code) {
    case 1001: 
      return "????%0????????";
    case 1002: 
      return "????????????";
    case 1003: 
      return "??????????";
    case 1004: 
      return "????????????????????";
    case 1005: 
      return "???????????????????????";
    case 1101: 
      return "?????????????";
    case 2001: 
      return "????????????????????";
    case 2002: 
      return "?????????????";
    case 2003: 
      return "?????????????";
    case 2004: 
      return "????????";
    case 2101: 
      return "?????????????";
    case 2102: 
      return "???????????";
    case 2103: 
      return "?????????";
    case 2104: 
      return "?????????????";
    case 2201: 
      return "?????????????";
    case 2202: 
      return "???????????";
    case 2301: 
      return "????????????????";
    case 2302: 
      return "??????????????";
    case 2401: 
      return "???????????????";
    }
    return "???????????";
  }
  









  public String getDefaultFormat(String msgFmt)
  {
    StringBuffer fmt = new StringBuffer(128);
    fmt.append(msgFmt);
    
    boolean bWord = false;
    if ((this.word != null) && (this.word.length() > 0)) {
      bWord = true;
      if (this.word.equals(this.string))
        bWord = false;
    }
    if (bWord) {
      fmt.append(" word=?%w?");
    }
    
    if (this.pos >= 0) {
      fmt.append(" pos=%p");
    }
    if (this.string != null) {
      fmt.append(" string=?%s?");
    }
    if (getCause() != null) {
      fmt.append(" cause by %e");
    }
    
    return fmt.toString();
  }
  
  public String toString() {
    String msg = getErrCodeMessage(this.msg_code);
    String fmt = getDefaultFormat(msg);
    return toString(fmt);
  }
  







































  public String toString(String fmt)
  {
    StringBuffer sb = new StringBuffer(256);
    int len = fmt.length();
    for (int i = 0; i < len; i++) {
      char c = fmt.charAt(i);
      if (c != '%') {
        sb.append(c);
      } else {
        if (i + 1 >= len) {
          sb.append(c);
          break;
        }
        c = fmt.charAt(++i);
        switch (c) {
        case '0': 
        case '1': 
        case '2': 
        case '3': 
        case '4': 
        case '5': 
        case '6': 
        case '7': 
        case '8': 
        case '9': 
          int n = c - '0';
          if ((this.msg_opt != null) && (n < this.msg_opt.length)) {
            sb.append(this.msg_opt[n]);
          }
          break;
        case 'c': 
          sb.append(this.msg_code);
          break;
        case 'w': 
          sb.append(this.word);
          break;
        case 'p': 
          sb.append(this.pos);
          break;
        case 's': 
          sb.append(this.string);
          break;
        case 'e': 
          sb.append(getCause());
          break;
        case '%': 
          sb.append('%');
          break;
        default: 
          sb.append('%');
          sb.append(c);
        }
        
      }
    }
    return sb.toString();
  }
}
