<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Feature Lock Console</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
    <h1>Feature Lock Console</h1>
    <table>
        <tr>
            <td class="table-head"><h4>OrgId</h4></td>
            <td><input type="text" id="orgId" required class="input-field"/></td>
        </tr>
        <tr>
            <td class="table-head"><h4>Component</h4></td>
            <td>
                <select class ="dropdown" id="feature">
                  <option value="1">Forms</option>
                  <option value="2">Fields</option>
                  <option value="3">Workflow</option>
                  <option value="4">Stateflow</option>
                  <option value="5">Custom Button</option>
                </select>

            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>IDs List</h4></td>
            <td>
                <textarea id="recordIds" placeholder="{123,456,789}" rows="10" cols="30"></textarea>
            </td>
        </tr>
        <tr>
            <td class="table-head"><h4>Action</h4></td>
            <td>
              <label class="radio-text">
                <input class="radio-label" type="radio" name="action" id="lock" value="lock" checked> Lock
              </label>
              <label class="radio-text">
                <input class="radio-label" type="radio" name="action" id="unlock" value="unlock"> UnLock
              </label>
            </td>
        </tr>
    </table>

    <button type="submit" class='submitBtn' id="submitBtn" onclick="sendAjax()">Submit</button>

    <div id="result"></div>

    <script>
        function sendAjax() {
            // get inputs
            var dataObject = new Object();
            dataObject.orgId = $('#orgId').val();
            dataObject.feature = $('#feature').val();
            dataObject.ids = $('#recordIds').val().trim();
            dataObject.locked = (document.getElementById("lock").checked) ? true : false;

            $.ajax({
                url: "/admin/updateFeatureLock",
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

        .table-head {
          font-weight: bold;
          width : 200px;
        }

        .input-field {
          flex: 2;
          padding: 5px;
          height: 30px;
          width: 200px;
          border-radius: 5px;
          border: 1px solid #000;
        }

        .dropdown {
        height: 30px;
        width: 200px;
        }

        tr td {
          padding: 10px;
        }
    </style>
</body>
</html>
