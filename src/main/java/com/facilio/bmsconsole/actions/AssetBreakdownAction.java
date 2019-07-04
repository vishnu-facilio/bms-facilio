package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AssetBreakdownAction extends FacilioAction {
       private static final long serialVersionUID = 1L;

	public String addNewAssetBreakDown() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS, assetBDSourceDetails);
		Chain newAssetBreakdown = TransactionChainFactory.getAddNewAssetBreakdownChain();
		newAssetBreakdown.execute(context);
		return SUCCESS;
	}
       private long parentId = -1;

       public long getParentId() {
               return parentId;
       }

       public void setParentId(long parentId) {
               this.parentId = parentId;
       }

	private AssetBDSourceDetailsContext assetBDSourceDetails;

	public AssetBDSourceDetailsContext getAssetBDSourceDetails() {
		return assetBDSourceDetails;
	}

	public void setAssetBDSourceDetails(AssetBDSourceDetailsContext assetBDSourceDetails) {
		this.assetBDSourceDetails = assetBDSourceDetails;
	}
       private AssetBreakdownContext assetBreakdown;

       public AssetBreakdownContext getAssetBreakdown() {
               return assetBreakdown;
       }

       public void setAssetBreakdown(AssetBreakdownContext assetBreakdown) {
               this.assetBreakdown = assetBreakdown;
       }
}
