package com.facilio.integration.actions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.LoginAction;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.opensymphony.xwork2.ActionSupport;

public class Home extends ActionSupport {
/*
 * redirect_uri = https://platform.zoho.com/auth/conns/urjanet/redirect
response_type = code
state = PORTAL-yogendrababu
 */
private static Logger log = LogManager.getLogger(Home.class.getName());

	public String loginSubmit()
	{
		System.out.println(redirect_uri + state + response_type);
		return SUCCESS;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		System.out.println("setting user name" + username);
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	private String username = null;
	private String password = null;
	private String inviteToken = null;
	
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
	private String phone;
	private String companyname;
	private String domainname;
	private String timezone;

	public String getInviteToken() {
		return inviteToken;
	}

	public void setInviteToken(String inviteToken) {
		this.inviteToken = inviteToken;
	}

	public Map getJsonresponse() {
		return jsonresponse;
	}
	public void setJsonresponse(Map jsonresponse) {
		this.jsonresponse = jsonresponse;
	}
	public void setJsonresponse(String key, Object value) {
		this.jsonresponse.put(key, value);
	}
	private Map jsonresponse = new HashMap();

	private String newPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public long portalId() {
		if(AccountUtil.getCurrentOrg() != null) {
			return AccountUtil.getCurrentOrg().getPortalId();
		}
		return -1l;
	}

	public String changePassword() throws Exception {
		User user = AccountUtil.getCurrentUser(); 
		String email = user.getEmail();
		boolean verifyOldPassword = verifyPassword(email, cryptWithMD5(password));
		if(verifyOldPassword) {
			String encryptedPassword = Home.cryptWithMD5(getNewPassword());
			user.setPassword(encryptedPassword);
			AccountUtil.getUserBean().updateUser(user);
			setJsonresponse("message", "Password changed successfully");
			setJsonresponse("status", "success");
			return SUCCESS;
		} else {
			setJsonresponse("message", "Current Password is incorrect");
			setJsonresponse("status", "failure");
			return ERROR;
		}
	}

	public String changePortalPassword() throws Exception {
		boolean verifyOldPassword = verifyPortalPassword(getEmailaddress(), cryptWithMD5(password), portalId());
		if(verifyOldPassword) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			try {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement("UPDATE faciliorequestors SET password = ? WHERE email= ? and PORTALID = ?");
				pstmt.setString(1, cryptWithMD5(newPassword));
				pstmt.setString(2, emailaddress);
				pstmt.setLong(3, portalId());
				pstmt.executeUpdate();

			} catch(SQLException | RuntimeException e) {
				log.info("Exception occurred ", e);
				throw e;
			} finally {
				DBUtil.closeAll(conn, pstmt);
			}
			setJsonresponse("message", "Password changed successfully");
			setJsonresponse("status", "success");
			return SUCCESS;
		} else {
			setJsonresponse("message", "Current Password is incorrect");
			setJsonresponse("status", "failure");
			return ERROR;
		}
	}


