package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class GenericUpdateListModuleDataCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<Long> recordIds = new ArrayList<Long>();

		if(records != null && !records.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			Map<Long, List<UpdateChangeSet>> changes = new HashMap<Long, List<UpdateChangeSet>>();
				
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
			
			for (ModuleBaseWithCustomFields record :records) {
				
				CommonCommandUtil.handleLookupFormData(fields, record.getData());
				
				UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
						.module(module)
						.fields(fields)
						.andCondition(CriteriaAPI.getIdCondition(record.getId(), module));
				if (withChangeSet != null && withChangeSet) {
					updateBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
				}
				updateBuilder.update(record);
				if (withChangeSet != null && withChangeSet) {
					Map<Long, List<UpdateChangeSet>> recordChanges = updateBuilder.getChangeSet();
					if (MapUtils.isNotEmpty(recordChanges)) {
						changes.put(record.getId(), recordChanges.get(record.getId()));
					}
				}
				recordIds.add(record.getId());
			}	
			context.put(FacilioConstants.ContextNames.RECORD_ID, recordIds);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
			if (MapUtils.isNotEmpty(changes)) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, changes);
			}

		}
		
		return false;
	}

}
