package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddCategoryReadingRelCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(AddCategoryReadingRelCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("Inside AddCategoryReadingRelCommand");
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		if (categoryReadingRelModule != null && !categoryReadingRelModule.equals(ModuleFactory.getAssetCategoryReadingRelModule())) { // Don't execute if its asset category reading module
			List<FacilioField> fields = FieldFactory.getCategoryReadingsFields(categoryReadingRelModule);
			long parentCategoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
			
			if(parentCategoryId != -1) {
				List<FacilioModule> modules = CommonCommandUtil.getModules(context);
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(categoryReadingRelModule.getTableName())
															.fields(fields)
															;
				for (FacilioModule module : modules) {
					Map<String, Object> prop = new HashMap<>();
					prop.put("parentCategoryId", parentCategoryId);
					prop.put("readingModuleId", module.getModuleId());
					insertBuilder.addRecord(prop);
				}
				
				insertBuilder.save();
			}
			else {
				throw new IllegalArgumentException("Parent Category ID cannot be null during addition of reading for category");
			}
		}
		return false;
	}
}
