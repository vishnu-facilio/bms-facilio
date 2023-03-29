package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;

public class FetchResourcesCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(FetchResourcesCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportData != null && !reportData.isEmpty()) {
			Collection<Map<String, Object>> csvData = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			JSONObject resourceAliases = (JSONObject) report.getFromReportState(FacilioConstants.ContextNames.REPORT_RESOURCE_ALIASES);
			
			if (resourceAliases != null && !resourceAliases.isEmpty()) {
				List<Long> resourceIds = new ArrayList<>();
				Map<String, Set<Long>> moduleMap = new HashMap<>();
				
				for (Map<String, Object> data : csvData) {
					resourceAliases.keySet().forEach(alias -> {
						String moduleName = (String) resourceAliases.get(alias);
						Set<Long> list = moduleMap.get(moduleName);
						if (list == null) {
							list = new HashSet<>();
							moduleMap.put(moduleName, list);
						}
						Long id = (Long) data.get(alias);
						if (id != null) {
							resourceIds.add(id);
						}
						list.add(id);
					});
				}
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				Map<String, Map<Long, ? extends ModuleBaseWithCustomFields>> dataMap = getModuleDataFromIds(moduleMap);
				for (Map<String, Object> data : csvData) {
					resourceAliases.keySet().forEach(alias -> {
					String moduleName = (String) resourceAliases.get(alias);
					Long id = (Long) data.get(alias);
					if (id != null) {
						try {
							ModuleBaseWithCustomFields resource =  dataMap.get(moduleName).get(id);
							FacilioField primaryField = modBean.getField("name", moduleName);
							Object property = PropertyUtils.getProperty(resource, primaryField.getName());
							data.put((String) alias, property.toString());
						} catch (Exception e) {
							LOGGER.error(e);
						}
					}
					});
				}
			}
		}
		long orgId = AccountUtil.getCurrentOrg().getId();
		if(orgId == 6l) {
			LOGGER.info("FetchResourcesCommand is" + context);
		}
		return false;
	}
	
	private Map<String, Map<Long, ? extends ModuleBaseWithCustomFields>> getModuleDataFromIds(Map<String, Set<Long>> map) throws Exception {

		if ((map == null || map.isEmpty())) {
			return Collections.EMPTY_MAP;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, Map<Long, ? extends ModuleBaseWithCustomFields>> dataMap = new HashMap<>();
		for (String moduleName : map.keySet()) {
			FacilioModule module = modBean.getModule(moduleName);
			if (module.getParentModule().equals(FacilioConstants.ContextNames.RESOURCE)) {
				Map<Long, ? extends ModuleBaseWithCustomFields> resourceAsMapFromIds = ResourceAPI.getResourceAsMapFromIds(map.get(moduleName));
				if (MapUtils.isNotEmpty(resourceAsMapFromIds)) {
					dataMap.put(moduleName, resourceAsMapFromIds);
				}
			}
			else {
				Map<Long, ? extends ModuleBaseWithCustomFields> moduleData = getModuleData(modBean, module, map.get(moduleName));
				dataMap.put(moduleName, moduleData);
			}
		}
		return dataMap;
	}
	
	private <E extends ModuleBaseWithCustomFields> Map<Long, E> getModuleData(ModuleBean modBean, FacilioModule module, Set<Long> parentIds) throws Exception {
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Class<E> moduleClass = FacilioConstants.ContextNames.getClassFromModule(module);

		SelectRecordsBuilder<E> selectBuilder = new SelectRecordsBuilder<E>().select(fields)
				.module(module).beanClass(moduleClass)
				.andCondition(CriteriaAPI.getIdCondition(parentIds, module));
		return selectBuilder.getAsMap();
	}

}
