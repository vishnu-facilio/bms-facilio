<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="facilio-tags" prefix="f" %>
<div class="navbar-default sidebar " role="navigation">
    <div class="sidebar-nav navbar-collapse">
        <ul class="nav metismenu" id="side-menu">
        <!-- 
            <li class="sidebar-search">
                <div class="input-group custom-search-form">
                    <input type="text" class="form-control" placeholder="Search...">
                    <span class="input-group-btn">
                    <button class="btn btn-default" type="button" style="padding: 9px 12px;">
                        <i class="fa fa-search"></i>
                    </button>
                </span>
                </div>
               
            </li>
            -->
            <f:hasPermission permission="DASHBOARD_ACCESS_ENABLE">
            	<li>
	                <a href="#dashboard">
	                <span class="nav-icon">
	                <i class=" sidebar-icon fa fa-dashboard fa-fw "></i> </span>
	                <span class="nav-title">Dashboard</span>
	                </a>
	            </li>
            </f:hasPermission>
            <f:hasPermission permission="WORKORDER_READ">
	             <li>
	                <a href="#workorder"><span class="nav-icon"><i class="sidebar-icon fa fa-briefcase fa-fw"></i></span>
	                <span class="nav-title all-workorder"> Work Orders</span>
	                <f:hasPermission permission="WORKORDER_CREATE">
	               		<span class="nav-icon-right add-new-workorder pull-right fc-plus">+</span>
	               	</f:hasPermission>
	                </a>
	            </li>
            </f:hasPermission>
            <f:hasPermission permission="TASK_READ">
	            <li>
	               <a href="#task"><span class="nav-icon"><i class="sidebar-icon fa fa-tasks fa-fw"></i></span>
	               <span class="nav-title all-task">Tasks</span>
	               <f:hasPermission permission="TASK_CREATE">
	               		<span class="nav-icon-right add-new-task pull-right fc-plus">+</span>
	               </f:hasPermission>
	               </a>
	            </li>
            </f:hasPermission>
            <f:hasPermission permission="TASK_READ">
	            <li>
	                <a href="#calendar">
	                <span class="nav-icon"><i class="sidebar-icon fa fa-calendar fa-fw"></i></span>
	                <span class="nav-title">Calendar
	                </span></a>
	            </li>
            </f:hasPermission>
            <f:hasPermission permission="TASK_ASSIGN">
	            <li>
	                <a href="#centraldispatch">
	                <span class="nav-icon"><i class="sidebar-icon fa fa-calendar fa-fw"></i></span>
	                <span class="nav-title">Central Dispatch
	                </span></a>
	            </li>
            </f:hasPermission>
            <f:hasPermission permission="SPACEMANAGEMENT_ACCESS_ENABLE">
				<li>
	               <a href="#">
	               <span class="nav-icon"><i class="sidebar-icon fa fa-globe fa-fw"></i></span> 
	               <span class="nav-title">Space Management</span>
	               <span class="fa arrow"></span>
	               </a>
	                <ul class="nav nav-second-level">
	               		<li>
	                        <a href="#campus">Campus</a>
	                    </li>
	                    <li>
	                        <a href="#building">Building</a>
	                    </li>
	                    <li>
	                        <a href="#floor">Floor</a>
	                    </li>
	                    <li>
	                        <a href="#space">Space</a>
	                    </li>
	                    <li>
	                        <a href="#zone">Zone</a>
	                    </li>
	                  </ul>
	            </li>
	        </f:hasPermission>
         <%--    <li>
                <a href="#"><i class="sidebar-icon fa fa-ticket fa-fw"></i> Facilities<span class="fa arrow"></span></a>
                <ul class="nav nav-second-level">
                    <li>
                        <a href="#">Requests <span class="fa arrow"></span></a>
                        <ul class="nav nav-third-level">
                            <li>
                                <a href="#ticket">All Requests</a>
                            </li>
                            <li>
                                <a href="#">All Open Requests</a>
                            </li>
                            <li>
                                <a href="#">Overdue Requests</a>
                            </li>
                            <li>
                                <a href="#">My Requests</a>
                            </li>
                            <li>
                                <a href="#">My Open Requests</a>
                            </li>
                            <li>
                                <a href="#">My Overdue Requests</a>
                            </li>
                        </ul>
                        <!-- /.nav-third-level -->
                    </li>
                    <li>
                        <a href="#">Tasks <span class="fa arrow"></span></a>
                        <ul class="nav nav-third-level">
                            <li>
                                <a href="#tasks">All Tasks</a>
                            </li>
                            <li>
                                <a href="#">My Tasks</a>
                            </li>
                            <li>
                                <a href="#">Unassigned</a>
                            </li>
                        </ul>
                        <!-- /.nav-third-level -->
                    </li>
                    <li>
                        <a href="#">Space Management <span class="fa arrow"></span></a>
                        <ul class="nav nav-third-level">
                            <li>
                                <a href="#campus">Campus</a>
                            </li>
                            <li>
                                <a href="#building">Building</a>
                            </li>
                            <li>
                                <a href="#floor">Floor</a>
                            </li>
                            <li>
                                <a href="#space">Space</a>
                            </li>
                            <li>
                                <a href="#zone">Zone</a>
                            </li>
                        </ul>
                        <!-- /.nav-third-level -->
                    </li>
                    <li>
                        <a href="#">Catalog & Knowledge <span class="fa arrow"></span></a>
                        <ul class="nav nav-third-level">
                            <li>
                                <a href="#">Request Templates</a>
                            </li>
                            <li>
                                <a href="#">Request Categories</a>
                            </li>
                            <li>
                                <a href="#">Knowledge</a>
                            </li>
                        </ul>
                        <!-- /.nav-third-level -->
                    </li>
                </ul>
                <!-- /.nav-second-level -->
            </li>
             --%>
            <f:hasPermission permission="REPORTS_ACCESS_ENABLE">
	            <li>
	                <a href="#">
	                <span class="nav-icon"><i class="sidebar-icon fa fa-pie-chart fa-fw"></i></span>
	                 <span class="nav-title">Reports</span>
	                 <span class="fa arrow"></span></a>
	                <ul class="nav nav-second-level">
	                    <li>
	                        <a href="#reports">Peak Energy Analysis</a>
	                    </li>
	                 </ul>
	            </li>
	        </f:hasPermission>
            <!-- 
            <li>
                <a href="#"><i class="fa fa-wrench fa-fw"></i> Administration<span class="fa arrow"></span></a>
                <ul class="nav nav-second-level">
                    <li>
                        <a href="#users">Users</a>
                    </li>
                    <li>
                        <a href="#role">Roles</a>
                    </li>
                    <li>
                        <a href="#groups">Groups</a>
                    </li>
                    <li>
                        <a href="#controller">Controllers</a>
                    </li>
                    <li>
                        <a href="#locations">Locations</a>
                    </li>
                    <li>
                        <a href="#skill">Skills</a>
                    </li>
                    <li>
                        <a href="#">Rules <span class="fa arrow"></span></a>
                        <ul class="nav nav-third-level">
                            <li>
                                <a href="#rules/assignmentRules">Assignment Rules</a>
                            </li>
                            <li>
                                <a href="#rules/businessRules">Business Rules</a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </li>
            <li>
                <a href="#help"><i class="fa fa-question-circle fa-fw"></i> Help</a>
            </li>
            -->
        </ul>
        
       <div class="nav  sidebar-footer-btn  ">
        <div class="col-lg-12 col-centered">
		<a href="#"><span class="icon-head-2x">
			<i class="fa fa-angle-left"></i>
			<i class="fa fa-angle-right hidden"></i>
			</span></a>
        </div>
        </div>
  
    </div>
    <!-- /.sidebar-collapse -->

</div>