	public String validatelogin() throws Exception
	{
		boolean portaluser=false;
		if(username != null && password != null) {
			try {
				System.out.println("validatelogin() : username : " + username + "; password : " + password + " portal id : " + portalId());
				String encryptedPass = cryptWithMD5(password);
				boolean validPassword = false;
				if (portalId() > 0) {
					validPassword = verifyPortalPassword(username, encryptedPass, portalId());
					portaluser =true;
				} else {
					validPassword = verifyPassword(username, encryptedPass);
				}
				if (!validPassword) {
					System.out.print(">>>>> verifyPassword :" + username);
					setJsonresponse("errorcode", "1");
					return ERROR;
				}
				String jwt = CognitoUtil.createJWT("id", "auth0", username, System.currentTimeMillis() + 24 * 60 * 60000,portaluser);
				System.out.println("Response token is " + jwt);
				setJsonresponse("token", jwt);
				setJsonresponse("username", username);

				HttpServletRequest request = ServletActionContext.getRequest();
				HttpServletResponse response = ServletActionContext.getResponse();

				Cookie cookie = new Cookie("fc.idToken.facilio", jwt);
				if(portaluser)
				{
					cookie = new Cookie("fc.idToken.facilioportal", jwt);
				}
				cookie.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
				cookie.setPath("/");
				cookie.setHttpOnly(true);
//		cookie.setDomain(request.getServerName());
				response.addCookie(cookie);

				Cookie authmodel = new Cookie("fc.authtype", "facilio");
				authmodel.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
				authmodel.setPath("/");
				authmodel.setHttpOnly(false);
				String parentdomain = request.getServerName().replaceAll("api.", "");
		authmodel.setDomain(parentdomain);
				System.out.println("#################### facilio.in::: " + request.getServerName());
				response.addCookie(authmodel);
				
				long uid = AccountUtil.getUserBean().getFacilioUser(username).getUid();
				String userAgent = request.getHeader("User-Agent");
				userAgent = userAgent != null ? userAgent : "";
				AccountUtil.getUserBean().startUserSession(uid, username, jwt, request.getRemoteAddr(), userAgent);
			} catch (Exception e) {
				log.info("Exception occurred ", e);
				setJsonresponse("message", "Error while validating user name and password");
				return ERROR;
			}
			return SUCCESS;
		}
		setJsonresponse("message", "Invalid username or password");
		return ERROR;
	}
/*
 * HTTP/1.1 200 OK
Content-Type: application/json
Cache-Control: no-store
Pragma: no-cache
 
{
  "access_token":"MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
  "token_type":"bearer",
  "expires_in":3600,
  "refresh_token":"IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk",
  "scope":"create",
  "state":"12345678"
}
 */
	public boolean verifyPassword(String emailaddress, String password) throws Exception
	{
		boolean userExists = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT password FROM Users WHERE ( email = ? or username=? ) and password=? and USER_VERIFIED=1");
			pstmt.setString(1, emailaddress);
			pstmt.setString(2, emailaddress);

			pstmt.setString(3, password);			
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				String storedPass = rs.getString("password");
				System.out.println("Stored : "+storedPass);
				System.out.println("UserGiv: "+password);
				if(storedPass.equalsIgnoreCase(password))
				{
					userExists = true;
				}
			}
				
		}catch(SQLException | RuntimeException e)
		{
			log.info("Exception occurred ", e);
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt);
		}
		
		return userExists;
	}

	private boolean verifyPortalPassword(String emailaddress, String password, long portalId) throws Exception {
		boolean userExists = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT password FROM faciliorequestors WHERE email = ? and PORTALID = ?");
			pstmt.setString(1, emailaddress);
			pstmt.setLong(2, portalId());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				String storedPass = rs.getString("password");
				System.out.println("Stored : "+storedPass);
				System.out.println("UserGiv: "+password);
				if(storedPass.equalsIgnoreCase(password)) {
					userExists = true;
				}
			}
		} catch(SQLException | RuntimeException e) {
			throw e;
		} finally {
			DBUtil.closeAll(conn, pstmt);
		}

		return userExists;
	}
	
	
	String emailaddress;
	
	
	public String getEmailaddress() {
		if(emailaddress==null)
		{
			return username;
		}
		return emailaddress;
	}
	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public String signupPortalUser() throws Exception	{
		System.out.println("signupUser() : username :"+username +", password :"+password+", email : "+getEmailaddress() + "portal " + portalId() );
		String encryptedPass = cryptWithMD5(password);
		return addPortalUser(username, encryptedPass, getEmailaddress(), portalId());
	}

	public String acceptOpInvite() throws Exception {

		JSONObject invitation = new LoginAction().acceptUserInvite(inviteToken);
		if((Boolean) invitation.get("accepted") && emailaddress != null) {
			Long userid = (Long)invitation.get("userid");
			User user = AccountUtil.getUserBean().getUser(userid);
			if(user.getPassword() == null && password != null) {
				user.setPassword(cryptWithMD5(password));
				AccountUtil.getUserBean().updateUser(user);
				
				
				Connection conn =FacilioConnectionPool.getInstance().getConnection();
				PreparedStatement pstmt = conn.prepareStatement("INSERT INTO faciliousers(username,email,password,USERID) VALUES(?,?,?,?)");
				try {
					pstmt.setString(1, emailaddress);
					pstmt.setString(2, emailaddress);
					pstmt.setString(3, cryptWithMD5(password));
					pstmt.setLong(4, userid);

					pstmt.executeUpdate();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.info("Exception occurred ", e);
				}
				finally
				{
					pstmt.close();
					conn.close();
				}
				
			}
            return SUCCESS;
		} else {
			return ERROR;
		}
	}

	private String addPortalUser(String username, String password, String emailaddress, long portalId) throws Exception {
		System.out.println("### AddNewUserDB() :"+emailaddress);
		Connection conn = null;
		PreparedStatement pstmt = null;
		try	{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO faciliorequestors(PORTALID, username, email, password) VALUES(?,?,?,?)");
			pstmt.setLong(1, portalId());
			pstmt.setString(2, username);
			pstmt.setString(3, emailaddress);
			pstmt.setString(4, password);
			pstmt.executeUpdate();
			
			User user = new User();
			user.setName(username);
			user.setEmail(emailaddress);
			
			UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
			userBean.addRequester(AccountUtil.getCurrentOrg().getId(), user);
			
		} catch (MySQLIntegrityConstraintViolationException e){
			setJsonresponse("message", "Username exists for this portal");
			return ERROR;
		} catch (SQLException | RuntimeException e) {
			log.info("Exception occurred ", e);
			setJsonresponse("message", "Error during signup");
			return ERROR;
		} finally {
			DBUtil.closeAll(conn, pstmt);
		}
		return validatelogin();
	}

	   private static MessageDigest md;

	   public static String cryptWithMD5(String pass){
	    try {
	        md = MessageDigest.getInstance("MD5");
	        byte[] passBytes = pass.getBytes();
	        md.reset();
	        byte[] digested = md.digest(passBytes);
	        StringBuffer sb = new StringBuffer();
	        for(int i=0;i<digested.length;i++){
	            sb.append(Integer.toHexString(0xff & digested[i]));
	        }
	        return sb.toString();
	    } catch (NoSuchAlgorithmException ex) {
	    		ex.printStackTrace();
	    }
	        return null;
	   }
	
	public String generateAuthToken()
	{
		System.out.println("generateAuthToken() : username :"+username);
		String jwt = CognitoUtil.createJWT("id", "auth0", username, System.currentTimeMillis()+24*60*60000,false);
		System.out.println("Response token is "+ jwt);
		setJsonresponse("authtoken",jwt);
		setJsonresponse("username",username);
			
		return SUCCESS;
	}

	public String getToken()
	{
		return SUCCESS;
	}
	
	private String redirect_uri;
	public String getRedirect_uri() {
		return redirect_uri;
	}

	public void setRedirect_uri(String redirect_uri) {
		this.redirect_uri = redirect_uri;
	}

	public String getResponse_type() {
		return response_type;
	}

	public void setResponse_type(String response_type) {
		this.response_type = response_type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	private String response_type;
	private String state ;
	
	
	private JSONObject content;
	
	public void setContent(JSONObject content) {
		this.content = content;
	}
	
	public JSONObject getContent() {
		return this.content;
	}
	
	public String confirmPayment() {
		// Org id details through the customer email 
		HashMap<String, Object> customer = (HashMap<String, Object>) this.getContent().get("customer");
		String customerEmail = (String) customer.get("email");
		String customerId = (String) customer.get("id");
		
		String isPlanRequest = "false";

		System.out.println("**************customer Email" +customerEmail);
		
		long orgId = 0;
        try{
        	
        	User userObj = AccountUtil.getUserBean().getFacilioUser(customerEmail);
        	orgId = userObj.getOrgId();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
			HashMap<String, Object> subscription = (HashMap<String, Object>) this.getContent().get("subscription");
			String PlanId = (String) subscription.get("plan_id");
		try {
				CommonCommandUtil.insertOrgInfo(orgId, "isPlanRequest", isPlanRequest);
				CommonCommandUtil.insertOrgInfo(orgId, "Customer_id", customerId);
				CommonCommandUtil.insertOrgInfo(orgId,"Plan_id",PlanId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e1);
		}
		ArrayList<?> addons = (ArrayList<?>) subscription.get("addons");
		if (addons!= null)	
		{
		for (int i = 0; i < addons.size(); i++) {
				HashMap<String, Object> addon = (HashMap<String, Object>) addons.get(i);
				String Name = (String) addon.get("id");
				if (Name.equals("staff-basic") || Name.equals("staff-professional")){
					Name = "staff";
			    }
			    if (Name.equals("buildings") || Name.equals("building-professional")){
			    	Name = "building";
			    }
				String Value = addon.get("quantity").toString();	
			
			try {
				System.out.println(Name);
				CommonCommandUtil.insertOrgInfo(orgId,Name,Value);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
			
		}
		}

		return SUCCESS;
	}
}
