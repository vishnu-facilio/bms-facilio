<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">

<div class="form-header">
<div class="row">
<div class="col-sm-12" >
  <h4 class="pull-left">New <s:property value="%{moduleName}"/></h4>
    <div class="action-btn text-right">
    	<button type="button" class="btn btn-default btn-circle cancel-btn" onclick="location.href='#<s:property value="%{moduleLinkName}"/>';"><i class="fa fa-times"></i></button>
 		<button type="button" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" class="btn btn-default save-btn" onclick="$('#addForm').submit();"><i class="fa fa-check" aria-hidden="true"></i>&nbsp;&nbsp;Save</button>
	 </div>
  </div>
</div>
</div>
<div class="form-content page-content">
<div class="row">
<div class="col-lg-12">
<form role="form" id="addForm" data-toggle="validator">

	<div class="row">
	<s:iterator value="formlayout" status="rowstatus" var="panel">
	
	<div class='<s:if test="%{#panel.display == @com.facilio.bmsconsole.context.Panel$Type@HALF }">col-lg-6</s:if><s:else>col-lg-12</s:else>'>
	<div class="panel-body">
		<s:iterator value="#panel" status="rowstatus" var="field">
			
			
			
			<div class="form-group">
					<label>
						<s:if test="%{#field.icon != null && #field.icon != ''}">
							<i class="<s:property value="#field.icon"/> field-label-icon" aria-hidden="true"></i>
						</s:if>
						<s:property value="#field.label"/>
						<s:if test="%{#field.required}">
							<span class="required">*</span>
						</s:if>
					</label>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@TEXTBOX}">
						
						<input name="<s:property value="#field.name"/>" 
							id="<s:property value="#field.id"/>" class="form-control" 
							type="<s:property value="#field.html5Type"/>"
							placeholder="<s:property value="#field.placeholder"/>"
							<s:if test="%{#field.required}">
							required="true"
							</s:if>
							<s:if test="%{#field.isDisabled}">
							disabled="disabled"
							</s:if>
							/>
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@DATE}">
					
						<div class="input-group">
							<input name="<s:property value="#field.name"/>" 
								id="<s:property value="#field.id"/>" class="form-control f-date" 
								type="<s:property value="#field.html5Type"/>"
								placeholder="<s:property value="#field.placeholder"/>"
								<s:if test="%{#field.required}">
								required="true"
								</s:if>
								/>
							<span class="input-group-btn">
								<button class="btn btn-default btn-md btn-lookup" type="button">
									<span class="fa fa-calendar"></span>
								</button>
							</span>
	                    </div>
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@DATETIME}">
					
						<div class="input-group">
							<input name="<s:property value="#field.name"/>" 
								id="<s:property value="#field.id"/>" class="form-control f-datetime" 
								type="<s:property value="#field.html5Type"/>"
								placeholder="<s:property value="#field.placeholder"/>"
								<s:if test="%{#field.required}">
								required="true"
								</s:if>
								/>
							<span class="input-group-btn">
								<button class="btn btn-default btn-md btn-lookup" type="button">
									<span class="fa fa-calendar"></span>
								</button>
							</span>
	                    </div>
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@LOOKUP}">
						<div class="input-group">
							<select
								class="select-lookup"
								id="<s:property value="#field.id"/>"
								name="<s:property value="#field.name"/>"
								placeholder="<s:property value="#field.placeholder"/>"
								<s:if test="%{#field.required}">
								required="true"
								</s:if>
								>
								<s:if test="%{#field.lookupModule.preloadedList != null && #field.lookupModule.preloadedList != ''}">
									<option value="">- None -</option>
									<s:iterator value="actionForm[#field.lookupModule.preloadedList]" status="rowstatus" var="option">
										<option value="<s:property value="#option.key"/>"><s:property value="#option.value"/></option>
									</s:iterator>
								</s:if>
							</select>
							<span class="input-group-btn">
								<button class="btn btn-default btn-md btn-lookup" data-toggle="tooltip" data-placement="top" title="Lookup using list" type="button" onclick="FacilioApp.lookupDialog('<s:property value="#field.lookupModule.name"/>', '<s:property value="#field.lookupModule.label"/>', '<s:property value="#field.lookupModule.criteria"/>', '<s:property value="#field.id"/>')">
									<i class="<s:property value="#field.lookupModule.lookupIcon"/>"></i>
								</button>
							</span>
	                    </div>					
					
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@SELECTBOX }">
					
						<select
							class="form-control"
							id="<s:property value="#field.id"/>"
							name="<s:property value="#field.name"/>"
							placeholder="<s:property value="#field.placeholder"/>"
							<s:if test="%{#field.required}">
							required="true"
							</s:if>
							>
							<option value="">- None -</option>
							<s:iterator value="actionForm[#field.list]" status="rowstatus" var="option">
								<option value="<s:property value="#option.key"/>"><s:property value="#option.value"/></option>
							</s:iterator>
						</select>
						
					</s:if>		
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@TEXTAREA }">
						<textarea class="form-control" name="<s:property value="#field.name"/>"
							id="<s:property value="#field.id"/>"
							name="<s:property value="#field.name"/>"
							placeholder="<s:property value="#field.placeholder"/>"
							<s:if test="%{#field.required}">
							required="true"
							</s:if>
							></textarea>
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@FILE }">
						<s:if test="%{#field.fileField.displayType == 'simple'}">
							<div class="input-group">
								<input type="text"
									class="form-control" 
									placeholder="<s:property value="#field.placeholder"/>"
									readonly="true"
									<s:if test="%{#field.required}">
									required="true"
									</s:if>
									/>
								<span class="input-group-btn">
									<button class="btn btn-default btn-md btn-lookup btn-file" type="button">
										<span class="fa fa-upload"></span>
										<input type="file" class="file-simple"
											id="<s:property value="#field.id"/>"
											name="<s:property value="#field.name"/>"
											<s:if test="%{#field.required}">
											required="true"
											</s:if>
											<s:if test="%{#field.fileField.maxFiles > 1}">
											multiple="true"
											</s:if>
											/>
									</button>
								</span>
		                    </div>
						</s:if>
						<s:else>
							<div class="file-section">
								<div class="col-md-12 file-row file-add">
									<span class="file-action btn btn-success btn-circle-sm col-md-2"><i class="fa fa-1x fa-plus" aria-hidden="true"></i></span>
									<span class="file-name col-md-10 text-left">Add Attachment</span>									
									<input type="file" class="hidden"
										id="<s:property value="#field.id"/>"
										name="<s:property value="#field.name"/>"
										<s:if test="%{#field.required}">
										required="true"
										</s:if>
										<s:if test="%{#field.fileField.maxFiles > 1}">
										multiple="true"
										</s:if>
										/>
								</div>
							</div>
							<div class="col-md-12 file-row file-row-template hidden">
									<input class="file-object" name="object-id" type="hidden"/>
									<span class="file-action btn btn-danger btn-circle-sm col-md-2"><i class="fa fa-1x fa-minus" aria-hidden="true"></i></span>
									<span class="file-name col-md-10 text-left"></span>
								</div>
						</s:else>
					</s:if>	
				</div>
			
		</s:iterator>
 </div>
  </div>
