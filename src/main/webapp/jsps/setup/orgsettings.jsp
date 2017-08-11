<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">
	<form role="form" id="newRoleForm" method="post" onsubmit="return false;">
		<div class="col-lg-6" >
			<div class="form-group">
	    		<label>Org Name</label>
	    		<s:textfield name="org.name" class="form-control" placeholder="Sample Inc." required="true"/>
			</div>
		

		</div>
                  <div class="col-lg-4" >
                <div class="form-group">
            
  			 <img  class="responsive-img" id="upfile1" alt="click to upload" width="240" height="124"  style="cursor:pointer;line-height: 100px;text-align: center;background: #F8F8F8; "/>
                
                
		     </div>
		     <input type="file" id="file1"  name="org.photo" style="display:none" accept="image/*" />

       
        </div>
		
		<div class="col-lg-12 fc-dashed-line"></div>
		<div class="col-lg-6" >
			<div class="form-group">
	    		<label>street</label>
	    		<s:textfield name="org.street" class="form-control" placeholder="Street" required="true"/>
			</div>
		</div>
				<div class="col-lg-5" >
			<div class="form-group">
	    		<label>City</label>
	    		<s:textfield name="org.city" class="form-control" placeholder="City" required="true"/>
			</div>
			</div>
			
			<div class="col-lg-3" >
			<div class="form-group">
	    		
	    		<s:textfield name="org.state" class="form-control" placeholder="State" required="true"/>
			</div>
				<div class="form-group">
	    		<label>Phone</label>
	    		<s:textfield name="org.phone" class="form-control"  required="true"/>
	    	
			</div>

			<div class="form-group">
	    		<label>Language</label>
	    		<s:textfield name="org.language" class="form-control"  required="true"/>
	    	
			</div>

		</div>
		<div class="col-lg-3" >
			<div class="form-group">
	    		<s:textfield name="org.zipcode" class="form-control" placeholder="ZIP Code" required="true"/>
			</div>
					<div class="form-group">
	    		<label>Fax</label>
	    		<s:textfield name="org.fax" class="form-control"  required="true"/>
	    	
			</div>
						<div class="form-group">
	    		<label>Date format</label>
	    		<s:textfield name="org.dateformat" class="form-control" placeholder="" required="true"/>
	    	
			</div>

		</div>
		
			<div class="col-lg-5" >
			<div class="form-group">
			    <label>Select Country</label>
			    <select id="select-country" name="org.country" class="form-control">
			    	
			    </select>
			</div>
				<div class="form-group">
	    		<label>Mobile</label>
	    		<s:textfield name="org.mobile" class="form-control" required="true"/>
			</div>
				<div class="form-group">
	    		<label>Time Zone</label>
	    		<s:textfield name="org.timezone" class="form-control" placeholder=" " required="true"/>
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

.selectize-dropdown{
position: relative;
}
</style>

<script>
	$(document).ready(function() {
		
		FacilioApp.countryCombo('#select-country');
		
		$(".action-btn .save-btn").click(function() {
			$('#newRoleForm').submit();
		});
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#orgsettings';
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
						url : contextPath + "/app/setup/updateorgsettings",
						data : $("#newRoleForm").serialize(),
						done: function(data) {
							
							FacilioApp.notifyMessage('success', 'company settings updated successfully!');

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
			console.log(this.files[0].size);
			document.getElementById('upfile1').src = window.URL.createObjectURL(this.files[0]);
		});
	});

</script>