# 开发环境介绍

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

## nacos


服务器程序位置：D:\software\nacos\bin

启动命令：

```
startup.cmd -m standalone
```
