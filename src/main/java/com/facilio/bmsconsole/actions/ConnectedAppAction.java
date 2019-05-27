package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.auth.SAMLAttribute;
import com.facilio.fw.auth.SAMLUtil;
import org.apache.commons.chain.Chain;
import org.apache.struts2.ServletActionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class ConnectedAppAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	
	
	private ConnectedAppContext connectedApp;

	public ConnectedAppContext getConnectedApp() {
		return connectedApp;
	}

	public void setConnectedApp(ConnectedAppContext connectedApp) {
		this.connectedApp = connectedApp;
	}

	private long connectedAppId;

	public long getConnectedAppId() {
		return connectedAppId;
	}

	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}

	public String addConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, connectedApp);
		
		Chain addItem = TransactionChainFactory.getAddOrUpdateConnectedAppChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}

	public String updateConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, connectedApp);
		context.put(FacilioConstants.ContextNames.ID, connectedApp.getId());

		Chain updateItemChain = TransactionChainFactory.getAddOrUpdateConnectedAppChain();
		updateItemChain.execute(context);
		setConnectedAppId(connectedApp.getId());
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}
	
	public String deleteConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, connectedAppId);

		Chain updateItemChain = TransactionChainFactory.getDeleteConnectedAppChain();
		updateItemChain.execute(context);
		setResult(FacilioConstants.ContextNames.ID, connectedAppId);
		return SUCCESS;
	}
	
	private String linkName;
	
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	public String getLinkName() {
		return this.linkName;
	}

	public String connectedAppDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getConnectedAppId());
		context.put(FacilioConstants.ContextNames.LINK_NAME, getLinkName());

		Chain fetchDetailsChain = ReadOnlyChainFactory.fetchConnectedAppDetails();
		fetchDetailsChain.execute(context);

		setConnectedApp((ConnectedAppContext) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}
	
	private List<ConnectedAppContext> connectedAppList;

	public List<ConnectedAppContext> getConnectedAppList() {
		return connectedAppList;
	}

	public void setConnectedAppList(List<ConnectedAppContext> connectedAppList) {
		this.connectedAppList = connectedAppList;
	}

	public String connectedAppList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain fetchListChain = ReadOnlyChainFactory.getConnectedAppsList();
		fetchListChain.execute(context);
		
		connectedAppList = (List<ConnectedAppContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedAppList);
		return SUCCESS;
	}
	
	private String acsUrl;
	private String samlResponse;
	private String relayState;
	
	public String getAcsUrl() {
		return acsUrl;
	}

	public void setAcsUrl(String acsUrl) {
		this.acsUrl = acsUrl;
	}

	public String getSamlResponse() {
		return samlResponse;
	}

	public void setSamlResponse(String samlResponse) {
		this.samlResponse = samlResponse;
	}

	public String getRelayState() {
		return relayState;
	}

	public void setRelayState(String relayState) {
		this.relayState = relayState;
	}
	
	public static String getServerURL(HttpServletRequest request) {
		
		String URL = request.getScheme() + "://" + request.getServerName();
		if (request.getServerPort() != 80 && request.getServerPort() != 443) {
			URL = URL + ":" + request.getServerPort();
		}
		return URL;
	}
	
	private InputStream downloadStream;
	
	public InputStream getDownloadStream() {
		return downloadStream;
	}

	public void setDownloadStream(InputStream downloadStream) {
		this.downloadStream = downloadStream;
	}
	
	public String downloadCertificate() throws Exception {
		File privateKeyFile = new File(ConnectedAppAction.class.getClassLoader().getResource("conf/saml/saml.crt").getFile());
		this.downloadStream = new FileInputStream(privateKeyFile); 
		return SUCCESS;
	}

	public String samlLogin() throws Exception {
		
		HttpServletRequest req = ServletActionContext.getRequest();
		
		String samlRequest = req.getParameter("SAMLRequest");
		String relay = req.getParameter("RelayState");
		
		this.connectedAppDetails();
		
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		
		if (this.getConnectedApp().getSamlEnabled() != null && this.getConnectedApp().getSamlEnabled()) {
		
			String requestId = null;
			String acsURL = null;
			String spEntityId = null;
			
			if (samlRequest != null) {
				// SP initiated login
				String decodedsamlRequest = SAMLUtil.decodeSAMLRequest(samlRequest);
				Document document = SAMLUtil.convertStringToDocument(decodedsamlRequest);
				document.getDocumentElement().normalize();
	
				Element authnRequestElement = (Element) document.getFirstChild();
				acsURL = authnRequestElement.getAttribute("AssertionConsumerServiceURL");
				requestId = authnRequestElement.getAttribute("ID");
				spEntityId = document.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer").item(0).getTextContent();
			}
			else {
				// IdP initiated login
				acsURL = getConnectedApp().getSpAcsUrl();
				spEntityId = getConnectedApp().getSpEntityId();
				requestId = "FAC_" + UUID.randomUUID().toString().replace("-", "");
			}
			
			SAMLAttribute attr = new SAMLAttribute()
					.setIssuer(AwsUtil.getClientAppUrl() + "/app/connectedapp/" + this.getConnectedApp().getLinkName())
					.setIntendedAudience(spEntityId)
					.setInResponseTo(requestId)
					.setRecipient(acsURL)
					.setEmail(AccountUtil.getCurrentUser().getEmail());
			
			String samlResponse = SAMLUtil.generateSignedSAMLResponse(attr);
			
			setResult("acsURL", acsURL);
			setResult("samlResponse", samlResponse);
			setResult("relay", relay);
		}
		
		return SUCCESS;
	}
}
