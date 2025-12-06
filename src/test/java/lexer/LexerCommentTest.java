package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.lang.reflect.Field;

import compiler.Lexer;
import compiler.sym;
import java_cup.runtime.Symbol;
import org.junit.jupiter.api.Test;

public class LexerCommentTest {

	@Test
	void testCommentsAndWhitespace() throws Exception {
		// O código contém apenas comentários e espaços.
		// O Lexer deve consumir tudo isso sem retornar nenhum token,
		// até chegar no EOF.
		String code = ""
				+ "-- comentario de linha\n"
				+ "(* comentario de bloco *)\n"
				+ "(* comentario \n aninhado (* interno *) final *)\n"
				+ "   \t  \n";

		Lexer lexer = new Lexer(new StringReader(code));

		// Ação: O lexer deve processar tudo silenciosamente e retornar EOF
		Symbol token = lexer.next_token();

		// Verificação: O único token retornado deve ser o de fim de arquivo
		assertEquals(sym.EOF, token.sym, "Deveria ter ignorado todos os comentários e retornado EOF");

		// Verificação: O Lexer deve ter contado as linhas corretamente (5 quebras de linha)
		try {
			Field f = lexer.getClass().getDeclaredField("yyline");
			f.setAccessible(true);
			int yyline = f.getInt(lexer);
			// Nota: yyline começa em 0. Se houve 5 '\n', yyline deve ser 5.
			assertEquals(5, yyline, "Número de linhas processadas (yyline) deve ser 5");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Não foi possível acessar yyline no Lexer via reflection", e);
		}
	}

	@Test
	void testUnterminatedComment() {
		String code = "(* Este comentario nao termina ";

		Lexer lexer = new Lexer(new StringReader(code));

		// Ao tentar pegar o próximo token, o lexer vai ler até o fim e perceber que o comentário não fechou
		assertThrows(
				Lexer.UnterminatedCommentException.class,
				() -> lexer.next_token()
		);
	}

	@Test
	void testUnterminatedStringAtNewline() {
		String code = "\"uma string\n com erro";
		Lexer lexer = new Lexer(new StringReader(code));

		// A exceção deve ser lançada imediatamente ao encontrar a quebra de linha dentro da string
		assertThrows(
				Lexer.UnterminatedStringException.class,
				() -> lexer.next_token()
		);
	}
}