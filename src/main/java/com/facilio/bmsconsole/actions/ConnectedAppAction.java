package com.facilio.bmsconsole.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectedAppConnectorContext;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppContext.HostingType;
import com.facilio.bmsconsole.context.ConnectedAppRequestContext;
import com.facilio.bmsconsole.context.ConnectedAppSAMLContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsole.context.VariableContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.auth.SAMLAttribute;
import com.facilio.fw.auth.SAMLUtil;
import com.facilio.modules.FacilioModule;
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

	private ConnectedAppWidgetContext connectedAppWidget;

	public ConnectedAppWidgetContext getConnectedAppWidget() {
		return connectedAppWidget;
	}

	public void setConnectedAppWidget(ConnectedAppWidgetContext connectedAppWidget) {
		this.connectedAppWidget = connectedAppWidget;
	}
	
	private ConnectedAppConnectorContext connectedAppConnector;

	public ConnectedAppConnectorContext getConnectedAppConnector() {
		return connectedAppConnector;
	}

	public void setConnectedAppConnector(ConnectedAppConnectorContext connectedAppConnector) {
		this.connectedAppConnector = connectedAppConnector;
	}

	private VariableContext variable;
	
	public VariableContext getVariable() {
		return variable;
	}

	public void setVariable(VariableContext variable) {
		this.variable = variable;
	}

	private long connectedAppId;

	public long getConnectedAppId() {
		return connectedAppId;
	}

	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}

	public String addOrUpdateConnectedAppSAML() throws Exception {
		
		FacilioChain addItem = TransactionChainFactory.getAddOrUpdateConnectedAppSAMLChain();
		addItem.getContext().put(FacilioConstants.ContextNames.RECORD, connectedAppSAML);
		addItem.execute();
		
		return SUCCESS;
	}

	public String deleteConnectedAppSAML() throws Exception {
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppId), NumberOperators.EQUALS));

		builder.delete();
		
		return SUCCESS;
	}

	public String deleteConnectedAppConnector() throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getConnectedAppConnectorsModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(connectedAppConnector.getId(), ModuleFactory.getConnectedAppConnectorsModule()));

		builder.delete();
		
		return SUCCESS;
	}
	
	public String deleteConnectedAppWidget() throws Exception {

		if (connectedAppWidget.getCriteriaId() > 0) {
			CriteriaAPI.deleteCriteria(connectedAppWidget.getCriteriaId());
		}

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(connectedAppWidget.getId(), ModuleFactory.getConnectedAppWidgetsModule()));

		builder.delete();
		
		return SUCCESS;
	}
	
	public String deleteVariable() throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getVariablesModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("NAME","name",name, StringOperators.IS));

		builder.delete();
		
		return SUCCESS;
	}
	
	public String getVariableByName() throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getVariablesFields())
				.table(ModuleFactory.getVariablesModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID", "connectedAppId", String.valueOf(connectedAppId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("NAME","name",name, StringOperators.IS));
		
		Map<String, Object> props = selectBuilder.fetchFirst();
		if (props != null && !props.isEmpty()) {
			variable = FieldUtil.getAsBeanFromMap(props, VariableContext.class);
		}
		setResult("variable", variable);
		
		return SUCCESS;
	}

	public String addOrUpdateConnectedAppWidget() throws Exception {
		
		if (connectedAppWidget.getCriteriaId() > 0){
			CriteriaAPI.deleteCriteria(connectedAppWidget.getCriteriaId());
			if(connectedAppWidget.getCriteria() == null){
				connectedAppWidget.setCriteriaId(-99);
			}
		}
		if(connectedAppWidget.getCriteria() != null){
			long criteriaId = CriteriaAPI.addCriteria(connectedAppWidget.getCriteria());
			connectedAppWidget.setCriteriaId(criteriaId);
		}

		FacilioChain addItem = TransactionChainFactory.getAddOrUpdateConnectedAppWidgetChain();
		addItem.getContext().put(FacilioConstants.ContextNames.RECORD, connectedAppWidget);
		addItem.execute();
		
		connectedAppWidget = (ConnectedAppWidgetContext) addItem.getContext().get(FacilioConstants.ContextNames.RECORD);
		
		setResult("connectedAppWidget", connectedAppWidget);
		return SUCCESS;
	}
	
	public String addOrUpdateConnectedAppConnector() throws Exception {
		FacilioChain addItem = TransactionChainFactory.getAddOrUpdateConnectedAppConnectorChain();
		addItem.getContext().put(FacilioConstants.ContextNames.RECORD, connectedAppConnector);
		addItem.execute();
		
		connectedAppConnector = (ConnectedAppConnectorContext) addItem.getContext().get(FacilioConstants.ContextNames.RECORD);
		
		setResult("connectedAppConnector", connectedAppConnector);
		return SUCCESS;
	}
	
	public String addOrUpdateVariable() throws Exception {
		FacilioChain addItem = TransactionChainFactory.getAddOrUpdateVariableChain();
		addItem.getContext().put("upsert", upsert);
		addItem.getContext().put(FacilioConstants.ContextNames.RECORD, variable);
		addItem.execute();
		
		variable = (VariableContext) addItem.getContext().get(FacilioConstants.ContextNames.RECORD);
		
		setResult("variable", variable);
		return SUCCESS;
	}

	public String addConnectedApp() throws Exception {
		
		connectedApp.setHostingType(HostingType.EXTERNAL.getValue());
		connectedApp.setIsActive(true);
		connectedApp.setProductionBaseUrl(connectedApp.getSandBoxBaseUrl());
		
		FacilioChain addItem = TransactionChainFactory.getAddOrUpdateConnectedAppChain();
		addItem.getContext().put(FacilioConstants.ContextNames.RECORD, connectedApp);
		addItem.execute();
		
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}

	public String updateConnectedApp() throws Exception {
		
		connectedApp.setProductionBaseUrl(connectedApp.getSandBoxBaseUrl());

		FacilioChain updateItemChain = TransactionChainFactory.getAddOrUpdateConnectedAppChain();
		updateItemChain.getContext().put(FacilioConstants.ContextNames.RECORD, connectedApp);
		updateItemChain.getContext().put(FacilioConstants.ContextNames.ID, connectedApp.getId());
		
		updateItemChain.execute();
		
		setConnectedAppId(connectedApp.getId());
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		
		return SUCCESS;
	}

	public String deleteConnectedApp() throws Exception {
		
		FacilioChain updateItemChain = TransactionChainFactory.getDeleteConnectedAppChain();
		updateItemChain.getContext().put(FacilioConstants.ContextNames.ID, connectedAppId);
		updateItemChain.execute();
		
		setResult(FacilioConstants.ContextNames.ID, connectedAppId);
		return SUCCESS;
	}

	public String getConnectedAppDetails() throws Exception {
		
		FacilioChain fetchDetailsChain = ReadOnlyChainFactory.fetchConnectedAppDetails();
		fetchDetailsChain.getContext().put(FacilioConstants.ContextNames.ID, getConnectedAppId());
		fetchDetailsChain.execute();

		setConnectedApp((ConnectedAppContext) fetchDetailsChain.getContext().get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.CONNECTED_APP, connectedApp);
		
		return SUCCESS;
	}
	
	public String getConnectedAppModules() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> sysytemModules = new ArrayList<>();
        sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.ASSET));
        sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.ALARM));
