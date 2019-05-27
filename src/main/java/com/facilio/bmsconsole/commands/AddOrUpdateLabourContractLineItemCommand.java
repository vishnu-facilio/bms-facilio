package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddOrUpdateLabourContractLineItemCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		LabourContractLineItemContext lineItemContext = (LabourContractLineItemContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (lineItemContext != null) {
			if (lineItemContext.getLabourContractId() == -1) {
				throw new Exception("Labour Contract cannot be null");
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

	private void updateRecord(LabourContractLineItemContext lineItemContext, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder<LabourContractLineItemContext> updateBuilder = new UpdateRecordBuilder<LabourContractLineItemContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(lineItemContext.getId(), module));
		updateBuilder.update(lineItemContext);
	}

	private void addRecord(LabourContractLineItemContext lineItemContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder<LabourContractLineItemContext> insertBuilder = new InsertRecordBuilder<LabourContractLineItemContext>()
				.module(module)
				.fields(fields);
		insertBuilder.addRecord(lineItemContext);
		insertBuilder.save();		
	}
	

}
