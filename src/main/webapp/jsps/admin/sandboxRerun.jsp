<%@page import="com.facilio.logging.SysOutLogger"%>
<%@page import="com.facilio.accounts.bean.OrgBean"%>
<%@page import="com.facilio.aws.util.FacilioProperties" %>
<%@page import="com.facilio.accounts.util.AccountUtil,java.util.Comparator"%>
<%@page import="com.facilio.accounts.dto.User,com.facilio.accounts.dto.*, java.util.*"%>
<%@page import="java.util.Iterator ,org.json.simple.JSONObject,org.json.simple.JSONArray,java.util.List"%>
<%@page import="com.facilio.accounts.dto.Organization ,org.json.simple.JSONObject,com.facilio.accounts.impl.OrgBeanImpl"%>
<%@page import="com.facilio.bmsconsole.commands.util.CommonCommandUtil, com.facilio.accounts.util.AccountUtil.FeatureLicense"%>
<%@ page import="com.facilio.bmsconsole.util.ApplicationApi" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@page import="com.facilio.componentpackage.bean.OrgSwitchBean"%>
<%@page import="com.facilio.sandbox.utils.SandboxAPI" %>
<%@page import="com.facilio.sandbox.utils.SandboxUtil" %>
<%@page import="com.facilio.sandbox.context.SandboxConfigContext" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String orgid = request.getParameter("orgid");
    Organization org = null;
    boolean user=false;
    JSONObject result = null;
    List<User> users = null;
    List<Role> roles = null;
    List<SandboxConfigContext> sandboxList = null;
    long sandboxCount;
    Map<Long, String> componentTypeMap = SandboxUtil.getComponentTypeMap();
    List<Integer> skipComponents = new ArrayList<>();
    TreeMap<String,Boolean> features = new TreeMap<>();

  if (orgid != null) {
    long orgId = Long.parseLong(orgid);

    OrgBean orgBean = AccountUtil.getOrgBean();
    org = orgBean.getOrg(Long.parseLong(orgid));
    result = AccountUtil.getOrgBean(orgId).orgInfo();
    sandboxCount = SandboxUtil.getOrgSwitchBean(orgId).getSandboxCount(null);
    sandboxList = SandboxUtil.getOrgSwitchBean(orgId).getAllSandbox(1, (int)sandboxCount, null);
    long appId = AccountUtil.getOrgBean(orgId).getDefaultApplicationId();
    if(appId > 0) {
      users = AccountUtil.getOrgBean(orgId).getAppUsers(orgId, appId, false);
      roles = AccountUtil.getRoleBean(orgId).getRolesForApps(Collections.singletonList(appId));
    }
    features  = AccountUtil.getFeatureLicenseMap(orgId);
  }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript">
