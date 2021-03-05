<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="/struts-tags" prefix="s" %>  
<%@taglib uri="facilio-tags" prefix="f" %>
<%@page import="com.opensymphony.xwork2.ActionContext" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  
 "http://www.w3.org/TR/html4/loose.dtd">  

<html>  
	<head>
		<meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    	<title>Admin Console</title>  

	    <script src="/jsps/admin/assets/js/jquery.js"></script>
		<script src="/jsps/admin/assets/js/jquery-ui.js"></script>
	    <link href="/jsps/admin/assets/css/bootstrap.min.css" rel="stylesheet">
	    <link href="/jsps/admin/assets/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	 	<link href="/jsps/admin/assets/css/sb-admin-2.css" rel="stylesheet">
   	    <script src="/jsps/admin/assets/js/bootstrap.min.js"></script>
		<script src="/jsps/admin/assets/js/sb-admin-2.js"></script>
		<script src="/jsps/admin/assets/js/app.js"></script>
		<script>
			var contextPath = "${pageContext.request.contextPath}";
		</script>
</head>
<body>
<div id="wrapper">

		<%@ include file="header.jsp" %>
		
		<%@ include file="sidebar.jsp" %>



	 <div id="page-wrapper" >
	 
            <tiles:insertAttribute name="body" />  
            
      </div>
      
         <%@ include file="footer.jsp" %> 

</body>

</html>