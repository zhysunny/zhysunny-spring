package com.zhysunny.spring.servlet;

import com.google.common.base.Splitter;
import com.zhysunny.spring.annotation.Autowired;
import com.zhysunny.spring.annotation.Component;
import com.zhysunny.spring.annotation.RequestMapping;
import com.zhysunny.spring.util.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhysunny
 * @date 2020/6/21 23:07
 */
public class DispatcherServlet extends HttpServlet {
    private final Properties props = new Properties();
    private final List<Class<?>> classes = new ArrayList<>();
    private final Map<String, Object> beans = new HashMap<>();
    private final Map<String, Method> mappings = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("Servlet init start");
        // 1.加载application配置
        doLoadAppConfig(config.getInitParameter("appConfigPath"));
        // 2.扫描相关类
        doScanner(props.getProperty("scanPackage"));
        // 3.IOC容器初始化，相关类实例化
        doInstance();
        // 4.依赖注入
        doAutowired();
        // 5.初始化HandlerMapping
        doHandlerMapping();
        System.out.println("Servlet init end");
    }

    private void doHandlerMapping() {
        beans.entrySet().forEach(entry -> {
            Object bean = entry.getValue();
            Method[] methods = MethodUtils.getMethodsWithAnnotation(bean.getClass(), RequestMapping.class);
            for (Method method : methods) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String url = requestMapping.value();
                mappings.put(url, method);
                System.out.println("url : " + url);
            }
        });
    }

    private void doAutowired() {
        beans.entrySet().forEach(entry -> {
            Object bean = entry.getValue();
            Field[] fields = FieldUtils.getFieldsWithAnnotation(bean.getClass(), Autowired.class);
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(bean, beans.get(getBeanName(field.getType())));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doInstance() {
        classes.forEach(cls -> {
            try {
                Object bean = cls.newInstance();
                beans.put(getBeanName(bean.getClass()), bean);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private String getBeanName(Class<?> cls) {
        String simpleName = cls.getSimpleName();
        return simpleName.substring(0, 1).toLowerCase(Locale.ENGLISH).concat(simpleName.substring(1));
    }

    private void doScanner(String scanPackage) {
        Splitter.on(',').trimResults().splitToStream(scanPackage)
        .forEach(scan -> classes.addAll(ClassUtils.getClasses(scan).stream().filter(cls -> cls.getAnnotation(
        Component.class) != null).collect(Collectors.toList())));
    }

    private void doLoadAppConfig(String appConfigPath) {
        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream(appConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        System.out.println("Servlet destroy");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        if (mappings.containsKey(url)) {
            Method method = mappings.get(url);
            try {
                String beanName = getBeanName(method.getDeclaringClass());
                Object invoke = method.invoke(beans.get(beanName), null);
                resp.getWriter().println(invoke);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            resp.getWriter().println("404 Not Found");
        }
    }
}
