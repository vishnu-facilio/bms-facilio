package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class DeployPackageComponentCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageUtil.setInstallThread();

		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);
		Long packageId = packageContext.getId();
		double packageVersion = packageContext.getVersion();
		PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
		double previousVersion = (double) context.get(PackageConstants.PREVIOUS_VERSION);
		XMLBuilder packageConfigXML = (XMLBuilder) context.get(PackageConstants.PACKAGE_CONFIG_XML);

		List<XMLBuilder> allComponents = packageConfigXML.getElement(PackageConstants.PackageXMLConstants.COMPONENTS).getElementList(PackageConstants.PackageXMLConstants.COMPONENT);
		PackageFolderContext componentsFolder = rootFolder.getFolder(PackageConstants.COMPONENTS_FOLDER_NAME);
		Map<ComponentType, Map<String, Long>> componentsUniqueIdVsComponentId = new HashMap<>();

		for(XMLBuilder component : allComponents) {
			ComponentType componentType = ComponentType.valueOf(component.getAttribute(PackageConstants.PackageXMLConstants.COMPONENT_TYPE));
			if(componentType.getComponentClass() == null) {
				continue;
			}
			Map<String, Long> uniqueIdVsComponentIdList = componentsUniqueIdVsComponentId.computeIfAbsent(componentType, k -> new HashMap<>());

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
					Long parentIdFromPackage = Long.parseLong(parentComponentIdAttr);
					parentId = componentsUniqueIdVsComponentId.get(componentType.getParentComponentType()).get(parentIdFromPackage);
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
				componentsUniqueIdVsComponentId.get(componentType).putAll(systemComponentIds);
			}

			//Create
			Map<String, Long> createdComponentIds = componentType.getPackageComponentClassInstance().createComponentFromXML(createComponentList);
			componentsUniqueIdVsComponentId.get(componentType).putAll(createdComponentIds);
			if(MapUtils.isNotEmpty(systemComponentIds)) {
				createdComponentIds.putAll(systemComponentIds);
			}
			computeAndAddPackageChangeset(mappingToAdd, createdComponentIds);

			//Update
			componentType.getPackageComponentClassInstance().updateComponentFromXML(updateComponentList);
			if(CollectionUtils.isNotEmpty(mappingToUpdate)) {
				PackageUtil.updatePackageMappingChangesets(mappingToUpdate);
			}

			//Delete
			if(CollectionUtils.isEmpty(deleteIdList)) {
				componentType.getPackageComponentClassInstance().deleteComponentFromXML(deleteIdList);
			}

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

}
