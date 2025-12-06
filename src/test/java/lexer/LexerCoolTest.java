package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.StringReader;
import java.lang.reflect.Field;

import compiler.Lexer;
import compiler.sym;
import java_cup.runtime.Symbol;
import org.junit.jupiter.api.Test;

public class LexerCoolTest {

	private void assertCurrentLine(int expectedLine, Lexer lexer) {
		try {
			Field f = lexer.getClass().getDeclaredField("yyline");
			f.setAccessible(true);
			int yyline = f.getInt(lexer);
			assertEquals(expectedLine, yyline, "Número de linhas (yyline) incorreto");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Não foi possível acessar yyline no Lexer via reflection", e);
		}
	}

	private void assertToken(int expectedSym, String expectedValue, Symbol token) {
		assertNotNull(token, "Token não deveria ser nulo");
		assertEquals(expectedSym, token.sym, "Tipo do token incorreto (" + expectedSym + " vs " + token.sym + ")");

		// Só verificamos o valor se esperarmos algum.
		// Se o token for um operador (ex: <-), o value é null, então passamos expectedValue = null no teste.
		if (expectedValue != null) {
			assertNotNull(token.value, "Valor do token é null, mas esperava: " + expectedValue);
			assertEquals(expectedValue, token.value.toString(), "Valor do token incorreto");
		}
	}

	private void assertToken(int expectedSym, int expectedValue, Symbol token) {
		assertNotNull(token, "Token não deveria ser nulo");
		assertEquals(expectedSym, token.sym, "Tipo do token incorreto");
		assertEquals(expectedValue, token.value, "Valor inteiro do token incorreto");
	}

	@Test
	void testSimpleExpression() throws Exception {
		String code = "var <- 123;";
		Lexer lexer = new Lexer(new StringReader(code));

		// Token 1: var (ID) -> Tem valor "var"
		Symbol t1 = lexer.next_token();
		assertToken(sym.ID, "var", t1);

		// Token 2: <- (ASSIGN) -> Não tem valor (é null), passamos null no esperado
		Symbol t2 = lexer.next_token();
		assertToken(sym.ASSIGN, null, t2);

		// Token 3: 123 (INT_CONST) -> Tem valor 123
		Symbol t3 = lexer.next_token();
		assertToken(sym.INT_CONST, 123, t3);

		// Token 4: ; (SEMI) -> NÃO tem valor
		Symbol t4 = lexer.next_token();
		assertToken(sym.SEMI, null, t4);

		// Token 5: EOF
		Symbol t5 = lexer.next_token();
		assertEquals(sym.EOF, t5.sym);
	}

	@Test
	void testTrueFalseSpecialHandling() throws Exception {
		String code = "true fALSE True";
		Lexer lexer = new Lexer(new StringReader(code));

		Symbol t1 = lexer.next_token();
		assertEquals(sym.TRUE, t1.sym);
		assertEquals(true, t1.value);

		Symbol t2 = lexer.next_token();
		assertEquals(sym.FALSE, t2.sym);
		assertEquals(false, t2.value);

		Symbol t3 = lexer.next_token();
		assertToken(sym.TYPEID, "True", t3);

		Symbol t4 = lexer.next_token();
		assertEquals(sym.EOF, t4.sym);
	}

	@Test
	void testKeywordCaseInsensitivity() throws Exception {
		String code = "Class IF\ntHeN";
		Lexer lexer = new Lexer(new StringReader(code));

		// Keywords não retornam valor, apenas o tipo
		Symbol t1 = lexer.next_token();
		assertEquals(sym.CLASS, t1.sym);

		Symbol t2 = lexer.next_token();
		assertEquals(sym.IF, t2.sym);

		Symbol t3 = lexer.next_token();
		assertEquals(sym.THEN, t3.sym);

		Symbol t4 = lexer.next_token();
		assertEquals(sym.EOF, t4.sym);
	}

	@Test
	void testNegationOperator() throws Exception {
		String code = "~42";
		Lexer lexer = new Lexer(new StringReader(code));

		// Operador NEG (~) não tem valor
		Symbol t1 = lexer.next_token();
		assertEquals(sym.NEG, t1.sym);

		Symbol t2 = lexer.next_token();
		assertToken(sym.INT_CONST, 42, t2);

		Symbol t3 = lexer.next_token();
		assertEquals(sym.EOF, t3.sym);
	}
}