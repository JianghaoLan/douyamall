# 开发环境介绍

## CentOS

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

## nacos


服务器程序位置：D:\software\nacos\bin

启动命令：

```
startup.cmd -m standalone
```