# memes

MySQL 实现，与sqlite的实现完全相同，只是在数据源上有区别

为了提升查询的速度，可以**手动的**创建索引。

```sql
create index date on submission (date);

create index date_del_rev on submission (timestamp, deleted, reviewed);

create index id on submission (id);
```

> 配置文件如下，其中用 $ 引起来的是环境变量，需要自行配置

```yaml
spring:
  application:
    name: memes
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ${mysqlUrl}
    username: ${mysqlUser}
    password: ${mysqlPassword}
  web:
    resources:
      static-locations: [ "classpath:/static/","file:./memes/" ]
  webflux:
    static-path-pattern: /static/**

urlPrefix: ${urlPrefix}

token: ${token}


```