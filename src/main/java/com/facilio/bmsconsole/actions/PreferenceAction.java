package com.facilio.bmsconsole.actions;

import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class PreferenceAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long recordId;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private Map<String, Object> value;
	public Map<String, Object> getValue() {
		return value;
	}
	public void setValue(Map<String, Object> value) {
		this.value = value;
	}
	
	private long preferenceId;
	
	public long getPreferenceId() {
		return preferenceId;
	}
	public void setPreferenceId(long preferenceId) {
		this.preferenceId = preferenceId;
	}
	private boolean isModuleSpecific;
	
	public boolean isModuleSpecific() {
		return isModuleSpecific;
	}
	public void setModuleSpecific(boolean isModuleSpecific) {
		this.isModuleSpecific = isModuleSpecific;
	}
	public String enablePreference() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREFERENCE_VALUE_LIST, getValue());
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PREFERENCE_NAME, getName());
		context.put(FacilioConstants.ContextNames.PREFERENCE_ID, getPreferenceId());
		
		Chain chain = TransactionChainFactory.getEnablePreference();
		chain.execute(context);
		setResult(FacilioConstants.ContextNames.PREFERENCE_META, context.get(FacilioConstants.ContextNames.PREFERENCE_META));
		
		
		return SUCCESS;
	}
	
	public String disablePreference() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREFERENCE_ID, getPreferenceId());
		
		Chain chain = TransactionChainFactory.getDisablePreference();
		chain.execute(context);
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		
		return SUCCESS;
	}
	
	public String getAllPreferences() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.MODULE_SPECIFIC, isModuleSpecific());
		
		Chain chain = TransactionChainFactory.getAllPreferences();
		chain.execute(context);
		setResult(FacilioConstants.ContextNames.PREFERENCE_LIST, context.get(FacilioConstants.ContextNames.PREFERENCE_LIST));
		
		
		return SUCCESS;
	}
	
	
	public String getAllEnabledPreferences() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
		
		Chain chain = TransactionChainFactory.getAllEnabledPreferences();
		chain.execute(context);
		setResult(FacilioConstants.ContextNames.PREFERENCE_LIST, context.get(FacilioConstants.ContextNames.PREFERENCE_LIST));
		
		
		return SUCCESS;
	}
}
