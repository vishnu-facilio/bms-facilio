/**
 * 
 */
package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fs.FileInfo.FileFormat;

import lombok.Getter;
import lombok.Setter;

public class CommissionAction extends FacilioAction{

	
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private long agentId = -1;

	public String exportPoints() throws Exception {
		
		FacilioChain exportModule = TransactionChainFactory.getExportPointsChain();
		FacilioContext context = exportModule.getContext();
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, FileFormat.getFileFormat(type));
		context.put(AgentConstants.AGENT_ID, agentId);

		exportModule.execute();
		String fileUrl = (String) context.get(FacilioConstants.ContextNames.FILE_URL);
		setResult("fileUrl", fileUrl);
		return SUCCESS;
	}
	
	
	
	private int type=-1;
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	private long controllerId=-1;

	/**
	 * @return the controllerId
	 */
	public long getControllerId() {
		return controllerId;
	}

	/**
	 * @param controllerId the controllerId to set
	 */
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
	
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}
	private String filters;

	public String logList() throws Exception {
			FacilioChain chain = ReadOnlyChainFactory.getCommissioningLogsChain();
			FacilioContext context = chain.getContext();
			context.put("status",status);
			context.put(ContextNames.MODULE_NAME,ContextNames.COMMISSIONING_LOG);
			constructListContext(context);

			chain.execute();
			setResult("logs", context.get("logs"));

			return SUCCESS;
	}
	public String logCount() throws Exception{
		FacilioChain chain = ReadOnlyChainFactory.getCommissioningLogsChain();
		FacilioContext context = chain.getContext();
		context.put("status",status);
		context.put(ContextNames.MODULE_NAME,ContextNames.COMMISSIONING_LOG);
		setFetchCount(true);
		constructListContext(context);

		chain.execute();
		setResult(FacilioConstants.ContextNames.COUNT,context.get(FacilioConstants.ContextNames.COUNT));

		return SUCCESS;
	}
	
	public String logDetails() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getCommissioningDetailsChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.ID, id);
		
		chain.execute();
		setResult(ContextNames.LOG, context.get(ContextNames.LOG));
		setResult(ContextNames.RESOURCE_LIST, context.get(ContextNames.RESOURCE_LIST));
		setResult(ContextNames.FIELDS, context.get(ContextNames.FIELDS));
		setResult(ContextNames.UNIT, context.get(ContextNames.UNIT));
		return SUCCESS;
	}
	
	private CommissioningLogContext log;
	public CommissioningLogContext getLog() {
		return log;
	}
	public void setLog(CommissioningLogContext log) {
		this.log = log;
	}
	
	public String addLog() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.getAddCommissioningChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.LOG, log);
		
		chain.execute();
		CommissioningLogContext log = (CommissioningLogContext) context.get(ContextNames.LOG);
		setId(log.getId());
		logDetails();
		
		return SUCCESS;
	}
	
	public String updateLog() throws Exception {
		FacilioChain chain = TransactionChainFactory.getUpdateCommissioningChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.LOG, log);
		
		chain.execute();
		setResult(ContextNames.RESULT, "success");
		
		return SUCCESS;
	}
	
	public String publishLog() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.getPublishCommissioningChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.ID, id);
		chain.execute();
		setResult(ContextNames.RESULT, "success");
		return SUCCESS;
	}

	public String mlPointsMigration() throws Exception {
		FacilioChain chain = TransactionChainFactory.MlCommissionedPointsMigration();
		FacilioContext context = chain.getContext();
		context.put(AgentConstants.AGENT_ID,agentId);
		chain.execute();
		setResult(ContextNames.RESULT,"success");
		return SUCCESS;
	}
	public String deleteLog() throws Exception {
		FacilioChain chain = TransactionChainFactory.getDeleteCommissioningChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.ID, id);
		
		chain.execute();
		setResult(ContextNames.RESULT, "success");
		
		return SUCCESS;
	}
	
	public String pointsList() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getCommissioningPointsChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.CONTROLLER_ID, controllerId);
		context.put(ContextNames.FETCH_MAPPED, fetchMapped);
		context.put("controllerType", getControllerType());
		
		chain.execute();
		setResult("points", context.get("points"));
		setResult(ContextNames.RESOURCE_LIST, context.get(ContextNames.RESOURCE_LIST));
		setResult(ContextNames.FIELDS, context.get(ContextNames.FIELDS));
		
		return SUCCESS;
	}
	
	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}

	public String fetchInputValues() throws Exception {
		
		List<Map<String, Object>> readingInputValues = ReadingsAPI.getReadingInputValues(resourceId, fieldId);
		setResult("inputValues", readingInputValues);
		
		return SUCCESS;
	}
	
	private Boolean fetchMapped;
	public Boolean getFetchMapped() {
		return fetchMapped;
	}
	public void setFetchMapped(Boolean fetchMapped) {
		this.fetchMapped = fetchMapped;
	}
	
	private FacilioControllerType controllerType;
	public FacilioControllerType getControllerType() {
		return controllerType;
	}
	public void setControllerType(int type) {
		this.controllerType = FacilioControllerType.valueOf(type);
	}

}