</s:iterator>
	</div>
</form>
</div>
</div>
</div>
<!-- ------------------------------ pop up ---------------------------------->

<div class="modal fade" id="AddNewTask" tabindex="-1" role="dialog"
	aria-labelledby="helpModalLabel" aria-hidden="true">

	<!--  popup header -->

	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">

				<h4 class="modal-title pull-left" id="myModalLabel">Add Task</h4>

				<div class="action-btn text-right">
					<button type="button" class="btn btn-default btn-circle cancel-btn"
						data-dismiss="modal">
						<i class="fa fa-times"></i>
					</button>
					<button type="button" class="btn btn-default save-btn">
						<i class="fa fa-check" aria-hidden="true"></i>&nbsp;&nbsp;Done
					</button>
				</div>

			</div>

			<!-- pop up body -->
			<form>
				<div class="modal-body form-content">
					<div class="row">
						<div class="col-md-12 col-sm-12">
							<div class="col-md-8 col-sm-8">
								<div class="form-group">
									<label>Title *</label> <input type="text" class="form-control">
								</div>
								<div class="form-group">
									<label>Assigned to</label> <input type="text"
										class="form-control">
								</div>
							</div>
							<div class="col-md-4 col-sm-4">
								<div class="form-group">
									<label>Status</label> <input type="text" class="form-control">
								</div>
								<div class="form-group">
									<label>Due date</label> <input type="text" class="form-control f-date">
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 hr-dashed-line">
						
						</div>
						</div>
					<div class="row">
						<div class="col-md-12 col-sm-12">
							<div class="col-md-4 col-sm-4">
								<div class="form-group">
									<label>Scheduled start</label> <input type="text"
										class="form-control f-datetime">
								</div>
								<div class="form-group">
									<label>Actual task start</label> <input type="text"
										class="form-control f-datetime">
								</div>
							</div>
							<div class="col-md-4 col-sm-4">
								<div class="form-group">
									<label>Estimated end</label> <input type="text"
										class="form-control f-datetime">
								</div>
								<div class="form-group">
									<label>Actual task end</label> <input type="text"
										class="form-control f-datetime">
								</div>
							</div>
							<div class="col-md-4 col-sm-4">
								<div class="form-group">
								<label>Work duration</label>
								<div class="row ">
								<div class="col-md-4 col-sm-4 ">
								<input type='text' class="form-control" placeholder="H" />
								</div>
								<div class="col-md-4 col-sm-4">
								<input type='text' class="form-control" placeholder="M" />
								</div>
								<div class="col-md-4 col-sm-4">
								<input type='text' class="form-control" placeholder="S"/>
								</div>
								
								</div>
									
							</div>
							</div>
						</div>
					</div>


				</div>
			</form>


		</div>
	</div>

