package com.facilio.bundle.context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowFieldType;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

import io.jsonwebtoken.lang.Collections;

public class FunctionBundleComponent extends CommonBundleComponent {
	
	public static final String NAME_SPACE = "nameSpace";
	public static final String RETURN_STRING = "return";
	
	public static final String FS_EXTN = "fs";
	
	@Override
	public String getBundleXMLComponentFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long functionId = (Long) context.get(BundleConstants.COMPONENT_ID);
		WorkflowUserFunctionContext userFunction = UserFunctionAPI.getUserFunction(functionId);
		
		return userFunction.getNameSpaceName()+"_"+userFunction.getLinkName();
	}
	
	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long functionId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		XMLBuilder xmlBuilder = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		WorkflowUserFunctionContext function = UserFunctionAPI.getUserFunction(functionId);

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
		
		// adding .fs file to function folder
		
		BundleFolderContext componentFolder = (BundleFolderContext) context.get(BundleConstants.COMPONENTS_FOLDER);
		
		String fileName = getBundleXMLComponentFileName(context);
		
		BundleFolderContext functionNameSpaceFolder = componentFolder.getOrAddFolder(BundleComponentsEnum.FUNCTION.getName());
		
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
		
		InstalledBundleContext installedBundle = (InstalledBundleContext) context.get(BundleConstants.INSTALLED_BUNDLE);
		
		switch(modeEnum) {
		case ADD: {
			WorkflowUserFunctionContext userFunction = new WorkflowUserFunctionContext();
			
			userFunction.setWorkflowV2String(scriptContent);
			userFunction.setLinkName(changeSetXMLFile.getXmlContent().getElement(BundleConstants.Components.NAME).getText());
			userFunction.setIsV2Script(Boolean.TRUE);
			userFunction.setReturnType(WorkflowFieldType.getStringvaluemap().get(returnType).getIntValue());
			userFunction.setNameSpaceId(UserFunctionAPI.getNameSpace(nameSpaceName).getId());
			
			userFunction.setSourceBundle(installedBundle.getId());
			
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
			
			userFunction.setSourceBundle(installedBundle.getId());
			
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
	public BundleModeEnum getInstallMode(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		XMLBuilder xmlContent = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		String nameSpaceName = xmlContent.getAttribute(NAME_SPACE);
		
		String functionName = xmlContent.getElement(BundleConstants.Components.NAME).getText();
		
		BundleModeEnum installMode = null;
		
		if(UserFunctionAPI.getWorkflowFunctionFromLinkName(nameSpaceName, functionName) != null) {
			installMode = BundleModeEnum.UPDATE;
		}
		else {
			installMode = BundleModeEnum.ADD;
		}
		
		return installMode;
		
	}
	
}
