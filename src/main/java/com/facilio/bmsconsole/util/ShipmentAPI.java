package com.facilio.bmsconsole.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class ShipmentAPI {

	
	public static List<ShipmentLineItemContext> getLineItemsForShipment(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<ShipmentLineItemContext> builder = new SelectRecordsBuilder<ShipmentLineItemContext>()
				.module(module).select(fields)
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("shipment"), String.valueOf(id), NumberOperators.EQUALS))
				.beanClass(ShipmentLineItemContext.class);

		List<ShipmentLineItemContext> lineItems = builder.get();
		if (lineItems != null && !lineItems.isEmpty()) {
			return lineItems;
		}
		return null;
	}
	

	public static ItemContext createItem(ShipmentContext shipment, ShipmentLineItemContext lineItem,
			boolean isRotating, List<ShipmentLineItemContext> shipmentRotatingAssets) throws Exception {
		ItemContext item = getItem(lineItem.getItemType(), shipment.getToStore());
		double quantity = 0;
		if (isRotating) {
			shipmentRotatingAssets.add(lineItem);	
		}
		else {
			quantity = lineItem.getQuantity();
		}
		item.setPurchasedItems(Collections.singletonList(getPurchasedItem(quantity, lineItem.getUnitPrice())));
		
		return item;
	}
	
	public static PurchasedItemContext getPurchasedItem(double quantity, double unitCost) {
		PurchasedItemContext purchasedItem = new PurchasedItemContext();
		purchasedItem.setQuantity(quantity);
		purchasedItem.setUnitcost(unitCost);
		purchasedItem.setCostDate(System.currentTimeMillis());
		
		return purchasedItem;
	}

	public static ToolContext createTool(ShipmentContext shipment, ShipmentLineItemContext lineItem,
			boolean isRotating, List<ShipmentLineItemContext> shipmentRotatingAssets) throws Exception {
		ToolContext tool = getTool(lineItem.getToolType(), shipment.getToStore(), lineItem.getQuantity(), lineItem.getUnitPrice());
		
		if (isRotating) {
			shipmentRotatingAssets.add(lineItem);		
		}
		return tool;
	}
	
	public static ShipmentContext getShipment(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIPMENT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SHIPMENT);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<ShipmentContext> builder = new SelectRecordsBuilder<ShipmentContext>()
				.module(module).select(fields)
				.andCondition(
						CriteriaAPI.getIdCondition(id, module))
				.beanClass(ShipmentContext.class);

		List<ShipmentContext> shipments = builder.get();
		if (shipments != null && !shipments.isEmpty()) {
			return shipments.get(0);
		}
		return null;
	}
	
	private static ItemContext getItem(ItemTypesContext itemType, StoreRoomContext storeroom) throws Exception {
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

	private static ItemContext addItem(FacilioModule module, List<FacilioField> fields, StoreRoomContext store, ItemTypesContext itemType) throws Exception {
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
	
	private static ToolContext getTool(ToolTypesContext toolType, StoreRoomContext storeroom, double quantity, double rate) throws Exception {
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
			return addTool(toolModule, toolFields,  storeroom, toolType, quantity, rate);
		} else {
			return addTool(toolModule, toolFields, storeroom, toolType, quantity, rate);
		}
	}

	private static ToolContext addTool(FacilioModule module, List<FacilioField> fields, StoreRoomContext store, ToolTypesContext toolType, double quantity, double rate) throws Exception {
		ToolContext tool = new ToolContext();
		tool.setStoreRoom(store);
		tool.setQuantity(quantity);
		tool.setRate(rate);
		tool.setToolType(toolType);
		InsertRecordBuilder<ToolContext> readingBuilder = new InsertRecordBuilder<ToolContext>().module(module)
				.fields(fields);
		readingBuilder.withLocalId();
		readingBuilder.insert(tool);
		return tool;
	}


}
