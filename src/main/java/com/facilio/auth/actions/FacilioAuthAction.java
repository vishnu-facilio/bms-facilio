package com.facilio.auth.actions;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.chain.Chain;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.PortalInfoAction;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.LoginUtil;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class FacilioAuthAction extends ActionSupport {

    private static final Logger LOGGER = Logger.getLogger(FacilioAuthAction.class.getName());

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
    private static MessageDigest md;


    public String getUsername() {
        if(username != null) {
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
        if(emailaddress != null) {
            return emailaddress;
        } else {
        	if(username == null) {
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
        FacilioAuthAction.md = md;
    }

    private void setJsonresponse(String key, Object value) {
        this.jsonresponse.put(key, value);
    }



    public String signupUser() throws Exception	{

        LOGGER.info("signupUser() : username :"+ getUsername() +", password :"+ getPassword() +", email : "+getEmailaddress() );
        return addFacilioUser(getUsername(), getPassword(), getEmailaddress());
    }

    private String addFacilioUser(String username, String password, String emailaddress) throws Exception {
        User userObj = AccountUtil.getUserBean().getFacilioUser(emailaddress);
        if(userObj != null) {
            setJsonresponse("message", "Email already exists");
            return ERROR;
        }

        Organization orgObj = AccountUtil.getOrgBean().getOrg(getDomainname());
        if(orgObj != null) {
            setJsonresponse("message", "Org Domain Name already exists");
            return ERROR;
        }
        HttpServletRequest request = ServletActionContext.getRequest();
        LOGGER.info("### addFacilioUser() :"+emailaddress);

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
        FacilioContext signupContext = new FacilioContext();
        signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupInfo);

        Chain c = FacilioChainFactory.getOrgSignupChain();
        c.execute(signupContext);
        setJsonresponse("message", "success");
        return SUCCESS;
    }

    public String validateLogin() {

        boolean portalUser = false;

        if(getUsername() != null && getPassword() != null) {
            try {
                LOGGER.info("validateLogin() : username : " + getUsername() + "; password : " + getPassword() + " portal id : " + portalId());
                Boolean validPassword = null;
                if (portalId() > 0) {
                    validPassword = verifyPortalPassword(getUsername(), getPassword(), portalId());
                    portalUser = true;
                } else {
                    validPassword = verifyPassword(getUsername(), getPassword());
                }

                if (validPassword == null) {
                    setJsonresponse("message", "User doesn't exist"); // can be removed later
                    setJsonresponse("errorcode", "1");
                    return ERROR;
                } else {
                    if (!validPassword) {
                        LOGGER.info(">>>>> invalid Password :" + getUsername());
                        setJsonresponse("errorcode", "1");
                        return ERROR;
                    }
                }

                String jwt = CognitoUtil.createJWT("id", "auth0", getUsername(), System.currentTimeMillis() + 24 * 60 * 60000,portalUser);
                LOGGER.info("Response token is " + jwt);
                setJsonresponse("token", jwt);
                setJsonresponse("username", getUsername());

                HttpServletRequest request = ServletActionContext.getRequest();
                HttpServletResponse response = ServletActionContext.getResponse();

                Cookie cookie = new Cookie("fc.idToken.facilio", jwt);
                if(portalUser) {
                    cookie = new Cookie("fc.idToken.facilioportal", jwt);
                }
                cookie.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);

                Cookie authmodel = new Cookie("fc.authtype", "facilio");
                authmodel.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
                authmodel.setPath("/");
                authmodel.setHttpOnly(false);
                String parentdomain = request.getServerName().replaceAll("api.", "");
                authmodel.setDomain(parentdomain);
                LOGGER.info("#################### facilio.in::: " + request.getServerName());
                response.addCookie(authmodel);
                User user = AccountUtil.getUserBean().getFacilioUser(getUsername());
                if(user != null ) {
                    long uid = user.getUid();
                    String userAgent = request.getHeader("User-Agent");
                    userAgent = userAgent != null ? userAgent : "";
                    String ipAddress = request.getHeader("X-Forwarded-For");
                    ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
                    AccountUtil.getUserBean().startUserSession(uid, getUsername(), jwt, ipAddress, userAgent);
                }
            } catch (Exception e) {
                LOGGER.log(Level.INFO, "Exception while validating password, ", e);
                setJsonresponse("message", "Error while validating user name and password");
                return ERROR;
            }
            return SUCCESS;
        }
        setJsonresponse("message", "Invalid username or password");
        return ERROR;
    }

    private Boolean verifyPassword(String emailaddress, String password) {
        boolean passwordValid = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try {
            conn = FacilioConnectionPool.INSTANCE.getConnection();
            pstmt = conn.prepareStatement("SELECT Users.password,Users.EMAIL FROM Users inner join faciliousers on Users.USERID=faciliousers.USERID WHERE (faciliousers.email = ? or faciliousers.username = ?) and USER_VERIFIED=1");
            pstmt.setString(1, emailaddress);
            pstmt.setString(2, emailaddress);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                String storedPass = rs.getString("password");
                String emailindb = rs.getString(2);
                this.emailaddress = emailindb;
                LOGGER.info("Stored : "+storedPass);
                LOGGER.info("UserGiv: "+password);
                if(storedPass.equals(password)) {
                    passwordValid = true;
                }
            } else {
            	LOGGER.log(Level.INFO, "No records found for  "+emailaddress);
                return null;
            }

        } catch(SQLException | RuntimeException e) {
            LOGGER.log(Level.INFO, "Exception while verifying password, ", e);
        } finally {
            DBUtil.closeAll(conn, pstmt,rs);
        }

        return passwordValid;
    }
    
    public long portalId() {
        if(AccountUtil.getCurrentOrg() != null) {
            return AccountUtil.getCurrentOrg().getPortalId();
        }
        return -1L;
    }

    private Boolean verifyPortalPassword(String emailAddress, String password, long portalId) {
        boolean passwordValid = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs;
        try {
            conn = FacilioConnectionPool.INSTANCE.getConnection();
            pstmt = conn.prepareStatement("SELECT Users.password FROM Users inner join faciliorequestors on Users.USERID=faciliorequestors.USERID WHERE faciliorequestors.email = ? and USER_VERIFIED=1 and PORTALID = ?");
            pstmt.setString(1, emailAddress);
            pstmt.setLong(2, portalId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                String storedPass = rs.getString("password");
                LOGGER.info("Stored : "+storedPass);
                LOGGER.info("UserGiv: "+password);
                if(storedPass.equals(password)) {
                    passwordValid = true;
                }
            } else {
                return null;
            }
        } catch(SQLException | RuntimeException e) {
            LOGGER.log(Level.INFO, "Exception while verifying password, ", e);
        } finally {
            DBUtil.closeAll(conn, pstmt);
        }

        return passwordValid;
    }

    public String validateInviteLink() throws Exception {

        JSONObject invitation = new JSONObject();
        User user = AccountUtil.getUserBean().validateUserInvite(getInviteToken());
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

    public String verifyEmail() throws Exception {

        JSONObject invitation = new JSONObject();
        User user = AccountUtil.getUserBean().verifyEmail(getInviteToken());
        if (user == null) {
            invitation.put("error", "link_expired");
        } else {
            invitation.put("email", user.getEmail());
            invitation.put("accepted", true);
        }
        ActionContext.getContext().getValueStack().set("invitation", invitation);

        return SUCCESS;
    }

    public String resetPassword() throws Exception {
        JSONObject invitation = new JSONObject();
        if(getInviteToken() != null) {
            User user = AccountUtil.getUserBean().resetPassword(getInviteToken(), getPassword());
            if(user.getUid() > 0){
                invitation.put("status", "success");
            }
        } else {
            User user;
            if(portalId() > 0) {
            	user = AccountUtil.getUserBean().getPortalUser(getEmailaddress(), portalId());
            } else {
                user = AccountUtil.getUserBean().getFacilioUser(getEmailaddress());
            }
            if(user != null) {
                AccountUtil.getUserBean().sendResetPasswordLink(user);
                invitation.put("status", "success");
            } else {
                invitation.put("status", "failed");
            }
        }
        ActionContext.getContext().getValueStack().set("invitation", invitation);
        return SUCCESS;
    }

    public String changePassword() throws Exception {
        User user = AccountUtil.getCurrentUser();
        if(user != null) {
            boolean verifyOldPassword = verifyPassword(user.getEmail(), getPassword());
            if (verifyOldPassword) {
                user.setPassword(getNewPassword());
                AccountUtil.getUserBean().updateUser(user);
                setJsonresponse("message", "Password changed successfully");
                setJsonresponse("status", "success");
                return SUCCESS;
            }
        }
        setJsonresponse("message", "Current Password is incorrect");
        setJsonresponse("status", "failure");
        return ERROR;
    }

    public String acceptUserInvite() throws Exception {
        boolean status = AccountUtil.getTransactionalUserBean().acceptInvite(getInviteToken(), getPassword());
        if(status){
            return SUCCESS;
        }
        return ERROR;
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
        LOGGER.info("generateAuthToken() : username :"+getUsername());
        if(verifyPassword(getUsername(), getPassword())) {
            String jwt = CognitoUtil.createJWT("id", "auth0", getUsername(), System.currentTimeMillis() + 24 * 60 * 60000, false);
            LOGGER.info("Response token is " + jwt);
            setJsonresponse("authtoken", jwt);
            setJsonresponse("username", getUsername());
        } else {
            setJsonresponse("message", "Invalid username / password");
        }
        return SUCCESS;
    }


    public String signupPortalUser() throws Exception	{
        LOGGER.info("signupUser() : username :"+getUsername() +", password :"+password+", email : "+getEmailaddress() + "portal " + portalId() );
        return addPortalUser(getUsername(), getPassword(), getEmailaddress(), portalId());
    }


    private String addPortalUser(String username, String password, String emailaddress, long portalId) throws Exception {
        LOGGER.info("### addPortalUser() :"+emailaddress);

        User user = new User();
        user.setName(username);
        user.setEmail(emailaddress);
        user.setPortalId(portalId);
        user.setPassword(password);
        
        PortalInfoAction authAction = new PortalInfoAction();
        authAction.getPortalInfo();
        PortalInfoContext portalInfo = authAction.getProtalInfo(); 
       
        boolean opensignup = portalInfo.isSignup_allowed();  // SIGNUP_ALLOWED
        boolean anydomain_allowedforsignup = portalInfo.is_anyDomain_allowed();

        System.out.println("Is signup allowed for all "+opensignup);
        
        if(!opensignup) {
        	setJsonresponse("message", "Signup not allowed for this portal");
			return SUCCESS;
        }
        
        boolean whitelisteddomain = false;

		if (opensignup && !anydomain_allowedforsignup) {
			String domains = portalInfo.getWhiteListed_domains();

			ArrayList<String> items = new ArrayList<String>();
			if (domains != null) {
				items = new ArrayList<>(Arrays.asList(domains.split(",")));
			}
			for (String item : items) {
				if (emailaddress.endsWith(item.trim())) {
					whitelisteddomain = true;
					break;
				}
			}
			if (!anydomain_allowedforsignup && !whitelisteddomain  ) {
				setJsonresponse("message", "Only whitelisted domains allowed");
				return SUCCESS;
			}

		}
        if(anydomain_allowedforsignup || opensignup || whitelisteddomain) {
            try {
                AccountUtil.getTransactionalUserBean().createRequestor(AccountUtil.getCurrentOrg().getId(), user);
                LOGGER.info("user signup done "+user);
                try {
					(new UserBeanImpl()).sendInvitation(AccountUtil.getCurrentOrg().getOrgId(), user,true);
					 LOGGER.info("Email invitation sent "+user);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					 LOGGER.log(java.util.logging.Level.SEVERE, "unable to send email", e);
					 }
               
            } catch (InvocationTargetException ie) {
                Throwable e= ie.getTargetException();
                if(e.getMessage()!=null && e.getMessage().equals("Email Already Registered")) {
                    setJsonresponse("message", "Email Already Registered");
                    return SUCCESS;
                } else {
                    throw ie;
                }
		    }
            setJsonresponse("message", "success");
            return SUCCESS;
        }
        return ERROR;
    }

    public String changePortalPassword() {
        boolean verifyOldPassword = verifyPortalPassword(getEmailaddress(), getPassword(), portalId());
        if(verifyOldPassword) {
            try {
                User user = AccountUtil.getUserBean().getPortalUser(getEmailaddress(), portalId());
                user.setPassword(getNewPassword());
                AccountUtil.getUserBean().updateUser(user);
                setJsonresponse("message", "Password changed successfully");
                setJsonresponse("status", "success");
                return SUCCESS;
            } catch(Exception e) {
                LOGGER.log(Level.INFO, "Exception while changing portal password, ", e);
            }
        } else {
            setJsonresponse("message", "Current Password is incorrect");
        }
        setJsonresponse("status", "failure");
        return ERROR;
    }

    public String apiLogout() throws Exception {

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpSession session = request.getSession();

        session.invalidate();
        if(portalId() > 0) {
            LoginUtil.eraseUserCookie(request, response, "fc.idToken.facilioportal", null);
        } else {
            LoginUtil.eraseUserCookie(request, response, "fc.idToken.facilio", null);
        }

        LoginUtil.eraseUserCookie(request, response, "fc.authtype", null);

        return SUCCESS;
    }
}
