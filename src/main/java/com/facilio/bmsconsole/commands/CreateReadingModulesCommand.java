package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

public class CreateReadingModulesCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(CreateReadingModulesCommand.class.getName());
	private static final int MAX_FIELDS_PER_TYPE_PER_MODULE = 5;
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("Inside CreateReadingModulesCommand");
		
		String readingName = (String) context.get(FacilioConstants.ContextNames.READING_NAME);
		Boolean overRideSplit = (Boolean) context.get(FacilioConstants.ContextNames.OVER_RIDE_READING_SPLIT);
		if (overRideSplit == null) {
        	overRideSplit = false;
        }
		
		Integer maxFields = (Integer) context.get(FacilioConstants.ContextNames.MAX_FIELDS_PER_MODULE); 
		if(maxFields == null) {
			maxFields = MAX_FIELDS_PER_TYPE_PER_MODULE;
		}
		
		
		if(readingName != null && !readingName.isEmpty()) {
			List<FacilioField> fields = (List<FacilioField>) context.remove(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
			
			if (fields == null) {
				FacilioField field = (FacilioField) context.remove(FacilioConstants.ContextNames.MODULE_FIELD);
				if (field != null) {
					fields = new ArrayList<>();
					fields.add(field);
				}
			}
			
			if (fields != null && !fields.isEmpty()) {
				FacilioModule module = createModule(readingName, context);
				List<FacilioModule> modules = splitFields(module, fields,overRideSplit,maxFields);
				context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
			}
			context.put(FacilioConstants.ContextNames.APPEND_MODULE_NAME,true);
		}
		return false;
	}
	
	private List<FacilioModule> splitFields(FacilioModule module, List<FacilioField> allFields,boolean overRideSplit, int maxFields) {
		if (allFields.size() <= maxFields || overRideSplit) {
			allFields.addAll(FieldFactory.getDefaultReadingFields(module));
			module.setFields(allFields);
			return Collections.singletonList(module);
		}
		else {
			Map<FieldType, List<FacilioField>> fieldMap = getTypeWiseFields(allFields);
			List<FacilioField> fieldList = new ArrayList<>();
			List<FacilioModule> modules = new ArrayList<>();
			while (!fieldMap.isEmpty()) {
				Iterator<List<FacilioField>> fieldsItr = fieldMap.values().iterator();
				while (fieldsItr.hasNext()) {
					List<FacilioField> fields = fieldsItr.next();
					Iterator<FacilioField> itr = fields.iterator();
					int count = 0;
					while (itr.hasNext() && count < maxFields) {
						fieldList.add(itr.next());
						count++;
						itr.remove();
					}
					if (fields.isEmpty()) {
						fieldsItr.remove();
					}
				}
				
				if (!fieldList.isEmpty()) {
					FacilioModule clone = copyModule(module, fieldList);
					LOGGER.debug("Module : "+clone);
					LOGGER.debug("Fields : "+module.getFields());
					modules.add(clone);		// module addition done here
					fieldList = new ArrayList<>();
				}
				else {
					break;
				}
			}
			return modules;
		}
	}
	
	private FacilioModule copyModule(FacilioModule module, List<FacilioField> fields) {
		FacilioModule newModule = new FacilioModule(module);
		fields.addAll(FieldFactory.getDefaultReadingFields(newModule));
		newModule.setFields(fields);
		return newModule;
	}
	
	private Map<FieldType, List<FacilioField>> getTypeWiseFields(List<FacilioField> fields) {
		Map<FieldType, List<FacilioField>> typeWiseFields = new HashMap<>();
		for (FacilioField field : fields) {
			List<FacilioField> fieldList = typeWiseFields.get(field.getDataTypeEnum());
			if (fieldList == null) {
				fieldList = new ArrayList<>();
				typeWiseFields.put(field.getDataTypeEnum(), fieldList);
			}
			fieldList.add(field);
		}
		return typeWiseFields;
	}
	
	private FacilioModule createModule(String readingName, Context context) {
		FacilioModule module = new FacilioModule();
		if (context.containsKey(FacilioConstants.ContextNames.MODULE_NAME)) {
			module.setName((String) context.get(FacilioConstants.ContextNames.MODULE_NAME));
		} else {
			module.setName(readingName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", ""));
		}
		module.setDisplayName(readingName);
		String tableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		if (tableName == null || tableName.isEmpty()) {
			module.setTableName("Readings_3"); //Readings created here after will be in Readings_3
		}
		else {
			module.setTableName(tableName);
		}
		module.setType((ModuleType) context.get(FacilioConstants.ContextNames.MODULE_TYPE));
		if (module.getTypeEnum() == null) {
			module.setType(ModuleType.READING);
		}
		
		Integer dataInterval = (Integer) context.get(FacilioConstants.ContextNames.MODULE_DATA_INTERVAL);
		if (dataInterval != null) {
			module.setDataInterval(dataInterval);
		}
		return module;
	}
 
}
