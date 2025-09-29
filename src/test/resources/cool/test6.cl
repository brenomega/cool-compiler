-- Arquivo 6, erro de comentario aninhado inserido

(* Este é o início de um comentario
    (* este é o aninhamento de um comentario
   Aqui falta o fechamento do comentario aninhado
*)

-- Continuando com um codigo qualquer

class Main inherits IO {
    main() : Object {
        out_string("Ola mundo!");
        0
    };
};