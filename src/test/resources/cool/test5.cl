(*
Arquivo Teste 2 com erro
    (*
        caractere inválido
    *)
*)

class Main inherits IO {

    nome     : String;
    primeiro : Int;
    segundo  : Int;
    media    : Int;

    main() : Object {
        -- Entrada do usuário
        out_string("Digite seu nome: ");
        nome <- in_string();

        out_string("Digite o primeiro inteiro: ");
        primeiro <- in_int();

        out_string("Digite o segundo inteiro: ");
        segundo <- in_int();

        -- Média por divisão inteira
        media <- (primeiro + segundo) / 2;

        out_string("A media é: ");
        out_int(media);
        out_string("\n");

        (*
          Incrementando a média 10 vezes.
          Declarando variável local para o while com let
        *)
        let i : Int <- 0 in
          while i < 10 loop
            media <- media + 1;
            out_string("Incrementando a media.\n");
            i <- i + 1
          pool
        ;

        -- Erro léxico inserido, caracter ">" não é reconhecido
        if media > 15 then
          out_string("A media final é maior que 15.\n")
        else
          out_string("A media final não é maior que 15.\n")
        fi;

        out_string(nome);
        out_string(", a media incrementada é: ");
        out_int(media);
        out_string("\n");

        0  -- retorno para main()
    };
};