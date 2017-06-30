<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<div class="row">
	<div class="col-lg-12">
		<h1 class="page-header">New Request</h1>
	</div>
	<!-- /.col-lg-12 -->
</div>
<form role="form" id="addTicketForm" onsubmit="return false;">

	<div class="row">

		<div class="col-lg-6">
			<div class="panel-body">
				<div class="form-group">
					<label>Requester</label>
					<s:textfield type="email" name="ticket.requester"
						id="inputRequester" class="form-control"
						placeholder="xyz@example.com" />
				</div>
				<div class="form-group">
					<label>Asset</label>
					<s:select class="form-control" list="actionForm.assetList"
						name="ticket.failedAssetId" id="inputAsset" headerKey="0"
						headerValue="--" />
				</div>
				<div class="form-group">
					<label>Location</label>
					<s:select class="form-control" list="actionForm.locations"
						name="location" id="inputlocation" headerKey="0" headerValue="--" />
				</div>
				<%--  <div class="form-group">
                <label>Template</label>
                <s:select class="form-control" list="assetList" name="asset" id="inputAsset" headerKey="-1" headerValue="--" />
            </div> --%>
			</div>
		</div>
		<div class="col-lg-6">
			<div class="panel-body">
				<div class="form-group">
					<label>Due Time</label>
					<div>
						<s:textfield class="form-control" type="datetime-local"
							name="ticket.dueTime" id="inputDueTime" />
					</div>
				</div>
				<%--
<div class="form-group">
         <label for="disabledSelect">Status</label>
          <input class="form-control" id="disabledInput" type="text" placeholder="Disabled input" disabled>
</div>
 
<div class="form-group">
                                <label>Priority</label>
                                <select class="form-control">
                                    <option>1 - Critical</option>
                                    <option>2 - High</option>
                                    <option>3 - Moderate</option>
                                    <option>4 - Low</option>
                                    <option>5 - Planning</option>
                                </select>
                            </div>
                            --%>
				<div class="form-group">
					<label>Status</label>
					<s:select class="form-control" list="statusList"
						name="ticket.statusCode" id="inputStatus" disabled="true" />
				</div>
				<div class="form-group">
					<label>Assigned To</label>
					<s:select class="form-control" list="actionForm.userList"
						name="ticket.assignedToId" id="inputAgent" headerKey="0"
						headerValue="--" />
				</div>

			</div>

		</div>
		<div class="row">

			<div class="col-lg-12">
				<div class="panel-body">

					<div class="form-group">
						<label>Subject</label>
						<s:textfield name="ticket.subject" id="inputSubject"
							class="form-control" placeholder="Batmobile is not working" />
					</div>
					<s:iterator var="customFieldName" value="customFieldNames">
						<div class="form-group">
							<label><s:property value="customFieldName" /></label>
							<s:textfield name="ticket.customProps['%{customFieldName}']"
								id="input%{customFieldName}" class="form-control" />
						</div>
					</s:iterator>
					<div class="form-group">
						<label>Description</label>
						<s:textarea class="form-control" name="ticket.description"
							label="inputDescription" rows="3"
							placeholder="More about the problem..." />
					</div>

					<div class="form-group">
						<button type="reset" class="btn btn-outline btn-default"
							onclick="location.href='#tickets';">Go Back</button>
						<button type="submit" class="btn btn-outline btn-primary">Save</button>
					</div>

				</div>
			</div>

		</div>
	</div>
</form>
<script>
	$(document).ready(function() {
		$("#addTicketForm").submit(function() {
			console.log("test");
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
				console.log(error);
			});
		});
	});
</script>
