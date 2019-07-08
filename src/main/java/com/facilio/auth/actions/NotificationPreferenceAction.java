package com.facilio.auth.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.NotificationPreference;
import com.facilio.bmsconsole.context.NotificationPreferenceFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class NotificationPreferenceAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getAllNotificationPreferences() {
		List<NotificationPreference> list = NotificationPreferenceFactory.getModuleNotificationPreferences(moduleName);
		setResult("list", list);
		return SUCCESS;
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
	
	public String addWorkflow() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put("value", getValue());
		context.put("recordId", recordId);
		context.put("moduleName", moduleName);
		context.put("name", getName());
		
		Chain chain = TransactionChainFactory.getAddNotificationWorkflow();
		chain.execute(context);
		
		return SUCCESS;
	}
}
