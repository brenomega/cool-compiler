package compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class LexerCommentTest {

	@Test
	void testCommentsAndWhitespace() throws Exception {
		String code = "" 
						+ "-- comentário de linha\n" 
						+ "(* comentário de bloco *)\n"
						+ "(* comentário \n aninhado (* interno *) final *)\n" 
						+ "   \t  \n";

		Lexer lexer = new Lexer(new StringReader(code));

		Lexer.Token token1 = lexer.yylex();
		assertEquals(Lexer.TokenType.COMMENT, token1.type());
		assertEquals("-- comentário de linha", token1.value());

		Lexer.Token token2 = lexer.yylex();
		assertEquals(Lexer.TokenType.COMMENT, token2.type());
		assertEquals("(* comentário de bloco *)", token2.value());

		Lexer.Token token3 = lexer.yylex();
		assertEquals(Lexer.TokenType.COMMENT, token3.type());
		assertTrue(token3.value().contains("(* interno *)"));

		Lexer.Token token4 = lexer.yylex();
		assertEquals(Lexer.TokenType.WHITESPACE, token4.type());
		assertTrue(token4.value().contains(" "));

		Lexer.Token token5 = lexer.yylex();
		assertEquals(Lexer.TokenType.EOF, token5.type());
	}
	
	@Test
	void testUnterminatedComment() throws Exception {
		String code = "(* Este comentário não termina ";
		
		Lexer lexer = new Lexer(new StringReader(code));
		
		assertThrows(
				Lexer.UnterminatedCommentException.class,
				() -> lexer.yylex()
			);
	}
}
