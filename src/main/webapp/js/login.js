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
	        console.log('access token + ' + result.getAccessToken().getJwtToken());
	        },

	        // This block does nothing with respect to login, this has to updated according to the requirement
	        //AWS.config.credentials = new AWS.CognitoIdentityCredentials({
	        //    IdentityPoolId : 'us-west-2_kzN5KrMZU', // your identity pool id here
	        //    Logins : {
	                	// Change the key below according to the specific region your user pool is in.
	        //        	'cognito-idp.us-west-2.amazonaws.com/us-west-2_kzN5KrMZU' : result.getIdToken().getJwtToken()
	        //        }
	        //    });

	            // Instantiate aws sdk service objects now that the credentials have been updated.
	            // example: var s3 = new AWS.S3();
	        // },

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