package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class CreateXMLPackageCommand extends FacilioCommand implements PostTransactionCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);

		PackageFolderContext rootFolder = new PackageFolderContext("Org_Package_" + packageContext.getUniqueName() + "_" + DateTimeUtil.getFormattedTime(DateTimeUtil.getCurrenTime()));
		PackageFolderContext componentsFolder = rootFolder.addFolder(PackageConstants.COMPONENTS_FOLDER_NAME);

		XMLBuilder packageConfigXML = XMLBuilder.create(PackageConstants.PackageXMLConstants.PACKAGE);
		packageConfigXML.element(PackageConstants.NAME).text(packageContext.getUniqueName());
		packageConfigXML.element(PackageConstants.DISPLAY_NAME).text(packageContext.getDisplayName());
		packageConfigXML.element(PackageConstants.VERSION).text(String.valueOf(packageContext.getVersion()));
		packageConfigXML.element(PackageConstants.TYPE).text(String.valueOf(packageContext.getType()));
		XMLBuilder componentsConfigXML = packageConfigXML.element(PackageConstants.PackageXMLConstants.COMPONENTS);
		long sandboxId = (long) context.getOrDefault(SandboxConstants.SANDBOX_ID, -1L);

		Map<ComponentType, List<PackageChangeSetMappingContext>> typeVsComponents = PackageUtil.getAllPackageChangsets(packageContext.getId());
		float i = 20;
		int count = typeVsComponents.size();
		float parts = (float) 30 /count;
		int progress;
		for(Map.Entry<ComponentType, List<PackageChangeSetMappingContext>> typeVsComponent : typeVsComponents.entrySet()) {
			ComponentType componentType = typeVsComponent.getKey();
			if(componentType.getComponentClass() == null) {
				continue;
			}
			LOGGER.info("####Sandbox - Started Packaging ComponentType - " + componentType.name());
			PackageBean componentImplClass = componentType.getPackageComponentClassInstance();
			XMLBuilder componentsXML = XMLBuilder.create(PackageConstants.PackageXMLConstants.COMPONENTS);

			List<Long> componentIds = typeVsComponent.getValue().stream().map(component -> component.getComponentId()).collect(Collectors.toList());
			Map idVsComponent = componentImplClass.fetchComponents(componentIds);

			for(PackageChangeSetMappingContext componentMapping : typeVsComponent.getValue()) {
				if (!idVsComponent.containsKey(componentMapping.getComponentId())) {
					continue;
				}
 				XMLBuilder componentXML = componentsXML.element(componentType.getValue());
				componentXML.attr(PackageConstants.PackageXMLConstants.UNIQUE_IDENTIFIER, componentMapping.getUniqueIdentifier());
				componentXML.attr(PackageConstants.PackageXMLConstants.COMPONENT_TYPE,  String.valueOf(componentType.getIndex()));
				componentXML.attr(PackageConstants.PackageXMLConstants.COMPONENT_STATUS, componentMapping.getStatusEnum().getValue());
				componentXML.attr(PackageConstants.PackageXMLConstants.CREATED_VERSION, String.valueOf(componentMapping.getCreatedVersion()));
				componentXML.attr(PackageConstants.PackageXMLConstants.MODIFIED_VERSION, (componentMapping.getModifiedVersion() != null) ? String.valueOf(componentMapping.getModifiedVersion()) : "0.0");
				if (componentMapping.getParentComponentId() > 0) {
					componentXML.attr(PackageConstants.PackageXMLConstants.PARENT_COMPONENT_ID, String.valueOf(componentMapping.getParentComponentId()));
				}
				componentImplClass.convertToXMLComponent(idVsComponent.get(componentMapping.getComponentId()), componentXML);
			}
			componentsFolder.addFile(componentType.getValue(), new PackageFileContext(componentType.getValue(), PackageConstants.XML_FILE_EXTN, componentsXML));
			XMLBuilder componentConfigXML = componentsConfigXML.element(PackageConstants.PackageXMLConstants.COMPONENT);
			componentConfigXML.attr(PackageConstants.PackageXMLConstants.COMPONENT_TYPE, componentType.getValue());
			componentConfigXML.text(componentType.getValue() + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);
			LOGGER.info("####Sandbox - Completed Packaging ComponentType - " + componentType.name());
			if(sandboxId > -1L) {
				i = i + parts;
				progress = (int)i;
				SandboxAPI.sendSandboxProgress(progress, sandboxId, "Packaging done for component name--> "+ componentType.name());
			}
		}
		rootFolder.addFile(PackageConstants.PACKAGE_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN, new PackageFileContext(PackageConstants.PACKAGE_CONF_FILE_NAME, PackageConstants.XML_FILE_EXTN, packageConfigXML));

		long fileId = PackageFileUtil.saveAsZipFile(rootFolder);
		String downloadUrl = FacilioFactory.getFileStore().getDownloadUrl(fileId);
		context.put(PackageConstants.DOWNLOAD_URL, downloadUrl);
		context.put(PackageConstants.FILE_ID, fileId);
		String domainName = (String) context.getOrDefault(PackageConstants.SANDBOX_DOMAIN_NAME, null);
		if(domainName != null) {
			SandboxAPI.setRecentPackageId(domainName, fileId);
		}
		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		PackageUtil.clearPickListConf();
		return false;
	}

	@Override
	public void onError() throws Exception {
		PackageUtil.clearPickListConf();
	}

}
