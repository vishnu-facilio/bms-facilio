package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FetchShipmentDetailsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ShipmentContext shipmentContext = (ShipmentContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (shipmentContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<ShipmentLineItemContext> builder = new SelectRecordsBuilder<ShipmentLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("SHIPMENT_ID", "shipment", String.valueOf(shipmentContext.getId()), NumberOperators.EQUALS))
					.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
					(LookupField) fieldsAsMap.get("toolType"),(LookupField) fieldsAsMap.get("asset")));
		
			List<ShipmentLineItemContext> list = builder.get();
			shipmentContext.setLineItems(list);
		}
		return false;
	}

}
