package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.constants.FacilioConstants;

public class SetColumnNameForNewCFsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> newFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		List<FacilioField> existingFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		if(newFields != null) {
			Map<FieldType, List<String>> existingColumns = getColumnNamesGroupedByType(existingFields);
			
			//Have to be changed to get in minimum number of queries
			for (FacilioField field : newFields) {
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
