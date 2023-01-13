package com.facilio.connectedapp.util;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.connectedapp.context.ConnectedAppDeploymentContext;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.FacilioException;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.filestore.FileStoreFactory;
import com.facilio.services.filestore.S3FileStore;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class ConnectedAppHostingAPI {

    public static final List<String> EDIT_SUPPORTED_FILE_TYPES = Arrays.asList(new String[]{"txt","md","xml","css","js","html","json","hbs"});

    public static class Constants {
        public static final long ROOT_FOLDER = -1000;
        public static final long SOURCE_ZIP_MAX_SIZE = 10485760; // 10 MB
        public static final long SOURCE_ZIP_MAX_FILES = 100; // 100 files
        public static final long CONNECTED_APP_FILE_MAX_SIZE = 2097152; // 2 MB

        public static final String CONNECTED_APP = "connectedApp";
        public static final String CONNECTED_APP_ID = "connectedAppId";
        public static final String CONNECTED_APP_FILE = "connectedAppFile";
        public static final String CONNECTED_APP_FILES = "connectedAppFiles";
        public static final String CONNECTED_APP_FILE_ID = "connectedAppFileId";
        public static final String CONNECTED_APP_DEPLOYMENT = "connectedAppDeployment";
        public static final String CONNECTED_APP_DEPLOYMENT_LIST = "connectedAppDeploymentList";
        public static final String ROWS_UPDATED = "rowsUpdated";
        public static final String CAN_DEPLOY = "canDeploy";
        public static final String CAN_PUBLISH = "canPublish";
        public static final String CONNECTED_APP_ZIP_FILE = "connectedAppZipFile";

        public static final String CONNECTED_APP_S3_BUCKET = "connectedApps.s3.bucket";
        public static final String CONNECTED_APP_HOSTING_DOMAIN = "connectedApps.hosting.domain";
    }

    private static String getConnectedAppS3Bucket() {
        return FacilioProperties.getConfig(Constants.CONNECTED_APP_S3_BUCKET);
    }

    public static String getConnectedAppHostingDomain() {
        return FacilioProperties.getConfig(Constants.CONNECTED_APP_HOSTING_DOMAIN);
    }

    public static boolean isInternalHostingEnabled() {
        return StringUtils.isNotEmpty(getConnectedAppS3Bucket()) && StringUtils.isNotEmpty(getConnectedAppHostingDomain());
    }

    public static ConnectedAppFileContext addAppFolder(Long connectedAppId) throws Exception {
        return addAppFolder(connectedAppId, "app");
    }

    public static ConnectedAppFileContext addAppFolder(Long connectedAppId, String appFolderName) throws Exception {
        ConnectedAppFileContext appFolder = new ConnectedAppFileContext();
        appFolder.setFileName(appFolderName);
        appFolder.setDirectory(true);
        appFolder.setParentId(Constants.ROOT_FOLDER);
        appFolder.setConnectedAppId(connectedAppId);
        appFolder.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
        appFolder.setSysCreatedTime(System.currentTimeMillis());
        appFolder.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        appFolder.setSysModifiedTime(System.currentTimeMillis());

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getConnectedAppFilesModule().getTableName())
                .fields(FieldFactory.getConnectedAppFilesFields());
        long fileId = builder.insert(FieldUtil.getAsProperties(appFolder));
        appFolder.setId(fileId);
        return appFolder;
    }

    public static ConnectedAppFileContext addConnectedAppFile(Long connectedAppId, ConnectedAppFileContext connectedAppFile) throws Exception {
        if (connectedAppFile.getParentId() <= 0) {
            throw new FacilioException("File/folder cannot be added to root folder.");
        }
        else {
            ConnectedAppFileContext parentFolder = getConnectedAppFile(connectedAppId, connectedAppFile.getParentId());
            if (parentFolder == null || !parentFolder.isDirectory()) {
                throw new FacilioException("Given Parent Folder is not exists or its not an directory.");
            }
        }
        connectedAppFile.setConnectedAppId(connectedAppId);
        connectedAppFile.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
        connectedAppFile.setSysCreatedTime(System.currentTimeMillis());
        connectedAppFile.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        connectedAppFile.setSysModifiedTime(System.currentTimeMillis());

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getConnectedAppFilesModule().getTableName())
                .fields(FieldFactory.getConnectedAppFilesFields());
        long fileId = builder.insert(FieldUtil.getAsProperties(connectedAppFile));
        connectedAppFile.setId(fileId);
        return connectedAppFile;
    }

    public static int updateConnectedAppFile(Long connectedAppId, ConnectedAppFileContext connectedAppFile) throws Exception {
        ConnectedAppFileContext oldConnectedAppFile = getConnectedAppFile(connectedAppId, connectedAppFile.getId());
        if (oldConnectedAppFile == null) {
            throw new FacilioException("No file/folder exists with the Id: \"+connectedAppFile.getId()+\" in capp: \"+connectedAppId+\" to update.");
        }
        else if (oldConnectedAppFile.getParentId() <= 0) {
            throw new FacilioException("Root folder cannot be updated.");
        }

        connectedAppFile.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        connectedAppFile.setSysModifiedTime(System.currentTimeMillis());

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getConnectedAppFilesModule().getTableName())
                .fields(FieldFactory.getConnectedAppFilesFields())
                .andCondition(CriteriaAPI.getIdCondition(connectedAppFile.getId(), ModuleFactory.getConnectedAppFilesModule()))
                .andCriteria(getSysDeletedCriteria());

        Map<String, Object> props = FieldUtil.getAsProperties(connectedAppFile);
        int updatedRows = updateBuilder.update(props);
        if (updatedRows > 0) {
            if (connectedAppFile.getFileId() > 0 && oldConnectedAppFile.getFileId() > 0 && connectedAppFile.getFileId() != oldConnectedAppFile.getFileId()) {
                // file content changed, so deleting old file entry
                FileStoreFactory.getInstance().getFileStore().deleteFile(oldConnectedAppFile.getFileId());
            }
            return updatedRows;
        }
        return 0;
    }

    public static int deleteConnectedAppFile(Long connectedAppId, Long connectedAppFileId) throws Exception {
        ConnectedAppFileContext oldConnectedAppFile = getConnectedAppFile(connectedAppId, connectedAppFileId);
        if (oldConnectedAppFile == null) {
            throw new FacilioException("No file/folder exists with the Id: "+connectedAppFileId+" in capp: "+connectedAppId+" to delete.");
        }
        else if (oldConnectedAppFile.getParentId() <= 0) {
            throw new FacilioException("Root folder cannot be deleted.");
        }
        List<ConnectedAppFileContext> filesToDelete = new ArrayList<>();
        filesToDelete.add(oldConnectedAppFile);
        if (oldConnectedAppFile.isDirectory()) {
            getChildConnectedAppFilesInternal(connectedAppId, oldConnectedAppFile, filesToDelete, 1, false);
        }
        List<Long> fileIdsToDelete = filesToDelete.stream().map(ConnectedAppFileContext::getId).collect(Collectors.toList());

        ConnectedAppFileContext deleteFileContext = new ConnectedAppFileContext();
        deleteFileContext.setSysDeletedBy(AccountUtil.getCurrentUser().getId());
        deleteFileContext.setSysDeletedTime(System.currentTimeMillis());

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getConnectedAppFilesModule().getTableName())
                .fields(FieldFactory.getConnectedAppFilesFields())
                .andCondition(CriteriaAPI.getIdCondition(fileIdsToDelete, ModuleFactory.getConnectedAppFilesModule()))
                .andCriteria(getSysDeletedCriteria());

        Map<String, Object> props = FieldUtil.getAsProperties(deleteFileContext);
        int updatedRows = updateBuilder.update(props);
        if (updatedRows > 0) {
            return updatedRows;
        }
        return 0;
    }

    private static Criteria getSysDeletedCriteria() {
        Criteria sysDeletedCriteria = new Criteria();
        sysDeletedCriteria.addOrCondition(CriteriaAPI.getCondition("SYS_DELETED_TIME", "sysDeletedTime", "", CommonOperators.IS_EMPTY));
        sysDeletedCriteria.addOrCondition(CriteriaAPI.getCondition("SYS_DELETED_TIME", "sysDeletedTime", "0", NumberOperators.LESS_THAN));
        return sysDeletedCriteria;
    }

    public static ConnectedAppFileContext getConnectedAppFile(Long connectedAppId, Long fileId) throws Exception {
        if (fileId == null || fileId <= 0) {
            return null;
        }
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConnectedAppFilesModule().getTableName())
                .select(FieldFactory.getConnectedAppFilesFields());

        builder.andCondition(CriteriaAPI.getIdCondition(fileId, ModuleFactory.getConnectedAppFilesModule()));
        builder.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID", "connectedAppId", connectedAppId+"", NumberOperators.EQUALS));
        builder.andCriteria(getSysDeletedCriteria());

        List<ConnectedAppFileContext> fileContextList = FieldUtil.getAsBeanListFromMapList(builder.get(), ConnectedAppFileContext.class);
        if (fileContextList != null && fileContextList.size() > 0) {
            return fileContextList.get(0);
        }
        return null;
    }

    public static ConnectedAppFileContext getRootFolder(Long connectedAppId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConnectedAppFilesModule().getTableName())
                .select(FieldFactory.getConnectedAppFilesFields());

        builder.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID", "connectedAppId", connectedAppId+"", NumberOperators.EQUALS));
        builder.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", Constants.ROOT_FOLDER+"", NumberOperators.EQUALS));
        builder.andCriteria(getSysDeletedCriteria());

        List<ConnectedAppFileContext> fileContextList = FieldUtil.getAsBeanListFromMapList(builder.get(), ConnectedAppFileContext.class);
        if (fileContextList != null && fileContextList.size() > 0) {
            return fileContextList.get(0);
        }
        return null;
    }

    public static List<ConnectedAppFileContext> getConnectedAppFileChilds(Long connectedAppId, Long parentId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConnectedAppFilesModule().getTableName())
                .select(FieldFactory.getConnectedAppFilesFields());

        builder.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID", "connectedAppId", connectedAppId+"", NumberOperators.EQUALS));
        builder.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", parentId+"", NumberOperators.EQUALS));
        builder.andCriteria(getSysDeletedCriteria());

        List<ConnectedAppFileContext> fileContextList = FieldUtil.getAsBeanListFromMapList(builder.get(), ConnectedAppFileContext.class);
        if (fileContextList != null && fileContextList.size() > 0) {
            return fileContextList;
        }
        return null;
    }

    public static ConnectedAppFileContext getConnectedAppFileTree(Long connectedAppId) throws Exception {
        ConnectedAppFileContext rootFolder = getConnectedAppFileChilds(connectedAppId,Constants.ROOT_FOLDER).get(0);;
        rootFolder.setFilePath("app");
        rootFolder.setChildren(getConnectedAppFileTreeInternal(connectedAppId, rootFolder, 1));
        return rootFolder;
    }

    public static List<ConnectedAppFileContext> getConnectedAppFilesList(Long connectedAppId) throws Exception {
        ConnectedAppFileContext rootFolder = getRootFolder(connectedAppId);
        rootFolder.setFilePath("app");
        List<ConnectedAppFileContext> childFiles = new ArrayList<>();
        getChildConnectedAppFilesInternal(connectedAppId, rootFolder, childFiles, 1, true);
        return childFiles;
    }

    private static List<ConnectedAppFileContext> getConnectedAppFileTreeInternal(Long connectedAppId, ConnectedAppFileContext parentFolder, int nestedCount) throws Exception {
        List<ConnectedAppFileContext> children = getConnectedAppFileChilds(connectedAppId, parentFolder.getId());
        if (children == null) {
            return null;
        }
        else if (nestedCount > 10) {
            // more than 10 nested folder levels not supported
            return null;
        }
        for (ConnectedAppFileContext connectedAppFile : children) {
            connectedAppFile.setFilePath(parentFolder.getFilePath() + File.separator + connectedAppFile.getFileName());
            if (connectedAppFile.isDirectory()) {
                connectedAppFile.setChildren(getConnectedAppFileTreeInternal(connectedAppId, connectedAppFile, nestedCount + 1));
            }
        }
        return children;
    }

    private static void getChildConnectedAppFilesInternal(Long connectedAppId, ConnectedAppFileContext parentFolder, List<ConnectedAppFileContext> fileList, int nestedCount, boolean onlyFiles) throws Exception {
        List<ConnectedAppFileContext> children = getConnectedAppFileChilds(connectedAppId, parentFolder.getId());
        if (children == null) {
            return;
        }
        else if (nestedCount > 10) {
            // more than 10 nested folder levels not supported
            return;
        }
        for (ConnectedAppFileContext connectedAppFile : children) {
            connectedAppFile.setFilePath(parentFolder.getFilePath() + File.separator + connectedAppFile.getFileName());
            if (connectedAppFile.isDirectory()) {
                if (!onlyFiles) {
                    fileList.add(connectedAppFile);
                }
                getChildConnectedAppFilesInternal(connectedAppId, connectedAppFile, fileList,nestedCount + 1, onlyFiles);
            }
            else {
                fileList.add(connectedAppFile);
            }
        }
    }

    public static ConnectedAppDeploymentContext getLatestConnectedAppDeployment(Long connectedAppId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConnectedAppDeploymentsModule().getTableName())
                .select(FieldFactory.getConnectedAppDeploymentsFields());

        builder.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID", "connectedAppId", connectedAppId+"", NumberOperators.EQUALS));

        builder.orderBy("VERSION_NUMBER desc");
        builder.limit(1);

        List<ConnectedAppDeploymentContext> deploymentContexts = FieldUtil.getAsBeanListFromMapList(builder.get(), ConnectedAppDeploymentContext.class);
        if (deploymentContexts != null && deploymentContexts.size() > 0) {
            return deploymentContexts.get(0);
        }
        return null;
    }

    public static boolean isChangesAvailable(Long connectedAppId) throws Exception {
        ConnectedAppDeploymentContext latestVersion = getLatestConnectedAppDeployment(connectedAppId);
        return isChangesAvailable(connectedAppId, latestVersion);
    }

    public static boolean isChangesAvailable(Long connectedAppId, ConnectedAppDeploymentContext latestVersion) throws Exception {
        if (latestVersion == null) {
            return true;
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConnectedAppFilesModule().getTableName())
                .select(FieldFactory.getConnectedAppFilesFields());

        builder.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID", "connectedAppId", connectedAppId+"", NumberOperators.EQUALS));
        builder.andCondition(CriteriaAPI.getCondition("SYS_MODIFIED_TIME", "sysModifiedTime", latestVersion.getSysCreatedTime()+"", NumberOperators.GREATER_THAN_EQUAL));
        builder.andCriteria(getSysDeletedCriteria());

        List<ConnectedAppFileContext> fileContextList = FieldUtil.getAsBeanListFromMapList(builder.get(), ConnectedAppFileContext.class);
        if (fileContextList != null && fileContextList.size() > 0) {
            return true;
        }
        return false;
    }

    public static ConnectedAppDeploymentContext publishConnectedAppDeployment(Long connectedAppId, Long deploymentId, String comments) throws Exception {
        ConnectedAppDeploymentContext latestDeployment = ConnectedAppHostingAPI.getLatestConnectedAppDeployment(connectedAppId);
        if (latestDeployment != null && latestDeployment.getId() != deploymentId) {
            throw new FacilioException("The publishing deployment (id: "+deploymentId+") is not a latest version. connectedAppId: "+connectedAppId);
        }
        ConnectedAppDeploymentContext deploymentContext = new ConnectedAppDeploymentContext();
        deploymentContext.setPublished(true);
        deploymentContext.setComments(comments);
        deploymentContext.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        deploymentContext.setSysModifiedTime(System.currentTimeMillis());

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getConnectedAppDeploymentsModule().getTableName())
                .fields(FieldFactory.getConnectedAppDeploymentsFields())
                .andCondition(CriteriaAPI.getIdCondition(deploymentId, ModuleFactory.getConnectedAppDeploymentsModule()));

        Map<String, Object> props = FieldUtil.getAsProperties(deploymentContext);
        int updatedRows = updateBuilder.update(props);
        if (updatedRows > 0) {
            String productionBaseURL = "https://" + latestDeployment.getUniqueId() + "." + getConnectedAppHostingDomain();
            updateHostingURL(connectedAppId, null, productionBaseURL);
            return latestDeployment;
        }
        return null;
    }

    public static ConnectedAppDeploymentContext addConnectedAppDeployment(Long connectedAppId, Boolean publish, String comments) throws Exception {
        publish = publish == null ? false : publish;
        String uniqueId = UUID.randomUUID().toString().replaceAll("-", "");
        int versionNumber = 1;
        ConnectedAppDeploymentContext latestVersion = getLatestConnectedAppDeployment(connectedAppId);
        if (latestVersion != null) {
            versionNumber = latestVersion.getVersionNumber() + 1;
        }

        ConnectedAppFileContext rootFolder = getConnectedAppFileChilds(connectedAppId,Constants.ROOT_FOLDER).get(0);;
        rootFolder.setFilePath("app");

        List<ConnectedAppFileContext> childFiles = new ArrayList<>();
        getChildConnectedAppFilesInternal(connectedAppId, rootFolder, childFiles, 1, true);

        String rootPath = uniqueId + File.separator;
        S3FileStore.getClient().putObject(getConnectedAppS3Bucket(), rootPath + "index.html", "Hello from Connected app.");
        for (ConnectedAppFileContext child : childFiles) {
            String fullFilePath = rootPath + child.getFilePath();
            InputStream fileStream = FileStoreFactory.getInstance().getFileStore().readFile(child.getFileId());
            if (fileStream != null) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(child.getFileSize());
                metadata.setContentType(child.getContentType());
                S3FileStore.getClient().putObject(getConnectedAppS3Bucket(), fullFilePath, fileStream, metadata);
            }
        }

        ConnectedAppDeploymentContext deploymentContext = new ConnectedAppDeploymentContext();
        deploymentContext.setConnectedAppId(connectedAppId);
        deploymentContext.setVersionNumber(versionNumber);
        deploymentContext.setUniqueId(uniqueId);
        deploymentContext.setComments(comments);
        deploymentContext.setPublished(publish);
        deploymentContext.setDeployStatus(ConnectedAppDeploymentContext.DeployStatus.DEPLOYED);
        deploymentContext.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
        deploymentContext.setSysCreatedTime(System.currentTimeMillis());
        deploymentContext.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        deploymentContext.setSysModifiedTime(System.currentTimeMillis());

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getConnectedAppDeploymentsModule().getTableName())
                .fields(FieldFactory.getConnectedAppDeploymentsFields());
        long fileId = builder.insert(FieldUtil.getAsProperties(deploymentContext));
        deploymentContext.setId(fileId);

        String sandboxBaseURL = "https://" + uniqueId + "." + getConnectedAppHostingDomain();
        String productionBaseURL = null;
        if (publish) {
            productionBaseURL = sandboxBaseURL;
        }
        updateHostingURL(connectedAppId, sandboxBaseURL, productionBaseURL);

        return deploymentContext;
    }

    private static int updateHostingURL(Long connectedAppId, String sandboxBaseURL, String productionBaseURL) throws Exception {
        ConnectedAppContext connectedAppContext = ConnectedAppAPI.getConnectedApp(connectedAppId);
        if (connectedAppContext == null) {
            throw new FacilioException("No active connected app available. connectedAppId: "+connectedAppId);
        }
        else if (connectedAppContext.getHostingTypeEnum() != ConnectedAppContext.HostingType.INTERNAL) {
            throw new FacilioException("Hosting URL cannot be updated for external connected apps. connectedAppId: "+connectedAppId);
        }
        else if (StringUtils.isEmpty(sandboxBaseURL) && StringUtils.isEmpty(productionBaseURL)) {
            throw new FacilioException("Sandbox URL or Production URL is mandatory. connectedAppId: "+connectedAppId);
        }

        ConnectedAppContext updateContext = new ConnectedAppContext();
        if (sandboxBaseURL != null) {
            updateContext.setSandBoxBaseUrl(sandboxBaseURL);
        }
        if (productionBaseURL != null) {
            updateContext.setProductionBaseUrl(productionBaseURL);
        }

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getConnectedAppsModule().getTableName())
                .fields(FieldFactory.getConnectedAppFields())
                .andCondition(CriteriaAPI.getIdCondition(connectedAppContext.getId(), ModuleFactory.getConnectedAppsModule()));

        Map<String, Object> props = FieldUtil.getAsProperties(updateContext);
        int updatedRows = updateBuilder.update(props);
        return updatedRows;
    }

    public static String getContentType(String fileName) {
        String extn = FilenameUtils.getExtension(fileName);
        String contentType = null;
        if (StringUtils.isEmpty(extn)) {
            contentType = "text/plain";
        }
        else if ("js".equalsIgnoreCase(extn)) {
            contentType = "application/javascript";
        }
        else if ("css".equalsIgnoreCase(extn)) {
            contentType = "text/css";
        }
        else if ("html".equalsIgnoreCase(extn)) {
            contentType = "text/html";
        }
        else if ("svg".equalsIgnoreCase(extn)) {
            contentType = "image/svg+xml";
        }
        else {
            contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
        }
        if (contentType != null) {
            return contentType;
        }
        return "text/plain";
    }
}
