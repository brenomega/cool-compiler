package compiler;

import java_cup.runtime.Symbol;
import java.util.Map;
import java.util.HashMap;

%%

%class Lexer
%public
%cup
%line
%column

%{
    // Constantes
    private static final int STRING_MAX_LENGTH = 1024;

    // Buffers para comentários e strings
    private int commentDepth = 0;
    private StringBuilder commentBuffer = new StringBuilder();
    private StringBuilder stringBuffer = new StringBuilder();

    // Métodos auxiliares para o JCup
    // Cria um símbolo sem valor
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    // Cria um símbolo com valor
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    // HashMap de Keywords
    private static final Map<String, Integer> KEYWORDS = new HashMap<>();
    static {
       KEYWORDS.put("class", sym.CLASS);
       KEYWORDS.put("else", sym.ELSE);
       KEYWORDS.put("fi", sym.FI);
       KEYWORDS.put("if", sym.IF);
       KEYWORDS.put("in", sym.IN);
       KEYWORDS.put("inherits", sym.INHERITS);
       KEYWORDS.put("isvoid", sym.ISVOID);
       KEYWORDS.put("let", sym.LET);
       KEYWORDS.put("loop", sym.LOOP);
       KEYWORDS.put("pool", sym.POOL);
       KEYWORDS.put("then", sym.THEN);
       KEYWORDS.put("while", sym.WHILE);
       KEYWORDS.put("case", sym.CASE);
       KEYWORDS.put("esac", sym.ESAC);
       KEYWORDS.put("new", sym.NEW);
       KEYWORDS.put("of", sym.OF);
       KEYWORDS.put("not", sym.NOT);
       // True e False tem um tratamento exclusivo mas são mapeados aqui para referência
       KEYWORDS.put("true", sym.TRUE);
       KEYWORDS.put("false", sym.FALSE);
    }

    // Exceptions
    public class LexicalException extends RuntimeException {
       private static final long serialVersionUID = 1L;
       public LexicalException(String message) { super(message); }
    }

    public class StringTooLongException extends LexicalException {
       private static final long serialVersionUID = 1L;
       public StringTooLongException(String message) { super(message); }
    }

    public class UnterminatedStringException extends LexicalException {
       private static final long serialVersionUID = 1L;
       public UnterminatedStringException(String message) { super(message); }
    }

    public class NullCharInStringException extends LexicalException {
        private static final long serialVersionUID = 1L;
        public NullCharInStringException(String message) { super(message); }
    }

    public class UnterminatedCommentException extends LexicalException {
       private static final long serialVersionUID = 1L;
       public UnterminatedCommentException(String message) { super(message); }
    }

    public class InvalidCharException extends LexicalException {
       private static final long serialVersionUID = 1L;
       public InvalidCharException(String message) { super(message); }
    }

    public class IntegerOutOfRangeException extends LexicalException {
       private static final long serialVersionUID = 1L;
       public IntegerOutOfRangeException(String message) { super(message); }
    }

    // Escape de strings
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
             default: sb.append(c); break; // Fallback
             }
          } else {
             sb.append(c);
          }
       }
       return sb.toString();
    }
%}

// Estados
%state COMMENT
%state STRING

DIGIT = [0-9]
LOWER = [a-z]
UPPER = [A-Z]
ID_CHAR = [A-Za-z0-9_]

