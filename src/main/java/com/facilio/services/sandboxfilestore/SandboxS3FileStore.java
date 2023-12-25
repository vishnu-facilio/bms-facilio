package com.facilio.services.sandboxfilestore;

import com.amazonaws.AbortedException;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

@Log4j
public class SandboxS3FileStore extends SandboxFileStore {
    private String bucketName;

    private String getBucketName() {
        if (this.bucketName == null) {
            this.bucketName = FacilioProperties.getSandboxS3BucketName();
        }
        LOGGER.info("####FileStore - Sandbox Bucket Name - " + this.bucketName);
        return this.bucketName;
    }

    private static final String SANDBOX_FILES_DIR = "sandbox-data";

    public SandboxS3FileStore(long orgId, long userId) {
        super(orgId, userId);
    }

    public SandboxS3FileStore(long orgId, long userId, String bucketName) {
        super(orgId, userId);
        this.bucketName = bucketName;
    }

    @Override
    public String getRootPath(String namespace) {
        NamespaceConfig namespaceConfig = getNamespace(namespace);
        Objects.requireNonNull(namespaceConfig, "Invalid namespace in getting root path");
        StringBuilder rootPath = new StringBuilder();
        rootPath.append(SANDBOX_FILES_DIR)
                .append(File.separator)
                .append(getOrgId())
                .append(File.separator)
                .append(namespace);
        return rootPath.toString();
    }

    @Override
    public String addFileAndGetURL(File file, String fileName) throws Exception {
        String rootFilePath = getRootPath(DEFAULT_NAMESPACE) + File.separator + fileName;
        getPutObjectResult(file, rootFilePath, 3);
        return rootFilePath;
    }

    @Override
    public InputStream readFile(String fileURL) throws Exception {
        return StringUtils.isNotEmpty(fileURL) ? getS3ObjectInputStream(fileURL, 3) : null;
    }

    private S3ObjectInputStream getS3ObjectInputStream(String fileURL, int maxRetryCount) {
        int retryCount = 0;

        while (retryCount < maxRetryCount) {
            try {
                S3Object so = AwsUtil.getAmazonS3ClientForSandbox().getObject(getBucketName(), fileURL);
                return so.getObjectContent();
            } catch (AbortedException e) {
                retryCount++;
                LOGGER.info("Retrying count: " + retryCount + " for " + fileURL);
            } catch (Exception e) {
                LOGGER.error("Error occurred while getting file: " + fileURL, e);
                throw e;
            }
        }
        // If maxRetryCount is reached without success, throw an exception
        throw new RuntimeException("Failed to retrieve S3 object after " + maxRetryCount + " retries for file: " + fileURL);
    }

    @Override
    public String addDirectoryAndGetURL(File file) throws Exception {
        String rootFilePath = getRootPath(DEFAULT_NAMESPACE) + File.separator + file.getName();
        try {
            PutObjectResult rs = getPutObjectResult(file, rootFilePath, 3);
            if (rs != null) {
                File rootFile = new File(rootFilePath);
                addFiles(rootFile, file, file);
                return AwsUtil.getAmazonS3ClientForSandbox().getUrl(getBucketName(), rootFilePath).toString();
            } else {
                LOGGER.info("PutObjectResult is null. File not added - " + rootFilePath);
                return null;
            }
        } catch (Exception e) {
            LOGGER.info("Error while adding file " + rootFilePath);
            throw e;
        }
    }

    private PutObjectResult getPutObjectResult(File file, String filePath, int maxRetryCount) {
        int retryCount = 0;

        while (retryCount < maxRetryCount) {
            try {
                PutObjectResult rs = AwsUtil.getAmazonS3ClientForSandbox().putObject(getBucketName(), filePath, file);
                return rs;
            } catch (AbortedException e) {
                retryCount++;
                LOGGER.info("Retrying count: " + retryCount + " for " + filePath);
            } catch (Exception e) {
                LOGGER.error("Error occurred while adding file: " + filePath, e);
                throw e;
            }
        }
        throw new RuntimeException("Failed to add S3 object after " + maxRetryCount + " retries for file: " + filePath);
    }

    private void addFiles(File newRoot, File oldRoot, File current) throws Exception {
        File[] files = current.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    constructFilePathAndGetPutObjectResult(newRoot, oldRoot, file);
                    addFiles(newRoot, oldRoot, file);
                } else {
                    constructFilePathAndGetPutObjectResult(newRoot, oldRoot, file);
                }
            }
        }
    }

    private void constructFilePathAndGetPutObjectResult(File newRoot, File oldRoot, File currFile) {
        String relativePathStr = oldRoot.toPath().relativize(currFile.toPath()).toString();
        String filePath = constructFilePath(newRoot.getPath(), relativePathStr);
        getPutObjectResult(currFile, filePath, 3);
    }

    private static String constructFilePath(String rootPath, String filePath) {
        return rootPath + File.separator + filePath;
    }
}
