<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.bmsconsole.ModuleSettingConfig.impl.PageBuilderConfigUtil" %>
<%@page import="java.util.List"%>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="com.facilio.aws.util.FacilioProperties" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%

  String orgid = request.getParameter("orgid");
  TreeMap<String,Boolean> modulesVsStatus = new TreeMap<>();
  if (orgid != null) {
      modulesVsStatus = PageBuilderConfigUtil.getModuleVsPageBuilderStatusMap(Long.parseLong(orgid));
  }

%>




<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Page Builder Module Configuration</title>
<script type= "text/javascript">

function openPageBuilder() {
  let x = document.getElementById("enablePageForModule");
  if (x.style.display === "block") {
    x.style.display = "hidden";
  } else {
    x.style.display = "block";
  }
}
</script>
<style>
.submitBtn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            align-items: center;
        }
</style>
</head>
<body>
      <h1>Page Builder Configuration</h1>

<form action="" name ="orgidform" method="GET">
    <h2><i class=" fa fa-building-o  fa-fw"></i>Org Id</h2>
 	 <div class=" col-lg-8 col-md-8">

    <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
		<span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
		<input id="orgid" type="number" class="form-control" name="orgid" value="<%= orgid %>" min = 0/>
    </div>

    <div style="margin-top:30px;">
		<button onclick="openPageBuilder()">Submit</button>
	</div>
</form>


    <div id="enablePageForModule" style="display:hidden">
                		<% if (orgid!=null) { %>
                			<form method="POST" ACTION="addOrUpdatePageBuilderModuleConfig">
                				<h4>Modulewise Page Builder Configuration:</h4>
                				<div>
                					<input type = "hidden" name = "orgid" value="<%= orgid %>" />
                					<table style=" width: 50%; margin-top:40px;"  class="table table-bordered">
                						<tr>
                							<td style="text-align:center;"><b>ID</b></td>
                							<td style="text-align:center;"><b>MODULES</b></td>
                							<td style="text-align:center;"><b>STATUS</b></td>
                						</tr>

                						<%
                							int id = 0;
                							for(String key  :modulesVsStatus.keySet())
                							{
                  						%>
                								<tr>
                									<td>
                										<label><%=++id%></label>
                									</td>
                									<td>
                										<label><%=key%></label>
                									</td>
                									<td style="text-align:center;">
                										<input type = "checkbox" <% if (modulesVsStatus.get(key)) { %> checked <%  }%> name="selected" value="<%=key%>" />
                									</td>
                								</tr>
                 						<%  } %>
                					</table>
				                </div>
                			<%--	<%
                					String currentUserEmail = AccountUtil.getCurrentUser().getEmail();
                					List<String> allowedUserToUpdateLicence = Arrays.asList("");
                					if(!FacilioProperties.isProduction() || (allowedUserToUpdateLicence.contains(currentUserEmail))) {
                  				%>     --%>
						            <input type="submit" name="addOrUpdatePageBuilderModuleConfig" value="Update" />
                			<%--	<%  } %>      --%>
                            </form>

                		<% } %>
    </div>

</body>
</html>
