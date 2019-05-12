package com.facilio.db.builder.mysql;

import com.facilio.modules.FacilioField;
import com.facilio.db.builder.DBSelectRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SelectRecordBuilder extends DBSelectRecordBuilder {

	private static final Logger LOGGER = LogManager.getLogger(SelectRecordBuilder.class.getName());
	
	public static SelectRecordBuilder getInstance(GenericSelectRecordBuilder selectRecordBuilder) {
		return new SelectRecordBuilder(selectRecordBuilder);
	}
	
	public SelectRecordBuilder(GenericSelectRecordBuilder selectBuilder) { //Do not call after calling getProps
		super(selectBuilder);
	}
	
	@Override
	public String constructSelectStatement() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		
		boolean isFirst = true;
		for(FacilioField field : selectFields) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append(field.getCompleteColumnName())
				.append(" AS `")
				.append(field.getName())
				.append("`");
		}
		
		sql.append(" FROM ")
			.append(tableName);
		
		sql.append(joinBuilder.toString());
		
		if(where.getWhereClause() != null && !where.getWhereClause().isEmpty()) {
			sql.append(" WHERE ")
				.append(where.getWhereClause());
		}
		
		if(groupBy != null && !groupBy.isEmpty()) {
			sql.append(" GROUP BY ")
				.append(groupBy);
		}
		
		if(having != null && !having.isEmpty()) {
			sql.append(" HAVING ")
				.append(having);
		}
		
		if(orderBy != null && !orderBy.isEmpty()) {
			sql.append(" ORDER BY ")
				.append(orderBy);
		}
		
		if(limit != -1) {
			sql.append(" LIMIT ")
				.append(limit);
			
			if(offset != -1) {
				sql.append(" OFFSET ")
					.append(offset);
			}
		}
		
		if (forUpdate) {
			sql.append(" FOR UPDATE ");
		}
		
		return sql.toString();
	}
}
