package com.auto.gen.junit.autoj.translator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Translate {
    String value() default "MyString";
    int integer() default 1;
    boolean isTrue() default true;

}
