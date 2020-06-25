package com.zhysunny.spring.servlet;

import com.zhysunny.spring.controller.HelloController;
import com.zhysunny.spring.service.HelloService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import static org.junit.Assert.*;

/**
 * DispatcherServlet Test
 */
public class DispatcherServletTest {

    private DispatcherServlet servlet;

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.out.println("Test DispatcherServlet Class Start...");
    }

    @Before
    public void before() throws Exception {
        servlet = new DispatcherServlet();
    }

    @After
    public void after() throws Exception {
    }

    @AfterClass
    public static void afterClass() throws Exception {
        System.out.println("Test DispatcherServlet Class End...");
    }

    @Test
    public void testDoHandlerMapping() throws Exception {
        Map<String, Object> beans = (Map<String, Object>)FieldUtils.getDeclaredField(servlet.getClass(), "beans", true).get(servlet);
        beans.put("helloService", new HelloService());
        beans.put("helloController", new HelloController());
        MethodUtils.invokeMethod(servlet, true, "doHandlerMapping");
        Map<String, Method> mappings = (Map<String, Method>)FieldUtils.getDeclaredField(servlet.getClass(), "mappings", true)
        .get(servlet);
        assertTrue(mappings.size() > 0);
    }

    @Test
    public void testDoAutowired() throws Exception {
        Map<String, Object> beans = (Map<String, Object>)FieldUtils.getDeclaredField(servlet.getClass(), "beans", true).get(servlet);
        beans.put("helloService", new HelloService());
        beans.put("helloController", new HelloController());
        MethodUtils.invokeMethod(servlet, true, "doAutowired");
        HelloController helloController = (HelloController)beans.get("helloController");
        HelloService helloService = (HelloService)FieldUtils.getDeclaredField(helloController.getClass(), "helloService", true)
        .get(helloController);
        assertNotNull(helloService);
        assertTrue(beans.get("helloService") == helloService);
    }

    /**
     * Method: doInstance()
     */
    @Test
    public void testDoInstance() throws Exception {
        List<Class<?>> classes = (List<Class<?>>)FieldUtils.getDeclaredField(servlet.getClass(), "classes", true).get(servlet);
        classes.add(HelloController.class);
        MethodUtils.invokeMethod(servlet, true, "doInstance");
        Map<String, Object> beans = (Map<String, Object>)FieldUtils.getDeclaredField(servlet.getClass(), "beans", true).get(servlet);
        assertTrue(beans.size() > 0);
    }

    @Test
    public void testGetBeanName() throws Exception {
        HelloController helloController = new HelloController();
        String beanName = (String)MethodUtils
        .invokeMethod(servlet, true, "getBeanName", new Object[]{ helloController.getClass() }, new Class[]{ Class.class });
        assertEquals("helloController", beanName);
    }

    /**
     * Method: doScanner(String scanPackage)
     */
    @Test
    public void testDoScanner() throws Exception {
        String scanPackage = "com.zhysunny.spring.controller";
        MethodUtils.invokeMethod(servlet, true, "doScanner", new Object[]{ scanPackage }, new Class[]{ String.class });
        List<Class<?>> classes = (List<Class<?>>)FieldUtils.getDeclaredField(servlet.getClass(), "classes", true).get(servlet);
        assertTrue(classes.size() > 0);
    }

    /**
     * Method: doLoadAppConfig(String appConfigPath)
     */
    @Test
    public void testDoLoadAppConfig() throws Exception {
        MethodUtils.invokeMethod(servlet, true, "doLoadAppConfig", new Object[]{ "application.properties" }, new Class[]{ String.class });
        Properties props = (Properties)FieldUtils.getDeclaredField(servlet.getClass(), "props", true).get(servlet);
        assertNotNull(props.getProperty("scanPackage"));
    }

} 
