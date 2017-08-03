<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">
	<form role="form" id="newRoleForm" method="post" onsubmit="return false;">
	
                  <div class="col-lg-6 " >
                <div class="form-group col-centered">
            
  			 <img  class="responsive-img" id="upfile1" alt="click to upload" width="250" height="250"  style="cursor:pointer;line-height: 100px;text-align: center;background: #F8F8F8; "/>
                
                
		     </div>
		     <input type="file" id="file1"  name="photo" style="display:none" accept="image/*" />
				
					<div class="form-group">
	    		<a href="#">Change profile picture</a>
	    		
			</div>
        </div>
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

		
		
	</form>
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
			$('#').submit();
		});
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#';
		});
		
		$('#newRoleForm').validator().on('submit', function (e) {
			  if (e.isDefaultPrevented()) {
					// handle the invalid form...
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
						data : $("#newRoleForm").serialize(),
						done: function(data) {
							FacilioApp.notifyMessage('success', 'Role created successfully!');
							location.href = '#';
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
		
		$("#upfile1").click(function () {
            $("#file1").trigger('click');
            
          
       });
		
		$("#file1").change(function() {
			
			document.getElementById('upfile1').src = window.URL.createObjectURL(this.files[0]);
		});
		
		var cogUtil = new CognitoUtil();
		var cognitoUser = cogUtil.getCurrentUser();
		 cognitoUser.changePassword('oldPassword', 'newPassword', function(err, result) {
		        if (err) {
		            alert(err);
		            return;
		        }
		        console.log('call result: ' + result);
		    });
		
	});

</script>