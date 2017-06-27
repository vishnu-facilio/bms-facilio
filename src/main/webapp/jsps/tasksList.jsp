<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<div style="width : 100%; height : 100%; margin : 0; padding: 0; overflow : auto;">
	<h3>All <s:property value='moduleName' /></h3>
	<table class="table table-striped table-hover table-bordered">
		<thead>
			<tr>
				<th>ID</th>
				<th>Subject</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="tasks">
				<tr>
					<td><s:property value="taskId" /></td>
					<td><a href="<s:property value='taskId' />"><s:property value="subject" /></a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>