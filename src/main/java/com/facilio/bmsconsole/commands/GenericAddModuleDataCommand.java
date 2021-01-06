package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

public class GenericAddModuleDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		if(record != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			System.out.println(" module name "+moduleName);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);

			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			
			InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
																						.module(module)
																						.fields(fields)
																						;
			
			Boolean setLocalId = (Boolean) context.get(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID);
			if (setLocalId != null && setLocalId) {
				insertRecordBuilder.withLocalId();
			}
			
			Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
			if (withChangeSet != null && withChangeSet) {
				insertRecordBuilder.withChangeSet();
			}
			
			List<SupplementRecord> supplements = new ArrayList<>();
			CommonCommandUtil.handleFormDataAndSupplement(fields, record.getData(), supplements);
			if(!supplements.isEmpty()) {
				insertRecordBuilder.insertSupplements(supplements);
			}
			
			long id = insertRecordBuilder.insert(record);
			record.setId(id);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
			if (withChangeSet != null && withChangeSet) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, insertRecordBuilder.getChangeSet());
			}
		}
		else {
			throw new IllegalArgumentException("Record cannot be null during addition");
		}
		return false;
	}

}
