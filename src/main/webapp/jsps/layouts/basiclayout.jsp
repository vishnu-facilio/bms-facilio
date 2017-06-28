<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="/struts-tags" prefix="s" %>  
<%@taglib uri="facilio-tags" prefix="f" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  
 "http://www.w3.org/TR/html4/loose.dtd">  
<html>  
	<head>  
		<title><tiles:getAsString name="title" /></title>  
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css">
		<link rel="stylesheet" href="<s:url value="/css/sidebar-menu.css"/>">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/js/d3/style.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/js/c3/c3.min.css">
		
		<script src="https://code.jquery.com/jquery-3.0.0.min.js"></script>
		<script src="http://www.jqueryscript.net/demo/Stylish-Multi-level-Sidebar-Menu-Plugin-With-jQuery-sidebar-menu-js/dist/sidebar-menu.js"></script> 
		<script src="https://d3js.org/d3.v3.min.js"></script>
		<script src="${pageContext.request.contextPath}/js/c3/c3.min.js"></script>
		<script src="${pageContext.request.contextPath}/js/chart.lib.js"></script>
		
		<script src="${pageContext.request.contextPath}/js/d3/d3wrapper.js"></script>
		<script src="${pageContext.request.contextPath}/js/webmessenger.js"></script>  
	</head>  
