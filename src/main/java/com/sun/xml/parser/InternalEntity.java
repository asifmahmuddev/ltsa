package com.sun.xml.parser;

class InternalEntity extends EntityDecl {
    char[] buf;

    InternalEntity(String paramString, char[] paramArrayOfchar) {
        this.name = paramString;
        this.buf = paramArrayOfchar;
    }
}
