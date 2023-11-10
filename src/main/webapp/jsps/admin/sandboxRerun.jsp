<%@ page import="com.facilio.sandbox.utils.SandboxUtil" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%
    Map<Long, String> componentTypeMap = SandboxUtil.getComponentTypeMap();
    List<Integer> skipComponents = new ArrayList<>(); // Initialize an empty list
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Feature Lock Console</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <style>
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

    </style>
</head>
<body>
<h1>SandBox Re-Run Console</h1>
<form id="componentForm">
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
    <button type="button" class='submitBtn' id="submitBtn" onclick="rerunAction()">Rerun-Sandbox</button>
    <button type="button" class='customBtn' id="customBtn" onclick="installAction()">Do Install From Last Created Package</button>
</form>

<script>
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

    function enableSubmitButton() {
        const canSubmit = checkInputFields();
        $('#submitBtn').prop("disabled", !canSubmit);
    }

    function rerunAction() {
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
            contentType: "application/json", // Set content type to JSON
            dataType: "json",
            data: jsonData, // Send JSON payload
            success: function(data) {
                alert("Message : " + data.message);
            },
            error: function(xhr, status, error) {
                console.log("Error: " + xhr.responseText);
                alert("Error occurred for Sandbox Rerun Action: " + xhr.responseText);
            }
        });

        $('#submitBtn').prop("disabled", true);
    }
    function installAction() {
        // Check if the input fields are filled
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
    $('#sourceOrgId, #domainName').on('input', enableSubmitButton);
</script>
</body>
</html>
