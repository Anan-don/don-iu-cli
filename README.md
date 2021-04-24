# donis-simple

A Simple Image System based TCP Programming.

## Description

Typora 写笔记、博客非常方便，但是图片的使用就不太友好，因此考虑将图片单独存放到外网服务器上。考察了很多的第三方服务，如第三方图床、github、gitee等，考虑到不稳定；开源图床是用PHP编写，我对此不擅长，因此打算自己搭建图床。 

## Conponent

ImageUploadClient：基于 TCP 协议的图片上传客户端。写这个工具主要是为了在 typora 中自动上传图片并获得外链，因此不需要图形化界面。

ImageServer：图片服务器，用于接收上传的图片并存储到指定位置。

serverconf.properties、clientconf.properties：自定义配置。

## Features

- 命令行程序，轻量。
- 支持批量上传，可一次性上传多张。
- 自定义配置，满足个性化需求。

## Requests

1. JDK > 7.0

## Install

1. 选择合适的 [released](https://github.com/Anan-don/donis-simple/releases)，下载 xxx.zip 文件并解压缩。

   - 也可以下载[最新源文件](https://github.com/Anan-don/donis-simple/archive/refs/heads/master.zip)，手动编译。

   ```powershell
   javac -encoding UTF-8 src\main\java\*.java
   ```

   - 获得 `src\main\java\*.class` 文件和 `src\main\resources\*.properties` 文件。

2. 将 class/server/*.class 文件和 conf/serverconf.properties 文件上传到服务器的同一个目录下，运行以下命令启动服务器：

   ```java
   java ImageServer
   ```

   - 可以修改 serverconf.properties 中的相关配置

   - 注意：防火墙需要开放图片服务器监听的端口，便于外界访问。比如CentOS可以运行如下命令：

     ```shell
     firewall-cmd --zone=public --add-port=你的端口号/tcp --permanent
     systemctl restart firewalld
     ```

3. 将 class/client/ImageUploadClient.class 文件和 conf/clientconf.properties 文件放到本地任意目录下，运行以下命令，将图片上传到服务器：

   ```java
   java ImageUploadClient xxx.png.....
   ```

   - 可以修改 clientconf.properties 中的相关配置

### Typora

1. “文件” -> "偏好设置" -> "图像“：

   - ”插入图片时....“  选择 ”上传图片”，并勾选 “对本地位置的图片应用上述规则” 、“对网络位置的图片应用上述规则”  和 “插入时自动转义图片 URL”；
   - 上传服务选择 “Custom Command”，自定义命令：`@chcp 65001 >nul & cmd /d/s/c java -cp 客户端目录 ImageUploadClient`
   - 点击 “验证图片上传选项”，验证配置是否成功。

2. 图片获取：

   - URL 格式：`http://ip:port/填充字符/xxxx.png`。

   - Nginx 服务器提供图片访问功能。在Nginx 配置文件中配置：例如我的URL格式为：`http://ip:port/images/xxx.png`，服务器存储图片的路径为：`/usr/local/images/`。

     ```conf
             location /images/ {
                 root  /usr/local;
                 index index.html index.htm;
             }
     ```
     - 注意：如果使用域名代替ip，并且域名没有备案，通过本地hosts文件解析域名和ip，则Nginx服务器端口不能指定为80.
     - 因为：80端口默认提供web服务，而在中国会对请求进行审查，如果发现域名没有备案，则会拒绝访问。

   - 图片服务器提供图片访问功能。**未实现**。

## Configuration

支持自定义配置，在 serverconf.properties、clientconf.properties 中可以更改服务器端口、图片存储位置。

### Client

| 属性 | 描述                     | 默认值    |
| ---- | ------------------------ | --------- |
| host | 图片服务器的ip地址或域名 | 127.0.0.1 |
| port | 图片服务器的端口号       | 8080      |

### Server

| 属性     | 描述                       | 默认值                |
| -------- | -------------------------- | --------------------- |
| host     | 服务器的域名，用于外链使用 | 本地ip                |
| port     | 服务器监听的端口           | 8080                  |
| location | 图片的存储位置             | /usr/local/images目录 |

## TODO

- [ ] 正确获取服务器ip地址
- [ ] 服务器重复图片直接返回外链
- [ ] renameTo() 在 linux 服务器中存在必然true的问题。

## Support

如果程序存在bug或者改进的地方，欢迎 [issues](https://github.com/Anan-don/donis-simple/issues)

## License

[Apache License Version 2.0](https://github.com/Anan-don/donis-simple/blob/master/LICENSE)

