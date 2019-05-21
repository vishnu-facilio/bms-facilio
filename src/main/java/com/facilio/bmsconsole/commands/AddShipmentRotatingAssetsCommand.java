package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddShipmentRotatingAssetsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ShipmentContext shipment = (ShipmentContext)context.get(FacilioConstants.ContextNames.SHIPMENT);
		List<ShipmentLineItemContext> lineItems = (List<ShipmentLineItemContext>)context.get(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM);
		if(CollectionUtils.isNotEmpty(lineItems)) {
			for(ShipmentLineItemContext lineItem : lineItems) {
				ItemContext item = null;
				ToolContext tool = null;
				if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
					item = ItemsApi.
							getItem(lineItem.getItemType(), lineItem.getShipmentContext().getToStore());
				}
				if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
					tool = ToolsApi.getTool(lineItem.getToolType(), lineItem.getShipmentContext().getToStore());
				}
			
				AssetContext asset = AssetsAPI.getAssetInfo(lineItem.getAsset().getId(), true);
				AssetContext ast = new AssetContext();
				ast.setSerialNumber(asset.getSerialNumber());
				ast.setSiteId(StoreroomApi.getStoreRoom(lineItem.getShipmentContext().getToStore().getId()).getSite().getId());
				ast.setName(asset.getName());
				ast.setRotatingItem(item);
				ast.setRotatingTool(tool);
				ast.setCategory(asset.getCategory());
				ast.setUnitPrice(asset.getUnitPrice());
				
				context.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.ASSET);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ASSET);
				context.put(FacilioConstants.ContextNames.RECORD, ast);
				context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, Long.valueOf(-1));
				context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
				Chain addAssetChain = TransactionChainFactory.getAddAssetChain();
				addAssetChain.execute(context);
				
				addOldNewAssetRelation(asset.getId(), ast.getId(), shipment.getId());
				
				
			}
		}
		return false;
	}
	
	private void markAssetFromStoreAsSysDeleted(Context context, long assetId) throws Exception {
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(assetId));
		
		Chain deleteAssetChain = FacilioChainFactory.getDeleteAssetChain();
		deleteAssetChain.execute(context);
	
	}
	
	private void addOldNewAssetRelation(long oldAssetId, long newAssetId, long shipmentId) throws Exception{
		Map<String, Object> prop = new HashMap<>();
		
		prop.put("assetIdFromStore", oldAssetId);
		prop.put("assetIdToStore", newAssetId);
		prop.put("shipmentId", shipmentId);
		prop.put("orgId", AccountUtil.getCurrentOrg().getId());
				
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
		insert.table(ModuleFactory.getShippedAssetRelModule().getTableName());
		insert.fields(FieldFactory.getShippedAssetRelFields());
		insert.addRecord(prop);

		insert.save(); 


	}

}
