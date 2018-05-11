 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%
 String copyPMMessage = "";
 if (request.getParameter("orgId") != null && request.getParameter("orgId").trim().length() > 0 && request.getParameter("newOrgId") != null && request.getParameter("newOrgId").trim().length() > 0) {
	 copyPMMessage = com.facilio.bmsconsole.actions.InternalActions.CopyPlannedMaintenance(Long.parseLong(request.getParameter("orgId").trim()), Long.parseLong(request.getParameter("newOrgId").trim()));
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

function init() {
	
	var message = "<%= copyPMMessage %>";
	if (message != null && message != '') {
		alert(message);
	}
}

</script>

</head>
<body onload="init()">
 <div class="row">
</div>


<div id="tg">
<form action = "" method ="GET">
<h3>Copy Planned Maintenance:</h3>
<br>Please Enter the Source OrgID:<input type = "text" name = "orgId" id="orgId" placeholder="orgId"/>
<br>Please Enter the Destination OrgID:<input type = "text" name = "newOrgId" id="newOrgId" placeholder="newOrgId"/><br>
<br><input type = "submit" value = "submit"> 
</form>
</div>




	
</body>
</html>




