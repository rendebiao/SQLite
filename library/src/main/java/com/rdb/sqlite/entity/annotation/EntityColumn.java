package com.rdb.sqlite.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityColumn {

    /**
     * 是否屏蔽 不存入数据库
     *
     * @return
     */
    boolean hide() default false;

    /**
     * 是否主键
     *
     * @return
     */
    boolean primary() default false;

    /**
     * 是否自增长
     *
     * @return
     */
    boolean autoIncrement() default false;

    /**
     * 是否可空
     *
     * @return
     */
    boolean nullable() default false;
}
