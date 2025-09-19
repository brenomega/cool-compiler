package compiler;

%%

%class Lexer
%unicode
%public
%type compiler.Lexer.Token
%{
	public enum TokenType {
		EOF
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
