<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action = "deleteMessageQueue">
    <label for="tableName">
			<div class="admin-data-grey">JobQueue :</div>
				</label>
				<select class="admin-data-select"
					name="tableName" id="tableName">
					<option value="" disabled selected>Select</option>
					<option value="FacilioExceptionQueue">FacilioExceptionQueue</option>
					<option value="FacilioInstantJobQueue">FacilioInstantJobQueue</option>
				</select>
				<br>
				
	<label for="days"><div class="admin-data-grey">DeletedDays :</div></label>
    <input type="text" id="days" name="days">
    <br><br>
	<div align="center">
	<input type = "submit" style="margin-left: -550px" name="deleteMessageQueue"  value = "Submit"/> 
    </div>


</form>
</body>
<style>
.admin-data-grey{
	color: #333;
	font-size: 17px;
	letter-spacing: 0.5px;
	font-weight: 400;
}
.admin-data-select {
	font-size: 14px;
	color: #333;
	background: #fff;
	padding: 12px 15px;
	margin: 8px 16px;
	margin-top: 5px;
	display: inline-block;
	border: 1px solid #ccc;
	border-radius: 4px;
}
input[type=text]{
  width: 12%;
  padding: 12px 20px;
  margin: 8px 16px;
  margin-top:8px;
  display: inline-block;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}
input[type=submit] {
  width: 13%;
  background-color: #4CAF50;
  color: white;
  padding: 12px 10px;
  margin: 2px 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
 font-size: 15px;
}
</style>
</html>