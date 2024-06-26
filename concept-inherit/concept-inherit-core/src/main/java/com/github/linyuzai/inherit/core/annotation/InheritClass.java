package com.github.linyuzai.inherit.core.annotation;

import com.github.linyuzai.inherit.core.flag.InheritFlag;

import java.lang.annotation.*;

/**
 * 通过该注解来继承指定类的字段和方法
 */
@Repeatable(InheritClasses.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InheritClass {

    /**
     * 继承属性的源 Class
     */
    Class<?>[] sources();

    /**
     * 是否继承父类字段和方法
     */
    boolean inheritSuper() default false;

    /**
     * 排除指定名称的字段
     */
    String[] excludeFields() default {};

    /**
     * 排除指定名称的方法
     */
    String[] excludeMethods() default {};

    /**
     * 继承标识
     */
    InheritFlag[] flags() default {};
}
