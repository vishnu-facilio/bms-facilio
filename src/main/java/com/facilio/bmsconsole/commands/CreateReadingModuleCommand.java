package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.SubModuleType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;

public class CreateReadingModuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String readingName = (String) context.get(FacilioConstants.ContextNames.READING_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		
		if (fields == null) {
			FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.MODULE_FIELD);
			if (field != null) {
				fields = new ArrayList<>();
				fields.add(field);
				context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
			}
		}
		
		if(readingName != null && !readingName.isEmpty() && fields != null && !fields.isEmpty()) {
			FacilioModule module = new FacilioModule();
			module.setName(readingName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			module.setDisplayName(readingName);
			module.setTableName("Readings");
			context.put(FacilioConstants.ContextNames.MODULE, module);
			context.put(FacilioConstants.ContextNames.SUB_MODULE_TYPE, SubModuleType.READING);
			
			fields.addAll(FieldFactory.getDefaultReadingFields(module));
		}
		else {
			throw new IllegalArgumentException("Invalid Reading Name/ Field list during addition of Reading");
		}
		return false;
	}

}
