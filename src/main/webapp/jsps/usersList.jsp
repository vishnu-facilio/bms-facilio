<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<table width="100%" class="table table-striped table-hover" id="record-list">
    <thead>
        <tr>
        	<th>
        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        		Name
        	</th>
        	<th>Email</th>
        	<th>Role</th>
        	<th>Created</th>
        	<th>Status</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
    	<s:iterator var="user" value="%{users}">
			<tr class="odd gradeX" data-user-id="<s:property value="#user.orgUserId" />">
  					<td>
                       <span class="user-name"><s:property value="#user.name" /></span>
                   </td>
                   <td>
                       <s:property value="#user.email" />
                   </td>
                   <td>
                       <s:property value="#user.role.name" />
                   </td>
                   <td><s:property value="#user.getInvitedTimeStr()" /></td>
                   <td>
                   		<div class="toggle-switch" >
                   			<s:if test="%{#user.userStatus}">
                   				<input type="checkbox" id="<s:property value="#user.orgUserId" />_status" class="checkbox hidden userstatus" checked/>
                   			</s:if>
                   			<s:else>
                   				<input type="checkbox" id="<s:property value="#user.orgUserId" />_status" class="checkbox hidden userstatus"/>
                   			</s:else>
                   			<label for="<s:property value="#user.orgUserId" />_status"></label>
                   		</div>
                   </td>
                   <td class="center">
                   		<div class="dropdown more-actions">
	                   		<a data-toggle="dropdown" class="dropdown-toggle"><i class="fa fa-ellipsis-h"></i></a>
	                   		<ul class="dropdown-menu" role="menu">
	                           	<li><a class="resetpwd">Reset password</a>
	                           	<li><a class="edit">Edit</a>
								<li><a class="delete">Delete</a>
	                        </ul>
                        </div>
                   </td>
               </tr>
	    </s:iterator>
	</tbody>
</table>
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
<script>

	jQuery(function($) {
		var $userName = $('.user-name');
		if ($userName.length) {
			$userName.avatarMe({
				avatarClass: 'avatar-me',
				max: 2
      		});
    	}
	});
  
	$(".setup-list-container table").dataTable({
		order: [[0, 'asc']],
	      language: {
	    	  paginate: {
	    		  previous: "<",
	    		  next: ">"
	    	  }
	      },
	      
	      columnDefs: [{
	    	  targets: 1,
	    	  orderable: false
	      },
	      {
	    	  targets: 2,
	    	  orderable: false
	      },
	      {
	    	  targets: 4,
	    	  searchable: false,
	    	  orderable: false
	      },
	      {
	    	  targets: 5,
	    	  searchable: false,
	    	  orderable: false
	      }],
	      
	      buttons: false,
	      responsive: true,
	      searching: false,
	      paging: false,
	      lengthChange: false,
	});

	$(".userstatus").change(function() {
		
		console.log('aaaa');
		var userId = $(this).closest('tr').data('user-id');
		var checked = this.checked;
		if (!checked) {
			var cnfm = confirm('Are you sure want to deactivate this user?');
			if (cnfm) {
				// deactivate the user
				console.log(userId);
				changeStatus(userId, false);
			}
			else {
				this.checked = true;
			}
		}
		else {
			changeStatus(userId, true);
		}
	});
	
	$(".action-btn .new-btn").click(function() {
		location.href = '#users/new';
	});
	
	$(".more-actions .resetpwd").click(function() {
		// reset password
		console.log('reset password');
	})
	
	$(".more-actions .edit").click(function() {
		// edit user
		var userId = $(this).closest('tr').data('user-id');
		location.href = '#users/edit?userId='+userId;
	})
	
	$(".more-actions .delete").click(function() {
		// delete user
		console.log('delete user');
	})
	
	function changeStatus(userId, userStatus) {
		FacilioApp.ajax({
			method : "post",
			url : contextPath + "/app/setup/users/changestatus",
			data : {'user.orgUserId': userId, 'user.userStatus': userStatus},
			done: function(data) {
				FacilioApp.notifyMessage('success', 'User status changed successfully!');
			},
			fail: function(error) {
				alert(JSON.stringify(error.responseJSON.fieldErrors));
			} 
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
			FacilioApp.ajax({
				method : "post",
				url : contextPath + "/app/users/resetpassword",
				data : $("#resetPassForm").serialize(),
				done: function(data) {
					$(btn).button('reset');
					$('#resetPassModel').modal('hide');
					FacilioApp.notifyMessage('success', 'User password reset successfully!');
				},
				fail: function(error) {
					$(btn).button('reset');
					$(btn).button('reset');
					alert(JSON.stringify(error.responseJSON.fieldErrors));
				} 
			});
		}
	}
</script>