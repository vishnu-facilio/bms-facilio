<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">

	<s:form namespace="/app/import" action="upload" method="post" enctype="multipart/form-data" id="import" data-toggle="validator">
	<div class="col-lg-6" >
			<div class="form-group">
		    	<label>Modules</label>
		    	<span class="required">*</span>
		    	<select name="module" required class="form-control">
				
				<option>campus</option>
				<option>Building</option>
				<option>Floor</option>
				<option>Space</option>
				<option>Zone</option>
			</select>
		
			</div>
			<div class="form-group">
			    <label>Select a file to upload</label>
			    <span class="required">*</span><br>
			  	<input type="file" required name="fileUpload"  /> <br /> 
			</div>
		<button type="button" class="btn btn-default new-btn save-btn">Upload</button>
				
		</div>


		
		
		
	</s:form>
</div>

<style>

</style>

<script>
	$(document).ready(function() {
		
		$(".save-btn").click(function() {
			$('#import').submit();
		});
	
		$('#import').validator().on('submit', function (e) {
			
			var module = $('select[name=module]').val();
			var fileObj = $('input[name=fileUpload]')[0].files[0];
			
			console.log(module);
			console.log(fileObj);
			
			var formData = new FormData();
			formData.append('module', module);
			formData.append('fileUpload', fileObj);	
			
			console.log("Test");
			console.log( $( this ).serializeArray() );
			event.preventDefault();
			
			  if (e.isDefaultPrevented()) {
					// handle the invalid form...
					;
			  }
			  else {
					// check if any validation errors
					if ($(this).find('.form-group').hasClass('has-error')) {
						return false;
					}
					
					$(".save-btn").button('loading');
					FacilioApp.ajax({
						method : "post",
						url : contextPath + "/app/import/upload",
						data : formData,
						processData: false,
						contentType: false,
						done: function(data) {
							$('.setup-list-content').html(data);
						},
						fail: function(error) {
							$(".save-btn").button('reset');
							console.log(error);
							alert(error);
						} 
					});
					return false;
			  	}
			
			
			});
	});

</script>