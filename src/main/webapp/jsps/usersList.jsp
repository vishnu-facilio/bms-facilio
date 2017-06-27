<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Users
       	<button data-toggle="modal" onclick="newUser();" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New User
       	</button>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row users-list">
	<div class="col-lg-12">
   		<table class="table table-hover">
   			<thead>
   				<tr>
   					<th class="col-md-3 sortable">Name</th>
   					<th class="col-md-3 sortable align-right"><span class="line"></span>Email</th>
   					<th class="col-md-2 sortable"><span class="line"></span>Created</th>
   					<th class="col-md-2 sortable"><span class="line"></span>Status</th>
   					<th class="col-md-2 sortable"></th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="user" value="%{users}">
					<tr>
	   					<td>
	                        <img src="https://www.shareicon.net/download/2016/09/15/829473_man.svg" alt="contact" class="img-circle avatar hidden-phone">
	                        <a href="#users/<s:property value="#user.userId" />" class="name"><s:property value="#user.name" /></a>
	                        <span class="subtext"><s:property value="#user.getRoleAsString()" /></span>
	                    </td>
	                    <td>
	                    	<a href="mailto:<s:property value="#user.email" />"><s:property value="#user.email" /></a></td>
	                    <td><s:property value="#user.getInvitedTimeStr()" /></td>
	                    <td>
	                    	<s:if test="%{#user.inviteAcceptStatus}">
								<h5><span class="label label-success">Active</span></h5>
							</s:if>
							<s:else>
								<h5><span class="label label-danger">Inactive</span></h5>
							</s:else>
	                    </td>
	                    <td class="center">
	                    	<div class="btn-group">
	                    		<s:if test="%{#user.role == 0}">
									<button type="button" class="btn btn-outline btn-primary btn-md disabled">
		                    			<i class="fa fa-key"></i>
		                    		</button>
									<button type="button" class="btn btn-outline btn-primary btn-md disabled">
		                    			<i class="fa fa-pencil"></i>
		                    		</button>
		                    		<button type="button" class="btn btn-outline btn-danger btn-md disabled">
		                    			<i class="fa fa-trash"></i>
		                    		</button>
								</s:if>
								<s:else>
									<button type="button" user-id="<s:property value="#user.userId" />" data-toggle="tooltip" data-placement="left" title="Reset password" onclick="resetPass(this);" class="btn btn-outline btn-primary btn-md">
		                    			<i class="fa fa-key"></i>
		                    		</button>
									<button type="button" user-id="<s:property value="#user.userId" />" data-toggle="tooltip" data-placement="top" title="Edit user" onclick="editUser(this);" class="btn btn-outline btn-primary btn-md">
		                    			<i class="fa fa-pencil"></i>
		                    		</button>
		                    		<button type="button" user-id="<s:property value="#user.userId" />" data-toggle="tooltip" data-placement="right" title="Delete user" onclick="deleteUser(this);" class="btn btn-outline btn-danger btn-md">
		                    			<i class="fa fa-trash"></i>
		                    		</button>
								</s:else>
	                    	</div>
	                    </td>
	                </tr>
			    </s:iterator>
             </tbody>
      </table>
  </div>
</div>
<div class="modal fade" id="newUserModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">New User</h4>
	        </div>
	        <div class="modal-body">
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="saveUser(this);" class="btn btn-primary">Save</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<div class="modal fade" id="editUserModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">Edit User</h4>
	        </div>
	        <div class="modal-body">
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="updateUser(this);" class="btn btn-primary">Save</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<div class="modal fade" id="resetPassModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">Reset password</h4>
	        </div>
	        <div class="modal-body">
	        	<form role="form" id="resetPassForm" method="post" onsubmit="return false;">
					<div class="form-group">
					    <label>Password</label>
					    <s:textfield type="password" name="password" class="form-control"/>
					</div>
					<div class="form-group">
					    <label>Confirm password</label>
					    <s:textfield type="password" name="confirm_password" class="form-control"/>
					</div>
				</form>
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="resetPassAction(this);" class="btn btn-primary">Save</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<style>
.users-list .table {
	border: 1px solid #ddd;
}

