<%@page import="java.net.Inet4Address"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="facilio-tags" prefix="f" %>
<%@page import="java.util.Properties, org.apache.struts2.ServletActionContext" %>
<%
Properties buildinfo = (Properties)ServletActionContext.getServletContext().getAttribute("buildinfo");
if (buildinfo == null) {
	buildinfo = new Properties();
}
%>
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
           <a href="orginfo">
           <span class="nav-icon">
           <i class="sidebar-icon fa fa-building-o  fa-fw"></i> </span>
           <span class="nav-titile ">Org Info</span>
           </a>
           </li>  	
               <li>
               <a href="sqlconsole">
               <span class="nav-icon">
               <i class=" sidebar-icon fa fa-terminal  fa-fw "></i> </span>
               <span class="nav-title ">SQL Console</span>
               </a>
           </li>
<%--            <li>
               <a href="copypm">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-copy fa-fw "></i> </span>
               <span class="nav-title">Copy PM</span>
               </a>
           </li> --%>
        
           <li> 
               <a href="anomalyconsole">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Anomaly Console</span>
               </a>
           </li>
           
           <li>
               <a href="closewo">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Close WorkOrder</span>
               </a>
           </li>
             <li>
               <a href="userlicense">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">User License</span>
               </a>
           </li>
           
         </ul>
        
      <div class="nav  sidebar-footer-btn  ">
      <div class="col-lg-12 col-centered build-details" style="margin:10px; font-size: 12px;">
	    <table>
	        <tbody><tr>
	            <td>
	                Build Date:
	            </td>
	            <td>
	                <%= buildinfo.get("build.date")%>
	            </td>
	        </tr>
	        <tr>
	            <td>
	                Server Ver:
	            </td>
	            <td>
	                <%= buildinfo.get("version")%>
	            </td>
	        </tr>
	        <tr>
	            <td>
	                Client Ver:
	            </td>
	            <td>
	                <%= (String)com.facilio.aws.util.AwsUtil.getClientInfo().get("version")%>
	            </td>
	        </tr>
	        <tr>
	            <td>
	                Serving IP:
	            </td>
	            <td>
	                <%= Inet4Address.getLocalHost().getHostAddress()%>
	            </td>
	        </tr></tbody></table>
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
<style>
  .build-details table tr td {
    color: #fff;
    padding: 2px;
    text-align: left;
}
  </style>