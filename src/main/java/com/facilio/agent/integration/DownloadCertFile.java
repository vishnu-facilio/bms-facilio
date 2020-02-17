package com.facilio.agent.integration;

import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DownloadCertFile
{

    private static final Logger LOGGER = LogManager.getLogger(DownloadCertFile.class.getName());


    public static InputStream getCertKeyZipInputStream(String type){
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId();
        Map<String, Object> orgInfo = null;
        try {
            String certFileId = FacilioAgent.getCertFileId(type);
            orgInfo = CommonCommandUtil.getOrgInfo(orgId,certFileId);
            if (orgInfo != null) {
                long fileId = Long.parseLong((String) orgInfo.get("value"));

                    FileStore fs = FacilioFactory.getFileStore();
                    return fs.readFile(fileId);

            }
        } catch (Exception e) {
            LOGGER.info("Exception Occurred ",e);
        }
        return null;
    }
    public static String downloadCertificate( String policyName, String type) {
        try {
            return downloadCertificateCommand(policyName,type);
        } catch (Exception e) {
            LOGGER.info("Exception while downloadCertificate for poilcy name ->"+policyName+" for type->"+type);
        }
        return null;
    }
    public static String downloadCertificateCommand( String policyName, String type) throws Exception {
        LOGGER.info("policy name "+policyName);
        String certFileId = FacilioAgent.getCertFileId(type);
        LOGGER.info("certFileId "+certFileId);
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String url=null;
            Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(orgId, certFileId);
            if (orgInfo != null) {
                    long fileId = Long.parseLong((String) orgInfo.get("value"));
                    FileStore fs = FacilioFactory.getFileStore();
                    url = fs.getPrivateUrl(fileId);
            }
        if (url == null) {
            LOGGER.info(" url not present ");
            String orgDomainName = AccountUtil.getCurrentAccount().getOrg().getDomain();
            CreateKeysAndCertificateResult certificateResult = AwsUtil.signUpIotToKinesis(orgDomainName,policyName,type );
            AwsUtil.getIotKinesisTopic(orgDomainName);
            String directoryName = "facilio/";
            String outFileName = FacilioAgent.getCertFileName(type);
            File file = new File(System.getProperty("user.home") + outFileName );
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
                addToZip(out, directoryName + "facilio.crt", certificateResult.getCertificatePem());
                addToZip(out, directoryName + "facilio-private.key", certificateResult.getKeyPair().getPrivateKey());
                addToZip(out, directoryName + "facilio.config", getFacilioConfig(orgDomainName));
                out.finish();
                out.flush();

                    FileStore fs = FacilioFactory.getFileStore();
                    long id = fs.addFile(file.getName(), file, "application/octet-stream");
                    url = fs.getPrivateUrl(id);
                    CommonCommandUtil.insertOrgInfo(orgId, certFileId, String.valueOf(id));
                file.delete();
            }
            FacilioFactory.getMessageQueue().start();
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
        StringBuilder builder = new StringBuilder("clientId=").append(domainName).append(System.lineSeparator())
                .append("privateKeyFile=facilio-private.key").append(System.lineSeparator())
                .append("certificateFile=facilio.crt").append(System.lineSeparator())
                .append("endpoint=").append(FacilioProperties.getIotEndPoint()).append(System.lineSeparator())
                .append("topic=").append(domainName);
        return builder.toString();
    }


    public static Map<String,InputStream> getCertAndKeyFileAsInputStream(String policyName, String type){
        Map<String,InputStream> filesInputStream = new HashMap<>();
        String url = DownloadCertFile.downloadCertificate(policyName,type);
        LOGGER.info(" url for certFile "+url);
        try {
            DownloadCertFile.downloadFileFromUrl(AgentIntegrationKeys.CERT_KEY_FILE,url);
            String home = System.getProperty("user.home");
            DownloadCertFile.unzip(AgentIntegrationKeys.CERT_KEY_FILE,home);
            String subDirectory = "/facilio/";
            File certFile = new File(home+subDirectory+AgentIntegrationKeys.CERT_FILE_NAME);
            File keyFile = new File(home+subDirectory+AgentIntegrationKeys.KEY_FILE_NAME);
            LOGGER.info(" files downloaded certfile "+certFile.exists()+"   keyfile "+keyFile.exists());
            InputStream certInputStream = new FileInputStream(certFile);
            InputStream keyInputStream = new FileInputStream(keyFile);
            certFile.delete();
            keyFile.delete();
            LOGGER.info(" files deleted ");
            filesInputStream.put(AgentIntegrationKeys.CERT_FILE_NAME,certInputStream);
            filesInputStream.put(AgentIntegrationKeys.KEY_FILE_NAME,keyInputStream);
            return filesInputStream;
        } catch (IOException e) {
            LOGGER.info(" Exception occurred ",e);
        }
        LOGGER.info(" Exception while downloading cert and key file, returning empty map");
        return new HashMap();
    }
    public static Map<String,InputStream> getCertKeyFileInputStreamsFromFileStore(String type) {
        String directoryName = "facilio/";
        InputStream fis =   getCertKeyZipInputStream(type);
        Map<String,InputStream> filesMap = new HashMap<>();
        if(fis == null){
            LOGGER.info(" Inputstream emty ");
            return filesMap;
        }
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                LOGGER.info(" file name "+fileName);
                int size;
                byte[] buffer = new byte[2048];
                fileName = fileName.replace(directoryName,"");
                LOGGER.info(" trimmed file name "+fileName);
                if(fileName.isEmpty()){
                    continue;
                }
                FileOutputStream fos = new FileOutputStream(fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
                while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, size);
                }
                bos.flush();
                bos.close();
                File file = new File(fileName);
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new FileReader(file));
                br.close();
                filesMap.put(fileName,inputStream);
                file.delete();
            }
            zis.close();
            fis.close();
            LOGGER.info(" files map  "+filesMap);
            return filesMap;
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }

        return null;
    }

    public static InputStream getKeyFileInputStream() {
        return null;
    }
}
