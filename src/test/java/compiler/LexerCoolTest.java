package compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class LexerCoolTest {

	static class Expected {
		Lexer.TokenType type;
		int line;

		Expected(Lexer.TokenType type, int line) {
			this.type = type;
			this.line = line;
		}
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
}
