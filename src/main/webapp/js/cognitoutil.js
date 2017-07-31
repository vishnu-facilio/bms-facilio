
var _REGION = "us-east-2";
//cognito
var _USER_POOL_ID = "us-west-2_kzN5KrMZU";
var _CLIENT_ID = "74d026sk7dde4vdsgpkhjhj17m";
var _POOL_DATA = {
    UserPoolId: _USER_POOL_ID,
    ClientId: _CLIENT_ID
};

var CognitoUtil = (function () {
    function CognitoUtil() {
    }
    
    CognitoUtil.prototype.getUserPool = function () {
        return new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(_POOL_DATA);
    };
    CognitoUtil.prototype.getCurrentUser = function () {
        return this.getUserPool().getCurrentUser();
    };
    CognitoUtil.prototype.getTokens = function (callback) {
    	
    	if (this.getCurrentUser() == null) {
    		callback(null, null);
    	}
    	else {
    		this.getCurrentUser().getSession(function (err, session) {
                if (err) {
                	callback(null, err);
                }
                else {
                    if (session.isValid()) {
                    	var tokens = {};
                    	tokens.idToken = session.getIdToken().getJwtToken();
                    	tokens.accessToken = session.getAccessToken().getJwtToken();
                    	tokens.refreshToken = session.getRefreshToken();
                        callback(tokens, null);
                    }
                    else {
                    	callback(null, null);
                    }
                }
            });
    	}
    };
    CognitoUtil.prototype.logout = function () {
    	if (this.getUserPool().getCurrentUser() != null) {
    		return this.getUserPool().getCurrentUser().signOut();
    	}
    	return true;
    };
    
    return CognitoUtil;
}());