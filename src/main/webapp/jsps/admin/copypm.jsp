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
<form action = "" method ="GET">
<h2> Copy Planned Maintenance:</h2>
<div >
<div style="margin-top:30px">
Please Enter the Source OrgID:  <input style="margin-left: 10px" type = "number" name = "orgId" id="orgId" placeholder="orgId"/>

</div>
<div style="margin-top:30px">
Please Enter the Destination OrgID: <input  style="margin-left: 10px"  type = "number" name = "newOrgId" id="newOrgId" placeholder="newOrgId"/>
</div>
<div style="margin-top:30px">
<input type = "submit" value = "submit"> 
</div>
</div>
</form>





	
</body>
</html>




