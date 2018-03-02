package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddSpaceReadingRelCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		if (parentId != -1) {
			Map<String, Object> prop = new HashMap<>();
			prop.put("spaceId", parentId);
			prop.put("readingId", module.getModuleId());
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getBasespaceReadingsModule().getTableName())
					.fields(FieldFactory.getBasespaceReadingsFields())
					.addRecord(prop);

			insertBuilder.save();
		}
		else {
			throw new IllegalArgumentException("Parent ID cannot be null during addition of reading for category");
		}
		
		return false;
	}

}
