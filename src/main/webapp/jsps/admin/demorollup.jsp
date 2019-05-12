<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
  <form action="demoRollUp">
    <label for="orgId"><h4>Enter  OrgId:</h4></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="text" id="orgId" name="orgId" placeholder="Enter valid orgid.."><br>

    <label for="durations"><h4>Enter ExecutionDays:</h4></label>
    <input type="text" id="durations" name="durations" placeholder="Enter the days.."><br><br>
	<div align="center">
	<input type = "submit" style="margin-left: -400px" name="demoRollUp"  value = "Submit"/> 
    </div>
  </form>

</body>
<style>
input[type=text]{
  width: 30%;
  padding: 12px 20px;
  margin: 8px 16px;
  margin-top:8px;
  display: inline-block;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

input[type=submit] {
  width: 20%;
  background-color: #4CAF50;
  color: white;
  padding: 14px 10px;
  margin: 2px 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

input[type=submit]:hover {
  background-color: #45a049;
}

</style>
</html>