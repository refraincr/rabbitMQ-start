# maven项目启动方式
 	1. 使用idea
 	2. 命令
```bash
mvn archetype:generate \
  -DgroupId=com.mycompany.app \
  -DartifactId=my-app \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
```

# maven编译运行
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.rabbitmq.app.RabbitComsumer"
```
