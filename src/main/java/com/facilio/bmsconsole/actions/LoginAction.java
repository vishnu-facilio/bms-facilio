package com.facilio.bmsconsole.actions;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Inflater;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.apache.commons.chain.Chain;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.EncryptionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;
import com.facilio.fw.auth.LoginUtil;
import com.facilio.fw.auth.SAMLAttribute;
import com.facilio.fw.auth.SAMLUtil;
import com.facilio.wms.util.WmsApi;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport{
	static
	{
		System.out.println("Login action loaded");
	}
	private static String HOSTNAME = null;
	
	private static final SimpleDateFormat SAML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private String response = null;
	public String getResponse()
	{
		return response;
	}
	
	// getter setter for identity token
	private String idToken;
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
		
	// getter setter for access token
	private String accessToken;
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	private HashMap<String,String> signupinfo = new HashMap<String,String>();
	public boolean isSignup() {
		return isSignup;
	}
	public void setSignup(boolean isSignup) {
		this.isSignup = isSignup;
	}
	private boolean isSignup = false;
	public void setSignupInfo(String key,String value)
	{
		signupinfo.put(key,value);
	}
	
	public HashMap<String,String> getSignupInfo()
	{
		return signupinfo;
	}
	public String getSignupInfo(String signupkey)
	{
		return signupinfo.get(signupkey);
	}
	
	public String login() throws Exception {
		
		if (HOSTNAME == null) {
			HOSTNAME = (String) ActionContext.getContext().getApplication().get("DOMAINNAME");
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse httpResp = ServletActionContext.getResponse();
		httpResp.addHeader("FC-Login-State", "1"); // to handle login page in ajax response
		
		String idToken = LoginUtil.getUserCookie(request, LoginUtil.IDTOKEN_COOKIE_NAME);
		
		try {
			CognitoUser cognitoUser = CognitoUtil.verifyIDToken(idToken);
			
			if (cognitoUser != null) {

				Account account = LoginUtil.getAccount(cognitoUser, false);

				String samlRequest = request.getParameter("SAMLRequest");
				String relay = request.getParameter("relayUrl");

				if (samlRequest != null && !"".equals(samlRequest.trim())) {

					boolean status = handleSAMLLogin(cognitoUser.getEmail(), account.getOrg().getDomain(), samlRequest, relay);

					if (status) {
						return "samlresponse";
					}
					else {
						return "loginpage";
					}
				}
				else {

					String redirectURL = request.getScheme() + "://" + account.getOrg().getDomain() + HOSTNAME + ":" + request.getServerPort();

					String nextURL = request.getParameter("redirect");
					if (nextURL == null || nextURL.trim().equals("")) {
						redirectURL = redirectURL + request.getContextPath() +"/app/index"; 
					}
					else {
						nextURL = (nextURL.startsWith("/") ? nextURL : "/" + nextURL);
						redirectURL = redirectURL + nextURL;
					}
					request.setAttribute("redirect_url", redirectURL);

					return "loginsuccess";
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return "loginpage";
	}
	
	public String apiLogin() throws Exception {
		
		account = new HashMap<>();
		account.put("org", AccountUtil.getCurrentOrg());
		account.put("user", AccountUtil.getCurrentUser());
		
		return SUCCESS;
	}
	
	public String apiLogout() throws Exception {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		session.invalidate();
		
		return SUCCESS;
	}
	
	public String validateUser() throws Exception {
		
		if (HOSTNAME == null) {
			HOSTNAME = (String) ActionContext.getContext().getApplication().get("DOMAINNAME");
		}
		
		// get the identity token and decode it.
		String idToken = getIdToken();
		if (idToken == null) {
			response = "idToken_missing";
		}
		else {
			// Temp identity id generation code
			/*try {
			String identityId = CognitoUtil.getIdentityId(idToken);
			System.out.println("identityId ======>  "+identityId);

			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
			AWSIot awsIot = AWSIotClientBuilder.standard().withRegion("us-west-2").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

			AttachPrincipalPolicyRequest policyResult = new AttachPrincipalPolicyRequest().withPolicyName("EM-Policy").withPrincipal(identityId);
			awsIot.attachPrincipalPolicy(policyResult);
		}
		catch (Exception e) {
			e.printStackTrace();
		}*/

			CognitoUtil.CognitoUser cognitoUser = CognitoUtil.verifyIDToken(idToken);
			Account account = LoginUtil.getAccount(cognitoUser, false);

			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse httpResp = ServletActionContext.getResponse();

			// setting id token cookie
			LoginUtil.addUserCookie(httpResp, LoginUtil.IDTOKEN_COOKIE_NAME, idToken, HOSTNAME);

			String redirectURL = request.getScheme() + "://" + account.getOrg().getDomain() + HOSTNAME + ":" + request.getServerPort();

			String nextURL = request.getParameter("redirect");
			if (nextURL == null || nextURL.trim().equals("")) {
				redirectURL = redirectURL + request.getContextPath() +"/app/index"; 
			}
			else {
				nextURL = (nextURL.startsWith("/") ? nextURL : "/" + nextURL);
				redirectURL = redirectURL + nextURL;
			}

			String samlRequest = request.getParameter("isSAML");
			if (samlRequest != null && Boolean.parseBoolean(samlRequest)) {
				response = "reload";
			}
			else {
				response = redirectURL;
			}
		}
		return SUCCESS;
	}
	
	public String logout() {
		
		if (HOSTNAME == null) {
			HOSTNAME = (String) ActionContext.getContext().getApplication().get("DOMAINNAME");
		}
		System.out.println("Logout called");
		HttpServletRequest httpReq = ServletActionContext.getRequest();
		HttpServletResponse httpResp = ServletActionContext.getResponse();
		
		LoginUtil.eraseUserCookie(httpReq, httpResp, LoginUtil.IDTOKEN_COOKIE_NAME, HOSTNAME);
		
		ActionContext.getContext().getSession().remove("USER_INFO");
		if(httpReq.getServerName().matches(HOSTNAME.substring(1)))
		{
			
			return SUCCESS;
		}
		else
		{
			
			System.out.println("redirecting to root domain");
			httpReq.setAttribute("redirUrl",getURL(httpReq,HOSTNAME.substring(1))+"/logout");
			return "REDIRECT";
		}
		
		
	}
	public static String getURL(HttpServletRequest req) {
		return getURL( req, null);
	}
	public static String getURL(HttpServletRequest req,String overridehostname) {

	    String scheme = req.getScheme();             // http
	    String serverName = req.getServerName();     // hostname.com
	    if(overridehostname!=null)
	    {
	    	serverName = overridehostname;
	    }
	    int serverPort = req.getServerPort();        // 80
	    String contextPath = req.getContextPath();   // /mywebapp
	    String servletPath = req.getServletPath();   // /servlet/MyServlet
	//    String pathInfo = req.getPathInfo();         // /a/b;c=123
	//    String queryString = req.getQueryString();          // d=789

	    // Reconstruct original requesting URL
	    StringBuilder url = new StringBuilder();
	    url.append(scheme).append("://").append(serverName);

	    if (serverPort != 80 && serverPort != 443) {
	        url.append(":").append(serverPort);
	    }

	    url.append(contextPath);//.append(servletPath);

	  
	  System.out.println(url);
	    return url.toString();
	}	
	private boolean handleSAMLLogin(String curUser, String subdomain, String samlRequest, String relay) throws Exception {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		
		String decodedsamlRequest = decodeSAMLRequest(samlRequest);
		if (decodedsamlRequest == null) {
			return false;
		}
		
		Document document = SAMLUtil.convertStringToDocument(decodedsamlRequest);
		document.getDocumentElement().normalize();

		Element authnRequestElement = (Element) document.getFirstChild();
		String assertionConsumerServiceURL = authnRequestElement.getAttribute("AssertionConsumerServiceURL");

		String spEntityID = document.getElementsByTagName("saml:Issuer").item(0).getTextContent();
		
		String requestID = "RANDOMID_"+UUID.randomUUID().toString().replace("-", "");

		String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
		if (request.getServerPort() == 80) {
			url = request.getScheme()+"://"+request.getServerName();
		}

		User user = AccountUtil.getUserBean().getUser(curUser);
		
		JSONObject customAttr = new JSONObject();
		customAttr.put("Facilio User Id", user.getOuid());
		customAttr.put("Facilio Org Id", user.getOrgId());
		customAttr.put("Facilio Domain", subdomain);

		SAMLAttribute samlAttributes = 
				new SAMLAttribute()
				.setIssuer(url)
				.setIntendedAudience(spEntityID)
				.setInResponseTo(requestID)
				.setRecipient(assertionConsumerServiceURL)
				.setEmail(curUser)
				.setCustomAttr(customAttr);

		String samlResponse = generateSignedSAMLResponse(samlAttributes);

		request.setAttribute("SAMLResponse", samlResponse);
		request.setAttribute("AssertionConsumerServiceURL", assertionConsumerServiceURL);
		
		relay = (relay == null || "".equals(relay.trim())) ? "token" : relay;
		request.setAttribute("relay", relay);
		return true;
	}
	
	private JSONObject signupData;
	
	public void setSignupData(JSONObject signupData) {
		this.signupData = signupData;
	}
	
	public JSONObject getSignupData() {
		return this.signupData;
	}
	
	public String signup() throws Exception
	{
		System.out.println("The parameters list::: " + getSignupData());
		
		FacilioContext signupContext = new FacilioContext();
		signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, getSignupData());
		
		Chain c = FacilioChainFactory.getOrgSignupChain();
		c.execute(signupContext);
		
		response = "success";
		return SUCCESS;
	}
	
	public String validateInviteLink() throws Exception
	{
		String[] inviteIds = EncryptionUtil.decode(getInviteToken()).split("_");
		long orgId = Long.parseLong(inviteIds[0]);
		long ouid = Long.parseLong(inviteIds[1]);
		
		long inviteLinkExpireTime = (7 * 24 * 60 * 60 * 1000); //7 days in seconds
		
		JSONObject invitation = new JSONObject();
		
		Organization org = AccountUtil.getOrgBean().getOrg(orgId);
		User user = AccountUtil.getUserBean().getUser(ouid);
		if ((System.currentTimeMillis() - user.getInvitedTime()) > inviteLinkExpireTime) {
			invitation.put("error", "link_expired");
		}
		else {
			invitation.put("orgname", org.getName());
			invitation.put("email", user.getEmail());
			invitation.put("account_exists", CognitoUtil.isEmailExists(user.getEmail()));
		}
		ActionContext.getContext().getValueStack().set("invitation", invitation);
		
		return SUCCESS;
	}
	
	public String acceptInvite() throws Exception {
		
		String[] inviteIds = EncryptionUtil.decode(getInviteToken()).split("_");
		long orgId = Long.parseLong(inviteIds[0]);
		long ouid = Long.parseLong(inviteIds[1]);
		
		long inviteLinkExpireTime = (7 * 24 * 60 * 60 * 1000); //7 days in seconds
		
		JSONObject invitation = new JSONObject();
		
		User user = AccountUtil.getUserBean().getUser(ouid);
		if ((System.currentTimeMillis() - user.getInvitedTime()) > inviteLinkExpireTime) {
			invitation.put("error", "link_expired");
		}
		else {
			boolean acceptStatus = AccountUtil.getUserBean().acceptInvite(ouid, null);
			if (acceptStatus) {
				invitation.put("accepted", true);
			}
			else {
				invitation.put("accepted", false);
			}
		}
		ActionContext.getContext().getValueStack().set("invitation", invitation);
		
		return SUCCESS;
	}
	
	private String inviteToken;
	
	public String getInviteToken() {
		return this.inviteToken;
	}
	
	public void setInviteToken(String inviteToken) {
		this.inviteToken = inviteToken;
	}
	
	private HashMap<String, Object> account;
	
	public HashMap<String, Object> getAccount() {
		return account;
	}
	public void setAccount(HashMap<String, Object> account) {
		this.account = account;
	}
	
	private HashMap<String, Object> createAccountResp;
	
	public HashMap<String, Object> getCreateAccountResp() {
		return createAccountResp;
	}
	public void setCreateAccountResp(HashMap<String, Object> createAccountResp) {
		this.createAccountResp = createAccountResp;
	}
	
	// API Method, so will return json
	public String newSignup() throws Exception
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		if (!request.getMethod().equalsIgnoreCase("POST")) {
			createAccountResp.put("error", true);
			createAccountResp.put("message", "post_only_api");
		}
		else {
			if (getAccount() == null || getAccount().isEmpty()) {
				createAccountResp = new HashMap<>();
				createAccountResp.put("error", true);
				createAccountResp.put("message", "mandatory_fields_missing");
			}
			else {
				FacilioContext orgsignupcontext = new FacilioContext();
				System.out.println("The parameters list"+ActionContext.getContext().getParameters());
				orgsignupcontext.put("signupinfo", getAccount());
				Chain c = FacilioChainFactory.getOrgSignupChain();
				c.execute(orgsignupcontext);
				
				createAccountResp = new HashMap<>();
				createAccountResp.put("success", true);
			}
		}
		
		return SUCCESS;
	}
	
	public String currentAccount() throws Exception {
		
		HashMap<String, Object> appProps = new HashMap<>();
		appProps.put("permissions", AccountConstants.Permission.toMap());
		appProps.put("permissions_groups", AccountConstants.PermissionGroup.toMap());
		
		account = new HashMap<>();
		account.put("org", AccountUtil.getCurrentOrg());
		account.put("user", AccountUtil.getCurrentUser());
		
		List<User> users = AccountUtil.getOrgBean().getAllOrgUsers(AccountUtil.getCurrentOrg().getOrgId());
		List<Group> groups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().getId());
		List<Role> roles = AccountUtil.getRoleBean().getRoles(AccountUtil.getCurrentOrg().getOrgId());
		
		Map<String, Object> data = new HashMap<>();
		data.put("users", users);
		data.put("groups", groups);
		data.put("roles", roles);
		
		data.put("alarmSeverity", AlarmAPI.getAlarmSeverityList());
		data.put("assetCategory", AssetsAPI.getCategoryList());
		data.put("serviceList", ReportsUtil.getPurposeMapping());
		
		Map<String, Object> config = new HashMap<>();
		config.put("ws_endpoint", WmsApi.getWebsocketEndpoint(AccountUtil.getCurrentUser().getUid()));
		
		account.put("data", data);
		account.put("config", config);
		account.put("appProps", appProps);
		
		return SUCCESS;
	}
	
	private String generateSignedSAMLResponse(SAMLAttribute samlAttr) throws Exception 
	{
		ClassLoader classLoader = LoginAction.class.getClassLoader();
		File samlXML = new File(classLoader.getResource("conf/saml/saml-response.xml").getFile());
		String samlTemplate = SAMLUtil.getFileAsString(samlXML);

		Date dt = new Date();
		String samlDT = SAML_DATE_FORMAT.format(dt);
		String randomUUID_1 = UUID.randomUUID().toString();
		String randomUUID_2 = UUID.randomUUID().toString();
		String samlResponse = samlTemplate.replaceAll("--IssueInstant--|--AuthnInstant--", samlDT);
		samlResponse = samlResponse.replaceAll("--Issuer--",samlAttr.getIssuer());
		samlResponse = samlResponse.replaceAll("--ResponseID--", "_"+randomUUID_1);
		samlResponse = samlResponse.replaceAll("--AssertionID--", "SAML_"+randomUUID_2);
		samlResponse = samlResponse.replaceAll("--SessionIndex--", "SAML_"+randomUUID_2);
		samlResponse = samlResponse.replaceAll("--EmailID--", samlAttr.getEmail());
		samlResponse = samlResponse.replaceAll("--InResponseTo--", samlAttr.getInResponseTo());
		samlResponse = samlResponse.replaceAll("--Recipient--|--Destination--", samlAttr.getRecipient());
		samlResponse = samlResponse.replaceAll("--AudienceRestriction--", samlAttr.getIntendedAudience());
		samlResponse = samlResponse.replaceAll("--ConditionsNotBefore--", getNotBeforeDateAndTime());
		samlResponse = samlResponse.replaceAll("--ConditionsNotOnOrAfter--|--SubjectNotOnOrAfter--", getNotOnOrAfterDateAndTime());

		Document document = SAMLUtil.convertStringToDocument(samlResponse);

		if (samlAttr.getCustomAttr() != null) {
			
			NodeList assertions = document.getElementsByTagName("Assertion");
			if (assertions != null && assertions.getLength() > 0) {
				
				Element assertion = (Element) assertions.item(0);
				JSONObject customAttr = samlAttr.getCustomAttr();
				
				Iterator<String> keys = customAttr.keySet().iterator();
				Element attributeStatement = document.createElement("AttributeStatement");
				while (keys.hasNext()) {
					String attr = keys.next();
					String value = customAttr.get(attr).toString();
					Element cuatomAttrElement = newAttrTag(document, attr,value);
					attributeStatement.appendChild(cuatomAttrElement);
				}
				assertion.appendChild(attributeStatement);
			}
		}
		
		String Temp = SAMLUtil.convertDomToString(document);
		document = SAMLUtil.convertStringToDocument(Temp);
		
		document = signSAMLDocument(document, samlAttr.getPrivateKey() , samlAttr.getX509Certificate());
		
		samlResponse = SAMLUtil.convertDomToString(document);
		
		byte[] enc = org.apache.commons.codec.binary.Base64.encodeBase64(samlResponse.getBytes("UTF-8"));
		return new String(enc, "UTF-8");
	}

	public static Document signSAMLDocument(Document root, PrivateKey privKey, X509Certificate x509Cert) throws KeyException, CertificateException {

		XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");

		DOMSignContext domSignCtx = new DOMSignContext(privKey, root.getElementsByTagName("Assertion").item(0));
		domSignCtx.setDefaultNamespacePrefix("ds");

		domSignCtx.setNextSibling(root.getElementsByTagName("Subject").item(0));
		Element assertion=(Element)root.getElementsByTagName("Assertion").item(0);
		assertion.setIdAttributeNode(assertion.getAttributeNode("ID"), true);
		String reference_URI= assertion.getAttribute("ID");
		Reference ref = null;
		SignedInfo signedInfo = null;
		try {
			Transform transform1 = xmlSigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec)null);
			Transform transform2 = xmlSigFactory.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#", (TransformParameterSpec)null);
			ArrayList<Transform> listt = new ArrayList<Transform>();
			listt.add(transform1);
			listt.add(transform2);
			ref = xmlSigFactory.newReference("#"+reference_URI, xmlSigFactory.newDigestMethod(DigestMethod.SHA1, null),listt,null, null);	
			signedInfo = xmlSigFactory.newSignedInfo(xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,(C14NMethodParameterSpec) null),xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),Collections.singletonList(ref));

		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (InvalidAlgorithmParameterException ex) {
			ex.printStackTrace();
		}

		KeyInfoFactory keyInfoFactory = xmlSigFactory.getKeyInfoFactory();

		ArrayList x509Content = new ArrayList();
		x509Content.add(x509Cert);
		X509Data xd = keyInfoFactory.newX509Data(x509Content);
		KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(xd));

		XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
		try {
			//Sign the document
			xmlSignature.sign(domSignCtx);
		}catch(Exception e){
			e.printStackTrace();
		}
		return root;
	}
	
	private Element newAttrTag(Document doc,String attribute,String value)
	{
		Element attributeTag = doc.createElement("Attribute");
		attributeTag.setAttribute("Name", attribute);
		Element attributeValue = doc.createElement("AttributeValue");
		attributeValue.setAttribute("xsi:type", "xs:string");
		attributeValue.setTextContent(value);
		attributeTag.appendChild(attributeValue);
		return attributeTag;
	}

	private String getNotBeforeDateAndTime() {
		Calendar beforeCal = Calendar.getInstance();
		beforeCal.add(Calendar.MINUTE, -5);
		return SAML_DATE_FORMAT.format(beforeCal.getTime());
	}

	private String getNotOnOrAfterDateAndTime() {
		Calendar afterCal = Calendar.getInstance();
		afterCal.add(Calendar.MINUTE, 15);
		return SAML_DATE_FORMAT.format(afterCal.getTime());
	}

	private String decodeSAMLRequest(String encodedStr) {
		try {
			byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(encodedStr);
			Inflater inf = new Inflater(true);

			inf.setInput(decoded);
			byte[] message = new byte[5000];
			int resultLength = inf.inflate(message);
			inf.end();
			return new String(message, 0, resultLength, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
}