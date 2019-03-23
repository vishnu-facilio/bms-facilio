package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ReceivableAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long receivableId = -1;
	
	public long getReceivableId() {
		return receivableId;
	}
	public void setReceivableId(long receivableId) {
		this.receivableId = receivableId;
	}
	public long getPoId() {
		return poId;
	}
	public void setPoId(long poId) {
		this.poId = poId;
	}

	private long poId = -1;
	
	private List<ReceivableContext> receivables;
	
	
	public List<ReceivableContext> getReceivables() {
		return receivables;
	}
	public void setReceivables(List<ReceivableContext> receivables) {
		this.receivables = receivables;
	}
	public String getReceivableList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain chain = ReadOnlyChainFactory.getAllReceivablesChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIVABLES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String getReceivableByPoId() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PO_ID,poId );
		
		Chain chain = ReadOnlyChainFactory.getAllReceivablesChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIVABLES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String getReceivableById() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, receivableId);
		
		Chain chain = ReadOnlyChainFactory.getAllReceivablesChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIVABLES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String addOrUpdateReceivables() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, receivables);
		
		Chain chain = TransactionChainFactory.getAddOrUpdateReceivablesChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIVABLES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	
	
}
