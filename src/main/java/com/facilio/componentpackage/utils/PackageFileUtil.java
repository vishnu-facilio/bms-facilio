package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.bean.OrgSwitchBean;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fs.FileInfo;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.*;
import com.facilio.sandbox.utils.SandboxUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.xml.builder.XMLBuilder;
import com.google.common.io.Files;
import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Priority;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

            if(!folder.getFiles().isEmpty() &&  !FILE_SUPPORTED_FOLDER_NAMES.contains(folder.getName())) {
                for(String fileName : folder.getFiles().keySet()) {
                    PackageFileContext fileContext = folder.getFiles().get(fileName);
                    String content = null;
                    if (Objects.equals(fileContext.getExtension(), "xml")) {
                        content = fileContext.getXmlContent().getAsXMLString();
                    } else if (Objects.equals(fileContext.getExtension(), "csv")) {
                        LOGGER.info("Zipped csv file name : " + fileContext.getName());
                        File fileContent = fileContext.getCsvContent();
                        String csvFilePath = fileContent.getPath();
                        StringBuilder csvContent = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            csvContent.append(line).append("\n"); // Append each line with a newline character
                        }
                        reader.close();
                        content = csvContent.toString();
                    }
                    try (FileWriter fWriter = new FileWriter(folder.getPath() + File.separator + fileContext.getName() + "." + fileContext.getExtension())) {
                        fWriter.write(content);
                    } catch (IOException e) {
                        LOGGER.log(Priority.ERROR, e.getMessage(), e);
                        throw e;
                    }
                }
            }
            if (!folder.getFilesInfo().isEmpty() && getFileSupportedFolderNames().contains(folder.getName())) {
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
        OrgSwitchBean orgSwitchBean = SandboxUtil.getOrgSwitchBean(sourceOrgId);
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

    public static String escapeCsv(String value) {
        if (value != null) {
            value = value.trim();
            try {
                StringBuilder sb = new StringBuilder(value);
                if (sb.charAt(0) == '=' || sb.charAt(0) == '@' || sb.charAt(0) == '+' || sb.charAt(0) == '-') {
                    sb.insert(0, '\\');
                }
                value = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            value = StringEscapeUtils.escapeCsv(value);
        }
        return value;
    }

    public static void updateCsvFile(FacilioModule module, File moduleCsvFile, List<Map<String, Object>> props, List<FacilioField> targetFields, Map<String, List<Long>> fetchedRecords, Map<String, Map<String, Object>> numberLookupDetails, Map<String, List<Long>> toBeFetchRecords,List<String> allMigrationModuleNames,boolean isRestrictDependantModules) throws Exception {

        if (CollectionUtils.isEmpty(targetFields) || CollectionUtils.isEmpty(props)) {
            return;
        }
        String filePath = moduleCsvFile.getPath();
        try (FileWriter writer = new FileWriter(filePath, true)) {
            StringBuilder str = new StringBuilder();
            addRowData(props, str, targetFields, fetchedRecords, toBeFetchRecords, numberLookupDetails, writer,allMigrationModuleNames,isRestrictDependantModules);
        }
    }

    private static File getFileWithPath(PackageFolderContext rootFolder, String moduleName) {

        String rootPath = System.getProperties().getProperty("java.io.tmpdir") + File.separator + "sandbox" + File.separator + "Unzipped-Package-Files" + File.separator + rootFolder.getName();

        File rootFile = new File(rootPath);

        if (!(rootFile.exists() && rootFile.isDirectory())) {
            rootFile.mkdirs();
        }
        String filePath = rootPath + File.separator + moduleName;

        File csvFile = new File(filePath);
        return csvFile;
    }

    public static File exportDataAsCSVFile(FacilioModule module, List<FacilioField> fields, List<Map<String, Object>> records, PackageFolderContext rootFolder, Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookupDetails, Map<String, List<Long>> fetchedRecords,List<String> allMigrationModuleNames,boolean isRestrictDependantModules) throws Exception {

        if (CollectionUtils.isEmpty(fields) || CollectionUtils.isEmpty(records)) {
            return null;
        }
        Map<String, FacilioField> fieldsNameVsFacilioFieldMap = fields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity(), (a, b) -> b));

        File csvFile = PackageFileUtil.getFileWithPath(rootFolder, module.getName());

        String filePath = csvFile.getAbsolutePath();

        try (FileWriter writer = new FileWriter(filePath, false)) {
            StringBuilder str = new StringBuilder();
            FacilioField idField = FieldFactory.getIdField(module);
            if ((!fieldsNameVsFacilioFieldMap.keySet().contains("siteId"))) {
                FacilioField siteIdField = FieldFactory.getSiteIdField(module);
                fields.add(0, siteIdField);
            }
            if (!fieldsNameVsFacilioFieldMap.keySet().contains("formId")) {
                FacilioField formIdField = FieldFactory.getNumberField("formId",null,module);
                fields.add(0, formIdField);
            }
            if (!fieldsNameVsFacilioFieldMap.keySet().contains(FacilioConstants.ContextNames.STATE_FLOW_ID)) {
                FacilioField formIdField = FieldFactory.getNumberField(FacilioConstants.ContextNames.STATE_FLOW_ID,null,module);
                fields.add(0, formIdField);
            }
            fields.add(0, idField);
            for (FacilioField field : fields) {
                String sheetColumnName = field.getName();
                str.append(PackageFileUtil.escapeCsv(sheetColumnName));
                str.append(',');
            }
            writer.append(StringUtils.stripEnd(str.toString(), ","));
            writer.append('\n');
            addRowData(records, str, fields, fetchedRecords, toBeFetchRecords, numberLookupDetails, writer,allMigrationModuleNames,isRestrictDependantModules);

            return csvFile;
        }
    }

    private static void handleTobeAndFetchedRecords(String lookupModuleName,FacilioModule.ModuleType moduleType ,Map<String, List<Long>> fetchedRecords,long longValue,Map<String, List<Long>> toBeFetchRecords,List<Long> values,List<String> allMigrationModuleNames){

        if (moduleType != FacilioModule.ModuleType.PICK_LIST && !Objects.equals(lookupModuleName, FacilioConstants.ContextNames.USERS) && allMigrationModuleNames.contains(lookupModuleName)) {
            if(fetchedRecords.containsKey(lookupModuleName)){
                List<Long> fetchedRecordIds = fetchedRecords.get(lookupModuleName);
                if(!fetchedRecordIds.contains(longValue)){
                    if (toBeFetchRecords.containsKey(lookupModuleName)) {
                        values = toBeFetchRecords.get(lookupModuleName);
                        if (!values.contains(longValue)) {
                            values.add(longValue);
                        }
                        toBeFetchRecords.put(lookupModuleName, values);
                    } else {
                        values.add(longValue);
                        toBeFetchRecords.put(lookupModuleName, values);
                    }
                }
            }else {
                if (toBeFetchRecords.containsKey(lookupModuleName)) {
                    values = toBeFetchRecords.get(lookupModuleName);
                    if (!values.contains(longValue)) {
                        values.add(longValue);
                    }
                    toBeFetchRecords.put(lookupModuleName, values);
                } else {
                    values.add(longValue);
                    toBeFetchRecords.put(lookupModuleName, values);
                }
            }
        }

    }

    private static void addRowData(List<Map<String, Object>> records, StringBuilder str, List<FacilioField> fields, Map<String, List<Long>> fetchedRecords, Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookupDetails, FileWriter writer,List<String> allMigrationModuleNames,boolean isRestrictDependantModules) throws IOException {

        for (Map<String, Object> record : records) {
            str = new StringBuilder();
            for (FacilioField field : fields) {
                String fieldName = field.getName();
                Object value = record.get(field.getName());
                String lookupModuleName = null;
                FacilioModule lookupModule = null;
                if (value != null) {
                    if (field instanceof MultiLookupField) {

                        StringJoiner sj = new StringJoiner(",");
                        lookupModule = ((BaseLookupField) field).getLookupModule();
                        lookupModuleName = lookupModule.getName();
                        FacilioModule.ModuleType moduleType = lookupModule.getTypeEnum();
                        List<Map<String, Object>> multiLookupValues = (List<Map<String, Object>>) value;

                        if(Objects.equals(field.getModule().getName(), FacilioConstants.ContextNames.SITE) && Objects.equals(lookupModuleName, FacilioConstants.ContextNames.LOCATION) && !allMigrationModuleNames.contains(FacilioConstants.ContextNames.LOCATION)){
                            allMigrationModuleNames.add(FacilioConstants.ContextNames.LOCATION);
                        }

                        for (Map<String, Object> multiLookupValue : multiLookupValues) {

                            List<Long> values = new ArrayList<>();
                            value = multiLookupValue.get("id");
                            sj.add(value.toString());

                            long longValue = ((Number) value).longValue();

                            if(!isRestrictDependantModules) {
                                handleTobeAndFetchedRecords(lookupModuleName, moduleType, fetchedRecords, longValue, toBeFetchRecords, values, allMigrationModuleNames);
                            }
                        }
                        value = sj;
                    } else if (field instanceof LookupField) {

                        lookupModule = ((BaseLookupField) field).getLookupModule();
                        lookupModuleName = lookupModule.getName();
                        FacilioModule.ModuleType moduleType = lookupModule.getTypeEnum();

                        if(Objects.equals(field.getModule().getName(), FacilioConstants.ContextNames.SITE) && Objects.equals(lookupModuleName, FacilioConstants.ContextNames.LOCATION) && !allMigrationModuleNames.contains(FacilioConstants.ContextNames.LOCATION)){
                            allMigrationModuleNames.add(FacilioConstants.ContextNames.LOCATION);
                            moduleType = FacilioModule.ModuleType.BASE_ENTITY;
                        }

                        Map<String, Object> fieldValue = (Map<String, Object>) value;
                        List<Long> values = new ArrayList<>();
                        value = fieldValue.get("id");

                        long longValue = ((Number) value).longValue();

                        if(!isRestrictDependantModules) {
                            handleTobeAndFetchedRecords(lookupModuleName, moduleType, fetchedRecords, longValue, toBeFetchRecords, values, allMigrationModuleNames);
                        }
                    } else if (field instanceof MultiEnumField) {
                        StringJoiner sj = new StringJoiner(",");
                        List<Integer> multiEnumValues = (List<Integer>) value;
                        for (Integer multiEnumValue : multiEnumValues) {
                            sj.add(multiEnumValue.toString());
                        }
                        value = sj;
                    } else if (field instanceof NumberField && Objects.equals(field.getName(), "siteId")) {

                        lookupModuleName = FacilioConstants.ContextNames.SITE;
                        long longValue = ((Number) value).longValue();
                        List<Long> values = new ArrayList<>();
                        if(!isRestrictDependantModules) {
                            handleTobeAndFetchedRecords(lookupModuleName, FacilioModule.ModuleType.BASE_ENTITY, fetchedRecords, longValue, toBeFetchRecords, values, allMigrationModuleNames);
                        }

                    } else if (field instanceof UrlField) {
                        StringJoiner sj = new StringJoiner(",");
                        Map<String, Object> urlValue = (Map<String, Object>) value;
                        sj.add(urlValue.get("href").toString());
                        sj.add(urlValue.get("target").toString());
                        if (urlValue.get("name") != null && StringUtils.isNotEmpty(urlValue.get("target").toString())) {
                            sj.add(urlValue.get("name").toString());
                        }
                        value = sj;
                    } else if (numberLookupDetails.containsKey(fieldName)) {
                        Map<String, Object> fieldLookupModuleDetails = numberLookupDetails.get(fieldName);
                        lookupModule = (FacilioModule) fieldLookupModuleDetails.get("lookupModule");
                        lookupModuleName = (String) fieldLookupModuleDetails.get("lookupModuleName");
                        FacilioModule.ModuleType moduleType = lookupModule.getTypeEnum();
                        value = record.get(fieldName);
                        long longValue = ((Number) value).longValue();
                        List<Long> values = new ArrayList<>();
                        if(!isRestrictDependantModules) {
                            handleTobeAndFetchedRecords(lookupModuleName, moduleType, fetchedRecords, longValue, toBeFetchRecords, values, allMigrationModuleNames);
                        }
                    }
                    str.append(PackageFileUtil.escapeCsv(value.toString()));
                } else {
                    str.append("");
                }
                str.append(',');
            }
            writer.append(StringUtils.stripEnd(str.toString(), ","));
            writer.append('\n');
        }
        writer.flush();
        writer.close();
    }

    public static List<String> getDataConfigModuleNames(PackageFolderContext rootFolder) throws Exception {
        PackageFileContext dataConfigFileContext = rootFolder.getFile(PackageConstants.DATA_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);
        XMLBuilder dataConfigXml = dataConfigFileContext.getXmlContent();
        XMLBuilder dataXML = dataConfigXml.getElement(PackageConstants.MODULE_NAMES);
        List<XMLBuilder> allModulesXml = dataXML.getFirstLevelElementListForTagName(PackageConstants.MODULE);
        if (CollectionUtils.isEmpty(allModulesXml)) {
            return new ArrayList<>();
        }
        return allModulesXml.stream().map(xml -> xml.getAttribute(PackageConstants.NAME)).collect(Collectors.toList());
    }

    public static Map<String, String> getModuleNameVsXmlFileName(PackageFolderContext rootFolder) {
        PackageFileContext dataConfigFileContext = rootFolder.getFile(PackageConstants.DATA_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);
        XMLBuilder dataConfigXml = dataConfigFileContext.getXmlContent();
        XMLBuilder dataXML = dataConfigXml.getElement(PackageConstants.MODULE_NAMES);
        List<XMLBuilder> allModulesXml = dataXML.getFirstLevelElementListForTagName(PackageConstants.MODULE);
        Map<String, String> moduleNameVsXmlFileName = new HashMap<>();
        if (CollectionUtils.isEmpty(allModulesXml)) {
            return moduleNameVsXmlFileName;
        }
        for (XMLBuilder moduleXml : allModulesXml) {
            moduleNameVsXmlFileName.put(moduleXml.getAttribute(PackageConstants.NAME), moduleXml.getText());
        }
        return moduleNameVsXmlFileName;
    }


    public static File getModuleCsvFile(PackageFolderContext rootFolder, String moduleFileName) {
        PackageFolderContext dataFolder = rootFolder.getFolder(PackageConstants.DATA_FOLDER_NAME);
        File moduleCsvFile = null;
        if(dataFolder.getFile(moduleFileName)!=null){
            moduleCsvFile = dataFolder.getFile(moduleFileName).getCsvContent();
        }

        return moduleCsvFile;
    }


    public static File exportNotesAsCSVFile(FacilioModule module, PackageFolderContext rootFolder, List<NoteContext> notes) throws Exception {

        PackageFolderContext dataFolder = rootFolder.getFolder(PackageConstants.DATA_FOLDER_NAME);
        PackageFolderContext dataFilesFolder = rootFolder.getFolder(PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME);

        File csvFile = PackageFileUtil.getFileWithPath(dataFolder, module.getName());;
        String filePath = csvFile.getAbsolutePath();

        List<FacilioField> notesFields = PackageNotesUtil.getAllNotesCsvFields(module);

        notesFields.stream().forEach(noteField -> noteField.setDefault(true));

        try (FileWriter writer = new FileWriter(filePath, false)) {
            StringBuilder str = new StringBuilder();

            for (FacilioField field : notesFields) {
                String sheetColumnName = field.getName();
                str.append(PackageFileUtil.escapeCsv(sheetColumnName));
                str.append(',');
            }

            writer.append(StringUtils.stripEnd(str.toString(), ","));
            writer.append('\n');

            for (NoteContext note : notes) {
                str = new StringBuilder();
                Map<String,FileInfo> tempInfo = new HashMap<>();
                for (FacilioField field : notesFields) {
                    Object value = null;
                    if (Objects.equals(field.getModule().getName(), FacilioConstants.ContextNames.COMMENT_ATTACHMENTS)) {
                        List<CommentAttachmentContext> commentAttachments = note.getAttachments();
                        if (CollectionUtils.isNotEmpty(commentAttachments)) {
                            StringJoiner sj = new StringJoiner(",");
                            for (CommentAttachmentContext commentAttachment : commentAttachments) {
                                FileStore fs = FacilioFactory.getFileStore();
                                FileInfo fileInfo = fs.getFileInfo(commentAttachment.getFileId());
                                String fileNameWithExtension = commentAttachment.getFileName();
                                String extension = FilenameUtils.getExtension(fileNameWithExtension);
                                String uniqueIdentifierForFile = PackageFileUtil.getUniqueIdentifierForFile(String.valueOf(commentAttachment.getParent()),fileInfo)+FacilioConstants.ContextNames.COMMENT_ATTACHMENTS+ (StringUtils.isEmpty(extension) ? "" : "."+extension);
                                if(!tempInfo.containsKey(uniqueIdentifierForFile)){
                                    dataFilesFolder.addFileInfo(uniqueIdentifierForFile,fileInfo);
                                }
                                tempInfo.put(uniqueIdentifierForFile,fileInfo);
                                if(Objects.equals(field.getName(), "attachmentContentType")){
                                    value = commentAttachment.getContentType();
                                }else if (Objects.equals(field.getName(), "attachmentFileName")){
                                    value = fileNameWithExtension;
                                }else if (Objects.equals(field.getName(), "attachmentUniqueIdentifierForFile")){
                                    value = uniqueIdentifierForFile;
                                }else {
                                    value = FieldUtil.getValue(commentAttachment, field);
                                }
                                if (value != null) {
                                    sj.add(escapeCsv(value.toString()));
                                }
                            }
                            str.append(escapeCsv(sj.toString()));
                        } else {
                            str.append("");
                        }
                    } else if (Objects.equals(field.getModule().getName(), FacilioConstants.ContextNames.COMMENT_SHARING)) {
                        List<CommentSharingContext> commentSharingContexts = note.getCommentSharing();
                        if (CollectionUtils.isNotEmpty(commentSharingContexts)) {
                            StringJoiner sj = new StringJoiner(",");
                            for (CommentSharingContext sharingContext : commentSharingContexts) {
                                value = FieldUtil.getValue(sharingContext, field);
                                if (value != null) {
                                    sj.add(escapeCsv(value.toString()));
                                }
                            }
                            str.append(PackageFileUtil.escapeCsv(sj.toString()));
                        } else {
                            str.append("");
                        }
                    } else if (Objects.equals(field.getModule().getName(), FacilioConstants.ContextNames.COMMENT_MENTIONS)) {
                        List<CommentMentionContext> commentMentionContexts = note.getMentions();
                        if (CollectionUtils.isNotEmpty(commentMentionContexts)) {
                            StringJoiner sj = new StringJoiner(",");
                            for (CommentMentionContext commentMention : commentMentionContexts) {
                                value = FieldUtil.getValue(commentMention, field);
                                if (value != null) {
                                    sj.add(escapeCsv(value.toString()));
                                }
                            }
                            str.append(PackageFileUtil.escapeCsv(sj.toString()));
                        } else {
                            str.append("");
                        }
                    } else {
                        value = FieldUtil.getValue(note, field);
                        if(Objects.equals(field.getName(), "parentNote") && value instanceof NoteContext){
                            value = ((NoteContext) value).getId();
                        }
                        if (value != null) {
                            if (value instanceof IAMUser) {
                                value = ((IAMUser) value).getId();
                            }
                            str.append(escapeCsv(value.toString()));
                        } else {
                            str.append("");
                        }
                    }
                    str.append(',');
                }

                writer.append(StringUtils.stripEnd(str.toString(), ","));
                writer.append('\n');
            }

            writer.flush();
            writer.close();
            csvFile = new File(filePath);

        }
        return csvFile;
    }


    public static File exportAttachmentsAsCSVFile(FacilioModule module, PackageFolderContext dataFolder, List<AttachmentContext> attachments, PackageFolderContext dataFilesFolder, ModuleBean moduleBean) throws Exception {

        File csvFile = PackageFileUtil.getFileWithPath(dataFolder, module.getName());
        String filePath = csvFile.getAbsolutePath();
        List<FacilioField> attachmentFields = getPackageAttachmentFields();

        try(FileWriter writer = new FileWriter(filePath,false)){
            StringBuilder str = new StringBuilder();

            for (FacilioField field : attachmentFields) {
                String sheetColumnName = field.getName();
                str.append(PackageFileUtil.escapeCsv(sheetColumnName));
                str.append(',');
            }

            writer.append(StringUtils.stripEnd(str.toString(), ","));
            writer.append('\n');

            for(AttachmentContext attachment : attachments){
                str = new StringBuilder();
                for (FacilioField field : attachmentFields) {
                    Object value = null;
                    if(Objects.equals(field.getName(), "id")){
                        value = attachment.getId();
                    } else if (Objects.equals(field.getName(), "recordId")) {
                        value = attachment.getParentId();
                    } else if (Objects.equals(field.getName(), "fileName")) {
                        value = attachment.getFileName();
                    } else if (Objects.equals(field.getName(), "contentType")) {
                        value = attachment.getContentType();
                    } else if (Objects.equals(field.getName(), "uniqueIdentifier")) {
                        FileStore fs = FacilioFactory.getFileStore();
                        FileInfo fileInfo = fs.getFileInfo(attachment.getFileId());
                        String fileNameWithExtension = attachment.getFileName();
                        String extension = FilenameUtils.getExtension(fileNameWithExtension);
                        String uniqueIdentifierForFile = PackageFileUtil.getUniqueIdentifierForFile(String.valueOf(attachment.getParentId()),fileInfo)+FacilioConstants.ContextNames.ATTACHMENT+ (StringUtils.isEmpty(extension) ? "" : "."+extension);
                        value = uniqueIdentifierForFile;
                        dataFilesFolder.addFileInfo(uniqueIdentifierForFile,fileInfo);
                    }
                    if(value!=null){
                        str.append(escapeCsv(value.toString()));
                    }else {
                        str.append("");
                    }
                    str.append(',');
                }
                writer.append(StringUtils.stripEnd(str.toString(), ","));
                writer.append('\n');
            }
            writer.flush();
            writer.close();
        }
        return csvFile;
    }
    
    public static List<AttachmentContext> getAttachmentListFromCsv(File attachmentCsvFile, FacilioModule parentModule,DataMigrationBean targetConnection, PackageFolderContext dataFileFolder, DataMigrationStatusContext dataMigrationObj) throws Exception{
        
        List<AttachmentContext> allModuleAttachments = new ArrayList<>();
        if(attachmentCsvFile==null || dataFileFolder==null){
            return allModuleAttachments;
        }

        Map<Long, List<Long>> moduleIdVsOldIds = new HashMap<>();
        Map<Long, Map<Long, Long>> moduleIdVsOldIdVsNewId = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(attachmentCsvFile.getAbsolutePath()))) {
            List<String[]> csvData = csvReader.readAll();
            String[] fieldNames = csvData.get(0);
            for (int i = 1; i < csvData.size(); i++) {
                String[] fieldValues = csvData.get(i);
                AttachmentContext AttachmentContext = getCsvAttachment(fieldNames, fieldValues,dataFileFolder,parentModule, moduleIdVsOldIds);
                allModuleAttachments.add(AttachmentContext);
            }
        }catch (Exception ex){
            LOGGER.info("####Sandbox --- Exception while creating csv attachment file from props modulename :" +parentModule.getName() +"Exception : "+ex);
            throw new Exception(ex);
        }

        for (Map.Entry<Long, List<Long>> entry : moduleIdVsOldIds.entrySet()) {
            Long moduleId = entry.getKey();
            List<Long> oldIds = entry.getValue();
            Map<Long, Long> idMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), moduleId, oldIds);
            moduleIdVsOldIdVsNewId.put(moduleId, idMappings);
        }

        Map<Long, Long> idMapping = moduleIdVsOldIdVsNewId.get(parentModule.getModuleId());

        for(AttachmentContext attachmentContext : allModuleAttachments){
            long oldRecordId = attachmentContext.getParentId();
            attachmentContext.setParentId(idMapping.get(oldRecordId));
            attachmentContext.setCreatedTime(System.currentTimeMillis());
        }

        return allModuleAttachments;
    }

    private static List<FacilioField> getPackageAttachmentFields (){

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField());
        fields.add(FieldFactory.getNumberField("recordId",null,null));
        fields.add(FieldFactory.getStringField("fileName",null,null));
        fields.add(FieldFactory.getStringField("contentType",null,null));
        fields.add(FieldFactory.getStringField("uniqueIdentifier",null,null));

        return fields;
    }

    private static AttachmentContext getCsvAttachment(String[] fieldNames, String[] fieldValues,PackageFolderContext dataFileFolder, FacilioModule parentModule, Map<Long, List<Long>> moduleIdVsOldIds) throws Exception{

        AttachmentContext attachmentContext = new AttachmentContext();
        List<Long> oldIds = new ArrayList<>();
        if (MapUtils.isNotEmpty(moduleIdVsOldIds)) {
            oldIds = moduleIdVsOldIds.get(parentModule.getModuleId());
        }
        for (int i = 0; i < fieldNames.length; i++) {
            if (i < fieldValues.length) {
                if (StringUtils.isEmpty(fieldValues[i])) {
                    continue;
                }
                String fieldName = fieldNames[i];
                String fieldValue = fieldValues[i];
                if (Objects.equals(fieldName, "fileName")) {
                    attachmentContext.setFileName(fieldValue);
                } else if (Objects.equals(fieldName, "contentType")) {
                    attachmentContext.setContentType(fieldValue);
                } else if (Objects.equals(fieldName, "recordId") || Objects.equals(fieldName, "parent")) {
                    long oldId = Long.parseLong(fieldValue);
                    if (!oldIds.contains(oldId)) {
                        oldIds.add(oldId);
                    }
                    attachmentContext.setParentId(oldId);
                } else if (Objects.equals(fieldName, "uniqueIdentifier")) {
                    String filePath = dataFileFolder.getPath() + File.separator + fieldValue;
                    File sourceOrgFile = new File(filePath);
                    File file = new File(sourceOrgFile.getParentFile(),attachmentContext.getFileName());
                    sourceOrgFile.renameTo(file);
                    FileContext fileContext = PackageFileUtil.addFileToStore(file, attachmentContext.getContentType());
                    attachmentContext.setFileId(fileContext.getFileId());
                }

            }
        }

        moduleIdVsOldIds.put(parentModule.getModuleId(), oldIds);

        return attachmentContext;
    }

    public static List<String> getFileSupportedFolderNames(){
        return FILE_SUPPORTED_FOLDER_NAMES;
    }
    private static final List<String> FILE_SUPPORTED_FOLDER_NAMES = Collections.unmodifiableList(
            new ArrayList<>(
                    Arrays.asList(PackageConstants.META_FILES_FOLDER_NAME,PackageConstants.DATA_ATTACHMENT_FILE_FOLDER_NAME)
            )
    );

    public static String getSandboxFolderPath() {
        return System.getProperties().getProperty("java.io.tmpdir") + File.separator + "sandbox" + File.separator + "Unzipped-Package-Files" ;
    }
}
