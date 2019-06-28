package com.facilio.agentIntegration;

import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.kinesis.KinesisProcessor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DownloadCertFile
{

    private static final Logger LOGGER = LogManager.getLogger(DownloadCertFile.class.getName());


    public static InputStream getCertFileInputStream(){
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String orgName = AccountUtil.getCurrentAccount().getOrg().getDomain();
        CreateKeysAndCertificateResult certificateResult = AwsUtil.signUpIotToKinesis(orgName);

        String certPemContent = certificateResult.getCertificatePem();
        InputStream certFileInputStream = new ByteArrayInputStream(certPemContent.getBytes());
        return certFileInputStream;
    }

    public static InputStream getKeyFileInputStream(){
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String orgName = AccountUtil.getCurrentAccount().getOrg().getDomain();
        CreateKeysAndCertificateResult keyResult = AwsUtil.signUpIotToKinesis(orgName);
        String ketFileContent= keyResult.getKeyPair().getPrivateKey();
        InputStream keyInputStream = new ByteArrayInputStream(ketFileContent.getBytes());
        return keyInputStream;
    }

    public static String downloadCertificate() {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String url=null;
        try {
            Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(orgId, FacilioConstants.ContextNames.FEDGE_CERT_FILE_ID);
            if (orgInfo != null) {
                long fileId = Long.parseLong((String) orgInfo.get("value"));
                FileStore fs = FileStoreFactory.getInstance().getFileStore();
                url = fs.getPrivateUrl(fileId);
            }
        } catch (Exception e) {
            LOGGER.info( "Exception in downloading certificate while getting orginfo details for " + FacilioConstants.ContextNames.FEDGE_CERT_FILE_ID, e);
        }
        if (url == null) {
            String orgName = AccountUtil.getCurrentAccount().getOrg().getDomain();
            CreateKeysAndCertificateResult certificateResult = AwsUtil.signUpIotToKinesis(orgName);
            AwsUtil.getIotKinesisTopic(orgName);
            String directoryName = "facilio/";
            File file = new File(System.getProperty("user.home") + "/fedge.zip");
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {

                addToZip(out, directoryName + "facilio.crt", certificateResult.getCertificatePem());
                addToZip(out, directoryName + "facilio-private.key", certificateResult.getKeyPair().getPrivateKey());
                addToZip(out, directoryName + "facilio.config", getFacilioConfig(orgName));
                out.finish();
                out.flush();
                FileStore fs = FileStoreFactory.getInstance().getFileStore();
                long id = fs.addFile(file.getName(), file, "application/octet-stream");
                url = fs.getPrivateUrl(id);
                CommonCommandUtil.insertOrgInfo(orgId, FacilioConstants.ContextNames.FEDGE_CERT_FILE_ID, String.valueOf(id));

            } catch (Exception e) {
                LOGGER.info("Exception occurred",e);
            }

            KinesisProcessor.startKinesis();
        }
        return url;
    }

    public static void downloadFileFromUrl(String fileName, String fileUrl)
            throws MalformedURLException, IOException {
        BufferedInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            URL fileUrlObj=new URL(fileUrl);
            inStream = new BufferedInputStream(fileUrlObj.openStream());
            outStream = new FileOutputStream(fileName);

            byte data[] = new byte[1024];
            int count;
            while ((count = inStream.read(data, 0, 1024)) != -1) {
                outStream.write(data, 0, count);
            }
        } finally {
            if (inStream != null)
                inStream.close();
            if (outStream != null)
                outStream.close();
        }
    }

    public static String unzip(String zipFilePath, String destDirectory) throws IOException {
        String extractPath = null;
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
               extractPath = extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        return destDirectory;
    }

    private static String  extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
        return filePath;
    }

    private static void addToZip(ZipOutputStream out, String fileName, String content){
        try {
            out.putNextEntry(new ZipEntry(fileName));
            out.write(content.getBytes("UTF-8"));
            out.closeEntry();
        } catch (IOException e){
            LOGGER.info("Exception occurred ",e);
        }
    }

    private static String getFacilioConfig(String domainName) {
        StringBuilder builder = new StringBuilder("clientId=").append(domainName).append("\n")
                .append("privateKeyFile=facilio-private.key").append("\n")
                .append("certificateFile=facilio.crt").append("\n")
                .append("endpoint=avzdxo3ow2ja2.iot.us-west-2.amazonaws.com").append("\n")
                .append("topic=").append(domainName);
        return builder.toString();
    }
}
