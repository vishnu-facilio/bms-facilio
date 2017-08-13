<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="facilio-tags" prefix="f" %>    
<div class="form-container form-content">
<div class="container1">
  <div class="row">
    <div class="heading2">
      <h3 class="leftHeading">Global support Emails</h3>
      <p class="rightText">
      
      	<s:if test="%{emailSetting.bccEmail == null || emailSetting.bccEmail == ''}">
      		<button class="btn btn-link" id="setBccBtn">Add Bcc</button> 
      	</s:if>
      	<s:else>
      		<small>Bcc: <s:property value="%{emailSetting.bccEmail}" /></small> 
      		<button class="btn btn-link" id="setBccBtn">Edit</button>
      	</s:else>
      	<i class="fa fa-question-circle-o" data-toggle="tooltip" title="This email address will be included automatically in the bcc field for all communications." aria-hidden="true"></i>
      </p>
    </div>
    <div class="borderTop"></div>
    <s:iterator var="supportEmail" value="supportEmails">
	    <div class="content">
	      <div class="row">
	        <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
	          <p></p>
	          <i class="fa fa-star" aria-hidden="true"></i> </div>
	        <div class="col-xs-4 col-sm-3 col-md-3 col-lg-3">
	          <div class="facilio">
	            <p class="titleStrong"><s:property value="#supportEmail.replyName" /></p>
	            <p class="mailId"><s:property value="#supportEmail.actualEmail" /></p>
	          </div>
	        </div>
	        <div class="col-xs-4 col-sm-3 col-md-3 col-lg-3">
	          <div class="forward">
	            <p class="titleLight">Forwarded to:</p>
	            <p class="mailId"><s:property value="#supportEmail.fwdEmail" /></p>
	          </div>
	        </div>
	        <div class="col-xs-3 col-sm-2 col-md-2 col-lg-3">
	          <p class="titleLight">Group:</p>
	          <s:if test="%{#supportEmail.autoAssignGroup == null}">
	          	<p>-</p>
	          </s:if>
	          <s:else>
	          	<p><s:property value="#supportEmail.autoAssignGroup.name" /></p>
	          </s:else>
	        </div>
	        <div class="col-xs-12 col-sm-3 col-md-3 col-lg-2 text-right">
	          <div class="btns"> <button type="button" class="btn btn-default btn-sm editSupportBtn">Edit</button> <button type="button" class="btn btn-default"><i class="fa fa-trash-o" aria-hidden="true"></i></button> </div>
	        </div>
	      </div>
	    </div>
    <div class="borderBottom"></div>
    </s:iterator>
  </div>
</div>
<form role="form" id="emailFlagsForm" data-toggle="validator">
	<div class="container1 question">
	  <div class="row">
	    <div class="agent">
	      <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
	        <h3 class="titleAgent">Use Agent names for Personalized Email Replies ?</h3>
	        <p class="agentContent">If YES then agent replies will have agent's name as the sender name (Email address will still be your Helpdesk Email address). For example 'zulfikar &lt;helpdesk@facilio1234.freshservice.com&gt;'. </p>
	      </div>
	      <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 text-right">
	      <s:checkbox class="emailFlag" name="emailSetting.useAgentNameInReply" onclick="$('#emailFlagsForm').submit();" value="%{emailSetting.useAgentNameInReply}" />
	     <!--  <div class="yesNo">
	        <input type="checkbox" id="toggle-two">
	        <script>
	  $(function() {
	    $('#toggle-two').bootstrapToggle({
	      on: 'Yes',
	      off: 'No'
	    });
	  })
	</script></div> -->
	</div>
	    </div>
	  </div>
	  <div class="row">
	    <div class="reply">
	      <p class="titleReply">
	      <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
	        <h3 class="titleAgent">Use "Reply-to" email address to create requestor contacts</h3>
	        <p class="replyContent">Note: If disabled, requestor will be created using 'From' address in email.</p>
	      </div>
	      <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 text-right">
	      <s:checkbox class="emailFlag" name="emailSetting.useReplyToAddrForRequestCreation" onclick="$('#emailFlagsForm').submit();" value="%{emailSetting.useReplyToAddrForRequestCreation}" />
	       <!--  <div class="yesNo1">
	          <input type="checkbox" id="toggle-two1">
	          <script>
	  $(function() {
	    $('#toggle-two1').bootstrapToggle({
	      on: 'Yes',
	      off: 'No'
	    });
	  })
	</script> 
	        </div> -->
	      </div>
	    </div>
	  </div>
	</div>
