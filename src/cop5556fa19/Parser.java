/**
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */

package cop5556fa19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cop5556fa19.AST.Block;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.Token.Kind;
import static cop5556fa19.Token.Kind.*;

public class Parser {
	
	@SuppressWarnings("serial")
	class SyntaxException extends Exception {
		Token t;
		
		public SyntaxException(Token t, String message) {
			super(t.line + ":" + t.pos + " " + message);
		}
	}
	
	final Scanner scanner;
	Token t;  //invariant:  this is the next token


	Parser(Scanner s) throws Exception {
		this.scanner = s;
		t = scanner.getNext(); //establish invariant
	}


	Exp exp() throws Exception {
		Token first = t;
		Exp e0 = andExp();
		while (isKind(KW_or)) {
			Token op = consume();
			Exp e1 = andExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	
private Exp andExp() throws Exception{
		// TODO Auto-generated method stub
	Token first = t;
	Exp e0 = compareExp();
	while (isKind(KW_and)) {
		Token op = consume();
		Exp e1 = compareExp();
		e0 = new ExpBinary(first, e0, op, e1);
	}
		//throw new UnsupportedOperationException("andExp");  //I find this is a more useful placeholder than returning null.
	return e0;
	}


	private Exp compareExp() throws Exception {
	Token first = t;
	Exp e0 = BitorExp();
	while (isKind(REL_LT) || isKind(REL_EQEQ) || isKind(REL_GT) || isKind(REL_GE) || isKind(Kind.REL_LE) || isKind(REL_NOTEQ)) {
		Token op = consume();
		Exp e1 = BitorExp();
		e0 = new ExpBinary(first, e0, op, e1);
	}
		//throw new UnsupportedOperationException("andExp");  //I find this is a more useful placeholder than returning null.
	return e0;
	}


	private Exp BitorExp()  throws Exception {
	Token first = t;
	Exp e0 = bitXORExp();
	while (isKind(BIT_OR)) {
		Token op = consume();
		Exp e1 = bitXORExp();
		e0 = new ExpBinary(first, e0, op, e1);
	}
		//throw new UnsupportedOperationException("andExp");  //I find this is a more useful placeholder than returning null.
	return e0;
	}
	private Exp bitXORExp()  throws Exception {
		Token first = t;
		Exp e0 = bitampExp();
		while (isKind(BIT_XOR)) {
			Token op = consume();
			Exp e1 = bitampExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
			//throw new UnsupportedOperationException("andExp");  //I find this is a more useful placeholder than returning null.
		return e0;
		}
	private Exp bitampExp()  throws Exception {
		Token first = t;
		Exp e0 = shiftopExp();
		while (isKind(BIT_AMP)) {
			Token op = consume();
			Exp e1 = shiftopExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
			//throw new UnsupportedOperationException("andExp");  //I find this is a more useful placeholder than returning null.
		return e0;
		}
	private Exp shiftopExp()  throws Exception {
		Token first = t;
		Exp e0 = dotdotExp();
		while (isKind(BIT_SHIFTL) || isKind(Kind.BIT_SHIFTR)) {
			Token op = consume();
			Exp e1 = dotdotExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
			//throw new UnsupportedOperationException("andExp");  //I find this is a more useful placeholder than returning null.
		return e0;
		}
	private Exp dotdotExp()  throws Exception {
		Token first = t;
		Exp e0 = addExp();
		while (isKind(DOTDOT)) {
			Token op = consume();
			Exp e1 = addExp();
			if(isKind(DOTDOT)) {
				consume();
				e0 = new ExpBinary(first,e0,DOTDOT,Expressions.makeBinary(e1, DOTDOT, dotdotExp()));
			}
			else {
				e1 = new ExpBinary(first,e0,DOTDOT,e1);
						return e1;
			}
		}
			//throw new UnsupportedOperationException("andExp");  //I find this is a more useful placeholder than returning null.
		return e0;
		}
	
	private Exp addExp()  throws Exception {
		Token first = t;
		Exp e0 = multiplyExp();
		while(isKind(OP_PLUS)|| isKind(OP_MINUS) ) {
			Token op = consume();
			Exp e1 = multiplyExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0;
		}
	private Exp multiplyExp()  throws Exception {
		Token first = t;
		//Exp e0 = unaryExp();
		Exp e0 = powExp();
		while(isKind(OP_TIMES)|| isKind(OP_DIV) || isKind(OP_DIVDIV) || isKind(OP_MOD) ) {
			Token op = consume();
			//Exp e1 = unaryExp();
			Exp e1 =  powExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0;
		}

	/*private Exp unaryExp()  throws Exception {
		Token first = t;
		Exp e0 = powExp();
		while(isKind(KW_not)|| isKind(OP_HASH) || isKind(OP_MINUS) || isKind(BIT_XOR) ) {
			Token op = consume();
			Exp e1 = powExp();
			e0 = new ExpBinary(first,e0,op,e1);
		}
		return e0;
		}*/
	private Exp powExp()  throws Exception {
		Token first = t;
		Exp e0 = term();
		while(isKind(OP_POW) ) {
			Token op = consume();
			Exp e1 = term();
			if(isKind(OP_POW)) {
				consume();
				e0 = new ExpBinary(first,e0,op,Expressions.makeBinary(e1, OP_POW, powExp()));
			}
			else {
			e1 = new ExpBinary(first,e0,op,e1);
			return e1;
			}
		}
		return e0;
		}


	private Exp term() throws Exception{
		// TODO Auto-generated method stub
		Exp e0 = null;
		if (isKind(INTLIT)) {
			e0 = new ExpInt(t);
			match(INTLIT);
		} else if(isKind(STRINGLIT)) {
			e0 = new ExpString(t);
			match(STRINGLIT);
		}
		else if(isKind(KW_true)) {
			e0 = new ExpTrue(t);
			match(Kind.KW_true);
		}else if(isKind(KW_false)) {
			e0 = new ExpFalse(t);
			match(Kind.KW_false);
		}else if(isKind(NAME)) {
			e0 = new ExpName(t);
			match(NAME);
		}
		else if(isKind(LPAREN)) {
			match(LPAREN);
			e0 = exp();
			match(RPAREN);
		}
		
		else if(isKind(KW_not)) {
			Token op = consume();
			e0 = new ExpUnary(op,KW_not,exp());
		}else if(isKind(OP_HASH)) {
			Token op = consume();
			e0 = new ExpUnary(op,OP_HASH,exp());
		}else if(isKind(BIT_XOR)) {
			Token op = consume();
			e0 = new ExpUnary(op,BIT_XOR,exp());
		
		}else if (isKind(OP_MINUS)) {
			Token op = consume();
			e0 = new ExpUnary(op,OP_MINUS,exp());
		}else if (isKind(DOTDOTDOT)) {
			Token op = consume();
			e0 = new ExpVarArgs(t);
		}else if(isKind(KW_function)) {
			match(KW_function);
			e0	= new ExpFunction(t, fbody());
		}
		else if(isKind(LCURLY)) {
			match(LCURLY);
			e0 = new ExpTable(t, fieldlistexp());
			match(RCURLY);
		}
		else {
			throw new SyntaxException(t, "Invalid token found");	
		}
		return e0;
	}

	private List<Field> fieldlistexp() throws Exception{
		Field e0= null;
		Field e1= null;
		e0 = fieldexp();
		List<Field> fieldlist = new ArrayList<Field>();
		fieldlist.add(e0);
		while(isKind(COMMA)|| isKind(SEMI)) {
			consume();
			e1 = fieldexp();
			fieldlist.add(e1);
		}
		return fieldlist;
	}
	
	private Field fieldexp() throws Exception{
		Exp e0= null;
		Exp e1= null;
		Field fieldexp = null;
		if(isKind(Kind.LSQUARE)) {
			match(LSQUARE);
			e0 = exp();
			match(RSQUARE);
			match(ASSIGN);
			e1 = exp();
			fieldexp  = new FieldExpKey(t, e0, e1);
		}
		else if(isKind(NAME)) {
			t = match(NAME);
			match(ASSIGN);
			e0 = exp();
		FieldNameKey f = new FieldNameKey(t, new Name(t, t.text), e0);			
		}
		else {
			fieldexp = new FieldImplicitKey(t,exp());
		}
		return fieldexp;
	}


	private FuncBody fbody() throws Exception{
		FuncBody fbody = null;
		Token first = t;
		if(isKind(LPAREN)) {
			match(LPAREN);
			fbody = new FuncBody(t, parlList(), block());
			match(RPAREN);
			match(KW_end);
		}
		else {
			throw new SyntaxException(t,"wrong tokens found");
		}
		return fbody;
	}
	
	private ParList parlList() throws Exception{
		ParList parlList = null;
		List<Name> expList = new ArrayList<Name>();
		Token first = t;
		boolean hasVarArgs = false;
		if(isKind(DOTDOTDOT)) {
			match(DOTDOTDOT);
			parlList = new ParList(t, expList, true);
					}
		else if(isKind(NAME)) {
			expList.add(new Name(t,t.text));
			consume();
			while(isKind(COMMA)) {
				match(COMMA);
				if(isKind(NAME)) {
					expList.add(new Name(t,t.text));
					consume();
				}
				else if(isKind(DOTDOTDOT)) {
					hasVarArgs = true;
					parlList = new ParList(t, expList, hasVarArgs);
					consume();
					}
			}
			
			}
			
			
		
		return parlList;
	}

	/*private List<Name> nameList() throws Exception{
		List<Name>expList = new ArrayList<>();
		boolean hasVarArgs = false;
		if(isKind(NAME)) {
			expList.add(new Name(t,t.text));
			consume();
		}
		while(isKind(COMMA)) {
			match()
		expList.add(new Name(t,t.text));
		consume();
			
			else {
				throw new SyntaxException(t,"Missing comma token here");
			}
		}
		
		return expList;
	}*/

	private Block block() {
		return new Block(null,null);  //this is OK for Assignment 2
	}


	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind kind) throws Exception {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		error(kind);
		return null; // unreachable
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind... kinds) throws Exception {
		Token tmp = t;
		if (isKind(kinds)) {
			consume();
			return tmp;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(kind1).append(" ");
		}
		error(kinds);
		return null; // unreachable
	}

	Token consume() throws Exception {
		Token tmp = t;
        t = scanner.getNext();
		return tmp;
	}
	
	void error(Kind... expectedKinds) throws SyntaxException {
		String kinds = Arrays.toString(expectedKinds);
		String message;
		if (expectedKinds.length == 1) {
			message = "Expected " + kinds + " at " + t.line + ":" + t.pos;
		} else {
			message = "Expected one of" + kinds + " at " + t.line + ":" + t.pos;
		}
		throw new SyntaxException(t, message);
	}

	void error(Token t, String m) throws SyntaxException {
		String message = m + " at " + t.line + ":" + t.pos;
		throw new SyntaxException(t, message);
	}
	


}
