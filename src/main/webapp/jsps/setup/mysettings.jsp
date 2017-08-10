<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">
	<form role="form" id="mysettings" method="post" onsubmit="return false;">
	<div class="col-lg-6" >
			<div class="form-group">
		    	<label>Name</label>
		    	<span class="required">*</span>
		    	<s:textfield name="user.name" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			    <label>Email</label>
			    <span class="required">*</span>
			    <s:textfield type="email" name="user.email" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			    <label>Phone</label>
			    <s:textfield type="text" name="user.phone" class="form-control"/>
			</div>
					<div class="form-group">
	    		<label>Address</label>
	    		<s:textfield name="role.name" class="form-control" placeholder="Street" required="true"/>
			</div>
						<div class="form-group">
			    <label>Time zone</label>
			    <select name="user.timezone" class="form-control">
			    	<option> -- </option>
			    </select>
			</div>
				
		</div>



		<div class="col-lg-6 ">
			<div class="form-group col-centered">

				<img class="responsive-img"  id="upfile1" alt="click to upload"
					width="250" height="250"
					style="cursor: pointer; line-height: 100px; text-align: center; background: #F8F8F8;" />


			</div>
			<input type="file" id="file1" name="userPhoto" style="display: none"
				accept="image/*" />

			<div class="form-group col-centered">
 <button type="button" class="btn btn-default" data-toggle="modal" data-target="#changePassModel">Change Password</button>
			</div>
		</div>
		
		
		
	</form>
</div>
<div class="modal fade" id="changePassModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	        	
	            <button type="button" class="close model-close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">Reset password</h4>
	        </div>
	        <div class="modal-body">
	        	<form role="form" id="changePassForm" method="post" onsubmit="return false;">
					<div class="form-group">
					    <label>current password</label>
					    <s:textfield type="password" name="old_password" class="form-control"/>
					</div>
					<div class="form-group">
					    <label>new password</label>
					    <s:textfield type="password" name="new_password" class="form-control"/>
					</div>
				</form>
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" id="model-close" data-dismiss="modal">Close</button>
	            <button type="submit" id="save-btn" class="btn btn-primary">Save</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<style>
.col-lg-12.fc-dashed-line{
margin-top:10px;
margin-bottom: 10px;
}
/* layout.css Style */
.upload-drop-zone {
  height: 120px;
  border-width: 2px;

}

/* skin.css Style*/
.upload-drop-zone {
  color: #ccc;
  border-style: dashed;
  border-color: #ccc;
  line-height: 120px;
  text-align: center
}
.upload-drop-zone.drop {
  color: #222;
  border-color: #222;
}
.col-centered{
  display: block;
  margin-left: auto;
  margin-right: auto;
  text-align: center;
}
</style>

<script>
	$(document).ready(function() {
		
		$(".action-btn .save-btn").click(function() {
			$('#mysettings').submit();
		});
		
		$("#save-btn").click(function() {
		
			$('#changePassForm').submit();
		});
		
		$(".model-close").click(function() {
	        $("#changePassForm")[0].reset();

		});
		$("#model-close").click(function() {
	        $("#changePassForm")[0].reset();

		});
		
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#mysettings';
		});
		
		$('#changePassModel').validator().on('submit', function (e) {
			
			  /* if (cognitoUser != null) {
		            cognitoUser.getSession(function (err, session) {
		                if (err) {
		                	console.log("error");
		                    alert(err);
		                    return;
		                }
		            });

		            cognitoUser.deleteUser(function (err, result) {
		                if (err) {
		                    alert(err);
		                    return;
		                }
		                
		            });
		        } */
			
			var old_pass = $('#changePassModel input[name=old_password]').val();
			var new_pass = $('#changePassModel input[name=new_password]').val();
			if ((old_pass.trim() == "")  &  (new_pass.trim() == "")) {
				alert('Password should not be empty.');
			}
			else if ((old_pass.trim() == "")) {
				alert('Password should not be empty.');
			}		
			
	
			
			var cogUtil = new CognitoUtil();
			var cognitoUser = cogUtil.getCurrentUser();
			
			
			cognitoUser.getSession(function(err, session) {
				
				if (session != null) {
					cognitoUser.changePassword(old_pass, new_pass , function(err, result) {
			             

				        if (err) {
		                	
							
				            alert(err);
							

				            return;
				        }
				        $(".model-close").trigger('click');
				 
				    });
				}
			});
		});
		
		
		
		
		$('#mysettings').validator().on('submit', function (e) {
			
			
			
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
						url : contextPath + "/app/setup/roles/add",
						data : $("#mysettings").serialize(),
						
						done: function(data) {
							FacilioApp.notifyMessage('success', 'Role created successfully!');
							location.href = '#';
						},
						fail: function(error) {
							$(".save-btn").button('reset');
							
							alert(error);
						} 
					});
					return false;
			  	}
			
			
			});
		
		$("#upfile1").click(function () {
            $("#file1").trigger('click');
            
          
       });
		
		$("#file1").change(function() {
			
			document.getElementById('upfile1').src = window.URL.createObjectURL(this.files[0]);
		});
	/*
		var cogUtil = new CognitoUtil();
		var cognitoUser = cogUtil.getCurrentUser();
		 cognitoUser.changePassword('oldPassword', 'newPassword', function(err, result) {
		        if (err) {
		            alert(err);
		            return;
		        }
		        console.log('call result: ' + result);
		    });
		 */
		
	});

</script>