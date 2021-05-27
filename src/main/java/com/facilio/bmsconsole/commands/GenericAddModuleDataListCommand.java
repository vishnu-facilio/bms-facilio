package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

public class GenericAddModuleDataListCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ModuleBaseWithCustomFields> record = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(record != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
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
			List<SupplementRecord> supplements = new ArrayList<>();
			for(FacilioField field : fields) {
				if(field.getDataTypeEnum().isMultiRecord())  {
					supplements.add((SupplementRecord) field);
				}
			}

			if(CollectionUtils.isNotEmpty(supplements)) {
				insertRecordBuilder.insertSupplements(supplements);
			}

			Boolean setLocalId = (Boolean) context.get(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID);
			if (setLocalId != null && setLocalId) {
				insertRecordBuilder.withLocalId();
			}
			
			Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
			if (withChangeSet != null && withChangeSet) {
				insertRecordBuilder.withChangeSet();
			}
			List<Long> ids = new ArrayList<>();
			for(ModuleBaseWithCustomFields rec : record) {
				List<SupplementRecord> sup = new ArrayList<>();
				CommonCommandUtil.handleLookupFormData(fields, rec.getData());
				CommonCommandUtil.handleFormDataAndSupplement(fields, rec.getData(), sup);
				insertRecordBuilder.addRecord(rec);
			}
			insertRecordBuilder.save();
			for(ModuleBaseWithCustomFields rec : record) {
				ids.add(rec.getId());
			}
			
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
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
