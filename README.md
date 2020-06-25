# 手写spring

## 工程简介

以阅读spring源码为目标，先猜想，再验证，通过手写spring的方式理解源码

## 搭建tomcat项目

* 1.创建maven项目，引入servlet依赖
* 2.创建目录src/main/webapp/WEB-INF，点击Project Structure -> Facets -> 添加WEB -> 配置webapp和web.xml路径，这时web.xml会自动生成
* 3.Project Structure -> Artifacts配置Web exploded，并将maven依赖的包导入WEB-INF/lib目录下
* 4.Run -> Edit Configurations -> 配置本地的tomcat
* 5.在webapp下新建index.html并启动tomcat
* 