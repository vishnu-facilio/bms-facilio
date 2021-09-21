package com.facilio.bundle.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public abstract class CommonBundleComponent implements BundleComponentInterface {

	
	
	@Override
	public void getParentDetails(FacilioContext context) throws Exception {
		
	}
	
	@Override
	public void getAddedChangeSet(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleComponentsEnum component = (BundleComponentsEnum) context.get(BundleConstants.COMPONENT);
		
		Map<String, FacilioField> componentFieldMap = component.getFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
		
		Map<String, FacilioField> bundleFieldMap = FieldFactory.getBundleChangeSetFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(component.getFields())
				.table(ModuleFactory.getBundleChangeSetModule().getTableName())
				.rightJoin(component.getModule().getTableName())
				.on("Bundle_Change_Set.COMPONENT_ID = "+component.getModule().getTableName()+"."+componentFieldMap.get(component.getIdFieldName()).getColumnName())
				//.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getModifiedTimeFieldName()), "", CommonOperators.IS_NOT_EMPTY))
				.andCustomWhere("MODIFIED_TIME is not null")
				.andCondition(CriteriaAPI.getCondition(bundleFieldMap.get("id"), "",CommonOperators.IS_EMPTY));
		
		
		List<Map<String, Object>> props = select.get();
		
		List<BundleChangeSetContext> changeSet = new ArrayList<BundleChangeSetContext>();
		
		if(!props.isEmpty()) {
			
			for(Map<String, Object> prop : props) {
				
				BundleChangeSetContext change = new BundleChangeSetContext();
				
				change.setComponentId((Long)prop.get(component.getIdFieldName()));
				change.setComponentTypeEnum(component);
				change.setModeEnum(BundleModeEnum.ADD);
				change.setComponentName((String)prop.get(component.getNameFieldName()));
				change.setComponentDisplayName((String)prop.get(component.getDisplayNameFieldName()));
				
				changeSet.add(change);
			}
			
			context.put(BundleConstants.CHANGE_SET, changeSet);
		}
		else {
			context.put(BundleConstants.CHANGE_SET, null);
		}
	}

	@Override
	public void getModifiedChangeSet(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleComponentsEnum component = (BundleComponentsEnum) context.get(BundleConstants.COMPONENT);
		
		Map<String, FacilioField> componentFieldMap = component.getFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
		
		Map<String, FacilioField> bundleFieldMap = FieldFactory.getBundleChangeSetFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(component.getFields())					// add componets fields too 
				.table(ModuleFactory.getBundleChangeSetModule().getTableName())
				.innerJoin(component.getModule().getTableName())
				.on("Bundle_Change_Set.COMPONENT_ID = "+component.getModule().getTableName()+"."+componentFieldMap.get(component.getIdFieldName()).getColumnName())
				.andCondition(CriteriaAPI.getCondition(bundleFieldMap.get("componentType"), component.getValue()+"", NumberOperators.EQUALS))
				//.andCondition(CriteriaAPI.getCondition(bundleFieldMap.get("componentLastEditedTime"), component.getModule().getTableName()+"."+componentFieldMap.get(component.getModifiedTimeFieldName()).getColumnName(),DateOperators.IS_BEFORE));
				.andCondition(CriteriaAPI.getCondition(bundleFieldMap.get("componentLastEditedTime"), component.getModule().getTableName()+".MODIFIED_TIME",DateOperators.IS_BEFORE));
		
		
		List<Map<String, Object>> props = select.get();
		
		List<BundleChangeSetContext> changeSet = new ArrayList<BundleChangeSetContext>();
		
		if(!props.isEmpty()) {
			
			for(Map<String, Object> prop : props) {
				
				BundleChangeSetContext change = new BundleChangeSetContext();
				
				change.setComponentId((Long)prop.get(component.getIdFieldName()));
				change.setComponentTypeEnum(component);
				change.setModeEnum(BundleModeEnum.UPDATE);
				change.setComponentName((String)prop.get(component.getNameFieldName()));
				change.setComponentDisplayName((String)prop.get(component.getDisplayNameFieldName()));
				
				changeSet.add(change);
			}
			
			context.put(BundleConstants.CHANGE_SET, changeSet);
		}
		else {
			context.put(BundleConstants.CHANGE_SET, null);
		}
	}

	@Override
	public void getDeletedChangeSet(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleComponentsEnum component = (BundleComponentsEnum) context.get(BundleConstants.COMPONENT);
		
		Map<String, FacilioField> componentFieldMap = component.getFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
		
		Map<String, FacilioField> bundleFieldMap = FieldFactory.getBundleChangeSetFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(FieldFactory.getBundleChangeSetFields())
				.table(ModuleFactory.getBundleChangeSetModule().getTableName())
				.leftJoin(component.getModule().getTableName())
				.on("Bundle_Change_Set.COMPONENT_ID = "+component.getModule().getTableName()+"."+componentFieldMap.get(component.getIdFieldName()).getColumnName())
				.andCondition(CriteriaAPI.getCondition(bundleFieldMap.get("componentType"), component.getValue()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getIdFieldName()), "",CommonOperators.IS_EMPTY));
		
		
		List<Map<String, Object>> props = select.get();
		
		if(!props.isEmpty()) {
			List<BundleChangeSetContext> changeSet = FieldUtil.getAsBeanListFromMapList(props, BundleChangeSetContext.class);
			
			for(BundleChangeSetContext change : changeSet) {
				
				change.setId(-1l);
				change.setBundleId(-1l);
				change.setModeEnum(BundleModeEnum.DELETE);
			}
			
			context.put(BundleConstants.CHANGE_SET, changeSet);
		}
		else {
			context.put(BundleConstants.CHANGE_SET, null);
		}
		
	}
}
