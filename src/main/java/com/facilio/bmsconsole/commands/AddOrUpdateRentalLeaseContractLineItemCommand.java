package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RentalLeaseContractLineItemsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateRentalLeaseContractLineItemCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<RentalLeaseContractLineItemsContext> lineItemContexts = (List<RentalLeaseContractLineItemsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(lineItemContexts)) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			for(RentalLeaseContractLineItemsContext lineItemContext : lineItemContexts) {
				if (lineItemContext.getRentalLeaseContractId() == -1) {
					throw new Exception("Rental/lease Contract cannot be null");
				}
				if (lineItemContext.getId() > 0) {
					updateRecord(lineItemContext, module, fields);
				} else {
					addRecord(lineItemContext, module, fields);
				}
			}
		}
		return false;
	}

	private void updateRecord(RentalLeaseContractLineItemsContext lineItemContext, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder<RentalLeaseContractLineItemsContext> updateBuilder = new UpdateRecordBuilder<RentalLeaseContractLineItemsContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(lineItemContext.getId(), module));
		updateBuilder.update(lineItemContext);
	}

	private void addRecord(RentalLeaseContractLineItemsContext lineItemContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder<RentalLeaseContractLineItemsContext> insertBuilder = new InsertRecordBuilder<RentalLeaseContractLineItemsContext>()
				.module(module)
				.fields(fields);
		insertBuilder.addRecord(lineItemContext);
		insertBuilder.save();		
	}
	
}
