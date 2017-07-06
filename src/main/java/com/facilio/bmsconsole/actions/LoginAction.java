package com.facilio.bmsconsole.actions;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.OrgApi;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.cognito.CognitoUtil;
import com.facilio.fw.OrgInfo;
import com.facilio.saml.SAMLAttribute;
import com.facilio.saml.SAMLUtil;
import com.facilio.sql.SQLScriptRunner;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import org.apache.commons.chain.Chain;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class LoginAction extends ActionSupport{
	static
	{
		System.out.println("Login action loaded");
	}
	private static String HOSTNAME = null;
	
	private static final SimpleDateFormat SAML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public String execute() throws Exception {
		
		if(HOSTNAME==null)
		{
			HOSTNAME = (String)ActionContext.getContext().getApplication().get("DOMAINNAME");
		}
        
		return SUCCESS;
	}
	
	public String validateLogin() throws Exception {
		
		if(HOSTNAME==null)
		{
			HOSTNAME = (String)ActionContext.getContext().getApplication().get("DOMAINNAME");
		}
		
		// get the identity token and decode it.
		String idToken = getIdToken();
		if (idToken == null) {
			return SUCCESS;
		}
		
		try {
			String identityId = CognitoUtil.getIdentityId(idToken);
			System.out.println("identityId ======>  "+identityId);

			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
			AWSIot awsIot = AWSIotClientBuilder.standard().withRegion("us-west-2").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

			AttachPrincipalPolicyRequest policyResult = new AttachPrincipalPolicyRequest().withPolicyName("EM-Policy").withPrincipal(identityId);
			awsIot.attachPrincipalPolicy(policyResult);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		int tempaccesscode = idToken.hashCode();
		System.out.println("The temp access code is:"+tempaccesscode);
		ActionContext.getContext().getApplication().put(tempaccesscode+"", idToken);
		Base64.Decoder decoder = Base64.getUrlDecoder();

		// the identity token has three block 0-header, 1-payload, 2-signature
		// 1-payload has the actual required user information.
		String[] payloads = idToken.split("\\.");

		// decode the payload block and make it as a json object
		JSONObject jsonObject = ((JSONObject) (JSONValue.parse(new String(decoder.decode(payloads[1]))))); 

		System.out.println("The JSON Object"+jsonObject);

		Map session = ActionContext.getContext().getSession(); 

		String userName = (String)jsonObject.get("email");
		boolean verifiedUser = ((Boolean) jsonObject.get("email_verified")).booleanValue();

		System.out.println(verifiedUser);

		if (verifiedUser && userName!=null)
		{
			String subdomain = null;
			try {
				subdomain = OrgInfo.getDefaultOrgInfo(userName);
			} catch (Exception e) {
				
				/*  
				 * If the user exists in Cognito but not exists in the local db, the below code will auto populate the org, user data into local db
				 */
				
				AdminGetUserResult userResult = CognitoUtil.getUser(userName);
				List<AttributeType> li = userResult.getUserAttributes();
				for (AttributeType attr : li) {
					if (attr.getName().equals("custom:orgName")) {
						subdomain = attr.getValue();
						break;
					}
				}
				if (subdomain == null || "".equals(subdomain.trim())) {
					// for local testing only
					subdomain = "dummy";
				}
				checkAndPopulateOrgInfo(subdomain, userName);
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			String redirecturl =request.getScheme()+"://"+subdomain + HOSTNAME+":"+request.getServerPort()+request.getContextPath() +"/home/index?accesscode="+tempaccesscode;

			session.put("USERNAME", userName);
			session.put("USER_ACCESSCODE", tempaccesscode+"");

			String samlRequest = request.getParameter("isSAML");
			
			if (samlRequest != null && Boolean.parseBoolean(samlRequest)) {
				response = "reload";
			}
			else {
				response = redirecturl;
			}
		}
		else if (!verifiedUser && userName!=null)
		{
			//user is not verified
			response = "unverified_user";

		}
		else 
		{
			// else the user is not verified - redirect to the login page.
		}
		return SUCCESS;
	}
	
	private boolean checkAndPopulateOrgInfo(String subdomain, String email) throws Exception {
		java.sql.Connection con = FacilioConnectionPool.INSTANCE.getConnection();
		
		long orgId = OrgApi.getOrgIdFromDomain(subdomain);
		
		PreparedStatement ps = null;
		try {
			if (orgId == -1) {
				String insertquery = "insert into Organizations (ORGNAME,FACILIODOMAINNAME) values (?,?)";
				PreparedStatement ps1 = con.prepareStatement(insertquery, Statement.RETURN_GENERATED_KEYS);
				ps1.setString(1, subdomain);
				ps1.setString(2, subdomain);
				ps1.executeUpdate();
				ResultSet rs = ps1.getGeneratedKeys();
				rs.next();
				orgId = rs.getLong(1);
				rs.close();
				ps1.close();
				
				try {
					Map<String, String> paramValues = new HashMap<>(); 
					paramValues.put("orgId", String.valueOf(orgId));
					
					File insertModulesSQL = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/defaultModules.sql").getFile());
					SQLScriptRunner scriptRunner = new SQLScriptRunner(insertModulesSQL, true, paramValues);
					scriptRunner.runScript(con);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String insertquery = "insert into Users (COGNITO_ID,USER_VERIFIED,EMAIL) values (?,?,?)";
			ps = con.prepareStatement(insertquery);
			ps.setString(1, null);
			ps.setBoolean(2, true);
			ps.setString(3, email);
			ps.addBatch();
			insertquery = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,INVITATION_ACCEPT_STATUS) values ((select USERID from Users where EMAIL='"+email+"'),(select ORGID from Organizations where FACILIODOMAINNAME='"+subdomain+"'),UNIX_TIMESTAMP() ,true,true)";
			System.out.println("insert query "+insertquery);
			ps.addBatch(insertquery);
			ps.executeBatch();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			ps.close();
			con.close();
		}
		return false;
	}
	
	private String response = null;
	public String getResponse()
	{
		return response;
	}
	// getter setter for access token
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	private String accessToken;
	
	// getter setter for identity token
	private String idToken;
	
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
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
		
		String userName = (String)ActionContext.getContext().getSession().get("USERNAME");
		String userAccessCode = (String)ActionContext.getContext().getSession().get("USER_ACCESSCODE");
		if (userName != null) {
			// already login
			
			String subdomain = OrgInfo.getDefaultOrgInfo(userName);
			
			HttpServletRequest request = ServletActionContext.getRequest();
			
			String samlRequest = request.getParameter("SAMLRequest");
			String relay = request.getParameter("relayUrl");
			
			if (samlRequest != null && !"".equals(samlRequest.trim())) {
				
				boolean status = handleSAMLLogin(userName, subdomain, samlRequest, relay);
				
				if (status) {
					return "samlresponse";
				}
				else {
					return "loginpage";
				}
			}
			else {
				String redirecturl = request.getScheme()+"://"+subdomain + HOSTNAME+":"+request.getServerPort()+request.getContextPath() +"/home/index";
				if (userAccessCode != null) {
					redirecturl = redirecturl + "?accesscode=" + userAccessCode;
				}
				request.setAttribute("redirect_url", redirecturl);
				
				return "loginsuccess";
			}
		}
		return "loginpage";
	}
	
	public String logout() {
		
		ActionContext.getContext().getSession().remove("USERNAME");
		
		return SUCCESS;
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

		UserContext uc = UserAPI.getUser(curUser);
		
		JSONObject customAttr = new JSONObject();
		customAttr.put("Facilio User Id", uc.getUserId());
		customAttr.put("Facilio Org Id", uc.getOrgId());
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
	
	public String signup() throws Exception
	{
		FacilioContext orgsignupcontext = new FacilioContext();
		System.out.println("The parameters list"+ActionContext.getContext().getParameters());
		orgsignupcontext.put("signupinfo", getSignupInfo());
		Chain c = FacilioChainFactory.getOrgSignupChain();
		c.execute(orgsignupcontext);
		response ="<result>signupsuccess</result>";
		return "success";
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
