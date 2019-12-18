<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import ="com.facilio.bmsconsole.actions.AdminAction" %>   
<%@page import=" com.amazonaws.services.ec2.model.Instance" %>
<%@page import=" com.amazonaws.services.ec2.model.Tag" %>
<%@page import="java.util.List"%> 
<%

List<Instance> instanceInfo= AdminAction.getAwsInstance();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
 <table class="table admin-data-border table-bordered  a">
		<tr>
			<th>S.No</th>
			<th>Instance Name</th>
			<th>Instance Id</th>
			<th>Instance State Name</th>
		</tr>
		<%
 int i=0;
 for(Instance instance : instanceInfo) {
    i++;
 %>
			
		<tr>
			<td align="center"><%= i%></td>
			<%
			List<Tag> tag = instance.getTags();
			String tagName="";
			for(Tag insTag : tag){
				tagName = insTag.getValue();
				break;
			}
			%>
			<td align="center"><%=tagName%></td>
			<td align="center"><%=instance.getInstanceId()%></td>
			<td align="center"><%=instance.getState().getName()%></td>

		</tr>
		<%} %>
	</table>
 
 
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

input[type=submit]:hover {
	background-color: #45a049;
}



.content {
	overflow: hidden;
	width: 450px;
	max-height: 75px;
	overflow-wrap: break-word;
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