<%@page import="java.net.Inet4Address"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="facilio-tags" prefix="f" %>
<%@page import="java.util.Properties, org.apache.struts2.ServletActionContext" %>
<%@page import="com.facilio.aws.util.FacilioProperties" %>
<%@ page import="com.facilio.client.app.util.ClientAppUtil" %>
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
           <a href="disableagent">
           <span class="nav-icon">
           <i class="sidebar-icon fa fa-building-o  fa-fw"></i> </span>
           <span class="nav-titile ">Agent Control</span>
           </a>
           </li> 
           
              <%--  <li>
               <a href="sqlconsole">
               <span class="nav-icon">
               <i class=" sidebar-icon fa fa-terminal  fa-fw "></i> </span>
               <span class="nav-title ">SQL Console</span>
               </a>
           </li> --%>
<%--            <li>
               <a href="copypm">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-copy fa-fw "></i> </span>
               <span class="nav-title">Copy PM</span>
               </a>
           </li> --%>
           
				<li>
               <a href="agentversion">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Agent Upgrade</span>
               </a>
           </li>
        
           <li>
               <a href="data">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Agent Data</span>
               </a>
           </li> 

           <li> 
               <a href="anomalyconsole">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Anomaly Console</span>
               </a>
           </li>
            <li>
                <a href="pmV1Monitoring">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-exclamation-triangle fa-fw "></i> </span>
                    <span class="nav-title">PM V1 Monitoring</span>
                </a>
            </li>
           <li>
               <a href="pmv2monitoring">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">PM V2 Monitoring</span>
               </a>
           </li>
           <li>
                <a href="srmonitoring">
                    <span class="nav-icon">
                        <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                    <span class="nav-title">SR Monitoring</span>
                </a>
           </li>

           <li>
               <a href="inspectionmonitoring">
                   <span class="nav-icon">
                       <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                   <span class="nav-title">Inspection Monitoring</span>
               </a>
           </li>
             <li>
               <a href="userlicense">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">User License</span>
               </a>
           </li>
			<li>
               <a href="demorollup">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Demo RollUpJob</span>
               </a>
           </li>           
			<li>
               <a href="adminreadingtools">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">Reading Tools</span>
               </a>
           </li>
           
           <li>
               <a href="deletemessagequeue">
               <span class="nav-icon">
              <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title">DeleteMessageQueue</span>
               </a>
           </li>
           <li>
                <a href="upload">
                <span class="nav-icon">
                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                <span class="nav-title">Upload credentials</span>
                 </a>
           </li>

           <% 
           String rebrand = FacilioProperties.getConfig("rebrand.domain");
           if (rebrand.equals("facilio.com")) { 
       %>
			<li>
                <a href="getinstance">
                <span class="nav-icon">
                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                <span class="nav-title">AwsInstances</span>
                 </a>
           </li>
           <% } %>
			<li>
                <a href="demorollupyearly">
                <span class="nav-icon">
                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                <span class="nav-title">DemoRollUpYearly</span>
                 </a>
           </li>
<%--            <li>--%>
<%--                <a href="fieldMigration">--%>
<%--                <span class="nav-icon">--%>
<%--                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>--%>
<%--                    <span class="nav-title">Reading Field Data Migration</span>--%>
<%--                </a>--%>
<%--            </li>--%>
<%--            <li>--%>
<%--                <a href="deleteReadings">--%>
<%--                <span class="nav-icon">--%>
<%--                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>--%>
<%--                    <span class="nav-title">Delete Readings</span>--%>
<%--                </a>--%>
<%--            </li>--%>
<%--            <li>--%>
<%--                <a href="moveReadings">--%>
<%--                <span class="nav-icon">--%>
<%--                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>--%>
<%--                    <span class="nav-title">Move Readings</span>--%>
<%--                </a>--%>
<%--            </li>--%>
            <li>
                <a href="fieldMigration">
                <span class="nav-icon">
                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                    <span class="nav-title">Field Migration</span>
                </a>
            </li>
            <li>
                <a href="deleteReadings">
                <span class="nav-icon">
                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                    <span class="nav-title">Delete Readings</span>
                </a>
            </li>
             <li>
                <a href="mlService">
                <span class="nav-icon">
                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                    <span class="nav-title">ML Service</span>
                </a>
            </li> 
            <li>
                <a href="moveReadings">
                <span class="nav-icon">
                <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
                    <span class="nav-title">Move Readings</span>
                </a>
            </li>
         </ul>
        
      <div class="nav  sidebar-footer-btn  ">
      <div class="col-lg-12 col-centered build-details" style="margin:10px; font-size: 12px;">
	    <table>
	        <tbody>
            <tr>
                <td>
                    Build Branch:
                </td>
                <td>
                    <%= buildinfo.get("build.branch")%>
                </td>
            </tr>
            <tr>
                <td>
                    Build Number:
                </td>
                <td>
                    <%= buildinfo.get("build.number")%>
                </td>
            </tr>
            <tr>
                <td>
                    Commit Id:
                </td>
                <td>
                <a href="https://bitbucket.org/facilio/bmsconsole/commits/<%=buildinfo.get("build.commitid")%>"target="_blank">
                   <%="<u>Click here</u>"%>



                </td>
            </tr>
            <tr>
	            <td>
	                Build Date:
	            </td>
	            <td>
	                <%= buildinfo.get("build.date")%>
	            </td>
	        </tr>
<%--	        <tr>--%>
<%--	            <td>--%>
<%--	                Server Ver:--%>
<%--	            </td>--%>
<%--	            <td>--%>
<%--	                <%= buildinfo.get("version")%>--%>
<%--	            </td>--%>
<%--	        </tr>--%>
            <tr>
                <td>
                    Client Ver:
                </td>
                <td>
                    <%= ClientAppUtil.getClientBuildInfo(request).getVersion() %>
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
#side-menu{
  padding-bottom: 150px;
}
  </style>
