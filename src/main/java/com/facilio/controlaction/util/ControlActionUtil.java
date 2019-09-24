package com.facilio.controlaction.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.context.ControlGroupInclExclContext;
import com.facilio.controlaction.context.ControlGroupSpace;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

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
}
