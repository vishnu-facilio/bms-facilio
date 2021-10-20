package com.facilio.bundle.context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowFieldType;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2API;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

import io.jsonwebtoken.lang.Collections;

public class FunctionBundleComponent extends CommonBundleComponent {
	
	public static final String NAME_SPACE = "nameSpace";
	public static final String RETURN_STRING = "return";
	
	public static final String FS_EXTN = "fs";
	
	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long functionId = (Long) context.get(BundleConstants.COMPONENT_ID);
		WorkflowUserFunctionContext userFunction = WorkflowV2API.getUserFunction(functionId);
		
		return userFunction.getNameSpaceName()+"_"+userFunction.getLinkName();
	}
	
	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		
		String fileName = BundleComponentsEnum.FUNCTION.getName()+File.separatorChar+getFileName(context)+".xml";
		XMLBuilder bundleBuilder = (XMLBuilder) context.get(BundleConstants.BUNDLE_XML_BUILDER);
		bundleBuilder.element(BundleConstants.VALUES).attr("version", componentChange.getTempVersion()+"").text(fileName);
	}

	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		BundleFolderContext componentFolder = (BundleFolderContext) context.get(BundleConstants.COMPONENTS_FOLDER);
		
		WorkflowUserFunctionContext function = WorkflowV2API.getUserFunction(componentChange.getComponentId());

		String fileName = getFileName(context);
		
		BundleFolderContext functionNameSpaceFolder = componentFolder.getOrAddFolder(componentChange.getComponentTypeEnum().getName());
		
		BundleFileContext functionXMLFile = new BundleFileContext(fileName, BundleConstants.XML_FILE_EXTN, componentChange.getComponentTypeEnum().getName(), null);
		
		XMLBuilder xmlBuilder = functionXMLFile.getXmlContent();
		
		xmlBuilder.attr(NAME_SPACE, function.getNameSpaceName())
				  .element(BundleConstants.Components.DISPLAY_NAME)
					.text(function.getName())
					.p()
				 .element(BundleConstants.Components.NAME)
					.text(function.getLinkName())
					.p()
				  .element(RETURN_STRING)
				    .text(function.getReturnTypeEnum().getStringValue())
				    .p();
		
		functionNameSpaceFolder.addFile(fileName+"."+BundleConstants.XML_FILE_EXTN, functionXMLFile);
		
		BundleFileContext functionFile = new BundleFileContext(fileName, FS_EXTN, null, function.getWorkflowV2String());
		
		functionNameSpaceFolder.addFile(fileName+"."+FS_EXTN, functionFile);
		
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		BundleFolderContext parentFolder = (BundleFolderContext) context.get(BundleConstants.BUNDLE_FOLDER);
		
		String scriptContent = parentFolder.getFile(changeSetXMLFile.getName()+"."+FS_EXTN).getFileContent();
		
		BundleModeEnum modeEnum = (BundleModeEnum) context.get(BundleConstants.INSTALL_MODE);
		
		String nameSpaceName = changeSetXMLFile.getXmlContent().getAttribute(NAME_SPACE);
		
		String returnType = changeSetXMLFile.getXmlContent().getElement(RETURN_STRING).getText();
		
		switch(modeEnum) {
		case ADD: {
			WorkflowUserFunctionContext userFunction = new WorkflowUserFunctionContext();
			
			userFunction.setWorkflowV2String(scriptContent);
			userFunction.setLinkName(changeSetXMLFile.getXmlContent().getElement(BundleConstants.Components.NAME).getText());
			userFunction.setIsV2Script(Boolean.TRUE);
			userFunction.setReturnType(WorkflowFieldType.getStringvaluemap().get(returnType).getIntValue());
			userFunction.setNameSpaceId(UserFunctionAPI.getNameSpace(nameSpaceName).getId());
			
			FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowUserFunctionChain();
			FacilioContext newContext = addWorkflowChain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
			addWorkflowChain.execute();
			
			break;
		}
		
		case UPDATE: {
			
			String functionName = changeSetXMLFile.getXmlContent().getElement(BundleConstants.Components.NAME).getText();
			
			WorkflowUserFunctionContext userFunction = (WorkflowUserFunctionContext) UserFunctionAPI.getWorkflowFunctionFromLinkName(nameSpaceName, functionName);
			
			userFunction.setWorkflowV2String(scriptContent);
			userFunction.setIsV2Script(Boolean.TRUE);
			
			FacilioChain updateWorkflowChain =  TransactionChainFactory.getUpdateWorkflowUserFunctionChain();
			FacilioContext newContext = updateWorkflowChain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
			updateWorkflowChain.execute();
			
			break;
		}
		
		}
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getInstallMode(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		
		XMLBuilder xmlContent = changeSetXMLFile.getXmlContent();
		
		String nameSpaceName = xmlContent.getAttribute(NAME_SPACE);
		
		String functionName = xmlContent.getElement(BundleConstants.Components.NAME).getText();
		
		BundleModeEnum installMode = null;
		
		if(UserFunctionAPI.getWorkflowFunctionFromLinkName(nameSpaceName, functionName) != null) {
			installMode = BundleModeEnum.UPDATE;
		}
		else {
			installMode = BundleModeEnum.ADD;
		}
		
		context.put(BundleConstants.INSTALL_MODE, installMode);
		
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
		
		Map<String, FacilioField> componentFieldMap = new HashMap<String, FacilioField>();
		
		for(com.facilio.modules.fields.FacilioField field : component.getFields()) {
			
			componentFieldMap.put(field.getName(), field);
		}

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(component.getFields())
				.table(component.getModule().getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on("Workflow.ID = Workflow_User_Function.ID")
				.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getModifiedTimeFieldName()), "", CommonOperators.IS_NOT_EMPTY))
				;
		
		if(!Collections.isEmpty(alreadyAddedComponentIds)) {
			select.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getIdFieldName()), StringUtils.join(alreadyAddedComponentIds, ","), NumberOperators.NOT_EQUALS));
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
		
		Map<String, FacilioField> componentFieldMap = new HashMap<String, FacilioField>();
		
		for(com.facilio.modules.fields.FacilioField field : component.getFields()) {
			
			componentFieldMap.put(field.getName(), field);
		}
		
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
						.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
						.on("Workflow.ID = Workflow_User_Function.ID")
						.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getIdFieldName()), StringUtils.join(alreadyAddedOrModifiedComponentIds, ","), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getModifiedTimeFieldName()), "", CommonOperators.IS_NOT_EMPTY))
						.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getModifiedTimeFieldName()), bundleCreatedTime+"", DateOperators.IS_AFTER))
						;
				
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

}
