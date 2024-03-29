package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class PageAction extends FacilioAction {
	
	private static final long serialVersionUID = 1L;
	
	public String fetchPage() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.IS_APPROVAL, isApproval());
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, true);
		FacilioChain chain = ReadOnlyChainFactory.getPageChain();
		chain.execute(context);
		
		setResult(ContextNames.PAGE, context.get(ContextNames.PAGE));
		setResult(ContextNames.RECORD, context.get(ContextNames.RECORD));
		
		return SUCCESS;
	}

	public String fetchLookupRelatedListData() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getRelatedListChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

		chain.execute();
		setResult(ContextNames.RELATED_LIST_META, context.get(ContextNames.RELATED_LIST_META));
		return SUCCESS;
	}

	public String fetchRelatedTabMeta() throws Exception {
		FacilioChain chain;
		FacilioContext context;
		if (id <= 0) {
			chain = ReadOnlyChainFactory.getRelatedTabMetaChain();

			context = chain.getContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		} else {
			chain = ReadOnlyChainFactory.getRelatedTabMetaFromPageChain();

			context = chain.getContext();
			context.put(FacilioConstants.ContextNames.ID, id);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put(FacilioConstants.ContextNames.IS_APPROVAL, isApproval());
			context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, true);
		}
		chain.execute();
		setResult(ContextNames.RELATED_LIST_META, context.get(ContextNames.RELATED_LIST_META));
		setResult(ContextNames.RELATIONSHIP_META, context.get(ContextNames.RELATIONSHIP_META));

		return SUCCESS;
	}

 	public String fetchSpecialModulePage() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.IS_SPECIAL_MODULE, true);
		FacilioChain chain = ReadOnlyChainFactory.getSpecialModulePageChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PAGE, context.get(FacilioConstants.ContextNames.PAGE));
		
		return SUCCESS;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private Boolean approval;
	public Boolean getApproval() {
		return approval;
	}
	private boolean isApproval() {
		if (approval == null) {
			return false;
		}
		return approval;
	}
	public void setApproval(Boolean approval) {
		this.approval = approval;
	}
}
