package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetColumnNameForNewCFsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioModule> modules = CommonCommandUtil.getModulesWithFields(context);
		
		if(modules != null && !modules.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Boolean isNewModules = (Boolean) context.get(FacilioConstants.ContextNames.IS_NEW_MODULES);
			if (isNewModules == null) {
				isNewModules = false;
			}
			for (FacilioModule module : modules) {
				if (module != null && module.getFields() != null && !module.getFields().isEmpty()) {
					List<FacilioField> existingFields = isNewModules ? null : modBean.getAllFields(module.getName());
					Map<FieldType, List<String>> existingColumns = getColumnNamesGroupedByType(existingFields);
					
					for (FacilioField field : module.getFields()) {
						FieldType dataType = field.getDataTypeEnum();
						
						if(dataType != null) {
							List<String> existingColumnNames = existingColumns.get(dataType);
							if(existingColumnNames == null) {
								existingColumnNames = new ArrayList<>();
								existingColumns.put(dataType, existingColumnNames);
							}
							if(field.getColumnName() == null || field.getColumnName().isEmpty()) {
								String newColumnName = getColumnNameForNewField(dataType, existingColumnNames);
								if(newColumnName == null) {
									throw new Exception("No more columns available.");
								}
								field.setColumnName(newColumnName);
							}
							existingColumnNames.add(field.getColumnName());
						}
						else {
							throw new IllegalArgumentException("Invalid Data Type Value");
						}
					}
				}
			}
		}
		else {
			throw new IllegalArgumentException("No Fields to Add");
		}
		
		
		return false;
	}
	
	private Map<FieldType, List<String>> getColumnNamesGroupedByType(List<FacilioField> fields) {
		Map<FieldType, List<String>> existingColumns = new HashMap<>();
		if(fields != null) {
			for(FacilioField field : fields) {
				List<String> columns = existingColumns.get(field.getDataTypeEnum());
				if(columns == null) {
					columns = new ArrayList<>();
					existingColumns.put(field.getDataTypeEnum(), columns);
				}
				columns.add(field.getColumnName());
			}
		}
		return existingColumns;
	}
	
	private String getColumnNameForNewField(FieldType type, List<String> existingColumns) throws Exception {
		String[] columns = type.getColumnNames();
		if(columns != null && columns.length > 0) {
			if(existingColumns == null || existingColumns.size() == 0) {
				return columns[0];
			}
			else {
				for(String column : columns) {
					if(!existingColumns.contains(column)) {
						return column;
					}
				}
			}
		}
		return null;
	}
	
	
}
