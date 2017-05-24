// AWS cognito user pool identity
var poolData = {
        UserPoolId : 'us-west-2_kzN5KrMZU', // pool id
        ClientId : '74d026sk7dde4vdsgpkhjhj17m' // client id here
};

function registerUser(){

	//triggered when signup event is invoked.
	//register an user in aws cognito user pool and set the org_name as an attribute to the user name.

	var userName = document.getElementById("user_email").value;
	var orgName = document.getElementById("account_domain").value;
	var password = document.getElementById("user_password").value;
    var phoneNumber = document.getElementById("user_phone").value;

	var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);

    // attribute list - custom and predefined
	var attributeList = [];

    //e-mail
	var dataEmail = {
        Name : 'email',
        Value : userName,
    };
    var attributeEmail = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataEmail);

    //domain-name
    var dataOrgName = {
        Name : 'custom:orgName',
        Value: orgName,
    }
    var attributeOrgName = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataOrgName);

    //phone number
    var dataPhone = {
        Name : 'phone_number',
        Value : phoneNumber,
    }
    var attributePhoneNumber = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataPhone);

	attributeList.push(attributeEmail);
    attributeList.push(attributeOrgName);
    attributeList.push(attributePhoneNumber);

	console.log(attributeList);

	userPool.signUp(userName, password, attributeList, null, function(error, result){

        if (error){
        	alert(error);
        	return;
        }

        // get the registered user
       	cognitoUser = result.user;
    });
}