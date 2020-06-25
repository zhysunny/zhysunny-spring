package com.zhysunny.spring.annotation;

import java.lang.annotation.*;

/**
 * @author zhysunny
 * @date 2020/6/26 0:23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Autowired {
}
