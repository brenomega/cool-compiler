package compiler;

import java.util.Map;
import java.util.HashMap;

%%

%class Lexer
%public
%type compiler.Lexer.Token
%{
	// CONSTANTS
	private static final int STRING_MAX_LENGTH = 1024;
	
	// BUFFERS
	private int commentDepth = 0;
	private StringBuilder commentBuffer = new StringBuilder();
	private StringBuilder stringBuffer = new StringBuilder();
	
	// TOKEN DEFINITION
	public enum TokenType {
		OBJECTID, TYPEID, INT, STRING, PLUS, MINUS, TIMES, 
		DIVIDE, LT, LE, EQ, ASSIGN, ARROW, AT, DOT, COLON,
		SEMI, COMMA, LPAREN, RPAREN, LBRACE, RBRACE, NEG,
		
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
	
	// KEYWORDS
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
	
	// BASE EXCEPTIONS
	public class LexicalException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public LexicalException(String message) {
			super(message);
		}
	}
	
	// EXCEPTIONS
	public class StringTooLongException extends LexicalException {
		private static final long serialVersionUID = 1L;
		
	    public StringTooLongException(String message) {
	        super(message);
	    }
	}
	
	public class UnterminatedStringException extends LexicalException {
		private static final long serialVersionUID = 1L;
	    public UnterminatedStringException(String message) {
	    	super(message);
	    }
	}

	public class NullCharInStringException extends LexicalException {
        private static final long serialVersionUID = 1L;
	    public NullCharInStringException(String message) {
	    	super(message);
	    }
	}

	public class UnterminatedCommentException extends LexicalException {
		private static final long serialVersionUID = 1L;
	    public UnterminatedCommentException(String message) {
	    	super(message);
	    }
	}

	public class InvalidCharException extends LexicalException {
		private static final long serialVersionUID = 1L;
	    public InvalidCharException(String message) {
	    	super(message);
	    }
	}
	
	public class IntegerOutOfRangeException extends LexicalException {
		private static final long serialVersionUID = 1L;
	    public IntegerOutOfRangeException(String message) {
	    	super(message);
	    }
	}
	
	// UTIL
	private String unescape(String s) { 
		StringBuilder sb = new StringBuilder(); 
		for (int i = 0; i < s.length(); i++) { 
			char c = s.charAt(i); 
			if (c == '\\' && i + 1 < s.length()) { 
				char nxt = s.charAt(++i); 
				switch (nxt) { 
				case 'n': sb.append('\n'); break; 
				case 't': sb.append('\t'); break; 
				case 'b': sb.append('\b'); break; 
				case 'f': sb.append('\f'); break; 
				case '"': sb.append('"'); break; 
				case '\\': sb.append('\\'); break; 
				} 
			} else { 
				throw new LexicalException("Isso não deveria acontecer.");
			} 
		} 
		return sb.toString(); 
	}
	
	public int getLine() {
		return yyline + 1;
	}
%}

// Para isolar lógica de comentários
%state COMMENT
// Para isolar lógica de strings
%state STRING

DIGIT = [0-9]
LOWER = [a-z]
UPPER = [A-Z]
ID_CHAR = [A-Za-z0-9_]

