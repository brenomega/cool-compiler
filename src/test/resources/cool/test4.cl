-- Arquivo de teste 3 com erros lexicos

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
    p1 : Pessoa <- new Pessoa;
    p2 : Pessoa <- new Pessoa;

    main() : Object {
        (* Inicializando os nomes*)
        p1 <- p1.set_nome("Andre);  -- Erro : Faltando fecha aspas em String
        p2 <- p2.set_nome("Carlos");

        (* Escrevendo os nomes *)
        out_string(p1.get_nome());
        out_string(" eh amigo de ");
        out_string(p2.get_nome());
        out_string("\n");

        0
    };

};