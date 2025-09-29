package lexer.tools;

import compiler.Lexer.*;
import compiler.Lexer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
                boolean ignorar_wp;
                System.out.print("Digite o número do teste (1 a 7) ou 'e' para sair: ");
                String input = scanner.nextLine().trim();

                System.out.println("Deseja ignorar os tokens WHITESPACE? (S/N)");
                String input_wp = scanner.nextLine().trim();
                if (input_wp.equalsIgnoreCase("S")) {
                    ignorar_wp = true;
                }else if (input_wp.equalsIgnoreCase("N")) {
                    ignorar_wp = false;
                }else{
                    ignorar_wp = false;
                    System.out.println("Opção inválida, ignorando WHISTESPACE por padrão.");
                }

                if (input.equalsIgnoreCase("e")) {
                    System.out.println("Saindo.");
                    break;
                }

                int n;
                try {
                    n = Integer.parseInt(input);
                    if (n < 0) {
                        System.out.println("Número negativo não permitido. Usando 0 (default).");
                        n = 0;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Entrada inválida — usando 0 (default).");
                    n = 0;
                }

                String inputFileName = (n == 0) ? "test_default.cl" : ("test" + n + ".cl");
                String outputFileName = "test" + n + "_output.txt";
                Path inputDir = Paths.get("src", "test", "resources", "cool");
                Path outputDir = Paths.get("target", "test-outputs", "lexer");
                Path inputPath = inputDir.resolve(inputFileName);
                Path outputPath = outputDir.resolve(outputFileName);

                if (!Files.exists(inputPath)) {
                    System.out.println("Arquivo de entrada não encontrado: " + inputPath.toString());
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

                    while (true) {
                        Token token;
                        try {
                            token = lexer.yylex();
                        } catch (LexicalException lexEx) {
                            String msg = lexEx.getClass().getSimpleName() + ": " + lexEx.getMessage();
                            System.out.println(msg);
                            writer.write(msg);
                            writer.newLine();
                            writer.flush();
                            System.out.println("Arquivo de saída criado: " + outputPath);
                            break;
                        } catch (ArrayIndexOutOfBoundsException ex){
                            int line = lexer.getLine();
                            String msg = String.format(
                                    "%s: NonAsciiChar - caractere fora do ASCII (0-127) detectado na linha %d.",
                                    ex.getClass().getSimpleName(),
                                    line
                            );
                            System.out.println(msg);
                            writer.write(msg);
                            writer.newLine();
                            writer.flush();
                            System.out.println("Arquivo de saída criado: " + outputPath);
                            break;
                        }

                        if (ignorar_wp && token.type() == TokenType.WHITESPACE) {
                            continue;
                        }

                        String outLine = String.format("Line %d: %s", lexer.getLine(), token.toString());
                        System.out.println(outLine);

                        writer.write(outLine);
                        writer.newLine();
                        writer.flush();

                        if (token.type() == TokenType.EOF) {
                            String doneMsg = "EOF alcançado. Fim da análise léxica.";
                            System.out.println(doneMsg);
                            System.out.println("Arquivo de saída criado: " + outputPath);
                            break;
                        }
                    }

                } catch (IOException ioEx) {
                    System.out.println("IOException ao abrir/escrever arquivo de saída: " + ioEx.getMessage());
                }
            }
        } finally {
            scanner.close();
        }
    }
}
