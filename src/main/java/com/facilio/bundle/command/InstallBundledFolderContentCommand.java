package com.facilio.bundle.command;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.io.FilenameUtils;

import com.facilio.bundle.context.BundleFileContext;
import com.facilio.bundle.context.BundleFolderContext;
import com.facilio.bundle.context.InstalledBundleContext;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.xml.builder.XMLBuilder;

public class InstallBundledFolderContentCommand extends FacilioCommand {

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
		
		for(XMLBuilder component :components) {
			
			String componentName = component.getAttribute(BundleConstants.Components.NAME);
			
			BundleComponentsEnum bundleComponentEnum = BundleComponentsEnum.getAllBundleComponentsByName().get(componentName);
			
			BundleFolderContext parentFolder = componentsFolder.getFolder(componentName);
			
			List<XMLBuilder> allchangeSet = component.getElementList(BundleConstants.VALUES);
			
			for(XMLBuilder subComponent : allchangeSet) {
				
				Double componentVersion = Double.parseDouble(subComponent.getAttribute(BundleConstants.VERSION));
				
				if(toBeInstalledVersionList.contains(componentVersion)) {
					String subComponentPath = subComponent.getText();
					
					String fileName = FilenameUtils.getName(subComponentPath);
					
					BundleFileContext changeSetXMLFile = parentFolder.getFile(fileName);
					
					FacilioContext newContext = new FacilioContext();
					
					newContext.put(BundleConstants.INSTALLED_BUNDLE, installedBundle);
					newContext.put(BundleConstants.BUNDLED_XML_COMPONENT_FILE, changeSetXMLFile);
					newContext.put(BundleConstants.BUNDLE_FOLDER, parentFolder);
					
					bundleComponentEnum.getBundleComponentClassInstance().getInstallMode(newContext);
					bundleComponentEnum.getBundleComponentClassInstance().install(newContext);
				}
			}
		}
		
		
		//delete handling here
		
		return false;
	}

}
