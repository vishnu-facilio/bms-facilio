package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
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
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddSerialNumberForPoLineItemsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
				context.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.ASSET);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ASSET);
				context.put(FacilioConstants.ContextNames.RECORD, ast);
				context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, Long.valueOf(-1));
				context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
				Chain addAssetChain = TransactionChainFactory.getAddAssetChain();
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
				} else {
					itemc = new ItemContext();
					itemc.setCostType(CostType.FIFO);
					itemc.setStoreRoom(storeroom);
					itemc.setItemType(itemType);
					return addItem(itemModule, itemFields, itemc);
				}
			}
		} else {
			itemc = new ItemContext();
			itemc.setCostType(CostType.FIFO);
			itemc.setStoreRoom(storeroom);
			itemc.setItemType(itemType);
			return addItem(itemModule, itemFields, itemc);
		}
		return null;
	}

	private ItemContext addItem(FacilioModule module, List<FacilioField> fields, ItemContext item) throws Exception {
		InsertRecordBuilder<ItemContext> readingBuilder = new InsertRecordBuilder<ItemContext>().module(module)
				.fields(fields);
		readingBuilder.withLocalId();
		readingBuilder.insert(item);
		return item;
	}

	private ToolContext getTool(ToolTypesContext toolType, StoreRoomContext storeroom) throws Exception {
		ToolContext toolc = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(toolFields);
		SelectRecordsBuilder<ToolContext> itemselectBuilder = new SelectRecordsBuilder<ToolContext>().select(toolFields)
				.table(toolModule.getTableName()).moduleName(toolModule.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getCondition(toolFieldsMap.get("storeRoom"),
						String.valueOf(storeroom.getId()), NumberOperators.EQUALS));

		List<ToolContext> items = itemselectBuilder.get();
		if (items != null && !items.isEmpty()) {
			for (ToolContext item : items) {
				if (item.getToolType().getId() == toolType.getId()) {
					return item;
				} else {
					toolc = new ToolContext();
					toolc.setStoreRoom(storeroom);
					toolc.setToolType(toolType);
					return addTool(toolModule, toolFields, toolc);
				}
			}
		} else {
			toolc = new ToolContext();
			toolc.setStoreRoom(storeroom);
			toolc.setToolType(toolType);
			return addTool(toolModule, toolFields, toolc);
		}
		return null;
	}

	private ToolContext addTool(FacilioModule module, List<FacilioField> fields, ToolContext tool) throws Exception {
		InsertRecordBuilder<ToolContext> readingBuilder = new InsertRecordBuilder<ToolContext>().module(module)
				.fields(fields);
		readingBuilder.withLocalId();
		readingBuilder.insert(tool);
		return tool;
	}

}
