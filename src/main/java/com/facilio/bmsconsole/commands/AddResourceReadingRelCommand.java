package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class AddResourceReadingRelCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> moduleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		
		Boolean isFromulaFieldIOpperationFromMAndV = (Boolean) context.get(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V);
		
		if(isFromulaFieldIOpperationFromMAndV == null || !isFromulaFieldIOpperationFromMAndV) {
			
			if (moduleList == null) {
				FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
				if (module != null) {
					moduleList = Collections.singletonList(module);
				}
			}
			List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
			if (parentIds == null) {
				Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
				if (parentId != null) {
					parentIds = Collections.singletonList(parentId);
				}
			}
			
			if (parentIds != null && !parentIds.isEmpty() && moduleList != null && !moduleList.isEmpty()) {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getResourceReadingsModule().getTableName())
						.fields(FieldFactory.getResourceReadingsFields());
				for (Long parentId : parentIds) {
					for (FacilioModule module : moduleList) {
						Map<String, Object> prop = new HashMap<>();
						prop.put("readingId", module.getModuleId());
						prop.put("resourceId", parentId);
						insertBuilder.addRecord(prop);
					}
				}
				insertBuilder.save();
			}
		}
		
		return false;
	}

}
