package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class StockedToolsReturnTrackingContext extends ModuleBaseWithCustomFields {

	private static final long serialVersionUID = 1L;
	private ToolTransactionContext StockedToolTransaction;

	public ToolTransactionContext getStockedToolTransaction() {
		return StockedToolTransaction;
	}

	public void setStockedToolTransaction(ToolTransactionContext stockedToolTransaction) {
		StockedToolTransaction = stockedToolTransaction;
	}

	private double returnQuantity = -1;

	public double getReturnQuantity() {
		return returnQuantity;
	}

	public void setReturnQuantity(double returnQuantity) {
		this.returnQuantity = returnQuantity;
	}

}
