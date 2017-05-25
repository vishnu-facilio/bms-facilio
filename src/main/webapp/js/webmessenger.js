var WebMessenger = new function()
{
	var ws;
	var uid;
	this.subscribe = function(uid, listener)
	{
		this.uid = uid;
        ws = new WebSocket("ws://" + document.location.host + "/bms/websocket/" + uid);
        ws.onmessage = listener;
	}
	
	this.send = function(to, content) 
	{
        var json = JSON.stringify({
            "to":to,
            "content":content
        });
        ws.send(json);
    }
}
//a = function(event) { var message = JSON.parse(event.data);console.log(message);};
//WebMessenger.subscribe("1", a);