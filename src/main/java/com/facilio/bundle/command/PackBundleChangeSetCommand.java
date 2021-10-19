package com.facilio.bundle.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
//import java.nio.file.Files;
//import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Priority;
import org.zeroturnaround.zip.ZipUtil;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.context.BundleFileContext;
import com.facilio.bundle.context.BundleFolderContext;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateTimeUtil;
import com.facilio.xml.builder.XMLBuilder;

import lombok.extern.log4j.Log4j;

@Log4j
public class PackBundleChangeSetCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<BundleChangeSetContext> changeSet = (List<BundleChangeSetContext>) context.get(BundleConstants.BUNDLE_CHANGE_SET_LIST);
		
		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		if(changeSet != null && !changeSet.isEmpty()) {
			
			BundleFolderContext rootFolder = new BundleFolderContext("Facilio_App_Bundle_"+DateTimeUtil.getFormattedTime(DateTimeUtil.getCurrenTime()));
			
			rootFolder.addFolder(BundleConstants.COMPONENTS_FOLDER_NAME);
			
			BundleFileContext bundleFile = new BundleFileContext(BundleConstants.BUNDLE_FILE_NAME, BundleConstants.XML_FILE_EXTN, BundleConstants.BUNDLE_FILE_NAME, null);
			
			XMLBuilder bundleXMLBuilder = bundleFile.getXmlContent();
			bundleXMLBuilder = bundleXMLBuilder.e(BundleConstants.VERSION).text(bundle.getVersion()+"").p();
			
			bundleXMLBuilder = bundleXMLBuilder.e(BundleConstants.GLOBAL_NAME).text(bundle.getBundleGlobalName()).p();
			
			bundleXMLBuilder = bundleXMLBuilder.e(BundleConstants.COMPONENTS);
			
			Map<Integer, List<BundleChangeSetContext>> changeSetMap = changeSet.stream().collect(Collectors.groupingBy(BundleChangeSetContext::getComponentType));
			
			Queue<BundleComponentsEnum> componentsQueue = new LinkedList<BundleComponentsEnum>();
			
			componentsQueue.addAll(BundleComponentsEnum.getParentComponentList());
			
			while(!componentsQueue.isEmpty()) {
				
				BundleComponentsEnum component = componentsQueue.poll();
				
				BundleComponentInterface bundleComponent = component.getBundleComponentClassInstance();
				
				if(changeSetMap.containsKey(component.getValue())) {
					
					bundleXMLBuilder = bundleXMLBuilder.e(BundleConstants.COMPONENT).attr("name", component.getName());
					
					List<BundleChangeSetContext> componentChangeSet = changeSetMap.get(component.getValue());
					
					for(BundleChangeSetContext componentChange : componentChangeSet) {
						
						FacilioContext newContext = new FacilioContext();
						newContext.put(BundleConstants.COMPONENT_ID, componentChange.getComponentId());
						newContext.put(BundleConstants.BUNDLE_CHANGE, componentChange);
						newContext.put(BundleConstants.COMPONENTS_FOLDER, rootFolder.getFolder(BundleConstants.COMPONENTS_FOLDER_NAME));
						newContext.put(BundleConstants.BUNDLE_XML_BUILDER, bundleXMLBuilder);
						
						bundleComponent.fillBundleXML(newContext);
						bundleComponent.getFormatedObject(newContext);
						
					}
					
					bundleXMLBuilder = bundleXMLBuilder.p();
				}
				
				ArrayList<BundleComponentsEnum> childList = BundleComponentsEnum.getParentChildMap().get(component);
				
				if(childList != null) {
					componentsQueue.addAll(childList);
				}
			}
			
			rootFolder.addFile(BundleConstants.BUNDLE_FILE_NAME+"."+BundleConstants.XML_FILE_EXTN, bundleFile);
			
			long fileId = saveAsZipFile(rootFolder);
			
			String downloadUrl = FacilioFactory.getFileStore().getDownloadUrl(fileId);
			
			context.put(BundleConstants.DOWNLOAD_URL, downloadUrl);
			context.put(FacilioConstants.ContextNames.FILE_ID, fileId);
			
			updateBundleWithFileId(bundle,fileId);
		}
		return false;
	}

	private void updateBundleWithFileId(BundleContext bundle, long fileId) throws Exception {
		
		
		bundle.setBundleFileId(fileId);
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getBundleModule().getTableName())
				.fields(FieldFactory.getBundleFields())
				.andCondition(CriteriaAPI.getIdCondition(bundle.getId(), ModuleFactory.getBundleModule()));
		
		update.update(FieldUtil.getAsProperties(bundle));
		
	}

	private long saveAsZipFile(BundleFolderContext rootFolder) throws Exception {

		String rootPath = PackBundleChangeSetCommand.class.getClassLoader().getResource("").getFile() + File.separator + "facilio-temp-files" + File.separator + AccountUtil.getCurrentOrg().getOrgId() + File.separator + "bundles"+File.separator + rootFolder.getName();

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
					
					String content = fileContext.isXMLFile() ? fileContext.getXmlContent().getAsXMLString() : fileContext.getFileContent();  
			       
					try(FileWriter fWriter = new FileWriter(folder.getPath() + File.separator + fileContext.getName() + "." + fileContext.getExtension())) {
			            fWriter.write(content);
			        }
			        catch (IOException e) {
			            LOGGER.log(Priority.ERROR, e.getMessage(), e);
			            throw e;
			        }
					
				}
			}
		}
		
		File zipFile = new File(rootPath+".zip");
		
		ZipUtil.pack(rootFile, zipFile);
		
		FileStore fs = FacilioFactory.getFileStore();
		
		long fileId = fs.addFile(rootFolder.getName()+".zip", zipFile, "application/zip");
		
		FileUtils.deleteDirectory(rootFile);
		zipFile.delete();
		
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
