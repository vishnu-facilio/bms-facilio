package com.facilio.oci.util;

import com.facilio.aws.util.FacilioProperties;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CopyObjectDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.RenameObjectDetails;
import com.oracle.bmc.objectstorage.model.StorageTier;
import com.oracle.bmc.objectstorage.requests.*;
import com.oracle.bmc.objectstorage.responses.*;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import lombok.extern.log4j.Log4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

@Log4j
public class OracleStorageClient {

    private static ObjectStorage CLIENT;
    private final String OCI_NAMESPACE = FacilioProperties.getConfig("oci.namespace");

    public static ObjectStorage initClient(AbstractAuthenticationDetailsProvider provider) {
        CLIENT = ObjectStorageClient.builder()
                .region(Region.UK_LONDON_1)
                .build(provider);
        return CLIENT;
    }

    private ObjectStorage getClient() {
        if(CLIENT == null) {
            return OracleStorageClient.initClient(OCIUtil.getAuth());
        }
        return CLIENT;
    }


    public PutObjectResponse putObject(String bucketName, String objectName, File file) throws IOException {
//        putMultiPartObject(bucketName, objectName, file);
        return putObject(bucketName, objectName, convertToStream(file));
    }

    private InputStream convertToStream(File file) throws IOException {
        return Files.newInputStream(file.toPath());
    }

    private PutObjectResponse putObject(String bucketName, String objectName, InputStream body) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .namespaceName(OCI_NAMESPACE)
                .bucketName(bucketName)
                .objectName(objectName)
                .putObjectBody(body)
                .storageTier(StorageTier.Standard)
                .build();

        /* Send request to the Client */
        try {
            return getClient().putObject(putObjectRequest);
        } catch (BmcException e) {
            LOGGER.error(e);
            return null;
        }
    }

    private void putMultiPartObject(String bucketName, String objectName, File body) {
        UploadConfiguration uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(true)
                        .allowParallelUploads(true)
                        .build();

        UploadManager uploadManager = new UploadManager(getClient(), uploadConfiguration);

        Map<String, String> metadata = null;
        String contentType = "file";
        String contentEncoding = null;
        String contentLanguage = null;
        String contentDisposition = "inline";

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(bucketName)
                        .namespaceName(OCI_NAMESPACE)
                        .objectName(objectName)
                        .build();

        UploadManager.UploadRequest uploadDetails =
                UploadManager.UploadRequest.builder(body).allowOverwrite(true).build(request);

        // upload request and print result
        // if multi-part is used, and any part fails, the entire upload fails and will throw
        // BmcException
        UploadManager.UploadResponse response = uploadManager.upload(uploadDetails);
    }

    public PutObjectResponse putObject(String bucketName, String objectName, byte[] imageInByte) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageInByte)) {
            return putObject(bucketName, objectName, inputStream);
        }
    }

    public PutObjectResponse putObject(String bucketName, String objectName, String content) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return putObject(bucketName, objectName, inputStream);
    }

    public GetObjectResponse getObject(String bucketName, String objectName) {
        try {
            return getClient().getObject(
                    GetObjectRequest.builder()
                            .namespaceName(OCI_NAMESPACE)
                            .bucketName(bucketName)
                            .objectName(objectName)
                            .build());
        } catch (BmcException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public CopyObjectResponse copyObject(String oldBucket, String oldObject, String newBucket, String newObject) {
        CopyObjectDetails copyObjectDetails = CopyObjectDetails.builder()
                .sourceObjectName(oldObject)
                .destinationRegion(Region.UK_LONDON_1.getRegionId())
                .destinationNamespace(OCI_NAMESPACE)
                .destinationBucket(newBucket)
                .destinationObjectName(newObject)
                .build();

        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .namespaceName(OCI_NAMESPACE)
                .bucketName(oldBucket)
                .copyObjectDetails(copyObjectDetails)
                .build();

        /* Send request to the Client */
        try {
            return getClient().copyObject(copyObjectRequest);
        } catch (BmcException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public DeleteObjectResponse deleteObject(String bucketName, String objectName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .namespaceName(OCI_NAMESPACE)
                .bucketName(bucketName)
                .objectName(objectName)
                .build();

        /* Send request to the Client */
        try {
            return getClient().deleteObject(deleteObjectRequest);
        } catch (BmcException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public HeadObjectResponse doesObjectExist(String bucketName, String objectName) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .namespaceName(OCI_NAMESPACE)
                .bucketName(bucketName)
                .objectName(objectName)
                .build();
        try {
            return getClient().headObject(headObjectRequest);
        } catch (BmcException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public String generatePresignedUrl(String bucketName, String objectName, long expiration) {
        Date expiry = new Date();
        expiry.setTime(expiry.getTime()+expiration);

        CreatePreauthenticatedRequestDetails requestDetails =
                CreatePreauthenticatedRequestDetails.builder()
                        .name("par-object-"+expiry.getTime())
                        .objectName(objectName)
                        .timeExpires(expiry)
                        .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectRead)
                        .build();

        CreatePreauthenticatedRequestRequest request =
                CreatePreauthenticatedRequestRequest.builder()
                        .createPreauthenticatedRequestDetails(requestDetails)
                        .namespaceName(OCI_NAMESPACE)
                        .bucketName(bucketName)
                        .build();
        try {
            CreatePreauthenticatedRequestResponse response =
                    getClient().createPreauthenticatedRequest(request);

            return MessageFormat.format("{0}{1}", getClient().getEndpoint(), response.getPreauthenticatedRequest().getAccessUri());
        } catch (BmcException e) {
            LOGGER.error(e);
            return null;
        }

    }

    public RenameObjectResponse renameObject(String bucketName, String oldObject, String newObject) {
        RenameObjectDetails renameObjectDetails = RenameObjectDetails.builder()
                .sourceName(oldObject)
                .newName(newObject)
                .build();

        RenameObjectRequest renameObjectRequest = RenameObjectRequest.builder()
                .namespaceName(OCI_NAMESPACE)
                .bucketName(bucketName)
                .renameObjectDetails(renameObjectDetails)
                .build();

        /* Send request to the Client */
        try {
            RenameObjectResponse response = getClient().renameObject(renameObjectRequest);
            return response;
        } catch (BmcException e) {
            LOGGER.error(e);
            return null;
        }
    }
}
