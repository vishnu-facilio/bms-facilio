package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BreakTransactionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class BreakTransactionAction extends ModuleAction{

	private static final long serialVersionUID = 1L;
	private BreakTransactionContext breakTransaction;
	public BreakTransactionContext getBreakTransaction() {
		return breakTransaction;
	}
	public void setBreakTransaction(BreakTransactionContext breakTransaction) {
		this.breakTransaction = breakTransaction;
	}
	
	public String addBreakTransaction() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, breakTransaction);
		Chain addItem = TransactionChainFactory.getAddBreakTransactionChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.BREAK_TRANSACTION, breakTransaction);
		return SUCCESS;
	}

}
