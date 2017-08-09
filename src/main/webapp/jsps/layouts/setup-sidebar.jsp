<%@taglib uri="facilio-tags" prefix="f" %>
<div class="navbar-default sidebar navbar-collapse" role="navigation">
	<div class="nav back-to-product">
		<div class="col-lg-12">
			<a href="index">
				<span class="icon-head-2x"><i class="fa fa-angle-left"></i></span>&nbsp;&nbsp;Back to Product
			</a>
        </div>
    </div>
    <div class="sidebar-nav ">
        <ul class="nav metismenu" id="side-menu">
        	<li class="sidebar-search">
                <div class="custom-search-form">
                    <input type="text" class="form-control setup-search" placeholder="Search...">
                </div>
            </li>
            <li>
                <a href="#">
                	<span class="nav-title">General</span>
                	<span class="fa arrow"></span>
                </a>
                <ul class="nav nav-second-level">
                    <li>
                        <a href="#mysettings">Personal Settings</a>
                    </li>
                    <f:hasPermission permission="ORG_ACCESS_ADMINISTER">
                    	<li>
	                        <a href="#orgsettings">Company Settings</a>
	                    </li>
                    </f:hasPermission>
                </ul>
            </li>
            <li>
                <a href="#">
                	<span class="nav-title">Users & Groups</span>
                	<span class="fa arrow"></span>
                </a>
                <ul class="nav nav-second-level">
                	 <f:hasPermission permission="USER_ACCESS_ADMINISTER">
                    	<li>
	                        <a href="#users">Users</a>
	                    </li>
                    </f:hasPermission>
                    <f:hasPermission permission="GROUP_ACCESS_ADMINISTER">
	                    <li>
	                        <a href="#groups">Groups</a>
	                    </li>
	                </f:hasPermission>
	                <f:hasPermission permission="ORG_ACCESS_ADMINISTER">
	                    <li>
	                        <a href="#roles">Roles</a>
	                    </li>
	                </f:hasPermission>
                </ul>
            </li>
            <f:hasPermission permission="ORG_ACCESS_ADMINISTER">
	            <li>
	                <a href="#">
	                	<span class="nav-title">Work Order</span>
	                	<span class="fa arrow"></span>
	                </a>
	                 <ul class="nav nav-second-level">
	                    <li>
	                        <a href="#">
	                            <span class="nav-title">Customize</span>
	                            <span class="fa arrow"></span>
	                        </a>
	                        <ul class="nav nav-third-level">
	                            <li>
	                                <a href="#ticketstatus">Status</a>
	                            </li>
	                            <li>
	                                <a href="#ticketpriority">Priority</a>
	                            </li>
	                            <li>
	                                <a href="#ticketcategory">Category</a>
	                            </li>
	                        </ul>
	                    </li>
	                    <li>
	                        <a href="#notifications">Notifications</a>
	                    </li>
	                    <li>
	                        <a href="#emailsettings">Email Settings</a>
	                    </li>
	                </ul>
	            </li>
	                     <li>
                <a href="#">
                	<span class="nav-title">Data Administration</span>
                	<span class="fa arrow"></span>
                </a>
                <ul class="nav nav-second-level">
                    <li>
                        <a href="#import">Import</a>
                    </li>
                    <f:hasPermission permission="ORG_ACCESS_ADMINISTER">
                    	<li>
	                        <a href="#">Export</a>
	                    </li>
                    </f:hasPermission>
                </ul>
            </li>
	            <li>
	                <a href="#subscriptions">
	                	<span class="nav-title">Subscriptions</span>
	                </a>
	            </li>
	            <li>
	                <a href="#customerPortal">
	                	<span class="nav-title">Customer Portal</span>
	                </a>
	            </li>
	        </f:hasPermission>
        </ul>
    </div>
    <!-- /.sidebar-collapse -->

</div>