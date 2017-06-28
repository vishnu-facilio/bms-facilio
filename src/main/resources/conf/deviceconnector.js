var bacnet = require('bacstack');
var json = require('jsonfile');
var AWS = require('aws-sdk');
var AWSIoTData = require('aws-iot-device-sdk');
var cron = require('node-cron');
var fs = require('fs');

var lines = fs.readFileSync('./gru.config','utf8').split('\n').filter(Boolean);
var config = new Object();
config.IdToken='';
for(var i=0;i<lines.length;i++)
{
	var key = lines[i].substr(0,lines[i].indexOf('='));
	var value = lines[i].substr(lines[i].indexOf('=')+1,lines[i].length);
	config[key] = value;
}

// Initialize BACStack
var client = bacnet({port: config.Port,interface: config.LocalInterface.trim(),broadcastAddress: config.BroadcastAddress.trim(),adpuTimeout: config.AdpuTimeout});

// Discover Devices
client.on('iAm', function(address, deviceId, maxAdpu, segmentation, vendorId) {
  console.log('address: ', address, ' - deviceId: ', deviceId, ' - maxAdpu: ', maxAdpu, ' - segmentation: ', segmentation, ' - vendorId: ', vendorId);
});
client.whoIs();

var resultArray = new Object();
var x = 1;
var runProcess = true;
var isConnected = false; 

AWS.config.region = 'us-west-2';
	
const mqttClient = AWSIoTData.device({
	protocol: 'wss',
	accessKeyId: '',
	secretKey: '',
	sessionToken: '',
	clientId: '100',
	region: AWS.config.region,
	debug: false,
	maximumReconnectTimeMs: 8000
});

var processItems = function(x, resultArray, runProcess){
	var requestArray = [
  	{objectIdentifier: {type: 2, instance: x}, propertyReferences: [{propertyIdentifier: 77},{propertyIdentifier: 85}]}
	];
client.readPropertyMultiple(config.ControllerIPAddress.trim(), requestArray, function(err, value) {
if(value.values[0].values[0].value.value != undefined)
{
	runProcess = false;
}
else
{
  	var result = new Object();
  	result.instance = value.values[0].objectIdentifier.instance;
  	switch(value.values[0].values[0].propertyIdentifier)
  	{
	  	case 77 :
			result.instanceName =  value.values[0].values[0].value[0].value;
	  	break;

	  	case 85 :
			result.currentvalue =  value.values[0].values[0].value[0].value;
	  	break;
  	}
 	switch(value.values[0].values[1].propertyIdentifier)
  	{
	  	case 77 :
			result.instanceName =  value.values[0].values[1].value[0].value;
	  	break;

	  	case 85 :
			result.currentvalue =  value.values[0].values[1].value[0].value;
	  	break;
  	}
	resultArray[x] = result;
	resultArray.metainfo = {controllerId:config.ControllerId.trim()};
}
if(runProcess)
{
	processItems(x+1, resultArray, runProcess);
}
else
{
	if(isConnected)
	{	
		console.log("Cred4 ::" + AWS.config.credentials.needsRefresh());
		if(AWS.config.credentials.needsRefresh())
		{
			AWS.config.credentials.refresh((error) => {
        	if (error) 
        	{
        		console.error("Error refresh::::");
            	console.error(error);
        	} 
        	else 
        	{
            	console.log('Successfully logged!');
				mqttClient.publish('iotdata', JSON.stringify(resultArray));
				console.log("Published");
        	}
    		});
    	}
    	else
    	{
			mqttClient.publish('iotdata', JSON.stringify(resultArray));
			console.log("Published");
    	}
	}
	else
	{
		var cognitoIdentity = new AWS.CognitoIdentity();
		AWS.config.credentials = new AWS.CognitoIdentityCredentials({
       		IdentityPoolId: 'us-west-2:ba15c3b0-a6d9-4f33-8841-5b813d55170e',
       		Logins: {'cognito-idp.us-west-2.amazonaws.com/us-west-2_kzN5KrMZU':config.IdToken}
    	});
       	AWS.config.credentials.get(function(err, data) {
   		if (!err) 
   		{
   			console.log('Credentials Updated');
      		mqttClient.updateWebSocketCredentials(AWS.config.credentials.accessKeyId, AWS.config.credentials.secretAccessKey, AWS.config.credentials.sessionToken);
		}
		else
		{
			console.log('Credentials Error');
			AWS.config.update({
    			accessKeyId: "AKIAJPM4FLBRC6DFAXIA",
    			secretAccessKey: "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",
    			region: "us-west-2" 
				});
			var cognitoidentityserviceprovider = new AWS.CognitoIdentityServiceProvider();
			var params = {
         		AuthFlow: 'REFRESH_TOKEN_AUTH',
         		ClientId: '74d026sk7dde4vdsgpkhjhj17m',
         		AuthParameters:{REFRESH_TOKEN:config.RefreshToken,USERNAME:config.UserName}
      		};
      		cognitoidentityserviceprovider.initiateAuth(params, function(err, data) {
  			if (err)
  			{
  			 	console.log(err); // an error occurred
  			}
  			else     
  			{	
  				config.IdToken = data.AuthenticationResult.IdToken;
  				AWS.config.credentials = new AWS.CognitoIdentityCredentials({
       				IdentityPoolId: 'us-west-2:ba15c3b0-a6d9-4f33-8841-5b813d55170e',
       				Logins: {'cognito-idp.us-west-2.amazonaws.com/us-west-2_kzN5KrMZU':config.IdToken}
    			});
       			AWS.config.credentials.get(function(err, data) {
   				if (!err) 
   				{
      				mqttClient.updateWebSocketCredentials(AWS.config.credentials.accessKeyId, AWS.config.credentials.secretAccessKey, AWS.config.credentials.sessionToken);
				}
				});
  			}
			});
		}	
		});
    	mqttClient
  		.on('connect', function() {
  		isConnected = true;
    	console.log('connect');
    	mqttClient.publish('iotdata', JSON.stringify(resultArray));
    	console.log("Published");
   	 	});
		mqttClient
  		.on('message', function(topic, payload) {
    	console.log('message', topic, payload.toString());
  		});
    }
}
});
};
//processItems(x, resultArray, runProcess);
var task = cron.schedule('*/30 * * * * *', function() {
  	console.log(new Date());
  	processItems(x, resultArray, runProcess);
}, false);
 
task.start();