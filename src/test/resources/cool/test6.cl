-- Arquivo 6, erro de comentario aninhado inserido

(* Este e o inicio de um comentario
    (* este eh o aninhamento de um comentario
   Aqui falta o fechamento do comentario aninhado
*)

-- Continuando com um codigo qualquer

class Main inherits IO {
    main() : Object {
        out_string("Ola mundo!");
        0
    };
};