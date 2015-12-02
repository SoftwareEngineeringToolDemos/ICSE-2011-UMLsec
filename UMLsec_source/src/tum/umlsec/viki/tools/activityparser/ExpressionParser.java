/**
 * Expression parser for conditions and assignments of integer variables in UML models:
 *  - variable names can be used as supported in Java
 *  - the following operators can be used in both conditions and assignments:
 *     + : addition
 *     - : subtraction
 *     * : multiplication 
 *     / : division
 *     % : modulo
 *     (): parenthesis
 *  - the following operators can only be used in assignments:
 *     = : current assignment
 *     ; : separation of the assignments
 *  - the following operators can only be used in conditions:
 *     ! : negation
 *     &&: logical AND
 *     ||: logical OR
 *     ==: equality (not possible for boolean values)
 *     !=: inequality (not possible for boolean values)
 *     < : checking if lower
 *     <=: checking if lower or equal
 *     > : checking if greater
 *     >=: checking if greater or equal
 *  - for the priority of the operators see Java
 *  - syntactical correctness is not checked
 */

package tum.umlsec.viki.tools.activityparser;

import java.util.Enumeration;
import java.util.Hashtable;

import tum.umlsec.viki.framework.ITextOutput;

public class ExpressionParser {

	// hashtable for values of variables
	private Hashtable vars;

	public ExpressionParser() {
		vars = new Hashtable();
	}

	public ExpressionParser(ExpressionParser e) {
		vars = new Hashtable(e.vars);
	}

	// method to check if two expression parser are equal
	public boolean isEqual(ExpressionParser e) {
		Enumeration e1 = vars.keys();
		Enumeration e2 = e.vars.keys();
		while (e1.hasMoreElements()) {
			if (e2.hasMoreElements() == false)
				return (false);
			e1.nextElement();
			e2.nextElement();
		}
		if (e2.hasMoreElements() == true)
			return (false);
		e1 = vars.keys();
		while (e1.hasMoreElements()) {
			String st = (String) e1.nextElement();
			if (e.vars.get(st) == null)
				return (false);
			if (vars.get(st).equals(e.vars.get(st)) == false)
				return (false);
		}
		return (true);
	}

	// method to evaluate an expression
	// expression: contains a valid expression
	public String getValue(String expression) {
		StringBuffer expr = new StringBuffer(expression);
		// deleting ' '
		while (expr.indexOf(" ") != -1) {
			expr.deleteCharAt(expr.indexOf(" "));
		}
		replaceVars(expr);
		reduceSigns(expr);
		changeSigns(expr);
		parenthesisa(expr);
		return (expr.toString());
	}

	// method to assign values to variables
	// assignment: contains the names of the variables to be assigned, '=' and
	// valid expressions separated by ';'
	public void assignment(String assignment) {
		StringBuffer assignbg = new StringBuffer(assignment);
		// processing the assignments
		while (assignbg.length() > 0) {
			StringBuffer assignb;
			// checking for other assignments
			if (assignbg.indexOf(";") != -1) {
				assignb = new StringBuffer(assignbg.substring(0,
						assignbg.indexOf(";")));
				assignbg.delete(0, assignbg.indexOf(";") + 1);
			} else {
				assignb = new StringBuffer(assignbg.toString());
				assignbg.delete(0, assignbg.length());
			}
			// checking the syntax of the assignments
			if (assignb.indexOf("=") != -1) {
				while (assignb.indexOf(" ") != -1) {
					assignb.deleteCharAt(assignb.indexOf(" "));
				}
				String var = assignb.substring(0, assignb.indexOf("="));
				StringBuffer expr = new StringBuffer(assignb.substring(assignb
						.indexOf("=") + 1));
				replaceVars(expr);
				reduceSigns(expr);
				changeSigns(expr);
				parenthesisa(expr);
				vars.put(var, expr.toString());
			}
		}
	}

	// method to check for conditions
	// condition: expression with boolean value
	public boolean condition(String condition) {
		StringBuffer expr = new StringBuffer(condition);
		// deleting ' '
		while (expr.indexOf(" ") != -1) {
			expr.deleteCharAt(expr.indexOf(" "));
		}
		replaceVars(expr);
		reduceSigns(expr);
		changeSigns(expr);
		parenthesisc(expr);
		if (expr.toString().equals("t"))
			return true;
		else
			return false;
	}

	// method to print the values of the variables
	public void printVars(ITextOutput _textOutput) {
		Enumeration e = vars.keys();
		// checking for other variables
		while (e.hasMoreElements() == true) {
			String s = (String) e.nextElement();
			_textOutput.write(s);
			_textOutput.write(" = ");
			_textOutput.write((String) vars.get(s));
			_textOutput.write("; ");
		}
	}

