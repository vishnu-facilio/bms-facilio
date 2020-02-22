package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FeedbackKioskContext;
import com.facilio.bmsconsole.context.FeedbackTypeContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class FeedbackKioskAction extends FacilioAction{
	
	private static final long serialVersionUID = 1L;
	
	private FeedbackKioskContext feedbackKiosk;
	private FeedbackTypeContext feedbackType;
	private Boolean fillCatalogForm;
	public Boolean getFillCatalogForm() {
		return fillCatalogForm;
	}

	public void setFillCatalogForm(Boolean fillCatalogForm) {
		this.fillCatalogForm = fillCatalogForm;
	}

	public FeedbackTypeContext getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(FeedbackTypeContext feedbackType) {
		this.feedbackType = feedbackType;
	}

	public FeedbackKioskContext getFeedbackKiosk() {
		return feedbackKiosk;
	}

	public void setFeedbackKiosk(FeedbackKioskContext feedbackKiosk) {
		this.feedbackKiosk = feedbackKiosk;
	}

	public String getFeedbackTypeList() throws Exception{
		FacilioChain chain=ReadOnlyChainFactory.getFeedbackTypeListChain();
		FacilioContext context=chain.getContext();		
		chain.execute();
		
		setResult("feedbackTypes",context.get(ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	
	public String getFeedbackKioskList() throws Exception{
		
		FacilioChain chain=ReadOnlyChainFactory.getFeedbackKioskListChain();
		FacilioContext context=chain.getContext();
		context.put(ContextNames.FILL_CATALOG_FORM, getFillCatalogForm());
		chain.execute();
		
		setResult("feedbackKiosks",context.get(ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	public String getFeedbackKioskDetails() throws Exception{
		FacilioChain chain=ReadOnlyChainFactory.getFeedbackKioskDetailsChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, feedbackKiosk.getId());
		chain.execute();
		
		setResult("feedbackKiosk",context.get(ContextNames.RECORD));
		return SUCCESS;
	}
	public String addOrUpdateFeedbackKiosk() throws Exception{
		
		FacilioChain chain=TransactionChainFactory.addOrUpdateFeedbackKioskChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD,getFeedbackKiosk());
		chain.execute();
		setResult("feedbackKiosk",context.get(ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String addOrUpdateFeedbackType() throws Exception{
		
		FacilioChain chain=TransactionChainFactory.addOrUpdateFeedbackTypeChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD,getFeedbackType());
		chain.execute();
		setResult("feedbackType",context.get(ContextNames.RECORD));

		return SUCCESS;
	}
	
	
	
}