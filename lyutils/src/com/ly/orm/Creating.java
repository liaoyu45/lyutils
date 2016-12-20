package com.ly.orm;

public @interface Creating {

	/**
	 * set first charactor ' ' if want to rename the column
	 * @return
	 */
	String desc();

	int index() default 0;

}
