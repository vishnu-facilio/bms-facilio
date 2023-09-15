package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.FileContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.bean.OrgSwitchBean;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.xml.builder.XMLBuilder;
import com.google.common.io.Files;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Priority;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;

@Log4j
public class PackageFileUtil {

    public static long saveAsZipFile(PackageFolderContext rootFolder) throws Exception {

        String rootPath = System.getProperties().getProperty("java.io.tmpdir") + File.separator + "sandbox"+ File.separator + "Unzipped-Package-Files" + File.separator + rootFolder.getName();

        File rootFile = new File(rootPath);
        if (!(rootFile.exists() && rootFile.isDirectory())) {
            rootFile.mkdirs();
        }

        rootFolder.setPath(rootPath);

        Queue<PackageFolderContext> foldersQueue = new LinkedList<PackageFolderContext>();
        foldersQueue.add(rootFolder);

        while(!foldersQueue.isEmpty()) {
            PackageFolderContext folder = foldersQueue.poll();
            if(!folder.getFolders().isEmpty()) {
                for(String folderName : folder.getFolders().keySet()) {
                    String subFolderPath = folder.getPath() + File.separator + folderName;

                    File subFolder = new File(subFolderPath);
                    subFolder.mkdirs();

                    PackageFolderContext subFolderContext = folder.getFolders().get(folderName);
                    subFolderContext.setPath(subFolderPath);
                    foldersQueue.add(subFolderContext);
                }
            }

            if(!folder.getFiles().isEmpty() && !folder.getName().endsWith(PackageConstants.META_FILES_FOLDER_NAME)) {
                for(String fileName : folder.getFiles().keySet()) {
                    PackageFileContext fileContext = folder.getFiles().get(fileName);
                    String content = fileContext.getXmlContent().getAsXMLString();
                    try(FileWriter fWriter = new FileWriter(folder.getPath() + File.separator + fileContext.getName() + "." + fileContext.getExtension())) {
                        fWriter.write(content);
                    }
                    catch (IOException e) {
                        LOGGER.log(Priority.ERROR, e.getMessage(), e);
                        throw e;
                    }
                }
            }
            if (!folder.getFilesInfo().isEmpty() && folder.getName().endsWith(PackageConstants.META_FILES_FOLDER_NAME)) {
                for (String fileName : folder.getFilesInfo().keySet()) {
                    FileInfo fileInfo = folder.getFilesInfo().get(fileName);

                    try (InputStream fileInputStream = FacilioFactory.getFileStoreFromOrg(fileInfo.getOrgId()).readFile(fileInfo.getFileId());
                         FileOutputStream fileOutputStream = new FileOutputStream(folder.getPath() + File.separator + fileName)) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;

                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }

                    } catch (IOException e) {
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
    public static File getPackageZipFile(Long fileId, Long sourceOrgId) throws Exception {
        OrgSwitchBean orgSwitchBean = (OrgSwitchBean) BeanFactory.lookup("OrgSwitchBean", sourceOrgId);
        try (InputStream inputStream = NewTransactionService.newTransactionWithReturn(() -> orgSwitchBean.getParentOrgFile(sourceOrgId, fileId))){
            String dirPath = System.getProperties().getProperty("java.io.tmpdir") + File.separator + "sandbox"+ File.separator + "Unzipped-Package-Files";
            String path = dirPath + File.separator +fileId+".zip";
            File file = new File(path);
            File dir = new File(dirPath);
            boolean directoryExits = (dir.exists() && dir.isDirectory());
            if (!directoryExits) {
                dir.mkdirs();
            }
            file.createNewFile();
            try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
                int read;
                byte[] bytes = new byte[8192];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
            return file;
        }
    }
    public static void accountSwitch(long orgId) throws Exception {
        Organization org = IAMOrgUtil.getOrg(orgId);
        User superAdminUser = AccountUtil.getOrgBean().getSuperAdmin(orgId);
        Account account = new Account(org, superAdminUser);
        AccountUtil.setCurrentAccount(account);
    }
    // Give ComponentType, FileInfo and ComponentId for Packaging File Attachment
    public static XMLBuilder constructMetaAttachmentXMLBuilder(ComponentType componentType, long componentId, XMLBuilder element, FileInfo fileInfo) throws Exception {
        if(componentId < -1L || fileInfo == null ){
            return element;
        }
        String fileNameWithExtn = fileInfo.getFileName();
        String extension = FilenameUtils.getExtension(fileNameWithExtn);
        if (!StringUtils.isEmpty(fileInfo.getFileName())) {
            element.element(PackageConstants.FileConstants.FILE_NAME).text(String.valueOf(fileInfo.getFileName()));
        }

        element.element(PackageConstants.FileConstants.FILE_PATH).text(PackageConstants.META_FILES_FOLDER_NAME);

        if (StringUtils.isNotEmpty(fileInfo.getContentType())) {
            element.element(PackageConstants.FileConstants.CONTENT_TYPE).text(fileInfo.getContentType());
        }
        String uniqueIdentifierForFile = PackageFileUtil.getUniqueIdentifierForFile(String.valueOf(componentId),fileInfo)+ (StringUtils.isEmpty(extension) ? "" : "."+extension);
        if (StringUtils.isNotEmpty(uniqueIdentifierForFile)) {
            element.element(PackageConstants.FileConstants.UNIQUE_FILE_IDENTIFIER).text(uniqueIdentifierForFile);
        }
        PackageUtil.addComponentFileForContext(componentType.name(), uniqueIdentifierForFile, fileInfo);
        return element;
    }
    // Give XMLBuilder for adding File Attachment to file-store
    public static FileContext addMetaFileAndGetContext(XMLBuilder attachmentElement) throws Exception {
        String rootPath = PackageUtil.getRootFolderPath();
        FileContext fileContext = null;
        if (attachmentElement != null) {
            String fileName = attachmentElement.getElement(PackageConstants.FileConstants.FILE_NAME).getText();
            String filePath = attachmentElement.getElement(PackageConstants.FileConstants.FILE_PATH).getText();
            String contentType = attachmentElement.getElement(PackageConstants.FileConstants.CONTENT_TYPE).getText();
            String uniqueFileIdentifier = attachmentElement.getElement(PackageConstants.FileConstants.UNIQUE_FILE_IDENTIFIER).getText();
            String absoluteFilePath = rootPath + File.separator + filePath + File.separator + uniqueFileIdentifier;
            File sourceOrgFile = new File(absoluteFilePath);
            File file = new File(sourceOrgFile.getParentFile(), fileName);
            sourceOrgFile.renameTo(file);
            fileContext = PackageFileUtil.addFileToStore(file, contentType);
        }
        return fileContext;
    }
    public static FileContext addFileToStore(File file, String contentType) throws Exception {
        if (contentType == null) {
            String contentTypeByFilename = URLConnection.guessContentTypeFromName(file.getName());
            contentType = contentTypeByFilename == null ? "application/octet-stream" : contentTypeByFilename;
        }
        FacilioContext context = new FacilioContext();

        context.put(FacilioConstants.ContextNames.FILE, file);
        context.put(FacilioConstants.ContextNames.FILE_NAME, file.getName());
        context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, contentType);

        FacilioChain addFileChain = FacilioChainFactory.getAddFileChain();
        addFileChain.execute(context);

        FileContext fileContext = (FileContext) context.get(FacilioConstants.ContextNames.FILE_CONTEXT_LIST);

        return fileContext;
    }

    public static String readFileContent(String filePath) throws IOException {
        String text = Files.asCharSource(new File(filePath), Charset.defaultCharset()).read();
        return text;
    }
    public static String getUniqueIdentifierForFile(String componentId, FileInfo fileInfo) throws Exception {
        try {
            String input = componentId + fileInfo.getFileName() + fileInfo.getFileId();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes).replaceAll("[.<>:\"/\\\\|?* ]", "");
        } catch (Exception e) {
            throw new Exception("Error generating uniqueFileIdentifier", e);
        }
    }
}