	// method to replace variables with their values
	// expression: contains a valid expression
	private void replaceVars(StringBuffer expression) {
		// variable indicating if current position is in-between a name of a
		// variable
		boolean atVar = false;
		// variable indicating if current position is last possible position
		boolean atEnd = false;
		// variable containing current position
		int n = 0;
		// variable containing position of first character of the current
		// variable
		int vstart = 0;
		while (atEnd == false) {
			if (atVar == false) {
				if (n == expression.length())
					atEnd = true;
				else {
					if (Character.isJavaIdentifierStart(expression.charAt(n)) == true) {
						atVar = true;
						vstart = n;
						n++;
					} else
						n++;
				}
			} else {
				if (n == expression.length()) {
					atEnd = true;
					String tvar = expression.substring(vstart, n);
					if (tvar.equals("true"))
						expression.replace(vstart, n, "t");
					else if (tvar.equals("false"))
						expression.replace(vstart, n, "f");
					else if (vars.containsKey(tvar)) {
						String nvar = (String) vars.get(tvar);
						expression.replace(vstart, n, nvar);
					}
				} else if (Character.isJavaIdentifierPart(expression.charAt(n)) == false) {
					atVar = false;
					String tvar = expression.substring(vstart, n);
					if (tvar.equals("true")) {
						expression.replace(vstart, n, "t");
						n = n - tvar.length() + 1;
					} else if (tvar.equals("false")) {
						expression.replace(vstart, n, "f");
						n = n - tvar.length() + 1;
					} else if (vars.containsKey(tvar)) {
						String nvar = (String) vars.get(tvar);
						expression.replace(vstart, n, nvar);
						n = n - tvar.length() + nvar.length();
					}
				} else
					n++;
			}
		}
	}

	// method to eliminate surplus '+' and '-'
	// expression: contains a temporary expression
	private void reduceSigns(StringBuffer expression) {
		// variable containing current position
		int n = 0;
		// variable indicating if current position is in-between a series of '+'
		// and '-'
		boolean atSign = false;
		// variable indicating if current result would be '+' or '-'
		int sign = 1;
		// variable containing first '+' or '-' of series
		int fsign = 0;
		while (n < expression.length()) {
			if (atSign == false) {
				if ((expression.charAt(n) == '+')
						|| (expression.charAt(n) == '-')) {
					atSign = true;
					fsign = n;
					if (expression.charAt(n) == '-')
						sign = -1;
					else
						sign = 1;
				}
				n++;
			} else {
				if ((expression.charAt(n) == '+')
						|| (expression.charAt(n) == '-')) {
					if (expression.charAt(n) == '-')
						sign = sign * (-1);
				} else {
					String sig;
					if ((n - fsign) > 1) {
						if (sign < 0)
							sig = "-";
						else
							sig = "+";
						expression.replace(fsign, n, sig);
						n = fsign + 1;
					}
					atSign = false;
				}
				n++;
			}
		}
	}

	// method to simplify further processing of the expression
	// expression: contains a temporary expression
	private void changeSigns(StringBuffer expression) {
		// variable containing current position
		int n = 0;
		while (n < expression.length()) {
			// replacing '+'/'-' following digits with 'p'/'m'
			// (addition/subtraction)
			if (Character.isDigit(expression.charAt(n))) {
				if (n < expression.length() - 1)
					if (expression.charAt(n + 1) == '+') {
						expression.setCharAt(n + 1, 'p');
						n++;
					} else if (expression.charAt(n + 1) == '-') {
						expression.setCharAt(n + 1, 'm');
						n++;
					}
			}
			// replacing "<=" with "l" (less or equal)
			else if (expression.charAt(n) == '<') {
				if (n < expression.length() - 1)
					if (expression.charAt(n + 1) == '=')
						expression.replace(n, n + 2, "l");
			}
			// replacing ">=" with "g" (greater or equal)
			else if (expression.charAt(n) == '>') {
				if (n < expression.length() - 1)
					if (expression.charAt(n + 1) == '=')
						expression.replace(n, n + 2, "g");
			}
			// replacing "!=" with "u" (unequal)
			else if (expression.charAt(n) == '!') {
				if (n < expression.length() - 1)
					if (expression.charAt(n + 1) == '=')
						expression.replace(n, n + 2, "u");
			}
			// replacing "==" with "e" (equal)
			else if (expression.charAt(n) == '=') {
				if (n < expression.length() - 1)
					if (expression.charAt(n + 1) == '=')
						expression.replace(n, n + 2, "e");
			}
			// replacing "&&" with "a" (logical and)
			else if (expression.charAt(n) == '&') {
				if (n < expression.length() - 1)
					if (expression.charAt(n + 1) == '&')
						expression.replace(n, n + 2, "a");
			}
			// replacing "||" with "o" (logical or)
			else if (expression.charAt(n) == '|') {
				if (n < expression.length() - 1)
					if (expression.charAt(n + 1) == '|')
						expression.replace(n, n + 2, "o");
			}
			// replacing '+'/'-' following ')' with 'p'/'m'
			// (addition/subtraction)
			else if (expression.charAt(n) == ')') {
				if (n < expression.length() - 1)
					if (expression.charAt(n + 1) == '+') {
						expression.setCharAt(n + 1, 'p');
						n++;
					} else if (expression.charAt(n + 1) == '-') {
						expression.setCharAt(n + 1, 'm');
						n++;
					}
			}
			n++;
		}
	}

