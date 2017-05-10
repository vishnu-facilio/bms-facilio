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
        alert('user name is' + cognitoUser.getUsername());
    });

    // display verification form after sign up
    showVerificationForm();
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

	var cognitoUser = new AWSCognito.CognitoIdentityServiceProvider.CognitoUser(userData);
	
	cognitoUser.authenticateUser(authenticationDetails, {
	    onSuccess: function (result) {
	    	
	    	// this response has three token Id,access and refresh
	    	// id token basically contains the identity information, access token will not 
	    	// be needed for authenticating the user.
	    	
	    	idToken = result.idToken.jwtToken;
	    	accessToken = result.getAccessToken().getJwtToken();
	    	
	        //pass on the idToken now to server side for validation
	    	
	    	var http = new XMLHttpRequest();
	        console.log('openeing a html request');
	        http.open("POST","login/validate?idToken="+idToken,true);
	        http.send();
	    },

	    onFailure: function(err) {
	        alert(err);
	    },

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