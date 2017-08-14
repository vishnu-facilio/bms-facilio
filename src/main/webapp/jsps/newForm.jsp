<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">

<div class="form-header">
<div class="row">
<div class="col-sm-12" >
  <h4 class="pull-left">New <s:property value="%{moduleName}"/></h4>
    <div class="action-btn text-right">
 		<button type="button" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" class="btn btn-default save-btn" onclick="$('#addForm').submit();"><i class="fa fa-check" aria-hidden="true"></i>&nbsp;&nbsp;Save</button>
	    <button type="button" class="btn btn-default btn-circle cancel-btn" onclick="location.href='#<s:property value="%{moduleLinkName}"/>';"><i class="fa fa-times"></i></button>
	 
	 </div>
  </div>
</div>
</div>
<div class="form-content page-content">
<div class="row">
<div class="col-lg-12">
<form role="form" id="addForm" data-toggle="validator">
	
	<%-- Include form layout generator jsp --%>
	<%@include file="formLayout.jsp" %>
	
</form>
</div>
</div>
</div>

<script>
	$(document).ready(function() {
		
		$("#addForm input:text").first().focus();
		
		$('.form-content').tooltip({
	        selector: "[data-toggle=tooltip]",
	        container: "body"
	    });
		
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
		
		$(".file-add .file-action, .file-add .file-name").click(function(e){
	        e.preventDefault();
	        var fileElm = $(this).parents('.file-add').find(':file');
	        $(fileElm).trigger('click');
	    });
		
		$(".file-add input[type=file]").change(function(e){
			
			var input = $(this);
			
			var fileList = input[0].files;
        	var formData = new FormData();
        	
        	var parent = $(input).parents('.file-section');
        	
        	var elmList = [];
        	
        	var len = fileList.length;
        	for (var i=0; i < len; i++) {
        		var file = fileList[i];
        	
        		// adding attachment row in UI
	        	var duplicate = $('.file-row-template').clone();
	        	duplicate.removeClass('file-row-template');
	        	duplicate.removeClass('hidden');
	        	duplicate.find('.file-name').text(file.name);
	        	
	        	var curIndex = $(parent).find('.file-row').length - 1;
	        	duplicate.find('.file-id').attr('name', 'attachmentId['+curIndex+']');
	        	
	        	$(duplicate).insertBefore(".file-add");
	        	
	        	elmList.push(duplicate);
	        	formData.append('attachment', file);
	        	
	        	$(duplicate).find(".file-action.btn-danger").click(function() {
	    	        var cnfrm = confirm('Are you sure want to remove this attachment?');
	    	        if (cnfrm) {
	    	        	
	    	        	var rowObj = $(this).closest('.file-row');
	    	        	var fileId = $(rowObj).find('.file-id').val();
	    	        	var data = {'attachmentId[0]': fileId};
	    	        	FacilioApp.ajax({
	    	        	       url : contextPath + '/app/attachment/delete',
	    	        	       type : 'POST',
	    	        	       data : data,
	    	        	       success : function(data) {
	    	        	    	   $(rowObj).remove();
	    	        	       }
	    	         	});
	    	        }
	    	    });
        	}
        	
        	FacilioApp.ajax({
       	       url : contextPath + '/app/attachment/add',
       	       type : 'POST',
       	       data : formData,
       	       processData: false,  // tell jQuery not to process the data
       	       contentType: false,  // tell jQuery not to set contentType
       	       success : function(data) {
       	    	   
       	           var idList = data.attachmentId;
       	           for (var i=0; i< idList.length; i++) {
       	        	   var attachmentId = idList[i];
       	        	   var elm = elmList[i];
       	        	   $(elm).find('.file-id').val(attachmentId);
       	           }
       	       }
        	});	
	    });
		
		$('#addForm').validator().on('submit', 
				FacilioApp.ajaxSubmitForm("#addForm", "<s:url action='add' />", 
				function(data, error) {
					if(data) {
						console.log(data);
						$.each(data, function(key, val) {
							window.location.href='#<s:property value="%{moduleLinkName}"/>/'+val;
						});
					}
					if(error) {
						console.log(error);
					}
				})
		);
	});
</script>