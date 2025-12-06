(* Arquivo Teste com erro de string *)

class Pessoa {
    nome : String <- "";
    set_nome(n : String) : Pessoa {
        { nome <- n; self; }
    };
    get_nome() : String { nome };
};

class Main inherits IO {
    p1 : Pessoa <- new Pessoa;
    p2 : Pessoa <- new Pessoa;

    main() : Object {
        (* Inicializando os nomes *)
        p1 <- p1.set_nome("Andre);
        p2 <- p2.set_nome("Carlos");

        0;
    };
};