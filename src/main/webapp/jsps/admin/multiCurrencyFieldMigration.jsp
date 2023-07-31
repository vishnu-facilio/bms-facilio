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
    <tr>
        <td><h4>Org Id : </h4></td>
        <td><label for="orgId"></label><input type="number" id="orgId"/></td>
    </tr>
    <tr>
        <td><h4>Module Name : </h4></td>
        <td><label for="moduleName"></label><input type="text" id="moduleName"/></td>
    </tr>
    <tr>
        <td><h4>Field Name : </h4></td>
        <td><label for="fieldName"></label><input type="text" id="fieldName"/></td>
    </tr>
    <tr>
        <td><h4>Base Currency Value Column Name : </h4></td>
        <td><label for="baseCurrencyValueColumnName"></label><input type="text" id="baseCurrencyValueColumnName"/></td>
    </tr>
    <tr>
        <td><h4>Revert</h4></td>
        <td>
            <label>
                <input type="checkbox" name="revert" id="revert" value="lock"/>
            </label>
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
    function sendAjax(){
        let dataObject = {};
        dataObject.orgId = $('#orgId').val();
        dataObject.moduleName = $('#moduleName').val();
        dataObject.fieldName = $('#fieldName').val();
        dataObject.baseCurrencyValueColumnName = $('#baseCurrencyValueColumnName').val();
        dataObject.revert = !!document.getElementById("revert").checked;
        dataObject.transactionTimeOut = $('#transactionTimeOut').val();
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
    }
</script>
</body>
</html>
