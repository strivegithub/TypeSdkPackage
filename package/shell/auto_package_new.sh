#!/bin/bash

#获取当前路径，并加载config中的设置
cur_path=`dirname $0`
echo "加载配置文件"$cur_path"/config"
. $cur_path/config

#定义常量
GAME_FILE_NAME='Game.zip'


#获取执行参数
while getopts "p:g:v:c:w:b:t:i:s:m:q:" option
do
  case $option in
    p)
      PROCESS_NO=$OPTARG
      ;;	
    g)
      GAME_ID=$OPTARG
      ;;
    v)
      GAME_VER=$OPTARG
      ;;
    c)
      CHANNEL_ID=$OPTARG
      ;;
    w)
      CHANNEL_VER=$OPTARG
      ;;	
    b)
      BATCH_NO=$OPTARG
      ;;			
    t)
      TASK_ID=$OPTARG
      ;;
    i)
      ICON_ID=$OPTARG
      ;;			
    s)
      SDK_VER=$OPTARG
      ;;	
    m)
      COMPILE_MODE=$OPTARG
      ;;	
    q)
      SIGN_NAME=$OPTARG
      ;;			
    \?)
      echo "parameter error" 
      exit 1
      ;;
  esac
done

#定义log_file
mkdir -p $log_path/${BATCH_NO}
log_file=$log_path/${BATCH_NO}/${TASK_ID}'.log'
touch $log_file
chmod 644 $log_file


#打印传入参数
startTime="$(date +"%Y/%m/%d %H:%M:%S")"
echo "开始: $startTime"  > $log_file
echo "命令行：$shell_path/$0 $* " >> $log_file
echo '工作目录：'$android_path >> $log_file 

#检测游戏项目工程是否存在
GAME_FILE=$share_path'/game_file/'${GAME_ID}'/'${GAME_VER}'/'${GAME_FILE_NAME}
echo "检查游戏工程文件：" ${GAME_FILE}  >> $log_file
if [ ! -f "${GAME_FILE}" ]; then
  echo "错误：游戏包文件没有找到."  >> $log_file
  exit 1
fi 

#cd 至android打包程序目录
cd $android_path

#清理并生成工作目录
TMP_FOLDER=$tmp_path'/'${TASK_ID}'/'
echo "打包临时文件夹：" ${TMP_FOLDER}  >> $log_file
#清空临时目录
if [  -d ${TMP_FOLDER} ]; then
  echo "清空临时文件夹。" >> $log_file
  rm -rf ${TMP_FOLDER}
fi

mkdir -p ${TMP_FOLDER} 
mkdir -p ${TMP_FOLDER}'icon/'

