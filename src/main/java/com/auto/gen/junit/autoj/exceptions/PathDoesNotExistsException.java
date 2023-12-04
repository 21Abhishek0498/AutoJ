package com.auto.gen.junit.autoj.exceptions;

public class PathDoesNotExistsException extends  RuntimeException{

    public PathDoesNotExistsException(String path){
        super("This path does not exists. Please check and provide the correct path: " + path);
    }
}
