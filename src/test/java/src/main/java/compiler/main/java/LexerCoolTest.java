package src.main.java.compiler.main.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compiler.Lexer;
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

	static class Expected {
		Lexer.TokenType type;
		int line;

		Expected(Lexer.TokenType type, int line) {
			this.type = type;
			this.line = line;
		}
	}

	@Test
	void testSimpleExpression() throws Exception {
		String code = "var <- 123;";
		Lexer lexer = new Lexer(new StringReader(code));

		// Token 1: var (OBJECTID)
		Lexer.Token t1 = lexer.yylex();
		assertEquals(Lexer.TokenType.OBJECTID, t1.type());
		assertEquals("var", t1.value());

		// Token 2: ' ' (WHITESPACE)
		Lexer.Token t2 = lexer.yylex();
		assertEquals(Lexer.TokenType.WHITESPACE, t2.type());
		assertEquals(" ", t2.value());

		// Token 3: <- (ASSIGN)
		Lexer.Token t3 = lexer.yylex();
		assertEquals(Lexer.TokenType.ASSIGN, t3.type());
		assertEquals("<-", t3.value());

		// Token 4: ' ' (WHITESPACE)
		Lexer.Token t4 = lexer.yylex();
		assertEquals(Lexer.TokenType.WHITESPACE, t4.type());
		assertEquals(" ", t4.value());

		// Token 5: 123 (INT)
		Lexer.Token t5 = lexer.yylex();
		assertEquals(Lexer.TokenType.INT, t5.type());
		assertEquals("123", t5.value());

		// Token 6: ; (SEMI)
		Lexer.Token t6 = lexer.yylex();
		assertEquals(Lexer.TokenType.SEMI, t6.type());
		assertEquals(";", t6.value());

		// Token 7: EOF
		Lexer.Token t7 = lexer.yylex();
		assertEquals(Lexer.TokenType.EOF, t7.type());

		// Verifica a contagem de linhas (deve ser 0, pois não há \n)
		assertCurrentLine(0, lexer);
	}

	@Test
	void testTrueFalseSpecialHandling() throws Exception {
		String code = "true fALSE True";
		Lexer lexer = new Lexer(new StringReader(code));

		// Token 1: true (TRUE)
		Lexer.Token t1 = lexer.yylex();
		assertEquals(Lexer.TokenType.TRUE, t1.type());
		assertEquals("true", t1.value());

		// Token 2: ' ' (WHITESPACE)
		Lexer.Token t2 = lexer.yylex();
		assertEquals(Lexer.TokenType.WHITESPACE, t2.type());
		assertEquals(" ", t2.value());

		// Token 3: fALSE (FALSE)
		Lexer.Token t3 = lexer.yylex();
		assertEquals(Lexer.TokenType.FALSE, t3.type());
		assertEquals("false", t3.value());

		// Token 4: ' ' (WHITESPACE)
		Lexer.Token t4 = lexer.yylex();
		assertEquals(Lexer.TokenType.WHITESPACE, t4.type());
		assertEquals(" ", t4.value());

		// Token 5: True (TYPEID)
		Lexer.Token t5 = lexer.yylex();
		assertEquals(Lexer.TokenType.TYPEID, t5.type());
		assertEquals("True", t5.value());

		// Token 6: EOF
		Lexer.Token t6 = lexer.yylex();
		assertEquals(Lexer.TokenType.EOF, t6.type());

		// Verifica a contagem de linhas (deve ser 0)
		assertCurrentLine(0, lexer);
	}

	@Test
	void testKeywordCaseInsensitivity() throws Exception {
		String code = "Class IF\ntHeN";
		Lexer lexer = new Lexer(new StringReader(code));

		// Token 1: class (CLASS)
		Lexer.Token t1 = lexer.yylex();
		assertEquals(Lexer.TokenType.CLASS, t1.type());
		assertEquals("class", t1.value());

		// Token 2: ' ' (WHITESPACE)
		Lexer.Token t2 = lexer.yylex();
		assertEquals(Lexer.TokenType.WHITESPACE, t2.type());
		assertEquals(" ", t2.value());

		// Token 3: IF (IF)
		Lexer.Token t3 = lexer.yylex();
		assertEquals(Lexer.TokenType.IF, t3.type());
		assertEquals("if", t3.value());

		// Token 4: tHeN (THEN)
		Lexer.Token t4 = lexer.yylex();
		assertEquals(Lexer.TokenType.THEN, t4.type());
		assertEquals("then", t4.value());

		// Token 5: EOF
		Lexer.Token t5 = lexer.yylex();
		assertEquals(Lexer.TokenType.EOF, t5.type());

		// Verifica a contagem de linhas (deve ser 1, por causa do \n)
		assertCurrentLine(1, lexer);
	}

	@Test
	void testLexerAgainstOutputFile() throws Exception {
		String code = Files.readString(Paths.get("src/test/resources/cool/test1.txt"));
		Lexer lexer = new Lexer(new StringReader(code));

		List<String> lines = Files.readAllLines(Paths.get("src/test/resources/cool/test1_output.txt"));
		List<Expected> expectedTokens = new ArrayList<>();

		Pattern pattern = Pattern.compile("Line (\\d+): (\\w+)\\('.*'\\)");

		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty())
				continue;
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
				int lineNum = Integer.parseInt(m.group(1));
				String tokenTypeStr = m.group(2);
				Lexer.TokenType type = Lexer.TokenType.valueOf(tokenTypeStr);
				expectedTokens.add(new Expected(type, lineNum));
			}
		}

		for (Expected exp : expectedTokens) {
			Lexer.Token token = lexer.yylex();
			assertNotNull(token, "Lexer retornou null");
			assertEquals(exp.type, token.type(), "Erro no tipo de token na linha: " + exp.line);
			assertEquals(exp.line, lexer.getLine(), "Erro no número da linha para o token: " + token);
		}

		Lexer.Token eof = lexer.yylex();
		assertEquals(Lexer.TokenType.EOF, eof.type(), "EOF esperado ao final");
	}

	@Test
	void testNegationOperator() throws Exception {
		String code = "~42";
		Lexer lexer = new Lexer(new StringReader(code));

		Lexer.Token t1 = lexer.yylex();
		assertEquals(Lexer.TokenType.NEG, t1.type(), "O primeiro token deve ser do tipo NEG");
		assertEquals("~", t1.value(), "O valor do token NEG deve ser '~'");

		Lexer.Token t2 = lexer.yylex();
		assertEquals(Lexer.TokenType.INT, t2.type(), "O segundo token deve ser do tipo INT");
		assertEquals("42", t2.value(), "O valor do token INT deve ser '42'");

		Lexer.Token t3 = lexer.yylex();
		assertEquals(Lexer.TokenType.EOF, t3.type(), "O último token deve ser EOF");

		assertCurrentLine(0, lexer);
	}
}
