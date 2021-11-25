package com.facilio.bundle.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.io.FilenameUtils;

import com.amazonaws.services.cloudfront.model.GetFieldLevelEncryptionConfigRequest;
import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.context.BundleFileContext;
import com.facilio.bundle.context.BundleFolderContext;
import com.facilio.bundle.context.InstalledBundleContext;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;

import io.jsonwebtoken.lang.Collections;

public class InstallBundleComponentsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Double> toBeInstalledVersionList = (List<Double>) context.get(BundleConstants.TO_BE_INSTALLED_VERSIONS_LIST);
		
		InstalledBundleContext installedBundle = (InstalledBundleContext) context.get(BundleConstants.INSTALLED_BUNDLE);
		
		BundleFolderContext rootFolder = (BundleFolderContext) context.get(BundleConstants.BUNDLE_FOLDER);
		
		BundleFileContext bundleXmlFileContext = rootFolder.getFile(BundleConstants.BUNDLE_FILE_NAME+"."+BundleConstants.XML_FILE_EXTN);
		
		XMLBuilder BundleXML = bundleXmlFileContext.getXmlContent();
		
		List<XMLBuilder> components = BundleXML.getElement(BundleConstants.COMPONENTS).getElementList(BundleConstants.COMPONENT);
		
		BundleFolderContext componentsFolder = rootFolder.getFolder(BundleConstants.COMPONENTS_FOLDER_NAME);
		
		String globalName = (String) context.get(BundleConstants.GLOBAL_NAME);
		
		List<InstalledBundleContext> installedVersions = getAllInstalledVersions(globalName);
		
		Map<BundleComponentsEnum, List<BundleChangeSetContext>> installedComponentsMap = getAllInstalledComponents(installedVersions);
		
		// --deletion handled here
		
		handleComponentDeletion(installedComponentsMap,components);
		
		// --add and update handled here
		
		handleComponentAddAndUpdate(components,componentsFolder,toBeInstalledVersionList,installedBundle);
		
		return false;
	}

	private void handleComponentAddAndUpdate(List<XMLBuilder> components, BundleFolderContext componentsFolder, List<Double> toBeInstalledVersionList, InstalledBundleContext installedBundle) throws Exception {
		// TODO Auto-generated method stub
		
		for(XMLBuilder component :components) {
			
			String componentName = component.getAttribute(BundleConstants.Components.NAME);
			
			BundleComponentsEnum bundleComponentEnum = BundleComponentsEnum.getAllBundleComponentsByName().get(componentName);
			
			BundleFolderContext parentFolder = componentsFolder.getFolder(componentName);
			
			List<XMLBuilder> allComponents = component.getElementList(BundleConstants.VALUES);
			
			for(XMLBuilder subComponent : allComponents) {
				
				Double componentVersion = Double.parseDouble(subComponent.getAttribute(BundleConstants.VERSION));
				
				if(toBeInstalledVersionList.contains(componentVersion)) {
					String subComponentPath = subComponent.getText();
					
					String fileName = FilenameUtils.getName(subComponentPath);
					
					BundleFileContext changeSetXMLFile = parentFolder.getFile(fileName);
					
					FacilioContext newContext = new FacilioContext();
					
					newContext.put(BundleConstants.INSTALLED_BUNDLE, installedBundle);
					newContext.put(BundleConstants.BUNDLED_XML_COMPONENT_FILE, changeSetXMLFile);
					newContext.put(BundleConstants.COMPONENT_XML_BUILDER, changeSetXMLFile.getXmlContent());
					newContext.put(BundleConstants.BUNDLE_FOLDER, parentFolder);
					
					BundleModeEnum modeEnum = bundleComponentEnum.getBundleComponentClassInstance().getInstallMode(newContext);
					
					newContext.put(BundleConstants.INSTALL_MODE, modeEnum);
					
					bundleComponentEnum.getBundleComponentClassInstance().install(newContext);
				}
			}
		}
	}

	private void handleComponentDeletion(Map<BundleComponentsEnum, List<BundleChangeSetContext>> installedComponentsMap, List<XMLBuilder> components) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String,List<String>> availableComponentsMap = new HashMap<String, List<String>>();
		
		for(XMLBuilder component :components) {
			
			String componentName = component.getAttribute(BundleConstants.Components.NAME);
			
			List<String> availableComponentsInCurrentInstall = new ArrayList<String>();
			
			List<XMLBuilder> allComponents = component.getElementList(BundleConstants.VALUES);
			
			for(XMLBuilder subComponent : allComponents) {
				String subComponentPath = subComponent.getText();
				
				availableComponentsInCurrentInstall.add(subComponentPath);
			}
			
			availableComponentsMap.put(componentName, availableComponentsInCurrentInstall);
		}
		
		Map<Integer, BundleComponentsEnum> allBundleComponents = BundleComponentsEnum.getAllBundleComponents();
		
		List<BundleComponentsEnum> reverseOrderedComponentList = allBundleComponents.values().stream().sorted((comp1,comp2) ->  comp2.getLevel() - comp1.getLevel()).collect(Collectors.toList());
		
		List<BundleChangeSetContext> toBeDeletedList = new ArrayList<BundleChangeSetContext>();
		
		for(BundleComponentsEnum componentEnum : reverseOrderedComponentList) {
			
			if(installedComponentsMap.containsKey(componentEnum)) {
				
				List<BundleChangeSetContext> installedComponents = installedComponentsMap.get(componentEnum);
				
				if(!Collections.isEmpty(installedComponents)) {
					
					List<String> availableComponentsFileName = availableComponentsMap.get(componentEnum.getName());
					
					for(BundleChangeSetContext installedComponent : installedComponents) {
						
						FacilioContext newContext = new FacilioContext();
						
						newContext.put(BundleConstants.COMPONENT_ID,installedComponent.getComponentId());
						
						String fileName = componentEnum.getName()+"/"+componentEnum.getBundleComponentClassInstance().getBundleXMLComponentFileName(newContext)+".xml";
						
						if(availableComponentsFileName == null || !availableComponentsFileName.contains(fileName)) {
							toBeDeletedList.add(installedComponent);
						}
					}
				}
			}
		}
		
		for(BundleChangeSetContext toBeDeleted :toBeDeletedList) {
			
			BundleComponentsEnum componentEnum = toBeDeleted.getComponentTypeEnum();
			
			FacilioContext newContext = new FacilioContext();
			
			newContext.put(BundleConstants.COMPONENT_ID, toBeDeleted.getComponentId());
			newContext.put(BundleConstants.INSTALL_MODE, BundleModeEnum.DELETE);
			
			componentEnum.getBundleComponentClassInstance().install(newContext);
		}
		
	}
	
	private Map<BundleComponentsEnum, List<BundleChangeSetContext>> getAllInstalledComponents(List<InstalledBundleContext> installedVersions) throws Exception {
		// TODO Auto-generated method stub
		
		Map<BundleComponentsEnum,List<BundleChangeSetContext>> installedComponentsMap = new HashMap<BundleComponentsEnum, List<BundleChangeSetContext>>();
		
		Queue<BundleComponentsEnum> componentsQueue = new LinkedList<BundleComponentsEnum>();
		
		componentsQueue.addAll(BundleComponentsEnum.getParentComponentList());
		
		Map<BundleComponentsEnum, ArrayList<BundleComponentsEnum>> parentChildMap = BundleComponentsEnum.getParentChildMap();
		
		
		List<Long> installedVersionIds = installedVersions.stream().map(InstalledBundleContext::getId).collect(Collectors.toList());
		
		while(!componentsQueue.isEmpty()) {
			
			BundleComponentsEnum component = componentsQueue.poll();
			
			FacilioContext newContext = new FacilioContext();
			
			newContext.put(BundleConstants.INSTALLED_BUNDLES, installedVersions);
			newContext.put(BundleConstants.COMPONENT, component);
			newContext.put(BundleConstants.INSTALLED_BUNDLE_VERSION_ID_LIST, installedVersionIds);
			
			component.getBundleComponentClassInstance().getInstalledComponents(newContext);
			
			List<BundleChangeSetContext> installedComponents = (List<BundleChangeSetContext>) newContext.get(BundleConstants.INSTALLED_COMPONENT_LIST);
			
			installedComponentsMap.put(component, installedComponents);
			
			ArrayList<BundleComponentsEnum> childList = parentChildMap.get(component);
			
			if(childList != null) {
				componentsQueue.addAll(childList);
			}
		}
		
		return installedComponentsMap;
	}

	private List<InstalledBundleContext> getAllInstalledVersions(String globalName) throws Exception {
		
		
		Map<String, FacilioField> installedBundleFieldMap = FieldFactory.getAsMap(FieldFactory.getInstalledBundleFields());
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getInstalledBundleModule().getTableName())
				.select(FieldFactory.getInstalledBundleFields())
				.andCondition(CriteriaAPI.getCondition(installedBundleFieldMap.get("bundleGlobalName"), globalName, StringOperators.IS))
				;
		
		List<Map<String, Object>> props = builder.get();
		
		List<InstalledBundleContext> installedVersions = FieldUtil.getAsBeanListFromMapList(props, InstalledBundleContext.class);
		
		return installedVersions;
	}

}
