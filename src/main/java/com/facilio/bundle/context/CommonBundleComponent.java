package com.facilio.bundle.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;

import io.jsonwebtoken.lang.Collections;

public abstract class CommonBundleComponent implements BundleComponentInterface {

	
	@Override
	public Condition getFetchChangeSetCondition(FacilioContext context) throws Exception {
		return null;
	}
	@Override
	public boolean isPackableComponent(FacilioContext context) throws Exception {
		return true;
	}
	
	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		
		XMLBuilder bundleBuilder = (XMLBuilder) context.get(BundleConstants.BUNDLE_XML_BUILDER);
		
		String fileName =  componentChange.getComponentTypeEnum().getName()+File.separatorChar+getBundleXMLComponentFileName(context)+".xml";
		
		bundleBuilder.element(BundleConstants.VALUES).attr("version", componentChange.getTempVersion()+"").text(fileName);
		
	}
	
	@Override
	public void getInstalledComponents(FacilioContext context) throws Exception {
		
		BundleComponentsEnum component = (BundleComponentsEnum) context.get(BundleConstants.COMPONENT);
		
		List<Long> installedVersionIds = (List<Long>) context.get(BundleConstants.INSTALLED_BUNDLE_VERSION_ID_LIST);
		
		Map<String, FacilioField> componentFieldMap = component.getFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(component.getFields())
				.table(component.getModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(componentFieldMap.get("sourceBundle"), installedVersionIds, NumberOperators.EQUALS))
				;
		
		if(component.getModule().getExtendModule() != null) {
			select.innerJoin(component.getModule().getExtendModule().getTableName())
				.on(component.getModule().getTableName()+".ID="+component.getModule().getExtendModule().getTableName()+".ID");
		}
		
		if(component.getDeletedFieldName() != null) {
			select.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getDeletedFieldName()), Boolean.FALSE.toString(), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = select.get();
		
		List<BundleChangeSetContext> changeSet = new ArrayList<BundleChangeSetContext>();
		
		if(!props.isEmpty()) {
			
			for(Map<String, Object> prop : props) {
				
				long componentID = (Long)prop.get(component.getIdFieldName());
				
				BundleChangeSetContext change = new BundleChangeSetContext();
				
				change.setComponentId(componentID);
				change.setComponentTypeEnum(component);
				change.setComponentDisplayName((String)prop.get(component.getDisplayNameFieldName()));
				
				changeSet.add(change);
				
			}
			
			context.put(BundleConstants.INSTALLED_COMPONENT_LIST, changeSet);
		}
		else {
			context.put(BundleConstants.INSTALLED_COMPONENT_LIST, null);
		}
		
	}
	
	@Override
	public void getAddedChangeSet(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleComponentsEnum component = (BundleComponentsEnum) context.get(BundleConstants.COMPONENT);
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		Map<BundleComponentsEnum,List<BundleChangeSetContext>> changeSetCache = (Map<BundleComponentsEnum, List<BundleChangeSetContext>>) context.get(BundleConstants.CHANGE_SET_CACHE);
		
		List<Long> alreadyAddedComponentIds = new ArrayList<Long>();
		
		if(!Collections.isEmpty(changeSetCache.get(component))) {
			alreadyAddedComponentIds.addAll(changeSetCache.get(component).stream().map(BundleChangeSetContext::getComponentId).collect(Collectors.toList()));
		}
		
		Map<String, FacilioField> componentFieldMap = component.getFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(component.getFields())
				.table(component.getModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getModifiedTimeFieldName()), "", CommonOperators.IS_NOT_EMPTY))
				;
		
		if(component.getModule().getExtendModule() != null) {
			select.innerJoin(component.getModule().getExtendModule().getTableName())
				.on(component.getModule().getTableName()+".ID="+component.getModule().getExtendModule().getTableName()+".ID");
		}
		
		if(!Collections.isEmpty(alreadyAddedComponentIds)) {
			select.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getIdFieldName()), StringUtils.join(alreadyAddedComponentIds, ","), NumberOperators.NOT_EQUALS));
		}
		
		if(component.getDeletedFieldName() != null) {
			select.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getDeletedFieldName()), Boolean.FALSE.toString(), BooleanOperators.IS));
		}
		
		Condition condition = getFetchChangeSetCondition(context);
		
		if(condition != null) {
			select.andCondition(condition);
		}
		
		List<Map<String, Object>> props = select.get();
		
		List<BundleChangeSetContext> changeSet = new ArrayList<BundleChangeSetContext>();
		
		if(!props.isEmpty()) {
			
			for(Map<String, Object> prop : props) {
				
				long componentID = (Long)prop.get(component.getIdFieldName());
				
				context.put(BundleConstants.COMPONENT_ID, componentID);
				
				if(isPackableComponent(context)) {
					BundleChangeSetContext change = new BundleChangeSetContext();
					
					change.setComponentId(componentID);
					change.setComponentTypeEnum(component);
					change.setModeEnum(BundleModeEnum.ADD);
					change.setComponentDisplayName((String)prop.get(component.getDisplayNameFieldName()));
					
					changeSet.add(change);
				}
				
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
		
		Map<BundleComponentsEnum,List<BundleChangeSetContext>> changeSetCache = (Map<BundleComponentsEnum, List<BundleChangeSetContext>>) context.get(BundleConstants.CHANGE_SET_CACHE);
		
		Map<String, FacilioField> componentFieldMap = component.getFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
		
		List<BundleChangeSetContext> currentChangeSet = new ArrayList<BundleChangeSetContext>();
		
		if(!Collections.isEmpty(changeSetCache.get(component))) {
			
			Map<Long, List<BundleChangeSetContext>> changeSetGroupedByBundleCreatedTime = changeSetCache.get(component).stream()
				.filter(changeSet -> (changeSet.getModeEnum() == BundleModeEnum.ADD || changeSet.getModeEnum() == BundleModeEnum.UPDATE))
				.collect(Collectors.groupingBy(BundleChangeSetContext::getComponentLastEditedTime));
			
			for(Long bundleCreatedTime : changeSetGroupedByBundleCreatedTime.keySet()) {
				
				List<Long> alreadyAddedOrModifiedComponentIds = changeSetGroupedByBundleCreatedTime.get(bundleCreatedTime).stream().map(BundleChangeSetContext::getComponentId).collect(Collectors.toList());
				
				GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
						.select(component.getFields())
						.table(component.getModule().getTableName())
						.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getIdFieldName()), StringUtils.join(alreadyAddedOrModifiedComponentIds, ","), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getModifiedTimeFieldName()), "", CommonOperators.IS_NOT_EMPTY))
						.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getModifiedTimeFieldName()), bundleCreatedTime+"", DateOperators.IS_AFTER))
						;
				
				if(component.getDeletedFieldName() != null) {
					select.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getDeletedFieldName()), Boolean.FALSE.toString(), BooleanOperators.IS));
				}
				
				if(component.getModule().getExtendModule() != null) {
					select.innerJoin(component.getModule().getExtendModule().getTableName())
						.on(component.getModule().getTableName()+".ID="+component.getModule().getExtendModule().getTableName()+".ID");
				}
				
				Condition condition = getFetchChangeSetCondition(context);
				
				if(condition != null) {
					select.andCondition(condition);
				}
				
				List<Map<String, Object>> props = select.get();
				
				if(!props.isEmpty()) {
					
					for(Map<String, Object> prop : props) {
						
						long componentID = (Long)prop.get(component.getIdFieldName());
						
						context.put(BundleConstants.COMPONENT_ID, componentID);
						
						if(isPackableComponent(context)) {
							
							BundleChangeSetContext change = new BundleChangeSetContext();
							
							change.setComponentId(componentID);
							change.setComponentTypeEnum(component);
							change.setModeEnum(BundleModeEnum.UPDATE);
							change.setComponentDisplayName((String)prop.get(component.getDisplayNameFieldName()));
							
							currentChangeSet.add(change);
						}
					}
				}
			}
			context.put(BundleConstants.CHANGE_SET, currentChangeSet);
		}
		else {
			context.put(BundleConstants.CHANGE_SET, null);
		}
	}

	@Override
	public void getDeletedChangeSet(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleComponentsEnum component = (BundleComponentsEnum) context.get(BundleConstants.COMPONENT);
		
		Map<BundleComponentsEnum,List<BundleChangeSetContext>> changeSetCache = (Map<BundleComponentsEnum, List<BundleChangeSetContext>>) context.get(BundleConstants.CHANGE_SET_CACHE);
		
		Map<String, FacilioField> componentFieldMap = component.getFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
		
		List<BundleChangeSetContext> currentChangeSet = new ArrayList<BundleChangeSetContext>();
		
		if(!Collections.isEmpty(changeSetCache.get(component)) && component.getDeletedFieldName() != null) {
			
			  List<Long> addedOrModifiedChangeSetComponentIDList = changeSetCache.get(component).stream()
				.filter(changeSet -> (changeSet.getModeEnum() == BundleModeEnum.ADD || changeSet.getModeEnum() == BundleModeEnum.UPDATE))
				.map(BundleChangeSetContext::getComponentId)
				.collect(Collectors.toList());
			
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
					.select(component.getFields())
					.table(component.getModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getIdFieldName()), StringUtils.join(addedOrModifiedChangeSetComponentIDList, ","), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getDeletedFieldName()), Boolean.TRUE.toString(), BooleanOperators.IS)) 										// commenting for now. will be released when all delete is handled properly.
					;
			
			if(component.getModule().getExtendModule() != null) {
				select.innerJoin(component.getModule().getExtendModule().getTableName())
					.on(component.getModule().getTableName()+".ID="+component.getModule().getExtendModule().getTableName()+".ID");
			}
			
			Condition condition = getFetchChangeSetCondition(context);
			
			if(condition != null) {
				select.andCondition(condition);
			}
			
			List<Map<String, Object>> props = select.get();
			
			if(!props.isEmpty()) {
				
				for(Map<String, Object> prop : props) {
					
					long componentID = (Long)prop.get(component.getIdFieldName());
					
					context.put(BundleConstants.COMPONENT_ID, componentID);
					
					BundleChangeSetContext change = new BundleChangeSetContext();
					
					change.setComponentId(componentID);
					change.setComponentTypeEnum(component);
					change.setModeEnum(BundleModeEnum.DELETE);
					change.setComponentDisplayName((String)prop.get(component.getDisplayNameFieldName()));
					
					currentChangeSet.add(change);
				}
			}
			context.put(BundleConstants.CHANGE_SET, currentChangeSet);
		}
		else {
			context.put(BundleConstants.CHANGE_SET, null);
		}
	}
}
