<%@page import="com.facilio.aws.util.FacilioProperties"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome to facilio</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
</head>

<style>

.login-page {
  width: 360px;
  padding: 8% 0 0;
  margin: auto;
}
.form {
  position: relative;
  z-index: 1;
  background: #FFFFFF;
  max-width: 360px;
  margin: 0 auto 100px;
  padding: 45px;
  text-align: center;
  box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);
}
.form input {
  font-family: "Roboto", sans-serif;
  outline: 0;
  background: #f2f2f2;
  width: 100%;
  border: 0;
  margin: 0 0 15px;
  padding: 15px;
  box-sizing: border-box;
  font-size: 14px;
}
.form button {
  font-family: "Roboto", sans-serif;
  text-transform: uppercase;
  outline: 0;
  background: #4CAF50;
  width: 100%;
  border: 0;
  padding: 15px;
  color: #FFFFFF;
  font-size: 14px;
  -webkit-transition: all 0.3 ease;
  transition: all 0.3 ease;
  cursor: pointer;
}
.form button:hover,.form button:active,.form button:focus {
  background: #43A047;
}
.form .message {
  margin: 15px 0 0;
  color: #b3b3b3;
  font-size: 12px;
}
.form .message a {
  color: #4CAF50;
  text-decoration: none;
}
.form .register-form {
  display: none;
}
.container {
  position: relative;
  z-index: 1;
  max-width: 300px;
  margin: 0 auto;
}
.container:before, .container:after {
  content: "";
  display: block;
  clear: both;
}
.container .info {
  margin: 50px auto;
  text-align: center;
}
.container .info h1 {
  margin: 0 0 15px;
  padding: 0;
  font-size: 36px;
  font-weight: 300;
  color: #1a1a1a;
}
.container .info span {
  color: #4d4d4d;
  font-size: 12px;
}
.container .info span a {
  color: #000000;
  text-decoration: none;
}
.container .info span .fa {
  color: #EF3B3A;
}
body {
  background: #76b852; /* fallback for old browsers */
  background: -webkit-linear-gradient(right, #76b852, #8DC26F);
  background: -moz-linear-gradient(right, #76b852, #8DC26F);
  background: -o-linear-gradient(right, #76b852, #8DC26F);
  background: linear-gradient(to left, #76b852, #8DC26F);
  font-family: "Roboto", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;      
}
</style>


<body>
<div class="login-page">
  <div class="form">
  
    <form class="register-form" method="POST"  id="signupform">
    		<input type="text" id="emailaddress" placeholder="emailaddress" name="username"/>
      <input type="password" id="password" placeholder="password" name="password"/>
      <input type="password" id="confirmPassword" placeholder="confirmPassword"/>    
      <button onclick="signup(this.form)">create</button>
      <p class="message">Already registered? <a href="#">Sign In</a></p>
    </form>
        
    
    <form class="login-form" method="POST"  id="target">
      <input type="text" placeholder="username" name="username"/>
      <input type="password" placeholder="password" name="password"/>
      <button type="submit">login</button>
      <p class="message">Not registered? <a href="#" id="createacct">Create an account</a></p>
    </form>
  </div>
</div>
</body>
</html>

<script>
function signup(frm)
{
	var pass = 	document.getElementById("password").value;
	var confirmpass = document.getElementById("confirmPassword").value;
	if(pass != confirmpass)
		{
			alert("Password not matched with ConfirmPassword");
			return false;
		}
	//var jsonStr = {"username" : document.getElementById("emailaddress").value, "password" : document.getElementById("password").value,"emailaddress" : document.getElementById("emailaddress").value };
	frm.submit();
}
</script>

<script type="text/javascript">
   
   
    $('.message a').click(function(){
    	   $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
    	});
    
    $( "#signupform" ).submit(function( event ) {
   // 	alert( "Handler for .submit() called." );
   	$.ajax({
			  type: "POST",
			  url: 'apisignup',
			  data: $("#signupform").serialize(),
			  success: handlelogin,
			  error: function (jqXHR, exception) {
			        var msg = '';
			        if (jqXHR.status === 0) {
			            msg = 'Not connect.\n Verify Network.';
			        } else if (jqXHR.status == 404) {
			            msg = 'Requested page not found. [404]';
			        } else if (jqXHR.status == 500) {
			            msg = 'Internal Server Error [500].';
			        } else if (exception === 'parsererror') {
			            msg = 'Requested JSON parse failed.';
			        } else if (exception === 'timeout') {
			            msg = 'Time out error.';
			        } else if (exception === 'abort') {
			            msg = 'Ajax request aborted.';
			        } else {
			            msg = 'Uncaught Error.\n' + jqXHR.responseText;
			        }
			        console.log(msg);
			        console.log(jqXHR);
			        console.log(exception);
			    }
			});
   	  event.preventDefault();
   	});  
    
    $( "#target" ).submit(function( event ) {
    	 // alert( "Handler for .submit() called." );
    	  $.ajax({
    		  type: "POST",
    		  url: 'faciliosubmit',
    		  data: $("#target").serialize(),
    		  success: handlelogin
    		});
    	  event.preventDefault();
    	});  
    
  var handlelogin =   function handleloginsubmit(response)
    {
    		//alert(JSON.stringify(response))
    		console.log("$$$$$$$ response "+response)
    		location.href = '<%= FacilioProperties.getConfig("clientapp.url")%>/app/wo';
    		
    }
</script>