.users-list .table td {
	padding:15px;
}

.users-list .table img.avatar {
    float: left;
    margin-right: 14px;
    max-width: 45px;
    position: relative;
    top: 5px;
}

.img-circle {
    border-radius: 50%;
}

img {
    vertical-align: middle;
}

img {
    border: 0;
}

.users-list .table a.name {
    display: block;
    font-size: 14px;
    margin: 8px 0 0 0;
}
.users-list .table .subtext {
    font-size: 12px;
    margin-left: 0;
    color: #778391;
    font-style: italic;
    margin-top: 0;
}
.users-list .table td {
    vertical-align: middle;
    font-size: 13px;
}
</style>
<script>
	$(".users-list table").dataTable();

	$('.users-list').tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });
    
	function newUser() {
		
		$("#newUserModel").modal("show");
		$('#newUserModel .modal-body').html("Loading...");
		
		$.ajax({
		      type: "GET",
		      url: "/home/users/new",
		      success: function (response) {
		    	  $('#newUserModel .modal-body').html(response);
		      }
		 });
	}
	
	function saveUser(btn) {
		$(btn).button('loading');
		
		$.ajax({
			method : "post",
			url : "/home/users/save",
			data : $("#newUserForm").serialize()
		})
		.done(function(data) {
			$('#newUserModel').modal('hide');
			FacilioApp.notifyMessage('success', 'User created successfully!');
			
			setTimeout(function() {
				FacilioApp.refreshView();
            }, 500);
		})
		.fail(function(error) {
			$(btn).button('reset');
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
	}
	
	function editUser(btn) {
		var userId = $(btn).attr('user-id');
		
		$("#editUserModel").modal("show");
		$('#editUserModel .modal-body').html("Loading...");
		
		$.ajax({
		      type: "GET",
		      url: "/home/users/edit?id="+userId,
		      success: function (response) {
		    	  $('#editUserModel .modal-body').html(response);
		      }
		 });
	}
	
	function updateUser(btn) {
		$(btn).button('loading');
		
		$.ajax({
			method : "post",
			url : "/home/users/update",
			data : $("#editUserForm").serialize()
		})
		.done(function(data) {
			$('#editUserModel').modal('hide');
			FacilioApp.notifyMessage('success', 'User updated successfully!');
			
			setTimeout(function() {
				FacilioApp.refreshView();
            }, 500);
		})
		.fail(function(error) {
			$(btn).button('reset');
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
	}
	
	function resetPass(btn) {
		var userId = $(btn).attr('user-id');
		
		$("#resetPassForm")[0].reset();
		$("#resetPassModel .btn-primary").attr('user-id', userId);
		$("#resetPassModel").modal("show");
	}
	
	function resetPassAction(btn) {
		
		var userId = $(btn).attr('user-id');
		
		var pass = $('#resetPassModel input[name=password]').val();
		var conf_pass = $('#resetPassModel input[name=confirm_password]').val();
		if (pass.trim() == "") {
			alert('Password should not be empty.');
		}
		else if (pass != conf_pass) {
			alert('Password does not match.');
		}
		else {
			$(btn).button('loading');
			$.ajax({
				method : "post",
				url : "/home/users/resetpassword",
				data : $("#resetPassForm").serialize()
			})
			.done(function(data) {
				$(btn).button('reset');
				$('#resetPassModel').modal('hide');
				FacilioApp.notifyMessage('success', 'User password reset successfully!');
			})
			.fail(function(error) {
				$(btn).button('reset');
				$(btn).button('reset');
				alert(JSON.stringify(error.responseJSON.fieldErrors));
			});
		}
	}
</script>