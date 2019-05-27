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
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ShipmentAPI;
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
				AssetContext asset = AssetsAPI.getAssetInfo(lineItem.getAsset().getId(), true);
				context.put(FacilioConstants.ContextNames.RECORD, asset);
				
				
				 Map<String, Object> props = new HashMap<String, Object>();
				 FacilioField isDeletedField = FieldFactory.getIsDeletedField(ModuleFactory.getAssetsModule().getParentModule());
				 FacilioField siteField = FieldFactory.getSiteIdField(ModuleFactory.getAssetsModule());
				 
				 props.put(isDeletedField.getName(), false);
					
				
				ItemContext item = null;
				ToolContext tool = null;
				if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
					item = ItemsApi.
							getItem(lineItem.getItemType(),shipment.getToStore());
					props.put("rotatingItem", item.getId());
					asset.setRotatingItem(item);
					
				}
				if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
					tool = ToolsApi.getTool(lineItem.getToolType(),shipment.getToStore());
					props.put("rotatingTool", tool.getId());
					asset.setRotatingTool(tool);
				}
				
				
				StoreRoomContext toStoreRoom = StoreroomApi.getStoreRoom(shipment.getToStore().getId());
				props.put(siteField.getName(), toStoreRoom.getSite().getId());
				props.put("currentLocation" , -1);
				props.put("isUsed", false);
				
				AssetsAPI.changeAssetSpace(asset.getId(), props);
				
				new AddRotatingItemToolCommand().execute(context);
				
			  	context.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.ASSET);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ASSET);
				context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, Long.valueOf(-1));
				context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
				
				//addOldNewAssetRelation(asset.getId(), ast.getId(), shipment.getId());
				
				
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
				
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
		insert.table(ModuleFactory.getShippedAssetRelModule().getTableName());
		insert.fields(FieldFactory.getShippedAssetRelFields());
		insert.addRecord(prop);

		insert.save(); 


	}

}
