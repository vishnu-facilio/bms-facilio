package com.facilio.integration.actions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.facilio.fw.auth.CognitoUtil;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;

public class Home extends ActionSupport {
/*
 * redirect_uri = https://platform.zoho.com/auth/conns/urjanet/redirect
response_type = code
state = PORTAL-yogendrababu
 */
	public String loginSubmit()
	{
		System.out.println(redirect_uri + state + response_type);
		return SUCCESS;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
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
	
	public String validatelogin() throws Exception
	{
		System.out.println("validatelogin() : username : "+username +"; password : "+password);
		String encryptedPass = cryptWithMD5(password);
		if(!verifyPassword(username, encryptedPass))
		{
			return ERROR;
		}		
		String jwt = CognitoUtil.createJWT("id", "auth0", username, System.currentTimeMillis()+24*60*60000);
		System.out.println("Response token is "+ jwt);
		setJsonresponse("token",jwt);
		setJsonresponse("username",username);
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();	

		Cookie cookie = new Cookie("fc.idToken.facilio", jwt);
		cookie.setMaxAge(60*60*24*2); // Make the cookie last a year
		cookie.setPath("/");
		cookie.setHttpOnly(true);
//		cookie.setDomain(request.getServerName());
		response.addCookie(cookie);

		Cookie authmodel = new Cookie("fc.authtype", "facilio");
		authmodel.setMaxAge(60*60*24*2); // Make the cookie last a year
		authmodel.setPath("/");
		authmodel.setHttpOnly(false);
//		authmodel.setDomain(request.getServerName());
		System.out.println("#################### facilio.in:::; "+ request.getServerName());
		response.addCookie(authmodel);

		return SUCCESS;
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
			pstmt = conn.prepareStatement("SELECT password FROM faciliousers WHERE email = ?");
			pstmt.setString(1, emailaddress);
			
			rs = pstmt.executeQuery();
			while(rs.next())
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
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt);
		}
		
		return userExists;
	}
	
	
	String emailaddress;
	
	
	public String getEmailaddress() {
		return emailaddress;
	}
	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}
	public String signupUser() throws Exception
	{
		System.out.println("signupUser() : username :"+username +", password :"+password+", email : "+emailaddress );
		String encryptedPass = cryptWithMD5(password);
		return AddNewUserDB(username,encryptedPass,emailaddress);
		
	//	return SUCCESS;
	}
	
	public  String AddNewUserDB(String username, String password, String emailaddress) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO faciliousers(username,email,password) VALUES(?,?,?)");
			pstmt.setString(1, username);
			pstmt.setString(2, emailaddress);
			pstmt.setString(3, password);
			pstmt.executeUpdate();
				
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt);
		}
		return generateAuthToken();
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
		String jwt = CognitoUtil.createJWT("id", "auth0", username, System.currentTimeMillis()+24*60*60000);
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
	
}
