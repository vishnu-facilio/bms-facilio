<%@page import="com.facilio.accounts.bean.OrgBean"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="java.util.Iterator ,org.json.simple.JSONObject"%>
<%@page import="com.facilio.accounts.dto.Organization ,org.json.simple.JSONObject"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String orgid = request.getParameter("orgid");
    Organization org = null;
    JSONObject result = null;
    if (orgid != null) {
        long orgId = Long.parseLong(orgid);
        OrgBean orgBean = AccountUtil.getOrgBean();
        org = orgBean.getOrg(Long.parseLong(orgid));
        result = AccountUtil.getOrgBean(orgId).orgInfo();
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Feature Lock Console</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
<form action="" method="GET">
    <h2><i class=" fa fa-building-o  fa-fw"></i>Org</h2>
    <div class=" col-lg-8 col-md-8">

        <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
            <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
            <input id="orginfo" type="text" value="<%= org == null ? "" : org.getId() %>" class="form-control" name="orgid" />
        </div>
        <div style="margin-top:30px;">
            <button  id="show" type="submit"  >Submit</button>
            <% if (org != null) {  %>
            <% if(result.size() != 0) {
                Iterator<?> keys = result.keySet().iterator();%>
            <h2> OrgINFO Table Content</h2>
            <table style=" margin-top:40px;" class="table table-bordered">
                <tr>
                    <th style="text-align:center">NAME</th>
                    <th style="text-align:center">VALUES</th>
                </tr>

                <% while( keys.hasNext() ) {
                    String key = (String) keys.next();
                    String value = (String) result.get(key);
                %>
                <tr>
                    <td><%=key %></td>
                    <td><%=value %></td>
                </tr>
                <% } %>
            </table>
            <%}%>
            <% } %>
        </div>
    </div>
</form>
<% if (org != null) {  %>
<div style="max-width: 280px;text-align: left;">
    <div style="margin-bottom: 10px;">
        <label for="name" style="display: block; margin-bottom: 5px;">Name:</label>
        <input type="text" id="name" name="name" required style="width: 100%; padding: 5px;">
    </div>
    <div style="margin-bottom: 10px;">
        <label for="value" style="display: block; margin-bottom: 5px;">Value:</label>
        <input type="text" id="value" name="value" required style="width: 100%; padding: 5px;">
    </div>
    <input type="hidden" id="orgId" name="orgId" value=<%=orgid%>
</div>
<button type="submit" class='submitBtn' id="submitBtn" onclick="sendAjax()" style="padding: 8px 16px; background-color: #4CAF50; color: #fff; border: none; cursor: pointer;">Submit</button>

<div id="result"></div>
<% } %>
<script>
    function sendAjax() {
        const dataObject = {};
        dataObject.name = $('#name').val();
        dataObject.value = $('#value').val();
        dataObject.orgId = $('#orgId').val();
        $.ajax({
            url: "/admin/addOrgInfoData",
            type: "POST",
            dataType: "json",
            data: dataObject,
            success: function(data) {
                localStorage.setItem("successMessage", data.result);
                location.reload();
            },
            error: function(xhr, status, error) {
                console.log("Error: " + xhr.responseText);
                $("#result").html(xhr.responseText);
            }
        });
    }
    $(document).ready(function() {
        var successMessage = localStorage.getItem("successMessage");
        if (successMessage) {
            $("#result").html(successMessage);
            localStorage.removeItem("successMessage");
        }
    });
</script>

</body>
</html>
