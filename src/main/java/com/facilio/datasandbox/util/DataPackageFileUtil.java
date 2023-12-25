package com.facilio.datasandbox.util;

import com.facilio.bmsconsole.context.FileContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.sandboxfilestore.SandboxFileStore;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class DataPackageFileUtil {
    public static File getTempFolderRoot() {
        String rootPath = System.getProperties().getProperty("java.io.tmpdir") + File.separator + "sandbox" + File.separator + "tempFiles" + File.separator + PackageUtil.getRootFolderPath();

        File rootFile = new File(rootPath);
        if (!(rootFile.exists() && rootFile.isDirectory())) {
            rootFile.mkdirs();
        }
        return rootFile;
    }

    // create new csv file
    public static File createTempFileForModule(String moduleName) {
        File rootFile = getTempFolderRoot();
        String filePath = rootFile.getPath() + File.separator + moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN;
        return new File(filePath);
    }

    // get file from bucket and add to temp directory
    private static File addFileToTempFolder(String fileName, FileInfo fileInfo) throws Exception {
        File rootFile = getTempFolderRoot();
        String tempFilepath = rootFile.getPath() + File.separator + fileName;

        try (InputStream fileInputStream = FacilioFactory.getFileStoreFromOrg(fileInfo.getOrgId()).readFile(fileInfo.getFileId());
             FileOutputStream fileOutputStream = new FileOutputStream(tempFilepath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            LOGGER.info("Error while adding file to temp folder " + fileInfo.getFilePath() + e);
            throw e;
        }
        return new File(tempFilepath);
    }

    // get file from bucket and add to temp directory
    public static File addFileToTempFolder(String sourceFileName, String targetFileName) throws Exception {
        File rootFile = getTempFolderRoot();
        String tempFilepath = rootFile.getPath() + File.separator + targetFileName;

        try (InputStream fileInputStream = getModuleCSVStream(sourceFileName);
             FileOutputStream fileOutputStream = new FileOutputStream(tempFilepath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            LOGGER.info("Error while adding file to temp folder " + sourceFileName + e);
            throw e;
        }
        return new File(tempFilepath);
    }

    public static String addRootFolder(String rootFolderName, boolean addChildFolders) throws Exception {
        String rootPath = addFile(new File(rootFolderName), rootFolderName);

        if (addChildFolders) {
            String csvFolderName = rootFolderName + File.separator + PackageConstants.DATA_FOLDER_NAME;
            String attachmentFolderName = rootFolderName + File.separator + PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME;

            addFile(new File(csvFolderName), csvFolderName);
            addFile(new File(attachmentFolderName), attachmentFolderName);
        }

        return rootPath;
    }

    public static String addDataConfFile(String xmlContent) throws Exception {
        String dataConfFileNameWithExtn = PackageConstants.DATA_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN;

        String tempFilePath = getTempFolderRoot().getPath() + File.separator + dataConfFileNameWithExtn;
        try (FileWriter fWriter = new FileWriter(tempFilePath)) {
            fWriter.write(xmlContent);
        } catch (IOException e) {
            LOGGER.info("Error while adding file to temp folder " + dataConfFileNameWithExtn + e);
            throw e;
        }

        String filePath = addFile(new File(tempFilePath), PackageUtil.getRootFolderPath() + File.separator + dataConfFileNameWithExtn);
        FileUtils.delete(new File(tempFilePath));
        return filePath;
    }

    public static String addModuleCSVFile(String moduleName, File file) throws Exception {
        String dataFolderName = PackageConstants.DATA_FOLDER_NAME;
        String dataFolderAbsolutePath = PackageUtil.getRootFolderPath() + File.separator + dataFolderName;
        String moduleCSVFileName = moduleName + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.CSV_FILE_EXTN;

        String filePath = addFile(file, dataFolderAbsolutePath + File.separator + moduleCSVFileName);
        return filePath;
    }

    public static String addAttachmentFile(String attachmentFileName, File file) throws Exception {
        String attachmentFolderName = PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME;
        String attachmentFolderAbsolutePath = PackageUtil.getRootFolderPath() + File.separator + attachmentFolderName;

        String filePath = addFile(file, attachmentFolderAbsolutePath + File.separator + attachmentFileName);
        return filePath;
    }

    public static String addFile(File file, String fileNameWithExtn) throws Exception {
        SandboxFileStore sandboxFileStore = FacilioFactory.getSandboxFileStore();
        String fileURL = sandboxFileStore.addFileAndGetURL(file, fileNameWithExtn);
        return fileURL;
    }

    public static String getFileUrl(String fileName) {
        SandboxFileStore sandboxFileStore = FacilioFactory.getSandboxFileStore();
        return sandboxFileStore.getRootPath(SandboxFileStore.DEFAULT_NAMESPACE) + File.separator + fileName;
    }

    public static boolean isValidDirectory(String fileURL) throws Exception {
        InputStream inputStream = getFileStreamFromPackage(fileURL);
        return inputStream != null;
    }

    public static InputStream getDataConfigStreamFromPackage() throws Exception {
        String dataConfFileNameWithExtn = PackageConstants.DATA_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN;
        if (StringUtils.isNotEmpty(PackageUtil.getRootFolderPath())) {
            InputStream inputStream = getFileStreamFromPackage(PackageUtil.getRootFolderPath() + File.separator + dataConfFileNameWithExtn);
            return inputStream;
        }
        return null;
    }

    public static InputStream getModuleCSVStream(String moduleFileName) throws Exception {
        String dataFolderPath = PackageConstants.DATA_FOLDER_NAME;
        if (StringUtils.isNotEmpty(PackageUtil.getRootFolderPath())) {
            String dataFolderAbsolutePath = PackageUtil.getRootFolderPath() + File.separator + dataFolderPath;
//            if (isValidDirectory(dataFolderAbsolutePath)) {
                InputStream inputStream = getFileStreamFromPackage(dataFolderAbsolutePath + File.separator + moduleFileName);
                return inputStream;
//            }
        }
        return null;
    }

    public static String getAttachmentFilePath(String attachmentFileName) throws Exception {
        String attachmentFolderName = PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME;
        if (StringUtils.isNotEmpty(PackageUtil.getRootFolderPath())) {
            String attachmentFolderAbsolutePath = PackageUtil.getRootFolderPath() + File.separator + attachmentFolderName;
//            if (isValidDirectory(attachmentFolderAbsolutePath)) {
                return attachmentFolderAbsolutePath + File.separator + attachmentFileName;
//            }
        }
        return null;
    }

    private static SandboxFileStore initializeSandboxFileStore() throws Exception {
        SandboxFileStore fs = null;
        String sandboxBucketName = PackageUtil.getSandboxBucketName();
        if (StringUtils.isEmpty(sandboxBucketName)) {
            fs = FacilioFactory.getSandboxFileStore();
        } else {
            LOGGER.info("####FileStore - Setting Bucket Name - " + sandboxBucketName);
            fs = FacilioFactory.getSandboxFileStore(sandboxBucketName);
        }
        return fs;
    }

    private static InputStream getFileStreamFromPackage(String fileURL) throws Exception {
        SandboxFileStore fs = initializeSandboxFileStore();
        InputStream inputStream = fs.readFile(fileURL);
        return inputStream;
    }

    public static List<String> getDataConfigModuleNames() throws Exception {
        List<String> moduleNames = new ArrayList<>();
        try (InputStream dataConfigStream = getDataConfigStreamFromPackage()) {
            if (dataConfigStream != null) {
                XMLBuilder dataConfigXml = XMLBuilder.parse(dataConfigStream);
                XMLBuilder moduleNamesXml = dataConfigXml.getElement(PackageConstants.MODULES);
                List<XMLBuilder> allModulesXml = moduleNamesXml.getFirstLevelElementListForTagName(PackageConstants.MODULE);
                moduleNames = CollectionUtils.isNotEmpty(allModulesXml) ?
                        allModulesXml.stream().map(xml -> xml.getAttribute(PackageConstants.NAME)).collect(Collectors.toList()) : new ArrayList<>();
            }
        }
        return moduleNames;
    }

    public static Map<String, String> getModuleNameVsXmlFileName() throws Exception {
        Map<String, String> moduleNameVsXmlFileName = new HashMap<>();
        try (InputStream dataConfigStream = getDataConfigStreamFromPackage()) {
            if (dataConfigStream != null) {
                XMLBuilder dataConfigXml = XMLBuilder.parse(dataConfigStream);
                XMLBuilder moduleNamesXml = dataConfigXml.getElement(PackageConstants.MODULES);
                List<XMLBuilder> allModulesXml = moduleNamesXml.getFirstLevelElementListForTagName(PackageConstants.MODULE);
                moduleNameVsXmlFileName = CollectionUtils.isEmpty(allModulesXml) ? moduleNameVsXmlFileName :
                        allModulesXml.stream().collect(Collectors.toMap(xmlBuilder -> xmlBuilder.getAttribute(PackageConstants.NAME), XMLBuilder::getText, (a, b) -> b));
            }
        }
        return moduleNameVsXmlFileName;
    }

    public static Map<String, String> exportFileFieldData(long recordId, long fileId, FileInfo fileInfo) throws Exception {
        if (fileId > 0 && fileInfo == null) {
            FileStore fs = FacilioFactory.getFileStore();
            fileInfo = fs.getFileInfo(fileId);
        }

        if (fileInfo == null) {
            return null;
        }

        Map<String, String> fileDataProp = getFileProps(recordId, fileInfo);
        String uniqueIdentifier = fileDataProp.get("uniqueIdentifier");

        File tempFile = addFileToTempFolder(uniqueIdentifier, fileInfo);
        addAttachmentFile(uniqueIdentifier, tempFile);
        FileUtils.delete(tempFile);

        return fileDataProp;
    }

    private static Map<String, String> getFileProps(long recordId, FileInfo fileInfo) throws Exception {
        String fileNameWithExtension = fileInfo.getFileName();
        String extension = FilenameUtils.getExtension(fileNameWithExtension);
        String uniqueIdentifierForFile = PackageFileUtil.getUniqueIdentifierForFile(String.valueOf(recordId), fileInfo);
        String value = uniqueIdentifierForFile + FacilioConstants.ContextNames.ATTACHMENT + (StringUtils.isEmpty(extension) ? "" : "." + extension);

        Map<String, String> fileDataProp = new HashMap<>();
        fileDataProp.put("uniqueIdentifier", value);
        fileDataProp.put("fileName", URLEncoder.encode(fileNameWithExtension, "UTF-8"));
        fileDataProp.put("fileId", String.valueOf(fileInfo.getFileId()));
        fileDataProp.put("contentType", fileInfo.getContentType());

        return fileDataProp;
    }

    public static long getNewFileId(Map<String, Object> fileProp) throws Exception {
        String fileName = (String) fileProp.get("fileName");
        String contentType = (String) fileProp.get("contentType");
        String uniqueIdentifier = (String) fileProp.get("uniqueIdentifier");
        String attachmentFilePath = getAttachmentFilePath(uniqueIdentifier);

        if (StringUtils.isNotEmpty(attachmentFilePath)) {
            File sourceOrgFile = new File(attachmentFilePath);
            String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
            File file = new File(sourceOrgFile.getParentFile(), decodedFileName);
            sourceOrgFile.renameTo(file);

            FileContext fileContext = PackageFileUtil.addFileToStore(file, contentType);
            return fileContext.getFileId();
        }
        return -1;
    }
}