ESC_KNOWN = \\[ntbf\"\\]
ESC_NUL = \\0
ESC_OTHER = \\[^\\\r\n0]
LINE_CONT = \\(\r\n|\r|\n)

%%

<YYINITIAL> {

    /* WHITESPACE (Ignorar e continuar) */
    [ \n\f\r\t\v]+ { /* Não retorna nada, apenas consome */ }

    /* Comentários de Linha (Ignorar até o fim da linha) */
    "--"[^\n\r]* { /* Ignorar */ }

    /* Bloco de Comentários */
    "\(\*" {
        commentDepth = 1;
        commentBuffer.setLength(0);
        commentBuffer.append(yytext());
        yybegin(COMMENT);
    }

    /* Int */
    {DIGIT}+ {
        try {
            // Retorna INT_CONST com o valor inteiro
            return symbol(sym.INT_CONST, Integer.parseInt(yytext()));
        } catch (NumberFormatException e) {
            throw new IntegerOutOfRangeException("Inteiro fora do intervalo 32 bits na linha " + (yyline + 1));
        }
    }

    /* Boolean,  Keywords e IDs */
    [a-zA-Z][a-zA-Z0-9_]* {
        String text = yytext();
        String lowerText = text.toLowerCase();
        Integer symCode = KEYWORDS.get(lowerText);

        // Case Sensitive para TRUE/FALSE na primeira letra para ser keyword
        if (symCode != null && (symCode == sym.TRUE || symCode == sym.FALSE)) {
            if (Character.isLowerCase(text.charAt(0))) {
                return symbol(symCode, symCode == sym.TRUE);
            }
            return symbol(sym.TYPEID, text);
        }

        // Keywords Case Insensitive
        if (symCode != null) {
            return symbol(symCode);
        }

        // Se não é keyword, verifica se é TypeId ou ObjectId
        if (Character.isUpperCase(text.charAt(0))) {
            return symbol(sym.TYPEID, text);
        } else {
            return symbol(sym.ID, text);
        }
    }

    // Início de String
    "\"" {
        stringBuffer.setLength(0);
        yybegin(STRING);
    }

    // Operadores e Pontuação
    "=>" { return symbol(sym.DARROW); }
    "<=" { return symbol(sym.LE); }
    "<-" { return symbol(sym.ASSIGN); }
    "<"  { return symbol(sym.LT); }
    "="  { return symbol(sym.EQ); }
    "+"  { return symbol(sym.PLUS); }
    "-"  { return symbol(sym.MINUS); }
    "~"  { return symbol(sym.NEG); }
    "*"  { return symbol(sym.MULT); }
    "/"  { return symbol(sym.DIV); }
    "@"  { return symbol(sym.AT); }
    "."  { return symbol(sym.DOT); }
    ":"  { return symbol(sym.COLON); }
    ";"  { return symbol(sym.SEMI); }
    ","  { return symbol(sym.COMMA); }
    "\(" { return symbol(sym.LPAREN); }
    "\)" { return symbol(sym.RPAREN); }
    "\{" { return symbol(sym.LBRACE); }
    "\}" { return symbol(sym.RBRACE); }

}

// Estado de Comentário
<COMMENT> {
    "\(\*" {
        commentDepth++;
        commentBuffer.append(yytext());
    }

    "\*\)" {
        commentDepth--;
        commentBuffer.append(yytext());
        if (commentDepth == 0) {
            yybegin(YYINITIAL);
        }
    }

    <<EOF>> {
        throw new UnterminatedCommentException("Comentário de bloco não terminado (EOF dentro de (* ... *))");
    }

    [^] { /* Consome qualquer outro caractere dentro do comentário */ }
}

// Estado de String
<STRING> {
    "\"" {
        yybegin(YYINITIAL);
        return symbol(sym.STR_CONST, stringBuffer.toString());
    }

    {LINE_CONT} {
        // Linha continuada com \ no final: apenas ignora a quebra no valor da string
    }

    {ESC_NUL} {
        throw new NullCharInStringException("String contém \\0 na linha " + (yyline+1));
    }

    {ESC_KNOWN} {
        String t = unescape(yytext());
        if (stringBuffer.length() + t.length() > STRING_MAX_LENGTH) {
           throw new StringTooLongException("String excede 1024 caracteres na linha " + (yyline+1));
        }
        stringBuffer.append(t);
    }

    {ESC_OTHER} {
        // Sequência de escape desconhecida (ex: \c vira apenas o caractere (c))
        if (stringBuffer.length() + 1 > STRING_MAX_LENGTH) {
           throw new StringTooLongException("String excede 1024 caracteres na linha " + (yyline+1));
        }
        String t = yytext();
        stringBuffer.append(t.charAt(1));
    }

    (\r\n|\r|\n) {
        throw new UnterminatedStringException("String não terminada (quebra de linha sem escape) na linha " + (yyline+1));
    }

    <<EOF>> {
        throw new UnterminatedStringException("EOF dentro de string na linha " + (yyline+1));
    }

    [^\"\n\\\r]+ {
        if (stringBuffer.length() + yytext().length() > STRING_MAX_LENGTH) {
           throw new StringTooLongException("String excede 1024 caracteres na linha " + (yyline+1));
        }
        stringBuffer.append(yytext());
    }
}

// Qualquer outro caractere é tido como inválido
[^] {
    throw new InvalidCharException("Caractere inválido: " + yytext() + " na linha " + (yyline+1));
}