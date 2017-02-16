#!/bin/sh
echo "####################show node version"&&echo "####################show node version">>env.log
node -v&&node -v>>env.log
echo "####################show selinux"&&echo "####################show selinux">>env.log
sestatus -v &&sestatus -v >>env.log
echo "####################show sysctl"&&echo "####################show sysctl">>env.log
sysctl -p&&sysctl -p>>env.log
echo "####################show iptables"&&echo "####################show iptables">>env.log
iptables -L&&iptables -L>>env.log
echo "####################show netstat"&&echo "####################show netstat">>env.log
netstat -ntlp&&netstat -ntlp>>env.log
echo "####################show top"&&echo "####################show top">>env.log
top -n 1 &&top -n 1 >>env.log
echo "####################show systemversion"&&echo "####################show systemversion">>env.log
cat /etc/issue&&cat /etc/issue>>env.log
cat /proc/version&&cat /proc/version>>env.log
echo "####################show ulimit"&&echo "####################show ulimit">>env.log
ulimit -a&&ulimit -a>>env.log
echo "####################show profile"&&echo "####################show profile">>env.log
cat /etc/profile&&cat /etc/profile>>env.log
echo "####################show cpuinfo"&&echo "####################show cpuinfo">>env.log
cat /proc/cpuinfo&&cat /proc/cpuinfo>>env.log
echo "####################show meminfo"&&echo "####################show meminfo">>env.log
cat /proc/meminfo&&cat /proc/meminfo>>env.log
echo "####################show disk"&&echo "####################show disk">>env.log
df -h&&df -h>>env.log
echo "####################show diskrate"&&echo "####################show diskrate">>env.log
du -sh&&du -sh>>env.log
echo "####################show date"&&echo "####################show date">>env.log
date -R&&date -R>>env.log
echo "####################show locale"&&echo "####################show locale">>env.log
locale&&locale>>env.log
echo "####################show pm2 diskrate"&&echo "####################show pm2 diskrate">>env.log
du -sh `find / -name .pm2`&&du -sh `find / -name .pm2`>>env.log
echo "####################show pm2 property"&&echo "####################show pm2 property">>env.log
ls -l `find  / -type f -name pm2`&&ls -l `find  / -type f -name pm2`>>env.log
echo "####################show AndroidSdkVersion"&&echo "####################show AndroidSdkVersion">>env.log
android list target&&android list target>>env.log
echo "####################show GlibcVersion"&&echo "####################show GlibcVersion">>env.log
rpm -qa|grep glibc&&rpm -qa|grep glibc>>env.log
echo "####################show AntVersion"&&echo "####################show AntVersion">>env.log
ant -version&&ant -version>>env.log
echo "####################show config.json"&&echo "####################show config.json">>env.log
cat config.json&&cat config.json>>env.log
echo "####################show samba"&&echo "####################show samba">>env.log
yum  install -y samba-client
smbclient -N -L 127.0.0.1&&smbclient -N -L 127.0.0.1 >> env.log
echo "####################show config"
echo "please wait............"
tar czf CPSettings.tar.gz ../../share/config/ >> /dev/null
echo "CPSettings tar complete"
echo "####################tar temp"
echo "please wait............"
tar cvf temp.tar.gz `find ../../../typesdk.tmp/*/MainActivity/ -type f -name "*.properties" -o -name "*.log"` >> /dev/null
echo "temp tar complete"
echo "####################show pm2log"
echo "please wait............"
tar cvf pm2log.tar.gz `find /  -name pm2.log` >> /dev/null
echo "pm2log tar complete"
#该片段检测配置是否能连接Manager
echo "####################show conn manager"&&echo "####################show conn manager">>env.log
str1=$(cat config.json|grep WEB_SERVER_IP|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g')
managerip=${str1/WEB_SERVER_IP": "/}
strcurl=$(curl  -s --connect-timeout 2 $managerip |grep TypeSDK)
if  [[ $strcurl =~ "TypeSDK" ]] ;then
	echo "WEB_SERVER_IP set secuess"&&echo "WEB_SERVER_IP set secuess" >>env.log
	echo $strcurl && echo $strcurl >>env.log
else
	echo "WEB_SERVER_IP set error" && echo "WEB_SERVER_IP set error" >>env.log
	echo "please set manager server ip" && echo "please set manager server ip" >>env.log
	echo $strcurl && echo $strcurl >>env.log
fi
echo "####################show filelist"
echo "please wait............"
find ../../ -type f ! -path "../../software/*" ! -path "../../share/output/*" ! -path "../../install/*" -print0 | xargs -0 ls  -d -l * >> filelist.log
echo "####################show filemd5"
echo "please wait............"
find ../../ -type f ! -path "../../software/*" ! -path "../../share/output/*" ! -path "../../install/*" -print0 | xargs -0 md5sum >> filemd5.log
mkdir PackgeEnvLog >> /dev/null
mv *.log ./PackgeEnvLog >> /dev/null
mv *.tar.gz ./PackgeEnvLog >> /dev/null
tar cvf PackgeEnvLog.tar.gz PackgeEnvLog >> /dev/null
rm -rf PackgeEnvLog >> /dev/null
echo "Check Complete!!"