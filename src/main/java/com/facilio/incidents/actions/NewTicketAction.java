package com.facilio.incidents.actions;

import java.util.Map;

import com.facilio.assets.AssetsAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.incidents.tickets.TicketContext;
import com.facilio.users.UserAPI;
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
