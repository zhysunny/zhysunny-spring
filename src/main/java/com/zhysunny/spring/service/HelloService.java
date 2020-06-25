package com.zhysunny.spring.service;

import com.zhysunny.spring.annotation.Component;

/**
 * @author zhysunny
 * @date 2020/6/21 23:54
 */
@Component
public class HelloService {
    public String get() {
        return "<h1>hello spring</h1>";
    }
}
