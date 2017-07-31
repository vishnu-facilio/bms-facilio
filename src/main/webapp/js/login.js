// display signup block
function displaysignup()
{
	reset();
	document.getElementById("signupform").style.display  = "block";
}

//display signin block
function displaysignin()
{
	reset();
	document.getElementById("signinform").style.display  = "block";
}

// display verification block
function showVerificationForm()
{
	reset();	
	document.getElementById("verify_user").style.display  = "block";
}

// reset the view
function reset()
{
	document.getElementById("signupform").style.display  = "none";
	document.getElementById("signinform").style.display  = "none";
	document.getElementById("verify_user").style.display = "none";

}

// AWS cognito pool data
var poolData = {
        UserPoolId : 'us-west-2_kzN5KrMZU', // Your user pool id here
        ClientId : '74d026sk7dde4vdsgpkhjhj17m' // Your client id here
};

// sign up a user
function signup(email,password)
{

    var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);

    var attributeList = [];

    var dataEmail = {
        Name : 'email',
        Value : email
    };

    
    var attributeEmail = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataEmail);

  	// var attributePhoneNumber = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataPhoneNumber);

    attributeList.push(attributeEmail);
 	// attributeList.push(attributePhoneNumber);

    userPool.signUp(email, password, attributeList, null, function(err, result){
        if (err) {
            alert("error: "+err);
            return;
        }
        cognitoUser = result.user;
    //    alert('user name is' + cognitoUser.getUsername());
        signin(email,password);
        //TODO
    });

    // display verification form after sign up
   // showVerificationForm();
}

function signin(email,password)
{
	var authenticationData = {
		Username : email,
	    Password : password,
	};

	var authenticationDetails = new AWSCognito.CognitoIdentityServiceProvider.AuthenticationDetails(authenticationData);
	   
	var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);
	var userData = {
	    Username : email,
	    Pool : userPool
	};

	$(".signupbtn").text("Signing In...").attr("disabled", true);
	
	var cognitoUser = new AWSCognito.CognitoIdentityServiceProvider.CognitoUser(userData);
	cognitoUser.authenticateUser(authenticationDetails, {
	    onSuccess: function (result) {
	    	
	    	// this response has three token Id,access and refresh
	    	// id token basically contains the identity information, access token will not 
	    	// be needed for authenticating the user.
	    	
	    	idToken = result.idToken.jwtToken;
	    	accessToken = result.getAccessToken().getJwtToken();
	    	
	        //pass on the idToken now to server side for validation
	    	
	    	var isSAML = false;
	    	if (window.location.href.indexOf("SAMLRequest=") != -1) {
	    		isSAML = true;
	    	}
	    	
	    	 $.post( "validateuser", { "idToken": idToken, "accessToken": accessToken, "isSAML": isSAML })
		        .done(function( data ) {
		        	//alert(data)
		        	if(data.startsWith("http"))
		        	{
		        		data = data + location.hash;
		        		window.location.replace(data);
		        	}
		        	else if(data.startsWith("reload"))
		        	{
		        		window.location.reload();
		        	}
		        	else if(data.indexOf("unverified_user")>-1){
		        		alert("pls verify the username");
		        	}
		        });
	        
	        
	    },

	    newPasswordRequired: function(userAttributes, requiredAttributes) {
	    	
	    	$("#signinform form").hide();
	    	$(".change-password").show();
	    	
	    	var scope = this;
	    	$(".updatepwdbtn").click(function() {
	    		
	    		var newPass = $("input[name=change-newpass]").val();
	    		var confirmNewPass = $("input[name=change-confirm-newpass]").val();
	    		
	    		console.log(newPass);
	    		if (newPass != confirmNewPass) {
	    			alert('Password does not match..');
	    		}
	    		else {
	        		delete userAttributes.email_verified;
	        		
	        		cognitoUser.completeNewPasswordChallenge(newPass, userAttributes, scope);
	    		}
	    	});
        },
	    
	    onFailure: function(err) {
	        
	    	$(".signupbtn").text("Sign In").attr("disabled", false);
	    	
	    	if (err.__type === 'UserNotConfirmedException') {
	    		$("#signinform form").hide();
		    	$(".verifyuser-section").show();
		    	
		    	$(".verifybtn").click(function() {
		       		
		       		var vcode = $("input[name=verification_code]").val();
		       		if (vcode.trim() == '') {
		       			alert('Please enter valid verification code.');
		       		}
		       		else {
		       			cognitoUser.confirmRegistration(vcode.trim(), true, function(err, result) {
		           	        if (err) {
		           	            alert(err);
		           	            return;
		           	        }
		           	        console.log('call result: ' + result);
		           	        
		           	        alert('Your account verified. You can login to your account now.');
		           	        location.href = "login";
		           	        
			           	    });
		       			}
		       	});
		    	
		    	$(".resend-code").click(function() {
		    		cognitoUser.resendConfirmationCode(function(err, result) {
			            if (err) {
			                alert(err);
			                return;
			            }
			            alert('Verification code sent to your mailbox.');
		    		});
		        });
	    	}
	    	else {
	    		alert(err.message);
	    	}
	    },

	});	
}

function forgotPassword() {
	
	$("#signinform form").hide();
	$(".forgot-password").show();
	
	var codeSent = false;
	$(".resetpwdbtn").click(function() {
		
		var email = $("input[name=forgot-email]").val();
		if (email.trim() === "") {
			alert('Please enter valid email address');
			return;
		}
		
		var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);
		var userData = {
		    Username : email,
		    Pool : userPool
		};

		var cognitoUser = new AWSCognito.CognitoIdentityServiceProvider.CognitoUser(userData);
		
		if (!codeSent) {
			cognitoUser.forgotPassword({
		        onSuccess: function () {
		            // successfully initiated reset password request
		        },
		        onFailure: function(err) {
		            alert(err);
		        },
		        //Optional automatic callback
		        inputVerificationCode: function(data) {
		            console.log('Code sent to: ' + data);
		            alert('Verification code sent to '+data.CodeDeliveryDetails.Destination);
		            
		            codeSent = true;
		            
		            $("input[name=forgot-email]").hide();
		            $("input[name=forgot-code]").show();
		            $("input[name=forgot-newpass]").show();
		            $("input[name=forgot-code]").focus();
		        }
		    });
		}
		else {
			var verificationCode = $("input[name=forgot-code]").val();
            var newPassword = $("input[name=forgot-newpass]").val();
            
			cognitoUser.confirmPassword(verificationCode, newPassword, this);
			
			alert('Password reset successfully. You can login to your account now.');
			location.href = "login";
		}
	});
}

// confirm user - verification
function confirmUser(username, code) {

	console.log(username);
	var verifyData = {
	  Username : username,
	  code : code,
	};
	console.log(verifyData);

	var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);
	
    var userData = {
       Username: username,
       Pool: userPool
    };

    var cognitoUser = new AWSCognito.CognitoIdentityServiceProvider.CognitoUser(userData);

    cognitoUser.confirmRegistration(code, true, function (err, result) {
       if (err) {
          console.log(err);
          return;
       }
    });

    // display sign in form after user verification is complete
    displaysignin();
 }