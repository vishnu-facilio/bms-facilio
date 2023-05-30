package com.facilio.componentpackage.command;

import com.facilio.bundle.utils.BundleConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class CreateXMLPackageCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);

		PackageFolderContext rootFolder = new PackageFolderContext("Org_Package_" + packageContext.getUniqueName() + "_" + DateTimeUtil.getFormattedTime(DateTimeUtil.getCurrenTime()));
		PackageFolderContext componentsFolder = rootFolder.addFolder(PackageConstants.COMPONENTS_FOLDER_NAME);

		XMLBuilder packageConfigXML = XMLBuilder.create(PackageConstants.PackageXMLConstants.PACKAGE);
		packageConfigXML.element(PackageConstants.NAME).text(packageContext.getUniqueName());
		packageConfigXML.element(PackageConstants.VERSION).text(String.valueOf(packageContext.getVersion()));
		packageConfigXML.element(PackageConstants.TYPE).text(String.valueOf(packageContext.getType()));
		XMLBuilder componentsConfigXML = packageConfigXML.element(PackageConstants.PackageXMLConstants.COMPONENTS);

		Map<ComponentType, List<PackageChangeSetMappingContext>> typeVsComponents = PackageUtil.getAllPackageChangsets(packageContext.getId());
		for(Map.Entry<ComponentType, List<PackageChangeSetMappingContext>> typeVsComponent : typeVsComponents.entrySet()) {
			ComponentType componentType = typeVsComponent.getKey();
			if(componentType.getComponentClass() == null) {
				continue;
			}
			PackageBean componentImplClass = componentType.getPackageComponentClassInstance();
			XMLBuilder componentsXML = XMLBuilder.create(PackageConstants.PackageXMLConstants.COMPONENTS);
			componentsXML.attr(PackageConstants.PackageXMLConstants.COMPONENT_TYPE, componentType.getValue());

			List<Long> componentIds = typeVsComponent.getValue().stream().map(component -> component.getComponentId()).collect(Collectors.toList());
			Map idVsComponent = componentImplClass.fetchComponents(componentIds);

			for(PackageChangeSetMappingContext componentMapping : typeVsComponent.getValue()) {
				XMLBuilder componentXML = componentsXML.element(componentType.getValue());
				componentXML.attr(PackageConstants.PackageXMLConstants.UNIQUE_IDENTIFIER, componentMapping.getUniqueIdentifier());
				componentXML.attr(PackageConstants.PackageXMLConstants.COMPONENT_TYPE,  String.valueOf(componentType.getIndex()));
				componentXML.attr(PackageConstants.PackageXMLConstants.COMPONENT_STATUS, componentMapping.getStatusEnum().getValue());
				componentXML.attr(PackageConstants.PackageXMLConstants.CREATED_VERSION, String.valueOf(componentMapping.getCreatedVersion()));
				componentXML.attr(PackageConstants.PackageXMLConstants.MODIFIED_VERSION, (componentMapping.getModifiedVersion() != null) ? String.valueOf(componentMapping.getModifiedVersion()) : "0.0");
				componentImplClass.convertToXMLComponent(idVsComponent.get(componentMapping.getComponentId()), componentXML);
			}
			componentsFolder.addFile(componentType.getValue(), new PackageFileContext(componentType.getValue(), PackageConstants.XML_FILE_EXTN, componentsXML));
			XMLBuilder componentConfigXML = componentsConfigXML.element(PackageConstants.PackageXMLConstants.COMPONENT);
			componentConfigXML.attr(PackageConstants.PackageXMLConstants.COMPONENT_TYPE, componentType.getValue());
			componentConfigXML.text(PackageConstants.PackageXMLConstants.COMPONENTS + PackageConstants.FILE_PATH_SEPARATOR + componentType.getValue() + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);
		}
		rootFolder.addFile(PackageConstants.PACKAGE_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN, new PackageFileContext(PackageConstants.PACKAGE_CONF_FILE_NAME, PackageConstants.XML_FILE_EXTN, packageConfigXML));

		long fileId = PackageFileUtil.saveAsZipFile(rootFolder);
		String downloadUrl = FacilioFactory.getFileStore().getDownloadUrl(fileId);
		context.put(BundleConstants.DOWNLOAD_URL, downloadUrl);
		context.put(FacilioConstants.ContextNames.FILE_ID, fileId);

		return false;
	}

}
