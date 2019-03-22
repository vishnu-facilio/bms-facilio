package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class DeleteLabourCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			context.put(FacilioConstants.ContextNames.RECORD_LIST, getRecords(module, recordIds, fields));
			
			DeleteRecordBuilder<ModuleBaseWithCustomFields> builder = new DeleteRecordBuilder<ModuleBaseWithCustomFields>()
																		.module(module)
																		.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.markAsDelete());
		}
		return false;
	}
	
	private List getRecords(FacilioModule module, List<Long> recordIds, List<FacilioField> fields) throws Exception {
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(module.getName());
		if (beanClassName == null) {
			beanClassName = ModuleBaseWithCustomFields.class;
		}
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																				.select(fields)
																				.module(module)
																				.beanClass(beanClassName)
																				.andCondition(CriteriaAPI.getIdCondition(recordIds, module))
																				;
		return builder.get();
	}
	

}
