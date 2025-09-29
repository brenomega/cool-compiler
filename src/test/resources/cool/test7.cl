-- Arquivo de teste 3 com erro de caractere inválido no identificacdor

class Pessoa{
    nome : String <- "";

    set_nome(n : String) : Pessoa{
        nome <- n; self
    };

    get_nome() : String{
        nome
    };
};

class Main inherits IO {
    eu : Pessoa <- new Pessoa;
    você : Pessoa <- new Pessoa; --caractere inválido no identificador

    main() : Object {
        (* Inicializando os nomes*)
        p1 <- p1.set_nome("André);  -- Erro : Faltando fecha aspas em String
        p2 <- p2.set_nome("Carlos");

        (* Escrevendo os nomes *)
        out_string(p1.get_nome());
        out_string(" é amigo de ");
        out_string(p2.get_nome());
        out_string("\n");

        0
    };

};