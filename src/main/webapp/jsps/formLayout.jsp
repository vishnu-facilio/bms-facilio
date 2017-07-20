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
					<s:if test="%{#field.displayType == @com.facilio.bmsconsole.context.Field$FieldType@DECISION_BOX}">
						
						<input name="<s:property value="#field.name"/>" 
							id="<s:property value="#field.id"/>" class="form-control" 
							type="<s:property value="#field.html5Type"/>"
							checked="true"
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
						<s:if test="%{#field.lookupModule.displayType == 'simple'}">
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
						<s:else>
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
						</s:else>
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
				</div>
			
		</s:iterator>
 </div>
  </div>
</s:iterator>
</div>