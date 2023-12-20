<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Sandbox Data Migration Console</title>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
<h1>Sandbox Data Migration Console</h1>
<table>
    <tr>
        <td class="table-head"><h4>Action</h4></td>
        <td>
            <label class="radio-text">
                <input class="radio-label" type="radio" name="createOrInstallAction" id="createPackageAction" value="create" checked="checked"> Create
            </label>
            <label class="radio-text">
                <input class="radio-label" type="radio" name="createOrInstallAction" id="installPackageAction" value="install"> Install
            </label>
        </td>
    </tr>
    <tr id="sourceOrgIdRow">
        <td class="table-head"><h4>Source OrgId</h4></td>
        <td><input type="text" id="sourceOrgId" required class="input-field"></td>
    </tr>
    <tr id="targetOrgIdRow">
        <td class="table-head"><h4>Target OrgId</h4></td>
        <td><input type="text" id="targetOrgId" required class="input-field"></td>
    </tr>
    <tr id="transactionTimeoutIdRow">
        <td class="table-head"><h4>Transaction TimeOut</h4></td>
        <td><input type="text" id="transactionTimeout" class="input-field"/></td>
    </tr>
    <tr id="packageTypeRowId">
        <td class="table-head"><h4>Package Type</h4></td>
        <td>
            <label class="radio-text">
                <input class="radio-label" type="radio" name="packageType" id="fullPackageType" value="fullPackage"> Full
            </label>
            <label class="radio-text">
                <input class="radio-label" type="radio" name="packageType" id="partialPackageType" value="partialPackage" checked="checked"> Partial
            </label>
        </td>
    </tr>
    <tr id="dataPackageFileUrlRow">
        <td class="table-head"><h4>Data Package File URL</h4></td>
        <td><input type="text" id="dataPackageFileUrl" required class="input-field"></td>
    </tr>
    <tr id="dataMigrationModulesIdRow">
        <td class="table-head"><h4>Create Package (with Dependant Modules)</h4></td>
        <td>
            <textarea id="dataMigrationModules" placeholder="asset, workorder" rows="5" cols="30"></textarea>
        </td>
    </tr>
    <tr id="dataMigrationForOnlyModulesIdRow">
        <td class="table-head"><h4>Create Package (only for Modules)</h4></td>
        <td>
            <textarea id="dataMigrationForOnlyMentionedModules" placeholder="asset, workorder" rows="5" cols="30"></textarea>
        </td>
    </tr>
    <tr id="limitRow">
        <td class="table-head"><h4>Record Limit</h4></td>
        <td><input type="text" id="limit" required class="input-field"></td>
    </tr>
    <tr id="offsetRow">
            <td class="table-head"><h4>Record Offset</h4></td>
            <td><input type="text" id="offset" required class="input-field"></td>
        </tr>
    <tr id="dataMigrationIdRow">
        <td class="table-head"><h4>Data Migration Id</h4></td>
        <td><input type="text" id="dataMigrationId" class="input-field"/></td>
    </tr>
    <tr id="packageIdRow">
        <td class="table-head"><h4>Meta Package Id</h4></td>
        <td><input type="text" id="packageId" required class="input-field"></td>
    </tr>
    <tr id="bucketNameRow">
        <td class="table-head"><h4>Sandbox BucketName</h4></td>
        <td><input type="text" id="bucketName" required class="input-field"></td>
    </tr>
    <tr id="moduleSequenceRow">
        <td class="table-head"><h4>Module Sequence</h4></td>
        <td>
            <textarea id="moduleSequence" placeholder="asset, workorder" rows="5" cols="30"></textarea>
        </td>
    </tr>
    <tr id="skipDataMigrationModulesIdRow">
        <td class="table-head"><h4>Skip Modules</h4></td>
        <td>
            <textarea id="skipDataMigrationModules" placeholder="asset, workorder" rows="5" cols="30"></textarea>
        </td>
    </tr>
    <tr id="dataMigrationLogModulesIdRow">
        <td class="table-head"><h4>Log Modules</h4></td>
        <td>
            <textarea id="dataMigrationLogModules" placeholder="asset, workorder" rows="5" cols="30"></textarea>
        </td>
    </tr>
</table>

<div>
    <button type="submit" id="submitBtn" onclick="sendAjax()" class="submitBtn">Submit</button>
</div>

<div id="result"></div>

