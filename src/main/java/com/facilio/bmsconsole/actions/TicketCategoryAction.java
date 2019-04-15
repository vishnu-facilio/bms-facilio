package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Chain;

import java.util.List;

public class TicketCategoryAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
