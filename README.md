# Fabric-Doc（该教程为Centos7操作系统）
 一、Docker安装
       Docker 是一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的容器中，
    然后发布到任何流行的Linux机器上，也可以实现虚拟化，容器是完全使用沙箱机制，相互之间不会有任何接口。
    安装步骤：
        1. yum-utils提供yum-config-manager工具，设备映射存储驱动需要device-mapper-persistent-data和lvm2工具
            # yum install -y yum-utils device-mapper-persistent-data lvm2
        2. 设置稳定存储库
            # yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
        3. 开启edge
            # yum-config-manager --enable docker-ce-edge
        4. 开启test
            # yum-config-manager --enable docker-ce-test
        5. 安装docker-ce
            # yum install docker-ce
        6. 运行docker
            # systemctl start docker
        7. 查看docker版本，判断是否安装成功
            # docker -version
        8. 设置开机自启动
        # chkconfig docker on
二、 Docker-Compose安装
       Docker-Compose 是用来定义和运行复杂应用的Docker工具。可以在一个文件中定义一个多容器应用和容器依赖，
   并且使用一条命令来启动你的应用，完成一切准备工作。
   安装步骤：
        1. 下载最新版本docker-compose到/usr/local/bin/docker-compose目录下
            # curl -L https://github.com/docker/compose/releases/download/1.22.0/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
        2. 设置/usr/local/bin/docker-compose目录为可执行权限
            # chmod +x /usr/local/bin/docker-compose
        3. 测试docker-compose安装是否成功
            # docker-compose -version
三、GO语言环境安装
       Go语言是谷歌2009发布的第二款开源编程语言，专门针对多处理器系统应用程序的编程进行了优化，
   使用Go编译的程序可以媲美C或C++代码的速度，而且更加安全、支持并行进程。
安装步骤：
    1. 下载并安装GO语言环境
        # cd /opt
        # mkdir golang
        # cd golang
        # yum install wget
        # wget https://studygolang.com/dl/golang/go1.10.3.linux-amd64.tar.gz
        # tar -zxvf go1.10.3.linux-amd64.tar.gz
    2. 配置GO语言环境变量
        # vi /etc/profile
        添加如下内容到/etc/profile后面
        export GOPATH=/opt/gopath
        export GOROOT=/opt/golang/go
        export PATH=$GOROOT/bin:$PATH
        # source /etc/profile
    3. 查看GO语言版本
        # go version
    4. 查看GO语言环境变量
        # go env
四、Git安装
       Git是一个开源的分布式版本控制系统，可以有效、高速的处理各类大小项目版本管理。
    # yum install git
五、Fabric安装
       Fabric安装可以有两种方式，一种通过Git安装，一种通过手动下载安装。
    1. Git安装
        安装步骤：
        1） 生成目录
            # mkdir -p $GOPATH/src/github.com/hyperledger/
            # cd $GOPATH/src/github.com/hyperledger/
        2） Git克隆源码
            # git clone https://github.com/hyperledger/fabric.git
            # cd fabric
        3） 切换Fabric版本
            # git checkout v1.4.3
    2. 手动安装
          访问github网站并下载Fabric，地址为：https://github.com/hyperledger/fabric/tree/v1.4.3，
      然后拷贝到$GOPATH/src/github.com/hyperledger/这个目录下面。
六、Fabric 镜像下载
# docker pull hyperledger/fabric-peer:latest
# docker pull hyperledger/fabric-orderer:latest
# docker pull hyperledger/fabric-tools:latest
# docker pull hyperledger/fabric-ccenv:latest
# docker pull hyperledger/fabric-baseos:latest
# docker pull hyperledger/fabric-kafka:latest
# docker pull hyperledger/fabric-zookeeper:latest
# docker pull hyperledger/fabric-couchdb:latest
# docker pull hyperledger/fabric-ca:latest
注：
配置加速镜像
    # mkdir -p /etc/docker
    # tee /etc/docker/daemon.json <<-'EOF'
        {
        "registry-mirrors": ["https://8w1wqmsz.mirror.aliyuncs.com"]
        }
        EOF
    # systemctl daemon-reload
    # systemctl restart docker
