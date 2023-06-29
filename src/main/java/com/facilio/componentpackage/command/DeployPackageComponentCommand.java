package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageUtil;
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

		List<XMLBuilder> allComponents = packageConfigXML.getElement(PackageConstants.PackageXMLConstants.COMPONENTS)
									.getFirstLevelElementListForTagName(PackageConstants.PackageXMLConstants.COMPONENT);
		PackageFolderContext componentsFolder = rootFolder.getFolder(PackageConstants.COMPONENTS_FOLDER_NAME);

		for(XMLBuilder component : allComponents) {
			ComponentType componentType = ComponentType.valueOf(component.getAttribute(PackageConstants.PackageXMLConstants.COMPONENT_TYPE));
			if(componentType.getComponentClass() == null) {
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
				for(Map.Entry<String, XMLBuilder> updateXMLs : idsToFetch.entrySet()) {
					updateComponentList.put(systemComponentIds.get(updateXMLs.getKey()), updateXMLs.getValue());
				}
			}

			//Create
			Map<String, Long> createdComponentIds = componentType.getPackageComponentClassInstance().createComponentFromXML(createComponentList);
			if(MapUtils.isNotEmpty(systemComponentIds)) {
				createdComponentIds.putAll(systemComponentIds);
			}
			PackageUtil.addComponentsUIdVsComponentId(componentType, createdComponentIds);
			checkForDuplicateUIds(componentType, mappingToAdd);

			computeAndAddPackageChangeset(mappingToAdd, createdComponentIds);

			//Update
			componentType.getPackageComponentClassInstance().updateComponentFromXML(updateComponentList);
			if(CollectionUtils.isNotEmpty(mappingToUpdate)) {
				PackageUtil.updatePackageMappingChangesets(mappingToUpdate);
			}

			//Delete
			if(CollectionUtils.isNotEmpty(deleteIdList)) {
				componentType.getPackageComponentClassInstance().deleteComponentFromXML(deleteIdList);
			}

			LOGGER.info("####Sandbox - Completed Deploying ComponentType - " + componentType.name());
		}

		PackageUtil.clearInstallThread();
		return false;
	}

	private void computeAndAddPackageChangeset(Map<String, PackageChangeSetMappingContext> uniqueIdVsMapping, Map<String, Long> uniqueIdVsComponentId) throws Exception {
		List<PackageChangeSetMappingContext> mappings = uniqueIdVsMapping.entrySet().stream()
				.filter(entry -> uniqueIdVsComponentId.containsKey(entry.getKey()))
				.peek(entry -> entry.getValue().setComponentId(uniqueIdVsComponentId.get(entry.getKey())))
				.map(Map.Entry::getValue)
				.collect(Collectors.toList())
				;
		PackageUtil.addPackageMappingChangesets(mappings);
	}

	private void checkForDuplicateUIds(ComponentType componentType, Map<String, PackageChangeSetMappingContext> mappingToAdd) {
		Map<String, Long> uidVsCompId = PackageUtil.getComponentsUIdVsComponentIdForComponent(componentType);

		Map<Long, String> compIdVsUid = new HashMap<>();
		Map<Long, Set<String>> compIdVsDuplicateUid = new HashMap<>();
		for (Map.Entry<String, Long> entry : uidVsCompId.entrySet()) {
			String currUId = entry.getKey();
			long currCompId = entry.getValue();

			if (compIdVsUid.containsKey(currCompId)) {
				String oldUId = compIdVsUid.get(currCompId);
				mappingToAdd.remove(currUId);

				Set<String> duplicateUIdSet;
				if (!compIdVsDuplicateUid.containsKey(currCompId)) {
					compIdVsDuplicateUid.put(currCompId, new HashSet<>());
				}
				duplicateUIdSet = compIdVsDuplicateUid.get(currCompId);
				duplicateUIdSet.add(oldUId);
				duplicateUIdSet.add(currUId);
			}
			compIdVsUid.put(currCompId, currUId);
		}

		LOGGER.info("####Sandbox - Duplicate ComponentIdVsUIds - " + componentType.name() + " - " + compIdVsDuplicateUid);
	}

	@Override
	public boolean postExecute() throws Exception {
		return false;
	}

	@Override
	public void onError() throws Exception {
		PackageUtil.clearInstallThread();
	}

}
