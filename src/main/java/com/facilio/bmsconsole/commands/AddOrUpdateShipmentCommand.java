package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseContractContext;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateShipmentCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ShipmentContext shipment = (ShipmentContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(shipment!=null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM);
			
		
			if(shipment.getFromStore() == null) {
				throw new IllegalArgumentException("From Store cannot be null");
			}
			if(shipment.getToStore() == null) {
				throw new IllegalArgumentException("To Store cannot be null");
			}
			if(shipment.getTransferredBy() == null) {
				shipment.setTransferredBy(AccountUtil.getCurrentUser());
			}
			if (shipment.getId() > 0) {
				updateRecord(shipment, module, fields);
				
				DeleteRecordBuilder<ShipmentLineItemContext> deleteBuilder = new DeleteRecordBuilder<ShipmentLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("SHIPMENT_ID", "shipment", String.valueOf(shipment.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				
				shipment.setStatus(ShipmentContext.Status.NOTSTAGED);
				addRecord(true,Collections.singletonList(shipment), module, fields);
			}
			
			updateLineItems(shipment);
			addRecord(false,shipment.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
			context.put(FacilioConstants.ContextNames.RECORD, shipment);
		}
		return false;
	}
	
		private void updateLineItems(ShipmentContext shipment) {
		for (ShipmentLineItemContext lineItemContext : shipment.getLineItems()) {
			lineItemContext.setShipment(shipment.getId());
		}
	}
	
	private void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}
	
	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}
	


}
