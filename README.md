# Compilador Cool - Guia de Execução no IntelliJ IDEA

Este guia explica como executar o compilador Cool diretamente da IDE IntelliJ IDEA.

## Passo 1: Abrir o Projeto

1.  Abra o IntelliJ IDEA.
2.  Vá em **File > Open...** e selecione o arquivo `pom.xml` que está na pasta do projeto.
3.  Clique em **Open** e escolha a opção **Open as Project**.
4.  Aguarde o IntelliJ sincronizar o projeto (uma barra de progresso aparecerá no canto inferior direito).

## Passo 2: Preparar o Projeto

Antes de executar, é necessário gerar os arquivos do analisador.

1.  No lado direito da IDE, abra a aba **Maven**.
2.  Expanda a seção **Lifecycle**.
3.  Dê um duplo-clique em **`package`**. Isso irá compilar e preparar tudo para a execução.

## Passo 3: Executar a Análise

A execução é feita através da classe `Main`, basta se dirigir para src/main/java/compiler/Main.java e clicar no símbolo de "play" na parte superior da tela, ou pressionar Shift+F10.