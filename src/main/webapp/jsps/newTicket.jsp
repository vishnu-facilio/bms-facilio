<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">

<div class="row form-header" >
<div class="col-sm-12" >
  <h4 class="pull-left">New Work Order</h4>
    <div class="action-btn text-right">
    	<button type="button" class="btn btn-default btn-circle cancel-btn" onclick="location.href='#tickets';"><i class="fa fa-times"></i></button>
 		<button type="button" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" class="btn btn-default save-btn" onclick="$('#addTicketForm').submit();"><i class="fa fa-check" aria-hidden="true"></i>&nbsp;&nbsp;Save</button>
	 </div>
  </div>
</div>
<div class="row form-content">
<div class="col-lg-12">
<form role="form" id="addTicketForm" onsubmit="return false;">

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
						placeholder="xyz@example.com" type="%{#field.html5Type}" />
						</s:if>
			<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@SELECTBOX }">
					
	<s:select class="form-control" list="actionForm[#field.list]"
						name="%{#field.name}" id="%{#field.id}" headerKey="0"
						headerValue="--" />					
						
						</s:if>		
							<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@TEXTAREA }">
							<s:textarea class="form-control" name="%{#field.name}"
							 rows="5"
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
							<s:textfield name="ticket.customProps['%{customFieldName}']"
								id="input%{customFieldName}" class="form-control" />
						</div>
					</s:iterator>
				</div>
			</div>

		
	</div>
	<div class="form-group col-lg-12 col-md-12">
	<label>Tasks</label>
	<div class="col-lg-12 col-md-12 related-list">
		<div class="text-left pull-down">
			<a href="javascript:void(0);">
				<button type="button" class="btn btn-default btn-circle-sm" style="background-color:#50CA7C;"><i class="fa fa-1x fa-plus" style="color:white;" aria-hidden="true"></i></button>
	 	&nbsp;Add New Task
			</a>
	 	</div>
	</div>
</div>

<div class="form-group col-lg-12 col-md-12">
	<label>Attachments</label>
	<div class="col-lg-12 col-md-12 related-list">
		<div class="text-left pull-down">
			<a href="javascript:void(0);">
				<button type="button" class="btn btn-default btn-circle-sm" style="background-color:#50CA7C;"><i class="fa fa-1x fa-plus" style="color:white;" aria-hidden="true"></i></button>
	 	&nbsp;Add Attachment
			</a>
	 	</div>
	</div>
</div>
</form>
</div>
</div>
<script>
	$(document).ready(function() {
		$("#addTicketForm").submit(function() {
			console.log("test");
			$(".save-btn").button('loading');
			$.ajax({
				method : "post",
				url : "<s:url action='add' />",
				data : $("#addTicketForm").serialize()
			})
			.done(function(data) {
				console.log(data);
				window.location.href='#tickets/'+data.ticketId;
			})
			.fail(function(error) {
				$(".save-btn").button('reset');
				console.log(error);
			});
		});
	});
</script>
