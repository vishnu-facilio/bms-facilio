<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Multi Currency Field Migration</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
<h1>Multi Currency Field Migration</h1>
<br/>
<table>
    <td>
        <label class="radio-text">
            <input class="radio-label" type="radio" name="fileVisibility" id="showFileRadio" value="migrateIndividually" checked="checked"> Migrate Individually
        </label>
        <label class="radio-text">
            <input class="radio-label" type="radio" name="fileVisibility" id="hideFileRadio" value="migrateByFileUpload"> Migrate via File Upload
        </label>
    </td>
    <tr id="orgIdRow">
        <td><h4>Org Id : </h4></td>
        <td><label for="orgId"></label><input type="number" id="orgId"/></td>
    </tr>
    <tr id="moduleNameRow">
        <td><h4>Module Name : </h4></td>
        <td><label for="moduleName"></label><input type="text" id="moduleName"/></td>
    </tr>
    <tr id="fieldNameRow">
        <td><h4>Field Name : </h4></td>
        <td><label for="fieldName"></label><input type="text" id="fieldName"/></td>
    </tr>
    <tr  id="baseCurrencyValueColumnNameRow">
        <td><h4>Base Currency Value Column Name : </h4></td>
        <td><label for="baseCurrencyValueColumnName"></label><input type="text" id="baseCurrencyValueColumnName"/></td>
    </tr>
    <tr id="revertRow">
        <td><h4>Revert</h4></td>
        <td>
            <label>
                <input type="checkbox" name="revert" id="revert" value="lock"/>
            </label>
        </td>
    </tr>
    <tr id="fileRow">
        <td class="table-head"><h4>File Upload</h4></td>
        <td>
            <input type="file" id="fileInput" />
        </td>
    </tr>
    <tr hidden="hidden">
        <td><h4>Transaction Time Out</h4></td>
        <td>
            <label>
                <input type="number" id="transactionTimeOut"/>
            </label>
        </td>
    </tr>
</table>
<button type="submit" class='submitBtn' id="submitBtn" onclick="sendAjax()">Submit</button>
<br>
<div id="spinner"></div>
<br>
<div id="result"></div>
<script>

    $(window).on('load', function() {
        hideOrShowFields();
        $('input[name="fileVisibility"]').change(hideOrShowFields);
    });

    function hideOrShowFields() {
        const showFileRadioChecked = $("#showFileRadio").is(":checked");
        $("#moduleNameRow").toggle(showFileRadioChecked);
        $("#fieldNameRow").toggle(showFileRadioChecked);
        $("#baseCurrencyValueColumnNameRow").toggle(showFileRadioChecked);
        $("#fileRow").toggle(!showFileRadioChecked);
    }

    function sendAjax(){
        const showFileRadioChecked = $("#showFileRadio").is(":checked");
        if (showFileRadioChecked) {
            let dataObject = {};
            dataObject.orgId = $('#orgId').val();
            dataObject.moduleName = $('#moduleName').val();
            dataObject.fieldName = $('#fieldName').val();
            dataObject.baseCurrencyValueColumnName = $('#baseCurrencyValueColumnName').val();
            dataObject.revert = !!document.getElementById("revert").checked;
            dataObject.transactionTimeOut = $('#transactionTimeOut').val();
            dataObject.migrateViaFile = showFileRadioChecked
            $('#spinner').html('<i class="fa fa-spinner fa-spin" style="height: 20px; width: 20px"></i>');
            $.ajax({
                url: "/admin/migrateMultiCurrencyField",
                type: "POST",
                dataType: "json",
                data: dataObject,
                success: function(data) {
                    $("#result").html(data.result);
                    $('#spinner').html("");
                },
                error: function(xhr, status, error) {
                    console.log("Error: " + xhr.responseText + " status :" + status + " error : " + error);
                    $("#result").html(xhr.responseText, status, error);
                    $('#spinner').html("");
                }
            })
        } else {
            const fileInput = document.getElementById('fileInput');
            const file = fileInput.files[0];
            const formData = new FormData();
            formData.append('file', file);
            formData.append('orgId', $('#orgId').val());
            formData.append('revert', !!document.getElementById("revert").checked);
            formData.append('migrateViaFile', showFileRadioChecked)
            formData.append('transactionTimeOut', $('#transactionTimeOut').val());
            $('#spinner').html('<i class="fa fa-spinner fa-spin" style="height: 20px; width: 20px"></i>');
            $.ajax({
                url: "/admin/migrateMultiCurrencyField",
                type: "POST",
                dataType: "json",
                data: formData,
                processData: false,
                contentType: false,
                enctype: 'multipart/form-data',
                cache: false,
                success: function(data) {
                    $("#result").html(data.result);
                    $('#spinner').html("");
                },
                error: function(xhr, status, error) {
                    console.log("Error: " + xhr.responseText + " status :" + status + " error : " + error);
                    $("#result").html(xhr.responseText, status, error);
                    $('#spinner').html("");
                }
            })
        }

    }
</script>
</body>
</html>
