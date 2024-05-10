# 该镜像需要依赖的基础镜像
FROM openjdk:17-jdk

# 创建挂载点
VOLUME /app

# 定义工作目录为/app，命令在此目录下执行
WORKDIR /app

# 指定docker容器启动时运行jar包，并将端口设置为8080
ENTRYPOINT ["java", "-jar", "-Dserver.port=8080", "app.jar"]

# 指定端口 8080
EXPOSE 8080

# 指定维护者的名字为zibo
LABEL maintainer="zibo"
