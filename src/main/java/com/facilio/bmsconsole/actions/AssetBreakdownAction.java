package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AssetBreakdownAction extends FacilioAction {
       private static final long serialVersionUID = 1L;

       public String addAssetBreakDown() throws Exception {
               FacilioContext context = new FacilioContext();
               context.put(FacilioConstants.ContextNames.ASSET_BREAKDOWN, assetBreakdown);
               Chain newAssetBreakdown = TransactionChainFactory.getAddAssetBreakdownChain();
               newAssetBreakdown.execute(context);
               return SUCCESS;
       }

       public String getLastBreakdown() throws Exception {
               FacilioContext context = new FacilioContext();
               assetBreakdown=new AssetBreakdownContext();
               assetBreakdown.setParentId(parentId);
               context.put(FacilioConstants.ContextNames.ASSET_BREAKDOWN, assetBreakdown);
               Chain getLastBreakDownChain = ReadOnlyChainFactory.getLastAssetBreakDownChain();
               getLastBreakDownChain.execute(context);
               assetBreakdown = (AssetBreakdownContext) context.get(FacilioConstants.ContextNames.ASSET_BREAKDOWN);
               setResult("assetBreakdown", assetBreakdown);
               return SUCCESS;
       }

       private long parentId = -1;

       public long getParentId() {
               return parentId;
       }

       public void setParentId(long parentId) {
               this.parentId = parentId;
       }

       private AssetBreakdownContext assetBreakdown;

       public AssetBreakdownContext getAssetBreakdown() {
               return assetBreakdown;
       }

       public void setAssetBreakdown(AssetBreakdownContext assetBreakdown) {
               this.assetBreakdown = assetBreakdown;
       }
}
