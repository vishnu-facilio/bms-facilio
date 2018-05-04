<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="/struts-tags" prefix="s" %>  
<%@taglib uri="facilio-tags" prefix="f" %>
<%@page import="com.opensymphony.xwork2.ActionContext" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  
 "http://www.w3.org/TR/html4/loose.dtd">  
 <%
 String staticURL = "/ROOT";
 %>
<html>  
	<head>
		<meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    	<title>Admin Console</title>  
    	
    	<!-- jQuery -->
	    <script src="${pageContext.request.contextPath}/js/home/jquery.js"></script>
				
		<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
		
		<script src="http://maps.google.com/maps/api/js?sensor=false&key=AIzaSyBRto3e31_woKTGsOMqgIjZtWUbX7ILyuw"></script>
		
    	<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
    	
    	<!-- Bootstrap Core CSS -->
	   <link href="${pageContext.request.contextPath}/css/home/bootstrap.min.css" rel="stylesheet"> 
	    
	     
		    <!-- MetisMenu CSS -->
	  <!--   <link href="<%=staticURL%>/vendor/metisMenu/metisMenu.min.css" rel="stylesheet"> -->
	
	    <!-- Morris Charts CSS -->
	   <!--   <link href="<%=staticURL%>/vendor/morrisjs/morris.css" rel="stylesheet"> -->
	
	    <!-- Custom Fonts -->
	    <link href="${pageContext.request.contextPath}/css/home/font-awesome.min.css" rel="stylesheet" type="text/css"> 
	      
	    <!-- DataTables CSS -->
    <!-- 	<link href="<%=staticURL%>/vendor/datatables-plugins/dataTables.bootstrap.css" rel="stylesheet">  -->

    	<!-- DataTables Responsive CSS -->
    	<!--  <link href="<%=staticURL%>/vendor/datatables-responsive/dataTables.responsive.css" rel="stylesheet"> -->
    	
    	 <!-- date picker 4.17 -->
	    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.min.css" /></head>
	    
	    <!--  selectize -->
	    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.4/css/selectize.bootstrap3.min.css" />
	  	   
    	
    <!-- 	FullCalendar CSS -->
    
    	  <link href="${pageContext.request.contextPath}/js/scheduler.min.css" rel="stylesheet"> 
    	  <link href="${pageContext.request.contextPath}/css/sb-admin-2.css" rel="stylesheet"> 
    	     	<link href="${pageContext.request.contextPath}/js/c3/c3.min.css" rel="stylesheet"> 
    	
    	<!-- scroll bar -->
    	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery.perfect-scrollbar/0.7.0/css/perfect-scrollbar.min.css" />
    	
    	 <!-- Bootstrap Core JavaScript -->
	    <script src="${pageContext.request.contextPath}/js/home/bootstrap.min.js"></script>
	
	    <!-- Metis Menu Plugin JavaScript -->
	  <!--   <script src="<%=staticURL%>/vendor/metisMenu/metisMenu.min.js"></script>   -->
	
	    <!-- Morris Charts JavaScript -->
	  <!--   <script src="<%=staticURL%>/vendor/raphael/raphael.min.js"></script>   -->
	    <!--  <script src="<%=staticURL%>/vendor/morrisjs/morris.min.js"></script>    -->
	    
	    <!-- DataTables JavaScript -->
	 <!--  <script src="<%=staticURL%>/vendor/datatables/js/jquery.dataTables.min.js"></script>  --> 
	   <!--    <script src="<%=staticURL%>/vendor/datatables-plugins/dataTables.bootstrap.min.js"></script>  --> 
	  <!--    <script src="<%=staticURL%>/vendor/datatables-responsive/dataTables.responsive.js"></script>  --> 
	    
	<!--    <script src="<%=staticURL%>/vendor/moment/moment.min.js"></script>  -->
	 <!--   <script src="<%=staticURL%>/vendor/fullcalendar/fullcalendar.min.js"></script>  -->
	    <script src="${pageContext.request.contextPath}/js/scheduler.min.js"></script> 
	     <script src="https://d3js.org/d3.v3.min.js"></script> 
		<script src="${pageContext.request.contextPath}/js/c3/c3.min.js"></script> 
    	
		<script src="http://ricostacruz.com/nprogress/nprogress.js"></script> 
		<link href="http://ricostacruz.com/nprogress/nprogress.css" rel="stylesheet"> 
		
	  	<script src="http://square.github.io/cubism/cubism.v1.js"></script> 
	
		<!-- moment.js plug in -->
		<script src="http://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js"></script>
	
		<!-- date picker js -->	
		<script src="http://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/js/bootstrap-datetimepicker.min.js"></script>
	
		<!--  selectize -->
	    <script src="http://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.4/js/standalone/selectize.min.js"></script>
	    
	    <!-- bootstrap validator -->
	    <script src="http://cdnjs.cloudflare.com/ajax/libs/1000hz-bootstrap-validator/0.11.9/validator.js"></script>	
	
		<!-- scroll bar js -->
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.perfect-scrollbar/0.7.0/js/perfect-scrollbar.jquery.min.js"></script>
		
		<script src="https://cdnjs.cloudflare.com/ajax/libs/js-cookie/2.1.4/js.cookie.min.js"></script>
		
		<!-- country code js -->
		<script src="https://cdnjs.cloudflare.com/ajax/libs/knockout/3.4.2/knockout-min.js"></script>
		
    	<script src="${pageContext.request.contextPath}/js/cognitoutil.js"></script>
		
		<script src="${pageContext.request.contextPath}/js/sb-admin-2.js"></script>
		<script src="${pageContext.request.contextPath}/js/app.js"></script>
		<script src="${pageContext.request.contextPath}/js/chart.lib.js"></script>
		<script src="${pageContext.request.contextPath}/js/d3/d3wrapper.js"></script>
		<script src="${pageContext.request.contextPath}/js/timezone/webmessenger.js"></script>
		
		<script src="${pageContext.request.contextPath}/js/timezone/timezones.full.js"></script>
		<script src="${pageContext.request.contextPath}/js/timezone/timezones.full.min.js"></script>
		
		
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