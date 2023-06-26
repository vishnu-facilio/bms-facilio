package com.facilio.services.filestore;

import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;


import com.azure.storage.blob.batch.BlobBatchClient;
import com.azure.storage.blob.batch.BlobBatchClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.DeleteSnapshotsOptionType;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ImageScaleUtil;
import com.facilio.fs.FileInfo;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;



public class AzureFileStore extends FileStore {
    private static final Logger LOGGER = LogManager.getLogger(AzureFileStore.class.getName());

    private static BlobServiceClient Azure_Blob_CLIENT = null;
    private static final long EXPIRATION = 2; //in days
    private static final String SECRET_ROOT_PATH = "secrets";


    private static String containerName;
    private static final String FILES_DIR = "files";


    private String getContainerName() {
        if (this.containerName == null) {
            this.containerName = FacilioProperties.getConfig("azure.container.name");
        }
        return this.containerName;
    }

    private long getExpiration() {
        return this.EXPIRATION;
    }

    public AzureFileStore(long orgId, long userId) {
        super(orgId, userId);
    }

    @Override
    protected String getRootPath(String namespace) {
        NamespaceConfig namespaceConfig = FileStore.getNamespace(namespace);
        Objects.requireNonNull(namespaceConfig, "Invalid namespace in getting root path");
        StringBuilder rootPath = new StringBuilder();
        rootPath.append(getOrgId())
                .append(File.separator)
                .append(FILES_DIR)
                .append(File.separator)
                .append(namespace);
        if (namespaceConfig.isDailyDirectoryNeeded()) {
            rootPath.append(File.separator).append(DATE_FORMAT.format(new Date()));
        }
        return rootPath.toString();
    }

    @Override
    public long addFile(String namespace, String fileName, File file, String contentType) throws Exception {
        return this.addFile(namespace, fileName, file, contentType, false);
    }


