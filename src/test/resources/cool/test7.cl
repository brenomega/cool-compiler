(* Arquivo de Teste com Erro em Caractere Inválido *)

class Pessoa {
    nome : String <- "";
    set_nome(n : String) : Pessoa {
        { nome <- n; self; }
    };
    get_nome() : String { nome };
};

class Main inherits IO {
    eu : Pessoa <- new Pessoa;

    -- O ERRO ESPERADO ESTÁ AQUI (Identificador com acento 'ê' não é ASCII):
    você : Pessoa <- new Pessoa;

    main() : Object {
        {
            out_string("Teste de identificador invalido");
            0;
        }
    };
};