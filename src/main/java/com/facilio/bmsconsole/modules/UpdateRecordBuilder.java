package com.facilio.bmsconsole.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.sql.UpdateBuilderIfc;
import com.facilio.sql.WhereBuilder;

public class UpdateRecordBuilder<E extends ModuleBaseWithCustomFields> implements UpdateBuilderIfc<E> {
	
	private GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder();
	private String moduleName;
	private long moduleId = -1;
	private String tableName;
	private Connection conn;
	private List<FacilioField> fields = new ArrayList<>();
	private WhereBuilder where = new WhereBuilder();
	
	public UpdateRecordBuilder () {
		// TODO Auto-generated constructor stub
	}
	
	public UpdateRecordBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	@Override
	public UpdateRecordBuilder<E> table(String tableName) {
		this.tableName = tableName;
		builder.table(tableName);
		return this;
	}
	
	public UpdateRecordBuilder<E> connection(Connection conn) {
		this.conn = conn;
		builder.connection(conn);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> andCustomWhere(String where, Object... values) {
		this.where.andCustomWhere(where, values);
		return this;
	}
	
	@Override
	public UpdateRecordBuilder<E> orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	private long getModuleId() {
		if (this.moduleId <= 0) {
			try {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				this.moduleId = modBean.getModule(moduleName).getModuleId();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.moduleId;
	}

	@Override
	public int update(E bean) throws Exception {
		Map<String, Object> moduleProps = FieldUtil.getAsProperties(bean);
		return update(moduleProps);
	}
	
	public int update(Map<String, Object> moduleProps) throws Exception {
		if(moduleProps != null) {
			checkForNull();
			
			moduleProps.remove("orgId");
			moduleProps.remove("moduleId");
			moduleProps.remove("id");
			
			WhereBuilder whereCondition = new WhereBuilder();
			
			Condition orgCondition = new Condition();
			orgCondition.setField(FieldFactory.getOrgIdField(tableName));
			orgCondition.setOperator(NumberOperators.EQUALS);
			orgCondition.setValue(String.valueOf(OrgInfo.getCurrentOrgInfo().getOrgid()));
			whereCondition.andCondition(orgCondition);
			
			Condition moduleCondition = new Condition();
			moduleCondition.setField(FieldFactory.getModuleIdField(tableName));
			moduleCondition.setOperator(NumberOperators.EQUALS);
			moduleCondition.setValue(String.valueOf(getModuleId()));
			whereCondition.andCondition(moduleCondition);
			
			whereCondition.andCustomWhere(where.getWhereClause(), where.getValues());
			where = whereCondition;
			
			updateLookupFields(moduleProps);
			
			fields.add(FieldFactory.getOrgIdField(tableName));
			fields.add(FieldFactory.getModuleIdField(tableName));
			fields.add(FieldFactory.getIdField(tableName));
			builder.fields(fields);
			
			builder.andCustomWhere(where.getWhereClause(), where.getValues());
			
			return builder.update(moduleProps);
		}
		return 0;
	}
	
	private void updateLookupFields(Map<String, Object> moduleProps) throws Exception {
		for(FacilioField field : fields) {
			if(field.getDataType() == FieldType.LOOKUP) {
				Map<String, Object> lookupProps = (Map<String, Object>) moduleProps.get(field.getName()); 
				if(lookupProps != null) {
					if(lookupProps.get("id") != null) {
						moduleProps.put(field.getName(), lookupProps.get("id"));
					}
					else {
						LookupField lookupField = (LookupField) field;
						if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
							//Not sure if we are handling update of special fields like this
						}
						else {
							ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							List<FacilioField> lookupBeanFields = modBean.getAllFields(lookupField.getLookupModule().getName());
							
							UpdateRecordBuilder<ModuleBaseWithCustomFields> lookupUpdateBuilder = new UpdateRecordBuilder<>()
																									.connection(conn)
																									.moduleName(lookupField.getLookupModule().getName())
																									.table(lookupField.getLookupModule().getTableName())
																									.fields(lookupBeanFields)
																									.andCustomWhere(getParentWhereClauseForLookup(field.getColumnName()), where.getValues());
							lookupUpdateBuilder.update(lookupProps);
						}
						moduleProps.remove(field.getName());
					}
				}
			}
		}
	}
	
	private String getParentWhereClauseForLookup(String columnName) {
		StringBuilder builder = new StringBuilder();
		builder.append(" ID in (SELECT ")
				.append(columnName)
				.append(" FROM ")
				.append(tableName)
				.append(" WHERE ")
				.append(where.getWhereClause())
				.append(")");
		
		return builder.toString();
	}
	
	private void checkForNull() {
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name cannot be empty");
		}
		
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Data Table Name cannot be empty");
		}
		
		if(conn == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
	}
}
