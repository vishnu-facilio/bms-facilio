package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReceiptContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

import java.util.List;

public class ReceiptAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private List<ReceiptContext> receipts;
	public List<ReceiptContext> getReceipts() {
		return receipts;
	}
	public void setReceipts(List<ReceiptContext> receipts) {
		this.receipts = receipts;
	}
	
	private ReceiptContext receipt;
	public ReceiptContext getReceipt() {
		return receipt;
	}
	public void setReceipt(ReceiptContext receipt) {
		this.receipt = receipt;
	}
	
	public String addOrUpdateReceipt() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, receipts);
		
		Chain chain = TransactionChainFactory.getAddOrUpdateReceiptsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIPTS, context.get(FacilioConstants.ContextNames.RECEIPTS));
		return SUCCESS;
	}
	
	private long receivableId = -1;
	
	public long getReceivableId() {
		return receivableId;
	}
	public void setReceivableId(long receivableId) {
		this.receivableId = receivableId;
	}
	public String getAllReceiptsByReceivableId() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, receivableId);
		
		Chain chain = ReadOnlyChainFactory.getAllReceiptsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIPTS, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
}
