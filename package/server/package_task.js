//--------------------------------------------------------------------
// 
//   打包任务监控常驻内存程序
//   （Nodejs服务--Android版）
//
// 功能：每隔一段时间请求打包任务
// Update Date 2016-02-24  
// Create By Typsdk
//--------------------------------------------------------------------
var child_process = require('child_process');
var http = require('http');
var qs = require('querystring');
var config = require('./config.json');
var log4js = require("log4js");
var fs = require('fs');

var SHARE_PATH = config.SHARE_PATH;            		  // 共享文件夹
var SHELL_PATH = config.SHELL_PATH;            		  // 打包根目录
var SHELL_FILE_PATH = SHELL_PATH + config.SHELL_FILE; // 打包shell文件
var INTERVAL = config.INTERVAL;               		  // 监控间隔时间
var WEB_REQUEST_URL = "http://" + config.WEB_SERVER_IP + "/taskmanage";		  
                                                      // api请求地址
var PLATFORM = config.PLATFORM;               	 	  // 平台（Android/ios）
//var IP_ADDR = config.IP_ADDR;                		  // IP地址
var IP_ADDR = getIPAdress();
var processNO = '01';                                 //默认值

function getIPAdress(){
  var interfaces = require('os').networkInterfaces();
  for(var devName in interfaces){
    var iface = interfaces[devName];
      for(var i=0;i<iface.length;i++){
        var alias = iface[i];
        if(alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal){
          return alias.address;
        }
     }
  }
}

process.argv.forEach(function (val, index, array) {
  if( index == 2 ){
    processNO = val; //命令行参数  
  } else {
    processNO = process.pid;
  }
});


log4js.configure(
  { "appenders":
    [
      {
        "type":"console",
        "category":"console"
      },
      {
        "type": "file",
        "filename": "logs/" + IP_ADDR + "_" + processNO + ".log",
        "maxLogSize": "1024000",
        "backups":"10",
        "category": "normal"
      }
    ],
    "replaceConsole": true
  }
);

var logger = log4js.getLogger('normal');
//trace, debug, info, warn, error, fatal
logger.setLevel('debug');

InfoLog("Package service started.");
InfoLog("process Id: " + process.pid);
InfoLog('process No: ' + processNO);

//打包shell参数
var TaskID = '';
var Channel = '';
var ChannelVersion = '';
var GameID = '';
var IconID = '';
var Version = '';
var BatchNo = '';
var SdkVer = '';
var GameFile = '';
var ConfigFileDir = '';
var IconFileDir = '';
var GameFileName = "Game.zip";
var IsEncrypt = '';
var AdID = '';
var CompileMode = 'release';
var PluginID = '';
var PluginVer = '';
var SignKey = '';

var isFileExitst = fs.existsSync(SHELL_PATH + "PACKAGING_FLAG_" + processNO);
if (isFileExitst) {
   //删除文件
   fs.unlinkSync(SHELL_PATH + "PACKAGING_FLAG_" + processNO);
}

