<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<div style="width : 100%; height : 100%; margin : 0; padding: 0; overflow : auto;">
	<h3>All Tickets</h3>
	<s:iterator value="tickets">
		<%-- <a href="tickets/<s:property value='ticketId' />"
			class="list-group-item"> <span class="badge">14</span>
			<h4 class="list-group-item-heading">
				<s:property value="subject" />
			</h4>
			<p class="list-group-item-text">
				<s:property value="description" />
			</p>
		</a> --%>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title"><s:property value="subject" /><span style="float : right;" class="badge text-right">14</span></h3>
			</div>
			<a href="<s:property value='ticketId' />" class="list-group-item">
				<div class="panel-body ">
						<s:property value="description" />
				</div>
			</a>
		</div>
	</s:iterator>
</div>	