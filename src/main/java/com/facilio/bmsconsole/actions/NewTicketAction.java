package com.facilio.bmsconsole.actions;

import java.util.Map;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
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
	
	@Override
	public String execute() throws Exception {
		
		statusList = TicketContext.getAllStatus();
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		
		agentList = UserAPI.getOrgUsers(orgId);
		assetList = AssetsAPI.getOrgAssets(orgId);
		
		return SUCCESS;
	}
}
