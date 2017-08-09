<div class="form-content form-container">
<form class="">
 <div class="row row-height">
    <section class="col-md-8">
        <!-- Content -->
        <h3 class="inline-header">User Sign Up and Login</h3>
         <div class="row row-height">
         <div class="col-xs-6">
        <!-- text -->
   		 <label class="inline-text">Allow users to Sign Up from the customer portal</label>
    	</div>
    	<div class="col-xs-6">
    	 <label class="radio-inline ">
     	 <input type="radio"  value="yes" name="user-login">Yes
   		 </label>
    	 <label class="radio-inline ">
    	 <input type="radio"  value="no" name="user-login">No
         </label>
    	</div>
		</div>
		 <div class="row row-height">
         <div class="col-xs-6">
        <!-- text -->
   		 <label class="inline-text">Allow users to Sign In using Google</label>
    	</div>
    	<div class="col-xs-6">
    	 <label class="radio-inline ">
     	 <input type="radio"  value="yes" name="google-login">Yes
   		 </label>
    	 <label class="radio-inline ">
    	 <input type="radio" value="no" name="google-login">No
         </label>
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
    	 <label class="radio-inline ">
     	 <input type="radio" value="user"  name="user-ticket-permissions">Logged in users
   		 </label>
    	 <label class="radio-inline ">
    	 <input type="radio" value="all"  name="user-ticket-permissions">Everyone
         </label>
    	</div>
		</div>
		 <div class="row row-height">
         <div class="col-xs-6">
        <!-- text -->
   		 <label class="inline-text">Who can view solutions</label>
    	</div>
    	<div class="col-xs-6">
    	 <label class="radio-inline ">
     	 <input type="radio" value="user" name="view-user-permissions">Logged in users
   		 </label>
    	 <label class="radio-inline ">
    	 <input type="radio" value="all" name="view-user-permissions">Everyone
         </label>
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
   		 <label class="inline-text">Who can log in (or) sign up (or) create tickets?</label>
    	</div>
    	<div class="col-xs-6">
    	 <label class="radio-inline ">
     	 <input type="radio" value="yes" name="whitelist-domain-radio">Users from any domain
   		 </label>
    	 <label class="radio-inline">
    	 <input class="display-input" type="radio" value="no" name="whitelist-domain-radio">Users from whitelisted domains
    	 </label>
    	 <input type="text" class="hidden-input"name="FirstName" >
         
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
	 		 $('.hidden-input').addClass('hidden-input1');

	 		 $('.hidden-input').removeClass('hidden-input');

		}
		else{
 		 $('.hidden-input1').addClass('hidden-input');
 		 $('.hidden-input').removeClass('hidden-input1');

		}
 		 
	});
	
	
});
</script>
