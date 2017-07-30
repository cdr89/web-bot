package it.caldesi.webbot.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ArgumentType {

	public enum Type {
		STRING, INTEGER
	}

	Type type() default Type.STRING;

	boolean nullable() default false;

	boolean onlyPositive() default false;

}