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
    
    	<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
    	
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
    	
    	 <!-- date picker 4.17 -->
	    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.min.css" /></head>
	    
	    <!--  selectize -->
	    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.4/css/selectize.bootstrap3.min.css" />
	  	   
    	
    	<!-- FullCalendar CSS -->
    	<link href="<%=staticURL%>/vendor/fullcalendar/fullcalendar.min.css" rel="stylesheet">
    	<link href="${pageContext.request.contextPath}/js/scheduler.min.css" rel="stylesheet">
    	
    	<link href="${pageContext.request.contextPath}/css/sb-admin-2.css" rel="stylesheet">
    	
    	<link href="${pageContext.request.contextPath}/js/c3/c3.min.css" rel="stylesheet">
    	
    	<!-- scroll bar -->
    	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery.perfect-scrollbar/0.7.0/css/perfect-scrollbar.min.css" />
    	
    	<link href="${pageContext.request.contextPath}/css/setup.css" rel="stylesheet">
    	
    	
    
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
		
		<script src="${pageContext.request.contextPath}/js/sb-admin-2.js"></script>
		<script src="${pageContext.request.contextPath}/js/app.js"></script>
		<script src="${pageContext.request.contextPath}/js/chart.lib.js"></script>
		<script src="${pageContext.request.contextPath}/js/d3/d3wrapper.js"></script>
		<script src="${pageContext.request.contextPath}/js/webmessenger.js"></script>
		
		<script>
			var contextPath = "${pageContext.request.contextPath}";
			
			$(document).ready(function() {
				$('#setuphome-wrapper').perfectScrollbar();
				
				var ht = $(window).height();
				//$('#setuphome-wrapper').css('height', (ht-100)+'px');
			});
		</script>
</head>
<body>
	<div id="wrapper">

		<%@ include file="header.jsp" %>

        <div id="setuphome-wrapper">
        	<div class="row setup-header">
        		<div class="col-lg-12 col-sm-12">
        			<div class="col-lg-6 col-sm-6">
	        			<div class="pull-left">
		       				<div class="mini-header">
		       					<a href="index"><span class="icon-head-2x"><i class="fa fa-angle-left"></i></span>&nbsp;&nbsp;Back to Product</a>
		       				</div>
		       				<div class="setup-header-text">Settings</div>
		      			</div>
		      		</div>
	      			<div class="col-lg-6 col-sm-6">
				     	<div class="action-btn pull-right">
				     	<button type="button" class="btn btn-default btn-circle cancel-btn" onclick="location.href='index';"><i class="fa fa-times"></i></button>
					 	</div>
					 </div>
	      		</div>
	      	</div>
       
       		<!-- body -->
        	<div class="row">
        		<div class="col-lg-12 col-sm-12 setup-body">
       				<div class="row">
       					<div class="col-lg-4 col-sm-4">
       						<ul class="list-unstyled">
       							<li class="setup-body-thead">General</li>
       							<li class="setup-body-tbody"><a href="setup#mysettings">Personal Settings</a></li>
       							<f:hasPermission permission="ORG_ACCESS_ADMINISTER">
       								<li class="setup-body-tbody"><a href="setup#orgsettings">Company Settings</a></li>
       							</f:hasPermission>
       						</ul>
       					</div>
       					
       					<div class="col-lg-4 col-sm-4">
       						<ul class="list-unstyled">
       							<li class="setup-body-thead">Users & Groups</li>
       							<f:hasPermission permission="USER_ACCESS_ADMINISTER">
       								<li class="setup-body-tbody"><a href="setup#users">Users</a></li>
       							</f:hasPermission>
       							<f:hasPermission permission="GROUP_ACCESS_ADMINISTER">
       								<li class="setup-body-tbody"><a href="setup#groups">Groups</a></li>
       							</f:hasPermission>
       							<f:hasPermission permission="ORG_ACCESS_ADMINISTER">
       								<li class="setup-body-tbody"><a href="setup#roles">Roles</a></li>
       							</f:hasPermission>
       						</ul>
       					</div>
       					
       					<div class="col-lg-4 col-sm-4">
       						<f:hasPermission permission="ORG_ACCESS_ADMINISTER">
      							<ul class="list-unstyled">
	       							<li class="setup-body-thead">Work Order</li>
	       							<li class="setup-body-tbody"><a href="setup#ticketstatus">Customize</a></li>
	       							<li class="setup-body-tbody"><a href="setup#notifications">Notifications</a></li>
	       							<li class="setup-body-tbody"><a href="setup#emailsettings">Email Settings</a></li>
	       						</ul>
       						</f:hasPermission>
       					</div>
       				</div>
       				
       				<div class="row">
       					<div class="col-lg-4 col-sm-4">
       						<f:hasPermission permission="ORG_ACCESS_ADMINISTER">
	       						<ul class="list-unstyled">
	       							<li class="setup-body-thead">Subscriptions</li>
	       							<li class="setup-body-tbody"><a href="#">Plans & Billings</a></li>
	       						</ul>
	       					</f:hasPermission>
       					</div>
       					
       					<div class="col-lg-4 col-sm-4">
       						<f:hasPermission permission="ORG_ACCESS_ADMINISTER">
	       						<ul class="list-unstyled">
	       							<li class="setup-body-thead">Automation</li>
	       							<li class="setup-body-tbody"><a href="#">Assignment Rules</a></li>
	       							<li class="setup-body-tbody"><a href="#">Workflow Rules</a></li>
	       							<li class="setup-body-tbody"><a href="#">SLA Esclation Rules</a></li>
	       						</ul>
       						</f:hasPermission>
       					</div>
       				</div>
        		</div>
        	</div>
        </div>
        
        <%@ include file="footer.jsp" %>
        
    </div>
</body>
</html>