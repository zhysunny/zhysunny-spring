package com.zhysunny.spring.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 反射扫描包下面所有的类
 * @author zhysunny
 * @date 2019/2/22 11:09
 */
public class ClassUtils {

    public static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        URL url = ClassUtils.class.getClassLoader().getResource(packageName.replace(".", "/"));
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            findClassLocal(packageName, classes);
        } else if ("jar".equals(protocol)) {
            findClassJar(packageName, classes);
        }
        return classes;
    }

    private static void findClassLocal(String packageName, List<Class<?>> classes) {
        URI uri = null;
        try {
            uri = ClassUtils.class.getClassLoader().getResource(packageName.replace(".", "/")).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        File path = new File(uri);
        findAndAddClasses(packageName, path, classes);
    }

    private static void findAndAddClasses(String packageName, File dir, List<Class<?>> classes) {
        File[] dirfiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClasses(packageName + "." + file.getName(), file, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> cls = Class.forName(packageName + "." + className);
                    if (!"".equals(cls.getSimpleName())) {
                        classes.add(cls);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static void findClassJar(String packageName, List<Class<?>> classes) {
        packageName = packageName.replace(".", "/");
        URL url = ClassUtils.class.getClassLoader().getResource(packageName);
        JarFile jarFile = null;
        try {
            JarURLConnection jarUrlConnection = (JarURLConnection)url.openConnection();
            jarFile = jarUrlConnection.getJarFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //这里获得jar包里面所有目录和文件
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        JarEntry jarEntry = null;
        String jarEntryName = null;
        while (jarEntries.hasMoreElements()) {
            jarEntry = jarEntries.nextElement();
            jarEntryName = jarEntry.getName();
            // jarEntryName的目录结构都是/
            if (jarEntryName.contains(packageName) && !jarEntryName.endsWith("/")) {
                if (jarEntryName.endsWith(".class")) {
                    String className = jarEntryName.substring(0, jarEntryName.length() - 6);
                    try {
                        Class<?> cls = Class.forName(className.replace("/", "."));
                        if (!"".equals(cls.getSimpleName())) {
                            classes.add(cls);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String packageName = ClassUtils.class.getPackage().getName();
        List<Class<?>> classes = getClasses(packageName);
        for (Class<?> cls : classes) {
            System.out.println(cls.getSimpleName());
        }
    }

}
