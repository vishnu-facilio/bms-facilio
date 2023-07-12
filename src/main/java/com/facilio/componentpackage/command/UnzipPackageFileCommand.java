package com.facilio.componentpackage.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.time.DateTimeUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

@Log4j
public class UnzipPackageFileCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);
		File file = (File) context.get(PackageConstants.FILE);
		Long fileId = (Long) context.get(PackageConstants.FILE_ID);
		Long sourceOrgId = (Long) context.getOrDefault(PackageConstants.SOURCE_ORG_ID, -1L);

		File packageZipFile;
		if (file != null) {
			packageZipFile = file;
		} else {
			packageZipFile = PackageFileUtil.getPackageZipFile(fileId, sourceOrgId);
		}
		context.put(PackageConstants.FILE, packageZipFile);

		File outputDirectory = new File(UnzipPackageFileCommand.class.getClassLoader().getResource("").getFile()
				+ File.separator + "facilio-temp-files" + File.separator + AccountUtil.getCurrentOrg().getOrgId()
				+ File.separator + "packages" +File.separator + "Org_Package_" + "_" + DateTimeUtil.getFormattedTime(DateTimeUtil.getCurrenTime()));
		ZipUtil.unpack(packageZipFile, outputDirectory);
		PackageFolderContext rootFolder = convertUnzippedFileToPackageFolder(outputDirectory.getAbsolutePath());
		FileUtils.deleteDirectory(outputDirectory);

		context.put(PackageConstants.PACKAGE_ROOT_FOLDER, rootFolder);

		return false;
	}

	private static PackageFolderContext convertUnzippedFileToPackageFolder(String filePath) throws Exception {
		String folderName = FilenameUtils.getBaseName(filePath);
		PackageFolderContext rootFolder = new PackageFolderContext(folderName);
		rootFolder.setPath(filePath);

		Queue<PackageFolderContext> folderQueue = new LinkedList<>();
		folderQueue.add(rootFolder);

		while(!folderQueue.isEmpty()) {
			PackageFolderContext folderContent = folderQueue.poll();
			File currentFolder = new File(folderContent.getPath());
			for(File file : currentFolder.listFiles()) {
				if(file.isDirectory()) {
					PackageFolderContext subFolderContent = new PackageFolderContext(file.getName());
					subFolderContent.setPath(file.getAbsolutePath());
					folderContent.addFolder(file.getName(), subFolderContent);
					folderQueue.add(subFolderContent);
				}
				else {
					String fileNameWithExtn = file.getName();
					String fileName = FilenameUtils.getBaseName(fileNameWithExtn);
					String extension = FilenameUtils.getExtension(fileNameWithExtn);

					if(PackageConstants.ALLOWED_EXTN.contains(extension) && !fileName.startsWith(".")) {
						String content = PackageFileUtil.readFileContent(file.getAbsolutePath());
						PackageFileContext packageFile = new PackageFileContext(fileName, extension, content);
						folderContent.addFile(fileNameWithExtn, packageFile);
					}
				}
			}
		}

		return rootFolder;
	}
}
