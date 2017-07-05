<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<h3>Add Custom Field(s)</h3>
<br />

<s:form id="addCFForm" theme="simple" method="post" class="form-horizontal">
	
	<div class="form-group">
	  		<label class="col-sm-2 control-label">Module Name</label>
		    	<div class="col-sm-10">  
					<s:textfield name="moduleName" class="form-control inputModuleName" placeholder="e.g : Tickets" id="inputModuleName" />
				</div>
		</div>
	
	<div class="form-inline customFieldData firstone">
		
		<div class="form-group">
	  		<label class="col-sm-2 control-label">Field Name</label>
		    	<div class="col-sm-10">  
					<s:textfield name="fieldName" class="form-control inputFieldName" placeholder="Custom Field Name" />
				</div>
		</div>
		
		<div class="form-group">
	  		<label class="col-sm-2 control-label">Data Type</label>
		    	<div class="col-sm-10">  
					<s:select class="form-control inputDataType" list="dataTypeList" name="dataType"/>
				</div>
		</div>
		
		<a class="removeField" href="#"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
	</div>
	
	<div class="form-group" id="buttonsFooter">
		<div class="col-sm-10 text-center">
			<a class="btn btn-default" id="addColumn" href="#"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a> 
			<input class="btn btn-primary" id="addButton" type="button" value="Add Field(s)">
		</div>
	</div>
</s:form>

<style>

.firstone > .removeField {display : none;}

</style>

<script>
	$(document).ready(function() {
		$("#addButton").click(function() {
			var data = [];
			
			$(".customFieldData").each(function(index, value) {
				var cf = {};
				
				cf.fieldName = $(value).find(".inputFieldName").val();
				cf.dataType = $(value).find(".inputDataType").val();
				data.push(cf);
			});
			
			var moduleName = $("#inputModuleName").val();
			
			//console.log(data);
			$.ajax({
				method : "post",
				url : "<s:url action='addCF' />",
				data : {cfData : JSON.stringify(data), moduleName : moduleName}
			})
			.done(function(data) {
				console.log(data);
				FacilioApp.notifyMessage('success', 'Field(s) created successfully!');
				
				setTimeout(function() {
					FacilioApp.refreshView();
	            }, 500);
				//window.location.
			})
			.fail(function(error) {
				alert("Error occurred. Please try again later");
				console.log(error);
			}); 
		});
		
		$("#addColumn").click(function() {
			var $cfFields = $(".firstone").clone().toggleClass("firstone");
			$cfFields.find("input").val("");
			$cfFields.find("select").val($($cfFields.find("select option:first")).val());
			$("#buttonsFooter").before($cfFields);
		});
		
		$("#addCFForm").on("click",".removeField",function(e) {
			e.preventDefault();
			$(this).parent().remove();
		});
	});
</script> 