#!/bin/bash

compile_mode=$1
log_file=$2

mkdir ./MainActivity/assets
mkdir ./MainActivity/res
mkdir ./MainActivity/src
mkdir ./MainActivity/libs

echo "移动version.properties文件" >> $log_file
mv -f ./Game/version.properties ./MainActivity/
if [ "$?" -ne "0" ]; then
   echo "移动version.properties文件失败." >> $log_file
   exit 1
fi

echo "移动CPSettings.txt文件" >> $log_file
mv -f ./CPSettings.txt ./MainActivity/assets/
if [ "$?" -ne "0" ]; then
   echo "移动CPSettings.txt文件失败." >> $log_file
   exit 1
fi

echo "复制local.properties文件" >> $log_file
cp -f ./local.properties ./TypeSDKBaseLibrary/
mv -f ./local.properties ./MainActivity/
if [ "$?" -ne "0" ]; then
   echo "移动local.properties文件失败." >> $log_file
   exit 1
fi

if [ -f ./MainActivity/local.properties ]; then
	version=`sed '/^targetSdkVersion=/!d;s/.*=//' ./MainActivity/local.properties`
	echo "targetSdkVersion: " $version >> $log_file
	new_version='android-'$version
	sed -i "s/\(target=\)\S\S*/\1$new_version/g" ./MainActivity/project.properties
else
	echo "MainActivity/local.properties不存在" >> $log_file
fi


if [ -f "./MainActivity/libs/android-support-v4.jar" ]; then
	cp -f ./MainActivity/libs/android-support-v4.jar ./TypeSDKBaseLibrary/libs/
fi

echo "拷贝unity-classes.jar文件" >> $log_file
cp -f ./Game/libs/unity-classes.jar ./TypeSDKBaseLibrary/libs/
if [ "$?" -ne "0" ]; then
   echo "拷贝unity-classes.jar文件失败." >> $log_file
   exit 1
fi

echo "拷贝strings.xml文件" >> $log_file
cp -f ./Game/res/values/strings.xml ./MainActivity/res/values/app_strings.xml
if [ "$?" -ne "0" ]; then
   echo "拷贝strings.xml文件失败." >> $log_file
   exit 1
fi


echo "拷贝icon文件" >> $log_file
cp -rf ./icon/* ./TypeSDKBaseLibrary/res/
if [ "$?" -ne "0" ]; then
   echo "拷贝icon文件失败." >> $log_file
   exit 1
fi

if [ -f "./icon/drawable/app_icon.png" ]; then
cp -rf ./icon/* ./MainActivity/res/
if [ "$?" -ne "0" ]; then
   echo "拷贝icon文件失败." >> $log_file
   exit 1
fi
fi 

cd ./MainActivity
pwd

echo "开始进行编译，请稍等..." >> $log_file
ant $compile_mode -propertyfile version.properties -l release.log  >> $log_file
if [ "$?" -ne "0" ]; then
	if [ ! -f "./release.log" ]; then
		echo "log文件找不到." >> $log_file
	else
		cat  ./release.log	   
	fi
	echo "编译失败：ant命令执行失败."  >> $log_file
	exit 1
fi
echo "编译成功"  >> $log_file
