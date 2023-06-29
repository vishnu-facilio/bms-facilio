<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Feature Lock Console</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
<h1>Meta Migration Console</h1>
<table>
    <tr>
        <td class="table-head"><h4>Source OrgId</h4></td>
        <td><input type="text" id="sourceOrgId" required class="input-field"/></td>
    </tr>
    <tr>
        <td class="table-head"><h4>Target OrgId</h4></td>
        <td><input type="text" id="targetOrgId" required class="input-field"/></td>
    </tr>
</table>

<button type="submit" class='submitBtn' id="submitBtn" onclick="sendAjax()">Submit</button>

<div id="result"></div>

<script>
    function sendAjax() {
        const dataObject = {};
        dataObject.sourceOrgId = $('#sourceOrgId').val();
        dataObject.targetOrgId = $('#targetOrgId').val();
        dataObject.fromAdminTool = true;

        $.ajax({
            url: "/admin/createAndInstallPackage",
            type: "POST",
            dataType: "json",
            data: dataObject,
            success: function(data) {
                $("#result").html(data.result);
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
        width : 200px;
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
