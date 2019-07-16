package com.iam.accounts.actions;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;
import com.iam.accounts.util.AuthUtill;
import com.iam.accounts.util.OrgUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class AuthAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(AuthAction.class.getName());

	private String username = null;
	private String password = null;
	private String inviteToken = null;
	private Map jsonresponse = new HashMap();
	private JSONObject signupData;
	private String response = null;
	private String emailaddress;
	private HashMap<String, Object> account;
	private String phone;
	private String companyname;
	private String domainname;
	private String timezone;
	private String newPassword;
	private String title;
	private static MessageDigest md;
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	JSONArray entry;

	public JSONArray getEntry() {
		return entry;
	}

	public void setEntry(JSONArray entry) {
		this.entry = entry;
	}

	public String getUsername() {
		if (username != null) {
			return username;
		} else {
			return getEmailaddress();
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = cryptWithMD5(password);
	}

	public String getInviteToken() {
		return inviteToken;
	}

	public void setInviteToken(String inviteToken) {
		this.inviteToken = inviteToken;
	}

	public Map<String, Object> getJsonresponse() {
		return jsonresponse;
	}

	public void setJsonresponse(Map<String, Object> jsonresponse) {
		this.jsonresponse = jsonresponse;
	}

	public JSONObject getSignupData() {
		return signupData;
	}

	public void setSignupData(JSONObject signupData) {
		this.signupData = signupData;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getEmailaddress() {
		if (emailaddress != null) {
			return emailaddress;
		} else {
			if (username == null) {
				return null;
			}
			return getUsername();
		}
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public HashMap<String, Object> getAccount() {
		return account;
	}

	public void setAccount(HashMap<String, Object> account) {
		this.account = account;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = cryptWithMD5(newPassword);
	}

	public static MessageDigest getMd() {
		return md;
	}

	public static void setMd(MessageDigest md) {
		AuthAction.md = md;
	}

	private void setJsonresponse(String key, Object value) {
		this.jsonresponse.put(key, value);
	}

	private String getTitle() {
		return title;
	}

	private void setTitle(String title) {
		this.title = title;
	}

	public String loadWebView() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String authtoken = request.getParameter("authtoken");
		String serviceurl = request.getParameter("serviceurl");
		String parentdomain = request.getServerName().replaceAll("app.", "");

		Cookie cookie = new Cookie("fc.idToken.facilio", authtoken);
		cookie.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		if (!(AwsUtil.isDevelopment() || AwsUtil.disableCSP())) {
			cookie.setSecure(true);
		}
		cookie.setDomain(parentdomain);
		response.addCookie(cookie);

		Cookie authmodel = new Cookie("fc.authtype", "facilio");
		authmodel.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
		authmodel.setPath("/");
		authmodel.setHttpOnly(false);
		if (!(AwsUtil.isDevelopment() || AwsUtil.disableCSP())) {
			authmodel.setSecure(true);
		}
		authmodel.setDomain(parentdomain);
		LOGGER.info("#################### facilio.in::: " + request.getServerName());
		response.addCookie(authmodel);

		Cookie token = new Cookie("fc.idToken", "facilio");
		token.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
		token.setPath("/");
		token.setHttpOnly(false);
		if (!(AwsUtil.isDevelopment() || AwsUtil.disableCSP())) {
			token.setSecure(true);
		}
		token.setDomain(request.getServerName());
		response.addCookie(token);

		try {
			response.sendRedirect(serviceurl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public String v2apiLogout() throws Exception {
		apiLogout();
		return SUCCESS;
	}

	public String signupUserv2() throws Exception {

		LOGGER.info("signupUser() : username :" + getUsername() + ", password :" + getPassword() + ", email : "
				+ getEmailaddress());
		return addFacilioUserv2(getUsername(), getPassword(), getEmailaddress());
	}

	private String addFacilioUserv2(String username, String password, String emailaddress) throws Exception {
		User userObj = AuthUtill.getUserBean().getFacilioUserv2(emailaddress);
		if (userObj != null) {
			setJsonresponse("message", "Email already exists");
			return ERROR;
		}

		Organization orgObj = AuthUtill.getOrgBean().getOrgv2(getDomainname());

		if (orgObj != null) {
			setJsonresponse("message", "Org Domain Name already exists");
			return ERROR;
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		LOGGER.info("### addFacilioUser() :" + emailaddress);

		JSONObject signupInfo = new JSONObject();
		signupInfo.put("name", getUsername());
		signupInfo.put("email", emailaddress);
		signupInfo.put("cognitoId", "facilio");
		signupInfo.put("phone", getPhone());
		signupInfo.put("companyname", getCompanyname());
		signupInfo.put("domainname", getDomainname());
		signupInfo.put("isFacilioAuth", true);
		signupInfo.put("timezone", getTimezone());
		signupInfo.put("servername", request.getServerName());
		signupInfo.put("password", password);
		
		OrgUtil.signUpOrg(signupInfo);
		setJsonresponse("message", "success");
		return SUCCESS;
	}

	

	public String verifyTokenv2() {

		CognitoUser user = AuthUtill.verifiyFacilioToken(getToken(), false, false);
		if (user == null) {
			setJsonresponse("message", "error");

			return ERROR;
		}
		setJsonresponse("message", "success");

		return SUCCESS;
	}

	public static String cryptWithMD5(String pass) {
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] passBytes = pass.getBytes();
			md.reset();
			byte[] digested = md.digest(passBytes);
			StringBuilder sb = new StringBuilder();
			for (byte aDigested : digested) {
				sb.append(Integer.toHexString(0xff & aDigested));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.log(Level.INFO, "Exception ", ex);
		}
		return null;
	}

	public String generateAuthToken() {
		LOGGER.info("generateAuthToken() : username :" + getUsername());
		Boolean passwordVerified = false;
		boolean isportaluser = false;

		passwordVerified = verifyPasswordv2(getUsername(), getPassword());
		if (passwordVerified != null && passwordVerified) {
			String jwt = CognitoUtil.createJWT("id", "auth0", getUsername(),
					System.currentTimeMillis() + 24 * 60 * 60000, isportaluser);
			LOGGER.info("Response token is " + jwt);
			setJsonresponse("authtoken", jwt);
			setJsonresponse("username", getUsername());
		} else {
			setJsonresponse("message", "Invalid username / password");
		}
		return SUCCESS;
	}

	private Boolean verifyPasswordv2(String emailaddress, String password) {
		boolean passwordValid = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(
					"SELECT Users.password,Users.EMAIL FROM Users WHERE Users.EMAIL = ? and USER_VERIFIED=1");
			pstmt.setString(1, emailaddress);
			pstmt.setString(2, emailaddress);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String storedPass = rs.getString("password");
				String emailindb = rs.getString(2);
				this.emailaddress = emailindb;
				LOGGER.info("Stored : " + storedPass);
				LOGGER.info("UserGiv: " + password);
				if (storedPass.equals(password)) {
					passwordValid = true;
				}
			} else {
				LOGGER.log(Level.INFO, "No records found for  " + emailaddress);
				return null;
			}

		} catch (SQLException | RuntimeException e) {
			LOGGER.log(Level.INFO, "Exception while verifying password, ", e);
		} finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}

		return passwordValid;
	}

	public String apiLogout() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();

		session.invalidate();

		String parentdomain = request.getServerName().replaceAll("app.", "");

//		if (portalId() > 0) {
//			FacilioCookie.eraseUserCookie(request, response, "fc.idToken.facilioportal", parentdomain);
//		} else {
		FacilioCookie.eraseUserCookie(request, response, "fc.idToken.facilio", parentdomain);
		// }

		FacilioCookie.eraseUserCookie(request, response, "fc.authtype", null);

		return SUCCESS;
	}

	public String verifyEmailv2() throws Exception {

		JSONObject invitation = new JSONObject();
		User user = AuthUtill.getUserBean().verifyEmailv2(getInviteToken());
		if (user == null) {
			invitation.put("error", "link_expired");
		} else {
			invitation.put("email", user.getEmail());
			invitation.put("accepted", true);
		}
		ActionContext.getContext().getValueStack().set("invitation", invitation);

		return SUCCESS;
	}
	
	 public String validateInviteLinkv2() throws Exception {

	        JSONObject invitation = new JSONObject();
	        User user = AuthUtill.getUserBean().validateUserInvitev2(getInviteToken());
	        if(AccountUtil.getCurrentOrg().getId()==75) {
	        LOGGER.info("validate user link email invitation"+user.getEmail());
	        LOGGER.info("validate user link username  invitation"+user.getName());
	        }
	        if (user != null) {
	            Organization org = AccountUtil.getOrgBean().getOrg(user.getOrgId());
	            invitation.put("email", user.getEmail());
	            invitation.put("orgname", org.getName());
	            if(user.getPassword() == null) {
	                invitation.put("account_exists", false);
	            } else {
	                invitation.put("account_exists", true);
	            }
	            invitation.put("userid", user.getOuid());
	        } else {
	            invitation.put("error", "link_expired");
	        }
	    	ActionContext.getContext().getValueStack().set("invitation", invitation);

	    	return SUCCESS;
	    }
	 
	 public String acceptUserInvitev2() throws Exception {
	        boolean status = AuthUtill.getTransactionalUserBean().acceptInvitev2(getInviteToken(), getPassword());
	        if(status){
	            return SUCCESS;
	        }
	        return ERROR;
	    }

}
