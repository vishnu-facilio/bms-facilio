package com.facilio.db.builder;

import com.facilio.bmsconsole.modules.FacilioField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DBUpdateRecordBuilder {
	
	protected List<FacilioField> fields;
	protected Map<String, List<FacilioField>> fieldMap;
	protected String baseTableName;
	protected String tableName;
	protected Map<String, Object> value;
	protected String joinString;
	protected WhereBuilder where = new WhereBuilder();
	
	protected DBUpdateRecordBuilder(GenericUpdateRecordBuilder updateRecordBuilder) {
		if (updateRecordBuilder.getFields() != null) {
			this.fields = new ArrayList<>(updateRecordBuilder.getFields());
		}
		if (updateRecordBuilder.getFieldMap() != null) {
			this.fieldMap = new HashMap<>(updateRecordBuilder.getFieldMap());
		}
		this.baseTableName = updateRecordBuilder.getBaseTableName();
		this.tableName = updateRecordBuilder.getTableName();
		if (updateRecordBuilder.getValue() != null) {
			this.value = new HashMap<>(updateRecordBuilder.getValue());
		}
		this.joinString = updateRecordBuilder.getJoinBuilder().toString();
		if (updateRecordBuilder.getWhere() != null) {
			this.where = new WhereBuilder(updateRecordBuilder.getWhere());
		}
	}
	
	public abstract String constructUpdateStatement();
}
