package com.facilio.bundle.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.zeroturnaround.zip.ZipUtil;

import com.facilio.command.FacilioCommand;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.context.BundleFileContext;
import com.facilio.bundle.context.BundleFolderContext;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.chain.FacilioContext;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.log4j.Log4j;

@Log4j
public class PackAllBundleComponentsCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<BundleChangeSetContext> changeSet = BundleUtil.getAllChangeSet();
		
		if(changeSet != null) {
			
			BundleFolderContext rootFolder = new BundleFolderContext("Facilio_App_Bundle");
			
			rootFolder.addFolder(BundleConstants.COMPONENTS_FOLDER_NAME);
			
			BundleFileContext bundleFile = new BundleFileContext(BundleConstants.BUNDLE_FILE_NAME, BundleConstants.XML_FILE_EXTN, BundleConstants.BUNDLE_FILE_NAME, null);
			
			XMLBuilder bundleBuilder = bundleFile.getXmlContent();
			bundleBuilder = bundleBuilder.e(BundleConstants.VERSION).text("1.0.0").p();
			
			bundleBuilder = bundleBuilder.e(BundleConstants.COMPONENTS);
			
			Map<Integer, List<BundleChangeSetContext>> changeSetMap = changeSet.stream().collect(Collectors.groupingBy(BundleChangeSetContext::getComponentType));
			
			Queue<BundleComponentsEnum> componentsQueue = new LinkedList<BundleComponentsEnum>();
			
			componentsQueue.addAll(BundleComponentsEnum.getParentComponentList());
			
			while(!componentsQueue.isEmpty()) {
				
				BundleComponentsEnum component = componentsQueue.poll();
				
				BundleComponentInterface bundleComponent = component.getBundleComponentClassInstance();
				
				if(changeSetMap.containsKey(component.getValue())) {
					
					bundleBuilder = bundleBuilder.e(BundleConstants.COMPONENT).attr("name", component.getName());
					
					List<BundleChangeSetContext> componentChangeSet = changeSetMap.get(component.getValue());
					
					for(BundleChangeSetContext componentChange : componentChangeSet) {
						
						if(componentChange.getModeEnum() == BundleModeEnum.DUMMY) {
							continue;
						}
						
						FacilioContext newContext = new FacilioContext();
						newContext.put(BundleConstants.COMPONENT_ID, componentChange.getComponentId());
						newContext.put(BundleConstants.BUNDLE_CHANGE, componentChange);
						newContext.put(BundleConstants.COMPONENTS_FOLDER, rootFolder.getFolder(BundleConstants.COMPONENTS_FOLDER_NAME));
						
						String fileName = component.getName()+File.separatorChar+bundleComponent.getFileName(newContext)+".xml";
						
						bundleComponent.getFormatedObject(newContext);
						
						bundleBuilder = bundleBuilder.element(BundleConstants.VALUES).text(fileName).p();
					}
					
					bundleBuilder = bundleBuilder.p();
					
					ArrayList<BundleComponentsEnum> childList = BundleComponentsEnum.getParentChildMap().get(component);
					
					if(childList != null) {
						componentsQueue.addAll(childList);
					}
				}
			}
			
			rootFolder.addFile(BundleConstants.BUNDLE_FILE_NAME+"."+BundleConstants.XML_FILE_EXTN, bundleFile);
			
			long fileId = saveAsZipFile(rootFolder);
			
			String downloadUrl = FacilioFactory.getFileStore().getDownloadUrl(fileId);
			
			context.put(BundleConstants.DOWNLOAD_URL, downloadUrl);
		}
		return false;
	}

	private long saveAsZipFile(BundleFolderContext rootFolder) throws Exception {

		String rootPath = PackAllBundleComponentsCommand.class.getClassLoader().getResource("").getFile() + File.separator + "facilio-temp-files" + File.separator + AccountUtil.getCurrentOrg().getOrgId() + File.separator + "bundles"+File.separator + rootFolder.getName();

		File rootFile = new File(rootPath);
		if (!(rootFile.exists() && rootFile.isDirectory())) {
			rootFile.mkdirs();
		}
		
		rootFolder.setPath(rootPath);
		
		Queue<BundleFolderContext> foldersQueue = new LinkedList<BundleFolderContext>();
		
		foldersQueue.add(rootFolder);
		
		while(!foldersQueue.isEmpty()) {
			BundleFolderContext folder = foldersQueue.poll();
			
			if(!folder.getFolders().isEmpty()) {
				
				for(String folderName : folder.getFolders().keySet()) {
					
					String subFolderPath = folder.getPath() + File.separator + folderName;
					
					File subFolder = new File(subFolderPath);
					subFolder.mkdirs();
					
					BundleFolderContext subFolderContext = folder.getFolders().get(folderName);
					subFolderContext.setPath(subFolderPath);
					foldersQueue.add(subFolderContext);
				}
			}
			
			if(!folder.getFiles().isEmpty()) {
				
				for(String fileName : folder.getFiles().keySet()) {
					
					BundleFileContext fileContext = folder.getFiles().get(fileName);
					
					Path filepath = Path.of(folder.getPath() + File.separator + fileContext.getName() + "." + fileContext.getExtension());
					
					String content = fileContext.isXMLFile() ? fileContext.getXmlContent().getAsXMLString() : fileContext.getFileContent();  
			       
					Files.writeString(filepath, content);
				}
			}
		}
		
		File zipFile = new File(rootPath+".zip");
		
		ZipUtil.pack(rootFile, zipFile);
		
		FileStore fs = FacilioFactory.getFileStore();
		
		long fileId = fs.addFile(rootFolder.getName()+".zip", zipFile, "application/zip");
		
		System.out.println("path ------------ "+zipFile);
		
		return fileId;
	}

