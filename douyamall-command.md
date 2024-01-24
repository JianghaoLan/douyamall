# 开发环境介绍

（此文件仅自用）

## CentOS

位置：实验室电脑的虚拟机上

目前部署的服务：

- MySQL
- Redis
- ElasticSearch
- Kibana
- nginx

MySQL、Redis等服务部署在实验室电脑的虚拟机上

- username: root
- password: 123


- username: ljh
- password: 123

ip: 192.168.56.10

## MySQL

地址：192.168.56.10:3306

- username: root
- password: root

docker运行命令:

```
docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql/conf.d \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7
```

设置自动启动


```
sudo docker update mysql --restart=always
```

## Redis

部署启动：

```
mkdir -p /mydata/redis/conf
touch /mydata/redis/conf/redis.conf

docker run -p 6379:6379 --name redis -v /mydata/redis/data:/data \
-v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf \
-d redis redis-server /etc/redis/redis.conf
```

设置自动启动:

```
sudo docker update redis --restart=always
```

## ElasticSearch

初始化配置文件

```shell
echo "http.host: 0.0.0.0" >> /mydata/elasticsearch/config/elasticsearch.yml
```

docker启动命令
(用chmod来放开挂载卷的权限)

```shell
sudo docker run --name elasticsearch \
       -p 9200:9200 -p 9300:9300 \
       -e "discovery.type=single-node" \
       -e ES_JAVA_OPTS="-Xms64m -Xmx512m" \
       -v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
       -v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
       -v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
       -d elasticsearch:7.17.14
```

设置自动启动

```shell
sudo docker update elasticsearch --restart=always
```

安装ik插件



## Kibana

docker启动命令

```shell
docker run --name kibana -p 5601:5601 -e "ELASTICSEARCH_HOSTS=http://192.168.56.10:9200" -d kibana:7.17.14
```

## Nginx

docker启动命令

```shell
docker run -p 80:80 --name nginx \
-v /mydata/nginx/html:/usr/share/nginx/html \
-v /mydata/nginx/logs:/var/log/nginx \
-v /mydata/nginx/conf:/etc/nginx \
-d nginx:1.25
```

## RabbitMQ

docker运行命令：

```shell
docker run -d --name rabbitmq \
  -p 5671:5671 -p 5672:5672 -p 4369:4369 -p 25672:25672 \
  -p 15671:15671 -p 15672:15672 \
  rabbitmq:management
  
docker update rabbitmq --restart=always
```

端口含义：

- 4369,25672(Erlang发现&集群端口)
- 5672,5671(AMQP端口)
- 15672(web管理后台端口)
- 61613,61614(STOMP协议端口)
- 1883,8883(MQTT协议端口)

https://rabbitmq.com/networking.html#ports

## Zipkin

docker运行命令：

```shell
docker run --name zipkin -d -p 9411:9411 openzipkin/zipkin
```

设置自动启动：

```shell
docker update zipkin --restart=always
```

## nacos


服务器程序位置：D:\software\nacos\bin

启动命令：

```
startup.cmd -m standalone
```

## Sentinel Dashboard

程序位置：笔记本C:\software\sentinel-dashboard-1.8.6

请使用端口8333

启动命令：

```shell
java -jar sentinel-dashboard-1.8.6.jar --server.port=8333
```

访问控制台：http://localhost:8333
用户名密码均为sentinel