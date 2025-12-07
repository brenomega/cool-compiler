package compiler;

import java.io.FileReader;
import java_cup.runtime.Symbol;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);

        System.out.print("Digite o número do teste que deseja executar: ");
        String input = console.nextLine().trim();

        int testNumber;
        try {
            testNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.err.println("Entrada inválida. O programa espera um número inteiro.");
            return;
        }

        String fileName = "test" + testNumber + ".cl";
        Path inputPath = Paths.get("src", "test", "resources", "cool", fileName);

        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            System.err.println("Verifique se você criou o arquivo " + fileName + " na pasta src/test/resources/cool/");
            return;
        }

        System.out.println("\n-----------------------------------------");
        System.out.println("Lendo arquivo: " + fileName);
        System.out.println("-----------------------------------------");

        try {
            Lexer scanner = new Lexer(new FileReader(inputPath.toFile()));
            Parser parser = new Parser(scanner);

            System.out.println("Iniciando análise sintática...");

            Symbol result = parser.parse();

            System.out.println("\nO código é válido sintaticamente!");
            System.out.println("-----------------------------------------");

        } catch (Exception e) {
            System.err.println("\nErro encontrado durante a execução.");
            System.err.println(e.toString());
        } finally {
            console.close();
        }
    }
}