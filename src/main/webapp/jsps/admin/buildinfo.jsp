<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.facilio.aws.util.AwsUtil, com.facilio.bmsconsole.actions.AdminAction, com.facilio.accounts.util.AccountUtil"%>
    <%
  	
  String version = request.getParameter("version");
  boolean isNewClientBuild = true;
  //hardcode to true , TO DO Revert all commites related to new build changes
    int aws;
    String user = null;
    if (version != null) {
    	aws = AwsUtil.updateClientVersion(version, isNewClientBuild);
    	
    	/* AdminAction.reloadBrowser();*/
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
<br>Module Cache =  <textarea style="width: 600px;height:1500px;overflow-y:auto;"><%
	out.println(com.facilio.fw.LRUCache.getModuleFieldsCache());
%></textarea>
<br>Fields Cache = <textarea  style="width: 600px;height:150px;overflow-y:auto; margin-bottom:20px"><%
	out.println(com.facilio.fw.LRUCache.getFieldsCache());
%></textarea>
<br>Field Name Cache = <textarea  style="width: 600px;height:150px;overflow-y:auto; margin-bottom:20px"><%
    out.println(com.facilio.fw.LRUCache.getFieldNameCache());
%></textarea>
<br>Response Cache = <textarea  style="width: 600px;height:150px;overflow-y:auto; margin-bottom:20px"><%
	out.println(com.facilio.fw.LRUCache.getResponseCache());
%></textarea>
<br>Feature License Cache = <textarea  style="width: 600px;height:150px;overflow-y:auto; margin-bottom:20px"><%
	out.println(com.facilio.fw.LRUCache.getFeatureLicenseCache());
%></textarea>
<br>Org Display Unit Cache = <textarea style="width: 600px;height:150px;overflow-y:auto; margin-bottom:20px"><%
    out.println(com.facilio.fw.LRUCache.getOrgUnitCachePs());
%></textarea>
</div>

<div style="margin-top:30px;">
<a class="btn btn-default bttn" href="clearcache" role="button">Clear Cache</a>
</div>
<div >
<form action = "" method ="POST">
<div style="margin-top:30px; margin-bottom:30px;" >
<div style="margin-top:30px">
<a class="btn btn-default bttn" href="reloadbrowser" role="button">Reload Browser</a>
</div>
<!-- <div style="margin-top: 30px">
Is updated Client Setup: <input style="margin-left: 10px" type="checkbox" name="isNewClientBuild" />
</div> TODO : revert all this--> 
<br>
<div>
<% 
	user = AccountUtil.getCurrentUser().getEmail();
%>
 <h4>Last-Update By :  <%= user %></h4>
 </div>
</div>
</form>
</div>



	
</body>
</html>




