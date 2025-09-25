package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.lang.reflect.Field;

import compiler.Lexer;
import org.junit.jupiter.api.Test;

public class LexerCommentTest {

	@Test
	void testCommentsAndWhitespace() throws Exception {
		String code = "" 
						+ "-- comentario de linha\n" 
						+ "(* comentario de bloco *)\n"
						+ "(* comentario \n aninhado (* interno *) final *)\n" 
						+ "   \t  \n";

		Lexer lexer = new Lexer(new StringReader(code));

		Lexer.Token token1 = lexer.yylex();
		assertEquals(Lexer.TokenType.COMMENT, token1.type());
		assertEquals("-- comentario de linha", token1.value());

		Lexer.Token token2 = lexer.yylex();
		assertEquals(Lexer.TokenType.COMMENT, token2.type());
		assertEquals("(* comentario de bloco *)", token2.value());

		Lexer.Token token3 = lexer.yylex();
		assertEquals(Lexer.TokenType.COMMENT, token3.type());
		assertTrue(token3.value().contains("(* interno *)"));

		Lexer.Token token4 = lexer.yylex();
		assertEquals(Lexer.TokenType.WHITESPACE, token4.type());
		assertTrue(token4.value().contains(" "));

		Lexer.Token token5 = lexer.yylex();
		assertEquals(Lexer.TokenType.EOF, token5.type());
		
		try {
			java.lang.reflect.Field f = lexer.getClass().getDeclaredField("yyline");
			f.setAccessible(true);
			int yyline = f.getInt(lexer);
			assertEquals(5, yyline, "Número de linhas processadas (yyline) deve ser 5");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Não foi possível acessar yyline no Lexer via reflection", e);
		}
	}

	@Test
	void testUnterminatedComment() throws Exception {
		String code = "(* Este comentario não termina ";
		
		Lexer lexer = new Lexer(new StringReader(code));
		
		assertThrows(
				Lexer.UnterminatedCommentException.class,
				() -> lexer.yylex()
			);
	}

	@Test
	void testUnterminatedStringAtNewline() {
		String code = "\"uma string\n com erro";
		Lexer lexer = new Lexer(new StringReader(code));

		// A exceção deve ser lançada na primeira chamada a yylex()
		assertThrows(
				Lexer.UnterminatedStringException.class,
				() -> lexer.yylex()
		);
	}
}
