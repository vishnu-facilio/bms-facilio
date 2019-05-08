package com.facilio.db.builder.mysql;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.db.builder.DBUpdateRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

public class UpdateRecordBuilder extends DBUpdateRecordBuilder {

	public UpdateRecordBuilder(GenericUpdateRecordBuilder updateRecordBuilder) {
		super(updateRecordBuilder);
	}

	@Override
	public String constructUpdateStatement() {
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(baseTableName == null ? tableName : baseTableName)
			.append(joinString.toString())
			.append(" SET ");
		boolean isFirst = true;
		for(FacilioField field : fields) {
//			List<FacilioField> fields = fieldMap.get(propKey);
//			if(fields != null) {
//				for (FacilioField field: fields) {
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
//				}
//			}
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
