<div class="row">
	<s:iterator value="formlayout" status="rowstatus" var="panel">
	<div class='col-lg-<s:property value="#panel.display.value" />'>
	<div class="panel-body">
		<s:if test="#panel.title != null">
			<div class="form-section"><s:property value="#panel.title"/> :</div>
		</s:if>
		<s:iterator value="#panel" status="rowstatus" var="field">
			<div class="form-group">
					<label>
						<s:if test="%{#field.icon != null && #field.icon != ''}">
							<i class="<s:property value="#field.icon"/> field-label-icon" aria-hidden="true"></i>
						</s:if>
						<s:property value="#field.displayName"/>
						<s:if test="%{#field.required}">
							<span class="required">*</span>
						</s:if>
					</label>
					
					
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@TEXTBOX || #field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@EMAIL || #field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@NUMBER}">
						
						<input name="<s:property value="#field.inputName"/>" 
							class="form-control" 
							type="<s:property value="#field.displayType.html5Type"/>"
							placeholder="<s:property value="#field.placeHolder"/>"
							<s:if test="%{#field.required}">
							required="true"
							</s:if>
							<s:if test="%{#field.disabled}">
							disabled="disabled"
							</s:if>
							/>
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@DECISION_BOX}">
						
						<input name="<s:property value="#field.inputName"/>" 
							class="form-control" 
							type="<s:property value="#field.displayType.html5Type"/>"
							placeholder="<s:property value="#field.placeHolder"/>"
							<s:if test="%{#field.required}">
							required="true"
							</s:if>
							<s:if test="%{#field.disabled}">
							disabled="disabled"
							</s:if>
							/>
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@DATE}">
					
						<div class="input-group">
							<input name="<s:property value="#field.inputName"/>"
								class="form-control f-date" 
								type="<s:property value="#field.displayType.html5Type"/>"
								placeholder="<s:property value="#field.placeHolder"/>"
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
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@DATETIME}">
					
						<div class="input-group">
							<input name="<s:property value="#field.inputName"/>"
								class="form-control f-datetime" 
								type="<s:property value="#field.displayType.html5Type"/>"
								placeholder="<s:property value="#field.placeHolder"/>"
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
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@LOOKUP_SIMPLE}">
						<select
							class="form-control"
							name="<s:property value="#field.inputName"/>"
							placeholder="<s:property value="#field.placeHolder"/>"
							data-module-name="<s:property value="#field.lookupModule.name"/>"
							<s:if test="%{#field.required}">
							required="true"
							</s:if>
							>
							<s:iterator value="actionForm[#field.list]" status="rowstatus" var="option">
								<option value="<s:property value="#option.key"/>"><s:property value="#option.value"/>
								</option>
								
							</s:iterator>
						</select>
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@LOOKUP_POPUP}">
							<div class="input-group">
								<select
									class="form-control select-lookup"
									name="<s:property value="#field.inputName"/>"
									placeholder="<s:property value="#field.placeHolder"/>"
									<s:if test="%{#field.specialType == 'users' || #field.specialType == 'groups' || #field.specialType == 'basespace' || #field.specialType == 'requester'}">
										data-module-name="<s:property value="#field.specialType"/>"
									</s:if>
										data-module-name="<s:property value="#field.lookupModule.name"/>"
									<s:else>
									</s:else>
									<s:if test="%{#field.required}">
									required="true"
									</s:if>
									>
									<option value="-1">- None -</option>
								</select>
								<span class="input-group-btn">
									<s:if test="%{#field.specialType == 'users'}">
										<button class="btn btn-default btn-md btn-lookup" data-toggle="tooltip" data-placement="top" title="Lookup using list" type="button" onclick="FacilioApp.lookupDialog('users', 'Users', '<s:property value="#field.inputName"/>')">
									</s:if>
									<s:elseif test="%{#field.specialType == 'groups'}">
										<button class="btn btn-default btn-md btn-lookup" data-toggle="tooltip" data-placement="top" title="Lookup using list" type="button" onclick="FacilioApp.lookupDialog('groups', 'Groups', '<s:property value="#field.inputName"/>')">
									</s:elseif>
									<s:elseif test="%{#field.specialType == 'basespace'}">
										<button class="btn btn-default btn-md btn-lookup" data-toggle="tooltip" data-placement="top" title="Lookup using list" type="button" onclick="FacilioApp.lookupDialog('basespace', 'Spaces', '<s:property value="#field.inputName"/>')">
									</s:elseif>
									<s:elseif test="%{#field.specialType == 'requester'}">
										<button class="btn btn-default btn-md btn-lookup" data-toggle="tooltip" data-placement="top" title="Lookup using list" type="button" onclick="FacilioApp.lookupDialog('requester', 'Requester', '<s:property value="#field.inputName"/>')">
									</s:elseif>
									<s:else>
										<button class="btn btn-default btn-md btn-lookup" data-toggle="tooltip" data-placement="top" title="Lookup using list" type="button" onclick="FacilioApp.lookupDialog('<s:property value="#field.lookupModule.name"/>', '<s:property value="#field.lookupModule.displayName"/>', '<s:property value="#field.inputName"/>')">
									</s:else>
										<i class="<s:property value="#field.lookupIcon"/>"></i>
									</button>
								</span>
		                    </div>					
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@LOOKUP_SECTION}">
							<div class="file-section">
								<div class="col-md-12 file-row file-add">
									<span class="file-action btn btn-success btn-circle-sm col-md-2"><i class="fa fa-1x fa-plus" aria-hidden="true"></i></span>
									<span class="file-name col-md-10 text-left">Add <s:property value="#field.lookupModule.label"/></span>									
								</div>
							</div>
							<div class="col-md-12 file-row file-row-template hidden">
								<input class="file-object" type="hidden"/>
								<span class="file-action btn btn-danger btn-circle-sm col-md-2"><i class="fa fa-1x fa-minus" aria-hidden="true"></i></span>
								<span class="file-name col-md-10 text-left"></span>
							</div>
					</s:if>
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@SELECTBOX }">
					
						<select
							class="form-control"
							name="<s:property value="#field.inputName"/>"
							placeholder="<s:property value="#field.placeHolder"/>"
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
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@TEXTAREA }">
						<textarea class="form-control"
							name="<s:property value="#field.inputName"/>"
							placeholder="<s:property value="#field.placeHolder"/>"
							<s:if test="%{#field.required}">
							required="true"
							</s:if>
							></textarea>
					</s:if>
					<%-- 
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.modules.FacilioField$FieldDisplayType@FILE }">
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
									<input class="file-id" name="attachmentId[]" type="hidden"/>
									<span class="file-action btn btn-danger btn-circle-sm col-md-2"><i class="fa fa-1x fa-minus" aria-hidden="true"></i></span>
									<span class="file-name col-md-10 text-left"></span>
								</div>
						</s:else>
					</s:if>	
					--%>
				</div>
			
		</s:iterator>
 </div>
  </div>
</s:iterator>
</div>
<script>
	$(document).ready(function() {
		$('select').selectize({
			preload : true,
			load : function(query, callback) {
				//console.log(this);
				var self = this;
				var moduleName = this.$input.data("module-name");
				//console.log(moduleName);
				FacilioApp.ajax({
					method : "post",
					url : "<s:url action='picklist' namespace='/app' />",
					data : {moduleName : moduleName},
					done: function(data) {
						var dataArr = [];
						var defaultValue;
						var isFirst = true;
						for(var key in data) {
							dataArr.push({value : key, text : data[key]});
							if(isFirst) {
								defaultValue = key;
								isFirst = false;
							}
						}
						callback(dataArr);
						self.setValue(defaultValue);
					},
					fail: function(error) {
					}
				});
			}
		});
	});
</script>