	// method to evaluate '!' (negation)
	// expression: contains a temporary expression
	private void eval1(StringBuffer expression) {
		for (int n = 0; n + 1 < expression.length(); n++) {
			if (expression.charAt(n) == '!') {
				if (expression.charAt(n + 1) == 't')
					expression.replace(n, n + 2, "f");
				else if (expression.charAt(n + 1) == 'f')
					expression.replace(n, n + 2, "t");
			}
		}
	}

	// method to evaluate '*', '/' and '%' (multiplication, division and modulo)
	// expression: contains a temporary expression
	private void eval2(StringBuffer expression) {
		// variable containing current position
		int n = 0;
		// variable containing position of first digit
		int fdigit = 0;
		while (n < expression.length()) {
			if (Character.isDigit(expression.charAt(n)) == false) {
				if ((expression.charAt(n) == '*')
						|| (expression.charAt(n) == '/')
						|| (expression.charAt(n) == '%')) {
					int farg = Integer
							.parseInt(expression.substring(fdigit, n));
					int m = n + 1;
					if ((expression.charAt(m) == '+')
							|| (expression.charAt(m) == '-'))
						m++;
					boolean edigit = false;
					while (edigit == false) {
						if (m == expression.length())
							edigit = true;
						else {
							if (Character.isDigit(expression.charAt(m)) == false)
								edigit = true;
							else
								m++;
						}
					}
					int sarg = Integer.parseInt(expression.substring(n + 1, m));
					int res = 0;
					if (expression.charAt(n) == '*')
						res = farg * sarg;
					else if (expression.charAt(n) == '/')
						res = farg / sarg;
					else if (expression.charAt(n) == '%')
						res = farg % sarg;
					if (res <= 0) {
						if (fdigit > 0) {
							if (expression.charAt(fdigit - 1) == '+')
								fdigit--;
							else if (expression.charAt(fdigit - 1) == '-') {
								fdigit--;
								res = res * (-1);
							}
						}
					}
					expression.replace(fdigit, m, Integer.toString(res));
					n = fdigit + Integer.toString(res).length();
					if (res < 0)
						fdigit++;
				} else {
					fdigit = n + 1;
					n++;
				}
			} else
				n++;
		}
	}

	// method to evaluate 'p' and 'm' (addition and subtraction)
	// expression: contains a temporary expression
	private void eval3(StringBuffer expression) {
		// variable containing current position
		int n = 0;
		// variable containing position of first digit
		int fdigit = 0;
		while (n < expression.length()) {
			if (Character.isDigit(expression.charAt(n)) == false) {
				if ((expression.charAt(n) == 'p')
						|| (expression.charAt(n) == 'm')) {
					if (fdigit > 0)
						if (expression.charAt(fdigit - 1) == '-')
							fdigit--;
					int farg = Integer
							.parseInt(expression.substring(fdigit, n));
					int m = n + 1;
					if ((expression.charAt(m) == '+')
							|| (expression.charAt(m) == '-'))
						m++;
					boolean edigit = false;
					while (edigit == false) {
						if (m == expression.length())
							edigit = true;
						else {
							if (Character.isDigit(expression.charAt(m)) == false)
								edigit = true;
							else
								m++;
						}
					}
					int sarg = Integer.parseInt(expression.substring(n + 1, m));
					int res = 0;
					if (expression.charAt(n) == 'p')
						res = farg + sarg;
					else if (expression.charAt(n) == 'm')
						res = farg - sarg;
					if ((fdigit > 0) && (farg > 0))
						if (expression.charAt(fdigit - 1) == '+')
							fdigit--;
					expression.replace(fdigit, m, Integer.toString(res));
					n = fdigit + Integer.toString(res).length();
					if (res < 0)
						fdigit++;
				} else {
					fdigit = n + 1;
					n++;
				}
			} else
				n++;
		}
	}

