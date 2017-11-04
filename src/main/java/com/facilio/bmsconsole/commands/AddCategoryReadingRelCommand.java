package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddCategoryReadingRelCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		List<FacilioField> fields = FieldFactory.getCategoryReadingsFields(categoryReadingRelModule);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		long parentCategoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		if(parentCategoryId != -1) {
			Map<String, Object> prop = new HashMap<>();
			prop.put("parentCategoryId", parentCategoryId);
			prop.put("readingModuleId", module.getModuleId());
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																.table(categoryReadingRelModule.getTableName())
																.fields(fields)
																.addRecord(prop);
			
			insertBuilder.save();
		}
		else {
			throw new IllegalAccessException("Parent Category ID cannot be null during addition of reading for category");
		}
		return false;
	}

}
