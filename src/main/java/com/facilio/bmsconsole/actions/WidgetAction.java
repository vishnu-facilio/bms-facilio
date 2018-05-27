package com.facilio.bmsconsole.actions;

import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.opensymphony.xwork2.ActionSupport;

public class WidgetAction extends ActionSupport {

	public Long ouid;

	public Long getOuid() {
		return ouid;
	}

	public void setOuid(Long ouid) {
		this.ouid = ouid;
	}
	
	JSONObject result;
	
	
	public JSONObject getResult() {
		return result;
	}

	public void setResult(JSONObject result) {
		this.result = result;
	}
	User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserCardData() throws Exception {
		
		user = AccountUtil.getUserBean().getUser(ouid);
		
		List<WorkOrderContext> openWorkOrders = WorkOrderAPI.getOpenWorkOrderForUser(user.getOuid());
		
		List<WorkOrderContext> dueTodayWorkOrders = WorkOrderAPI.getDueTodayWorkOrders(openWorkOrders);
		
		List<WorkOrderContext> overDueWorkOrders = WorkOrderAPI.getOverdueWorkOrders(openWorkOrders);
		
		result = result == null ? new JSONObject() :result;
		
		result.put("openWorkOrdersCount", openWorkOrders == null ? 0 :openWorkOrders.size());
		result.put("dueTodayWorkOrdersCount", dueTodayWorkOrders == null ? 0 :dueTodayWorkOrders.size());
		result.put("overDueWorkOrdersCount", overDueWorkOrders == null ? 0 :overDueWorkOrders.size());
		
		return SUCCESS;
	}
}
