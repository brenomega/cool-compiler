-- simple line comment
(* simple block comment *)

cLasS Main inherits IO {
    x : Int <- 10;  -- simple assign
    y : String <- "Hello, COOL!\nNew line";

    (* Block comment
       new line (* nested comment *)
       end *)
    z : Bool <- true;

    if x <= 100 then
        y <- "Greater than 100";
    else
        y <- "Less or equal 100";
    fi;

    while 0 < x loop
        x <- x - 1;
    pool;
}