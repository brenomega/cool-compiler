package compiler;

import java.util.Map;
import java.util.HashMap;

%%

%class Lexer
%unicode
%public
%type compiler.Lexer.Token
%{
	public enum TokenType {
		OBJECTID, TYPEID, INT, STRING, PLUS, MINUS, TIMES, 
		DIVIDE, LT, LE, EQ, ASSIGN, ARROW, AT, DOT, COLON,
		SEMI, COMMA, LPAREN, RPAREN, LBRACE, RBRACE, 
		
		CLASS, ELSE, FI, IF, IN, INHERITS, ISVOID, LET, LOOP, 
		POOL, THEN, WHILE, CASE, ESAC, NEW, OF, NOT, TRUE, FALSE,
		
		COMMENT, WHITESPACE, ERROR, EOF
	}
	
	public record Token(TokenType type, String value) {
		public static Token eof() {
			return new Token(TokenType.EOF, "");
		}
		@Override
		public String toString() {
			return type + "('" + value + "')";
		}
	}
	
	private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
	static {
		KEYWORDS.put("class", TokenType.CLASS);
		KEYWORDS.put("else", TokenType.ELSE);
		KEYWORDS.put("fi", TokenType.FI);
		KEYWORDS.put("if", TokenType.IF);
		KEYWORDS.put("in", TokenType.IN);
		KEYWORDS.put("inherits", TokenType.INHERITS);
		KEYWORDS.put("isvoid", TokenType.ISVOID);
		KEYWORDS.put("let", TokenType.LET);
		KEYWORDS.put("loop", TokenType.LOOP);
		KEYWORDS.put("pool", TokenType.POOL);
		KEYWORDS.put("then", TokenType.THEN);
		KEYWORDS.put("while", TokenType.WHILE);
		KEYWORDS.put("case", TokenType.CASE);
		KEYWORDS.put("esac", TokenType.ESAC);
		KEYWORDS.put("new", TokenType.NEW);
		KEYWORDS.put("of", TokenType.OF);
		KEYWORDS.put("not", TokenType.NOT);
		KEYWORDS.put("true", TokenType.TRUE);
		KEYWORDS.put("false", TokenType.FALSE);
	}
	
	private int commentDepth = 0;
	private StringBuilder commentBuffer = new StringBuilder();
%}

// Para isolar lógica de comentários
%state COMMENT

DIGIT = [0-9]
LOWER = [a-z]
UPPER = [A-Z]
ID_CHAR = [A-Za-z0-9_]

%%

<YYINITIAL> <<EOF>> {
	return Token.eof();
}

<YYINITIAL> [ \t\r]+ { 
	return new Token(TokenType.WHITESPACE, yytext());
}

<YYINITIAL> \n {
	yyline++;
}

<YYINITIAL> "--"[^\n]* { 
	return new Token(TokenType.COMMENT, yytext());
}

<YYINITIAL> "\(\*" {
	commentDepth = 1;
	commentBuffer.setLength(0);
	commentBuffer.append(yytext());
	yybegin(COMMENT);
}

<COMMENT> "\(\*" {
	commentDepth++;
	commentBuffer.append(yytext());
}

<COMMENT> "\*\)" {
	commentDepth--;
	commentBuffer.append(yytext());
	if (commentDepth == 0) {
		yybegin(YYINITIAL);
		return new Token(TokenType.COMMENT, commentBuffer.toString());
	}
}

<COMMENT> [^\(\*]* { 
	commentBuffer.append(yytext());
}

<COMMENT> . { 
	commentBuffer.append(yytext());
}

<COMMENT> <<EOF>> {
	throw new Error("Comentário de bloco não terminado (EOF dentro de (* ... *))");
}

. {
	throw new Error("Caractere inválido: " + yytext());
}
