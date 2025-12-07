(* Arquivo Teste 2 *)

class Main inherits IO {

    name     : String;
    primeiro : Int;
    segundo  : Int;
    media    : Int;

    main() : Object {
        {
            -- Entrada do usuario
            out_string("Digite seu nome: ");
            name <- in_string();

            out_string("Digite o primeiro inteiro: ");
            primeiro <- in_int();

            out_string("Digite o segundo inteiro: ");
            segundo <- in_int();

            -- Media por divisao inteira
            media <- (primeiro + segundo) / 2;

            out_string("A media eh: ");
            out_int(media);
            out_string("\n");

            (* Incrementando a media 10 vezes. *)
            let i : Int <- 0 in
              while i < 10 loop
                { -- CHAVES OBRIGATORIAS PARA BLOCO
                    media <- media + 1;
                    out_string("Incrementando a media.\n");
                    i <- i + 1;
                }
              pool;

            -- Em cool nao existe '>', testamos 15 < media
            if 15 < media then
              out_string("A media final eh maior que 15.\n")
            else
              out_string("A media final nao eh maior que 15.\n")
            fi;

            out_string(nome);
            out_string(", a media incrementada eh: ");
            out_int(media);
            out_string("\n");

            0;  -- retorno para main()
        }
    };
};