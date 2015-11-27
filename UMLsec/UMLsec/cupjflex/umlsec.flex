package tum.umlsec.viki.tools.dynaviki.model.scanner;

import java_cup.runtime.*;

%%

%class Lexer
%public
%cup
%char


%{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, value);
  }
%}



WhiteSpace     = [\t\f\r\n ]

Identifier = [:jletter:] [:jletterdigit:]*
IntNumber = 0 | [1-9][0-9]*


/* %state STRING */

%%


<YYINITIAL> "ApplyKey"          { return symbol(sym.APPLYKEY); 		}
<YYINITIAL> "SenderOf"		{ return symbol(sym.SENDEROF); 		}
<YYINITIAL> "PublicKeyOf"	{ return symbol(sym.PUBLICKEYOF);	}
<YYINITIAL> "SecretKeyOf"	{ return symbol(sym.SECRETKEYOF);	}
<YYINITIAL> "SymmetricKeyOf"	{ return symbol(sym.SYMMETRICKEYOF);	}
<YYINITIAL> "NonceOf"		{ return symbol(sym.NONCEOF);		}
<YYINITIAL> "this"          	{ return symbol(sym.THIS); 		}
<YYINITIAL> "else"          	{ return symbol(sym.ELSE); 		}





<YYINITIAL> {
  /* identifiers */ 
  {Identifier}                  { return symbol(sym.IDENTIFIER, yytext()); 	}
  {IntNumber}                   { return symbol(sym.INTNUMBER, new Integer(yytext())); 	}
 
 
 
  /* operators */
  "="                           { return symbol(sym.ASSIGN); 		}
  "=="                          { return symbol(sym.EQUAL); 		}
  "!="				{ return symbol(sym.NOTEQUAL); 		}
  "."				{ return symbol(sym.OPCALL); 		}
  ","				{ return symbol(sym.COMMA); 		}
  "("				{ return symbol(sym.LPAR); 		}
  ")"				{ return symbol(sym.RPAR); 		}
  "["				{ return symbol(sym.LPARSQUARE);	}
  "]"				{ return symbol(sym.RPARSQUARE);	}
  "::"				{ return symbol(sym.CONCAT);		}
   
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}


/* error fallback */
.|\n                             { throw new Error("Illegal character <" + yytext() + ">"); }

