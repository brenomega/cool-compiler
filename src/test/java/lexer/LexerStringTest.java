package lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import compiler.Lexer;
import compiler.sym;            // Constantes dos tokens (gerado pelo CUP)
import java_cup.runtime.Symbol; // Classe padrão de token do CUP
import org.junit.jupiter.api.Test;

public class LexerStringTest {

	@Test
	void testStrings() throws Exception {
		String code = Files.readString(Paths.get("src/test/resources/strings/test1.cl"));

		Lexer lexer = new Lexer(new StringReader(code));

		Symbol token = lexer.next_token();
		assertEquals(sym.STR_CONST, token.sym);
		assertEquals("Isso esta OK.\nTeste para tab\tfuncionou.\nTeste para barra invertida \\ funcionou.\nTeste para aspas \" funcionou.", token.value.toString());
	}

	@Test
	void testMaxLength() throws Exception {
		String code = Files.readString(Paths.get("src/test/resources/strings/test2.txt"));

		Lexer lexer = new Lexer(new StringReader(code));

		Lexer.StringTooLongException thrown = assertThrows(
				Lexer.StringTooLongException.class,
				() -> lexer.next_token()
		);

		assertTrue(thrown.getMessage().contains("String excede 1024 caracteres"));
	}

	@Test
	void testNullChar() throws Exception {
		String code = Files.readString(Paths.get("src/test/resources/strings/test3.txt"));

		Lexer lexer = new Lexer(new StringReader(code));

		Lexer.NullCharInStringException thrown = assertThrows(
				Lexer.NullCharInStringException.class,
				() -> lexer.next_token()
		);

		assertTrue(thrown.getMessage().contains("String contém \\0"));
	}

	@Test
	void testUnterminatedString() throws Exception {
		String code = Files.readString(Paths.get("src/test/resources/strings/test4.txt"));

		Lexer lexer = new Lexer(new StringReader(code));

		assertThrows(
				Lexer.UnterminatedStringException.class,
				() -> lexer.next_token()
		);
	}
}