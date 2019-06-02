package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class AssetBreakdownAction extends FacilioAction {
       private static final long serialVersionUID = 1L;

       public String addAssetBreakDown() throws Exception {
               FacilioContext context = new FacilioContext();
               context.put(FacilioConstants.ContextNames.ASSET_BREAKDOWN, assetBreakdown);
               Chain newAssetBreakdown = FacilioChainFactory.getAddAssetBreakdownChain();
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