<script>
    $(window).on('load', function () {
        hideOrShowFields();
        $('input[name="packageType"]').change(hideOrShowFields);
        $('input[name="createOrInstallAction"]').change(hideOrShowFields);
    });

    function hideOrShowFields() {
        const isPackageCreationAction = $("#createPackageAction").is(":checked");
        const partialPackageType = $("#partialPackageType").is(":checked");
        $("#packageTypeRowId").toggle(isPackageCreationAction);
        $("#limitRow").toggle(isPackageCreationAction && partialPackageType);
        $("#offsetRow").toggle(isPackageCreationAction && partialPackageType);
        $("#dataMigrationModulesIdRow").toggle(isPackageCreationAction && partialPackageType);
        $("#dataMigrationForOnlyModulesIdRow").toggle(isPackageCreationAction && partialPackageType);

        $("#packageIdRow").toggle(!isPackageCreationAction);
        $("#bucketNameRow").toggle(!isPackageCreationAction);
        $("#targetOrgIdRow").toggle(!isPackageCreationAction);
        $("#dataPackageFileUrlRow").toggle(!isPackageCreationAction);
        $("#dataMigrationIdRow").toggle(!isPackageCreationAction);
        $("#moduleSequenceRow").toggle(!isPackageCreationAction);
        $("#dataMigrationLogModulesIdRow").toggle(!isPackageCreationAction);
        $("#skipDataMigrationModulesIdRow").toggle(!partialPackageType);
    }

    function sendAjax() {
        const fullPackageType = $("#fullPackageType").is(":checked");
        const isPackageCreationAction = $("#createPackageAction").is(":checked");

        if (isPackageCreationAction) {
            const dataObject = {
                fromAdminTool: true,
                limit: $("#limit").val(),
                offset: $("#offset").val(),
                fullPackageType: fullPackageType,
                bucketName: $("#bucketName").val(),
                sourceOrgId: $("#sourceOrgId").val(),
                transactionTimeout: $('#transactionTimeout').val(),
                dataMigrationModules: $("#dataMigrationModules").val(),
                skipDataMigrationModules : $('#skipDataMigrationModules').val(),
                dataMigrationForOnlyMentionedModules : $('#dataMigrationForOnlyMentionedModules').val(),
            };

            $.ajax({
                url: "/admin/createSandboxDataPackage",
                type: "POST",
                dataType: "json",
                data: dataObject,
                cache: false,
                success: function (data) {
                    $("#result").html(data.result);
                },
                error: function (xhr, status, error) {
                    console.log("Error: " + xhr.responseText);
                    $("#result").html(xhr.responseText);
                }
            });
        } else {
            const dataObject = {
                fromAdminTool: true,
                limit: $("#limit").val(),
                packageId: $("#packageId").val(),
                sourceOrgId: $("#sourceOrgId").val(),
                targetOrgId: $("#targetOrgId").val(),
                packageFileURL: $("#dataPackageFileUrl").val(),
                moduleSequence: $("#moduleSequence").val(),
                dataMigrationId: $("#dataMigrationId").val(),
                transactionTimeout: $('#transactionTimeout').val(),
                dataMigrationModules: $("#dataMigrationModules").val(),
                skipDataMigrationModules : $('#skipDataMigrationModules').val(),
            };

            $.ajax({
                url: "/admin/installSandboxDataPackage",
                type: "POST",
                dataType: "json",
                data: dataObject,
                cache: false,
                success: function (data) {
                    $("#result").html(data.result);
                },
                error: function (xhr, status, error) {
                    console.log("Error: " + xhr.responseText);
                    $("#result").html(xhr.responseText);
                }
            });
        }
        $('#submitBtn').attr("disabled", true);
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

    .table-head {
        font-weight: bold;
        width: 200px;
    }

    .radio-label {
        width: 15px;
        height: 15px;
        margin-right: 5px !important;
        margin-left: 10px !important;
    }

    .radio-text {
        font-size: 20px;
        font-weight: bold;
    }

    ul.components-list {
        flex-direction: column;
        list-style-type: disc;
        padding-left: 15px;
        font-weight: bold;
        font-size: 14px;
        display: flex;
        gap: 12px;
    }

    .input-field {
        flex: 2;
        padding: 5px;
        height: 30px;
        width: 200px;
        border-radius: 5px;
        border: 1px solid #000;
    }

    tr td {
        padding: 10px;
    }
</style>
</body>
</html>
