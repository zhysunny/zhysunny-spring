package com.zhysunny.spring.annotation;

import java.lang.annotation.*;

/**
 * @author zhysunny
 * @date 2020/6/26 0:26
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {
    String value() default "";
}