</div>

<script>
	$(document).ready(function() {
		
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
		
		$(".file-add .file-action, .file-add .file-name").click(function(e){
	        e.preventDefault();
	        var fileElm = $(this).parents('.file-add').find(':file');
	        $(fileElm).trigger('click');
	    });
		
		$(".file-add input[type=file]").change(function(e){
			var input = $(this),
	        numFiles = input.get(0).files ? input.get(0).files.length : 1,
	        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
	        
	        if (numFiles > 1) {
	        	
	        }
	        else if (numFiles == 1) {
	        	var parent = $(input).parents('.file-section');
	        	var duplicate = $('.file-row-template').clone();
	        	duplicate.removeClass('file-row-template');
	        	duplicate.removeClass('hidden');
	        	duplicate.find('.file-name').text(label);
	        	
	        	$(duplicate).insertBefore(".file-add");
	        }
	    });
		
		$('#addForm').validator().on('submit', function (e) {
		  if (e.isDefaultPrevented()) {
				// handle the invalid form...
		  }
		  else {
				// check if any validation errors
				if ($(this).find('.form-group').hasClass('has-error')) {
					return false;
				}
				
				$(".save-btn").button('loading');
				$.ajax({
					method : "post",
					url : "<s:url action='add' />",
					data : $("#addForm").serialize()
				})
				.done(function(data) {
					console.log(data);
					$.each(data, function(key, val) {
						window.location.href='#<s:property value="%{moduleLinkName}"/>/'+val;
					});
				})
				.fail(function(error) {
					$(".save-btn").button('reset');
					console.log(error);
				});
				return false;
		  	}
		});
	});
</script>