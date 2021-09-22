package com.facilio.bundle.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;

import com.facilio.bundle.anotations.ExcludeInBundle;
import com.facilio.bundle.anotations.IncludeInBundle;
import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.google.common.io.Files;

import lombok.extern.log4j.Log4j;

@Log4j
public class BundleUtil {

//	public static BundleChangeSetContext markModifiedComponent(BundleComponentsEnum componentType,Long componentId,String name,BundleModeEnum mode) throws Exception {
//		
//		BundleChangeSetContext existingChangeSet = getOrAddModifiedComponent(componentType, componentId, name, mode);
//		
//		boolean isUpdateNeeded = true;
//		
//		switch (existingChangeSet.getModeEnum()) {
//		case ADD:
//			switch(mode) {
//				case ADD:
//				case UPDATE:
//					break;
//				case DELETE:
//					deleteBundleRelated(ModuleFactory.getBundleChangeSetModule(), CriteriaAPI.getIdCondition(existingChangeSet.getId(), ModuleFactory.getBundleChangeSetModule()));
//					isUpdateNeeded = false;
//					break;
//				case DUMMY:
//					throw new ValidationException("Add mode cannot be set to Dummy");
//			}
//			break;
//		case UPDATE:
//			switch(mode) {
//				case ADD:
//					throw new ValidationException("Update mode cannot be set to Add");
//				case UPDATE:
//					break;
//				case DELETE:
//					existingChangeSet.setModeEnum(BundleModeEnum.DELETE);
//					break;
//				case DUMMY:
//					throw new ValidationException("Update mode cannot be set to Dummy");
//			}
//			break;
//		case DELETE:
//			switch(mode) {
//				case ADD:
//					throw new ValidationException("Delete mode cannot be set to Add");
//				case UPDATE:
//					throw new ValidationException("Delete mode cannot be set to Update");
//				case DELETE:
//					break;
//				case DUMMY:
//					throw new ValidationException("Delete mode cannot be set to Dummy");
//			}
//			break;
//		case DUMMY:
//			switch(mode) {
//				case ADD:
//					throw new ValidationException("Dummy mode cannot be set to Add");
//				case UPDATE:
//					existingChangeSet.setModeEnum(BundleModeEnum.UPDATE);
//				case DELETE:
//					existingChangeSet.setModeEnum(BundleModeEnum.DELETE);
//					break;
//				case DUMMY:
//					break;
//			}
//			break;
//		}
//		
//		if(isUpdateNeeded) {
//			
//			existingChangeSet.setLastEditedTime(DateTimeUtil.getCurrenTime());
//			
//			updateBundleModule(ModuleFactory.getBundleChangeSetModule(), FieldFactory.getBundleChangeSetFields(), existingChangeSet, CriteriaAPI.getIdCondition(existingChangeSet.getId(), ModuleFactory.getBundleChangeSetModule()));
//		}
//		
//		return existingChangeSet;
//	}
//	
//	public static BundleChangeSetContext getOrAddModifiedComponent(BundleComponentsEnum componentType,Long componentId,String componentName,BundleModeEnum mode) throws Exception {
//		
//		Map<String, FacilioField> bundleChangeSetFieldMap = FieldFactory.getAsMap(FieldFactory.getBundleChangeSetFields());
//		
//		Criteria fetchCriteria = new Criteria();
//		fetchCriteria.addAndCondition(CriteriaAPI.getCondition(bundleChangeSetFieldMap.get("componentType"), componentType.getValue()+"", NumberOperators.EQUALS));
//		fetchCriteria.addAndCondition(CriteriaAPI.getCondition(bundleChangeSetFieldMap.get("componentId"), componentId+"", NumberOperators.EQUALS));
//		fetchCriteria.addAndCondition(CriteriaAPI.getCondition(bundleChangeSetFieldMap.get("commitStatus"), BundleCommitStatusEnum.NOT_YET_COMMITED.getValue()+"", NumberOperators.EQUALS));
//		
//		List<Map<String, Object>> changeSetComponents = fetchBundleRelated(ModuleFactory.getBundleChangeSetModule(), FieldFactory.getBundleChangeSetFields(), fetchCriteria, null);
//		
//		if(changeSetComponents != null && !changeSetComponents.isEmpty()) {
//			BundleChangeSetContext changeSet = FieldUtil.getAsBeanFromMap(changeSetComponents.get(0), BundleChangeSetContext.class);
//			return changeSet;
//		}
//		else {
//			
//			BundleChangeSetContext bundleChangeSetContext = new BundleChangeSetContext();
//			
//			bundleChangeSetContext.setComponentTypeEnum(componentType);
//			bundleChangeSetContext.setComponentId(componentId);
//			bundleChangeSetContext.setName(componentName);
//			bundleChangeSetContext.setOrgId(AccountUtil.getCurrentOrg().getId());
//			bundleChangeSetContext.setCommitStatusEnum(BundleCommitStatusEnum.NOT_YET_COMMITED);
//			bundleChangeSetContext.setModeEnum(mode == null ? BundleModeEnum.DUMMY : mode);
//			bundleChangeSetContext.setLastEditedTime(DateTimeUtil.getCurrenTime());
//			
//			if(componentType.getParent() != null) {
//				
//				FacilioContext context = new FacilioContext();
//				
//				context.put(BundleConstants.COMPONENT_ID, componentId);
//				
//				componentType.getBundleComponentClassInstance().getParentDetails(context);
//				Long parentComponentId = (Long)context.get(BundleConstants.PARENT_COMPONENT_ID);
//				String parentComponentName = (String)context.get(BundleConstants.PARENT_COMPONENT_NAME);
//				
//				BundleChangeSetContext parentChangeSet = getOrAddModifiedComponent(componentType.getParent(), parentComponentId, parentComponentName, null);
//				
//				bundleChangeSetContext.setParentId(parentChangeSet.getId());
//			}
//			
//			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
//					.table(ModuleFactory.getBundleChangeSetModule().getTableName())
//					.fields(FieldFactory.getBundleChangeSetFields());
//
//			Map<String, Object> props = FieldUtil.getAsProperties(bundleChangeSetContext);
//			insertBuilder.addRecord(props);
//			insertBuilder.save();
//
//			bundleChangeSetContext.setId((Long) props.get("id"));
//
//			return bundleChangeSetContext;
//		}
//	}
//	
	public static List<BundleChangeSetContext> getAllChangeSet() throws Exception {
		
		
		List<BundleChangeSetContext> returnChangeset = new ArrayList<BundleChangeSetContext>();
		
		Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> parentChildMap = BundleComponentsEnum.getParentChildMap();
		
		Queue<BundleComponentsEnum> componentsQueue = new LinkedList<BundleComponentsEnum>();
		
		componentsQueue.addAll(BundleComponentsEnum.getParentComponentList());
		
		while(!componentsQueue.isEmpty()) {
			
			BundleComponentsEnum component = componentsQueue.poll();
			
			BundleComponentInterface componentClass = component.getBundleComponentClassInstance();
			
			FacilioContext context = new FacilioContext();
			context.put(BundleConstants.COMPONENT, component);
			
			componentClass.getAddedChangeSet(context);
			
			List<BundleChangeSetContext> addedChangeSet = (List<BundleChangeSetContext>) context.get(BundleConstants.CHANGE_SET);
			
			context.put(BundleConstants.CHANGE_SET, null);
			
			componentClass.getModifiedChangeSet(context);
			
			List<BundleChangeSetContext> modifiedChangeSet = (List<BundleChangeSetContext>) context.get(BundleConstants.CHANGE_SET);
			
			context.put(BundleConstants.CHANGE_SET, null);
			
			componentClass.getDeletedChangeSet(context);
			
			List<BundleChangeSetContext> deletedChangeSet = (List<BundleChangeSetContext>) context.get(BundleConstants.CHANGE_SET);
			
			context.put(BundleConstants.CHANGE_SET, null);
			
			if(addedChangeSet != null) {
				returnChangeset.addAll(addedChangeSet);
			}
			if(modifiedChangeSet != null) {
				returnChangeset.addAll(modifiedChangeSet);
			}
			if(deletedChangeSet != null) {
				returnChangeset.addAll(deletedChangeSet);
			}
			
			ArrayList<BundleComponentsEnum> childList = parentChildMap.get(component);
			
			if(childList != null) {
				componentsQueue.addAll(childList);
			}
			
		}
		
		return returnChangeset;
	}
	
