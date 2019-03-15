package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Map;

public class ControllerAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String deleteController() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, id);
		context.put(FacilioConstants.ContextNames.DEL_READING_RULE, deleteReading);
		
		Chain deleteControllerSettings = TransactionChainFactory.getDeleteControllerChain();
		deleteControllerSettings.execute(context);
		
		setResult("result", "success");
		return SUCCESS; 
	}
	
	private Boolean deleteReading;
	public Boolean getDeleteReading() {
		if (deleteReading == null) {
			return false;
		}
		return deleteReading;
	}
	public void setDeleteReading(Boolean deleteReading) {
		this.deleteReading = deleteReading;
	}


	private Long agentId ;

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	private JSONArray agentDetails;

	public JSONArray getAgentDetails() {
		return agentDetails;
	}

	public void setAgentDetails(JSONArray agentDetails){
		this.agentDetails = agentDetails;
	}

	public String agentAction() {
		if( (AccountUtil.getCurrentOrg() != null ) ) {
				List<Map<String, Object>> agentDetailsList;
				agentDetailsList = new AgentUtil(AccountUtil.getCurrentOrg().getId(), AccountUtil.getCurrentOrg().getDomain()).agentDetailsAPI();

				if (agentDetailsList != null) {
					JSONArray agentDetails = new JSONArray();
					agentDetails.addAll(agentDetailsList);
					setAgentDetails(agentDetails);
				}
				setResult("agentDetails", this.agentDetails);
				return SUCCESS;
		}
		else {
			return ERROR;
		}
	}

	public JSONArray controllerDetails;

	public JSONArray getControllerDetails() {
		return controllerDetails;
	}

	public void setControllerDetails(JSONArray controllerDetails) {
		this.controllerDetails = controllerDetails;
	}
	public String fetchControllerDetails(){
		if( (AccountUtil.getCurrentOrg() != null ) ) {
			Long agentId = getAgentId();
			List<Map<String, Object>> agentControllerList;
			agentControllerList = new AgentUtil(AccountUtil.getCurrentOrg().getId(), AccountUtil.getCurrentOrg().getDomain()).controllerDetailsAPI(agentId);
			if (agentControllerList != null) {
				JSONArray controllerDetails = new JSONArray();
				controllerDetails.addAll(agentControllerList);
				setControllerDetails(controllerDetails);
			}
			setResult("controllerDetails", this.controllerDetails);
			return SUCCESS;
		}
		else{
			return ERROR;
		}
	}


}