//        sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VISITOR)) {
        	sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR));
//        	sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS));
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS));
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.TENANT));
       }
//       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
//    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
//    	   sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
//       }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SERVICE_REQUEST)) {
            sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
        }
        sysytemModules.add(modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE));
        
        List<FacilioModule> customModules = new ArrayList<>();

        customModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));
        
        List<FacilioModule> modules = new ArrayList<FacilioModule>();
        modules.addAll(sysytemModules);
        modules.addAll(customModules);
        
        modules =  modules.stream().filter(m->m!= null).collect(Collectors.toList());
        
		setResult("modules", modules);
		
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
		
		FacilioChain fetchListChain = ReadOnlyChainFactory.getConnectedAppsList();
		fetchListChain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONNECTED_APPS);
		
		if (getFilters() != null) {
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		fetchListChain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
 		}
		
		fetchListChain.execute();

		connectedAppList = (List<ConnectedAppContext>) fetchListChain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);

		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedAppList);
		return SUCCESS;
	}
	
	private List<ConnectedAppWidgetContext> connectedAppWidgetList;

	public List<ConnectedAppWidgetContext> getConnectedAppWidgetList() {
		return connectedAppWidgetList;
	}

	public void setConnectedAppWidgetList(List<ConnectedAppWidgetContext> connectedAppWidgetList) {
		this.connectedAppWidgetList = connectedAppWidgetList;
	}

	public String connectedAppWidgetList() throws Exception {
		
		FacilioChain fetchListChain = ReadOnlyChainFactory.getConnectedAppWidgetsList();
		fetchListChain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONNECTED_APP_WIDGETS);
		
		if (getFilters() != null) {
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		fetchListChain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
 		}
		
		fetchListChain.execute();

		connectedAppWidgetList = (List<ConnectedAppWidgetContext>) fetchListChain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);

		setResult(FacilioConstants.ContextNames.CONNECTED_APP_WIDGETS, connectedAppWidgetList);
		return SUCCESS;
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
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String linkName;

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getLinkName() {
		return this.linkName;
	}

	public String viewConnectedApp() throws Exception {

		FacilioChain viewConnectedAppChain = ReadOnlyChainFactory.getViewConnectedAppChain();
		viewConnectedAppChain.getContext().put(FacilioConstants.ContextNames.ID, getConnectedAppId());
		viewConnectedAppChain.getContext().put(FacilioConstants.ContextNames.LINK_NAME, getLinkName());

		viewConnectedAppChain.execute();

		ConnectedAppContext connectedApp = (ConnectedAppContext) viewConnectedAppChain.getContext().get(FacilioConstants.ContextNames.CONNECTED_APP);
		String viewURL = (String) viewConnectedAppChain.getContext().get(FacilioConstants.ContextNames.CONNECTED_APP_VIEW_URL);

		handleSAMLResponse(connectedApp, viewURL);

		return SUCCESS;
	}
	
	private boolean upsert = false;
	
	public boolean isUpsert() {
		return upsert;
	}

	public void setUpsert(boolean upsert) {
		this.upsert = upsert;
	}

	private long widgetId;

	public long getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(long widgetId) {
		this.widgetId = widgetId;
	}

	private Long recordId;

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public String viewWidget() throws Exception {

		FacilioChain viewConnectedAppWidgetChain = ReadOnlyChainFactory.getViewConnectedAppWidgetChain();
		viewConnectedAppWidgetChain.getContext().put(FacilioConstants.ContextNames.ID, getWidgetId());
		viewConnectedAppWidgetChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());

		viewConnectedAppWidgetChain.execute();

		ConnectedAppContext connectedApp = (ConnectedAppContext) viewConnectedAppWidgetChain.getContext().get(FacilioConstants.ContextNames.CONNECTED_APP);
		ConnectedAppWidgetContext connectedAppWidget = (ConnectedAppWidgetContext) viewConnectedAppWidgetChain.getContext().get(FacilioConstants.ContextNames.CONNECTED_APP_WIDGET);
		String viewURL = (String) viewConnectedAppWidgetChain.getContext().get(FacilioConstants.ContextNames.CONNECTED_APP_VIEW_URL);

		setResult("connectedAppWidget", connectedAppWidget);
		handleSAMLResponse(connectedApp, viewURL);

		return SUCCESS;
	}

	private void handleSAMLResponse(ConnectedAppContext connectedApp, String viewURL) throws Exception {

		HttpServletRequest req = ServletActionContext.getRequest();

		ConnectedAppSAMLContext connectedAppSAML = ConnectedAppAPI.getConnectedAppSAML(connectedApp.getId());

		if (connectedAppSAML != null) {
			// saml enabled connected apps

			String samlRequest = req.getParameter("SAMLRequest");

			String requestId = null;
			String acsURL = null;
			String spEntityId = null;
			String relayState = null;

			if (samlRequest != null) {
				// SP Initiated Login
				relayState = req.getParameter("RelayState");

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

			if (relayState == null) {
				relayState = viewURL;
			}

			String issuer = FacilioProperties.getClientAppUrl() + "/app/connectedapp/" + connectedApp.getLinkName(); 

			SAMLAttribute attr = new SAMLAttribute()
					.setIssuer(issuer)
					.setIntendedAudience(spEntityId)
					.setInResponseTo(requestId)
					.setRecipient(acsURL)
					.setEmail(AccountUtil.getCurrentUser().getEmail());

			String samlResponse = SAMLUtil.generateSignedSAMLResponse(attr);

			setResult("acsURL", acsURL);
			setResult("samlResponse", samlResponse);
			setResult("relay", relayState);
		}

		setResult("viewUrl", viewURL);
	}
	
	private ConnectedAppRequestContext apiRequest;
	
	public void setApiRequest(ConnectedAppRequestContext apiRequest) {
		this.apiRequest = apiRequest;
	}
	
	public ConnectedAppRequestContext getApiRequest() {
		return this.apiRequest;
	}
	
	public String executeAPI() throws Exception {
		
		FacilioChain executeApiChain = ReadOnlyChainFactory.getConnectedAppExecuteAPIChain();
		executeApiChain.getContext().put(FacilioConstants.ContextNames.CONNECTED_APP_REQUEST, apiRequest);
		executeApiChain.execute();

		String apiResponse = (String) executeApiChain.getContext().get(FacilioConstants.ContextNames.RESULT);
		setResult("apiResponse", apiResponse);
		
		return SUCCESS;
	}
}
