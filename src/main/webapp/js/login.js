function displaysignup()
{
	reset();
	document.getElementById("signupform").style.display  = "block";


}

function displaysignin()
{
	reset();
	document.getElementById("signinform").style.display  = "block";

}
function showDefault()
{
reset();	
//document.getElementById("login").style.display  = "block";

}
function reset()
{
	document.getElementById("signupform").style.display  = "none";
	document.getElementById("signinform").style.display  = "none";

	}
var poolData = {
        UserPoolId : 'us-west-2_kzN5KrMZU', // Your user pool id here
        ClientId : '74d026sk7dde4vdsgpkhjhj17m' // Your client id here
    };
function signup(email,password)
{
   // var CognitoUserPool = AmazonCognitoIdentity.CognitoUserPool;

alert(email);
alert(password);

    var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);
    alert(userPool)	;

    var attributeList = [];

    var dataEmail = {
        Name : 'email',
        Value : email
    };

    
    var attributeEmail = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataEmail);
  //  var attributePhoneNumber = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataPhoneNumber);

    attributeList.push(attributeEmail);
 //   attributeList.push(attributePhoneNumber);
    alert("before signup triggered");

    userPool.signUp(email, password, attributeList, null, function(err, result){
        if (err) {
            alert("erro"+err);
            return;
        }
        cognitoUser = result.user;
        alert('user name is ' + cognitoUser.getUsername());
    });


}

function signin(email,password)
{
	var authenticationData = {
	        Username : email,
	        Password : passsord,
	    };
	alert(authenticationData);
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

	            AWS.config.credentials = new AWS.CognitoIdentityCredentials({
	                IdentityPoolId : '...', // your identity pool id here
	                Logins : {
	                    // Change the key below according to the specific region your user pool is in.
	                    'cognito-idp.us-west-2.amazonaws.com/us-west-2_kzN5KrMZU' : result.getIdToken().getJwtToken()
	                }
	            });

	            // Instantiate aws sdk service objects now that the credentials have been updated.
	            // example: var s3 = new AWS.S3();

	        },

	        onFailure: function(err) {
	            alert(err);
	        },

	    });	
}