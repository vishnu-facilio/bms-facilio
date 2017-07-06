<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="/struts-tags" prefix="s" %>  
<%@taglib uri="facilio-tags" prefix="f" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  
 "http://www.w3.org/TR/html4/loose.dtd">  
 <%
 String staticURL = "http://facilio-static.s3-website-us-west-2.amazonaws.com";
 %>
<html>  
	<head>
		<meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    	<title><tiles:getAsString name="title" /></title>  
    
    	<!-- Bootstrap Core CSS -->
	    <link href="<%=staticURL%>/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	    
	    <link rel="stylesheet" href="http://xilinus.com/jquery-addresspicker/demos/themes/base/jquery.ui.all.css">
	
		<!-- Social Buttons CSS -->
    	<link href="<%=staticURL%>/vendor/bootstrap-social/bootstrap-social.css" rel="stylesheet">
    	
	    <!-- MetisMenu CSS -->
	    <link href="<%=staticURL%>/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">
	
	    <!-- Morris Charts CSS -->
	    <link href="<%=staticURL%>/vendor/morrisjs/morris.css" rel="stylesheet">
	
	    <!-- Custom Fonts -->
	    <link href="<%=staticURL%>/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	    
	    <!-- DataTables CSS -->
    	<link href="<%=staticURL%>/vendor/datatables-plugins/dataTables.bootstrap.css" rel="stylesheet">

    	<!-- DataTables Responsive CSS -->
    	<link href="<%=staticURL%>/vendor/datatables-responsive/dataTables.responsive.css" rel="stylesheet">
    	
    	<!-- FullCalendar CSS -->
    	<link href="<%=staticURL%>/vendor/fullcalendar/fullcalendar.min.css" rel="stylesheet">
    	
    	<link href="${pageContext.request.contextPath}/css/sb-admin-2.css" rel="stylesheet">
    	
    	<link href="${pageContext.request.contextPath}/js/c3/c3.min.css" rel="stylesheet">
    	
    
		<!-- jQuery -->
	    <script src="<%=staticURL%>/vendor/jquery/jquery.min.js"></script>
		
		<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
		
		<script src="http://maps.google.com/maps/api/js?sensor=false&key=AIzaSyBRto3e31_woKTGsOMqgIjZtWUbX7ILyuw"></script>
		
		<script src="http://xilinus.com/jquery-addresspicker/src/jquery.ui.addresspicker.js"></script>
		
	    <!-- Bootstrap Core JavaScript -->
	    <script src="<%=staticURL%>/vendor/bootstrap/js/bootstrap.min.js"></script>
	
	    <!-- Metis Menu Plugin JavaScript -->
	    <script src="<%=staticURL%>/vendor/metisMenu/metisMenu.min.js"></script>
	
	    <!-- Morris Charts JavaScript -->
	    <script src="<%=staticURL%>/vendor/raphael/raphael.min.js"></script>
	    <script src="<%=staticURL%>/vendor/morrisjs/morris.min.js"></script>    
	    
	    <!-- DataTables JavaScript -->
	    <script src="<%=staticURL%>/vendor/datatables/js/jquery.dataTables.min.js"></script>
	    <script src="<%=staticURL%>/vendor/datatables-plugins/dataTables.bootstrap.min.js"></script>
	    <script src="<%=staticURL%>/vendor/datatables-responsive/dataTables.responsive.js"></script>
	    
	    <script src="<%=staticURL%>/vendor/moment/moment.min.js"></script>
	    <script src="<%=staticURL%>/vendor/fullcalendar/fullcalendar.min.js"></script>
	    <script src="https://d3js.org/d3.v3.min.js"></script>
		<script src="${pageContext.request.contextPath}/js/c3/c3.min.js"></script>
    	
		<script src="http://ricostacruz.com/nprogress/nprogress.js"></script>
		<link href="http://ricostacruz.com/nprogress/nprogress.css" rel="stylesheet">
		
		<script src="http://square.github.io/cubism/cubism.v1.js"></script>
	
		<script src="${pageContext.request.contextPath}/js/sb-admin-2.js"></script>
		<script src="${pageContext.request.contextPath}/js/app.js"></script>
		<script src="${pageContext.request.contextPath}/js/chart.lib.js"></script>
		<script src="${pageContext.request.contextPath}/js/d3/d3wrapper.js"></script>
		<script src="${pageContext.request.contextPath}/js/webmessenger.js"></script>
		
		<script>
			var contextPath = "${pageContext.request.contextPath}";
		</script>
</head>
<body>
	<div id="wrapper">

		<%@ include file="header.jsp" %>

        <div id="page-wrapper">
            <tiles:insertAttribute name="body" />  
        </div>
        <!-- /#page-wrapper -->
       
       	<div class="common-notification-alert col-xs-11 col-sm-4" role="alert" style="margin: 0px auto; position: fixed; transition: all 0.5s ease-in-out; z-index: 1031; top: 20px; left: 0px; right: 0px; animation-iteration-count: 1;">
       		<div class="alert alert-success alert-dismissable" style="display: none;">
       			<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
       			<span class="msg"></span>
       		</div>
       	</div>
       	<!-- /#common notification alert -->
        
        <%@ include file="footer.jsp" %>
    </div>
</body>
</html>