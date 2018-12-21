package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

public class LoadViewCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(LoadViewCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		if(viewName != null && !viewName.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String parentViewName = (String) context.get(FacilioConstants.ContextNames.PARENT_VIEW);	// eg: to get default report columns
			FacilioView view = null;
			boolean isCVEnabled = true;
			if(isCVEnabled) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				long moduleId = modBean.getModule(moduleName).getModuleId();
				if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
					view = ViewAPI.getView(viewName, moduleName, AccountUtil.getCurrentOrg().getOrgId());
				} else {
					view = ViewAPI.getView(viewName, moduleId, AccountUtil.getCurrentOrg().getOrgId());
				}
			}
			
			if(view == null) {
				view = ViewFactory.getView(moduleName, viewName);
				if (view == null && parentViewName != null) {
					view = ViewFactory.getView(moduleName, parentViewName);
				}
				if(view != null && view.getFields() != null) {
					ViewAPI.setViewFieldsProp(view.getFields(), moduleName);
				}
			}
			
			if(view != null) {
				view.setDefaultModuleFields(moduleName, parentViewName);
				if (view.getSortFields() != null && !view.getSortFields().isEmpty()) {
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
			FacilioModule lookupMod = ModuleFactory.getLookupFieldsModule();
			
			String columnName = field.getSortField().getColumnName();
			
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
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(Arrays.asList(moduleID, name, table))
					.table(ModuleFactory.getLookupFieldsModule().getTableName())
					.innerJoin("Modules")
					.on("Modules.MODULEID = LookupFields.LOOKUP_MODULE_ID")
					.andCustomWhere(lookupMod.getTableName()+".FIELDID = ? AND "+lookupMod.getTableName()+".ORGID = ?", fieldID, AccountUtil.getCurrentOrg().getOrgId());
			
			List<Map<String, Object>> props = builder.get();
			
			FacilioModule lookupModule = modBean.getModule((long) props.get(0).get("moduleID"));
			Pair<String, Boolean> fieldOrdering = FieldFactory.getSortableFieldName(lookupModule.getName());
			FacilioField sortableField = modBean.getField(fieldOrdering.getLeft(), lookupModule.getName());
			
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
			
			List<Long> ids = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				ids.add((Long) prop.get("id"));
			}
				
			String idString = String.join(",", Lists.transform(ids, Functions.toStringFunction()));
			if (idString.length() > 0) {
				return "FIELD("+columnName + "," + idString+")";
			}
		} 
		return field.getSortField().getColumnName() + " " + (field.getIsAscending()? "asc" : "desc");
	}
	
	private void setFieldDisplayNames(String moduleName, FacilioView view) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, ViewField> fieldMap = null;
		if (view.getFields() != null) {
			fieldMap = view.getFields().stream().collect(Collectors.toMap(vf -> vf.getField().getName(), Function.identity()));
		}
		Map<String, String> fieldNames = null;
		if (fields != null) {
			fieldNames = new HashMap<> ();
			for(FacilioField field : fields) {
				String displayName;
				if (fieldMap != null && fieldMap.get(field.getName()) != null) {
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