    @Override
    public long addFile(String namespace, String fileName, File file, String contentType, int[] resize) throws Exception {
        long fileId = this.addFile(namespace, fileName, file, contentType, false);

        for (int resizeVal : resize) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); FileInputStream fis = new FileInputStream(file);) {
                if (contentType.contains("image/")) {
                    BufferedImage imBuff = ImageIO.read(fis);
                    BufferedImage out = ImageScaleUtil.resizeImage(imBuff, resizeVal, resizeVal);
                    ImageIO.write(out, "png", baos);
                    baos.flush();
                    byte[] imageInByte = baos.toByteArray();
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);) {
                        String resizedFilePath = getRootPath(namespace) + File.separator + fileId + "-resized-" + resizeVal + "x" + resizeVal;
                        addBlob(bis, resizedFilePath,contentType);
                        addResizedFileEntry(namespace, fileId, resizeVal, resizeVal, resizedFilePath, imageInByte.length, "image/png");

                    }
                }
            } catch (Exception e) {
                LOGGER.error("Exception occurred ", e);
            }
        }
        return fileId;
    }

    @Override
    public long addOrphanedFile(String namespace, String fileName, File file, String contentType) throws Exception {
        return this.addFile(namespace, fileName, file, contentType, true);
    }

    @Override
    public long addFile(String namespace, String fileName, String content, String contentType) throws Exception {
        return this.addFile(namespace, fileName, content, contentType, false);
    }

    @Override
    public long addFile(String namespace, String fileName, byte[] content, String contentType, boolean isOrphan) throws Exception {
        if (contentType == null) {
            throw new IllegalArgumentException("Content type is mandatory");
        }

        long fileId = addDummyFileEntry(namespace, fileName, isOrphan);
        String filePath = getRootPath(namespace) + File.separator + fileId + "-" + fileName;
        long fileSize = content.length;
        try {
            InputStream inputStream = new ByteArrayInputStream(content);
            addBlob(getContainerName(), filePath, inputStream, contentType);
            updateFileEntry(namespace, fileId, fileName, filePath, fileSize, contentType);
        } catch (Exception e) {
            deleteBlob(getContainerName(), filePath);
            deleteFileEntry(namespace, fileId);
            throw e;
        }
        return fileId;
    }

    private long addFile(String namespace, String fileName, File file, String contentType, boolean isOrphan) throws Exception {
        if (contentType == null) {
            throw new IllegalArgumentException("Content type is mandtory");
        }
        long fileId = addDummyFileEntry(namespace, fileName, false);
        String filePath = getRootPath(namespace) + File.separator + fileId + "-" + fileName;
        long fileSize = file.length();
        LOGGER.debug("addFile: filePath: " + filePath);
        LOGGER.debug("addFile: fileName: " + fileName);
        try {
            addBlob(getContainerName(), filePath, file,contentType);
            updateFileEntry(namespace, fileId, fileName, filePath, fileSize, contentType);
            scheduleCompressJob(namespace, fileId, contentType);
            return fileId;
        } catch (Exception e) {
            deleteBlob(getContainerName(), filePath);
            deleteFileEntry(namespace, fileId);
            throw e;
        }
    }

    private long addFile(String namespace, String fileName, String content, String contentType, boolean isOrphan) throws Exception {
        if (contentType == null) {
            throw new IllegalArgumentException("Content type is mandtory");
        }
        long fileId = addDummyFileEntry(namespace, fileName, false);
        String filePath = getRootPath(namespace) + File.separator + fileId + "-" + fileName;
        LOGGER.debug("addFile: filePath: " + filePath);
        LOGGER.debug("addFile: fileName: " + fileName);

        try {
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());
            addBlob(getContainerName(), filePath, inputStream, null);
            updateFileEntry(namespace, fileId, fileName, filePath, -1, contentType);
            scheduleCompressJob(namespace, fileId, contentType);
            return fileId;
        } catch (Exception e) {
            deleteBlob(getContainerName(), filePath);
            deleteFileEntry(namespace, fileId);
            throw e;
        }
    }

    @Override
    public void addCompressedFile(String namespace, long fileId, FileInfo fileInfo) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String resizedFilePath = getRootPath(namespace) + File.separator + fileId + "-compressed";
            byte[] imageInByte = writeCompressedFile(namespace, fileId, fileInfo, baos, resizedFilePath);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);) {
                addBlob(bis, resizedFilePath,null);
            } catch (Exception e) {
                deleteBlob(getContainerName(), resizedFilePath);
                deleteFileEntry(namespace, fileId);
                throw e;
            }
        }
    }

    @Override
    public InputStream readFile(String namespace, long fileId) throws Exception {
        return readFile(namespace, fileId, false);
    }

    @Override
    public InputStream readFile(String namespace, long fileId, boolean fetchOriginal) throws Exception {
        FileInfo fileInfo = getFileInfo(namespace, fileId, fetchOriginal);
        return readFile(fileInfo);
    }

    @Override
    public InputStream readFile(FileInfo fileInfo) throws Exception {
        if (fileInfo == null) {
            return null;
        }
        LOGGER.debug("filePath: " + getContainerName() + "" + fileInfo.getFilePath());
        try {
            BlobClient blobClient = getBlobServiceClient().getBlobContainerClient(getContainerName()).getBlobClient(fileInfo.getFilePath());
            return blobClient.openInputStream();

        } catch (Exception e) {
            LOGGER.error("Error occurred while getting file : " + String.valueOf(fileInfo.getFilePath()), e);
            throw e;
        }
    }

    @Override
    public boolean deleteFile(String namespace, long fileId) throws Exception {
        FileInfo fileInfo = getFileInfo(namespace, fileId);
        if (fileInfo == null) {
            return false;
        }
        return deleteFiles(namespace, Collections.singletonList(fileId));
    }

    @Override
    public boolean deleteFiles(String namespace, List<Long> fileId) throws Exception {
        return markAsDeleted(namespace, fileId) > 0;
    }

    @Override
    public boolean deleteFilePermanently(String namespace, long fileId) throws Exception {

        FileInfo fileInfo = getFileInfo(namespace, fileId);
        BlobClient blobClient = getBlobServiceClient().getBlobContainerClient(getContainerName()).getBlobClient(fileInfo.getFilePath());
        blobClient.deleteIfExists();
        return deleteFileEntry(namespace, fileId);
    }

    @Override
    public boolean deleteFilesPermanently(String namespace, List<Long> fileIds) throws Exception {
        List<String> filePathList = getFilePathList(namespace, fileIds);
        String containerPath = FacilioProperties.getConfig("azure.storage.endpoint") + getContainerName() + File.separator;
        List<String> blobUrlList = filePathList.stream().map(filePath -> {
            try {
                return   containerPath+URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        List<List<String>> partitionList = ListUtils.partition(blobUrlList, 1000);
        try {
             BlobContainerClient blobContainerClient = getBlobServiceClient().getBlobContainerClient(getContainerName());
            BlobBatchClient blobBatchClient = new BlobBatchClientBuilder(blobContainerClient).buildClient();
            for(List<String> batch:partitionList )
            {       blobBatchClient.deleteBlobs(batch, DeleteSnapshotsOptionType.INCLUDE).forEach((Response<Void> response)->{
                     response.getRequest().getUrl();
                     response.getStatusCode();
                 });
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("Error occurred while getting file : ", e);
            throw e;
        }
    }

    @Override
    public boolean renameFile(String namespace, long fileId, String newName) throws Exception {
        FileInfo fileInfo = getFileInfo(namespace, fileId);
        if (fileInfo == null) {
            return false;
        }
        String oldFilePath = fileInfo.getFilePath();
        String newFilePath = getRootPath(namespace) + File.separator + fileId + "-" + newName;
        BlobContainerClient blobContainerClient = getBlobServiceClient().getBlobContainerClient(getContainerName());
        BlobClient oldBlobClient = blobContainerClient.getBlobClient(oldFilePath);
        BlobClient newBlobClient = blobContainerClient.getBlobClient(newFilePath);
        newBlobClient.upload(oldBlobClient.openInputStream());
        oldBlobClient.delete();
        updateFileEntry(namespace, fileId, newName, newFilePath, fileInfo.getFileSize(), fileInfo.getContentType());
        return true;
    }

    @Override
    public String getDownloadUrl(long fileId) throws Exception {
        return super.getDownloadUrl(fileId);
    }

    @Override
    public String getOrgiFileUrl(String namespace, long fileId) throws Exception {
        FileInfo fileInfo = getFileInfo(namespace, fileId);
        if (fileInfo != null) {
            return fetchUrl(fileInfo, getExpiration(), false);
        }
        return null;
    }

    @Override
    public String getOrgiDownloadUrl(String namespace, long fileId) throws Exception {
        FileInfo fileInfo = getFileInfo(namespace, fileId, true);
        if (fileInfo != null) {
            return fetchUrl(fileInfo, getExpiration(), true);
        }
        return null;
    }

    @Override
    public boolean isFileExists(String newVersion) {
        return false;
    }

    @Override
    public long addSecretFile(String fileName, File file, String contentType) throws Exception {
        LOGGER.info("add secret file called : " + fileName + ":" + file.getPath() + " : " + contentType);
        long fileId = addDummySecretFileEntry(fileName);
        String filePath = SECRET_ROOT_PATH + File.separator + fileName;
        long fileSize = file.length();
        try {
            addBlob(getContainerName(), filePath, file,contentType);
            updateSecretFileEntry(fileId, fileName, filePath, fileSize, contentType);
            return fileId;
        } catch (Exception ex) {
            deleteBlob(getContainerName(), fileName);
            deleteSecretFileEntry(fileId);
            throw ex;
        }
    }


    @Override
    public InputStream getSecretFile(String fileName) {

        try {
            BlobClient blobClient = getBlobServiceClient().getBlobContainerClient(getContainerName()).getBlobClient(SECRET_ROOT_PATH + File.separator + fileName);
            return blobClient.openInputStream();

        } catch (Exception e) {
            LOGGER.error("Error occurred while getting secret file : " + fileName, e);
            throw e;
        }
    }

    @Override
    public boolean removeSecretFile(String fileName) throws Exception {
        FileInfo fileInfo = SecretFileUtils.getSecretFileInfo(fileName);
        BlobClient blobClient = getBlobServiceClient().getBlobContainerClient(getContainerName()).getBlobClient(fileInfo.getFilePath());
        blobClient.deleteIfExists();
        return false;
    }

    @Override
    public boolean isSecretFileExists(String fileName) {
        return false;
    }

    private void addBlob(String containerName, String filePath, File file,String contentType) throws FileNotFoundException {
        BlobContainerClient blobContainerClient = getBlobServiceClient().getBlobContainerClient(containerName);
        BlobParallelUploadOptions parallelTransferOptions = new BlobParallelUploadOptions(new FileInputStream(file));
        BlobClient blobClient = blobContainerClient.getBlobClient(filePath);
        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
        parallelTransferOptions.setHeaders(headers);
        blobClient.uploadWithResponse(parallelTransferOptions,null, Context.NONE);
    }

    private void addBlob(String containerName, String filePath, InputStream inputStream,String contentType)  {
        BlobContainerClient blobContainerClient = getBlobServiceClient().getBlobContainerClient(containerName);
        BlobParallelUploadOptions parallelTransferOptions = new BlobParallelUploadOptions(inputStream);
        BlobClient blobClient = blobContainerClient.getBlobClient(filePath);
        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
        parallelTransferOptions.setHeaders(headers);
        blobClient.uploadWithResponse(parallelTransferOptions,null, Context.NONE);
    }

    private void addBlob(ByteArrayInputStream byteArray, String filePath, String contentType)   {
        BlobContainerClient blobContainerClient = getBlobServiceClient().getBlobContainerClient(getContainerName());
        BlobParallelUploadOptions parallelTransferOptions = new BlobParallelUploadOptions(byteArray);
        BlobClient blobClient = blobContainerClient.getBlobClient(filePath);
        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
        parallelTransferOptions.setHeaders(headers);
        blobClient.uploadWithResponse(parallelTransferOptions,null, Context.NONE);
    }

    private String fetchUrl(FileInfo fileInfo, long expiryDays, boolean isDownloadable) {
        BlobServiceClient client = getBlobServiceClient();
        BlobClient blobClient = client.getBlobContainerClient(getContainerName()).getBlobClient(fileInfo.getFilePath());
        BlobSasPermission blobSasPermission = new BlobSasPermission().setReadPermission(true);

        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(expiryDays);

        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, blobSasPermission)
                .setProtocol(SasProtocol.HTTPS_ONLY).setStartTime(OffsetDateTime.now());
        if (isDownloadable) {
            values.setContentDisposition("attachment; filename=" + fileInfo.getFileName());
        } else {
            values.setContentDisposition("inline; filename=" + fileInfo.getFileName());
        }
        return blobClient.getBlobUrl() + "?" + blobClient.generateSas(values);
    }


    private void deleteBlob(String containerName, String fileName) {
        BlobContainerClient blobContainerClient = getBlobServiceClient().getBlobContainerClient(containerName);
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        blobClient.deleteIfExists();

    }

    public static BlobServiceClient getBlobServiceClient() {
        if (Azure_Blob_CLIENT == null) {
            Azure_Blob_CLIENT = new BlobServiceClientBuilder().connectionString(FacilioProperties.getConfig("storage.connectionStr")).buildClient();
        }
        return Azure_Blob_CLIENT;
    }
}
