package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.fw.cache.LRUCache;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class DeployPackageComponentCommand extends FacilioCommand implements PostTransactionCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageUtil.setInstallThread();

		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);
		Long packageId = packageContext.getId();
		double packageVersion = packageContext.getVersion();
		PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
		double previousVersion = (double) context.get(PackageConstants.PREVIOUS_VERSION);
		XMLBuilder packageConfigXML = (XMLBuilder) context.get(PackageConstants.PACKAGE_CONFIG_XML);
		long sourceOrgId = (Long) context.getOrDefault(PackageConstants.SOURCE_ORG_ID, -1L);

		List<XMLBuilder> allComponents = packageConfigXML.getElement(PackageConstants.PackageXMLConstants.COMPONENTS)
									.getFirstLevelElementListForTagName(PackageConstants.PackageXMLConstants.COMPONENT);
		PackageFolderContext componentsFolder = rootFolder.getFolder(PackageConstants.COMPONENTS_FOLDER_NAME);

		List<Integer> skipComponents = (List<Integer>) context.getOrDefault(PackageConstants.SKIP_COMPONENTS, new ArrayList<>());
		long sandboxId = (long) context.getOrDefault(SandboxConstants.SANDBOX_ID, -1L);

		// Add PickList Modules data to ThreadLocal even if a component is not present in PACKAGE_CONFIG_XML
		if (CollectionUtils.isNotEmpty(allComponents)) {
			List<ComponentType> componentTypeList = allComponents.stream().map(component -> ComponentType.valueOf(component.getAttribute(PackageConstants.PackageXMLConstants.COMPONENT_TYPE))).collect(Collectors.toList());

			Collection<ComponentType> picklistComponentsNotInPackageConfig = CollectionUtils.subtract(ComponentType.PICKLIST_COMPONENTS, componentTypeList);
			for (ComponentType componentType : picklistComponentsNotInPackageConfig) {
				componentType.getPackageComponentClassInstance().addPickListConf();
			}
		}

        // Adding AllPackageChangesets to ThreadLocal
		PackageUtil.setPackageId(packageId);
        PackageBeanUtil.addAllPackageChangesetsToThread(packageContext.getId());

		//For sending sandbox progress on installation
		double sandboxProgress =  ((Number) context.getOrDefault(SandboxConstants.SANDBOX_PROGRESS, PackageUtil.SandboxProgressCheckPointType.PACKAGE_INSTALLATION_STARTED.getIntVal())).doubleValue();
		int process;
		long count = allComponents.isEmpty() ? 1L : allComponents.size();
		double originalParts = (double) PackageUtil.SandboxProcessLoadType.CUSTOMIZATION_INSTALL_PROCESS.getIntVal() / count;
		double parts = Math.floor(originalParts * 100) / 100;
		for(XMLBuilder component : allComponents) {
			ComponentType componentType = ComponentType.valueOf(component.getAttribute(PackageConstants.PackageXMLConstants.COMPONENT_TYPE));
			if(componentType.getComponentClass() == null || skipComponents.contains(componentType.getIndex())) {
				if(sandboxId > -1L) {
					sandboxProgress = sandboxProgress + parts;
					process = (int)sandboxProgress;
					SandboxAPI.sendSandboxProgress(process, sandboxId, "Installation done for component name--> "+ componentType.name(), sourceOrgId);
				}
				continue;
			}
			LOGGER.info("####Sandbox - Started Deploying ComponentType - " + componentType.name());
			Map<String, Long> uniqueIdVsComponentIdList = PackageUtil.getComponentsUIdVsComponentIdForComponent(componentType);

			String componentTypeFilePath = component.getText();
			PackageFileContext componentXMLContext = componentsFolder.getFile(componentTypeFilePath);
			List<XMLBuilder> componentXMLBuilders = componentXMLContext.getXmlContent()
											.getElementList(componentType.name());

			Map<String, XMLBuilder> createComponentList = new HashMap<>();
			Map<Long, XMLBuilder> updateComponentList = new HashMap<>();
			List<Long> deleteIdList = new ArrayList<>();
			Map<String, XMLBuilder> idsToFetch = new HashMap<>();
			List<PackageChangeSetMappingContext> mappingToUpdate = new ArrayList<>();
			Map<String, PackageChangeSetMappingContext> mappingToAdd = new HashMap();

			for(XMLBuilder componentXML : componentXMLBuilders) {
				String uniqueIdentifier = componentXML.getAttribute(PackageConstants.PackageXMLConstants.UNIQUE_IDENTIFIER);
				Double createdVersion = Double.parseDouble(componentXML.getAttribute(PackageConstants.PackageXMLConstants.CREATED_VERSION));
				Double modifiedVersion = Double.parseDouble(componentXML.getAttribute(PackageConstants.PackageXMLConstants.MODIFIED_VERSION));
				PackageChangeSetMappingContext.ComponentStatus status = PackageChangeSetMappingContext.ComponentStatus.valueOf(componentXML.getAttribute(PackageConstants.PackageXMLConstants.COMPONENT_STATUS));

				if(previousVersion > 0.0 &&
						(status.equals(PackageChangeSetMappingContext.ComponentStatus.ADDED) && createdVersion <= previousVersion)
						|| ((status.equals(PackageChangeSetMappingContext.ComponentStatus.UPDATED)
								|| status.equals(PackageChangeSetMappingContext.ComponentStatus.DELETED))
							&& modifiedVersion <= previousVersion)) {
					continue;
				}


				long parentId = -1l;
				String parentComponentIdAttr = componentXML.getAttribute(PackageConstants.PackageXMLConstants.PARENT_COMPONENT_ID);
				if(StringUtils.isNotEmpty(parentComponentIdAttr)) {
					parentId = PackageUtil.getParentComponentId(componentType, parentComponentIdAttr);
				}

				PackageChangeSetMappingContext mapping = new PackageChangeSetMappingContext();
				mapping.setPackageId(packageId);
				mapping.setUniqueIdentifier(uniqueIdentifier);
				mapping.setParentComponentId(parentId);
				mapping.setComponentType(componentType);
				mapping.setStatus(status);

				if(previousVersion == 0.0D && createdVersion.equals(modifiedVersion) && status.equals(PackageChangeSetMappingContext.ComponentStatus.UPDATED)) {
					idsToFetch.put(uniqueIdentifier, componentXML);
					mapping.setCreatedVersion(packageVersion);
					mapping.setModifiedVersion(packageVersion);
					mappingToAdd.put(uniqueIdentifier, mapping);
				} else {
					switch(status) {
						case ADDED:
							createComponentList.put(uniqueIdentifier, componentXML);
							mapping.setCreatedVersion(packageVersion);
							mappingToAdd.put(uniqueIdentifier, mapping);
							break;
						case UPDATED:
							long componentId = uniqueIdVsComponentIdList.get(uniqueIdentifier);
							updateComponentList.put(componentId, componentXML);
							mapping.setModifiedVersion(packageVersion);
							mapping.setComponentId(componentId);
							mappingToUpdate.add(mapping);
							break;
						default:
							if(uniqueIdVsComponentIdList.containsKey(uniqueIdentifier)) {
								deleteIdList.add(uniqueIdVsComponentIdList.get(uniqueIdentifier));
							}
							break;
					}
				}
			}

//			if(!updateComponentList.isEmpty()) {
//				componentType.getPackageComponentClassInstance().getDeletedComponentIds(updateComponentList.keySet());
//			}

			Map<String, Long> systemComponentIds = new HashMap<>();
			if(MapUtils.isNotEmpty(idsToFetch)) {
				systemComponentIds = componentType.getPackageComponentClassInstance().getExistingIdsByXMLData(idsToFetch);
				if (MapUtils.isNotEmpty(systemComponentIds)) {
					for (Map.Entry<String, XMLBuilder> updateXMLs : idsToFetch.entrySet()) {
						// Skipping if System ComponentID is not found
						if (systemComponentIds.containsKey(updateXMLs.getKey())) {
							updateComponentList.put(systemComponentIds.get(updateXMLs.getKey()), updateXMLs.getValue());
						} else {
							LOGGER.info("####Sandbox - System ComponentID not found for UID " + updateXMLs.getKey());
						}
					}
				}
			}

			//Create
			Map<String, Long> createdComponentIds = componentType.getPackageComponentClassInstance().createComponentFromXML(createComponentList);
			if(MapUtils.isNotEmpty(systemComponentIds)) {
				createdComponentIds.putAll(systemComponentIds);
			}
			PackageUtil.addComponentsUIdVsComponentId(componentType, createdComponentIds);
			PackageUtil.checkForDuplicateUIds(componentType, mappingToAdd);

			PackageUtil.computeAndAddPackageChangeset(mappingToAdd, createdComponentIds);

			//Update
			componentType.getPackageComponentClassInstance().updateComponentFromXML(updateComponentList);
			if(CollectionUtils.isNotEmpty(mappingToUpdate)) {
				PackageUtil.updatePackageMappingChangesets(mappingToUpdate);
			}

			componentType.getPackageComponentClassInstance().addPickListConf();

			//Delete
			if(CollectionUtils.isNotEmpty(deleteIdList)) {
				componentType.getPackageComponentClassInstance().deleteComponentFromXML(deleteIdList);
			}

			LOGGER.info("####Sandbox - Completed Deploying ComponentType - " + componentType.name());
			if(sandboxId > -1L) {
				sandboxProgress = sandboxProgress + parts;
				process = (int)sandboxProgress;
				SandboxAPI.sendSandboxProgress(process, sandboxId, "Installation done for component name--> "+ componentType.name(), sourceOrgId);
			}
		}

		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		LRUCache.purgeAllCache();
		PackageUtil.clearInstallThread();
		PackageUtil.clearPickListConf();
		return false;
	}

	@Override
	public void onError() throws Exception {
		LRUCache.purgeAllCache();
		PackageUtil.clearInstallThread();
		PackageUtil.clearPickListConf();
	}

}
