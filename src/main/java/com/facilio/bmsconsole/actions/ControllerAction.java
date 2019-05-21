package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentUtil;
import com.facilio.agent.ControllerUtil;
import com.facilio.agent.PublishType;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ControllerAction extends FacilioAction {

	private static final long serialVersionUID = 1L;

	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * This Action is for Deleting a controller from database's Controller table
	 * @return SUCCESS if deletion is done else ERROR
	 */
	public String deleteController() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, id);
		context.put(FacilioConstants.ContextNames.DEL_READING_RULE, deleteReading);

		Chain deleteControllerSettings = TransactionChainFactory.getDeleteControllerChain();
		if(deleteControllerSettings.execute(context)) {
			setResult("result", "success");
			return SUCCESS;
		}
		return ERROR;
	}

	private Boolean deleteReading;
	public Boolean getDeleteReading() {
		return deleteReading;
	}
	public void setDeleteReading(Boolean deleteReading) {
		this.deleteReading = deleteReading;
	}


	private Long agentId;
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	private JSONObject agentContext;
	public JSONObject getAgentContext() {
		return agentContext;
	}
	public void setAgentContext(JSONObject agentContext) {
		this.agentContext = agentContext;
	}

	public String editAgent() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(PublishType.agent.getValue(), getAgentContext());
		Chain createAgentChain = TransactionChainFactory.editAgent();
		if (createAgentChain.execute(context)) {
			 setResult("msg", SUCCESS);
			return SUCCESS;
		} else {
			 setResult("msg", ERROR);
			return ERROR;
		}
	}

	private JSONArray agentDetail;
	public JSONArray getAgentDetail() {
		return agentDetail;
	}
	public void setAgentDetail(JSONArray agentDetail) {
		this.agentDetail = agentDetail;
	}

	/**
	 * This action is to list agent details.
	 * @return SUCCESS if agent list obtained, ERROR if Account-null and NONE if no agent detail is found.
	 */
	public String getAgentDetails() {
		if ((AccountUtil.getCurrentOrg() != null)) {
			List<Map<String, Object>> agentDetailsList;
			agentDetailsList = new AgentUtil(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentOrg().getDomain()).agentDetails();

			if (agentDetailsList != null) {
				JSONArray agentDetails = new JSONArray();
				agentDetails.addAll(agentDetailsList);
				setAgentDetail(agentDetails);
				setResult("agentDetails", getAgentDetail());
				return SUCCESS;
			}else {
				setResult("agentDetails", getAgentDetail());
				return NONE;
			}
		} else {
			setResult("agentDetails",getAgentDetail());
			return ERROR;
		}
	}


	private JSONArray controllerDetails;
	public JSONArray getControllerDetails() {
		return controllerDetails;
	}
	public void setControllerDetails(JSONArray controllerDetails) {
		this.controllerDetails = controllerDetails;
	}

	/**
	 * This action is to list all Controller details, it works both with or without AgentId filtration.
	 * @return SUCCESS if Controller list is obtained, ERROR if no list obtained.
	 * @throws Exception
	 */
	public String fetchControllerDetails() throws Exception {

		List<Map<String, Object>> agentControllerList;
		agentControllerList =    ControllerUtil.controllerDetailsAPI(getAgentId());

		if (!agentControllerList.isEmpty()) {
			this.controllerDetails = new JSONArray();
			this.controllerDetails.addAll(agentControllerList);
			setResult("controllerDetails", getControllerDetails());
			return SUCCESS;
		}
		setResult("controllerDetails", getControllerDetails());
		return ERROR;
	}

	public JSONArray getJsonArray() {
		return jsonArray;
	}

	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	private JSONArray jsonArray;
	public String getAgentLog() throws Exception{
		List<Map<String, Object>> agentLog;
		agentLog = AgentUtil.getAgentLog(getAgentId());
		if(!agentLog.isEmpty()){
			this.jsonArray = new JSONArray();
			this.jsonArray.addAll(agentLog);
			setResult("agentLog", getJsonArray());
			return SUCCESS;
		}
		setResult("agentLog", getJsonArray());
		return ERROR;
	}
	private Integer publishType;

	public Integer getPublishType() {
		return publishType;
	}

	public void setPublishType(Integer publishType) {
		this.publishType = publishType;
	}

	public String getAgentMetrics() throws Exception{
		List<Map<String, Object>> agentMetrics;
		agentMetrics = AgentUtil.getAgentMetrics(getAgentId(),getPublishType());
		if(!agentMetrics.isEmpty()){
			this.jsonArray = new JSONArray();
			this.jsonArray.addAll(agentMetrics);
			setResult("agentMetrics", getJsonArray());
			return SUCCESS;
		}
		setResult("agentMetrics", getJsonArray());
		return ERROR;
	}

	/**
	 * this Action is to delete an Agent.
	 * @return SUCCESS if deleted else ERROR.
	 * @throws Exception
	 */
	public String deleteAgent() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(AgentKeys.ID, getAgentId());
		Chain deleteAgentChain = TransactionChainFactory.deleteAgent();
		if (deleteAgentChain.execute(context)) {
			setResult("msg", SUCCESS);
			return SUCCESS;
		} else {
			setResult("msg", ERROR);
			return ERROR;
		}

	}

	private JSONObject controllerContext;
	public JSONObject getControllerContext() {
		return controllerContext;
	}
	public void setControllerContext(JSONObject controllerContext) {
		this.controllerContext = controllerContext;
	}

	/**
	 * This Action is to edit controller details.
	 * @return SUCCESS if changes are made else Error.
	 * @throws Exception
	 */
	public String controllerEdit() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(AgentKeys.CONTROLLER_TABLE, getControllerContext());
		Chain editController = TransactionChainFactory.controllerEdit();
		if (editController.execute(context)) {
			setResult("msg", SUCCESS);
			return SUCCESS;
		} else {
			setResult("msg", ERROR);
			return ERROR;
		}
	}
	private static final Logger LOGGER = LogManager.getLogger(ControllerAction.class.getName());

	public String getLogMessage() throws Exception{
		ModuleCRUDBean bean;
		bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId());
		FacilioModule agentModule = ModuleFactory.getAgentDataModule();
		FacilioContext context = new FacilioContext();
		Criteria criteria = new Criteria();
		JSONArray logDetails = new JSONArray();
		if(getAgentId() != null) {
			criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(agentModule), getAgentId().toString(), NumberOperators.EQUALS));
		}
		if(getPerPage() != -1 && getPage() > 0){
			context.put(FacilioConstants.ContextNames.LIMIT_VALUE,getPerPage());
			context.put(FacilioConstants.ContextNames.OFFSET,(getPerPage()*(getPage()-1)));
		}
		context.put(FacilioConstants.ContextNames.TABLE_NAME,AgentKeys.AGENT_LOG_TABLE);
		context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getAgentLogFields());
		context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
		logDetails.addAll(bean.getRows(context));
		if(! logDetails.isEmpty()){
			setResult("logs",logDetails);
			return SUCCESS;
		}
		else {
			setResult("logs",logDetails);
			return ERROR;
		}
	}

	public String getLogCount(){
		ModuleCRUDBean bean;
		try {
			bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId());
			FacilioModule agentModule = ModuleFactory.getAgentDataModule();
			FacilioContext context = new FacilioContext();
			Criteria criteria = new Criteria();
			JSONArray logDetails = new JSONArray();
			if(getAgentId() != null) {
				criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(agentModule), getAgentId().toString(), NumberOperators.EQUALS));
			}
			context.put(FacilioConstants.ContextNames.TABLE_NAME,AgentKeys.AGENT_LOG_TABLE);
			context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getAgentLogFields());
			context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
			context.put(FacilioConstants.ContextNames.LIMIT_VALUE,100);
			context.put(FacilioConstants.ContextNames.OFFSET,0);
			setResult("logCount",bean.getRows(context).size());
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ERROR;
	}
}




