package lexer.tools;

import compiler.Lexer;
import compiler.sym;             // Importa as constantes dos tokens
import java_cup.runtime.Symbol;  // Importa a classe padrão de token do CUP

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class LexerDriver {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        try {
            while (true) {
                System.out.println("\n--- Driver do Lexer (Modo CUP) ---");
                System.out.print("Digite o número do teste (1 a 7) ou 'e' para sair: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("e")) {
                    System.out.println("Saindo.");
                    break;
                }

                int n;
                try {
                    n = Integer.parseInt(input);
                    if (n < 0) n = 0;
                } catch (NumberFormatException ex) {
                    System.out.println("Entrada inválida — usando 0 (default).");
                    n = 0;
                }

                String inputFileName = (n == 0) ? "test_default.cl" : ("test" + n + ".cl");
                String outputFileName = "test" + n + "_output.txt";

                Path inputDir = Paths.get("src", "test", "resources", "cool");
                Path outputDir = Paths.get("target", "test-outputs", "lexer");

                // Cria diretório de saída se não existir
                Files.createDirectories(outputDir);

                Path inputPath = inputDir.resolve(inputFileName);
                Path outputPath = outputDir.resolve(outputFileName);

                if (!Files.exists(inputPath)) {
                    System.out.println("Arquivo de entrada não encontrado: " + inputPath.toAbsolutePath());
                    continue;
                }

                String code;
                try {
                    code = Files.readString(inputPath, StandardCharsets.UTF_8);
                } catch (IOException ioe) {
                    System.out.println("IOException ao ler arquivo: " + ioe.getMessage());
                    continue;
                }

                try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {

                    Reader reader = new StringReader(code);
                    Lexer lexer = new Lexer(reader);

                    System.out.println("\nIniciando análise...");

                    while (true) {
                        Symbol token;
                        try {
                            token = lexer.next_token();
                        } catch (Exception lexEx) {
                            String msg = "ERRO LÉXICO: " + lexEx.getMessage();
                            System.out.println(msg);
                            writer.write(msg);
                            writer.newLine();
                            break;
                        }

                        if (token.sym == sym.EOF) {
                            String doneMsg = "EOF alcançado. Fim da análise léxica.";
                            System.out.println(doneMsg);
                            break;
                        }

                        // Formatação para saída (Recupera nome do token e valor)
                        String tokenName = getSymbolName(token.sym);
                        String valueStr = (token.value != null) ? "('" + token.value + "')" : "";

                        // token.left armazena a linha (configurado no Lexer.flex)
                        String outLine = String.format("Line %d: %s%s", token.left, tokenName, valueStr);

                        System.out.println(outLine);
                        writer.write(outLine);
                        writer.newLine();
                    }

                    writer.flush();
                    System.out.println("Arquivo de saída gerado em: " + outputPath.toAbsolutePath());

                } catch (IOException ioEx) {
                    System.out.println("IOException ao escrever arquivo de saída: " + ioEx.getMessage());
                }
            }
        } finally {
            scanner.close();
        }
    }

    private static String getSymbolName(int symId) {
        try {
            for (Field field : sym.class.getFields()) {
                if (field.getType() == int.class && field.getInt(null) == symId) {
                    return field.getName();
                }
            }
        } catch (Exception e) {
            return "UNKNOWN_SYM(" + symId + ")";
        }
        return String.valueOf(symId);
    }
}