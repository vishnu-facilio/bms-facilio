package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.opensymphony.xwork2.ActionSupport;

public class TicketStatusAction extends ActionSupport {
	
	public String statusList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain statusListChain = FacilioChainFactory.getTicketStatusListChain();
		statusListChain.execute(context);
		
		setStatuses((List<TicketStatusContext>) context.get(FacilioConstants.ContextNames.TICKET_STATUS_LIST));
		
		return SUCCESS;
	}
	
	private List<TicketStatusContext> statuses = null;
	public List<TicketStatusContext> getStatuses() {
		return statuses;
	}
	public void setStatuses(List<TicketStatusContext> statuses) {
		this.statuses = statuses;
	}
	
	public SetupLayout getSetup() {
		return SetupLayout.getTicketStatusListLayout();
	}
	
	public JSONObject getStateFlow() {
		return stateFlow;
	}
	public void setStateFlow(JSONObject stateFlow) {
		this.stateFlow = stateFlow;
	}

	private JSONObject stateFlow ;
	 
	public String showStateFlow()
	{
		try {
			ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
			
			stateFlow = new JSONObject();
			stateFlow.put("nextstates", bean.getStateFlow("workorder"));
			return SUCCESS;
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
