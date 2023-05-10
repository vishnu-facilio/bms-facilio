package com.facilio.controlaction.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.WebTabUtil;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.context.ControlGroupInclExclContext;
import com.facilio.controlaction.context.ControlGroupSpace;
import com.facilio.controlaction.context.ControllableAssetCategoryContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder.BatchUpdateByIdContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.unitconversion.UnitsUtil;

public class ControlActionUtil {

	public static final String CONTROL_ACTION_COMMANDS = "controlActionCommands";
	public static final String CONTROLLABLE_RESOURCES = "controllableResources";
	public static final String CONTROLLABLE_FIELDS = "controllableFields";
	public static final String CONTROL_ACTION_COMMANDS_COUNT = "controlActionCommandscount";
	public static final String CONTROL_ACTION_COMMAND_EXECUTED_FROM = "controlActionCommandExecutedFrom"; 
	public static final String CONTROL_ACTION_CONTROLLABLE_POINTS = "controllablePoints"; 
	
	public static final String VALUE = "value";
	
	public static final String CONTROL_ACTION_GROUP_ID = "controlActionGroupId";
	public static final String CONTROL_ACTION_GROUP_CONTEXT = "controlActionGroup";
	public static final String CONTROL_ACTION_GROUP_CONTEXT_SPACE = "controlActionGroupSpace";
	public static final String CONTROL_ACTION_GROUP_CONTEXT_INCL_EXLC = "controlActionGroupInclExcl";
	public static final String CONTROL_ACTION_GROUP_CONTEXTS = "controlActionGroups";
	public static final String CONTROL_ACTION_GROUP_COUNT = "controlActionGroupCount";
	
	
	public static final String CONTROLLABLE_CATEGORIES = "controllableCategories";
	public static final String SPACE_CONTROLLABLE_CATEGORIES_MAP = "spaceControllableCategoriesMap";
	public static final String CONTROLLABLE_CATEGORY = "controllableCategory";
	public static final String CONTROLLABLE_POINT = "controllablePoint";
	public static final String SPACE_INCLUDE_LIST = "spaceIncludeList";
	public static final String SPACE_EXCLUDE_LIST = "spaceExcludeList";
	public static final String CATEGORY_INCLUDE_LIST = "categoryIncludeList";
	public static final String CATEGORY_EXCLUDE_LIST = "categoryExcludeList";
	
	public static final String CONTROL_MODE = "controlMode";
	
