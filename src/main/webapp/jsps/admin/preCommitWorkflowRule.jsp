<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@page import="java.util.List"%>
<%@ page import="java.util.*" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="com.facilio.aws.util.FacilioProperties" %>
<%@ page import="com.facilio.bmsconsole.commands.ReadOnlyChainFactory" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.chain.FacilioContext" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.v3.context.Constants" %>
<%@ page import="java.util.stream.*" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext" %>
<%@ page import="com.facilio.bmsconsole.ModuleSettingConfig.impl.PreCommitWorkflowRuleUtil" %>

<%
    String orgid = request.getParameter("orgid");
    String[] selectedModules = request.getParameterValues("modules");
    String[] option = request.getParameterValues("PreCommit");
    Map<Long, List<WorkflowRuleContext>> moduleIdVsRule=null;
    List<WorkflowRuleContext> ruleList=null;
    String optionSelected=null;
    if(orgid!=null)
      {
           if(option!=null ){
                optionSelected=option[0];
                moduleIdVsRule=PreCommitWorkflowRuleUtil.getModulesForPreCommitRule(Long.parseLong(orgid),option);
                ruleList= PreCommitWorkflowRuleUtil.getRuleList(selectedModules,ruleList,moduleIdVsRule);
                }
     }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pre Commit Workflow Rule</title>
<script type= "text/javascript">

function openModuleList() {
  let x = document.getElementById("showAutomationModule");
  if (x.style.display === "block") {
    x.style.display = "hidden";
  } else
    x.style.display = "block";
  }
}
function openRulesForModules() {
  let x = document.getElementById("showRulesForModules");
  if (x.style.display === "block") {
    x.style.display = "hidden";
  } else {
    x.style.display = "block";
  }
}

</script>
</head>

<body>
 <h1>Pre Commit Workflow Rule</h1>
 <form action="" name ="orgidform" method="GET">
     <h2><i class=" fa fa-building-o  fa-fw"></i>Org Id</h2>
     <div class=" col-lg-8 col-md-8">

     <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8  ">
    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
    <input id="orgid" type="number" class="form-control" name="orgid" value="<%= orgid %>" min = 0/>
     </div>
     <div style="margin-top:30px;">
    <button  onclick="openModuleList()"  >Submit</button>
  </div>
</form>

 <% if (orgid!=null) {  %>
 <div  style="margin-top:30px;">
  <h3> Select Option</h3>
    <label >
    <input type="radio"  name="PreCommit" value="true"  <% if(optionSelected!=null && optionSelected.equals("true")) { %> checked <% } %> >
     Get PreCommit Workflow Rules Modules</label>
     <label style="padding-left:30px" >
    <input type="radio" name="PreCommit" value="false"  <% if(optionSelected==null || optionSelected.equals("false")) {%> checked <% } %> >
     Get postExecute Workflow Rules Modules</label><br>
    <input type="submit" value="Submit" style="margin-top:20px;">
   </div>

 <% } %>



  <div id="showAutomationModule" style="display:hidden">
          <% if (moduleIdVsRule != null && moduleIdVsRule.size()>0) {  %>

             <h4 style="margin-top:20px; padding-left:80px">Modules for the Org --- <%=orgid%></h4>
            <div>
                <input type = "hidden" name = "orgid" value="<%= orgid %>" />
                <table style=" width: 50%; margin-top:20px;"  class="table table-bordered">
                    <tr>
                        <td style="text-align:center;"><b>ID</b></td>
                        <td style="text-align:center;"><b>MODULE NAME</b></td>
                        <td style="text-align:center;"><b>SELECT</b></td>
                    </tr>

                    <%
                        int id = 1;
                    for(Long moduleId:moduleIdVsRule.keySet())
                        {
                    %>
                            <tr>
                                <td>
                                    <label><%=id++%></label>
                                </td>
                                <td>
                                    <label><%= Constants.getModBean().getModule(moduleId).getDisplayName()%></label>
                                </td>
                                <td style="text-align:center;">
                                    <input type = "checkbox"  name="modules" value="<%=moduleId%>" <% if( selectedModules !=null && Arrays.asList(selectedModules).contains(String.valueOf(moduleId))) { %> checked <% } %> / >
                                </td>
                            </tr>
                    <%  } %>
                </table>
            </div>
            <button  id="show"  onclick="openRulesForModules()" type="submit"  >Submit</button>
          <% } else if(orgid!=null && optionSelected!=null && moduleIdVsRule==null) {  %>
          <h4>No Modules with <% if(optionSelected!=null && optionSelected.equals("true") ) { %> PreCommit <% } else { %> PostExecute <% } %> workflow Rules </h4>
          <%}%>
  </div>

<div id="showRulesForModules" style="display:hidden;width:100%">
<% if(ruleList!=null && ruleList.size()>0 ) { %>
<form method="POST" ACTION="updateRulesToPreCommit">
<h4 style="margin-top:20px; padding-left:250px">Change Workflow Rules to <% if(optionSelected!=null && optionSelected.equals("true") ) { %> PostExecute <% } else { %> PreCommit <% } %> </h4>
            <div>
                <input type = "hidden" name = "orgid" value="<%= orgid %>" />
                <table style="margin-top:20px;"  class="table table-bordered">
                    <tr>
                        <td style="text-align:center;"><b>ID</b></td>
                        <td style="width:110px;text-align:center;"><b>MODULE NAME</b></td>
                        <td style="width:110px;text-align:center;"><b>WORKFLOW ID</b></td>
                        <td style="max-width: 500px;width:400px;text-align:center;"><b>WORFLOW NAME</b></td>
                        <td style="text-align:center;"><b>SELECT</b></td>
                    </tr>

                    <%
                        int id = 1;
                    for(WorkflowRuleContext rule:ruleList)
                        {
                        String moduleName=Constants.getModBean().getModule(rule.getModuleId()).getDisplayName();
                    %>
                            <tr>
                                <td>
                                    <label><%=id++%></label>
                                </td>
                                <td>
                                    <label><%=moduleName%></label>
                                </td>
                                <td>
                                    <label><%=rule.getId()%></label>
                                </td>
                                <td>
                                    <label><%=rule.getName()%></label>
                                </td>
                                <td style="text-align:center;">
                                    <input type = "checkbox"  name="selectedRules" value="<%=rule.getId()%>" />
                                </td>
                            </tr>
                    <%  } %>
                </table>
            </div>

            <button type="submit" style="margin-bottom:50px" name="updateRules" value="<%=optionSelected%>" >update</button>

          <% }  %>
  </div>
</form>
</div>

</body>

</html>
