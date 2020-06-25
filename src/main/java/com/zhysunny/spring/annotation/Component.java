package com.zhysunny.spring.annotation;

import java.lang.annotation.*;

/**
 * @author zhysunny
 * @date 2020/6/26 0:59
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {
}