function showComponents() {
  let x = document.getElementById("rerunPage");
  if (x.style.display === "block") {
    x.style.display = "none";
  } else {
    x.style.display = "block";
  }
}
$(window).on('load', function() {
        hideOrShowFields();
        $('input[name="fileVisibility"]').change(hideOrShowFields);
    });

    function hideOrShowFields() {
        const showFileRadioChecked = $("#showFileRadio").is(":checked");
        $("#sourceOrgIdRow").toggle(showFileRadioChecked);
        $("#targetOrgIdRow").toggle(!showFileRadioChecked);
        $("#fileRow").toggle(!showFileRadioChecked);
    }

    function selectAllItems() {
        $("input[name='selectedComponents']").prop('checked', true);
    }

    function deselectAllItems() {
        $("input[name='selectedComponents']").prop('checked', false);
    }

    function checkInputFields() {
        const sourceOrgId = $('#sourceOrgId').val();
        const domainName = $('#domainName').val();
        return sourceOrgId && domainName;
    }

    function handleAllAction() {
        const actionType = $("input[name='actionType']:checked").val();

        if (actionType === 'MetaRerun') {
            // Handle Meta-Rerun action
            if (!checkInputFields()) {
                alert("Please enter values for both Source OrgId and Sandbox Domain Name.");
                return;
            }

            const dataObject = {};
            dataObject.sourceOrgId = $('#sourceOrgId').val();
            dataObject.domainName = $('#domainName').val();
            dataObject.fromAdminTool = true;

            var selectedComponents = [];
            $("input[name='selectedComponents']:checked").each(function() {
                selectedComponents.push(parseInt($(this).val()));
            });

            dataObject.skipComponents = selectedComponents;

            var jsonData = JSON.stringify(dataObject);

            $.ajax({
                url: "/admin/reRunSandbox",
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: jsonData,
                success: function(data) {
                    alert("Message : " + data.message);
                },
                error: function(xhr, status, error) {
                    console.log("Error: " + xhr.responseText);
                    alert("Error occurred for Sandbox Rerun Action: " + xhr.responseText);
                }
            });
        } else if (actionType === 'MetaInstall') {
            // Handle Meta-install action
            if (!checkInputFields()) {
                alert("Please enter values for both Source OrgId and Sandbox Domain Name.");
                return;
            }


            const dataObject = {};
            const fileInput = document.getElementById('fileInput');
            const file = fileInput.files[0];
            const formData = new FormData();
            formData.append('file', file);
            formData.append('fromAdminTool', true);
            formData.append('sourceOrgId', $('#sourceOrgId').val());
            formData.append('domainName', $('#domainName').val());

            var selectedComponents = [];
            $("input[name='selectedComponents']:checked").each(function() {
                selectedComponents.push(parseInt($(this).val()));
            });


            formData.append('skipComponents',selectedComponents);
            $.ajax({
            url: "/admin/doInstall", // Change this to your custom endpoint URL
            type: "POST",
            dataType: "json",
            data: formData,
            processData: false,
            contentType: false,
            enctype: 'multipart/form-data',
            cache: false,
                success: function (data) {
                    alert("Message : " + data.message);
                },
                error: function (xhr, status, error) {
                    console.log("Error: " + xhr.responseText);
                    alert("Error Occurred For Do Install Only Action: " + xhr.responseText);
                }
            });
        }
        else if (actionType === 'sandboxCreation') {
            if (!checkInputFields()) {
                alert("Please enter values for both Source OrgId and Sandbox Domain Name.");
                return;
            }

            const dataObject = {};
            dataObject.sourceOrgId = $('#sourceOrgId').val();
            dataObject.domainName = $('#domainName').val();
            dataObject.fromAdminTool = true;

            var selectedComponents = [];
            $("input[name='selectedComponents']:checked").each(function() {
                selectedComponents.push(parseInt($(this).val()));
            });

            dataObject.skipComponents = selectedComponents;

            $.ajax({
                url: "/admin/sandboxCreation",
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(dataObject),
                success: function (data) {
                    alert("Message : " + data.message);
                },
                error: function (xhr, status, error) {
                    console.log("Error: " + xhr.responseText);
                    alert("Error Occurred For Sandbox Creation Action: " + xhr.responseText);
                }
            });
        }
    }
</script>

