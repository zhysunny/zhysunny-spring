package com.zhysunny.spring.controller;

import com.zhysunny.spring.annotation.Autowired;
import com.zhysunny.spring.annotation.Component;
import com.zhysunny.spring.annotation.RequestMapping;
import com.zhysunny.spring.service.HelloService;

/**
 * @author zhysunny
 * @date 2020/6/21 23:54
 */
@Component
public class HelloController {
    @Autowired
    private HelloService helloService;

    @RequestMapping(value = "/get")
    public String get() {
        return helloService.get();
    }

}