//每隔一段时间请求打包任务
setInterval(function() {
	
	//判断打包标识文件
	InfoLog(SHELL_PATH);
        InfoLog("PACKAGING_FLAG_" + processNO);
	var isBusy = fs.existsSync(SHELL_PATH + "PACKAGING_FLAG_" + processNO);
	if (isBusy) {
		//系统正在打包
		InfoLog("Performing the package task. (id:" + TaskID + ")");
		return;
	}
	
	//InfoLog("Check package task.");
	//发送【获取任务/gainTask】命令
	var urlGainTask = WEB_REQUEST_URL + "?action=gainTask" ;
		urlGainTask = urlGainTask + "&platform=" + PLATFORM;
		urlGainTask = urlGainTask + "&serveraddr=" + IP_ADDR;
	InfoLog(urlGainTask);
	http.get(urlGainTask, function(res) {
		InfoLog("Received normal response. (gainTask)");

		if (res.statusCode != '200'){
			InfoLog('STATUS: ' + res.statusCode);
			ErrLog('Status Code Error.');
			return;
		}

		res.setEncoding('utf8');
		res.on('data', function (chunk) {
			InfoLog('BODY: ' + chunk);

			var body = JSON.parse(chunk);
			if(body.data !== null){
				//返回打包任务
				InfoLog("Got a package task.");
				//获取任务参数（游戏id，渠道id，版本，文件下载路径，）
				InfoLog(JSON.stringify(body.data[0]));
				TaskID = body.data[0].TaskID;
				Channel = body.data[0].Channel;
				ChannelVersion = body.data[0].ChannelVersion;
				GameID = body.data[0].GameID;
				IconID = body.data[0].IconID;
				Version = body.data[0].GameVersion;
				BatchNo = body.data[0].BatchNo;
				SdkVer = body.data[0].SdkVer;					
				IsEncrypt = body.data[0].IsEncrypt;	
				AdID = body.data[0].AdID;
				CompileMode = body.data[0].CompileMode;
				PluginID = body.data[0].PluginID;
				PluginVer = body.data[0].PluginVersion;					
				SignKey = body.data[0].SignKey;
				
				if(typeof PluginID == "undefined" || typeof PluginVer == "undefined"  ) {
					PluginID = "0";
				}
				
				//检查打包材料
				GameFile = SHARE_PATH + "game_file/" + GameID + "/" + Version + "/" + GameFileName;
				ConfigFileDir = SHARE_PATH + "config/" + GameID + "/";
				IconFileDir = SHARE_PATH + "icon/" + GameID + "/" + IconID;

				var exists = fs.existsSync(GameFile);
				if (!exists) {
					ErrLog("Game file no exist. " + GameFile);
					sendLoseTask(TaskID);
					InfoLog("--------------------------------------------------------------------------------------\n\n");
					return;
				} else {
					InfoLog("Game file exist.   " + GameFile);	
				}

				exists = fs.existsSync(ConfigFileDir);
				if (!exists) {
					ErrLog("Config file no exist. " + ConfigFileDir);
					sendLoseTask(TaskID);
					InfoLog("--------------------------------------------------------------------------------------\n\n");
					return;
				} else {
					InfoLog("Config file exist. " + ConfigFileDir);	
				}

				exists = fs.existsSync(IconFileDir);
				if (!exists) {
					ErrLog("Icon file no exist. " + IconFileDir);
					sendLoseTask(TaskID);
					InfoLog("--------------------------------------------------------------------------------------\n\n");
					return;
				} else {
					InfoLog("Icon file exist.   " + IconFileDir);	
				}

				
				//发送【开始任务/startTask】命令
				var urlStartTask = WEB_REQUEST_URL + "?action=startTask";
				urlStartTask = urlStartTask + "&platform=" + PLATFORM;		
				urlStartTask = urlStartTask + "&taskid=" + TaskID;
				urlStartTask = urlStartTask + "&serveraddr=" + IP_ADDR;

				InfoLog("Send start task reply.");
				InfoLog(urlStartTask);

				//发送开始打包的消息
				http.get(urlStartTask, function(res) {
					if (res.statusCode != '200'){
						ErrLog("Status Code Error. (startTask) STATUS:" + res.statusCode);
						return;
					}
					InfoLog("Received normal response. (startTask)");
					res.setEncoding('utf8');
					res.on('data', function (chunk) {
						//消息回答
						InfoLog('BODY: ' + chunk);

						//执行shell打包
						callShell(TaskID, function callback(taskId, result){
							if(result == "normal"){
								//shell执行正常
								sendFinishTask(taskId);
							} else {
								//shell执行失败
								sendLoseTask(taskId);
							}
						});
					});
				}).on('error', function(e) {
					ErrLog("Received error response. (startTask) message:" + e.message);
					InfoLog("--------------------------------------------------------------------------------------\n\n");
				});
			} else {
				//无打包任务
				InfoLog("Check package task. No task.\n");
			}

		});

	}).on('error', function(e) {
		ErrLog("Received error response. (gainTask) message:" + e.message);
		InfoLog("--------------------------------------------------------------------------------------\n\n");
	});

}, INTERVAL);

