<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="facilio-tags" prefix="f" %>
<%@page import="java.util.Properties, org.apache.struts2.ServletActionContext" %>
<%Properties buildinfo = (Properties)ServletActionContext.getServletContext().getAttribute("buildinfo");%>
<div >
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
 <li >
               <a href="buildInfo">
               <span class="nav-icon">
               <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title ">Build Info</span>
               </a>
           </li>
           
             <li>
               <a href="usermanagement">
                <span class="nav-icon">
               <i class=" sidebar-icon fa fa-user fa-fw "></i> </span>
               <span class="nav-title ">User Management</span>
               </a>
           </li>
                      
           <li>
               <a href="${pageContext.request.contextPath}/app/admin/sqlconsole">
               <span class="nav-icon">
               <i class=" sidebar-icon fa fa-terminal  fa-fw "></i> </span>
               <span class="nav-title ">SQL Console</span>
               </a>
           </li>
           <li>
               <a href="copypm">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Copy PM</span>
               </a>
           </li>
           <li>
               <a href="closewo">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Close WorkOrder</span>
               </a>
           </li>
           
         </ul>
        
      <div class="nav  sidebar-footer-btn  ">
        <div class="col-lg-12 col-centered" style="margin-bottom:30px">
		<a href="#" data-toggle="tooltip" data-placement="bottom" title="Build Version" > Build Date: <%= buildinfo.get("build.date") %> </a>
		<br> <a href="#" data-toggle="tooltip" data-placement="bottom" title="Build Version" > Version: <%= buildinfo.get("version") %></a>
			
        </div>
        </div>
    </div>
    
    
     
    </div>
    
        
        
        <script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();   
});

</script>
        
  

</div>