	public static ControllableAssetCategoryContext getControllableAssetCategory(long id) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule controllableCategoryModule = modbean.getModule(FacilioConstants.ContextNames.CONTROLLABLE_ASSET_CATEGORY);
		List<FacilioField> controllableCategoryFields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROLLABLE_ASSET_CATEGORY);
		
		SelectRecordsBuilder<ControllableAssetCategoryContext> selectProject = new SelectRecordsBuilder<ControllableAssetCategoryContext>()
				.module(controllableCategoryModule)
				.select(controllableCategoryFields)
				.beanClass(ControllableAssetCategoryContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, controllableCategoryModule));
		
		List<ControllableAssetCategoryContext> props = selectProject.get();
		if(props == null || props.isEmpty()) {
			return null;
		}
		return props.get(0);
	}
	
	
	public static List<ControlGroupContext> getControlActionGroups(List<Long> ids) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getControlGroupFields())
				.table(ModuleFactory.getControlGroupModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getControlGroupModule()));
		
		List<Map<String, Object>> controlGroupProps = selectBuilder.get();
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getControlGroupSpaceFields())
				.table(ModuleFactory.getControlGroupSpaceModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONTROL_GROUP_ID", "controlGroupId", StringUtils.join(ids, ","), NumberOperators.EQUALS));
		
		List<Map<String, Object>> controlGroupSpaceProps = selectBuilder.get();
		
		Map<Long,List<ControlGroupSpace>> controlSpaceMap = getControlGroupSpaceMap(controlGroupSpaceProps);
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getControlGroupInclExclFields())
				.table(ModuleFactory.getControlGroupInclExclModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONTROL_GROUP_ID", "controlGroupId", StringUtils.join(ids, ","), NumberOperators.EQUALS));
		
		List<Map<String, Object>> controlGroupinclExclProps = selectBuilder.get();
		
		Map<Long,List<ControlGroupInclExclContext>> controlGroupInclExclMap = getControlIncludeExcludeMap(controlGroupinclExclProps);
		
		List<ControlGroupContext> controlGroupContexts = new ArrayList<>();
		if (controlGroupProps != null && !controlGroupProps.isEmpty()) {
			
			for(Map<String, Object> controlGroupProp :controlGroupProps) {
				
				ControlGroupContext controlGroup =  FieldUtil.getAsBeanFromMap(controlGroupProp, ControlGroupContext.class);
				
				FacilioField field = modbean.getField(controlGroup.getFieldId());
				controlGroup.setField(field);
				controlGroup.setControlGroupInclExclContexts(controlGroupInclExclMap.get(controlGroup.getId()));
				controlGroup.setControlGroupSpaces(controlSpaceMap.get(controlGroup.getId()));
				controlGroupContexts.add(controlGroup);
			}
			
		}
		getResourceListForControlGroup(controlGroupContexts);
		return controlGroupContexts;
	}
	
	private static List<ControlGroupContext> getResourceListForControlGroup(List<ControlGroupContext> controlGroupContexts) throws Exception {
		
		for(ControlGroupContext controlGroupContext :controlGroupContexts) {
			
			List<Long> ActualMatchingResourceIds = new ArrayList<>();
			List<Long> spaceIDs = new ArrayList<>();
			
			if(controlGroupContext.getControlGroupSpaces() != null) {
				for(ControlGroupSpace spaces :  controlGroupContext.getControlGroupSpaces()) {
					spaceIDs.add(spaces.getSpaceId());
				}
			}
			
			List<AssetContext> assetContexts = AssetsAPI.getAssetListOfCategory(controlGroupContext.getAssetCategoryId(), spaceIDs);
			
			for(AssetContext assetContext :assetContexts) {
				ActualMatchingResourceIds.add(assetContext.getId());
			}
			
			List<Long> includedIds = null;
			List<Long> excludedIds = null;
			if(controlGroupContext.getControlGroupInclExclContexts() != null && !controlGroupContext.getControlGroupInclExclContexts().isEmpty()) {
				for(ControlGroupInclExclContext includeExcludeRes :controlGroupContext.getControlGroupInclExclContexts()) {
					if(includeExcludeRes.getIsInclude()) {
						includedIds = includedIds == null ? new ArrayList<>() : includedIds; 
						includedIds.add(includeExcludeRes.getResourceId());
					}
					else {
						excludedIds = excludedIds == null ? new ArrayList<>() : excludedIds;
						excludedIds.add(includeExcludeRes.getResourceId());
					}
				}
			}
			List<Long> finalResIds = new ArrayList<>();
			if(includedIds != null) {
				for(Long includedId :includedIds) {
					if(ActualMatchingResourceIds.contains(includedId)) {
						finalResIds.add(includedId);
					}
				}
			}
			else {
				finalResIds = ActualMatchingResourceIds;
			}
			if(excludedIds != null) {
				finalResIds.removeAll(excludedIds);
			}
			
			controlGroupContext.setMatchedResources(finalResIds);
		}
		return controlGroupContexts;
	}
	
	private static Map<Long,List<ControlGroupSpace>> getControlGroupSpaceMap(List<Map<String, Object>> controlGroupSpaceProps) {
		
		Map<Long,List<ControlGroupSpace>> controlSpaceMap = new HashMap<Long, List<ControlGroupSpace>>();
		
		for(Map<String, Object> controlGroupSpaceProp :controlGroupSpaceProps) {
			ControlGroupSpace controlGroupSpace =  FieldUtil.getAsBeanFromMap(controlGroupSpaceProp, ControlGroupSpace.class);
			
			List<ControlGroupSpace> controlGroupSpaces = controlSpaceMap.get(controlGroupSpace.getControlGroupId()) != null ? controlSpaceMap.get(controlGroupSpace.getControlGroupId()) : new ArrayList<ControlGroupSpace>();
			controlGroupSpaces.add(controlGroupSpace);
			controlSpaceMap.put(controlGroupSpace.getControlGroupId(), controlGroupSpaces);
		}
		return controlSpaceMap;
	}
	
	private static Map<Long,List<ControlGroupInclExclContext>> getControlIncludeExcludeMap(List<Map<String, Object>> controlGroupinclExclProps) {
		
		Map<Long,List<ControlGroupInclExclContext>> controlGroupInclExclMap = new HashMap<Long, List<ControlGroupInclExclContext>>();
		
		for(Map<String, Object> controlGroupinclExclProp :controlGroupinclExclProps) {
			ControlGroupInclExclContext ControlGroupInclExclContext = FieldUtil.getAsBeanFromMap(controlGroupinclExclProp, ControlGroupInclExclContext.class);
			
			List<ControlGroupInclExclContext> controlGroupInclExclContexts = controlGroupInclExclMap.get(ControlGroupInclExclContext.getControlGroupId()) != null ? controlGroupInclExclMap.get(ControlGroupInclExclContext.getControlGroupId()) : new ArrayList<ControlGroupInclExclContext>();
			controlGroupInclExclContexts.add(ControlGroupInclExclContext);
			controlGroupInclExclMap.put(ControlGroupInclExclContext.getControlGroupId(), controlGroupInclExclContexts);
		}
		return controlGroupInclExclMap;
	}
	
	
	public static List<ControlActionCommandContext> getCommands() throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule controlActionModule = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		List<FacilioField> controlActionFields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		SelectRecordsBuilder<ControlActionCommandContext> selectProject = new SelectRecordsBuilder<ControlActionCommandContext>()
				.module(controlActionModule)
				.select(controlActionFields)
				.beanClass(ControlActionCommandContext.class);
		
		List<ControlActionCommandContext> props = selectProject.get();
		
		for(ControlActionCommandContext prop :props) {
			FacilioField field = modbean.getField(prop.getFieldId());
			prop.setField(field);
			prop.setResource(ResourceAPI.getResource(prop.getResource().getId()));
		}
		return props;
	}
	
	public static ControlActionCommandContext getLastExecutedCommand(long resourceId,long fieldId) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule controlActionModule = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		List<FacilioField> controlActionFields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(controlActionFields);
		
		SelectRecordsBuilder<ControlActionCommandContext> selectProject = new SelectRecordsBuilder<ControlActionCommandContext>()
				.module(controlActionModule)
				.select(controlActionFields)
				.beanClass(ControlActionCommandContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), resourceId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), fieldId+"", NumberOperators.EQUALS))
				.orderBy("EXECUTED_TIME desc")
				.limit(1);
		
		List<ControlActionCommandContext> props = selectProject.get();
		
		if(props != null && !props.isEmpty()) {
			ControlActionCommandContext prop = props.get(0);
			FacilioField field = modbean.getField(prop.getFieldId());
			prop.setField(field);
//			prop.setResource(ResourceAPI.getResource(prop.getResource().getId()));
			return prop;
		}
		return null;
	}
	
	public static ControlActionCommandContext getLastExecutedCommandGreaterThanSpecificTime(long resourceId,long fieldId,long ttime) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule controlActionModule = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		List<FacilioField> controlActionFields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(controlActionFields);
		
		SelectRecordsBuilder<ControlActionCommandContext> selectProject = new SelectRecordsBuilder<ControlActionCommandContext>()
				.module(controlActionModule)
				.select(controlActionFields)
				.beanClass(ControlActionCommandContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), resourceId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), fieldId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("executedTime"), ttime+"", DateOperators.IS_AFTER))
				.orderBy("EXECUTED_TIME desc")
				.limit(1);
		
		List<ControlActionCommandContext> props = selectProject.get();
		
		if(props != null && !props.isEmpty()) {
			ControlActionCommandContext prop = props.get(0);
			FacilioField field = modbean.getField(prop.getFieldId());
			prop.setField(field);
//			prop.setResource(ResourceAPI.getResource(prop.getResource().getId()));
			return prop;
		}
		return null;
	}

	public static void deleteDependenciesForControlGroup(ControlGroupContext controlGroup) throws Exception {
		deleteGroupSpaces(controlGroup);
		deleteGroupInclExclResources(controlGroup);
	}

	private static void deleteGroupInclExclResources(ControlGroupContext controlGroup) throws Exception {
		
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getControlGroupSpaceModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONTROL_GROUP_ID", "controlGroupId", controlGroup.getId()+"", NumberOperators.EQUALS));
		delete.delete();
	}

	private static void deleteGroupSpaces(ControlGroupContext controlGroup) throws Exception {

		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getControlGroupInclExclModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CONTROL_GROUP_ID", "controlGroupId", controlGroup.getId()+"", NumberOperators.EQUALS));
		delete.delete();
	}
	
	public static void updateControlMessageId(Map<Long, List<Long>> controlVsMessage) throws Exception {
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE));
        FacilioField messageField = fieldMap.get("iotMessageId");
		
		List<BatchUpdateByIdContext> batchUpdateList = new ArrayList<>();
		for (Entry<Long, List<Long>> controlMessage : controlVsMessage.entrySet()) {
			long messageId = controlMessage.getKey();
			List<Long> controlIds = controlMessage.getValue();
			controlIds.forEach(controlId -> {
				BatchUpdateByIdContext batchValue = new BatchUpdateByIdContext();
				batchValue.setWhereId(controlId);
				batchValue.addUpdateValue(messageField.getName(), messageId);
				batchUpdateList.add(batchValue);
			});
		}
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(Collections.singletonList(messageField))
				;
		
		updateBuilder.batchUpdateById(batchUpdateList);
	}

	public static boolean hasControlPermission() throws Exception {
		boolean hasPermission;
		if (AccountUtil.getCurrentUser().getRole().isPrevileged()) {
			hasPermission  = true;
		}
		else if(AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
			hasPermission = PermissionUtil.currentUserHasPermission(FacilioConstants.ContextNames.ASSET, "CONTROL", AccountUtil.getCurrentUser().getRole());
		}
		else {
			WebTabContext tab = AccountUtil.getCurrentTab();
			hasPermission = WebTabUtil.currentUserHasPermission(tab.getId(),"CONTROL",AccountUtil.getCurrentUser().getRole());
		}

		return hasPermission;
	}
}
