workspace
=========

**java 爬虫功能**

**用于学习java及中间件技术**


## 中间件部署使用docker

#### docker-compose配置文件如下

```yaml
version: '3'
services:
    zookeeper:
        image: wurstmeister/zookeeper
        ports:
            - "2181:2181"
    kafka:
        image: wurstmeister/kafka
        ports:
            - "9092"
        environment:
            HOSTNAME_COMMAND: "docker info | grep ^Name: | cut -d' ' -f 2" # Normal instances
            # KAFKA_ADVERTISED_HOST_NAME: 192.168.0.106
            KAFKA_ADVERTISED_LISTENERS: INSIDE://:9092,OUTSIDE://_{HOSTNAME_COMMAND}:9094
            KAFKA_LISTENERS: INSIDE://:9092,OUTSIDE://:9094
            KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT
            KAFKA_INTER_BROKER_LISTENER_NAME: INSIDE
            KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
        volumes:
            - /var/run/docker.sock:/var/run/docker.sock
    rabbitmq:
        image: "rabbitmq"
        # hostname: "rabbit1"
        environment:
            RABBITMQ_ERLANG_COOKIE: "SWQOKODSQALRPCLNMEQG"
            RABBITMQ_DEFAULT_USER: "rabbitmq"
            RABBITMQ_DEFAULT_PASS: "rabbitmq"
            RABBITMQ_DEFAULT_VHOST: "/"
            RABBITMQ_LOG_BASE: "/var/log/rabbitmq"
        ports:
            - "15672:15672"
            - "5672:5672"
        labels:
            NAME: "rabbitmq"
        volumes:
            - /var/run/docker.sock:/var/run/docker.sock
    postgres:
        image: postgres
        volumes:
            - db-data:/var/lib/postgresql/data
        ports:
            - 5432:5432

volumes:
    db-data:
        driver: local


```

---

#### 数据库初始化
- 登录docker
- 切换到postgres用户
```
  su - postgres
```
- 登录postgres
```shell
postgres@da608447a836:/$ psql -s postgres
psql (11.3 (Debian 11.3-1.pgdg90+1))
Type "help" for help.

postgres=#

```
- 创建数据库
```sql
postgres=# CREATE DATABASE zhihu;
CREATE DATABASE
postgres=#
```

- 切换到上一步创建的数据
```
# \c 命令
postgres=# \c zhihu
postgres=# 
```

- 创建表
```sql
CREATE TABLE users (
    id         varchar(100) CONSTRAINT firstkey PRIMARY KEY,
    name       varchar(500),
    gender     varchar(500),
    headline   varchar(1000),
    url        varchar(500)
);
```