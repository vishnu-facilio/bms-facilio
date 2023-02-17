<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Data Migration</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
    <h1>Data Migration Details</h1>
	<form action="runDataMigration" method="post">
            <table>
            <tr>
                <td class="table-head"><h4>Source OrgId</h4></td>
                <td><input type="text" name="sourceOrgId" class="input-field"/></td>
            </tr>
            <tr>
                <td class="table-head"><h4>Target OrgId</h4></td>
                <td><input type="text" name="targetOrgId" class="input-field"/></td>
            </tr>
            <tr>
                <td class="table-head"><h4>Data Migration Id</h4></td>
                <td><input type="text" name="dataMigrationId" class="input-field"/></td>
            </tr>
            <tr>
                <td class="table-head"><h4>Site Scoped</h4></td>
                <td>
                    <label class="radio-label">
                        <input type="radio" id="isSiteScoped" name="siteScoped" onclick="showHideTable()" value="true"/> <span class="radio-text"> Yes </span>
                    </label>
                    <label class="radio-label">
                    <input type="radio" id="isSiteScoped" name="siteScoped" onclick="showHideTable()" value="false"/> <span class="radio-text"> No </span>
                    </label>
                </td>
            </tr>
        </table>
        <table id="site-table" style="display:none;">
            <td class="table-head"><h4>Site Mapping</h4></td>
            <div class="row" id="site-div">
                    <td><label for="fromSite">Source</label></td>
                    <td><input type="text" name="fromSiteIds" id="fromSite" class="input-field"></td>
                    <td><label for="toSite">Target </label></td>
                    <td><input type="text" name="toSiteIds" id="toSite" class="input-field"></td>
                    <td><button type="button" disabled onclick="deleteRow('site-table', 'fromSite')" class="delete-row-btn">Delete Row</button></td>
                    <td><button type="button" onclick="addRow('site-table','fromSiteIds','fromSite','toSiteIds','toSite','site-div')" class="add-row-btn"class="add-row-btn">Add Row</button></td>
            </div>
        </table>
        <table id="user-table">
            <tr>
            <td class="table-head"><h4>User Mapping</h4></td>
            <div class="row" id="user-div">
                    <td><label for="fromUser">Source </label></td>
                    <td><input type="text" name="fromUserIds" id="fromUser" class="input-field" ></td>
                    <td><label for="toUser">Target </label></td>
                    <td><input type="text" name="toUserIds" id="toUser" class="input-field"></td>
                    <td><button type="button" disabled onclick="deleteRow('user-table', 'fromUser')" class="delete-row-btn">Delete Row</button></td>
                    <td><button type="button" onclick="addRow('user-table','fromUserIds','fromUser','toUserIds','toUser','user-div')" class="add-row-btn">Add Row</button></td>
            </div>
            </tr>
        </table>
        <br/>
        <table id="group-table">
            <td class="table-head"><h4>Group Mapping</h4></td>
            <div class="row" id="group-div">
                    <td><label for="fromGroup">Source </label></td>
                    <td><input type="text" name="fromGroupIds" id="fromGroup" class="input-field"></td>
                    <td><label for="toGroup">Target </label></td>
                    <td><input type="text" name="toGroupIds" id="toGroup" class="input-field"></td>
                    <td><button type="button" disabled onclick="deleteRow('group-table', 'fromGroup')" class="delete-row-btn">Delete Row</button></td>
                    <td><button type="button" onclick="addRow('group-table','fromGroupIds','fromGroup','toGroupIds','toGroup','group-div')"class="add-row-btn">Add Row</button></td>
            </div>
        </table>
        <br/>
        <table id="role-table">
            <td class="table-head"><h4>Role Mapping</h4></td>
            <div class="row" id="role-div">
                    <td><label for="fromRole">Source </label></td>
                    <td><input type="text" name="fromRoleIds" id="fromRole"  class="input-field"></td>
                    <td><label for="toRole">Target </label></td>
                    <td><input type="text" name="toRoleIds" id="toRole"  class="input-field"></td>
                    <td><button type="button" disabled onclick="deleteRow('role-table', 'fromRole')" class="delete-row-btn">Delete Row</button></td>
                    <td><button type="button" onclick="addRow('role-table','fromRoleIds','fromRole','toRoleIds','toRole','role-div')"class="add-row-btn">Add Row</button></td>
            </div>
        </table>
        <br/>
        <table id="stateFlow-table">
            <td class="table-head"><h4>StateFlow Mapping</h4></td>
            <div class="row" id="stateflow-div">
                    <td><label for="fromStateFlow">Source </label></td>
                    <td><input type="text" name="fromStateFlowIds" id="fromStateFlow" class="input-field"></td>
                    <td><label for="toStateFlow">Target </label></td>
                    <td><input type="text" name="toStateFlowIds" id="toStateFlow" class="input-field"></td>
                    <td><button type="button" disabled onclick="deleteRow('stateFlow-table', 'fromStateFlow')" class="delete-row-btn">Delete Row</button></td>
                    <td><button type="button" onclick="addRow('stateFlow-table','fromStateFlowIds','fromStateFlow','toStateFlowIds','toStateFlow','stateFlow-div')"class="add-row-btn">Add Row</button></td>
            <div>
        </table>
        <br/>
        <table id="sla-table">
            <td class="table-head"><h4>SLA Mapping</h4></td>
            <div class="row" id="sla-div">
                    <td><label for="fromSla">Source </label></td>
                    <td><input type="text" name="fromSlaIds" id="fromSla" class="input-field"></td>
                    <td><label for="toSla">Target </label></td>
                    <td><input type="text" name="toSlaIds" id="toSla" class="input-field"></td>
                    <td><button type="button" disabled onclick="deleteRow('sla-table', 'fromSla')" class="delete-row-btn">Delete Row</button></td>
                    <td><button type="button" onclick="addRow('sla-table','fromSlaIds','fromSla','toSlaIds','toSla','sla-div')"class="add-row-btn">Add Row</button></td>
            </div>
        </table>
        <br/>
        <table id="form-table">
            <td class="table-head"><h4>Form Mapping</h4></td>
            <div class="row" id="form-div">
                    <td><label for="fromForm">Source </label></td>
                    <td><input type="text" name="fromFormIds" id="fromForm" class="input-field"></td>
                    <td><label for="toForm">Target </label></td>
                    <td><input type="text" name="toFormIds" id="toForm" class="input-field"></td>
                    <td><button type="button" disabled onclick="deleteRow('form-table', 'form-div')" id="fromForm" class="delete-row-btn">Delete Row</button></td>
                    <td><button type="button" onclick="addRow('form-table','fromFormIds','fromForm','toFormIds','toForm', 'form-div')"class="add-row-btn">Add Row</button></td>
            </div>
        </table>
        <br>
        <button type="submit" class='submitBtn'>Submit</button>
    </form>

    <script>
        var i = 1;
        function addRow(tableId, fromFieldName, fromFieldId, toFieldName, toFieldId, tableDivId) {
            tableDivId = tableDivId + i;
            var table = document.getElementById(tableId);
            var row = table.insertRow();
            row.setAttribute("id", tableDivId);
            var cell0 = row.insertCell(0);
            var cell1 = row.insertCell(1);
            var cell2 = row.insertCell(2);
            var cell3 = row.insertCell(3);
            var cell4 = row.insertCell(4);
            var cell5 = row.insertCell(5);
            cell1.innerHTML = "<label for='fromForm'>Source </label>"
            cell2.innerHTML = "<input type='text' name='" + fromFieldName + "' id='" + fromFieldId + "' class='input-field'>";
            cell3.innerHTML = "<label for='toSla'>Target </label>"
            cell4.innerHTML = "<input type='text' name='" + toFieldName + "' id='" + toFieldId + "' class='input-field'>";
            var deleteFunction = "deleteRow('" + tableId + "','" + tableDivId +"')";
            cell5.innerHTML = "<button type='button' onclick="+ deleteFunction + " class='delete-row-btn'>Delete Row</button>";
            i++;
        }

        function deleteRow(tableId, rowId) {
            var row = document.getElementById(rowId);
            if (row) {
                row.parentNode.removeChild(row);
            }
        }

        function showHideTable() {
            var table = document.getElementById("site-table");
            var radio = document.getElementById("isSiteScoped");
            if (radio.checked == true) {
                table.style.display = "table";
            } else {
                table.style.display = "none";
            }
        }
    </script>

    <style>
        .radio-label input[type="radio"] {
          width: 15px;
          height: 15px;
          margin-right: 5px;
          margin-left: 10px;
        }

        .radio-label .radio-text {
            font-size: 20px;
            font-weight: bold;
        }

        .submitBtn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            align-items: center;
        }

        input[type=submit]:hover {
            background-color: #45a049;
        }

        .table-head {
          font-weight: bold;
          width : 200px;
        }

        .table-div {
          font-size: 13px;
          padding: 15px 20px !important;
        }

        .row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 10px;
          padding-left: 30px;
        }

        .input-field {
          flex: 2; /* distribute remaining space equally among input fields */
          padding: 5px;
          border-radius: 5px;
          border: 1px solid #000;
        }

        .add-row-btn, .delete-row-btn {
          padding: 5px 10px;
          margin-left: 10px;
          border-radius: 5px;
          background-color: #e1e9f5;
          color: #000;
          border: 1px solid black;
          cursor: pointer;
        }

        .add-row-btn:hover, .delete-row-btn:hover {
          background-color: #0062cc;
        }

        tr td {
          padding: 10px;
        }
    </style>
</body>
</html>