
//----------------------------------------------------
// The following code was generated by CUP v0.10k
// Sat Oct 18 02:15:55 CEST 2003
//----------------------------------------------------

package tum.umlsec.viki.tools.dynaviki.model.scanner;


/** CUP v0.10k generated parser.
  * @version Sat Oct 18 02:15:55 CEST 2003
  */
public class parser extends java_cup.runtime.lr_parser {

  /** Default constructor. */
  public parser() {super();}

  /** Constructor which sets the default scanner. */
  public parser(java_cup.runtime.Scanner s) {super(s);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\033\000\002\003\003\000\002\002\004\000\002\003" +
    "\003\000\002\003\003\000\002\006\003\000\002\006\003" +
    "\000\002\006\005\000\002\005\005\000\002\005\003\000" +
    "\002\012\003\000\002\012\003\000\002\007\010\000\002" +
    "\011\002\000\002\011\003\000\002\011\005\000\002\010" +
    "\005\000\002\004\005\000\002\004\005\000\002\004\006" +
    "\000\002\004\003\000\002\004\003\000\002\004\010\000" +
    "\002\004\006\000\002\004\006\000\002\004\006\000\002" +
    "\004\006\000\002\004\006" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\077\000\026\004\014\005\015\006\013\007\006\010" +
    "\010\011\016\012\017\013\020\022\007\027\012\001\002" +
    "\000\006\002\ufffc\021\ufffc\001\002\000\006\002\ufffd\021" +
    "\ufffd\001\002\000\004\022\077\001\002\000\024\004\014" +
    "\005\015\006\013\007\006\010\010\011\016\012\017\013" +
    "\035\022\007\001\002\000\004\022\072\001\002\000\004" +
    "\002\001\001\002\000\004\002\ufff9\001\002\000\004\022" +
    "\067\001\002\000\004\022\062\001\002\000\022\002\uffed" +
    "\016\uffed\017\uffed\020\uffed\021\uffed\023\uffed\024\uffed\026" +
    "\uffed\001\002\000\004\022\057\001\002\000\004\022\054" +
    "\001\002\000\020\002\uffee\015\052\016\uffee\017\uffee\020" +
    "\uffee\024\uffee\026\uffee\001\002\000\016\002\ufffe\016\047" +
    "\017\050\020\031\024\027\026\030\001\002\000\004\002" +
    "\045\001\002\000\006\002\uffff\021\024\001\002\000\024" +
    "\004\014\005\015\006\013\007\006\010\010\011\016\012" +
    "\017\013\020\022\007\001\002\000\010\020\031\024\027" +
    "\026\030\001\002\000\006\002\ufffb\021\024\001\002\000" +
    "\004\014\043\001\002\000\024\004\014\005\015\006\013" +
    "\007\006\010\010\011\016\012\017\013\035\022\007\001" +
    "\002\000\004\013\032\001\002\000\004\022\033\001\002" +
    "\000\026\004\014\005\015\006\013\007\006\010\010\011" +
    "\016\012\017\013\035\022\007\023\ufff5\001\002\000\004" +
    "\023\041\001\002\000\022\002\uffee\016\uffee\017\uffee\020" +
    "\uffee\021\uffee\023\uffee\024\uffee\026\uffee\001\002\000\012" +
    "\021\037\023\ufff4\024\027\026\030\001\002\000\026\004" +
    "\014\005\015\006\013\007\006\010\010\011\016\012\017" +
    "\013\035\022\007\023\ufff5\001\002\000\004\023\ufff3\001" +
    "\002\000\006\002\ufff6\021\ufff6\001\002\000\022\002\ufff0" +
    "\016\ufff0\017\ufff0\020\ufff0\021\ufff0\023\ufff0\024\ufff0\026" +
    "\ufff0\001\002\000\004\025\044\001\002\000\022\002\uffef" +
    "\016\uffef\017\uffef\020\uffef\021\uffef\023\uffef\024\uffef\026" +
    "\uffef\001\002\000\004\002\000\001\002\000\024\004\014" +
    "\005\015\006\013\007\006\010\010\011\016\012\017\013" +
    "\035\022\007\001\002\000\024\004\ufff8\005\ufff8\006\ufff8" +
    "\007\ufff8\010\ufff8\011\ufff8\012\ufff8\013\ufff8\022\ufff8\001" +
    "\002\000\024\004\ufff7\005\ufff7\006\ufff7\007\ufff7\010\ufff7" +
    "\011\ufff7\012\ufff7\013\ufff7\022\ufff7\001\002\000\010\002" +
    "\ufffa\024\027\026\030\001\002\000\024\004\014\005\015" +
    "\006\013\007\006\010\010\011\016\012\017\013\035\022" +
    "\007\001\002\000\012\002\ufff2\021\ufff2\024\027\026\030" +
    "\001\002\000\024\004\014\005\015\006\013\007\006\010" +
    "\010\011\016\012\017\013\035\022\007\001\002\000\010" +
    "\023\056\024\027\026\030\001\002\000\022\002\uffe7\016" +
    "\uffe7\017\uffe7\020\uffe7\021\uffe7\023\uffe7\024\uffe7\026\uffe7" +
    "\001\002\000\024\004\014\005\015\006\013\007\006\010" +
    "\010\011\016\012\017\013\035\022\007\001\002\000\010" +
    "\023\061\024\027\026\030\001\002\000\022\002\uffe8\016" +
    "\uffe8\017\uffe8\020\uffe8\021\uffe8\023\uffe8\024\uffe8\026\uffe8" +
    "\001\002\000\024\004\014\005\015\006\013\007\006\010" +
    "\010\011\016\012\017\013\035\022\007\001\002\000\010" +
    "\021\064\024\027\026\030\001\002\000\024\004\014\005" +
    "\015\006\013\007\006\010\010\011\016\012\017\013\035" +
    "\022\007\001\002\000\010\023\066\024\027\026\030\001" +
    "\002\000\022\002\uffec\016\uffec\017\uffec\020\uffec\021\uffec" +
    "\023\uffec\024\uffec\026\uffec\001\002\000\004\013\070\001" +
    "\002\000\004\023\071\001\002\000\022\002\uffeb\016\uffeb" +
    "\017\uffeb\020\uffeb\021\uffeb\023\uffeb\024\uffeb\026\uffeb\001" +
    "\002\000\024\004\014\005\015\006\013\007\006\010\010" +
    "\011\016\012\017\013\035\022\007\001\002\000\010\023" +
    "\074\024\027\026\030\001\002\000\022\002\uffe9\016\uffe9" +
    "\017\uffe9\020\uffe9\021\uffe9\023\uffe9\024\uffe9\026\uffe9\001" +
    "\002\000\010\023\076\024\027\026\030\001\002\000\022" +
    "\002\ufff1\016\ufff1\017\ufff1\020\ufff1\021\ufff1\023\ufff1\024" +
    "\ufff1\026\ufff1\001\002\000\024\004\014\005\015\006\013" +
    "\007\006\010\010\011\016\012\017\013\035\022\007\001" +
    "\002\000\010\023\101\024\027\026\030\001\002\000\022" +
    "\002\uffea\016\uffea\017\uffea\020\uffea\021\uffea\023\uffea\024" +
    "\uffea\026\uffea\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\077\000\016\003\021\004\020\005\010\006\022\007" +
    "\004\010\003\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\004\004\074\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\004\012\045\001\001" +
    "\000\002\001\001\000\002\001\001\000\012\004\024\006" +
    "\025\007\004\010\003\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\004\004\041\001\001\000" +
    "\002\001\001\000\002\001\001\000\006\004\035\011\033" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\006\004\035\011\037\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\004\004\050\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\004\004\052\001\001\000\002\001\001\000\004\004\054" +
    "\001\001\000\002\001\001\000\002\001\001\000\004\004" +
    "\057\001\001\000\002\001\001\000\002\001\001\000\004" +
    "\004\062\001\001\000\002\001\001\000\004\004\064\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\004\004\072\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\004\004\077\001\001\000\002\001" +
    "\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$parser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 1;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}




	public void unrecovered_syntax_error(java_cup.runtime.Symbol cur_token) {
		throw new tum.umlsec.viki.tools.dynaviki.model.ExceptionScannerSyntaxError("Unrecovered Syntax Error");
	}

	public void syntax_error(java_cup.runtime.Symbol cur_token) {
		throw new tum.umlsec.viki.tools.dynaviki.model.ExceptionScannerSyntaxError("Syntax Error");
	}



}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$parser$actions {
  private final parser parser;

  /** Constructor */
  CUP$parser$actions(parser parser) {
    this.parser = parser;
  }

  /** Method with the actual generated action code. */
  public final java_cup.runtime.Symbol CUP$parser$do_action(
    int                        CUP$parser$act_num,
    java_cup.runtime.lr_parser CUP$parser$parser,
    java.util.Stack            CUP$parser$stack,
    int                        CUP$parser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$parser$result;

      /* select the action based on the action number */
      switch (CUP$parser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 26: // expr ::= NONCEOF LPAR expr RPAR 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = new USE_NonceOf((USE_RTerm) e1); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 25: // expr ::= SYMMETRICKEYOF LPAR expr RPAR 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = new USE_SymmetricKeyOf((USE_RTerm) e1); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 24: // expr ::= SECRETKEYOF LPAR expr RPAR 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = new USE_SecretKeyOf((USE_RTerm) e1); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 23: // expr ::= PUBLICKEYOF LPAR expr RPAR 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = new USE_PublicKeyOf((USE_RTerm) e1); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 22: // expr ::= SENDEROF LPAR IDENTIFIER RPAR 
            {
              Object RESULT = null;
		int i1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int i1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		String i1 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = new USE_SenderOf(new USE_Variable(i1)); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 21: // expr ::= APPLYKEY LPAR expr COMMA expr RPAR 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-3)).value;
		int e2left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int e2right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object e2 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = new USE_ApplyKey((USE_RTerm)e1, (USE_RTerm)e2); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-5)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 20: // expr ::= THIS 
            {
              Object RESULT = null;
		 RESULT = new USE_This(); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 19: // expr ::= IDENTIFIER 
            {
              Object RESULT = null;
		int i1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int i1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		String i1 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = new USE_Variable(i1); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 18: // expr ::= expr LPARSQUARE INTNUMBER RPARSQUARE 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-3)).value;
		int i1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int i1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Integer i1 = (Integer)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = new USE_Select((USE_RTerm)e1, i1.intValue()); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 17: // expr ::= expr CONCAT expr 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int e2left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int e2right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object e2 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = new USE_Concatenation((USE_RTerm)e1, (USE_RTerm)e2); 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 16: // expr ::= LPAR expr RPAR 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = e1; 
              CUP$parser$result = new java_cup.runtime.Symbol(2/*expr*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 15: // assignment ::= IDENTIFIER ASSIGN expr 
            {
              Object RESULT = null;
		int i1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int i1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		String i1 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = new USE_Assignment(new USE_Variable(i1), (USE_RTerm)e1); 
              CUP$parser$result = new java_cup.runtime.Symbol(6/*assignment*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 14: // paramlist ::= expr COMMA paramlist 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int pl1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int pl1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object pl1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = new USE_ParameterListCont((USE_RTerm)e1, (USE_ParameterList)pl1); 
              CUP$parser$result = new java_cup.runtime.Symbol(7/*paramlist*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 13: // paramlist ::= expr 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = new USE_ParameterListEnd((USE_RTerm)e1); 
              CUP$parser$result = new java_cup.runtime.Symbol(7/*paramlist*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 12: // paramlist ::= 
            {
              Object RESULT = null;
		 RESULT = null; 
              CUP$parser$result = new java_cup.runtime.Symbol(7/*paramlist*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 11: // functioncall ::= expr OPCALL IDENTIFIER LPAR paramlist RPAR 
            {
              Object RESULT = null;
		int ct1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-5)).left;
		int ct1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-5)).right;
		Object ct1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-5)).value;
		int i1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left;
		int i1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).right;
		String i1 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-3)).value;
		int pl1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int pl1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object pl1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		 RESULT = new USE_FunctionCall((USE_RTerm)ct1, (String)i1, (USE_ParameterList)pl1); 
              CUP$parser$result = new java_cup.runtime.Symbol(5/*functioncall*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-5)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // compareop ::= NOTEQUAL 
            {
              Object RESULT = null;
		 RESULT = new USE_CompareOperatorNotEqual(); 
              CUP$parser$result = new java_cup.runtime.Symbol(8/*compareop*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // compareop ::= EQUAL 
            {
              Object RESULT = null;
		 RESULT = new USE_CompareOperatorEqual(); 
              CUP$parser$result = new java_cup.runtime.Symbol(8/*compareop*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // guard ::= ELSE 
            {
              Object RESULT = null;
		 RESULT = new USE_GuardElse(); 
              CUP$parser$result = new java_cup.runtime.Symbol(3/*guard*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // guard ::= expr compareop expr 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int co1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int co1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object co1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		int e2left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int e2right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object e2 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = new USE_Guard((USE_RTerm)e1, (USE_CompareOperator)co1, (USE_RTerm)e2); 
              CUP$parser$result = new java_cup.runtime.Symbol(3/*guard*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // effect ::= effect COMMA effect 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int e2left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int e2right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object e2 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = new USE_EffectList((USE_EffectBase)e1, (USE_EffectBase)e2); 
              CUP$parser$result = new java_cup.runtime.Symbol(4/*effect*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // effect ::= assignment 
            {
              Object RESULT = null;
		int a1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int a1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object a1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = a1; 
              CUP$parser$result = new java_cup.runtime.Symbol(4/*effect*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // effect ::= functioncall 
            {
              Object RESULT = null;
		int fc1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int fc1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object fc1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = fc1; 
              CUP$parser$result = new java_cup.runtime.Symbol(4/*effect*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // toplevel ::= expr 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = e1; 
              CUP$parser$result = new java_cup.runtime.Symbol(1/*toplevel*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // toplevel ::= effect 
            {
              Object RESULT = null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = e1; 
              CUP$parser$result = new java_cup.runtime.Symbol(1/*toplevel*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // $START ::= toplevel EOF 
            {
              Object RESULT = null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object start_val = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		RESULT = start_val;
              CUP$parser$result = new java_cup.runtime.Symbol(0/*$START*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          /* ACCEPT */
          CUP$parser$parser.done_parsing();
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // toplevel ::= guard 
            {
              Object RESULT = null;
		int g1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left;
		int g1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right;
		Object g1 = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-0)).value;
		 RESULT = g1; 
              CUP$parser$result = new java_cup.runtime.Symbol(1/*toplevel*/, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-0)).right, RESULT);
            }
          return CUP$parser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number found in internal parse table");

        }
    }
}
