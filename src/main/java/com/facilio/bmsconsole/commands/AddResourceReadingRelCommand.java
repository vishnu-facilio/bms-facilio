package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddResourceReadingRelCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> moduleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
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
		return false;
	}

}