// Mantive \r para manter compatibilidade com Windows
ESC_KNOWN = \\[ntbf\"\\]
ESC_NUL = \\0
ESC_OTHER = \\[^\\\r\n0]
LINE_CONT = \\(\r\n|\r|\n)

%%

<YYINITIAL> <<EOF>> {
	return Token.eof();
}

<YYINITIAL> [ \n\f\r\t\v]+ {

    for (int i = 0; i < yylength(); i++) {
        if (yycharat(i) == '\n') yyline++;
    }
	return new Token(TokenType.WHITESPACE, yytext());
}

<YYINITIAL> (\r\n|\r|\n) {
	yyline++;
}

<YYINITIAL> "--"[^\n\r]* { 
	return new Token(TokenType.COMMENT, yytext());
}

<YYINITIAL> "\(\*" {
	commentDepth = 1;
	commentBuffer.setLength(0);
	commentBuffer.append(yytext());
	yybegin(COMMENT);
}

<COMMENT> (\r\n|\r|\n) {
    yyline++;
    commentBuffer.append(yytext());
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

<COMMENT> [^\(\*\r\n]+ { 
	commentBuffer.append(yytext());
}

<COMMENT> . { 
	commentBuffer.append(yytext());
}

<COMMENT> <<EOF>> {
	throw new UnterminatedCommentException("Comentário de bloco não terminado (EOF dentro de (* ... *))");
}

<YYINITIAL> "\"" {
	stringBuffer.setLength(0);
	yybegin(STRING);
}

<STRING> {LINE_CONT} { 
	String t = yytext();
	for (int i = 0; i < t.length(); i++) {
		if (t.charAt(i) == '\n') yyline++;
	}
}

<STRING> {ESC_NUL} {
	throw new NullCharInStringException("String contém \\0 na linha " + (yyline+1));
}

<STRING> {ESC_KNOWN} {
	String t = unescape(yytext());
	if (stringBuffer.length() + t.length() > STRING_MAX_LENGTH) {
		throw new StringTooLongException("String excede 1024 caracteres na linha " + (yyline+1));
	}
	stringBuffer.append(t);
}

<STRING> {ESC_OTHER} {
	if (stringBuffer.length() + 1 > STRING_MAX_LENGTH) {
		throw new StringTooLongException("String excede 1024 caracteres na linha " + (yyline+1));
	}
	String t = yytext();
	stringBuffer.append(t.charAt(1));
}

<STRING> [^\"\n\\\r]+ {
	if (stringBuffer.length() + yytext().length() > STRING_MAX_LENGTH) {
		throw new StringTooLongException("String excede 1024 caracteres na linha " + (yyline+1));
	}
	stringBuffer.append(yytext());
}

<STRING> "\"" {
	String value = stringBuffer.toString();
	yybegin(YYINITIAL);
	return new Token(TokenType.STRING, value);
}

<STRING> (\r\n|\r|\n) {
	throw new UnterminatedStringException("String não terminada na linha " + (yyline+1));
}

<STRING> <<EOF>> {
	throw new UnterminatedStringException("EOF dentro de string na linha " + (yyline+1));
}

<YYINITIAL> {DIGIT}+ {
	try {
		Integer.parseInt(yytext());
        return new Token(TokenType.INT, yytext());
	} catch (NumberFormatException e) {
    	throw new IntegerOutOfRangeException("Inteiro fora do intervalo 32 bits na linha " + (yyline + 1));
	}
}

<YYINITIAL> [a-zA-Z][a-zA-Z0-9_]* {
    String text = yytext();
    String lowerText = text.toLowerCase();
    TokenType keywordType = KEYWORDS.get(lowerText);
    if (keywordType == TokenType.FALSE || keywordType == TokenType.TRUE) {
        if (Character.isLowerCase(text.charAt(0))) {
            return new Token(keywordType, yytext());
        }
        return new Token(TokenType.TYPEID, yytext());
    }
    if (keywordType != null) {
        return new Token(keywordType, yytext());
    }
    if (Character.isUpperCase(text.charAt(0))) {
        return new Token(TokenType.TYPEID, yytext());
    }
    return new Token(TokenType.OBJECTID, yytext());
}

<YYINITIAL> "=>" { return new Token(TokenType.ARROW, yytext()); } 
<YYINITIAL> "<=" { return new Token(TokenType.LE, yytext()); } 
<YYINITIAL> "<-" { return new Token(TokenType.ASSIGN, yytext()); }

<YYINITIAL> "<" { return new Token(TokenType.LT, yytext()); } 
<YYINITIAL> "=" { return new Token(TokenType.EQ, yytext()); } 
<YYINITIAL> "+" { return new Token(TokenType.PLUS, yytext()); } 
<YYINITIAL> "-" { return new Token(TokenType.MINUS, yytext()); }
<YYINITIAL> "~" { return new Token(TokenType.NEG, yytext()); }
<YYINITIAL> "*" { return new Token(TokenType.TIMES, yytext()); } 
<YYINITIAL> "/" { return new Token(TokenType.DIVIDE, yytext()); } 
<YYINITIAL> "@" { return new Token(TokenType.AT, yytext()); } 
<YYINITIAL> "." { return new Token(TokenType.DOT, yytext()); } 
<YYINITIAL> ":" { return new Token(TokenType.COLON, yytext()); } 
<YYINITIAL> ";" { return new Token(TokenType.SEMI, yytext()); } 
<YYINITIAL> "," { return new Token(TokenType.COMMA, yytext()); } 
<YYINITIAL> "\(" { return new Token(TokenType.LPAREN, yytext()); } 
<YYINITIAL> "\)" { return new Token(TokenType.RPAREN, yytext()); } 
<YYINITIAL> "\{" { return new Token(TokenType.LBRACE, yytext()); } 
<YYINITIAL> "\}" { return new Token(TokenType.RBRACE, yytext()); }

. {
	throw new InvalidCharException("Caractere inválido: " + yytext());
}
