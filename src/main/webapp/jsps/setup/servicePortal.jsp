<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="form-content form-container">
<form id="service-form" class="service-form">
 <div class="row row-height">
    <section class="col-md-8">
        <!-- Content -->
        <h3 class="inline-header">User Sign Up and Login</h3>
         <div class="row row-height">
         <div class="col-xs-6">
   		 <label class="inline-text">Allow users to Sign Up from the customer portal</label>
    	</div>
    	<div class="col-xs-6">
    	<s:radio list="#{'true':'Yes','false':'No'}" name="setup.data.signupAllowed"  />
    	
    	</div>
		</div>
		 <div class="row row-height">
         <div class="col-xs-6">
        <!-- text -->
   		 <label class="inline-text">Allow users to Sign In using Google</label>
    	</div>
    	<div class="col-xs-6">
		<s:radio list="#{'true':'Yes','false':'No'}" name="setup.data.gmailLoginAllowed"  />

    	</div>
		</div>
    </section>
    
    <nav class="col-md-4">
        <!-- Navigation -->
    </nav>
</div>
<div class="col-lg-12 fc-dashed-line"></div>
 <div class="row row-height">
    <section class="col-md-8">
        <!-- Content -->
        <h3 class="inline-header">User Permissions</h3>
         <div class="row row-height">
         <div class="col-xs-6">
        <!-- text -->
   		 <label class="inline-text">Who can submit a new ticket on portal</label>
    	</div>
    	<div class="col-xs-6">
    			<s:radio list="#{'false':'Logged In Users','true':'Everyone'}" name="setup.data.ticketAlloedForPublic"  />
    	
    	 
          <div id="enable-captcha" class="hidden-input">
         <label class="radio-inline "> 
         <input	type="checkbox" name="captcha" value="captcha-enabled">Enable CAPTCHA to help avoid spam
		  </label>
		  </div>

		</div>
		</div>
	  </section>
    
    <nav class="col-md-4">
        <!-- Navigation -->
    </nav>
</div>
<div class="col-lg-12 fc-dashed-line"></div>
 <div class="row row-height">
    <section class="col-md-8">
        <!-- Content -->
        <h3 class="inline-header">Helpdesk Restrictions</h3>
		 <div class="row row-height">
         <div class="col-xs-6">
        <!-- text -->
   		 <label class="inline-text">Who can log in (or) sign up ?</label>
    	</div>
    	<div class="col-xs-6">
    	
       <s:radio  list="#{'true':'Any domain','false':'Whitelisted domains'}" name="setup.data.anyDomain" />
    	
    	 <input type="text" id="domain-name" class="hidden-input" name="domainName" placeholder="Enter Domain Name" >
         
    	</div>
		</div>
    </section>
    
    <nav class="col-md-4">
        <!-- Navigation -->
    </nav>
</div>
<div class="col-lg-12 fc-dashed-line"></div>
 <div id="" class="row row-height smal">
    <section class="col-md-9">
        <!-- Content -->
        <h3 class="inline-header">SAML authentication details</h3>
		 <div class="row row-height">
		 <div class="col-xs-9 ">
        
        <label class="inline-text text-right ">Login URL :</label>
        <input name="" type="url" class="pull-right form-control">
  		</div>
  		
		</div>
		 <div class="row row-height">
		 <div class="col-xs-9 ">
        
        <label class="inline-text text-right ">Logout URL :</label>
        <input type="url" class="pull-right form-control">
  		</div>
  		
		</div>
		 <div class="row row-height">
		 <div class="col-xs-9 ">
        
        <label class="inline-text text-right ">Change Password URL :</label>
        <input type="url" class="pull-right form-control" >
  		</div>
  		
		</div>
			 <div class="row row-height">
		 <div class="col-xs-9 ">
        
        <label class="inline-text text-right ">Public Key  :</label>
   		<input type="text" class="pull-right form-control" placeholder="Get Key from file" >

  		</div>
  		
		</div>
		 <div class="row row-height">
		 <div class="col-xs-9 ">
        
        <label class="inline-text text-right ">Algorithm  :</label>
        <select class="pull-right form-control" >
        <option>RSA</option>
        </select>
  		</div>
  		
		</div>
			 <div class="row row-height">
		 <div class="col-xs-9 ">
		 <button id="reset" class="btn btn-default btn-primary pull-right ">Reset</button>
  		</div>
  		
		</div>
	
	
	
		
    </section>
    

</div>

   </form>
</div>
<style>
.fc-dashed-line{

margin-top: 20px;
margin-bottom: 20px;
}
.hidden-input{
 display: none; 
}
.show-hidden-input{
display: block;
}
.fc-form{

background-color: white;


}
.form-content input[type=checkbox], .form-content input[type=radio]{
    margin-right: 5px;
    margin-top: 5px;
}
.checkbox input[type=checkbox], .checkbox-inline input[type=checkbox], .radio input[type=radio], .radio-inline input[type=radio]{
position: relative !important;
}
.checkbox-inline+.checkbox-inline, .radio-inline+.radio-inline{
margin-left: 0px;
}
label.inline-text{
margin-top: 5px;}
.row-height{
margin-top: 10px;
margin-bottom: 10px;
}
.inline-header{
margin-top: 10px;
}
..checkbox-inline, .radio-inline{
padding-left: 0px !important;
}
</style>
<script>
$(document).ready(function() {

	
$("input[name=setup\\.data\\.anyDomain]").change(function () {
	
	var val = ($(this).val());
	
 	if(val == "false"){
 		

 		 $('#domain-name').removeClass('hidden-input');

	}
	else{
		 $('#domain-name').addClass('hidden-input');

	}
		 
	
});

$("input[name=setup\\.data\\.ticketAlloedForPublic]").change(function () {
	
	var val = ($(this).val());
	
	if(val == "true"){
 		

 		 $('#enable-captcha').removeClass('hidden-input');

	}
	else{
		 $('#enable-captcha').addClass('hidden-input');

	}
		 
	
});
	

$(".action-btn .save-btn").click(function() {
	$('#service-form').submit();
});

$(".action-btn .cancel-btn").click(function() {
	location.href = '#servicePortal';
});
	
$('#service-form').validator().on('submit', function (e) {
	
	event.preventDefault();
	  console.log( $( this ).serialize() );
 	
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
				url : contextPath + "/app/setup/updateServicePortal",
				 data : $("#service-form").serialize(), 
				processData: false,
				contentType: false,
				done: function(data) {
					
					FacilioApp.notifyMessage('success', 'company settings updated successfully!');

				},
				fail: function(error) {
					$(".save-btn").button('reset');
					console.log(error);
					alert(error);
				} 
			});
			$(".save-btn").button('reset');
			return false;
	  	}
	
	 
	});


	
});
</script>
