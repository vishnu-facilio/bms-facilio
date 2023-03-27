<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Data Migration</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
    <h1>Data Migration Details</h1>
    <table>
        <tr>
            <td class="table-head"><h4>Source OrgId</h4></td>
            <td><input type="text" id="sourceOrgId" required class="input-field"/></td>
        </tr>
        <tr>
            <td class="table-head"><h4>Target OrgId</h4></td>
            <td><input type="text" id="targetOrgId" required class="input-field"/></td>
        </tr>
        <tr>
            <td class="table-head"><h4>Data Migration Id</h4></td>
            <td><input type="text" id="dataMigrationId" class="input-field"/></td>
        </tr>
        <tr>
            <td class="table-head"><h4>Transaction TimeOut</h4></td>
            <td><input type="text" id="transactionTimeout" class="input-field"/></td>
        </tr>
        <tr>
            <td class="table-head"><h4>Site Scoped</h4></td>
            <td>
                <input class="radio-label" type="radio" name="siteScoped" id="isSiteScoped" onclick="showHideTable(this)" value="yes"/>
                <label class="radio-text" for="yes">Yes</label>
                <input class="radio-label" type="radio" name="siteScoped" id="isNotSiteScoped" onclick="showHideTable(this)" value="no"/>
                <label class="radio-text" for="no">No</label>
            </td>
        </tr>
        <tr id="site-row" style="display:none;">
            <td class="table-head"><h4>Site Mapping</h4></td>
            <td>
                <textarea id="siteMapping" placeholder="{'1':2, '3':4}}" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>User Mapping</h4></td>
            <td>
                <textarea id="userMapping" placeholder="{'1':2, '3':4}}" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Group Mapping</h4></td>
            <td>
                <textarea id="groupMapping" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Role Mapping</h4></td>
            <td>
                <textarea id="roleMapping" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>StateFlow Mapping</h4></td>
            <td>
                <textarea id="stateFlowMapping" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Sla Mapping</h4></td>
            <td>
                <textarea id="slaMapping" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Form Mapping</h4></td>
            <td>
                <textarea id="formMapping" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Module Sequence</h4></td>
            <td>
                <textarea id="moduleSequence" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Log Module Names</h4></td>
            <td>
                <textarea id="logModuleNames" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Run migration only for modules</h4></td>
            <td>
                <textarea id="runOnlyForModules" placeholder="asset, workorder" rows="5" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Skip Modules</h4></td>
            <td>
                <textarea id="skipModuleNames" placeholder="site, space" rows="5" cols="30"></textarea>
            </td>
        </tr>
    </table>

    <button type="submit" class='submitBtn' id="submitBtn" onclick="sendAjax()">Submit</button>

<div id="result">
</div>
    <script>
        function showHideTable(radio) {
            var row = document.getElementById("site-row");
            if (radio.value == 'yes') {
                row.style.display = "table-row";
            } else {
                row.style.display = "none";
            }
        }

        function addToDataObject(object, paramName, value) {
            if (value.length > 0) {
                object[paramName] = JSON.parse(value);
            }
        }

        function sendAjax() {
            // get inputs
            var dataObject = new Object();
            dataObject.sourceOrgId = $('#sourceOrgId').val();
            dataObject.targetOrgId = $('#targetOrgId').val();
            dataObject.dataMigrationId = $('#dataMigrationId').val();
            dataObject.transactionTimeout = $('#transactionTimeout').val();
            dataObject.siteScoped = (document.getElementById("isSiteScoped").checked) ? true : false;
            if (dataObject.siteScoped) { addToDataObject(dataObject, 'siteIdMapping', $('#siteMapping').val().trim()); }

            addToDataObject(dataObject, 'userIdMapping', $('#userMapping').val().trim());
            addToDataObject(dataObject, 'groupIdMapping', $('#groupMapping').val().trim());
            addToDataObject(dataObject, 'roleIdMapping', $('#roleMapping').val().trim());
            addToDataObject(dataObject, 'stateFlowMapping', $('#stateFlowMapping').val().trim());
            addToDataObject(dataObject, 'slaMapping', $('#slaMapping').val().trim());
            addToDataObject(dataObject, 'formMapping', $('#formMapping').val().trim());

            dataObject.logModuleNames = $('#logModuleNames').val().trim();
            dataObject.moduleSequence = $('#moduleSequence').val().trim();
            dataObject.skipModuleNames = $('#skipModuleNames').val().trim();
            dataObject.runOnlyForModules = $('#runOnlyForModules').val().trim();

            $.ajax({
                url: "/admin/runDataMigration",
                type: "POST",
                dataType: "application",
                data: dataObject,
                success: function(data) {
                        $("#result").html(data);
                    },
                error: function(xhr, status, error) {
                    console.log("Error: " + xhr.responseText);
                    $("#result").html(xhr.responseText);
                }
                });
            $('#submitBtn').attr("disabled", true);
        }

    </script>
    <style>
        .radio-label  {
          width: 15px;
          height: 15px;
          margin-right: 5px !important;
          margin-left: 10px !important;
        }

        .radio-text {
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

        .input-field {
          flex: 2;
          padding: 5px;
          border-radius: 5px;
          border: 1px solid #000;
        }

        tr td {
          padding: 10px;
        }
    </style>
</body>
</html>