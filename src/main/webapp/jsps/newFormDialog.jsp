<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <h4 class="modal-title pull-left" style="line-height: 35px;" id="myModalLabel">New <s:property value="%{moduleName}"/></h4>
            <div class="action-btn text-right">
				<button type="button" class="btn btn-default btn-circle cancel-btn"
					data-dismiss="modal">
					<i class="fa fa-times"></i>
				</button>
				<button type="button" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" class="btn btn-default save-btn" onclick="$('#addFormDialog').submit();" class="btn btn-default save-btn">
					<i class="fa fa-check" aria-hidden="true"></i>&nbsp;&nbsp;Done
				</button>
			</div>
        </div>
        <div class="modal-body form-content">
        	<form role="form" id="addFormDialog" data-toggle="validator">
        		
        		<%-- Include form layout generator jsp --%>
        		<%@include file="formLayout.jsp" %>
        		
			</form>
        </div>
   </div>
</div>
<script>
	$(document).ready(function() {
		
		$("#addFormDialog input:text").first().focus();
		
		$('.form-content').tooltip({
	        selector: "[data-toggle=tooltip]",
	        container: "body"
	    });
		
		$('select').selectize();
		
		$(".f-datetime").datetimepicker();
		
		$(".f-datetime").closest('div').find('.btn-lookup').click(function() {
			$(".f-datetime").data("DateTimePicker").toggle();
		});
		
		$(".f-date").datetimepicker({
			format:'DD/MM/YYYY'
		});
		$(".f-date").closest('div').find('.btn-lookup').click(function() {
			$(".f-date").data("DateTimePicker").toggle();
		});
		
		$("input[type=file].file-simple").change(function() {
			var input = $(this),
	        numFiles = input.get(0).files ? input.get(0).files.length : 1,
	        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
	    	
	    	var input = $(this).parents('.input-group').find(':text'),
            log = numFiles > 1 ? numFiles + ' files selected' : label;

	        if( input.length ) {
	            input.val(log);
	        }
		});
	});
</script>