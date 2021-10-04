package com.facilio.bundle.command;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.zeroturnaround.zip.ZipUtil;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bundle.context.BundleFileContext;
import com.facilio.bundle.context.BundleFolderContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import lombok.extern.java.Log;

@Log
public class ParseZIPToFolderContentCommand extends FacilioCommand {
	
	 

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		File bundleZip =  (File) context.get(BundleConstants.BUNDLE_ZIP_FILE);
		String bundleZipName =  (String) context.get(BundleConstants.BUNDLE_ZIP_FILE_NAME);
		
		FileStore fs = FacilioFactory.getFileStore();
		
		long fileId = fs.addFile(bundleZipName, bundleZip, BundleConstants.BUNDLE_ZIP_FILE_CONTENT_TYPE);
		
		File outputDirectory = new File(ParseZIPToFolderContentCommand.class.getClassLoader().getResource("").getFile() + File.separator + "facilio-temp-files" + File.separator + AccountUtil.getCurrentOrg().getOrgId() + File.separator + "bundles" +File.separator + bundleZipName);
		
		ZipUtil.unpack(bundleZip, outputDirectory);
		
		BundleFolderContext rootFolder = parseFolderToBundleFolder(outputDirectory.getAbsolutePath());
		
		System.out.println("pathh1 -- " +outputDirectory.getAbsolutePath());
		
		FileUtils.deleteDirectory(outputDirectory);
		
		context.put(BundleConstants.BUNDLE_FOLDER, rootFolder);
		context.put(FacilioConstants.ContextNames.FILE_ID, fileId);
		
		return false;
	}

	private BundleFolderContext parseFolderToBundleFolder(String outputDirectoryPath) throws Exception {
		// TODO Auto-generated method stub
		
		String folderName = FilenameUtils.getBaseName(outputDirectoryPath);
		
		BundleFolderContext rootFolder = new BundleFolderContext(folderName);
		rootFolder.setPath(outputDirectoryPath);
		
		Queue<BundleFolderContext> folderQueue = new LinkedList<BundleFolderContext>();
		
		folderQueue.add(rootFolder);		
		
		while(!folderQueue.isEmpty()) {
			
			BundleFolderContext folderContent = folderQueue.poll();
			
			File currentFolder = new File(folderContent.getPath());
			
			for(File file : currentFolder.listFiles()) {
				
				if(file.isDirectory()) {
					
					BundleFolderContext subFolderContent = new BundleFolderContext(file.getName());
					subFolderContent.setPath(file.getAbsolutePath());
					
					folderContent.addFolder(file.getName(), subFolderContent);
					
					folderQueue.add(subFolderContent);
				}
				else {
					String fileNameWithExtn = file.getName();
					
					String fileName = FilenameUtils.getBaseName(fileNameWithExtn);
					String extn = FilenameUtils.getExtension(fileNameWithExtn);
					
					if(BundleConstants.ALLOWED_EXTN.contains(extn) && !fileName.startsWith(".")) {
						String content = BundleUtil.readFileContent(file.getAbsolutePath());
					
						BundleFileContext bundleFile = new BundleFileContext(fileName, extn, content);
					
						folderContent.addFile(fileNameWithExtn, bundleFile);
					}
				}
			}
		}
		
		return rootFolder;
	}

	
}
