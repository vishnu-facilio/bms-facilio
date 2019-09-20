package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

public class LoadViewCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(LoadViewCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		if(viewName != null && !viewName.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String parentViewName = (String) context.get(FacilioConstants.ContextNames.PARENT_VIEW);	// eg: to get default report columns
			FacilioView view = null;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			long moduleId = module.getModuleId();
			if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				view = ViewAPI.getView(viewName, moduleName, AccountUtil.getCurrentOrg().getOrgId());
			} else {
				view = ViewAPI.getView(viewName, moduleId, AccountUtil.getCurrentOrg().getOrgId());
			}
			
			if(view == null) {
				
				if (viewName.equals("hidden-all")) {
					viewName = "all";
				}
				
				view = ViewFactory.getView(module, viewName, modBean);
				if (view == null && parentViewName != null) {
					view = ViewFactory.getView(module, parentViewName, modBean);
				}
				else if (view == null) {
					String extendedModName = module.getExtendModule() == null ? StringUtils.EMPTY : module.getExtendModule().getName();
					if (extendedModName.contains("asset")) {
						view = ViewFactory.getModuleView(module, extendedModName);
					}
				}
				
				if(view != null && view.getFields() != null) {
					ViewAPI.setViewFieldsProp(view.getFields(), moduleName);
				}
			}
			
			if(view != null) {
				view.setDefaultModuleFields(moduleName, parentViewName);
				Boolean overrideSorting = (Boolean) context.get(ContextNames.OVERRIDE_SORTING);
				if (overrideSorting != null && overrideSorting) {
					JSONObject sortObj = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
					SortField sortField = getQuerySortField(modBean, moduleName, sortObj);
					String sortQuery;
					if (sortField != null) {
						sortQuery = getOrderClauseForLookupTable(sortField, moduleName);			
						view.setSortFields(Collections.singletonList(sortField));
					}
					else {
						String orderByColName = (String) sortObj.get("orderBy");
						String orderType = (String) sortObj.get("orderType");
						sortQuery = orderByColName + " " + orderType;
					}
					context.put(FacilioConstants.ContextNames.SORTING_QUERY, sortQuery);
					
				}
				else if (view.getSortFields() != null && !view.getSortFields().isEmpty()) {
					StringBuilder orderBy = new StringBuilder();
					String prefix = "";
					for (SortField sortField : view.getSortFields()) {
						orderBy.append(prefix);
						prefix = ",";
						orderBy.append(getOrderClauseForLookupTable(sortField, moduleName));	
					}
					if(orderBy.length() != 0) {
						context.put(FacilioConstants.ContextNames.SORTING_QUERY, orderBy.toString());
					}
				} else {
					List<SortField> defaultSortFields = ColumnFactory.getDefaultSortField(moduleName);
					if (defaultSortFields != null && !defaultSortFields.isEmpty()) {
						StringJoiner joiner = new StringJoiner(",");
						for (SortField sortField : defaultSortFields) {
							joiner.add(getOrderClauseForLookupTable(sortField, moduleName));	
						}
						context.put(FacilioConstants.ContextNames.SORTING_QUERY, joiner.toString());
						view.setSortFields(defaultSortFields);
					}
				}
				
				Boolean fetchDisplayNames = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_FIELD_DISPLAY_NAMES);
				if (fetchDisplayNames != null && fetchDisplayNames) {
					setFieldDisplayNames(moduleName, view);
				}
				
				context.put(FacilioConstants.ContextNames.CUSTOM_VIEW, view);
				view.setSharingType(ViewAPI.getViewSharingDetail(view.getId()));
			}
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute LoadViewCommand : "+timeTaken);
		return false;
	}
	
	private String getOrderClauseForLookupTable(SortField field, String moduleName) throws Exception {
		if (field.getSortField().getDataTypeEnum() == FieldType.LOOKUP) {
			long fieldID;
			if (field.getSortField().getFieldId() == -1) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField sfield = modBean.getField(field.getSortField().getName(), moduleName);
				fieldID = sfield.getFieldId();
			} else {
				fieldID = field.getSortField().getFieldId();
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Pair<String, Boolean> fieldOrdering;
			FacilioModule lookupModule = null;
			List<Map<String, Object>> props = null;
			GenericSelectRecordBuilder builder;
			FacilioField sortableField;
			String columnName = null;
			
			if ((((LookupField) field.getSortField()).getSpecialType()) == null) {
			FacilioModule lookupMod = ModuleFactory.getLookupFieldsModule();
			
			columnName = field.getSortField().getCompleteColumnName();
			
			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			
			FacilioField table = new FacilioField();
			table.setName("tableName");
			table.setDataType(FieldType.STRING);
			table.setColumnName("TABLE_NAME");
			
			FacilioField moduleID = new FacilioField();
			moduleID.setName("moduleID");
			moduleID.setDataType(FieldType.NUMBER);
			moduleID.setColumnName("MODULEID");
			
			builder = new GenericSelectRecordBuilder()
					.select(Arrays.asList(moduleID, name, table))
					.table(ModuleFactory.getLookupFieldsModule().getTableName())
					.innerJoin("Modules")
					.on("Modules.MODULEID = LookupFields.LOOKUP_MODULE_ID")
					.andCustomWhere(lookupMod.getTableName()+".FIELDID = ? AND "+lookupMod.getTableName()+".ORGID = ?", fieldID, AccountUtil.getCurrentOrg().getOrgId());
			
			props = builder.get();
			
			lookupModule = modBean.getModule((long) props.get(0).get("moduleID"));
			fieldOrdering = FieldFactory.getSortableFieldName(lookupModule.getName());
			sortableField = modBean.getField(fieldOrdering.getLeft(), lookupModule.getName());
			String order;
			if (fieldOrdering.getRight()) {
				order = field.getIsAscending() ? "asc" : "desc";
			} else {
				order = field.getIsAscending() ? "desc" : "asc";
			}
			
			builder = new GenericSelectRecordBuilder()
					.select(Arrays.asList(FieldFactory.getIdField()))
					.table((String) props.get(0).get("tableName"))
					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.orderBy(sortableField.getColumnName() + " " + order);					
			props = builder.get();
		}
			else {	
				fieldOrdering = FieldFactory.getSortableFieldName(((LookupField) field.getSortField()).getSpecialType());
				sortableField = modBean.getField(fieldOrdering.getLeft(), ((LookupField) field.getSortField()).getSpecialType());
				String order;
				if (fieldOrdering.getRight()) {
					order = field.getIsAscending() ? "asc" : "desc";
				} else {
					order = field.getIsAscending() ? "desc" : "asc";
				}
				
				FacilioModule tableNames;
				tableNames = LookupSpecialTypeUtil.getModule(((LookupField) field.getSortField()).getSpecialType());

			}
			
			
			List<Long> ids = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				ids.add((Long) prop.get("id"));
			}
				
			String idString = String.join(",", Lists.transform(ids, Functions.toStringFunction()));
			if (idString.length() > 0) {
				return "FIELD("+columnName + "," + idString+")";
			}
		} 
		return field.getSortField().getCompleteColumnName() + " " + (field.getIsAscending()? "asc" : "desc");
	}
	
	private SortField getQuerySortField(ModuleBean modBean, String moduleName, JSONObject sortObj) throws Exception {
		String orderByColName = (String) sortObj.get("orderBy");
		String orderType = (String) sortObj.get("orderType");
		FacilioField field = modBean.getField(orderByColName, moduleName);
		if (field != null) {
			SortField newSortField = new SortField();
			newSortField.setFieldId(field.getFieldId());
			newSortField.setSortField(field);
			newSortField.setIsAscending("asc".equalsIgnoreCase(orderType));
			return newSortField;
		}
		return null;
	}
	

	private void setFieldDisplayNames(String moduleName, FacilioView view) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, ViewField> fieldMap = new HashMap<>();
