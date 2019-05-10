package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

public class ToolsApi {
	public static ToolTypesContext getToolTypes(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<ToolTypesContext> selectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ToolTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

		List<ToolTypesContext> tools = selectBuilder.get();

		if (tools != null && !tools.isEmpty()) {
			return tools.get(0);
		}
		return null;
	}
	
	public static ToolContext getTool(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) fieldsAsMap.get("toolType"));
		lookUpfields.add((LookupField) fieldsAsMap.get("storeRoom"));
		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ToolContext.class).andCondition(CriteriaAPI.getIdCondition(id, module))
				.fetchLookups(lookUpfields);

		List<ToolContext> tools = selectBuilder.get();

		if (tools != null && !tools.isEmpty()) {
			return tools.get(0);
		}
		return null;
	}
	
	public static Map<String, Long> getToolTypesMap() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<ToolTypesContext> selectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ToolTypesContext.class);

		List<ToolTypesContext> tools = selectBuilder.get();
		Map<String, Long> toolNameVsIdMap = new HashMap<>();
		if (tools != null && !tools.isEmpty()) {
			for(ToolTypesContext toolType : tools) {
				toolNameVsIdMap.put(toolType.getName(), toolType.getId());
			}
			return toolNameVsIdMap;
		}
		return null;
	}
	
	public static List<ToolContext> getToolsForStore(long storeId) throws Exception {
		if (storeId <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
				;
		List<ToolContext> tools = selectBuilder.get();
		return tools;
	}

}
