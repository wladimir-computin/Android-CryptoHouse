package de.wladimircomputin.libcryptoiot.v2.protocol.api;

enum Datatype{
    BOOL("bool"),
    INT("int"),
    FLOAT("float"),
    STRING("string");

    private String str;

    Datatype(String str) {
        this.str = str;
    }

    public String toString() {
        return str;
    }

    public static Datatype fromString(String str){
        for(Datatype d : Datatype.values()){
            if(str.equals(d.toString())){
                return d;
            }
        }
        return STRING;
    }
}