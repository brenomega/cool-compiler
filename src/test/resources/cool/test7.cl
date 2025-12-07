(* Arquivo de Teste com Erro: palavra reservada como identificador *)

class Pessoa {
    self : String <- "";
    set_nome(n : String) : Pessoa {
        { nome <- n; self; }
    };
    get_nome() : String { nome };
};

class Main inherits IO {
    eu : Pessoa <- new Pessoa;
    voce : Pessoa <- new Pessoa;

    main() : Object {
        {
            out_string("Teste de identificador invalido");
            0;
        }
    };
};