//打包完成通知
function sendFinishTask(taskId) {

	var urlFinish = WEB_REQUEST_URL + "?action=finishTask";
	var Encrypted = "";      //文件名加密标记
	if (IsEncrypt == "1") {
		Encrypted = "_encrypted";
	}
	var packagename = GameID + "_" + Version.substring(0, Version.length-18) + "_" + Channel + ChannelVersion + "_" + TaskID + Encrypted + ".apk";
	
	urlFinish = urlFinish + "&platform=" + PLATFORM;		
	urlFinish = urlFinish + "&taskid=" + taskId;
	urlFinish = urlFinish + "&packagename=" + packagename;
	urlFinish = urlFinish + "&serveraddr=" + IP_ADDR;
	
	InfoLog("Send finish task message.");
	InfoLog(urlFinish);

	//发送打包完成的消息
	http.get(urlFinish, function(res) {
		if (res.statusCode != '200'){
			ErrLog("Status Code Error. (finishTask) STATUS:" + res.statusCode);
			//重新发送请求
			http.get(urlFinish, function(res) {
				if (res.statusCode != '200'){
					ErrLog("Status Code Error. (finishTask) STATUS:" + res.statusCode);
				} else {
					InfoLog("Status Code Normal. (finishTask) STATUS:" + res.statusCode);				
				}
			});
			
			return;
		}
		InfoLog("Received normal response. (finishTask)");
		res.setEncoding('utf8');
		res.on('data', function (chunk) {
			//消息回答
			InfoLog('BODY: ' + chunk);
			InfoLog("--------------------------------------------------------------------------------------\n\n");
		});

	}).on('error', function(e) {
		ErrLog("Received error response. (finishTask) message:" + e.message);
		InfoLog("--------------------------------------------------------------------------------------\n\n");
	});
}

//打包失败通知
function sendLoseTask(taskId) {

	var urlLose = WEB_REQUEST_URL + "?action=loseTask";
	urlLose = urlLose + "&platform=" + PLATFORM;
	urlLose = urlLose + "&taskid=" + taskId;
	urlLose = urlLose + "&batchno=" + BatchNo;	
	urlLose = urlLose + "&serveraddr=" + IP_ADDR;
	InfoLog("Send lose task message.");
	InfoLog(urlLose);

	//发送打包失败的消息
	http.get(urlLose, function(res) {
		if (res.statusCode != '200'){
			ErrLog("Status Code Error. (loseTask) STATUS:" + res.statusCode);
			return;
		}
		InfoLog("Received normal response. (loseTask)");
		res.setEncoding('utf8');
		res.on('data', function (chunk) {
			//消息回答
			InfoLog('BODY: ' + chunk);
			InfoLog("--------------------------------------------------------------------------------------\n\n");
		});

	}).on('error', function(e) {
		ErrLog("Received error response. (loseTask) message:" + e.message);
		InfoLog("--------------------------------------------------------------------------------------\n\n");
	});
}

//打包处理
function callShell(taskId, callback){

	InfoLog("Run shell ==> " + SHELL_FILE_PATH);
	InfoLog("Start packing tasks. (id:" + taskId + ")");

	var strAdFile = "";
	if (typeof(AdID) != "undefined" && AdID != "" && AdID != "0") {

    	strAdFile = '{' + '\n' +
   			'	"adid": ' + AdID + ',' + '\n' +
   			'	"adurl": [' + '\n' +
        	'	{' + '\n' +
          	'		"url": "' + config.AD_SERVER_URL_1 + '"\n' +
        	'	},' + '\n' +
       		' 	{' + '\n' +
            '		"url": "' + config.AD_SERVER_URL_2 + '"\n' +
      		'	}' + '\n' +
    		'	],' + '\n' +
    		'	"extra": "info"' + '\n' +
			'}';
		InfoLog("AD文件：" + strAdFile);
	}

	//执行shell文件
	child_process.execFile(SHELL_FILE_PATH,
		//shell参数
		['-p', processNO, '-g', GameID, '-v', Version, '-c', Channel, '-w', ChannelVersion, '-b', BatchNo, '-t', taskId, '-i', IconID, '-s', SdkVer, '-m', CompileMode, '-q', SignKey],
		null,
		//回调处理
		function (err, stdout, stderr) {
			if(err !== null){
				ErrLog("Shell return error. " + err);
				ErrLog("Output package log information.\n" + stdout);
				callback(taskId, "error");
			} else {
				InfoLog("Shell return normal.");
				InfoLog("Output package log information.\n" + stdout);
				callback(taskId, "normal");
			}
		}
	);
}

function ErrLog(message){
	msg = '[process No:' + processNO +  '] ' + message;
	console.error(msg);
	logger.error(msg);
}

function WarnLog(message){
	msg = '[process No:' + processNO +  '] ' + message;	
	console.warn(msg);
	logger.warn(msg);
}

function InfoLog(message){
	msg = '[process No:' + processNO +  '] ' + message;
	console.log(msg);
	logger.info(msg);
}
