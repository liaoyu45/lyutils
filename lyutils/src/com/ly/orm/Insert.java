package com.ly.orm;

public @interface Insert {

	String desc();

	int index() default 0;

}
