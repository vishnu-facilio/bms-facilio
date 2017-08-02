package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class TicketCategoryAction extends ActionSupport {
	public String categoryList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain statusListChain = FacilioChainFactory.getTicketCategoryListChain();
		statusListChain.execute(context);
		
		setCategories((List<TicketCategoryContext>) context.get(FacilioConstants.ContextNames.TICKET_CATEGORY_LIST));
		
		return SUCCESS;
	}
	
	private List<TicketCategoryContext> categories = null;
	public List<TicketCategoryContext> getCategories() {
		return categories;
	}
	public void setCategories(List<TicketCategoryContext> categories) {
		this.categories = categories;
	}
	
	public SetupLayout getSetup() {
		return SetupLayout.getTicketCategoryListLayout();
	}
}
