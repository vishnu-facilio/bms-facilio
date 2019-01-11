package com.facilio.sql.mysql;

import java.util.List;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.sql.DBUpdateRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateRecordBuilder extends DBUpdateRecordBuilder {

	protected UpdateRecordBuilder(GenericUpdateRecordBuilder updateRecordBuilder) {
		super(updateRecordBuilder);
	}

	@Override
	public String constructUpdateStatement() {
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(tableName)
			.append(joinString.toString())
			.append(" SET ");
		boolean isFirst = true;
		for(String propKey : value.keySet()) {
			List<FacilioField> fields = fieldMap.get(propKey);
			if(fields != null) {
				for (FacilioField field: fields) {
					if(isFirst) {
						isFirst = false;
					}
					else {
						sql.append(", ");
					}
					sql.append(field.getCompleteColumnName())
						.append(" = ?");
				}
			}
		}
		
		if(isFirst) {
			return null; //Nothing to update
		}
		
		sql.append(" WHERE ")
			.append(where.getWhereClause())
			;
		
		return sql.toString();
	}

}