//		if (view.getFields() != null) {
//			fieldMap = view.getFields().stream().collect(Collectors.toMap(vf -> vf.getField().getName(), Function.identity()));
//		}
		if (view.getFields() != null) {
			for(int i=0;i<view.getFields().size();i++) {
			String modulesName;
			String fieldsName;
			modulesName = view.getFields().get(i).getField().getModule().getName();
			fieldsName = view.getFields().get(i).getName();
			fieldMap.put(fieldsName + modulesName, view.getFields().get(i));
			}
		}
		Map<String, String> fieldNames = null;
		if (fields != null) {
			fieldNames = new HashMap<> ();
			for(FacilioField field : fields) {
				if(field.getName().equals("tenant")) {
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
						continue;
					}
				}
				String displayName;
				if (fieldMap != null && fieldMap.get(field.getName()) != null && fieldMap.get(field.getName()).getColumnDisplayName() != null) {
					displayName = fieldMap.get(field.getName()).getColumnDisplayName();
				}
				else if (view.getDefaultModuleFields() != null && view.getDefaultModuleFields().get(field.getName()) != null && view.getDefaultModuleFields().get(field.getName()).getColumnDisplayName() != null) {
					displayName = view.getDefaultModuleFields().get(field.getName()).getColumnDisplayName();
				}
				else {
					displayName = field.getDisplayName();
				}
				fieldNames.put(field.getName(), displayName);
			}
		}
		view.setFieldDisplayNames(fieldNames);
	}

}
