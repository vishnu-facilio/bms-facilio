<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<!--<script src="/js/webmessenger.js"></script>-->
<script>
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
	WebMessenger.subscribe("3", function(event){ 
			var message = JSON.parse(event.data);
			console.log(message.content);
			$('#data').append(message.content).append("<br />");
		}
	);

</script>
<div id="data"></div>