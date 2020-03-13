/**
 * 
 */
package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fs.FileInfo.FileFormat;

/**
 * @author facilio
 *
 */
public class CommissionAction extends FacilioAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String exportPointsModule() throws Exception {
		
		FacilioContext context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, FileFormat.getFileFormat(type));
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, getControllerId());

		FacilioChain exportModule = TransactionChainFactory.getExportPointsChain();
		exportModule.execute(context);
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
	
	public String logList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getCommissioningLogsChain();
		FacilioContext context = chain.getContext();
		constructListContext(context);
		
		chain.execute();
		setResult("logs", context.get("logs"));
		
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

}