</form>
<div class="modal fade" id="setBccModal" tabindex="-1" role="dialog"
    aria-labelledby="Set automatic Bcc email" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
            	<h4 class="modal-title pull-left" style="line-height: 35px;" id="myModalLabel">Set automatic Bcc email</h4>
	            <div class="action-btn text-right">
					<button type="button" class="btn btn-default btn-circle cancel-btn"
						data-dismiss="modal">
						<i class="fa fa-times"></i>
					</button>
					<button type="button" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" class="btn btn-default save-btn" onclick="$('#setBccForm').submit();" class="btn btn-default save-btn">
						<i class="fa fa-check" aria-hidden="true"></i>&nbsp;&nbsp;Done
					</button>
				</div>  
        	</div>
        <div class="modal-body form-content">
          <form role="form" id="setBccForm" data-toggle="validator">
          	<div class="form-group">
          		<label for="emailSetting.bccEmail">Bcc address</label>
          		<input name="emailSetting.bccEmail" 
          			id="emailSetting.bccEmail" 
					class="form-control" 
					type="email" 
					placeholder="example@xyz.com"
					value="<s:property value="%{emailSetting.bccEmail}"/>"/>
          	</div>
          </form>
        </div>
      </div>
    </div>
  </div>
  <div class="modal fade" id="supportTicketModal" tabindex="-1" role="dialog"
    aria-labelledby="Supoort Ticket" aria-hidden="true">
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
          <%@ include file="supportemail.jsp" %>  
        </div>
      </div>
    </div>
  </div>
</div>
<script>

	$(".action-btn .new-btn").click(function() {
		location.href = '#newemailsettings';
	});

	$(document).ready(function() {
		$('[data-toggle="tooltip"]').tooltip();
		
		$(".editSupportBtn").click(function() {
			$("#supportTicketModal").modal("show");
		});
		
		$("#setBccBtn").click(function() {
			$("#setBccModal").modal("show");
		});
		
		$('#setBccForm').validator().on('submit',
			FacilioApp.ajaxSubmitForm("#setBccForm", contextPath + '/app/setup/updateemailsettings', 
				function(data, error) {
					if(data) {
						$("#setBccModal").modal("hide");
						//console.log(data);
						FacilioApp.notifyMessage('success', 'Updated Successfully!');
						setTimeout(function() {
							FacilioApp.refreshView();
						}, 500);
					}
					if(error) {
						console.log(error);
					}
				}
			)
		);
	
		$('#emailFlagsForm').validator().on('submit',
			FacilioApp.ajaxSubmitForm("#emailFlagsForm", contextPath + '/app/setup/updateemailsettings', 
				function(data, error) {
					if(data) {
						console.log(data);
					}
					if(error) {
						console.log(error);
						FacilioApp.notifyMessage('danger', 'Unable to change now. Try again later');
					}
				}
			)
		);
	
		/* $('.emailFlag').click(function() {
			var emailSetting = {};
			emailSetting[$(this).attr("name")] = $(this).is(':checked');
			console.log(emailSetting);
			FacilioApp.ajax({
				method : "post",
				url : contextPath + '/app/setup/updateemailsettings',
				data : {emailSetting : emailSetting},
				type : 'json',
				done: function(data) {
					console.log(data);
				},
				fail: function(error) {
					console.log(error);
					FacilioApp.notifyMessage('danger', 'Unable to change now. Try again later');
				}
			});*/
		}); 
</script>