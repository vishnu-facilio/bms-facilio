package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.bmsconsole.fields.FieldType;
import com.facilio.constants.FacilioConstants;

public class SetColumnNameForNewCFsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> newFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		List<FacilioField> existingFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		Map<FieldType, List<String>> existingColumns = getColumnNamesGroupedByType(existingFields);
		
		//Have to be changed to get in minimum number of queries
		for (FacilioField field : newFields) {
			FieldType dataType = FieldType.getCFType(field.getDataTypeCode());
			
			if(dataType != null) {
				field.setDataType(dataType);
				String newColumnName = getColumnNameForNewField(dataType, existingColumns.get(dataType));
				if(newColumnName == null) {
					throw new Exception("No more columns available.");
				}
				field.setColumnName(newColumnName);
			}
			else {
				throw new IllegalArgumentException("Invalid Data Type Value");
			}
		}
		
		return false;
	}
	
	private Map<FieldType, List<String>> getColumnNamesGroupedByType(List<FacilioField> fields) {
		Map<FieldType, List<String>> existingColumns = new HashMap<>();
		if(fields != null) {
			for(FacilioField field : fields) {
				List<String> columns = existingColumns.get(field.getDataType());
				if(columns == null) {
					columns = new ArrayList<>();
					existingColumns.put(field.getDataType(), columns);
				}
				columns.add(field.getColumnName());
			}
		}
		return existingColumns;
	}
	
	private String getColumnNameForNewField(FieldType type, List<String> existingColumns) throws Exception {
		String[] columns = type.getColumnNames();
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
		return null;
	}
	
	
}
