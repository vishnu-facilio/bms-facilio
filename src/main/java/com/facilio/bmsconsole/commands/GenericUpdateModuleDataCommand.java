package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GenericUpdateModuleDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

		if(record != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
																					.module(module)
																					.fields(fields)
																					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			
			Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
			if (withChangeSet != null && withChangeSet) {
				updateBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
			}
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(record));
			if (withChangeSet != null && withChangeSet) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, updateBuilder.getChangeSet());
			}
		}
		
		return false;
	}

}
