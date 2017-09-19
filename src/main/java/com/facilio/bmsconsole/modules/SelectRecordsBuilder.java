package com.facilio.bmsconsole.modules;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder.GenericJoinBuilder;
import com.facilio.sql.JoinBuilderIfc;
import com.facilio.sql.SelectBuilderIfc;
import com.facilio.sql.WhereBuilder;

public class SelectRecordsBuilder<E extends ModuleBaseWithCustomFields> implements SelectBuilderIfc<E> {
	
	private static final int LEVEL = 2;
	
	private String tableName;
	private GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	private Class<E> beanClass;
	private List<FacilioField> selectFields;
	private Connection conn;
	private int level = 0;
	private int maxLevel = LEVEL;
	private String moduleName;
	private long moduleId = -1;
	private WhereBuilder where = new WhereBuilder();
	//Need where condition builder for custom field
	
	public SelectRecordsBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public SelectRecordsBuilder (int level) {
		this.level = level;
	}
	
	@Override
	public SelectRecordsBuilder<E> select(List<FacilioField> selectFields) {
		this.selectFields = selectFields;
		return this;
	}
	
	public SelectRecordsBuilder<E> maxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> table(String tableName) {
		this.tableName = tableName;
		builder.table(tableName);
		return this;
	}

	@Override
	public JoinRecordBuilder<E> innerJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, builder.innerJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> leftJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, builder.leftJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> rightJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, builder.rightJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> fullJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, builder.fullJoin(tableName));
	}
	
	@Override
	public SelectRecordsBuilder<E> andCustomWhere(String where, Object... values) {
		this.where.andCustomWhere(where, values);
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> groupBy(String groupBy) {
		throw new UnsupportedOperationException("No Group By support for Bean Select Builder. Use Generic");
	}
	
	@Override
	public SelectRecordsBuilder<E> having(String having) {
		throw new UnsupportedOperationException("No Having support for Bean Select Builder. Use Generic");
	}
	
	@Override
	public SelectRecordsBuilder<E> orderBy(String orderBy) {
		builder.orderBy(orderBy);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> limit(int limit) {
		builder.limit(limit);
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> connection(Connection conn) {
		this.conn = conn;
		builder.connection(conn);
		return this;
	}
	
	public SelectRecordsBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	public SelectRecordsBuilder<E> beanClass(Class<E> beanClass) {
		this.beanClass = beanClass;
		return this;
	}
	
	@Override
	public List<E> get() throws Exception {
		checkForNull(true);
		List<Map<String, Object>> propList = getAsJustProps();
		
		List<E> beans = new ArrayList<>();
		if(propList != null && propList.size() > 0) {
			List<FacilioField> lookupFields = getLookupFields();
			for(Map<String, Object> props : propList) {
				for(FacilioField lookupField : lookupFields) {
					Long recordId = (Long) props.remove(lookupField.getName());
					if(recordId != null) {
						Object lookedupObj = null;
						if(level < maxLevel) {
							lookedupObj = FieldUtil.getLookupVal((LookupField) lookupField, recordId, level+1);
						}
						else {
							lookedupObj = getEmptyLookupVal((LookupField) lookupField, recordId);
						}
						if(lookedupObj != null) {
							props.put(lookupField.getName(), lookedupObj);
						}
					}
				}
				E bean = beanClass.newInstance();
				BeanUtils.populate(bean, props);
				beans.add(bean);
			}
		}
		return beans;
	}
	
	public Map<Long, E> getAsMap() throws Exception {
		checkForNull(true);
		List<Map<String, Object>> propList = getAsJustProps();
		
		Map<Long, E> beanMap = new HashMap<>();
		
		if(propList != null && propList.size() > 0) {
			List<FacilioField> lookupFields = getLookupFields();
			for(Map<String, Object> props : propList) {
				for(FacilioField lookupField : lookupFields) {
					Long recordId = (Long) props.remove(lookupField.getName());
					if(recordId != null) {
						Object lookedupObj = null;
						if(level < maxLevel) {
							lookedupObj = FieldUtil.getLookupVal((LookupField) lookupField, recordId, level+1);
						}
						else {
							lookedupObj = getEmptyLookupVal((LookupField) lookupField, recordId);
						}
						if(lookedupObj != null) {
							props.put(lookupField.getName(), lookedupObj);
						}
					}
				}
				E bean = beanClass.newInstance();
				BeanUtils.populate(bean, props);
				beanMap.put(bean.getId(), bean);
			}
		}
		return beanMap;
	}
	
	public List<Map<String, Object>> getAsProps() throws Exception {
		checkForNull(false);
		List<Map<String, Object>> propList = getAsJustProps();
		
		if(propList != null && propList.size() > 0) {
			List<FacilioField> lookupFields = getLookupFields();
			if(lookupFields.size() > 0) {
				for(Map<String, Object> props : propList) {
					for(FacilioField lookupField : lookupFields) {
						Long recordId = (Long) props.remove(lookupField.getName());
						if(recordId != null) {
							Object lookedupObj = null;
							if(level <= maxLevel) {
								lookedupObj = FieldUtil.getLookupVal((LookupField) lookupField, recordId, level+1);
							}
							else {
								lookedupObj = getEmptyLookupVal((LookupField) lookupField, recordId);
							}
							if(lookedupObj != null) {
								props.put(lookupField.getName(), lookedupObj);
							}
						}
					}
				}
			}
		}
		return propList;
	}
	
	private List<FacilioField> getLookupFields() {
		List<FacilioField> lookupFields = new ArrayList<>();
		for(FacilioField field : selectFields) {
			if(field.getDataType() == FieldType.LOOKUP) {
				lookupFields.add(field);
			}
		}
		return lookupFields;
	}
	
	private List<Map<String, Object>> getAsJustProps() throws Exception {
		selectFields.add(FieldFactory.getOrgIdField(tableName));
		selectFields.add(FieldFactory.getModuleIdField(tableName));
		selectFields.add(FieldFactory.getIdField(tableName));
		builder.select(selectFields);
		
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
		
		builder.andCustomWhere(whereCondition.getWhereClause(), whereCondition.getValues());
		return builder.get();
	}
	
	private Object getEmptyLookupVal(LookupField lookupField, long id) throws Exception {
		if(id > 0) {
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return LookupSpecialTypeUtil.getEmptyLookedupObject(lookupField.getSpecialType(), id);
			}
			else {
				Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(lookupField.getLookupModule().getName());
				if(moduleClass != null) {
					ModuleBaseWithCustomFields lookedupModule = moduleClass.newInstance();
					lookedupModule.setId(id);
					return lookedupModule;
				}
				else {
					throw new IllegalArgumentException("Unknown Module Name in Lookup field "+lookupField);
				}
			}
		}
		else {
			return null;
		}
	}
	
	private void checkForNull(boolean checkBean) {
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name cannot be empty");
		}
		
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Data Table Name cannot be empty");
		}
		
		if(checkBean) {
			if(beanClass == null) {
				throw new IllegalArgumentException("Bean class object cannot be null");
			}
		}
		
		if(conn == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		
		if(selectFields == null || selectFields.size() <= 0) {
			throw new IllegalArgumentException("Select Fields cannot be null or empty");
		}
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
	
	public static class JoinRecordBuilder<E extends ModuleBaseWithCustomFields> implements JoinBuilderIfc<E> {
		private SelectRecordsBuilder<E> parentBuilder;
		private GenericJoinBuilder joinBuilder;
		
		private JoinRecordBuilder(SelectRecordsBuilder<E> parentBuilder, GenericJoinBuilder joinBuilder) {
			this.parentBuilder = parentBuilder;
			this.joinBuilder = joinBuilder;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public SelectRecordsBuilder<E> on(String condition) {
			// TODO Auto-generated method stub
			joinBuilder.on(condition);
			return parentBuilder;
		}
		
	}
}
