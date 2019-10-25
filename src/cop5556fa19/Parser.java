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
import cop5556fa19.AST.Chunk;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpFunctionCall;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTableLookup;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.FuncName;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.AST.RetStat;
import cop5556fa19.AST.Stat;
import cop5556fa19.AST.StatAssign;
import cop5556fa19.AST.StatBreak;
import cop5556fa19.AST.StatDo;
import cop5556fa19.AST.StatFor;
import cop5556fa19.AST.StatForEach;
import cop5556fa19.AST.StatFunction;
import cop5556fa19.AST.StatGoto;
import cop5556fa19.AST.StatIf;
import cop5556fa19.AST.StatLabel;
import cop5556fa19.AST.StatLocalAssign;
import cop5556fa19.AST.StatLocalFunc;
import cop5556fa19.AST.StatRepeat;
import cop5556fa19.AST.StatWhile;
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
	
	public Chunk parse() throws Exception {
		Chunk chunk = new Chunk(t,block());
		return chunk;
		
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
		
		}else if(isKind(KW_nil)) {
			 Token x = consume();
			 e0 = new ExpNil(x);
		}
		else if (isKind(OP_MINUS)) {
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
		if(e0!=null) {
			fieldlist.add(e0);
		}
		while(isKind(COMMA)|| isKind(SEMI)) {
			consume();
			e1 = fieldexp();
			if(e1!=null) {
				fieldlist.add(e1);
			}
		}
		return fieldlist;
	}
	
	private Field fieldexp() throws Exception{
		Exp e0= null;
		Exp e1= null;
		Field fieldexp = null;
		Token first=t;
		if(isKind(Kind.LSQUARE)) {
			match(LSQUARE);
			e0 = exp();
			match(RSQUARE);
			match(ASSIGN);
			e1 = exp();
			fieldexp  = new FieldExpKey(t, e0, e1);
		}
		else if(isKind(NAME)) {
			first = match(NAME);
			if(isKind(ASSIGN)) {
				match(ASSIGN);
				e0 = exp();
				FieldNameKey f = new FieldNameKey(t, new Name(t, t.text), e0);	
			}
			else{
				fieldexp = new FieldImplicitKey(first,new ExpName(first));
			}
					
		}
		else if(!isKind(RCURLY)){
			fieldexp = new FieldImplicitKey(t,exp());
		}
		return fieldexp;
	}


	private FuncBody fbody() throws Exception{
		FuncBody fbody = null;
		Token first = t;
		if(isKind(LPAREN)) {
			match(LPAREN);
			ParList p = parlList();
			match(RPAREN);
			Block b = block(KW_end,KW_until,KW_elseif);
			match(KW_end);
			fbody = new FuncBody(first, p, b);
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
			hasVarArgs = true;
			
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
					consume();
					}
			}
			
			}
			
		parlList = new ParList(first, expList, hasVarArgs);
		
		return parlList;
	}

	

	private Block block() {
		Token first = new Token(SEMI, "dummy",0,0);
		if(!isKind(EOF)) {
			first = t;
		};
		Stat e0=null ;
		Stat e1  = null;
		List<Stat> statementlist = new ArrayList<Stat>();
		while(!isKind(EOF) && !isKind(KW_return)) {
			 e0 = statBlock();
				if(e0!=null) {
					statementlist.add(e0); 
				}	
				
		}
		if(isKind(KW_return)) {
			e1 = returnBlock();
			if(e1!= null) {
				statementlist.add(e1);
			}
		}
		return new Block(first,statementlist);  //this is OK for Assignment 3
		
	}

	private Block block(Kind... end) {

		Token first = new Token(SEMI, "dummy",0,0);
		if(!isKind(EOF)) {
			first = t;
		};
		Stat e0=null ;
		Stat e1  = null;
		List<Stat> statementlist = new ArrayList<Stat>();
		while(!isKind(end)) {
			 e0 = statBlock();
				if(e0!=null) {
					statementlist.add(e0); 
				}	
				
		}
		return new Block(first,statementlist);  //this is OK for Assignment 3
		
	
	}
	
	private Stat returnBlock() {
		Stat e0 = null;
		Token first = t;
		try {
			match(KW_return);
			List <Exp> returnList = expListBlock(); 
			if(isKind(SEMI)) {
				match(SEMI);
			}
			e0 = new RetStat(first, returnList);
			
		} catch (Exception e) {
			System.out.println("Unexpected token found while matching return token found ");
		}
		return e0;
	}

	private Stat statBlock() {
		Token first = t;
		Stat e0=null;
		try {
			switch(t.kind) {
				case SEMI: {
					match(SEMI);
					break;
				}
				case COLONCOLON: {
					e0=labelBlock();
					break;	
					}
				case KW_break:{
					e0=breakBlock();
					break;
				}
				case KW_goto:{
					e0=gotoBlock();
					break;
				}
				case KW_do:{
					e0 = doBlock();
					break;
				}
				case KW_while:{
					e0 = whileBlock();
				break;
				}
				case KW_repeat:{
					e0 = repeatBlock();
					break;
				}
				case KW_if:{
					e0 = ifBlock();
					break;
				}
				case KW_for:{
					e0 = forBlock();
					break;
				}
				case KW_function:{
					e0 = functionBlock();
				}
				case NAME : case LPAREN:{

					List<Exp>nameList = varListBlock();
					match(ASSIGN);
					List<Exp> e1 = expListBlock();
					e0 = new StatAssign(first, nameList, e1);
				break;
				}
				case KW_local:{
					e0 = localBlock();
					break;
				}
			}
				}
		catch(Exception e) {
			
		}
		return e0;
	}

	private Stat labelBlock() throws Exception {
		Token first = t;
		match(COLONCOLON);
		if(isKind(NAME)) {
			first = consume();
		}
		else {
			throw new SyntaxException(t,"Invalid token found.Expected Name token in label Block");
		}
		match(COLONCOLON);
		
		return new StatLabel(first, new Name(first,first.text));
	}

	private Stat localBlock()  throws Exception{
		// TODO Auto-generated method stub
		Token first = t;
		match(KW_local);
		Stat e0=null;
		if(isKind(KW_function)) {
			match(KW_function);
			if(isKind(NAME)) {
				first = consume();
				FuncName funcName = new FuncName(first, new ExpName(first.text));
				FuncBody funcBody = fbody();
				e0 = new StatLocalFunc(first, funcName, funcBody);
			}
		}
		else if(isKind(NAME)) {
			Token a = consume();
			List<ExpName> nameList = namelistBlock(a);
			if(isKind(ASSIGN)) {
				match(ASSIGN);
				List<Exp> expList = expListBlock();
				e0 = new StatLocalAssign(first, nameList, expList);
			}
		}
		return e0;
	}

	private List<Exp> varListBlock()  throws Exception{
		Token first = t;
		List<Exp> nameList= new ArrayList<>();
		Exp ee = null;
		ee = varBlock(ee);
		nameList.add(ee);
		while(isKind(COMMA)) {
			match(COMMA);
			ee = varBlock(ee);	
			nameList.add(ee);
		}
		return nameList;
	}

	private Exp varBlock(Exp ee) throws Exception{
		if(isKind(NAME)) {
			Token a = consume();
			 ee = new ExpName(a);
			while(isKind(LPAREN) || isKind(DOT) || isKind(LSQUARE) || isKind(COLON)) {
				ee = prefixTailexp(ee);
				
			}
		}
		else if(isKind(LPAREN)) {
			match(LPAREN);
			 ee = exp();
			match(RPAREN);
			while(isKind(LPAREN) || isKind(DOT) || isKind(LSQUARE) || isKind(COLON)) {
				ee = prefixTailexp(ee);
			}
		}
		return ee;
	}

	private Exp prefixTailexp(Exp ee) throws Exception{
		if(isKind(LPAREN)) {
			ee = functionCallObject(ee);
		}
		else if(isKind(DOT) ) {
			ee = DotTableLookupBlock(ee);
		}
		else if(isKind(LSQUARE)) {
			ee = TableLookupBlock(ee);
		}
		else if(isKind(COLON)) {
			ee = ColonSyntacticSugar(ee);
		}
		return ee;
	}

	

	private Exp DotTableLookupBlock(Exp ee) throws Exception {
		Exp e0 = null;
		match(DOT);
		Token first =t;
		if(isKind(NAME)) {
			Exp e1 = new ExpString(t);
			consume();
			e0 = new ExpTableLookup(first, ee, e1);
		}
		return e0;
	}
	
	private Exp TableLookupBlock(Exp e0) throws Exception {
		// TODO Auto-generated method stub
		Token first = t;
		match(LSQUARE);
		Exp e1 = exp();
		while(isKind(LPAREN)) {
			e1 = functionCallObject(e1);
		}
		while(isKind(DOT)) {
			e1 = DotTableLookupBlock(e1);
		}
		match(RSQUARE);
		e0 = new ExpTableLookup(first, e0, e1);
		return e0;
	}
	
	private Exp ColonSyntacticSugar(Exp ee) throws Exception {
		// TODO Auto-generated method stub
		Token first = t;
		List<Exp> expList = expListBlock();
		match(COLON);
		if(isKind(NAME)) {
			Token a = consume();
			expList.add(ee);
			switch(t.kind) {
			case LPAREN: {
				match(LPAREN);
				ee = new ExpTableLookup(first,ee,new ExpString(a));
				ee = new ExpFunctionCall(first, ee, expList);
				match(RPAREN);
			}break;
			case LCURLY:{
				match(LCURLY);
				List<Field> fields = fieldlistexp();
				Exp e1 = new ExpTable(first, fields);
				ee = new ExpTableLookup(first,ee,new ExpString(a));
				expList.add(e1);
				ee = new ExpFunctionCall(first, ee, expList);
				match(RCURLY);
			}break;
			case STRINGLIT:{
				Exp e1 = new ExpString(consume());
				expList.add(e1);
				ee = new ExpTableLookup(first,ee,new ExpString(a));
				ee = new ExpFunctionCall(first, ee, expList);
			}
			}
		}
		return ee;
	}
	private Stat functionBlock()  throws Exception{
		Token first = t;
		match(KW_function);
		FuncName e0 = funcName();
		FuncBody e1 = fbody();
		return new StatFunction(first, e0, e1);
	}

	private FuncName funcName() throws Exception {
		FuncName funcName = null;
		Token first = t;
		List<ExpName> nameList = new ArrayList<>();
		ExpName nameafterColon = null;
		if(isKind(NAME)) {
			Token b = consume();
			nameList.add(new ExpName(b));
			while(isKind(DOT)) {
				match(DOT);
				if(isKind(NAME)) {
					Token a = consume();
					nameList.add(new ExpName(a));
				}
			}
			if(isKind(COLON)) {
				match(COLON);
				if(isKind(NAME)) {
					Token a = consume();
					nameafterColon = new ExpName(a);
				}
			}
		}
		else {
			throw new SyntaxException(t,"Invalid token found. Expected Name token for funcName object");
		}
		funcName = new FuncName(first, nameList, nameafterColon);		
		return funcName;
	}

	private Stat forBlock() throws Exception{
		Token first = t;
		Stat e0=null;
		match(KW_for);
		if(isKind(NAME)) {
			Token name = consume();
			if(isKind(ASSIGN)) {
				e0 = forNameBlock(name);
			}
			else if(isKind(COMMA) || isKind(KW_in)) {
				e0 = forNamelistBlock(name);
			}
		}else {
			throw new SyntaxException(t,"Invalid token found. Expected name token in for block");
		}
		return e0;
	}

	private Stat  forNameBlock(Token first) throws Exception {
		Stat e0 = null;
		match(ASSIGN);
		ExpName name = new ExpName(first);
		Exp e1 = exp();
		match(COMMA);
		Exp e2 = exp();
		Exp e3 = null;
		if(isKind(COMMA)) {
			match(COMMA);
			e3 = exp();
		}
		match(KW_do);
		Block e4  = block(KW_end,KW_until);
		match(KW_end);
		e0 = new StatFor(first, name, e1, e2, e3, e4);
		return e0;
	}
	
	private Stat forNamelistBlock(Token name) throws Exception {
		Stat e0 = null;
		List<ExpName> nameList = new ArrayList<>();
		List<Exp> expList = new ArrayList<>();
		nameList = namelistBlock(name);
		match(KW_in);
		expList = expListBlock();
		match(KW_do);
		Block e1 = block(KW_end,KW_until);
		match(KW_end);
		e0 = new StatForEach(name, nameList, expList, e1);
		return e0;
	}
	
	private List<Exp> expListBlock() throws Exception {
		List<Exp> expList = new ArrayList<>();
		Exp e0 = exp();
		
		if(e0 !=null) {
			while(isKind(DOT) || isKind(LPAREN) || isKind(COLON) || isKind(LSQUARE)) {
				e0 = prefixTailexp(e0);
			}
			expList.add(e0);
		}
		while(isKind(COMMA)) {
			match(COMMA);
			Exp e1 = exp();
			while(isKind(DOT) || isKind(LPAREN) || isKind(COLON) || isKind(LSQUARE)) {
				e1 = prefixTailexp(e1);
			}
			if(e1!=null) {
				expList.add(e1);
			}
		}
		return expList;
	}

	private Exp functionCallObject(Exp e1) throws Exception {
		Exp e0=null;
		Token first = t;
		match(LPAREN);
		List<Exp> args = new ArrayList<>();
		Exp e2 = exp();
		while(isKind(DOT)) {
			e2 = DotTableLookupBlock(e2);
		}
		while(isKind(LSQUARE)) {
			e2 = TableLookupBlock(e2);
		}
		if(e2!=null) {
			args.add(e2);
		}
		while(isKind(COMMA)) {
			match(COMMA);
			e2 = exp();
			if(e2!=null) {
				while(isKind(DOT)) {
					e2 = DotTableLookupBlock(e2);
				}
				args.add(e2);
			}
		}
		match(RPAREN);
		e0 = new ExpFunctionCall(first, e1, args);
		return e0;
	}

	private List<ExpName> namelistBlock(Token name) throws Exception {
		List<ExpName> nameList = new ArrayList<ExpName>();
		if(name!=null) {
		nameList.add(new ExpName(name));
		}
		if(isKind(KW_in)) {
			return nameList;
		}
		while(isKind(COMMA)) {
			match(COMMA);
			if(isKind(NAME)) {
				Token a = consume();
				nameList.add(new ExpName(a));
			}
		}
		return nameList;
	}

	private Stat ifBlock()  throws Exception{
		Token first = t;
		match(KW_if);
		List<Exp> expList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		Exp e0 = exp();
		while(isKind(LPAREN)) {
			e0 = functionCallObject(e0);
		}
		while(isKind(DOT)) {
			e0 = DotTableLookupBlock(e0);
		}
		while(isKind(LSQUARE)) {
			e0 = TableLookupBlock(e0);
		}
		if(e0!=null) {
			expList.add(e0);
		}
		match(KW_then);
		Block e1 = block(KW_end,KW_until,KW_elseif,KW_else);
		if(e1!=null) {
			blockList.add(e1);
		}
		while(isKind(KW_elseif)) {
			match(KW_elseif);
			e0 = exp();
			while(isKind(LPAREN)) {
				e0 = functionCallObject(e0);
			}
			while(isKind(DOT)) {
				e0 = DotTableLookupBlock(e0);
			}
			while(isKind(LSQUARE)) {
				e0 = TableLookupBlock(e0);
			}
				expList.add(e0);
			match(KW_then);
			e1 = block(KW_end,KW_until,KW_elseif,KW_else);
			blockList.add(e1);
		}
		if(isKind(KW_else)) {
			match(KW_else);
			e1 = block(KW_end,KW_until);
			blockList.add(e1);
		}
		
		return new StatIf(first, expList, blockList);
	}

	

	private Stat repeatBlock()  throws Exception{
		Token first = t;
		match(KW_repeat);
		Block e0 = block(KW_until);
		match(KW_until);
		Exp e1 = exp();
		return new StatRepeat(first, e0, e1);
	}

	private Stat whileBlock()  throws Exception{
		Token first = t;
		match(KW_while);
		Exp e0 = exp();
		match(KW_do);
		Block e1=block(KW_end,KW_until);
		match(KW_end);
		return new StatWhile(first, e0, e1);
	}

	private Stat doBlock()  throws Exception{
		Token first = t;
		Block e0 ;
		match(KW_do);
		e0 = block(KW_end,KW_until);
		match(KW_end);
		return new StatDo(first,e0);
	}

	private Stat gotoBlock() throws Exception {
		// TODO Auto-generated method stub
		Token first = t;
		Stat e0 ;
		match(KW_goto);
		if(isKind(NAME)) {
			first = consume();
			e0 = new StatGoto(first, new Name(first,first.text));
		}
		else {
			throw new SyntaxException(t,"Invalid token found. Expected Name token ");
		}
		return e0;
	}

	private Stat breakBlock()  throws Exception{
		
		return new StatBreak(consume());
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
