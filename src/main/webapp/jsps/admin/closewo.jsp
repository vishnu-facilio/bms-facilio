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

<script>

function init() {
	
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

<div >
<form action = "" method ="GET">
<h2>Close Work Orders:</h2>
<div style="margin-top:30px">
Please enter the OrgID: <input style="margin-left: 10px"  type = "text" name = "orgId" id="orgId" placeholder="orgId"/>
</div>

<div style="margin-top:30px">
<input type = "submit" value = "submit"> 
</div>
</form>

</div>
</body>
</html>