	public static void getFormattedObject(Object beanObject) throws Exception {
		
		List<Class<?>> superClasses = getSuperClasses(beanObject.getClass());
		
		JSONObject jsonValue = FieldUtil.getAsJSON(beanObject);
		
		for(Class<?> superClass : superClasses) {
			
			Reflections reflections = new Reflections(superClass.getName(), new FieldAnnotationsScanner());
			
			Set<Field> includeFields = reflections.getFieldsAnnotatedWith(IncludeInBundle.class);
			
			
			if(includeFields != null && !includeFields.isEmpty()) {
				
				for(Field field : includeFields) {
//					System.out.println("fields To Be added on include --- "+field.getName() +" value --- "+jsonValue.get(field.getName()));
				}
			}
			else {
				Set<Field> excludeFields = reflections.getFieldsAnnotatedWith(ExcludeInBundle.class);
				if(excludeFields != null && !excludeFields.isEmpty()) {
					
					Field[] fields = superClass.getDeclaredFields();
					
					List<String> excludeFieldNames = excludeFields.stream().map(Field::getName).collect(Collectors.toList());
					
					for(Field field : fields) {
						
						if(!excludeFieldNames.contains(field.getName())) {
//							System.out.println("fields To Be added on exclude --- "+field.getName() +" value --- "+jsonValue.get(field.getName()));
						}
					}
				}
			}
			
		}
		
	}
	
	private static List<Class<?>> getSuperClasses(Class<?> testClass) {
        ArrayList<Class<?>> results = new ArrayList<Class<?>>();
        Class<?> current = testClass;
        while (current != null && !current.getName().equals(Object.class.getName())) {
            results.add(current);
            current = current.getSuperclass();
        }
        return results;
    }
	
	public static int updateBundleModule(FacilioModule module,List<FacilioField> fields,Object context,Condition condition) throws Exception {
		
		GenericUpdateRecordBuilder updatetBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCondition(condition);

		Map<String, Object> props = FieldUtil.getAsProperties(context);
		return updatetBuilder.update(props);

	}
	
	public static int deleteBundleRelated(FacilioModule module,Condition condition) throws Exception {
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(condition);

		int detedRows =  deleteBuilder.delete();
		
		return detedRows;
	}
	
	public static String readFileContent(String filePath) throws IOException {
		
		String text = Files.asCharSource(new File(filePath), Charset.defaultCharset()).read();

		return text;
	}
	
	public static List<Map<String, Object>> fetchBundleRelated(FacilioModule module,List<FacilioField> fields,Criteria fetchCriteria,Condition condition) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				;
		
		if(fetchCriteria != null) {
			selectBuilder.andCriteria(fetchCriteria);
		}
		if(condition != null) {
			selectBuilder.andCondition(condition);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		return props;
	}
	
}
