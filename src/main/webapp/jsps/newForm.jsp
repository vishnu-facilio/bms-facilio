<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<div class="row">
	<div class="col-lg-12">
		<h1 class="page-header">New <s:property value="%{moduleName}" /></h1>
	</div>
	<!-- /.col-lg-12 -->
</div>
<form role="form" id="addForm" onsubmit="return false;">
	<div class="row">
		<s:iterator value="formlayout" status="rowstatus" var="panel">
		<div class='<s:if test="%{#panel.display == @com.facilio.bmsconsole.context.Panel$Type@HALF }">col-lg-6</s:if><s:else>col-lg-12</s:else>'>
			<div class="panel-body">
				<s:iterator value="#panel" status="rowstatus" var="field">
					<div class="form-group">
						<label><s:property value="#field.label"/></label>
						<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@TEXTBOX  || #field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@DATETIME }">
							<s:textfield  name="%{#field.name}"
							id="%{#field.id}" class="form-control"
							placeholder="xyz@example.com" type="%{#field.html5Type}" disabled="%{#field.isDisabled}" />
						</s:if>
						<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@SELECTBOX }">
							<s:select class="form-control" list="actionForm[#field.list]"
							name="%{#field.name}" id="%{#field.id}" headerKey="0"
							headerValue="--" />		
						</s:if>		
						<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@TEXTAREA }">
							<s:textarea class="form-control" name="%{#field.name}"
							label="%{#field.id}" rows="3"
							placeholder="More about the problem..." />
						</s:if>	
					</div>
				</s:iterator>
		 	</div>
	  	</div>
	</s:iterator>
		<div class="col-lg-12">
			<div class="panel-body">
				<s:iterator var="customFieldName" value="customFieldNames">
					<div class="form-group">
						<label><s:property value="customFieldName" /></label>
						<s:textfield name="ticket.customProps['%{customFieldName}']" id="input%{customFieldName}" class="form-control" />
					</div>
				</s:iterator>
				<div class="form-group">
					<button type="reset" class="btn btn-outline btn-default"
						onclick="location.href='#<s:property value="%{moduleLinkName}" />';">Go Back</button>
					<button type="submit" class="btn btn-outline btn-primary">Save</button>
				</div>
			</div>
		</div>
	</div>
</form>
<script>
	var moduleLinkName = '<s:property value="%{moduleLinkName}" />';
	$(document).ready(function() {
		$("#addForm").submit(function() {
			$.ajax({
				method : "post",
				url : "<s:url action='add' />",
				data : $("#addForm").serialize()
			})
			.done(function(data) {
				console.log(data);
				$.each(data, function(key, val) {
					window.location.href='#'+moduleLinkName+'/'+val;
				});
			})
			.fail(function(error) {
				console.log(error);
			});
		});
	});
</script>