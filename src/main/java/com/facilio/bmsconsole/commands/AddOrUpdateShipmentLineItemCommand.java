package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateShipmentLineItemCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<ShipmentLineItemContext> lineItems = (List<ShipmentLineItemContext>) context.get(FacilioConstants.ContextNames.RECORD);
		if (CollectionUtils.isNotEmpty(lineItems)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			for(ShipmentLineItemContext lineItemContext : lineItems) {
				if (lineItemContext.getShipment() == -1) {
					throw new Exception("Shipment cannot be null");
				}
				if (lineItemContext.getId() > 0) {
					RecordAPI.updateRecord(lineItemContext, module, fields);
				} else {
					RecordAPI.addRecord(false, Collections.singletonList(lineItemContext), module, fields);
				}
			}
		}
		return false;
	}
	
}
