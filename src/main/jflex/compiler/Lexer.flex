package compiler;

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
%}

%%

<<EOF>> {
	return Token.eof();
}

. {
	throw new Error("Caractere inválido: " + yytext());
}
