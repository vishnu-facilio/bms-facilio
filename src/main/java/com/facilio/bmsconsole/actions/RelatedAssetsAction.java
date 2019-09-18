package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.RelatedAssetsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class RelatedAssetsAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RelatedAssetsContext relatedAssets;

	public RelatedAssetsContext getRelatedAssets() {
		return relatedAssets;
	}

	public void setRelatedAssets(RelatedAssetsContext relatedAssets) {
		this.relatedAssets = relatedAssets;
	}

	private long id = -1;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String addOrUpdateRelatedAssets() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RELATED_ASSETS, relatedAssets);
		
		FacilioChain chain;
		if (relatedAssets.getId() > 0) {
			chain = TransactionChainFactory.getUpdateRelatedAssetsChain();
		} else {
			chain = TransactionChainFactory.getAddRelatedAssetsChain();
		}
		chain.execute(context);

		return SUCCESS;
	}

	public String deleteRelatedAsset() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		
		FacilioChain chain = TransactionChainFactory.getDeleteRelatedAssetsChain();
		chain.execute(context);
		return SUCCESS;
	}
	
	public String fetchRelatedAssets() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.fetchRelatedAssetChain();
		chain.getContext().put(ContextNames.RESOURCE_ID, id);
		chain.execute();
		
		setResult(ContextNames.RELATED_ASSETS, chain.getContext().get(ContextNames.RESOURCE_LIST));
		
		return SUCCESS;
	}

}
