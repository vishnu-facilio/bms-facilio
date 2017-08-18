package com.facilio.bmsconsole.modules;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SelectRecordsBuilder<E extends ModuleBaseWithCustomFields> {
	
	private static final int LEVEL = 2;
	
	private String dataTableName;
	private GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	private Class<E> beanClass;
	private List<FacilioField> selectFields;
	private Connection conn;
	private int level = 0;
	private ModuleBean modBean;
	private String moduleName;
	private long moduleId = -1;
	private String where;
	private Object[] whereValues;
	//Need where condition builder for custom field
	
	public SelectRecordsBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public SelectRecordsBuilder (int level) {
		this.level = level;
	}
	
	public SelectRecordsBuilder<E> dataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
		builder.table(dataTableName);
		return this;
	}
	
	public SelectRecordsBuilder<E> beanClass(Class<E> beanClass) {
		this.beanClass = beanClass;
		return this;
	}
	
	public SelectRecordsBuilder<E> select(List<FacilioField> selectFields) {
		this.selectFields = selectFields;
		return this;
	}
	
	public SelectRecordsBuilder<E> connection(Connection conn) {
		this.conn = conn;
		builder.connection(conn);
		return this;
	}
	
	public SelectRecordsBuilder<E> where(String where, Object... values) {
		this.where = where;
		this.whereValues = values;
		return this;
	}
	
	public SelectRecordsBuilder<E> orderBy(String orderBy) {
		builder.orderBy(orderBy);
		return this;
	}
	
	public SelectRecordsBuilder<E> limit(int limit) {
		builder.limit(limit);
		return this;
	}
	
	public SelectRecordsBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	private ModuleBean getModuleBean() throws Exception {
		if (this.modBean == null) {
			this.modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		}
		return this.modBean;
	}
	
	public List<E> getAsBean() throws Exception {
		checkForNull(true);
		List<Map<String, Object>> propList = getAsJustProps();
		
		List<E> beans = new ArrayList<>();
		if(propList != null && propList.size() > 0) {
			List<FacilioField> lookupFields = getLookupFields();
			for(Map<String, Object> props : propList) {
				if(lookupFields.size() > 0) {
					for(FacilioField field : lookupFields) {
						Long recordId = (Long) props.remove(field.getName());
						if(recordId != null) {
							Object lookedupObj = getLookupVal(field, recordId);
							if(lookedupObj != null) {
								props.put(field.getName(), lookedupObj);
							}
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
	
	public List<Map<String, Object>> getAsProps() throws Exception {
		checkForNull(false);
		List<Map<String, Object>> propList = getAsJustProps();
		
		if(propList != null && propList.size() > 0) {
			List<FacilioField> lookupFields = getLookupFields();
			if(lookupFields.size() > 0) {
				for(Map<String, Object> props : propList) {
					for(FacilioField field : lookupFields) {
						Long recordId = (Long) props.remove(field.getName());
						if(recordId != null) {
							Object lookedupObj = getLookupVal(field, recordId);
							if(lookedupObj != null) {
								props.put(field.getName(), lookedupObj);
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
		selectFields.add(FieldFactory.getOrgIdField(dataTableName));
		selectFields.add(FieldFactory.getModuleIdField(dataTableName));
		selectFields.add(FieldFactory.getIdField(dataTableName));
		builder.select(selectFields);
		
		StringBuilder whereCondition = new StringBuilder(dataTableName);
		whereCondition.append(".ORGID = ? AND ")
						.append(dataTableName)
						.append(".MODULEID = ?");
		if(where != null && !where.isEmpty()) {
			whereCondition.append(" AND ")
							.append(where);
		}
		
		Object[] whereVals = null;
		if(whereValues != null) {
			whereVals = new Object[whereValues.length+2];
			whereVals[0] = OrgInfo.getCurrentOrgInfo().getOrgid();
			whereVals[1] = getModuleId();
			for(int i=0; i<whereValues.length; i++) {
				whereVals[i+2] = whereValues[i];
			}
		}
		else {
			whereVals = new Object[2];
			whereVals[0] = OrgInfo.getCurrentOrgInfo().getOrgid();
			whereVals[1] = getModuleId();
		}
		builder.where(whereCondition.toString(), whereVals);
		return builder.get();
	}
	
	private Object getLookupVal(FacilioField field, long id) throws Exception {
		if(id != 0) {
			LookupField lookupField = (LookupField) field;
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				if(level <= LEVEL) {
					return LookupSpecialTypeUtil.getLookedupObject(lookupField.getSpecialType(), id);
				}
				else {
					return LookupSpecialTypeUtil.getEmptyLookedupObject(lookupField.getSpecialType(), id);
				}
			}
			else {
				Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(lookupField.getLookupModule().getName());
				if(moduleClass != null) {
					if(level <= LEVEL) {
						List<FacilioField> lookupBeanFields = getModuleBean().getAllFields(lookupField.getLookupModule().getName());
						SelectRecordsBuilder<ModuleBaseWithCustomFields> lookupBeanBuilder = new SelectRecordsBuilder<>(level+1)
																							.connection(conn)
																							.dataTableName(lookupField.getLookupModule().getTableName())
																							.moduleName(lookupField.getLookupModule().getName())
																							.beanClass(moduleClass)
																							.select(lookupBeanFields)
																							.where("ID = ?", id);
						List<ModuleBaseWithCustomFields> records = lookupBeanBuilder.getAsBean();
						if(records != null && records.size() > 0) {
							return records.get(0);
						}
						else {
							return null;
						}
					}
					else {
						ModuleBaseWithCustomFields lookedupModule = moduleClass.newInstance();
						lookedupModule.setId(id);
						return lookedupModule;
					}
				}
				else {
					throw new IllegalArgumentException("Unknown Module Name in Lookup field "+field);
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
		
		if(dataTableName == null || dataTableName.isEmpty()) {
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
}
