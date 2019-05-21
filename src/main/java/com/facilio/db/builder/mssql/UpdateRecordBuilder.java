package com.facilio.db.builder.mssql;

import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FieldType;
import com.facilio.db.builder.DBUpdateRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

public class UpdateRecordBuilder extends DBUpdateRecordBuilder {

	public UpdateRecordBuilder(GenericUpdateRecordBuilder updateRecordBuilder) {
		super(updateRecordBuilder);
	}

	@Override
	public String constructUpdateStatement() {
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(tableName)
			.append(" SET ");
		boolean isFirst = true;
		for(FacilioField field : fields) {
			if (field.getDataType() == FieldType.ID.getTypeAsInt()) {
				continue;
			}
			if (value.containsKey(field.getName())) {
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
		
		if(isFirst) {
			return null; //Nothing to update
		}
		
		if (joinString.length() > 0) {
			sql.append(" FROM " + (baseTableName == null ? tableName : baseTableName) + " " + joinString.toString());
		}
		String whereString = where.getWhereClause();
		whereString = whereString.replaceAll("true", "1");
		whereString = whereString.replaceAll("false", "0");
		sql.append(" WHERE ")
			.append(whereString)
			;
		
		return sql.toString();
	}

}
