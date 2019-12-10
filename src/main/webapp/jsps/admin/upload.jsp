<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload Credentials</title>
</head>
<body>
<%= "GOOGLE_APPLICATION_CREDENTIALS" %>
<div class = "admin-data-grey">
 <form action = "http://localhost:8080/admin/addSecretFile" method = "post"
         enctype = "multipart/form-data">
         <input type = "file" name = "file" />
         <input type = "text" name = "fileName"/>
         <input type = "text" name = "contentType"/>
         <br />
         <input type = "submit" value = "Upload File" />
      </form>
</div>
</body>
<style>
input[type=text] {
	width: 20%;
	padding: 12px 20px;
	margin: 8px 16px;
	margin-top: 8px;
	display: inline-block;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
}

table.a {
	table-layout: auto;
	width: 100%;
	font-size: 17px;
}

input[type=submit] {
	width: 10%;
	background-color: #4CAF50;
	color: white;
	padding: 11px 9px;
	margin: 2px 0;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	font-size: 15px;
}

input[type=submit]:hover {
	background-color: #45a049;
}

.long-message {

}

.content {
	overflow: hidden;
	width: 450px;
	max-height: 75px;
	overflow-wrap: break-word;
}

.show-more {

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

.admin-data-container{
    width: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-top: 20px;
}
.admin-data-grey{
	color: #333;
	font-size: 13px;
	letter-spacing: 0.5px;
	font-weight: 400;
}
.admin-data-date{
	font-size: 13px;
	font-weight: 500;
	letter-spacing: 0.5px;
	color: #333;
}

.admin-data-border{
    min-width: 100%;
    border-collapse: collapse;
    display: table;
    border: solid 1px #e6ecf3;
}
.admin-data-border thead{
	display: table-header-group;
    vertical-align: middle;
    border-color: inherit;
}
.admin-data-border th{
	color: #324056;
    font-size: 11px;
    letter-spacing: 1px;
    font-weight: bold;
	white-space: nowrap;
    padding: 23px 30px;
    text-align: left;
    display: table-cell;
    text-transform: uppercase;
}
.admin-data-border tbody{
	display: table-row-group;
    vertical-align: middle;
}

.admin-data-border td{
    color: #333333;
    font-size: 14px;
    border-collapse: separate;
    padding: 15px 30px;
    letter-spacing: 0.6px;
    font-weight: normal;
}

.admin-data-border th, admin-data-border td{
    font-size: 12px;
    color: #333;
    padding: 10px 15px;
    line-height: 20px;
}

</style>

</html>