	// method to evaluate '<', 'l', '>' and 'g' (less than, less or equal,
	// greater than and greater or equal)
	// expression: contains a temporary expression
	private void eval4(StringBuffer expression) {
		// variable containing current position
		int n = 0;
		// variable containing position of first digit
		int fdigit = 0;
		while (n < expression.length()) {
			if (Character.isDigit(expression.charAt(n)) == false) {
				if ((expression.charAt(n) == '<')
						|| (expression.charAt(n) == 'l')
						|| (expression.charAt(n) == '>')
						|| (expression.charAt(n) == 'g')) {
					if (fdigit > 0)
						if (expression.charAt(fdigit - 1) == '-')
							fdigit--;
					int farg = Integer
							.parseInt(expression.substring(fdigit, n));
					int m = n + 1;
					if ((expression.charAt(m) == '+')
							|| (expression.charAt(m) == '-'))
						m++;
					boolean edigit = false;
					while (edigit == false) {
						if (m == expression.length())
							edigit = true;
						else {
							if (Character.isDigit(expression.charAt(m)) == false)
								edigit = true;
							else
								m++;
						}
					}
					int sarg = Integer.parseInt(expression.substring(n + 1, m));
					boolean res = true;
					if (expression.charAt(n) == '<')
						res = (farg < sarg);
					else if (expression.charAt(n) == 'l')
						res = (farg <= sarg);
					else if (expression.charAt(n) == '>')
						res = (farg > sarg);
					else if (expression.charAt(n) == 'g')
						res = (farg >= sarg);
					if ((fdigit > 0) && (farg > 0))
						if (expression.charAt(fdigit - 1) == '+')
							fdigit--;
					if (res == true)
						expression.replace(fdigit, m, "t");
					else
						expression.replace(fdigit, m, "f");
					n = fdigit + 1;
				} else {
					fdigit = n + 1;
					n++;
				}
			} else
				n++;
		}
	}

	// method to evaluate 'e' and 'u' (equal and unequal)
	// expression: contains a temporary expression
	private void eval5(StringBuffer expression) {
		// variable containing current position
		int n = 0;
		// variable containing position of first digit
		int fdigit = 0;
		while (n < expression.length()) {
			if (Character.isDigit(expression.charAt(n)) == false) {
				if ((expression.charAt(n) == 'e')
						|| (expression.charAt(n) == 'u')) {
					if (fdigit > 0)
						if (expression.charAt(fdigit - 1) == '-')
							fdigit--;
					int farg = Integer
							.parseInt(expression.substring(fdigit, n));
					int m = n + 1;
					if ((expression.charAt(m) == '+')
							|| (expression.charAt(m) == '-'))
						m++;
					boolean edigit = false;
					while (edigit == false) {
						if (m == expression.length())
							edigit = true;
						else {
							if (Character.isDigit(expression.charAt(m)) == false)
								edigit = true;
							else
								m++;
						}
					}
					int sarg = Integer.parseInt(expression.substring(n + 1, m));
					boolean res = true;
					if (expression.charAt(n) == 'e')
						res = (farg == sarg);
					else if (expression.charAt(n) == 'u')
						res = (farg != sarg);
					if ((fdigit > 0) && (farg > 0))
						if (expression.charAt(fdigit - 1) == '+')
							fdigit--;
					if (res == true)
						expression.replace(fdigit, m, "t");
					else
						expression.replace(fdigit, m, "f");
					n = fdigit + 1;
				} else {
					fdigit = n + 1;
					n++;
				}
			} else
				n++;
		}
	}

	// method to evaluate 'a' (logical and)
	// expression: contains a temporary expression
	private void eval6(StringBuffer expression) {
		for (int n = 1; n + 1 < expression.length(); n++) {
			if (expression.charAt(n) == 'a') {
				if ((expression.charAt(n - 1) == 't')
						&& (expression.charAt(n + 1) == 't')) {
					expression.replace(n - 1, n + 2, "t");
					n--;
				} else if ((expression.charAt(n - 1) == 't')
						&& (expression.charAt(n + 1) == 'f')) {
					expression.replace(n - 1, n + 2, "f");
					n--;
				} else if ((expression.charAt(n - 1) == 'f')
						&& (expression.charAt(n + 1) == 't')) {
					expression.replace(n - 1, n + 2, "f");
					n--;
				} else if ((expression.charAt(n - 1) == 'f')
						&& (expression.charAt(n + 1) == 'f')) {
					expression.replace(n - 1, n + 2, "f");
					n--;
				}
			}
		}
	}

