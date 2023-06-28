package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.function.Function;

public class TicketStatusAction extends FacilioAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(TicketStatusAction.class.getName());
	public String statusList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		FacilioChain statusListChain = FacilioChainFactory.getTicketStatusListChain();
		statusListChain.execute(context);
		
		setStatuses((List<FacilioStatus>) context.get(FacilioConstants.ContextNames.TICKET_STATUS_LIST));
		
		return SUCCESS;
	}
	
	private String parentModuleName;
	public String getParentModuleName() {
		return parentModuleName;
	}
	public void setParentModuleName(String parentModuleName) {
		this.parentModuleName = parentModuleName;
	}

	private Boolean approvalStatus = false;
	public Boolean getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(Boolean approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String v2StatusList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, parentModuleName);
		context.put(FacilioConstants.ContextNames.APPROVAL_STATUS, approvalStatus);
		
		FacilioChain statusListChain = FacilioChainFactory.getTicketStatusListChain();
		statusListChain.execute(context);
		
		setResult("status", context.get(FacilioConstants.ContextNames.TICKET_STATUS_LIST));
		
		return SUCCESS;
	}
	
	private FacilioStatus facilioStatus;
	public FacilioStatus getFacilioStatus() {
		return facilioStatus;
	}
	public void setFacilioStatus(FacilioStatus facilioStatus) {
		this.facilioStatus = facilioStatus;
	}
	
	public String v2AddStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET_STATUS, facilioStatus);
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, parentModuleName);
		
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateTicketStatusChain();
		chain.execute(context);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(parentModuleName);
		
		setResult("status", context.get(FacilioConstants.ContextNames.TICKET_STATUS));
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("State {%s} of %s has been added.", facilioStatus.getDisplayName(),module.getDisplayName()),
				null,
				AuditLogHandler.RecordType.SETTING,
				"State", facilioStatus.getId())
				.setActionType(AuditLogHandler.ActionType.ADD)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", facilioStatus.getId());
					json.put("moduleName",parentModuleName);
					json.put("navigateTo", "states");

					array.add(json);
					return array.toJSONString();
				}).apply(null))
		);
		return SUCCESS;
	}
	
	public String v2UpdateStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET_STATUS, facilioStatus);
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, parentModuleName);
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateTicketStatusChain();
		chain.execute(context);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(parentModuleName);
		
		setResult("status", context.get(FacilioConstants.ContextNames.TICKET_STATUS));
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("State {%s} of %s has been updated.", facilioStatus.getDisplayName(),module.getDisplayName()),
				null,
				AuditLogHandler.RecordType.SETTING,
				"State", facilioStatus.getId())
				.setActionType(AuditLogHandler.ActionType.UPDATE)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", facilioStatus.getId());
					json.put("moduleName",parentModuleName);
					json.put("navigateTo","states");

					array.add(json);
					return array.toJSONString();
				}).apply(null))
		);
		return SUCCESS;
	}
	
	private List<FacilioStatus> statuses = null;
	public List<FacilioStatus> getStatuses() {
		return statuses;
	}
	public void setStatuses(List<FacilioStatus> statuses) {
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
			log.info("Exception occurred ", e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		return null;
	}
}
