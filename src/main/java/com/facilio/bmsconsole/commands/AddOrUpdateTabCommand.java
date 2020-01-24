package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.context.WebTabContext.Type;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddOrUpdateTabCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		WebTabContext tab = (WebTabContext) context.get(FacilioConstants.ContextNames.WEB_TAB);
		if (tab != null) {
			if (StringUtils.isEmpty(tab.getName())) {
				throw new IllegalArgumentException("Name cannot be empty");
			}

			if (tab.getGroupId() == -1) {
				throw new IllegalArgumentException("Group cannot empty");
			}

			if (tab.getTypeEnum() == null || tab.getConfig() == null) {
				throw new IllegalArgumentException("Type or config cannot be empty");
			}

			// Validate configuration of tab
			tab.validateConfig();

			if (checkRouteAlreadyFound(tab)) {
				throw new IllegalArgumentException("Route is already found for this app");
			}

			if (tab.getId() <= 0) {
				int lastOrderNumber = getLastOrderNumber(tab.getGroupId());
				tab.setOrder(lastOrderNumber + 1);
			} else {
				tab.setOrder(-1);
			}

			if (tab.getId() > 0) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getWebTabModule().getTableName()).fields(FieldFactory.getWebTabFields())
						.andCondition(CriteriaAPI.getIdCondition(tab.getId(), ModuleFactory.getWebTabModule()));
				builder.update(FieldUtil.getAsProperties(tab));
				context.put(FacilioConstants.ContextNames.WEB_TAB_ID, tab.getId());
				deleteEntriesForTab(tab.getId());
				insertIntoTabIdAppIdMappingTable(tab);
			} else {
				GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getWebTabModule().getTableName()).fields(FieldFactory.getWebTabFields());
				long tabId = builder.insert(FieldUtil.getAsProperties(tab));
				context.put(FacilioConstants.ContextNames.WEB_TAB_ID, tabId);
				tab.setId(tabId);
				insertIntoTabIdAppIdMappingTable(tab);
			}
		}
		return false;
	}

	private boolean checkRouteAlreadyFound(WebTabContext tab) throws Exception {
		if (StringUtils.isEmpty(tab.getRoute())) {
			throw new IllegalArgumentException("Route cannot be empty");
		}

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
				.andCondition(CriteriaAPI.getCondition("ROUTE", "route", tab.getRoute(), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(tab.getGroupId()),
						NumberOperators.EQUALS));

		if (tab.getId() > 0) {
			builder.andCondition(
					CriteriaAPI.getCondition("ID", "id", String.valueOf(tab.getId()), NumberOperators.NOT_EQUALS));
		}

		Map<String, Object> map = builder.fetchFirst();
		return map != null;
	}

	private int getLastOrderNumber(Long groupId) throws Exception {
		if (groupId == null) {
			throw new IllegalArgumentException("Group Id cannot be empty");
		}

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWebTabModule().getTableName())
				.select(Arrays.asList(FieldFactory.getField("order", "MAX(TAB_ORDER)", FieldType.NUMBER)))
				.andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(groupId),
						NumberOperators.EQUALS));
		Map<String, Object> map = builder.fetchFirst();
		if (MapUtils.isNotEmpty(map)) {
			Integer order = (Integer) map.get("order");
			if (order != null) {
				return order;
			}
		}
		return 0;
	}

	private void insertIntoTabIdAppIdMappingTable(WebTabContext webTab) throws Exception {
		if (webTab.getModuleIds() != null && !webTab.getModuleIds().isEmpty()) {
			List<Map<String, Object>> tabIdAppIdProps = new ArrayList<>();
			List<TabIdAppIdMappingContext> tabIdAppIdMapping = new ArrayList<TabIdAppIdMappingContext>();
			for (Long moduleId : webTab.getModuleIds()) {
				if (webTab.getTypeEnum() == Type.MODULE) {
					if (isModuleIdForModuleTabForAppIdAlreadyAdded(webTab.getId(), moduleId, webTab.getAppId())) {
						throw new IllegalStateException("Module of the tab type is already added.");
					}
				}
				TabIdAppIdMappingContext tabIdAppIdMappingContext = new TabIdAppIdMappingContext(webTab.getId(),
						moduleId, webTab.getAppId());
				tabIdAppIdMapping.add(tabIdAppIdMappingContext);
				tabIdAppIdProps.add(FieldUtil.getAsProperties(tabIdAppIdMappingContext));
			}

			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
					.fields(FieldFactory.getTabIdAppIdMappingFields()).addRecords(tabIdAppIdProps);
			builder.save();
		}
	}

	private boolean isModuleIdForModuleTabForAppIdAlreadyAdded(long tabId, long moduleId, long appId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
				.select(FieldFactory.getTabIdAppIdMappingFields())
				.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.APP_ID", "appId",
						String.valueOf(appId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.MODULE_ID", "moduleId",
						String.valueOf(moduleId), NumberOperators.EQUALS));
		List<TabIdAppIdMappingContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(),
				TabIdAppIdMappingContext.class);
		if (webTabGroups != null && !webTabGroups.isEmpty()) {
			return true;
		}
		return false;
	}

	private void deleteEntriesForTab(long tabId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getTabIdAppIdMappingModule().getTableName()).andCondition(CriteriaAPI.getCondition(
						"TABID_MODULEID_APPID_MAPPING.TAB_ID", "tabId", String.valueOf(tabId), NumberOperators.EQUALS));
		builder.delete();
	}
}
