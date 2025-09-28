package compiler;

import compiler.Lexer;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main {

    private static Path escolher_arquivo(){
        Scanner scanner = new Scanner(System.in);
        Path caminho = Paths.get("src/test/resources/cool");
        File[] arquivos = caminho.toFile().listFiles();
        System.out.println("Escolha uma arquivo:");


        for (int i = 0; i < arquivos.length; i++){
            System.out.println(i+1 + " - " + arquivos[i].getName());
        }

        int arq = scanner.nextInt() - 1;

        return arquivos[arq].toPath();
    }


    public static void main(String[] args) {
        System.out.println("");
        try {
            Reader r = Files.newBufferedReader(escolher_arquivo());
            Lexer lex =  new Lexer(r);

            while (true) {
                Lexer.Token t = lex.yylex();
                if (t == null || t.type() == Lexer.TokenType.EOF) {
                    System.out.println("EOF");
                    break;
                }
                // pule espaços/comentários; remova este if se quiser ver tudo
                //if (t.type() == Lexer.TokenType.WHITESPACE || t.type() == Lexer.TokenType.COMMENT) {
                  //  continue;
                //}//
                System.out.println("line " + (lex.getLine() + 1) + ": " + t);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
