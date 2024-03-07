package com.cm.maker.meta;

/**
 * @author 语仄无言
 */
public class MetaException extends RuntimeException {

    public MetaException(String message){
        super(message);
    }

    public MetaException(String message,Throwable cause){
        super(message, cause);
    }
}
