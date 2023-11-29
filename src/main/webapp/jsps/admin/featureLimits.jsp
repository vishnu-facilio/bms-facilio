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
<%@ page import="org.apache.commons.collections.MapUtils" %>
<%@ page import="com.facilio.bmsconsole.util.FeatureLimitsUtil" %>
<%
    String orgid = request.getParameter("orgid");
    Map<String, Long> featureNameVsLimits=null;
    if(orgid!=null)
      {
          featureNameVsLimits= FeatureLimitsUtil.getFeatureNameVsLimits(Long.parseLong(orgid));

     }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pre Commit Workflow Rule</title>
<script type= "text/javascript">
var map = new Map();
function openModuleList() {
  let x = document.getElementById("showAutomationModule");
  if (x.style.display === "block") {
    x.style.display = "hidden";
  } else{
    x.style.display = "block";
  }
}

</script>
    <style type="text/css">
        .btn {
            display: inline-block;
            padding: 8px 20px;
            font-size: 16px;
            font-weight: bold;
            text-align: center;
            text-decoration: none;
            border-radius: 5px;
            border: 2px solid #3498db;
            color: #fff;
            background-color: #3498db;
            overflow: hidden;
            position: relative;
            transition: color 0.4s, background-color 0.4s;
            cursor: pointer;
            height: auto;
        }

        .btn::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 300%;
            height: 300%;
            background-color: #fff;
            transition: width 0.4s, height 0.4s, top 0.4s, left 0.4s, opacity 0.4s;
            border-radius: 50%;
            z-index: 0;
            transform: translate(-50%, -50%);
            opacity: 0;
        }

        .btn:hover::before {
            width: 0;
            height: 0;
            top: 50%;
            left: 50%;
            opacity: 1;
        }

        .btn:hover {
            color: #3498db;
            background-color: #fff;
            z-index: 1;
        }
        .form-container {
            max-width: 300px;
            margin: 20px auto;
        }
        .form-container > div {
            display: grid;
            grid-template-columns: max-content auto;
            align-items: center;
            margin-bottom: 10px;
        }

        label {
            display: flex;
            width: 200px;
            justify-content: center;
            text-align: right;
            margin-right: 10px;
            font-size: 15px;
        }
        input[type="text"],
        input[type="number"] {
            width: 200px;
            height: 30px;
            padding: 8px;
            border-radius: 4px;
            border: 1px solid #ccc;
        }

        /* Style for button container */
        .button-container {
            display: flex;
            justify-content: center;
        }
    </style>
</head>
<body>
 <h1>Feature Limits</h1>
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

<div id="showRulesForModules" style="display:hidden;width:100%">
<% if (MapUtils.isNotEmpty(featureNameVsLimits)) { %>
   <form method="POST" ACTION="updateFeatureLimits">
<h4 style="margin-top:20px; padding-left:250px">Features and Limits Table </h4>
            <div>
                <input type = "hidden" name = "orgid" value="<%= orgid %>" />
                <table style="margin-top:20px;"  class="table table-bordered">
                    <tr>
                        <td style="width:90px;text-align:center;"><b>ID</b></td>
                        <td style="width:200px;text-align:center;"><b>FEATURE NAME</b></td>
                        <td style="width:100px;text-align:center;"><b>FEATURE LIMITS</b></td>
                    </tr>
                    <%
                        int id = 1;
                    for(String key :featureNameVsLimits.keySet())
                        {
                    %>
                            <tr>
                                <td style="text-align:center;">
                                    <label>#<%=id++%></label>
                                </td>
                                <td style="text-align:center;">
                                    <label><%=key%></label>
                                </td>
                                <td style="text-align:center;">
                                    <label> <%=featureNameVsLimits.get(key) %> </label>
                                </td>

                            </tr>
                    <%  } %>
                </table>
            </div>
       <div class="form-container">
           <div>
               <label for="featureName"><b style="font-size: 15px;">Enter Feature Name :</b></label>
               <input type="text" name="featureName" id="featureName">
           </div>
           <div>
               <label for="featureLimit"><b style="font-size: 15px;">Enter Limit Count :</b></label>
               <input type="number" name="featureLimit" id="featureLimit">
           </div>
           <div class="button-container">
               <button type="submit" class="btn">Update</button>
           </div>
       </div>
            </form>
    <% } else {  %>
    <h4>No Data Available</h4>
    <%}%>
  </div>
</div>
</body>
