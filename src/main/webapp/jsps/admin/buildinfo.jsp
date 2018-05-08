<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%
 String closeWoMessage = "";
 if (request.getParameter("orgId") != null && request.getParameter("orgId").trim().length() > 0) {
	 closeWoMessage = com.facilio.bmsconsole.actions.InternalActions.CloseAllWorkOrder(Long.parseLong(request.getParameter("orgId").trim()));
 }
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>	
<style>
.bttn{
background-color:#f1f1f1;
}
</style>
<script>
function toggle() {
	var ele = document.getElementById("tg");
	var text = document.getElementById("sc");
	if(ele.style.display == "block") {
    		ele.style.display = "none";
			}
	else {
		ele.style.display = "block";
		}
} 
function init() {
	if (window.location.search.indexOf('showcache') != -1) {
		toggle();
	} 
	
	var message = "<%= closeWoMessage %>";
	if (message != null && message != '') {
		alert(message);
	}
}

</script>

</head>
<body onload="init()">
 <div class="row">
</div>
<div style="margin-top:30px;" >
<a id="sc"class="btn btn-default bttn" href="javascript:toggle();"  role="button">Show Cache</a>
</div>
<div id="tg" style="display:none; margin-top:30px;">
<br>Module Cache =  <textarea style="width: 600px;height:1500px;overflow-y:auto;"><%out.println(com.facilio.fw.LRUCache.getModuleFieldsCache()); %></textarea>
<br>Fields Cache = <textarea  style="width: 600px;height:150px;overflow-y:auto; margin-bottom:20px"><%out.println(com.facilio.fw.LRUCache.getFieldsCache()); %></textarea>

</div>

<div id="tg">
<form action = "" method ="GET">
<h3>Close Workorder:</h3>
Please enter the OrgID:<input type = "text" name = "orgId" id="orgId" placeholder="orgId"/>
<input type = "submit" value = "submit"> 
</form>
</div>

<div style="margin-top:30px;">
<a class="btn btn-default bttn" href="clearcache" role="button">Clear Cache</a>
</div>
<div style="margin-top:30px; margin-bottom:30px;" >
<a class="btn btn-default bttn" href="reloadbrowser" role="button">Reload all users</a>
</div>



	
</body>
</html>