<body>
<form action="" method="GET">
        <h2><i class="fa fa-building-o fa-fw"></i>Org Info</h2>
        <div class="col-lg-8 col-md-8">
            <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8">
                <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                <input id="orginfo" type="text" value="<%= org == null ? "" : org.getId() %>" class="form-control" name="orgid" />
            </div>
            <div style="margin-top:30px;">
                <button id="show" type="submit">Submit</button>
                <% if(orgid != null && sandboxList!=null) { %>
                    <table style="margin-top:40px;" class="table table-bordered">
                        <!-- Your table headers -->
                        <!-- Iterating over sandboxList based on orgid -->
                        <% for (SandboxConfigContext b : sandboxList) { %>
                            <% if (orgid != null && orgid.equals(Long.toString(b.getOrgId()))) { %>
                                <tr>
                                    <td style="max-width: 350px;width:350px;" align="center"><%= b.getId() %></td>
                                    <td style="max-width: 350px;width:350px;" align="center"><%= b.getOrgId() %></td>
                                    <td style="max-width: 350px;width:350px;" align="center"><%= b.getName() %></td>
                                    <td style="max-width: 350px;width:350px;" align="center"><%= b.getSubDomain() %></td>
                                    <td style="max-width: 350px;width:350px;" align="center"><%= b.getSandboxOrgId() %></td>
                                    <td style="max-width: 350px;width:350px;" align="center"><%= b.getStatusEnum() %></td>
                                    <td style="max-width: 350px;width:350px;" align="center"><%= b.getSandboxType() %></td>
                                </tr>
                            <% } %>
                        <% } %>
                    </table>
                <% } %>
            </div>
        </div>
    </form>

    <!-- Show Components button below the table -->
    <div class=" col-lg-12 col-md-12">
    	<% if (orgid!=null) { %>
    		<button onclick="showComponents()">Show Components</button>
    	<% } %>
    	<br>
    	<div id="rerunPage" style="display:none">
    		<% if (orgid!=null) { %>
    			<form id="componentForm">
                    <div class="radio-group">
                        <label class="radio-text">
                            <input class="radio-label" type="radio" name="actionType" value="MetaRerun" checked="checked"> MetaRerun
                        </label>
                        <label class="radio-text">
                            <input class="radio-label" type="radio" name="actionType" value="MetaInstall"> MetaInstall
                        </label>
                        <label class="radio-text">
                            <input class="radio-label" type="radio" name="actionType" value="sandboxCreation"> Sandbox-Creation
                        </label>
                    </div>
                    <table class='orgIdTable' id="orgIdTable">
                        <tr>
                           <td><h4>Source OrgId</h4></td>
                           <td>
                              <input type="text" id="sourceOrgId" required class="input-field-long" />
                           </td>
                        </tr>
                        <tr>
                           <td class="table-head"><h4>SandBox Domain-Name</h4></td>
                           <td>
                              <input type="text" id="domainName" required class="input-field-long" />
                           </td>
                        </tr>
                    </table>
                    <h3> !!! Select The Components You Want To Skip, But Select Only Independent Components (Try To skipComponents Which Are Copied Already) !!! </h3>
                    <button type="button" class='selectBtn' id="selectBtn" onclick="selectAllItems()">Select All</button>
                    <button type="button" class='deselectBtn' id="deselectBtn" onclick="deselectAllItems()">Deselect All</button>
                    <table class='componentTable' id="componentTable">
                        <tr>
                            <th>Order</th>
                            <th>Component Value</th>
                            <th>Select</th>
                        </tr>
                        <%
                            for (Map.Entry<Long, String> ordinalVsComponent : componentTypeMap.entrySet()) {
                                long key = ordinalVsComponent.getKey() + 1L;
                                String value = ordinalVsComponent.getValue();
                        %>
                        <tr>
                            <td><%= key %></td>
                            <td><%= value %></td>
                            <td style="text-align:center;">
                                <input type="checkbox" name="selectedComponents" value="<%= key %>"
                                        <% if (skipComponents.contains(key)) { %> checked="checked" <% } %> />
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    <tr id="fileRow">
                        <td class="table-head"><h4>File Upload</h4></td>
                        <td>
                            <input type="file" id="fileInput" />
                        </td>
                    </tr>
                    </table>
                    <button type="button" class='submitBtn' id="submitBtn" onclick="handleAllAction()">Submit</button>
                </form>

    		<% } %>
    		</div>
          </div>

<style>
.org-th {
  background-color: #f0f2f4;
  font-size: 14px;
  color: #717b85;
  font-weight: 500;
  padding: 18px 20px !important;
}
.org-td {
  color: #333;
  font-size: 13px;
  padding: 15px 20px !important;
}
select {
	width: 17%;
	padding: 12px 20px;
	margin: 8px 16px;
	margin-top: 8px;
	display: inline-block;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
	font-size: 16px;
	margin-left:44px;
}

input[type=text] {
  width: 50%;
  padding: 12px 20px;
  margin: 8px 16px;
  margin-top:8px;
  display: inline-block;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

input[type=submit] {
  width: 15%;
  background-color: #4CAF50;
  color: white;
  padding: 14px 10px;
  margin: 2px 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

input[type=submit]:hover {
  background-color: #45a049;
}
.selectBtn, .deselectBtn, .submitBtn {
    background-color: green;
    color: white;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
}
.customBtn {
    background-color: blue;
    color: white;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
}

.selectBtn:hover, .deselectBtn:hover, .submitBtn:hover {
    background-color: darkgreen;
}

.componentTable {
    border-collapse: collapse;
    width: 60%;
    max-width: 400px;
}

.orgIdTable {
    border-collapse: collapse;
    width: 80%;
    max-width: 600px;
}

th, td {
    border: 1px solid #dddddd;
    text-align: left;
    padding: 8px;
    max-width: 150px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.input-field-long {
    width: 100%;
    border: 1px solid #dddddd;
    padding: 8px;
    font-size: 16px; /* Adjust the font size as needed */
}

.radio-group {
    display: flex;
    align-items: center;
    gap: 10px; /* Adjust the gap between radio buttons */
}
</style>
</body>
</html>