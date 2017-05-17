var WebMessenger = new function()
{
	var ws
	var uid;
	this.register = function(uid)
	{
		this.uid = uid;
        ws = new WebSocket("ws://" + document.location.host + "/websocket/chat/" + uid);
        ws.onmessage = function(event) {
        	var message = JSON.parse(event.data);
        	console.log(message);
        };
	}
	
	function send(to, content) 
	{
        var json = JSON.stringify({
            "to":uid,
            "content":content
        });
        ws.send(json);
    }
}