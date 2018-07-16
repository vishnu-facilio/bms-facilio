<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"  %>
    <%@page import=" com.facilio.bmsconsole.commands.util.CommonCommandUtil" %>
<% 
String userid = request.getParameter("userid");
System.out.println(""+userid);
Boolean verified = null;
verified = CommonCommandUtil.verifiedUser(Long.parseLong(userid)); 

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<script type="text/javascript">

</script>
</body>
</html>