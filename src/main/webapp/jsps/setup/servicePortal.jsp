<%@ taglib uri="/struts-tags" prefix="s" %>    
<div class="form-content form-container">
<form class="">
 <div class="row row-height">
    <section class="col-md-8">
        <!-- Content -->
        <h3 class="inline-header">User Sign Up and Login</h3>
         <div class="row row-height">
         <div class="col-xs-6">
   		 <label class="inline-text">Allow users to Sign Up from the customer portal</label>
    	</div>
    	<div class="col-xs-6">
    	<s:radio list="#{'true':'Yes','false':'No'}" name="setup.data.signupAllowed" cssClass="radio-inline" />
    	
    	</div>
		</div>
		 <div class="row row-height">
         <div class="col-xs-6">
        <!-- text -->
   		 <label class="inline-text">Allow users to Sign In using Google</label>
    	</div>
    	<div class="col-xs-6">
		<s:radio list="#{'true':'Yes','false':'No'}" name="setup.data.gmailLoginAllowed" cssClass="radio-inline" />

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
    			<s:radio list="#{'false':'Logged In Users','true':'Everyone'}" name="setup.data.ticketAlloedForPublic" cssClass="radio-inline" />
    	
    	 
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
    	
       <s:radio list="#{'true':'Any domain','false':'Whitelisted domains'}" name="setup.data.anyDomain" cssClass="radio-inline" />
    	
    	 <input type="text" id="domain-name" class="hidden-input" name="domainName" placeholder="Enter Domain Name" >
         
    	</div>
		</div>
    </section>
    
    <nav class="col-md-4">
        <!-- Navigation -->
    </nav>
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
</style>
<script>
$(document).ready(function() {
	
	$("input[name=whitelist-domain-radio]").change(function() {
		var val = ($(this).val());
		if(val == "no"){
	 		

	 		 $('#domain-name').removeClass('hidden-input');

		}
		else{
 		 $('#domain-name').addClass('hidden-input');

		}
 		 
	});
	$("input[name=user-ticket-permissions]").change(function() {
		var val = ($(this).val());
		if(val == "all"){
	 		

	 		 $('#enable-captcha').removeClass('hidden-input');

		}
		else{
 		 $('#enable-captcha').addClass('hidden-input');

		}
 		 
	});
	
	
});
</script>
