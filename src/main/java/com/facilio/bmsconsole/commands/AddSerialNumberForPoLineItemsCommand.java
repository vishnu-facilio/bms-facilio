package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PoLineItemsSerialNumberContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddSerialNumberForPoLineItemsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<AssetContext> assets = (List<AssetContext>) context.get(FacilioConstants.ContextNames.ASSETS);
		if (assets != null) {
			PoLineItemsSerialNumberContext polineitemSerialNumber = (PoLineItemsSerialNumberContext) context
					.get(FacilioConstants.ContextNames.RECORD);
			List<PoLineItemsSerialNumberContext> records = new ArrayList<>();
			PurchaseOrderContext po = getPurchaseOrder(polineitemSerialNumber.getPoId());
			long StoreroomId = po.getStoreRoom().getId();
			StoreRoomContext storeRoom = StoreroomApi.getStoreRoom(StoreroomId);
			PurchaseOrderLineItemContext lineItem = getLineItem(polineitemSerialNumber.getLineItem().getId());
			ItemContext item = null;
			ToolContext tool = null;
			if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
				item = getItem(lineItem.getItemType(), storeRoom);
			}
			if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
				tool = getTool(lineItem.getToolType(), storeRoom);
			}
			long siteid = storeRoom.getSite().getId();
			for (AssetContext asset : assets) {
				AssetContext ast = new AssetContext();
				ast.setSerialNumber(asset.getSerialNumber());
				ast.setSiteId(siteid);
				ast.setName(asset.getName());
				ast.setRotatingItem(item);
				ast.setRotatingTool(tool);
				ast.setUnitPrice((int) lineItem.getUnitPrice());
				ast.setPurchasedDate(po.getOrderedTime());
				ast.setPurchaseOrder(po);
				context.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.ASSET);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ASSET);
				context.put(FacilioConstants.ContextNames.RECORD, ast);
				context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, Long.valueOf(-1));
				context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
				FacilioChain addAssetChain = TransactionChainFactory.getAddAssetChain();
				addAssetChain.execute(context);
				PoLineItemsSerialNumberContext record = new PoLineItemsSerialNumberContext();
				record.setLineItem(polineitemSerialNumber.getLineItem());
				record.setPoId(polineitemSerialNumber.getPoId());
				record.setReceiptId(polineitemSerialNumber.getReceiptId());
				record.setAsset(ast);
				records.add(record);
			}
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		}
		return false;
	}

	private PurchaseOrderContext getPurchaseOrder(long poId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER);
		SelectRecordsBuilder<PurchaseOrderContext> builder = new SelectRecordsBuilder<PurchaseOrderContext>()
				.select(fields).andCondition(CriteriaAPI.getIdCondition(poId, module)).module(module)
				.beanClass(PurchaseOrderContext.class);
		List<PurchaseOrderContext> poList = builder.get();
		if (poList != null && !poList.isEmpty()) {
			return poList.get(0);
		}
		return null;
	}

	private PurchaseOrderLineItemContext getLineItem(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
				.module(module).select(fields).andCondition(CriteriaAPI.getIdCondition(id, module))
				.beanClass(PurchaseOrderLineItemContext.class);

		List<PurchaseOrderLineItemContext> lineItems = builder.get();
		if (lineItems != null && !lineItems.isEmpty()) {
			return lineItems.get(0);
		}
		return null;
	}

	private ItemContext getItem(ItemTypesContext itemType, StoreRoomContext storeroom) throws Exception {
		ItemContext itemc = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);
		SelectRecordsBuilder<ItemContext> itemselectBuilder = new SelectRecordsBuilder<ItemContext>().select(itemFields)
				.table(itemModule.getTableName()).moduleName(itemModule.getName()).beanClass(ItemContext.class)
				.andCondition(CriteriaAPI.getCondition(itemFieldMap.get("storeRoom"), String.valueOf(storeroom.getId()),
						NumberOperators.EQUALS));

		List<ItemContext> items = itemselectBuilder.get();
		if (items != null && !items.isEmpty()) {
			for (ItemContext item : items) {
				if (item.getItemType().getId() == itemType.getId()) {
					return item;
				}
			}
			return addItem(itemModule, itemFields, storeroom, itemType);
		} else {
			return addItem(itemModule, itemFields, storeroom, itemType);
		}
	}

	private ItemContext addItem(FacilioModule module, List<FacilioField> fields, StoreRoomContext store, ItemTypesContext itemType) throws Exception {
		ItemContext item = new ItemContext();
		item.setStoreRoom(store);
		item.setItemType(itemType);
		item.setCostType(CostType.FIFO);
		InsertRecordBuilder<ItemContext> readingBuilder = new InsertRecordBuilder<ItemContext>().module(module)
				.fields(fields);
		readingBuilder.withLocalId();
		readingBuilder.insert(item);
		return item;
	}

	private ToolContext getTool(ToolTypesContext toolType, StoreRoomContext storeroom) throws Exception {
		ToolContext toolc = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(toolFields);
		SelectRecordsBuilder<ToolContext> itemselectBuilder = new SelectRecordsBuilder<ToolContext>().select(toolFields)
				.table(toolModule.getTableName()).moduleName(toolModule.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getCondition(toolFieldsMap.get("storeRoom"),
						String.valueOf(storeroom.getId()), NumberOperators.EQUALS));

		List<ToolContext> tools = itemselectBuilder.get();
		if (tools != null && !tools.isEmpty()) {
			for (ToolContext tool : tools) {
				if (tool.getToolType().getId() == toolType.getId()) {
					return tool;
				}
			}
			return addTool(toolModule, toolFields,  storeroom, toolType);
		} else {
			return addTool(toolModule, toolFields, storeroom, toolType);
		}
	}

	private ToolContext addTool(FacilioModule module, List<FacilioField> fields, StoreRoomContext store, ToolTypesContext toolType) throws Exception {
		ToolContext tool = new ToolContext();
		tool.setStoreRoom(store);
		tool.setToolType(toolType);
		InsertRecordBuilder<ToolContext> readingBuilder = new InsertRecordBuilder<ToolContext>().module(module)
				.fields(fields);
		readingBuilder.withLocalId();
		readingBuilder.insert(tool);
		return tool;
	}

}