<body>  
  	<table style="height: 90px;" width="1220px" border="1">
		<tbody>
		<tr>
			<td rowspan="2" style="width: 220px; background-color: #222d32;" valign="top">
				<section style="width: 220px">
    				<ul class="sidebar-menu" style="height:800px">
				      	<li class="header">MAIN NAVIGATION</li>
				      	<li class="treeview">
        					<a href="#">
          						<i class="fa fa-dashboard"></i> <span>Dashboard</span> <i class="fa fa-angle-left pull-right"></i>
        					</a>
        					<ul class="treeview-menu">
          						<li><a href="#"><i class="fa fa-circle-o"></i> Dashboard v1</a></li>
          						<li><a href="#"><i class="fa fa-circle-o"></i> Dashboard v2</a></li>
        					</ul>
      					</li>
      					<li class="treeview">
        					<a href="#">
				          		<i class="fa fa-files-o"></i>
				        		<span>Buildings</span>
				          		<span class="label label-primary pull-right">4</span>
				        	</a>
				        	<ul class="treeview-menu" style="display: none;">
					          	<li><a href="#"><i class="fa fa-circle-o"></i> Estance</a></li>
					          	<li><a href="#"><i class="fa fa-circle-o"></i> Signature</a></li>
					          	<li><a href="#"><i class="fa fa-circle-o"></i> EA</a></li>
					          	<li class=""><a href="#"><i class="fa fa-circle-o"></i> White House</a></li>
					        </ul>
      					</li>
      					<li class="treeview">
					        <a href="#">
					          	<i class="fa fa-share"></i> 
					          	<span>Facilities</span>
					          	<i class="fa fa-angle-left pull-right"></i>
					        </a>
        					<ul class="treeview-menu">
          						<li>
          							<a href="#"><i class="fa fa-circle-o"></i>Requests<i class="fa fa-angle-left pull-right"></i></a>
          							<ul class="treeview-menu">
              							<li><a href="#"><i class="fa fa-circle-o"></i>Overview</a></li>
                            			<li><a href="#"><i class="fa fa-circle-o"></i>Create New</a></li>
			                            <li><a href="#"><i class="fa fa-circle-o"></i>Created by me</a></li>
			                            <li><a href="#"><i class="fa fa-circle-o"></i>All Facility Requests</a></li>
			                            <li><a href="#"><i class="fa fa-circle-o"></i>Assigned to me</a></li>
			                         	<li><a href="#"><i class="fa fa-circle-o"></i>Open - Unassigned</a></li>
            						</ul>
            					</li>
          						<li>
						            <a href="#"><i class="fa fa-circle-o"></i>Tasks <i class="fa fa-angle-left pull-right"></i></a>
						            <ul class="treeview-menu">
						              	<li><a href="#"><i class="fa fa-circle-o"></i>All facility Tasks</a></li>
						                <li><a href="#"><i class="fa fa-circle-o"></i>Assigned to me</a></li>
						                <li><a href="#"><i class="fa fa-circle-o"></i>Open &amp; Unassigned</a></li>
						            </ul>
						        </li>
          						<li>
          							<a href="#"><i class="fa fa-circle-o"></i>Space Management<i class="fa fa-angle-left pull-right"></i></a>
						          	<ul class="treeview-menu">
				              			<li><a href="#"><i class="fa fa-circle-o"></i>Campus</a></li>
			                            <li><a href="#"><i class="fa fa-circle-o"></i>Buildings</a></li>
			            				<li><a href="#"><i class="fa fa-circle-o"></i>Floors</a></li>
			                            <li><a href="#"><i class="fa fa-circle-o"></i>Space</a></li>
			                            <li><a href="#"><i class="fa fa-circle-o"></i>Zone</a></li>
						            </ul>
          						</li>
          						<li>
          							<a href="#"><i class="fa fa-circle-o"></i>Catalog & Knowledge</a>
           							<ul class="treeview-menu">
              							<li><a href="#"><i class="fa fa-circle-o"></i>Request Templates</a></li>
                           	 			<li><a href="#"><i class="fa fa-circle-o"></i>Request Categories</a></li>
            							<li><a href="#"><i class="fa fa-circle-o"></i>Knowledge</a></li>
            						</ul>
          						</li>
          						<li><a href="#"><i class="fa fa-circle-o"></i>Administration</a></li>
        					</ul>
      					</li> 
				      	<li id="controllerId" url="/bms/home/controller">
				        	<a href="#controller">
				          		<i class="fa fa-calendar"></i> <span>Controllers</span>
				        	</a>
				      	</li>
				      	<li class="treeview">
				      	  	<a href="#">
				          		<i class="fa fa-pie-chart"></i>
				        		<span>Reports</span>
				          		<i class="fa fa-angle-left pull-right"></i>
				        	</a>
					        <ul class="treeview-menu">
					          	<li><a href="#"><i class="fa fa-circle-o"></i>View/Run</a></li>
					          	<li><a href="#"><i class="fa fa-circle-o"></i>Create New</a></li>
					          	<li><a href="#"><i class="fa fa-circle-o"></i>Scheduled Reports</a></li>
					          	<li><a href="#"><i class="fa fa-circle-o"></i>Header/Footer Templates</a></li>
					        </ul>
				      	</li>
				      	<li class="treeview">
				        	<a href="#">
				          		<i class="fa fa-edit"></i> <span>Integrations</span>
				          		<i class="fa fa-angle-left pull-right"></i>
				        	</a>
					        <ul class="treeview-menu">
					          	<li><a href="#"><i class="fa fa-circle-o"></i>ServiceNow</a></li>
					          	<li><a href="#"><i class="fa fa-circle-o"></i>Salesforce</a></li>
					          	<li><a href="#"><i class="fa fa-circle-o"></i>Zendesk</a></li>
					        </ul>
				      	</li>
				        <li>
				        	<a href="#">
				          		<i class="fa fa-th"></i> <span>Apps</span>
				          		<small class="label pull-right label-info">new</small>
				        	</a>
				      	</li>
			     	 	<li id="calendarId" url="/bms/home/calendar">
				        	<a href="#calendar">
				          		<i class="fa fa-calendar"></i> <span>Calendar</span>
				          		<small class="label pull-right label-danger">3</small>
				        	</a>
				      	</li>
				      	<li>
				        	<a href="#">
				          		<i class="fa fa-envelope"></i> <span>Mailbox</span>
				          		<small class="label pull-right label-warning">12</small>
				        	</a>
				      	</li>
				      	<li>
				        	<a href="/home/users/">
				          		<i class="fa fa-user"></i><span>Users</span>
				        	</a>
				      	</li>
      					<li><a href="#"><i class="fa fa-book"></i> <span>Documentation</span></a></li>
    				</ul>
    				<div class="user-info">
    					<div>${sessionScope.USERNAME}</div>
    					<div class="user-info-role">${requestScope.user_role}</div>
    				</div>
  				</section>
			</td>
			<td align="top" style="height:50px">
				<%@  include file="header.jsp" %>  
			</td>
		</tr>
		<tr style="vertical-align: top;">
			<td style="padding : 5px;">
			<tiles:insertAttribute name="body" />  
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<%@ include file="footer.jsp" %>  
			</td>
		</tr>
		</tbody>
	</table>
<script>
$.sidebarMenu($('.sidebar-menu'));
loadUrlFromHash();
$(window).on('hashchange',function(){ 
	loadUrlFromHash();
});

function loadUrlFromHash()
{	
	var module = location.hash.slice(1);
	if(module != "")
	{	
		var url = $('#' + module + "Id").attr('url');
		switchSection(url);
	}
}

function switchSection(url)
{
	$.ajax({
	      type: "GET",
	      url: url,   
	      success: function (response) {
	    	$('#content').html(response)
	      }
	 });
}
//loadMeters();

</script>
</body> 
</html>   
