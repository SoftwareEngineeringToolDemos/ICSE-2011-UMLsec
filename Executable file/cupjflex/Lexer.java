/* The following code was generated by JFlex 1.3.5 on 15/01/04 14:52 */

package tum.umlsec.viki.tools.dynaviki.model.scanner;

import java_cup.runtime.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.3.5
 * on 15/01/04 14:52 from the specification file
 * <tt>file:/F:/projects/da/viki/cupjflex/umlsec.flex</tt>
 */
public class Lexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  final public static int YYEOF = -1;

  /** initial size of the lookahead buffer */
  final private static int YY_BUFFERSIZE = 16384;

  /** lexical states */
  final public static int YYINITIAL = 0;

  /** 
   * Translates characters to character classes
   */
  final private static char [] yycmap = {
     3,  3,  3,  3,  3,  3,  3,  3,  3,  1,  1,  0,  1,  1,  3,  3, 
     3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  0,  0,  0,  0, 
     1, 30,  0,  0,  2,  0,  0,  0, 33, 34,  0,  0, 32,  0, 31,  0, 
     4,  5,  5,  5,  5,  5,  5,  5,  5,  5, 37,  0,  0, 29,  0,  0, 
     0,  6,  2,  2,  2,  2,  2,  2,  2,  2,  2, 10,  2,  2, 25, 16, 
    18,  2,  2, 12,  2,  2,  2,  2,  2,  2,  2, 35,  0, 36,  0,  2, 
     0,  2, 20, 22, 14, 11, 17,  2, 27, 21,  2,  2,  8, 24, 13, 26, 
     7,  2, 15, 28, 23, 19,  2,  2,  2,  9,  2,  0,  0,  0,  0,  3
  };

  /** 
   * Translates a state to a row index in the transition table
   */
  final private static int yy_rowMap [] = { 
        0,    38,    38,    76,    38,   114,   152,   190,   228,   266, 
      304,   342,   380,   418,    38,    38,    38,    38,    38,    38, 
      456,   494,   532,   570,   608,   646,   684,   722,    38,    38, 
       38,   760,   798,   836,   874,   912,   950,   988,  1026,  1064, 
       76,  1102,  1140,  1178,  1216,    76,  1254,  1292,  1330,  1368, 
     1406,  1444,  1482,  1520,  1558,  1596,  1634,  1672,  1710,  1748, 
     1786,  1824,  1862,  1900,    76,    76,  1938,    76,  1976,  2014, 
     2052,  2090,  2128,  2166,  2204,  2242,  2280,    76,    76,  2318, 
     2356,    76
  };

  /** 
   * The packed transition table of the DFA (part 0)
   */
  final private static String yy_packed0 = 
    "\1\2\1\3\1\4\1\2\1\5\1\6\1\7\4\4"+
    "\1\10\1\11\5\4\1\12\4\4\1\13\1\4\1\14"+
    "\3\4\1\15\1\16\1\17\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\50\0\33\4\15\0\2\6\42\0\5\4"+
    "\1\26\25\4\13\0\6\4\1\27\24\4\13\0\7\4"+
    "\1\30\1\4\1\31\21\4\13\0\21\4\1\32\11\4"+
    "\13\0\31\4\1\33\1\4\13\0\30\4\1\34\2\4"+
    "\46\0\1\35\45\0\1\36\55\0\1\37\2\0\5\4"+
    "\1\40\25\4\13\0\32\4\1\41\13\0\26\4\1\42"+
    "\4\4\13\0\13\4\1\43\10\4\1\44\6\4\13\0"+
    "\22\4\1\45\10\4\13\0\23\4\1\46\7\4\13\0"+
    "\13\4\1\47\17\4\13\0\6\4\1\50\24\4\13\0"+
    "\11\4\1\51\21\4\13\0\26\4\1\52\4\4\13\0"+
    "\14\4\1\53\16\4\13\0\15\4\1\54\15\4\13\0"+
    "\6\4\1\55\24\4\13\0\32\4\1\56\13\0\24\4"+
    "\1\57\6\4\13\0\7\4\1\60\23\4\13\0\11\4"+
    "\1\61\21\4\13\0\11\4\1\62\21\4\13\0\11\4"+
    "\1\63\21\4\13\0\23\4\1\64\7\4\13\0\11\4"+
    "\1\65\21\4\13\0\10\4\1\66\22\4\13\0\25\4"+
    "\1\67\5\4\13\0\15\4\1\70\15\4\13\0\25\4"+
    "\1\71\5\4\13\0\24\4\1\72\6\4\13\0\16\4"+
    "\1\73\14\4\13\0\11\4\1\74\21\4\13\0\15\4"+
    "\1\75\15\4\13\0\16\4\1\76\14\4\13\0\10\4"+
    "\1\77\22\4\13\0\10\4\1\100\22\4\13\0\17\4"+
    "\1\101\13\4\13\0\7\4\1\102\23\4\13\0\23\4"+
    "\1\103\7\4\13\0\17\4\1\104\13\4\13\0\11\4"+
    "\1\105\21\4\13\0\11\4\1\106\21\4\13\0\24\4"+
    "\1\107\6\4\13\0\7\4\1\110\23\4\13\0\7\4"+
    "\1\111\23\4\13\0\10\4\1\112\22\4\13\0\16\4"+
    "\1\113\14\4\13\0\16\4\1\114\14\4\13\0\11\4"+
    "\1\115\21\4\13\0\17\4\1\116\13\4\13\0\17\4"+
    "\1\117\13\4\13\0\7\4\1\120\23\4\13\0\16\4"+
    "\1\121\14\4\13\0\17\4\1\122\13\4\11\0";

  /** 
   * The transition table of the DFA
   */
  final private static int yytrans [] = yy_unpack();


  /* error codes */
  final private static int YY_UNKNOWN_ERROR = 0;
  final private static int YY_ILLEGAL_STATE = 1;
  final private static int YY_NO_MATCH = 2;
  final private static int YY_PUSHBACK_2BIG = 3;

  /* error messages for the codes above */
  final private static String YY_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Internal error: unknown state",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * YY_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private final static byte YY_ATTRIBUTE[] = {
     0,  9,  9,  1,  9,  1,  1,  1,  1,  1,  1,  1,  1,  1,  9,  9, 
     9,  9,  9,  9,  1,  1,  1,  1,  1,  1,  1,  1,  9,  9,  9,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1
  };

  /** the input device */
  private java.io.Reader yy_reader;

  /** the current state of the DFA */
  private int yy_state;

  /** the current lexical state */
  private int yy_lexical_state = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char yy_buffer[] = new char[YY_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int yy_markedPos;

  /** the textposition at the last state to be included in yytext */
  private int yy_pushbackPos;

  /** the current text position in the buffer */
  private int yy_currentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int yy_startRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int yy_endRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn; 

  /** 
   * yy_atBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean yy_atBOL = true;

  /** yy_atEOF == true <=> the scanner is at the EOF */
  private boolean yy_atEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean yy_eof_done;

  /* user code: */
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, value);
  }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer(java.io.Reader in) {
    this.yy_reader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Lexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the split, compressed DFA transition table.
   *
   * @return the unpacked transition table
   */
  private static int [] yy_unpack() {
    int [] trans = new int[2394];
    int offset = 0;
    offset = yy_unpack(yy_packed0, offset, trans);
    return trans;
  }

  /** 
   * Unpacks the compressed DFA transition table.
   *
   * @param packed   the packed transition table
   * @return         the index of the last entry
   */
  private static int yy_unpack(String packed, int offset, int [] trans) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do trans[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   IOException  if any I/O-Error occurs
   */
  private boolean yy_refill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (yy_startRead > 0) {
      System.arraycopy(yy_buffer, yy_startRead, 
                       yy_buffer, 0, 
                       yy_endRead-yy_startRead);

      /* translate stored positions */
      yy_endRead-= yy_startRead;
      yy_currentPos-= yy_startRead;
      yy_markedPos-= yy_startRead;
      yy_pushbackPos-= yy_startRead;
      yy_startRead = 0;
    }

    /* is the buffer big enough? */
    if (yy_currentPos >= yy_buffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[yy_currentPos*2];
      System.arraycopy(yy_buffer, 0, newBuffer, 0, yy_buffer.length);
      yy_buffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = yy_reader.read(yy_buffer, yy_endRead, 
                                            yy_buffer.length-yy_endRead);

    if (numRead < 0) {
      return true;
    }
    else {
      yy_endRead+= numRead;  
      return false;
    }
  }


  /**
   * Closes the input stream.
   */
  final public void yyclose() throws java.io.IOException {
    yy_atEOF = true;            /* indicate end of file */
    yy_endRead = yy_startRead;  /* invalidate buffer    */

    if (yy_reader != null)
      yy_reader.close();
  }


  /**
   * Closes the current stream, and resets the
   * scanner to read from a new input stream.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>YY_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  final public void yyreset(java.io.Reader reader) throws java.io.IOException {
    yyclose();
    yy_reader = reader;
    yy_atBOL  = true;
    yy_atEOF  = false;
    yy_endRead = yy_startRead = 0;
    yy_currentPos = yy_markedPos = yy_pushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    yy_lexical_state = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  final public int yystate() {
    return yy_lexical_state;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  final public void yybegin(int newState) {
    yy_lexical_state = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  final public String yytext() {
    return new String( yy_buffer, yy_startRead, yy_markedPos-yy_startRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  final public char yycharat(int pos) {
    return yy_buffer[yy_startRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  final public int yylength() {
    return yy_markedPos-yy_startRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void yy_ScanError(int errorCode) {
    String message;
    try {
      message = YY_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = YY_ERROR_MSG[YY_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  private void yypushback(int number)  {
    if ( number > yylength() )
      yy_ScanError(YY_PUSHBACK_2BIG);

    yy_markedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void yy_do_eof() throws java.io.IOException {
    if (!yy_eof_done) {
      yy_eof_done = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int yy_input;
    int yy_action;

    // cached fields:
    int yy_currentPos_l;
    int yy_startRead_l;
    int yy_markedPos_l;
    int yy_endRead_l = yy_endRead;
    char [] yy_buffer_l = yy_buffer;
    char [] yycmap_l = yycmap;

    int [] yytrans_l = yytrans;
    int [] yy_rowMap_l = yy_rowMap;
    byte [] yy_attr_l = YY_ATTRIBUTE;

    while (true) {
      yy_markedPos_l = yy_markedPos;

      yychar+= yy_markedPos_l-yy_startRead;

      yy_action = -1;

      yy_startRead_l = yy_currentPos_l = yy_currentPos = 
                       yy_startRead = yy_markedPos_l;

      yy_state = yy_lexical_state;


      yy_forAction: {
        while (true) {

          if (yy_currentPos_l < yy_endRead_l)
            yy_input = yy_buffer_l[yy_currentPos_l++];
          else if (yy_atEOF) {
            yy_input = YYEOF;
            break yy_forAction;
          }
          else {
            // store back cached positions
            yy_currentPos  = yy_currentPos_l;
            yy_markedPos   = yy_markedPos_l;
            boolean eof = yy_refill();
            // get translated positions and possibly new buffer
            yy_currentPos_l  = yy_currentPos;
            yy_markedPos_l   = yy_markedPos;
            yy_buffer_l      = yy_buffer;
            yy_endRead_l     = yy_endRead;
            if (eof) {
              yy_input = YYEOF;
              break yy_forAction;
            }
            else {
              yy_input = yy_buffer_l[yy_currentPos_l++];
            }
          }
          int yy_next = yytrans_l[ yy_rowMap_l[yy_state] + yycmap_l[yy_input] ];
          if (yy_next == -1) break yy_forAction;
          yy_state = yy_next;

          int yy_attributes = yy_attr_l[yy_state];
          if ( (yy_attributes & 1) == 1 ) {
            yy_action = yy_state; 
            yy_markedPos_l = yy_currentPos_l; 
            if ( (yy_attributes & 8) == 8 ) break yy_forAction;
          }

        }
      }

      // store back cached position
      yy_markedPos = yy_markedPos_l;

      switch (yy_action) {

        case 81: 
          {  return symbol(sym.SYMMETRICKEYOF);	 }
        case 83: break;
        case 16: 
          {  return symbol(sym.LPAR); 		 }
        case 84: break;
        case 15: 
          {  return symbol(sym.COMMA); 		 }
        case 85: break;
        case 17: 
          {  return symbol(sym.RPAR); 		 }
        case 86: break;
        case 28: 
          {  return symbol(sym.EQUAL); 		 }
        case 87: break;
        case 30: 
          {  return symbol(sym.CONCAT);		 }
        case 88: break;
        case 40: 
          {  return symbol(sym.ELSE); 		 }
        case 89: break;
        case 45: 
          {  return symbol(sym.THIS); 		 }
        case 90: break;
        case 4: 
        case 5: 
          {  return symbol(sym.INTNUMBER, new Integer(yytext())); 	 }
        case 91: break;
        case 78: 
          {  return symbol(sym.PUBLICKEYOF);	 }
        case 92: break;
        case 77: 
          {  return symbol(sym.SECRETKEYOF);	 }
        case 93: break;
        case 67: 
          {  return symbol(sym.SENDEROF); 		 }
        case 94: break;
        case 65: 
          {  return symbol(sym.APPLYKEY); 		 }
        case 95: break;
        case 18: 
          {  return symbol(sym.LPARSQUARE);	 }
        case 96: break;
        case 19: 
          {  return symbol(sym.RPARSQUARE);	 }
        case 97: break;
        case 29: 
          {  return symbol(sym.NOTEQUAL); 		 }
        case 98: break;
        case 3: 
        case 6: 
        case 7: 
        case 8: 
        case 9: 
        case 10: 
        case 11: 
        case 21: 
        case 22: 
        case 23: 
        case 24: 
        case 25: 
        case 26: 
        case 27: 
        case 31: 
        case 32: 
        case 33: 
        case 34: 
        case 35: 
        case 36: 
        case 37: 
        case 38: 
        case 39: 
        case 41: 
        case 42: 
        case 43: 
        case 44: 
        case 46: 
        case 47: 
        case 48: 
        case 49: 
        case 50: 
        case 51: 
        case 52: 
        case 53: 
        case 54: 
        case 55: 
        case 56: 
        case 57: 
        case 58: 
        case 59: 
        case 60: 
        case 61: 
        case 62: 
        case 63: 
        case 66: 
        case 68: 
        case 69: 
        case 70: 
        case 71: 
        case 72: 
        case 73: 
        case 74: 
        case 75: 
        case 76: 
        case 79: 
        case 80: 
          {  return symbol(sym.IDENTIFIER, yytext()); 	 }
        case 99: break;
        case 14: 
          {  return symbol(sym.OPCALL); 		 }
        case 100: break;
        case 12: 
          {  return symbol(sym.ASSIGN); 		 }
        case 101: break;
        case 64: 
          {  return symbol(sym.NONCEOF);		 }
        case 102: break;
        case 1: 
        case 13: 
        case 20: 
          {  throw new Error("Illegal character <" + yytext() + ">");  }
        case 103: break;
        case 2: 
          {  /* ignore */  }
        case 104: break;
        default: 
          if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
            yy_atEOF = true;
            yy_do_eof();
              { return new java_cup.runtime.Symbol(sym.EOF); }
          } 
          else {
            yy_ScanError(YY_NO_MATCH);
          }
      }
    }
  }


}
