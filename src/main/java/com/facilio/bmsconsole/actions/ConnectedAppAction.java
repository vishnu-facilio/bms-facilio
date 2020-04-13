package com.facilio.bmsconsole.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppContext.HostingType;
import com.facilio.bmsconsole.context.ConnectedAppSAMLContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.auth.SAMLAttribute;
import com.facilio.fw.auth.SAMLUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class ConnectedAppAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	
	
	private ConnectedAppContext connectedApp;

	public ConnectedAppContext getConnectedApp() {
		return connectedApp;
	}

	public void setConnectedApp(ConnectedAppContext connectedApp) {
		this.connectedApp = connectedApp;
	}
	
	private ConnectedAppSAMLContext connectedAppSAML;

	public ConnectedAppSAMLContext getConnectedAppSAML() {
		return connectedAppSAML;
	}

	public void setConnectedAppSAML(ConnectedAppSAMLContext connectedAppSAML) {
		this.connectedAppSAML = connectedAppSAML;
	}

	private long connectedAppId;

	public long getConnectedAppId() {
		return connectedAppId;
	}

	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}
	
	public String addOrUpdateConnectedAppSAML() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, connectedAppSAML);
		FacilioChain addItem = TransactionChainFactory.getAddOrUpdateConnectedAppSAMLChain();
		addItem.execute(context);
		return SUCCESS;
	}

	public String addConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		connectedApp.setHostingType(HostingType.EXTERNAL.getValue());
		connectedApp.setIsActive(true);
		connectedApp.setProductionBaseUrl(connectedApp.getSandBoxBaseUrl());
		context.put(FacilioConstants.ContextNames.RECORD, connectedApp);
		FacilioChain addItem = TransactionChainFactory.getAddOrUpdateConnectedAppChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}

	public String updateConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		connectedApp.setProductionBaseUrl(connectedApp.getSandBoxBaseUrl());
		context.put(FacilioConstants.ContextNames.RECORD, connectedApp);
		context.put(FacilioConstants.ContextNames.ID, connectedApp.getId());

		FacilioChain updateItemChain = TransactionChainFactory.getAddOrUpdateConnectedAppChain();
		updateItemChain.execute(context);
		setConnectedAppId(connectedApp.getId());
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}
	
	public String deleteConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, connectedAppId);

		FacilioChain updateItemChain = TransactionChainFactory.getDeleteConnectedAppChain();
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

		FacilioChain fetchDetailsChain = ReadOnlyChainFactory.fetchConnectedAppDetails();
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
	
	private List<ConnectedAppSAMLContext> connectedAppSAMLList;

	public List<ConnectedAppSAMLContext> getConnectedAppSAMLList() {
		return connectedAppSAMLList;
	}

	public void setConnectedAppSAMLList(List<ConnectedAppSAMLContext> connectedAppSAMLList) {
		this.connectedAppSAMLList = connectedAppSAMLList;
	}

	public String connectedAppList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		FacilioChain fetchListChain = ReadOnlyChainFactory.getConnectedAppsList();
		fetchListChain.execute(context);
		
		connectedAppList = (List<ConnectedAppContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		connectedAppSAMLList = (List<ConnectedAppSAMLContext>) context.get(FacilioConstants.ContextNames.CONNECTEDAPP_SAML_LIST);
		
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedAppList);
		setResult(FacilioConstants.ContextNames.CONNECTEDAPP_SAML_LIST, connectedAppSAMLList);
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
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppSAMLFields())
				.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedApp.getId()), NumberOperators.EQUALS));
		Map<String, Object> props = selectBuilder.fetchFirst();
		
		if (props != null && !props.isEmpty()) {
			connectedAppSAML = FieldUtil.getAsBeanFromMap(props, ConnectedAppSAMLContext.class);
		}
		
		
		if (connectedAppSAML != null) {
		
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
				acsURL = connectedAppSAML.getSpAcsUrl();
				spEntityId = connectedAppSAML.getSpEntityId();
				requestId = "FAC_" + UUID.randomUUID().toString().replace("-", "");
			}
			
			SAMLAttribute attr = new SAMLAttribute()
					.setIssuer(FacilioProperties.getClientAppUrl() + "/app/connectedapp/" + this.getConnectedApp().getLinkName())
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
