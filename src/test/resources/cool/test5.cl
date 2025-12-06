(* Arquivo de Teste com Erro Sintatico (Falta FI) *)

class Main inherits IO {
    main() : Object {
        if 1 < 2 then
            out_string("Menor")
        else
            out_string("Maior")
    };
};