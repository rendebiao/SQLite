package com.rdb.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityColumn {

    /**
     * 是否主键
     */
    boolean primary() default false;

    /**
     * 是否自增长
     */
    boolean autoIncrement() default false;

    /**
     * 是否可空
     */
    boolean nullable() default false;

    /**
     * 是否隐藏
     */
    boolean hidden() default false;
}