echo "复制Config文件"  >> $log_file
#复制配置文件至工作目录内
cp -r $share_path'/config/'${GAME_ID}/${CHANNEL_ID}/* ${TMP_FOLDER}
if [ "$?" -ne "0" ]; then
  echo "错误：${share_path}/config/${GAME_ID}/${CHANNEL_ID}/内文件复制失败."  >> $log_file
  exit 1
fi

#复制SDK框架文件至工作目录内,通过-s参数设置SDK框架版本。
gde_sdk_dir=$share_path'/SDK/Type_SDK/'${SDK_VER}/lib
echo "复制SDK框架文件 " ${gde_sdk_dir} >> $log_file
if [ ! -d "$gde_sdk_dir" ]; then
  echo "错误: 所指定的SDK框架版本文件夹不存在."  >> $log_file
  echo $gde_sdk_dir  >> $log_file
  exit 1
fi
cp -r ${gde_sdk_dir}/* ${TMP_FOLDER}

#复制渠道SDK接入逻辑、渠道SDK库至工作目录内,通过-w参数设置渠道SDK版本，接入代码和渠道版本一同保存。
channel_sdk_dir=$share_path'/SDK/Channel_SDK/'${CHANNEL_ID}/${CHANNEL_VER}
echo "复制渠道SDK文件 " ${channel_sdk_dir} >> $log_file
if [ ! -d "$channel_sdk_dir" ]; then
  echo "所指定的渠道SDK版本文件夹不存在."  >> $log_file
  echo $channel_sdk_dir  >> $log_file
  exit 1
fi
cp -r ${channel_sdk_dir}/* ${TMP_FOLDER}

#复制渠道准备脚本文件至工作目录内，如对个别渠道有特殊处理，可修改各个渠道的准备脚本以达到目的。
channel_shell_path=$share_path'/SDK/Channel_Shell/'${CHANNEL_ID}
if [ -d "$channel_shell_path" ]; then
  echo "发现准备脚本，复制该渠道准备脚本至工作目录." $channel_shell_path >> $log_file
  cp -r ${channel_shell_path}/* ${TMP_FOLDER}
fi

#复制指定的图标文件至工作目录内，可制作多套图标资源，通过-i参数指定使用图标。
cp -R $share_path'/icon/'${GAME_ID}/${ICON_ID}/* ${TMP_FOLDER}'icon/'
if [ "$?" -ne "0" ]; then
  echo "错误：${share_path}/icon/${GAME_ID}/${ICON_ID}/内文件复制失败."  >> $log_file
  exit 1
fi
rm -rf ${TMP_FOLDER}'icon/app_icon.png'

#如果扩展配置中存在对应任务ID编号的AndroidManifest.xml文件则替换主项目，该文件由打包控制器在发布任务前，更具游戏、基础项目、渠道项目的AndroidManifest.xm整合生成，详见打包控制器源代码。如不需要自动生成，可注销此段替换逻辑，并将手动修改的AndroidManifest.xml文件存放在各个渠道目录下。
new_AndroidManifest_path=$share_path'/SDK/Extra_Config/Manifest/'${TASK_ID}'/AndroidManifest.xml'
if [ -f "new_AndroidManifest_path" ]; then
  cp -rf $share_path'/SDK/Extra_Config/Manifest/'${TASK_ID}'/AndroidManifest.xml' ${TMP_FOLDER}/MainActivity/
  if [ "$?" -ne "0" ]; then
    echo "复制AndroidManifest文件夹拷贝失败"  >> $log_file
    rm -rf $flag_file
    exit 1
  fi
fi

#创建正在打包标识文件，防止启动新任务
flag_file=$shell_path'/PACKAGING_FLAG_'${PROCESS_NO}
touch $flag_file
echo StartTime：$startTimeStamp >> $flag_file
echo game:${GAME_ID} >> $flag_file
echo Channel：${CHANNEL_ID} >> $flag_file
echo TaskID：${TASK_ID}  >> $flag_file

#解压游戏项目工程至工作目录内
echo "正在解压游戏包."  >> $log_file
unzip $share_path'/game_file/'${GAME_ID}'/'${GAME_VER}'/'${GAME_FILE_NAME} -d ${TMP_FOLDER} > /dev/null
if [ "$?" -ne "0" ]; then
  echo "错误：zip文件解压失败."  >> $log_file
  rm -rf $flag_file
  exit 1
fi

#修正可能出现的目录大小写问题
if [ -d "${TMP_FOLDER}/game" ]; then
  mv -f ${TMP_FOLDER}'/game' ${TMP_FOLDER}'Game'
fi

#检查游戏项目解压复制结果
if [ ! -d "${TMP_FOLDER}/Game" ]; then
  echo "错误：游戏包里缺少Game文件夹."  >> $log_file
  rm -rf $flag_file
  exit 1
fi
echo "文件解压完成."  >> $log_file

#复制version.properties文件至工作目录，其定义了APK的version_name和version_code，注意androidmainfest.xml定义单version_name和version_code有更高的优先级。
echo "复制version.properties文件"  >> $log_file
cp $share_path'/game_file/'${GAME_ID}'/'${GAME_VER}'/version.properties' ${TMP_FOLDER}Game/
if [ "$?" -ne "0" ]; then
  echo "复制version.properties文件失败."  >> $log_file
  rm -rf $flag_file
  exit 1
fi

#复制游戏项目内资源文件至编译工程项目内，标记项目为lib工程项目，并使用android update命令对编译项目生成build.xml文件。
cd ${TMP_FOLDER}
echo "复制assets文件"  >> $log_file
mkdir ./MainActivity/assets
cp -rf ./Game/assets/* ./MainActivity/assets/
if [ ! -f ./Game/build.xml ]; then
  echo android.library=true>> ./Game/project.properties
  cd ./Game
  android update lib-project -p ./ &   >> $log_file
  echo "正在创建build.xml，请稍等"  >> $log_file
  sleep 5
  cd ..
fi

#准备工作完成，开始进行打包操作。
echo "开始执行打包脚本..." >> $log_file

if [ -f "./package.sh" ]; then
#渠道下面存在package.sh则用渠道的shell
  echo "调用渠道独自的package.sh" >> $log_file
  sh ./package.sh $COMPILE_MODE $log_file
else
#渠道没有独立shell的时候用共通的
  echo "$shell_path'/package.sh' $COMPILE_MODE $log_file" >> $log_file
  sh $shell_path'/package.sh' $COMPILE_MODE $log_file
fi

#判断打包返回，如果失败则将release.log打印到log_file中，以供查询错误。
if [ "$?" -ne "0" ]; then
  echo "错误：打包失败." >> $log_file
#删除正在打包标识
   
  rm -rf $flag_file
  echo " ----------------------- Release log ----------------------- " >> $log_file
  cat ./MainActivity/release.log  >> $log_file
  exit 1
fi

#打包成功，将签名成功的APK复制到统一输出目录中。
echo "拷贝apk文件" >> $log_file
APK_NAME=${GAME_ID}_${GAME_VER%_*}_${CHANNEL_ID}${CHANNEL_VER}_${TASK_ID}
apk_savepath=$share_path'/output/apk/'${GAME_ID}/${BATCH_NO}/${APK_NAME}'.apk'
echo "apk保存路径："$apk_savepath >> $log_file
echo "log信息："$share_path'/output/logs/'${BATCH_NO}/${TASK_ID}.log >> $log_file

mkdir -p $share_path'/output/apk/'${GAME_ID}/${BATCH_NO}

mv ./*/bin/*${COMPILE_MODE}.apk $apk_savepath
if [ -f "$apk_savepath" ]; then
#文件存在
  echo "apk文件拷贝成功" >> $log_file
else
#apk文件不存在
  echo "apk文件拷贝失败" >> $log_file
#删除正在打包标识
  rm -rf $flag_file
  exit 1
fi

#保存release.log至统一输出目录中
mv ./MainActivity/release.log $share_path'/output/logs/'${BATCH_NO}/${TASK_ID}'_release.log'   
cd $android_path

#如果是release打包模式，则清理工作目录。
if [ "${COMPILE_MODE}" == "release" ]; then
  echo "删除打包临时文件夹" >> $log_file
  rm -rf ${TMP_FOLDER}
else
#如果是其他模式，则保留工作目录，并输出至日志打印。
  echo "Debug模式时保留打包临时文件夹。文件路径：【"${TMP_FOLDER}"】" >> $log_file
fi

echo "打包成功" >> $log_file

#最后删除正在打包标识
rm -rf $flag_file
cd $android_path

endTimeStamp="$(date +"%Y/%m/%d %H:%M:%S")"
echo "完成: $endTimeStamp" >> $log_file

#输出release日志至打印日志中。
echo " ----------------------- Release log ----------------------- " >> $log_file
cat $share_path'/output/logs/'${BATCH_NO}/${TASK_ID}'_release.log' >> $log_file
#删除release日志
rm -rf $share_path'/output/logs/'${BATCH_NO}/${TASK_ID}'_release.log'
