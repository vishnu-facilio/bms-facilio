package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class StockedToolsTransactionContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private StockedToolsContext stockedTool;

	public StockedToolsContext getStockedTool() {
		return stockedTool;
	}
	public void setStockedTool(StockedToolsContext stockedTool) {
		this.stockedTool = stockedTool;
	}
	private double issueQuantity = -1;

	public double getIssueQuantity() {
		return issueQuantity;
	}

	public void setIssueQuantity(double issueQuantity) {
		this.issueQuantity = issueQuantity;
	}

	private double returnQuantity = -1;

	public double getReturnQuantity() {
		return returnQuantity;
	}

	public void setReturnQuantity(double returnQuantity) {
		this.returnQuantity = returnQuantity;
	}

}