//	public boolean executeCommand1(Context context) throws Exception {
//		// TODO Auto-generated method stub
//		
//		ArrayList<BundleComponentsEnum> parents = BundleComponentsEnum.getParentComponentList();
//		
//		JSONObject finalBundledObject = new JSONObject();
//		for(BundleComponentsEnum parentComponent : parents) {
//			BundleComponentInterface bundleComponentObject = parentComponent.getBundleComponentClassInstance();
//			
//			JSONArray parentObjects = bundleComponentObject.getAllFormatedObject((FacilioContext) context);
//			
//			for(int i=0;i<parentObjects.size();i++) {
//				
//				JSONObject parentObject = (JSONObject) parentObjects.get(i);
//				
//				fillChildComponents(parentComponent, parentObject, (FacilioContext) context);
//			}
//			
//			finalBundledObject.put(parentComponent.getName(), parentObjects);
//		}
//		
//		LOGGER.info("finalBundledObject --- "+finalBundledObject);
//		context.put(BundleConstants.BUNDLED_COMPONENTS, finalBundledObject);
//		return false;
//	}
	
	
//	private void fillChildComponents(BundleComponentsEnum parentComponent,JSONObject parentObject,FacilioContext context) throws Exception {
//		
//		ArrayList<BundleComponentsEnum> childList = BundleComponentsEnum.getParentChildMap().get(parentComponent);
//		
//		if(!Collections.isEmpty(childList)) {
//			for(BundleComponentsEnum childComponent : childList) {
//				
//				BundleComponentInterface bundleComponentObject = childComponent.getBundleComponentClassInstance();
//				
//				context.put(BundleConstants.PARENT_COMPONENT_OBJECT, parentObject);
//				
//				JSONArray childObjects = bundleComponentObject.getAllFormatedObject(context);
//				if(childObjects != null) {
//					for(int i=0;i<childObjects.size();i++) {
//						
//						JSONObject childObject = (JSONObject) childObjects.get(i);
//						
//						fillChildComponents(childComponent, childObject, context);
//					}
//					
//					parentObject.put(childComponent.getName(), childObjects);
//				}
//			}
//		}
//	}

}