	// method to evaluate 'o' (logical or)
	// expression: contains a temporary expression
	private void eval7(StringBuffer expression) {
		for (int n = 1; n + 1 < expression.length(); n++) {
			if (expression.charAt(n) == 'o') {
				if ((expression.charAt(n - 1) == 't')
						&& (expression.charAt(n + 1) == 't')) {
					expression.replace(n - 1, n + 2, "t");
					n--;
				} else if ((expression.charAt(n - 1) == 't')
						&& (expression.charAt(n + 1) == 'f')) {
					expression.replace(n - 1, n + 2, "t");
					n--;
				} else if ((expression.charAt(n - 1) == 'f')
						&& (expression.charAt(n + 1) == 't')) {
					expression.replace(n - 1, n + 2, "t");
					n--;
				} else if ((expression.charAt(n - 1) == 'f')
						&& (expression.charAt(n + 1) == 'f')) {
					expression.replace(n - 1, n + 2, "f");
					n--;
				}
			}
		}
	}

	// method to evaluate parenthesis for assignments (evaluate the innermost
	// expression until all parenthesis are resolved)
	// expression: contains a temporary expression
	private void parenthesisa(StringBuffer expression) {
		boolean op = true;
		// checking for other parenthesis
		while (op == true) {
			int openp = 0;
			int ip = expression.indexOf("(");
			if (ip == -1) {
				eval2(expression);
				eval3(expression);
				op = false;
			} else {
				int gop = 1;
				int n = ip + 1;
				openp = 1;
				while (n < expression.length()) {
					if (expression.charAt(n) == '(') {
						openp++;
						if (openp > gop) {
							ip = n;
							gop = openp;
						}
					} else if (expression.charAt(n) == ')')
						openp--;
					n++;
				}
				int np = expression.indexOf(")", ip + 1);
				StringBuffer pexpr = new StringBuffer(expression.substring(
						ip + 1, np));
				eval2(pexpr);
				eval3(pexpr);
				if (pexpr.charAt(0) == '-') {
					if (ip > 0) {
						if (expression.charAt(ip - 1) == '-') {
							pexpr.setCharAt(0, '+');
							ip--;
						} else if (expression.charAt(ip - 1) == 'm') {
							pexpr.setCharAt(0, 'p');
							ip--;
						} else if (expression.charAt(ip - 1) == '+')
							ip--;
						else if (expression.charAt(ip - 1) == 'p') {
							pexpr.setCharAt(0, 'm');
							ip--;
						}
					}
				} else if (pexpr.charAt(0) == '+')
					pexpr.deleteCharAt(0);
				expression.replace(ip, np + 1, pexpr.toString());
			}
		}
	}

	// method to evaluate parenthesis for conditions (evaluate the innermost
	// expression until all parenthesis are resolved)
	// expression: contains a temporary expression
	private void parenthesisc(StringBuffer expression) {
		boolean op = true;
		while (op == true) {
			int openp = 0;
			int ip = expression.indexOf("(");
			if (ip == -1) {
				eval1(expression);
				eval2(expression);
				eval3(expression);
				eval4(expression);
				eval5(expression);
				eval6(expression);
				eval7(expression);
				op = false;
			} else {
				int gop = 1;
				int n = ip + 1;
				openp = 1;
				while (n < expression.length()) {
					if (expression.charAt(n) == '(') {
						openp++;
						if (openp > gop) {
							ip = n;
							gop = openp;
						}
					} else if (expression.charAt(n) == ')')
						openp--;
					n++;
				}
				int np = expression.indexOf(")", ip + 1);
				StringBuffer pexpr = new StringBuffer(expression.substring(
						ip + 1, np));
				eval1(pexpr);
				eval2(pexpr);
				eval3(pexpr);
				eval4(pexpr);
				eval5(pexpr);
				eval6(pexpr);
				eval7(pexpr);
				if (pexpr.charAt(0) == '-') {
					if (ip > 0) {
						if (expression.charAt(ip - 1) == '-') {
							pexpr.setCharAt(0, '+');
							ip--;
						} else if (expression.charAt(ip - 1) == 'm') {
							pexpr.setCharAt(0, 'p');
							ip--;
						} else if (expression.charAt(ip - 1) == '+')
							ip--;
						else if (expression.charAt(ip - 1) == 'p') {
							pexpr.setCharAt(0, 'm');
							ip--;
						}
					}
				} else if (pexpr.charAt(0) == '+')
					pexpr.deleteCharAt(0);
				expression.replace(ip, np + 1, pexpr.toString());
			}
		}
	}

}