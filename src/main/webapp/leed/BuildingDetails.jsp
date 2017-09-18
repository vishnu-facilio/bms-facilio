<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
Welcome to BuildingDetails
<table width="100%" class="table table-striped table-hover" id="building">
	<tbody>
				<tr>
				<td> Building Name : </td>
   				<td><s:property value="building.name" /></td>
   				</tr>
				<tr>
				<td>Campus : </td>
   				<td><s:property value="building.campus" /></td>
   				</tr>
				<tr>
				<td>Current Occupancy : </td>
   				<td><s:property value="building.currentOccupancy" /></td>
   				</tr>
   				<tr>
				<td>Max Occupancy : </td>
   				<td><s:property value="building.maxOccupancy" /></td>
   				</tr>
   				<tr>
				<td>Area : </td>
   				<td><s:property value="building.area" /></td>
   				</tr>
				<tr>
				<td>Location : </td>
   				<td><s:property value="building.location" /></td>
   				</tr>
				<tr>
				<td>Floors : </td>
   				<td><s:property value="building.floors" /></td>
   				</tr>
				<tr>
				<td>UtilityProviders : </td>
   				<td><s:property value="building.utilityProviders[0].name" /></td>
   				</tr>
				
	

	</tbody>
</table>
<div>
<s:iterator var="utilityProvider" value="building.utilityProviders" >
	<s:property value="#utilityProvider.name" />
</s:iterator>
</div>
</body>
</html>