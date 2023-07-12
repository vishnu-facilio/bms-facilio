<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Feature Lock Console</title>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
<h1>Copy Customization Console</h1>
<table>
  <tr>
    <td class="table-head"><h4>Action</h4></td>
    <td>
      <label class="radio-text">
        <input class="radio-label" type="radio" name="fileVisibility" id="showFileRadio" value="create" checked="checked"> Create
      </label>
      <label class="radio-text">
        <input class="radio-label" type="radio" name="fileVisibility" id="hideFileRadio" value="install"> Install
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
  <tr id="fileRow">
    <td class="table-head"><h4>File Upload</h4></td>
    <td>
      <input type="file" id="fileInput" />
    </td>
  </tr>
</table>

<div>
  <button type="submit" id="submitBtn" onclick="sendAjax()" class="submitBtn">Submit</button>
  <button id="downloadButton" style="display: none;" class="submitBtn">Download Package</button>
</div>

<div id="result"></div>

<script>
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

  function sendAjax() {
    const showFileRadioChecked = $("#showFileRadio").is(":checked");

    if (showFileRadioChecked) {
      const dataObject = {
        fromAdminTool: true,
        sourceOrgId: $("#sourceOrgId").val()
      };

      $.ajax({
        url: "/admin/createPackage",
        type: "POST",
        dataType: "json",
        data: dataObject,
        success: function(data) {
          const downloadUrl = data.downloadUrl;
          if (downloadUrl) {
            $("#downloadButton").show().click(function() {
              window.location.href = downloadUrl;
            });
          }
        },
        error: function(xhr, status, error) {
          console.log("Error: " + xhr.responseText);
          $("#result").html(xhr.responseText);
        }
      });
    } else {
      const fileInput = document.getElementById('fileInput');
      const file = fileInput.files[0];

      const formData = new FormData();
      formData.append('file', file);
      formData.append('fromAdminTool', true);
      formData.append('targetOrgId', $('#targetOrgId').val());

      $.ajax({
        url: "/admin/installPackage",
        type: "POST",
        dataType: "json",
        data: formData,
        processData: false,
        contentType: false,
        enctype: 'multipart/form-data',
        cache: false,
        success: function(data) {
          $("#result").html(data.result);
        },
        error: function(xhr, status, error) {
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
    width : 200px;
  }

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
