<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="facilio-tags" prefix="f" %>
<form role="form" id="supportEmailForm" data-toggle="validator">    
<div class="container1">
	<input type="hidden" name="supportEmail.id" value="<s:property value="%{supportEmail.id}"/>" />
  <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
      <p class="name">Name</p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
      <input type="text" name="supportEmail.replyName" value="<s:property value="%{supportEmail.replyName}"/>" class="txtName" />
      <p class="subText1">Name of the email that will be used in the ticket replies</p>       
    </div>
  </div>
   <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
      <p class="name">Your helpdesk email <span class="star"> *</span></p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
      <input type="email" name="supportEmail.actualEmail" id="actualEmail" value="<s:property value="%{supportEmail.actualEmail}"/>" class="txtName" />
      <p class="subText1">This is also your Reply-to address eg. helpdesk@yourcompany.com</p>       
    </div>
  </div>
  <div class="row">
        <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
          <p class="name">Forward your emails to:</p>    
        </div>
        <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
      <input type="email" name="supportEmail.fwdEmail" id="fwdEmail" value="<s:property value="%{supportEmail.fwdEmail}" />" class="forwardMail" style="color:#0078bd;font-size:18px;font-weight:400;" disabled />
      <p class="subText2"><a href="#" data-toggle="tooltip" title="Learn how to configure your support email">How to convert your emails into Freshservice tickets?</a></p>       
    </div>
  	</div>
 <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
      <p class="name">Assign to Group</p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
      
     <select name="supportEmail.autoAssignGroup.groupId" data-module-name="groups" data-default-val="<s:property value="%{supportEmail.autoAssignGroup.groupId}" />">
  		<option value="-1">--</option>	
	</select>
      <p class="subText1">New tickets to this support email will get automatically assigned to a group</p>       
    </div>
  </div>

</div>
</form>
<script>
	$(document).ready(function() {
		$('select').selectize({
			preload : true,
			load : function(query, callback) {
				//console.log(this);
				var self = this;
				var moduleName = this.$input.data("module-name");
				var defaultValue = this.$input.data("default-val") || -1;
				//console.log(moduleName);
				if(moduleName) {
					FacilioApp.ajax({
						method : "post",
						url : "<s:url action='picklist' namespace='/app' />",
						data : {moduleName : moduleName},
						done: function(data) {
							var dataArr = [];
							var isFirst = true;
							for(var key in data) {
								dataArr.push({value : key, text : data[key]});
							}
							callback(dataArr);
							self.setValue(defaultValue);
						},
						fail: function(error) {
						}
					});
				}
			}
		});
		
		$("#actualEmail").on("keyup", function() {
			//var regEx = /^([a-z0-9])+@<s:property value="%{orgDomain}" />.facilio.com$/i;
			var actualEmail = $(this).val();
			var orgEmailDomain = "@<s:property value="%{orgDomain}" />.facilio.com";
			console.log(actualEmail);
			if(actualEmail.toLowerCase().endsWith(orgEmailDomain)) {
				$("#fwdEmail").val(actualEmail);
			}
			else {
				var emailSplit = actualEmail.toLowerCase().split("@");
				var domain = emailSplit[1] || "";
				$("#fwdEmail").val(domain.replace(/\./g,"")+emailSplit[0]+orgEmailDomain);
			}
		});
		
		$('#supportEmailForm').validator().on('submit',
			FacilioApp.ajaxSubmitForm("#supportEmailForm", 
				<s:if test="%{supportEmail == null}">
					contextPath + '/app/setup/addsupportemail', 
				</s:if>
				<s:else>
					contextPath + '/app/setup/editsupportemail',
				</s:else> 
				function(data, error) {
					if(data) {
						console.log(data);
						$('.modal').modal('hide');
						
						<s:if test="%{supportEmail == null}">
							FacilioApp.notifyMessage('success', 'Added Successfully');
						</s:if>
						<s:else>
							FacilioApp.notifyMessage('success', 'Edited Successfully');
						</s:else>
						
						setTimeout(function() {
							FacilioApp.refreshView();
						}, 500);
					}
					if(error) {
						console.log(error);
						<s:if test="%{supportEmail == null}">
							FacilioApp.notifyMessage('danger', 'Unable to add now. Try again later');
						</s:if>
						<s:else>
							FacilioApp.notifyMessage('danger', 'Unable to update now. Try again later');
						</s:else>
					}
				}
			)
		);
	});
</script>