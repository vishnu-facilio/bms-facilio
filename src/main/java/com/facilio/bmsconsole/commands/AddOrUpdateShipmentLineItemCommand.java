package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateShipmentLineItemCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ShipmentLineItemContext lineItemContext = (ShipmentLineItemContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (lineItemContext != null) {
			if (lineItemContext.getShipment() == -1) {
				throw new Exception("Shipment cannot be null");
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			if (lineItemContext.getId() > 0) {
				updateRecord(lineItemContext, module, fields);
			} else {
				addRecord(lineItemContext, module, fields);
			}
		}
		return false;
	}

	private void updateRecord(ShipmentLineItemContext lineItemContext, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder<ShipmentLineItemContext> updateBuilder = new UpdateRecordBuilder<ShipmentLineItemContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(lineItemContext.getId(), module));
		updateBuilder.update(lineItemContext);
	}

	private void addRecord(ShipmentLineItemContext lineItemContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder<ShipmentLineItemContext> insertBuilder = new InsertRecordBuilder<ShipmentLineItemContext>()
				.module(module)
				.fields(fields);
		insertBuilder.addRecord(lineItemContext);
		insertBuilder.save();		
	}
	
}
