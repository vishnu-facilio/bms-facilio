package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
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
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class LoadViewCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(LoadViewCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(viewName != null && !viewName.isEmpty()) {
			String parentViewName = (String) context.get(FacilioConstants.ContextNames.PARENT_VIEW);	// eg: to get default report columns
			FacilioView view = null;
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
						sortQuery = orderByColName + " IS NULL," + orderByColName + " " + orderType;
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
		else {
			Boolean overrideSorting = (Boolean) context.get(ContextNames.OVERRIDE_SORTING);
			JSONObject sortObj = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
			if (overrideSorting != null && overrideSorting && sortObj != null) {
				String orderByColName = (String) sortObj.get("orderBy");
				String orderType = (String) sortObj.get("orderType");
				String sortQuery = orderByColName + " IS NULL," + orderByColName + " " + orderType;
				context.put(FacilioConstants.ContextNames.SORTING_QUERY, sortQuery);
			}
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute LoadViewCommand : "+timeTaken);
		return false;
	}
	
	private String getOrderClauseForLookupTable(SortField field, String moduleName) throws Exception {
		List<Long> ids = new ArrayList<>();
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
			
			if ((((LookupField) field.getSortField()).getSpecialType()) == null) {
			FacilioModule lookupMod = ModuleFactory.getLookupFieldsModule();
			
			
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
			
			if (props != null) {
			
			for (Map<String, Object> prop : props) {
				ids.add((Long) prop.get("id"));
			}
			}
		}
		else if (field.getFieldName() != null && field.getFieldName().equals("siteId")) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(ContextNames.SITE);
			FacilioField nameField = modBean.getField("name", ContextNames.RESOURCE);
			FacilioField idField = FieldFactory.getIdField(module);
			String order = field.getIsAscending() ? " asc" : " desc";
			SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>()
					.moduleName(module.getName()).beanClass(SiteContext.class)
					.select(Collections.singletonList(nameField))
					.orderBy(nameField.getColumnName() + order);
			Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
			if (scopeCriteria != null) {
				builder.andCriteria(scopeCriteria);
			}
			List<Map<String, Object>> sites = builder.getAsProps();
			if (CollectionUtils.isNotEmpty(sites)) {
				ids = sites.stream().map(site -> (long)site.get("id")).collect(Collectors.toList());
			}
		}
		String idString = String.join(",", Lists.transform(ids, Functions.toStringFunction()));
		if (idString.length() > 0) {
			String columnName = field.getSortField().getCompleteColumnName();
			return "FIELD("+columnName + "," + idString+")";
		}
		return field.getSortField().getCompleteColumnName() + " IS NULL," + field.getSortField().getCompleteColumnName() + " " + (field.getIsAscending()? "asc" : "desc");
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
		FacilioModule module = modBean.getModule(moduleName);
		Map<String, ViewField> fieldMap = new HashMap<>();
//		if (view.getFields() != null) {
//			fieldMap = view.getFields().stream().collect(Collectors.toMap(vf -> vf.getField().getName(), Function.identity()));
//		}
		if (view.getFields() != null) {
			for(int i=0;i<view.getFields().size();i++) {
			String modulesName;
			String fieldsName;
			if (view.getFields().get(i).getField() == null && view.getFields().get(i).getName() == null) {
//				Temporary handling for Deyari
			if (view.getFields().get(i).getFieldName().equals("siteId") && (moduleName.equals("workorder") || moduleName.equals("asset") || moduleName.equals("tenant") || moduleName.equals("safetyPlan"))) {
				modulesName = moduleName;
				fieldsName	= view.getFields().get(i).getFieldName();
			}
			else {
				modulesName = FieldFactory.getSystemField(view.getFields().get(i).getFieldName(),module).getModule().getName();
				fieldsName = view.getFields().get(i).getFieldName();
			}
			}
			else {
 			modulesName = view.getFields().get(i).getField().getModule().getName();
			fieldsName = view.getFields().get(i).getName();
			}
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
				else if(field.getName().equals("safetyPlan")) {
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
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
		if (module.isCustom()) {
			List<FacilioField> systemFields = FieldFactory.getSystemPointFields(module);
			for(FacilioField systemField : systemFields) {
				fieldNames.put(systemField.getName(), systemField.getDisplayName());
				
			}
		}
		else if(module.getName().equals("safetyPlan") || module.getName().equals("hazard") || module.getName().equals("precaution")) {
			List<FacilioField> systemFields = FieldFactory.getSystemPointFields(module);
			for(FacilioField systemField : systemFields) {
				fieldNames.put(systemField.getName(), systemField.getDisplayName());

			}
		}
		
//		Temporary handling for Deyari
		if (moduleName.equals("workorder") || moduleName.equals("safetyPlan") || moduleName.equals("asset") || moduleName.equals("tenant")) {
			FacilioField siteField = FieldFactory.getSiteIdField(module);
				fieldNames.put(siteField.getName(), siteField.getDisplayName());
				
		}
		view.setFieldDisplayNames(fieldNames);
	}

}
