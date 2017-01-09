TypeSDK Package工具有2种模式可以选择：单机模式、集群模式。
一、单机模式
应用场景：TypeSDK Package的单机模式建议应用于测试环境或渠道数量小于10个的场景。
设备配置：建议使用物理4核以上CPU、SSD存储、Linux CentOS 6.5 64位操作系统。CPU数量与并发编译任务数对应，如需增加并发可考虑增加物理CPU个数(超线程对编译帮助不大)。
安装说明：
1. 创建 /data/typesdk 目录
mkdir -p /data/typesdk
2. 安装git工具，并clone整个TypeSDK_Package项目。如服务器无外网环境，可在其他有外网的服务器上直接整个项目压缩文件，解压至/data/typesdk
cd /data/typesdk
git clone https://code.csdn.net/typesdk_code/typesdk_package.git /data/typesdk/
3. 运营安装脚本
/data/typesdk/install/install.sh
4. 安装完成后,使用sdk用户启动任务调度器
su - sdk -c /data/typesdk/startTypeSdkPackage.sh
5. 在TypeSDK平台中创建一组渠道打包任务测试。

二、集群模式
集群模式部署支持多台服务器组成编译集群，成倍提高出包效率，以解决多项目多渠道情况下对发包速度的要求。集群模式只在高级版本中提供，如有需要请访问TypeSDK官网 www.typesdk.com 咨询商务。
