

/* *
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites or repositories,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */
package cop5556fa19;


import static cop5556fa19.Token.Kind.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import cop5556fa19.Token.Kind;

public class Scanner {
	
	Reader r;
	String testString;
	int numPos=-1;
	int lineNo=0;
	Kind kind;
	StringBuilder sb;
	StringBuilder params = new StringBuilder();
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {	
		public LexicalException(String arg0) {
			super(arg0);
		}
	}
	
	public Scanner(Reader r) throws IOException {
		this.r = r;
		sb = new StringBuilder();
	}

	public Token getNext() throws Exception {
		    //replace this code.  Just for illustration
		char a ;String line="";
		BufferedReader reader = new BufferedReader(r,10);
		Token t = new Token(EOF,"EOF",0,0);
		
		if(testString == null) {
			intializeInput(reader);
		}
		if(testString !=null) {
			numPos +=1;
		if(numPos<testString.length()) 
		{a = testString.charAt(numPos);}
		else 
		{
			return new Token((Kind.START.compareTo(kind)==0)?EOF:kind,(Kind.START.compareTo(kind)==0)?"EOF":sb.toString(),numPos,lineNo);
		}	
		
		
		while(kind.START.equals(kind)&& Character.compare(' ', a)==0) {
		numPos+=1;
		a=testString.charAt(numPos);
		}
	
			System.out.println("character is "+a);
			switch(kind) {
			case START:
				switch(a) {
				case '0': {
					t= new Token(INTLIT,"0",numPos,lineNo);
						
				}break;

				case '@': {
					throw new LexicalException("Error :- Invalid token found as "+a+" in line: "+ lineNo + " and position:"+numPos);	//@
					}
				case '\\': {
					//kind = Kind.ESCSEQ;
					numPos+=1;
					a = testString.charAt(numPos);
					switch(a) {
					case 'n': case 'r':case 't':case 'f': {
						kind = Kind.START;
						t= new Token(kind,sb.toString(),numPos,lineNo);
						sb = new StringBuilder();
						return t;
						}
					default: {
						kind = Kind.START;
						throw new LexicalException("Invalid line terminator : \\"+a+"found at numPos: "+numPos+" and LineNo: "+lineNo);
					}
					}
					
				}
				
				case ',': {
					kind = Kind.START;
					t=new Token(COMMA,",",numPos,lineNo);
				}
				break;
				case ':': {
					kind = Kind.COLON;
						return (getNext());
					}
				case '.': {
					kind = Kind.DOT;
					return (getNext());
				}
				case '=': {
					kind = Kind.ASSIGN;
					return (getNext());
					}
				case '+':{
					t = new Token(OP_PLUS,"+",numPos,lineNo);
					break;
				}
				case '-':{
					kind = kind.OP_MINUS;
					return getNext();
				}
				case '*':{
					t = new Token(OP_TIMES,"*",numPos,lineNo);
					break;
				}
				case '^':{
					t = new Token(OP_POW,"^",numPos,lineNo);
					break;
				}
				case '#':{
					t = new Token(OP_HASH,"#",numPos,lineNo);
					break;
				}
				case '%':{
					t = new Token(OP_MOD,"%",numPos,lineNo);
					break;
				}
				case '&':{
					t = new Token(BIT_AMP,"&",numPos,lineNo);
					break;
				}
				case '/':{
					kind = Kind.OP_DIV;
					return getNext();
				}
				case '<':{
					kind = Kind.REL_LT;
					sb.append("<");
					t= getNext();
					kind = Kind.START;
					break;
				}
				case '>':{
					kind = Kind.REL_GT;
					sb.append(">");
					t= getNext();
					kind = Kind.START;
					break;
				}
				
				case '~':{
					kind = Kind.BIT_XOR;
					sb.append("|");
					t= getNext();
					kind = Kind.START;
					break;
				}
				case '|':{
					t = new Token(BIT_OR,"|",numPos,lineNo);
					break;
				}
				case '(':
					t = new Token(LPAREN,"(",numPos,lineNo);
					break;
				case ')':
					t = new Token(RPAREN,")",numPos,lineNo);
					break;	
				case '{':
					t = new Token(LCURLY,"{",numPos,lineNo);
					break;
				case '}':
					t = new Token(RCURLY,"}",numPos,lineNo);
					break;
				case '[':
					t = new Token(LSQUARE,"[",numPos,lineNo);
					break;
				case ']':
					t = new Token(RSQUARE,"]",numPos,lineNo);
					break;
				case ';':
					t = new Token(SEMI,";",numPos,lineNo);
					break;
				case '"':{
					kind = Kind.STRINGLIT;
					sb.append('"');
					params.append('"');
					t= getNext();
					break;
					}
				case '\'':{
					kind = Kind.STRINGLIT;
					params.append('\'');
					t= getNext();
					break;
					}

				default:{
					if(Character.isJavaIdentifierStart(a) && Character.compare('$', a)!=0 && Character.compare('_', a)!=0  ) {
						kind = Kind.NAME;
						sb.append((char)a);
						t = new Token(NAME,sb.toString(),numPos,lineNo);
					}
					else if(Character.isDigit(a)) {
						kind = Kind.INTLIT;
						sb.append((char)a);
						t = new Token(INTLIT,sb.toString(),numPos,lineNo);
						}
					else {
					throw new LexicalException("Error :- Invalid token found as "+a+" in line: "+ lineNo + " and position:"+numPos);	
					}
					if(numPos!=testString.length()-1) {
						t= getNext();
					}else {kind = Kind.START;}
					
				}
				}
				kind = Kind.START;
				checkForKeyWords(t);
				break;
			case COLON :
				{
					if(Character.compare(':', a) ==0) {
					kind = Kind.START;
				t = new Token(COLONCOLON,"::",0,0);
					}
					else {
						kind = Kind.START;
						numPos-=1;
						t = new Token(COLON,":",numPos,lineNo);
					}
				}
				break;
			case NAME:{
				if(Character.compare(' ', a)==0) {
					kind = Kind.START;
					t= new Token(NAME,sb.toString(),numPos-1,lineNo);
					sb = new StringBuilder();
					return t;
				}
				if(Character.isJavaIdentifierPart(a) || Character.compare('$', a)==0 ||Character.compare('_', a)==0 ||Character.compare(';', a)==0  ) {
				sb.append((char)a);
				if(numPos != testString.length()-1 && Character.compare(';', a)!=0){
					return getNext();
				}
				else {
					kind = START;
					t = new Token(NAME,sb.toString(),numPos,lineNo);
				}
				
				}
				else if(Character.compare('\\', a)==0) {
					//kind = Kind.ESCSEQ;
					numPos+=1;
					a = testString.charAt(numPos);
					switch(a) {
					case 'n': case 'r':case 't':case 'f': {
						if(Character.compare('n', a)==0) {lineNo+=1;}
						t= new Token(kind,sb.toString(),numPos,lineNo);
						kind = Kind.START;
						sb = new StringBuilder();
						return t;
						}
					default: {
						kind = Kind.START;
						throw new LexicalException("Invalid line terminator : \\"+a+"found at numPos: "+numPos+" and LineNo: "+lineNo);
					}
					}
					
				}
				else {
					numPos-=1;
					t = new Token(NAME,sb.toString(),numPos,lineNo);
				}
				if(Character.compare('+', a)==0) {
					kind = Kind.OP_PLUS;
				}
				else if(Character.compare('-', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('*', a)==0) {
					kind = Kind.OP_TIMES;
				}
				else if(Character.compare('/', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('%', a)==0) {
					kind = Kind.OP_MOD;
				}
				else if(Character.compare('^', a)==0) {
					kind = Kind.OP_POW;
				}
				else if(Character.compare('#', a)==0) {
					kind = Kind.OP_HASH;
				}
				else if(Character.compare('&', a)==0) {
					kind = Kind.BIT_AMP;
				}
				else if(Character.compare('~', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('|', a)==0) {
					kind = Kind.BIT_OR;
				}
				else if(Character.compare(':', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('<', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('>', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('=', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('(', a)==0) {
					kind = Kind.START;
				}else if(Character.compare(')', a)==0) {
					kind = Kind.START;
				}else if(Character.compare('{', a)==0) {
					kind = Kind.START;
				}else if(Character.compare('}', a)==0) {
					kind = Kind.START;
				}else if(Character.compare('[', a)==0) {
					kind = Kind.START;
				}else if(Character.compare(']', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare(',', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('.', a)==0) {
					kind = Kind.START;
				}
				
				break;
			}
			
					
			case INTLIT:{
				if(Character.compare(' ', a)==0) {
					kind = Kind.START;
					t= new Token(INTLIT,sb.toString(),numPos-1,lineNo);
					sb = new StringBuilder();
					return t;
				}
				if(Character.isDigit(a)) {
				sb.append((char)a);
					if(numPos != testString.length()-1){
						return getNext();
					}
				else {
					t = new Token(INTLIT,sb.toString(),numPos,lineNo);
				}
				
				}
				else {
					numPos-=1;
					t = new Token(INTLIT,sb.toString(),numPos,lineNo);
				}
				
				if(Character.compare('+', a)==0) {
					kind = Kind.OP_PLUS;
				}
				else if(Character.compare('-', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('*', a)==0) {
					kind = Kind.OP_TIMES;
				}
				else if(Character.compare('/', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('%', a)==0) {
					kind = Kind.OP_MOD;
				}
				else if(Character.compare('^', a)==0) {
					kind = Kind.OP_POW;
				}
				else if(Character.compare('#', a)==0) {
					kind = Kind.OP_HASH;
				}
				else if(Character.compare('&', a)==0) {
					kind = Kind.BIT_AMP;
				}
				else if(Character.compare('~', a)==0) {
					kind = Kind.BIT_XOR;
				}
				else if(Character.compare('|', a)==0) {
					kind = Kind.BIT_OR;
				}
				else if(Character.compare(':', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('<', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('>', a)==0) {
					kind = Kind.START;
				}
				else if(Character.compare('=', a)==0) {
					kind = Kind.START;
				}else if(Character.compare(')', a)==0) {
					kind = Kind.START;
				}
				
				break;
			}
			case STRINGLIT:{
				if(Character.compare('"', a)==0){ 	//proper checking for opening and closing of quotes
					params = removeLastOccurence(params,'"');
					if(!"".equals(params.toString())){
						throw new LexicalException("Invalid Number of quotes in the input");
					}
					kind = Kind.START;
					sb.append('"');
					t = new Token(STRINGLIT,sb.toString(),numPos,lineNo);
				}
				else if(Character.compare('\'', a)==0){ 	//proper checking for opening and closing of quotes
					params = removeLastOccurence(params,'\'');
					if(!"".equals(params.toString())){
						throw new LexicalException("Invalid Number of quotes in the input");
					}
					kind = Kind.START;
					t = new Token(STRINGLIT,sb.toString(),numPos,lineNo);
				}
				else if(Character.compare('\\', a)==0) { //ESCAPE SEQ for string literal
					numPos+=1;
					a = testString.charAt(numPos);
					switch(a) {
					
					case 'a':{
						sb.append("\\u0007");
						return getNext();
					}
					case 'b':{
						sb.append("\\u0008");
						return getNext();
					}
					case 'f':{
						sb.append("\\u0012");
						return getNext();
					}
					case 'n':{
						lineNo+=1;
						sb.append("\n");
						return getNext();
					}
					case 'r':{
						sb.append("\\u0013");
						return getNext();
					}
					case 't':{
						sb.append("\\u0009");
						return getNext();
					}
					case 'v':{
						sb.append("\\u00011");
						return getNext();
					}
					case '\\':{
						sb.append("\\");
						return getNext();
					}
					case '\"':{
						sb.append('"');
						return getNext();
					}
					case '\'':{
						sb.append('\'');
						return getNext();
					}
					}
				}
					else {
					sb.append((char)a);
					t= getNext();
					/*if(!"".equals(params.toString())){
						throw new LexicalException("Invalid Number of quotes in the input");
					}*/
					return t;
				}
				break;
			}
			
			case ASSIGN:{
				if(Character.compare('=', a)==0) {
					kind = Kind.START;
					t = new Token(REL_EQEQ,"==",numPos,lineNo);
				}
				else {
					numPos-=1;
					kind = Kind.START;
					t = new Token(ASSIGN,"=",numPos,lineNo);
				}
				break;
			}
			case OP_PLUS:{
				if(Character.compare(a, '+')==0) {
					kind = Kind.START;
					t = new Token(OP_PLUS,"+",numPos,lineNo);
					break;
				}
			}
			case OP_MINUS:{
				if(Character.compare(a, '-')!=0) {
					numPos-=1;
					kind = Kind.START;
					t = new Token(OP_MINUS,"-",numPos,lineNo);
					break;
				}
				else {
					kind=Kind.START;
					boolean lf = false;
					while(numPos!=testString.length()-1) {
												numPos+=1;
											if(Character.compare('\\',testString.charAt(numPos))==0){
												lf = true;
											}
											else if(lf && (Character.compare('n', testString.charAt(numPos))==0 || Character.compare('r', testString.charAt(numPos))==0)) {
								break;
							}else {
								lf = false;
							}
							
					}
					return getNext();
				}
			}
			case OP_TIMES:{
				if(Character.compare(a, '*')==0) {
					kind = Kind.START;
					t = new Token(OP_TIMES,"*",numPos,lineNo);
					break;
				}
			}
			case OP_DIV:{
				if(Character.compare('/', a) ==0) {
				kind = Kind.START;
			t = new Token(Kind.OP_DIVDIV,"//",numPos,lineNo);
				}
				else {
					kind = Kind.START;
					numPos-=1;
					t = new Token(OP_DIV,"/",numPos,lineNo);
				}
				break;
			}
			case DOT :
			{
				if(Character.compare('.', a) ==0) {
				kind = Kind.START;
				t = new Token(Kind.DOTDOT,"..",numPos,lineNo);
				if(numPos!=testString.length()-1) {
					kind = Kind.DOTDOT;
					t = getNext();
				}
				}
				else {
					kind = Kind.START;
					numPos-=1;
					t = new Token(DOT,".",numPos,lineNo);
				}
				break;
			}
			case DOTDOT :
			{
				if(Character.compare('.', a) ==0) {
					kind = Kind.START;
					t = new Token(DOTDOTDOT,"...",numPos,lineNo);
			}
				else {
					numPos-=1;
					kind = Kind.START;
					t = new Token(DOTDOT,"..",numPos,lineNo);
			}
				break;
			}
			case OP_MOD:{
				if(Character.compare(a, '%')==0) {
					kind = Kind.START;
					t = new Token(OP_MOD,"%",numPos,lineNo);
					break;
				}
			}
			case OP_POW:{
				if(Character.compare(a, '^')==0) {
					kind = Kind.START;
					t = new Token(OP_POW,"^",numPos,lineNo);
					break;
				}
			}
			case OP_HASH:{
				if(Character.compare(a, '#')==0) {
					kind = Kind.START;
					t = new Token(OP_HASH,"#",numPos,lineNo);
					break;
				}
			}
			case BIT_AMP:{
				if(Character.compare(a, '&')==0) {
					kind = Kind.START;
					t = new Token(BIT_AMP,"&",numPos,lineNo);
					break;
				}
			}
			case BIT_XOR:{
				if(Character.compare(a, '=')==0) {
					kind = Kind.START;
					t = new Token(REL_NOTEQ,"~=",numPos,lineNo);
				}
				else {
					numPos-=1;
					kind = Kind.START;
					t = new Token(BIT_XOR,"~",numPos,lineNo);
				}
				break;
			}
			case BIT_OR:{
				if(Character.compare(a, '|')==0) {
					kind = Kind.START;
					t = new Token(BIT_OR,"|",numPos,lineNo);
					break;
				}
			}
			case REL_LT:{
				if(Character.compare(a, '<')==0) {
					kind = Kind.START;
					t = new Token(BIT_SHIFTL,"<<",numPos,lineNo);
					break;
				}
				else if(Character.compare('=', a)==0) {
					kind = Kind.START;
					t = new Token(REL_LE,"<=",numPos,lineNo);
					break;
				}
				else {
					numPos-=1;
					kind = Kind.START;
					t = new Token(REL_LT,"<",numPos,lineNo);
					break;
				}
			
			}
			case REL_GT:{
				if(Character.compare(a, '>')==0) {
					kind = Kind.START;
					t = new Token(BIT_SHIFTR,">>",numPos,lineNo);
					break;
				}
				else if(Character.compare('=', a)==0) {
					kind = Kind.START;
					t = new Token(REL_GE,">=",numPos,lineNo);
					break;
				}
				else {
					numPos-=1;
					kind = Kind.START;
					t = new Token(REL_GT,">",numPos,lineNo);
					break;
				}
			
			}
				
			}
		 }
		sb = new StringBuilder();
		if(!"".equals(params.toString())) {
			throw new LexicalException("Invalid token due to incomplete quotes");
		}
		if(!t.kind.equals(STRINGLIT)) {t = checkForKeyWords(t);}
		return t;
}


	

	private StringBuilder removeLastOccurence(StringBuilder params,char a) {
		String params1 = params.toString();
		int index = params1.lastIndexOf(a);
		params = params.deleteCharAt(index);
		return params;
	}

	private Token checkForKeyWords(Token t) {
		if(t.kind.equals(NAME)) {
			switch(t.text){
				case "and": t = new Token(KW_and,t.text,numPos,lineNo);break;
				case "break": t = new Token(KW_break,t.text,numPos,lineNo);break;
				case "do": t = new Token(KW_do,t.text,numPos,lineNo);break;
				case "else": t = new Token(KW_else,t.text,numPos,lineNo);break;
				case "elseif": t = new Token(KW_elseif,t.text,numPos,lineNo);break;
				case "end": t = new Token(KW_end,t.text,numPos,lineNo);break;
				case "false": t = new Token(KW_false,t.text,numPos,lineNo);break;
				case "for": t = new Token(KW_for,t.text,numPos,lineNo);break;
				case "function": t = new Token(KW_function,t.text,numPos,lineNo);break;
				case "if": t = new Token(KW_if,t.text,numPos,lineNo);break;
				case "in": t = new Token(KW_in,t.text,numPos,lineNo);break;
				case "local": t = new Token(KW_local,t.text,numPos,lineNo);break;
				case "nil": t = new Token(KW_nil,t.text,numPos,lineNo);break;
				case "not": t = new Token(KW_not,t.text,numPos,lineNo);break;
				case "or": t = new Token(KW_or,t.text,numPos,lineNo);break;
				case "repeat": t = new Token(KW_repeat,t.text,numPos,lineNo);break;
				case "return": t = new Token(KW_return,t.text,numPos,lineNo);break;
				case "then": t = new Token(KW_then,t.text,numPos,lineNo);break;
				case "true": t = new Token(KW_true,t.text,numPos,lineNo);break;
				case "until": t = new Token(KW_until,t.text,numPos,lineNo);break;
				case "while": t = new Token(KW_while,t.text,numPos,lineNo);break;
				
			}
		}
		return t;
	}


	private void intializeInput(BufferedReader r) throws Exception {
		 int input;
		 StringBuilder sb=new StringBuilder();
	        while ((input = r.read()) != -1) {
	            char ch = (char) input;
	            sb.append(ch);
	        }
		testString = sb.toString();	
		kind = Kind.START;
	}
	}
