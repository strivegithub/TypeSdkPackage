#!/bin/sh
setenforce 0
echo -e "\033[31m ###################################################################################################### \033[0m"
echo -e "\033[32m ################################### install typesdkPackge          ################################### \033[0m"
echo -e "\033[32m ################################### website:http://www.typesdk.com ################################### \033[0m"
echo -e "\033[32m ################################### QQ:1771930259                  ################################### \033[0m"
echo -e "\033[31m ###################################################################################################### \033[0m"
read -p "Input TypeSDK Manager server IP:Port :" manager_ip
yum -y install git unzip xorg-x11-xauth ld-linux.so.2 libz.so.1 libgcc libgcc_s.so.1 samba cifs-utils
yum -y groupinstall chinese-support
git clone https://code.csdn.net/typesdk/software.git
useradd sdk
ln -s /dev/shm /data/typesdk.tmp
rm -f /etc/samba/smb.conf
cp /data/typesdk/install/smb.conf /etc/samba/
chkconfig smb on
chkconfig nmb on
service smb restart
service nmb restart
cat /data/typesdk/install/export_home.txt >> /etc/profile
cp /data/typesdk/package/server/config.json.bak /data/typesdk/package/server/config.json
sed -i "s/managerserver/$manager_ip/" /data/typesdk/package/server/config.json
ln -s /data/typesdk/package/shell/startTypeSdkPackage.sh /data/typesdk/startTypeSdkPackage.sh
chown -R sdk.sdk /data/typesdk.tmp /data/typesdk
source /etc/profile
