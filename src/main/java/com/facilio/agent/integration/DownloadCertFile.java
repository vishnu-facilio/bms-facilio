package com.facilio.agent.integration;

import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.service.FacilioHttpUtilsFW;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.util.FacilioUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DownloadCertFile {

    private static final Logger LOGGER = LogManager.getLogger(DownloadCertFile.class.getName());
    private static final String FACILIO_CERT_FILE = "facilio.crt";
    private static final String FACILIO_PRIVATE_KEY = "facilio-private.key";
    public static final String ROOT_CRT = "root.crt";


    public static InputStream getCertKeyZipInputStream(String type) {
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId();
        Map<String, Object> orgInfo = null;
        try {
            String certFileId = FacilioAgent.getCertFileId(type);
            orgInfo = CommonCommandUtil.getOrgInfo(orgId, certFileId);
            if (orgInfo != null) {
                long fileId = Long.parseLong((String) orgInfo.get("value"));

                FileStore fs = FacilioFactory.getFileStore();
                return fs.readFile(fileId);

            }
        } catch (Exception e) {
            LOGGER.info("Exception Occurred ", e);
        }
        return null;
    }

    public static long addCertificate(String policyName, String type) throws Exception {
        long fileId = -1;
        CreateKeysAndCertificateResult certificateResult = AwsUtil.createIotToKafkaLink(policyName, policyName, type);
        String directoryName = FacilioUtil.normalizePath("facilio/");
		String certFileId = FacilioAgent.getCertFileId(type);
		String outFileName = FacilioAgent.getCertFileName(type);
		File file = new File(System.getProperty("user.home") + outFileName);
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
			addToZip(out, directoryName + FACILIO_CERT_FILE, certificateResult.getCertificatePem());
			addToZip(out, directoryName + FACILIO_PRIVATE_KEY, certificateResult.getKeyPair().getPrivateKey());
            String rootCaCertificate = getRootCaCertificate();
            if(rootCaCertificate!=null){
                addToZip(out, directoryName + ROOT_CRT, rootCaCertificate);
            }
			out.finish();
			out.flush();
			FileStore fs = FacilioFactory.getFileStore();
			long id = fs.addFile(file.getName(), file, "application/octet-stream");
			CommonCommandUtil.insertOrgInfo(certFileId, String.valueOf(id));
			file.delete();
		}
		return fileId;
	}

    public static String getRootCaCertificate() throws Exception {
        String url = "https://www.amazontrust.com/repository/AmazonRootCA1.pem";
        StringBuilder response = new StringBuilder();

        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                }

                LOGGER.info("Response from the server ROOT CA:\n" + response);
            } else {
                LOGGER.error("Error ROOT CA: HTTP GET request failed. Response Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            LOGGER.error("Exception while downloading amazon root CA certificate", e);
            return null;
        }

        return response.toString();
    }

    public static void downloadFileFromUrl(String fileName, String fileUrl)
            throws MalformedURLException, IOException {
        BufferedInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            URL fileUrlObj = new URL(fileUrl);
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

    private static String extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
        return filePath;
    }

    private static void addToZip(ZipOutputStream out, String fileName, String content) {
        try {
            out.putNextEntry(new ZipEntry(fileName));
            out.write(content.getBytes("UTF-8"));
            out.closeEntry();
        } catch (IOException e) {
            LOGGER.info("Exception occurred ", e);
        }
    }

    public static Map<String, InputStream> getCertKeyFileInputStreamsFromFileStore(String type) {
        String directoryName = FacilioUtil.normalizePath("facilio/");
        InputStream fis = getCertKeyZipInputStream(type);
        Map<String, InputStream> filesMap = new HashMap<>();
        if (fis == null) {
            LOGGER.info(" Inputstream emty ");
            return filesMap;
        }
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                LOGGER.info(" file name " + fileName);
                int size;
                byte[] buffer = new byte[2048];
                fileName = fileName.replace(directoryName, "");
                LOGGER.info(" trimmed file name " + fileName);
                if (fileName.isEmpty()) {
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
                filesMap.put(fileName, inputStream);
                file.delete();
            }
            zis.close();
            fis.close();
            LOGGER.info(" files map  " + filesMap);
            return filesMap;
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }

        return null;
    }

    public static InputStream getKeyFileInputStream() {
        return null;
    }
}
