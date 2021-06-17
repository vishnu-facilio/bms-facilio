package com.facilio.bmsconsole.actions;

import java.util.List;

import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DigestConfigContext;
import com.facilio.bmsconsole.context.DigestMailTemplateMapContext;
import com.facilio.bmsconsole.util.DigestConfigAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.ScheduleInfo;

public class DigestMailConfigurationAction extends FacilioAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JSONArray expressions;
	
	
	public JSONArray getExpressions() {
		return expressions;
	}
	public void setExpressions(JSONArray expressions) {
		this.expressions = expressions;
	}
	private ActionContext action;
	public ActionContext getAction() {
		return action;
	}
	public void setAction(ActionContext action) {
		this.action = action;
	}
	private Integer defaultTemplate;
	
	
	public Integer getDefaultTemplate() {
		return defaultTemplate;
	}
	public void setDefaultTemplate(Integer defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}
	
	private ScheduleInfo scheduleInfo;
	
	public ScheduleInfo getScheduleInfo() {
		return scheduleInfo;
	}
	public void setScheduleInfo(ScheduleInfo scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	
	private long configId;
	
	
	public long getConfigId() {
		return configId;
	}
	public void setConfigId(long configId) {
		this.configId = configId;
	}
	private Boolean status;
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	private List<DigestMailTemplateMapContext> digestTemplates;
	
	public List<DigestMailTemplateMapContext> getDigestTemplates() {
		return digestTemplates;
	}
	public void setDigestTemplates(List<DigestMailTemplateMapContext> digestTemplates) {
		this.digestTemplates = digestTemplates;
	}
	
	private String toAddr;
	public String getToAddr() {
		return toAddr;
	}
	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}
	
	private DigestConfigContext config;
	
	public DigestConfigContext getConfig() {
		return config;
	}
	public void setConfig(DigestConfigContext config) {
		this.config = config;
	}
	
	public String addOrUpdateDigestConfig() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		facilioContext.put(FacilioConstants.ContextNames.WORK_FLOW_EXPRESSIONS, expressions);
		facilioContext.put(FacilioConstants.ContextNames.SCHEDULE_INFO, scheduleInfo);
		facilioContext.put(FacilioConstants.ContextNames.CONFIG, config);
		
		FacilioChain addAction = TransactionChainFactory.getAddOrUpdateDigestMailConfigChain();
		addAction.execute(facilioContext);
		setResult(SUCCESS,  "success");
		return SUCCESS;
			
	}
	
	public String activateDigestConfig() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		facilioContext.put(FacilioConstants.ContextNames.DIGEST_CONFIG_ID, configId);
		facilioContext.put(FacilioConstants.ContextNames.STATUS, status);
		
		FacilioChain addAction = TransactionChainFactory.getActivateDigestConfigChain();
		addAction.execute(facilioContext);
		setResult(FacilioConstants.ContextNames.DIGEST_CONFIG,  configId);
		return SUCCESS;
			
	}
	
	public String deleteDigestConfig() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		facilioContext.put(FacilioConstants.ContextNames.DIGEST_CONFIG_ID, configId);
		
		FacilioChain addAction = TransactionChainFactory.getDeleteDigestConfigChain();
		addAction.execute(facilioContext);
		setResult(FacilioConstants.ContextNames.DIGEST_CONFIG,  configId);
		return SUCCESS;
			
	}
	
	public String getAllDigestConfigs() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		
		FacilioChain getConfigChain = TransactionChainFactory.getAllDigestConfigChain();
		getConfigChain.execute(facilioContext);
		 
		setResult(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST, facilioContext.get(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST) );
		return SUCCESS;
			
	}
	
	public String getEnabledDigestConfigs() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		
		FacilioChain getConfigChain = TransactionChainFactory.getAllEnabledDigestConfigChain();
		getConfigChain.execute(facilioContext);
		 
		setResult(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST, facilioContext.get(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST) );
		return SUCCESS;
			
	}
	
	public String sendInstantDigestMail() throws Exception {
		DigestConfigAPI.sendInstantDigestMail(configId, toAddr);
		setResult("action", "success");
		return SUCCESS;
	}
	
	public String viewEnabledDigestConfig() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		facilioContext.put(FacilioConstants.ContextNames.DIGEST_CONFIG_ID, configId);
		FacilioChain getConfigChain = TransactionChainFactory.getAllEnabledDigestConfigChain();
		getConfigChain.execute(facilioContext);
		 
		setResult(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST, facilioContext.get(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST) );
		return SUCCESS;
			
	}
	
}
