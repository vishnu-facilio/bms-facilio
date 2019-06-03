package com.facilio.bmsconsole.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
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
		ItemContext item = new ItemContext();
		item.setStoreRoom(shipment.getToStore());
		item.setItemType(lineItem.getItemType());
		item.setCostType(CostType.FIFO);
		if (isRotating) {
			shipmentRotatingAssets.add(lineItem);		
		} else {
			PurchasedItemContext purchasedItem = new PurchasedItemContext();
			purchasedItem.setQuantity(lineItem.getQuantity());
			purchasedItem.setUnitcost(lineItem.getUnitPrice());
			item.setPurchasedItems(Collections.singletonList(purchasedItem));
		}
		return item;
	}

	public static ToolContext createTool(ShipmentContext shipment, ShipmentLineItemContext lineItem,
			boolean isRotating, List<ShipmentLineItemContext> shipmentRotatingAssets) throws Exception {
		ToolContext tool = new ToolContext();
		tool.setStoreRoom(shipment.getToStore());
		tool.setToolType(lineItem.getToolType());
		tool.setQuantity(lineItem.getQuantity());
		tool.setRate(lineItem.getRate());
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
}
