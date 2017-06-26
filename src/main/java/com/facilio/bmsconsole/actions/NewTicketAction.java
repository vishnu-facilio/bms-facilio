package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class NewTicketAction extends ActionSupport {
	
	private Map<Integer, String> statusList;
	public Map<Integer, String> getStatusList() {
		return statusList;
	}
	public void setStatusList(Map<Integer, String> statusList) {
		this.statusList = statusList;
	}
	
	private Map<Long, String> agentList;
	public Map<Long, String> getAgentList() {
		return agentList;
	}
	public void setAgentList(Map<Long, String> agentList) {
		this.agentList = agentList;
	}
	
	private Map<Long, String> assetList;
	public Map<Long, String> getAssetList() {
		return assetList;
	}
	public void setAssetList(Map<Long, String> assetList) {
		this.assetList = assetList;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private List<String> customFields;
	public List<String> getCustomFields() {
		return customFields;
	}
	public void setCustomFields(List<String> customFields) {
		this.customFields = customFields;
	}
	
	@Override
	public String execute() throws Exception {
		
		statusList = TicketContext.getAllStatus();
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		
		agentList = UserAPI.getOrgUsers(orgId);
		assetList = AssetsAPI.getOrgAssets(orgId);
		moduleName = CFUtil.getModuleName("Tickets_Objects", orgId);
		
		List<FacilioCustomField> cfs = CFUtil.getCustomFields("Tickets_Objects", "Tickets_Fields", orgId);
		
		customFields = new ArrayList<>();
		for(FacilioCustomField field : cfs) {
			customFields.add(field.getFieldName());
		}
		Map mp = ActionContext.getContext().getParameters();
		System.out.println(mp.get("ajax").getClass().getName());;
		String isajax =((org.apache.struts2.dispatcher.Parameter)mp.get("ajax")).getValue();
		
		System.out.println("isajaxisajax"+isajax);;

		if(isajax!=null && isajax.equals("true")){
			return "ajaxsuccess";
		}
		
		return SUCCESS;
	}
}
