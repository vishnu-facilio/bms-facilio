package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.fw.BeanFactory;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterForInsertSkipCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		if(importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
			ArrayListMultimap<String,ReadingContext> categoryBasedAssets = (ArrayListMultimap<String, ReadingContext>) context.get(ImportAPI.ImportProcessConstants.CATEGORY_BASED_ASSETS);
			for(String moduleName : categoryBasedAssets.keySet()) {
				List<ReadingContext> readingsList = categoryBasedAssets.get(moduleName);
				
				JSONObject meta = importProcessContext.getImportJobMetaJson();
				ArrayList<String> insertFields = (ArrayList<String>)meta.get(ImportAPI.ImportProcessConstants.INSERT_FIELDS);

				for(ReadingContext readingContext : readingsList) {
					ReadingContext inDataBase = getAssetNames(moduleName, importProcessContext,insertFields, readingContext);
					if(inDataBase == null) {
						continue;
					}
					else {
						categoryBasedAssets.remove(moduleName, readingContext);
					}
				}
			}
			context.put(categoryBasedAssets, ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
			return false;
		}
		else {
			return false;
		}
	}

	public static ReadingContext getAssetNames(String moduleName, ImportProcessContext importProcessContext,ArrayList<String>fieldList, ReadingContext readingContext) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule Module = modBean.getModule(moduleName);

		List<FacilioField> fields = new ArrayList<>();
		for(String field : fieldList) {
			HashMap<String, String > fieldMapping = importProcessContext.getFieldMapping();
			for(String fielMappingEntry: fieldMapping.keySet()) {
				if(fieldMapping.get(fielMappingEntry).equals(field)) {
					String[] temp = fielMappingEntry.split("__");
					FacilioField name = modBean.getField(temp[(temp.length - 1)],Module.getName());
					fields.add(name);
					break;
				}
				else {
					continue;
				}
			}
		}
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.beanClass(ReadingContext.class)
				.moduleName(Module.getName())
				.select(fields)
				.table(Module.getTableName());
		
		for(FacilioField field: fields) {
			String columnName = field.getColumnName();
			if(field.getDataTypeEnum().equals(FieldType.LOOKUP) ) {
				Map<String,Object> lookupField = (Map<String,Object>) readingContext.getData().get(field.getName());
				Long Id = (Long) lookupField.get("id");
				selectBuilder.andCustomWhere(columnName+"= ?", Id);
			}
			else {
			selectBuilder.andCustomWhere(columnName+"= ?", readingContext.getData().get(field.getName()));
			}
			}
		
		List<ReadingContext> context = selectBuilder.get();
		if(context.isEmpty()) {
			return null;
		}
		else {
			return context.get(0);
		}
	}
}
