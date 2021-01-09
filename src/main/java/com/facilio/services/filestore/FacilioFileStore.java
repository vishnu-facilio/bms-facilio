package com.facilio.services.filestore;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.fs.FileInfo;

public class FacilioFileStore extends FileStore {
	private static Logger log = LogManager.getLogger(FacilioFileStore.class.getName());

	public FacilioFileStore(long orgId, long userId) {
		super(orgId, userId);
	}

	private static final Map<String, String> ROOT_PATHS = new ConcurrentHashMap<>();
	@Override
	protected String getRootPath(String namespace) {
		NamespaceConfig namespaceConfig = FileStore.getNamespace(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace in getting root path");
		String key = namespaceConfig.getName(), dateVal = null, rootPath = null;
		if (namespaceConfig.isDailyDirectoryNeeded()) {
			dateVal = DATE_FORMAT.format(new Date());
			key += "-" + dateVal;
		}
		rootPath = ROOT_PATHS.get(key);
		if (rootPath == null) {
			StringBuilder path = new StringBuilder()
									.append(getOrgId())
									.append(File.separator)
									.append("files")
									.append(File.separator)
									.append(namespace);
			if (namespaceConfig.isDailyDirectoryNeeded()) {
				path.append(File.separator).append(dateVal);
			}
			 rootPath = path.toString();
			 ROOT_PATHS.put(key, rootPath);
		}
		return rootPath;
	}

	@Override
	public long addFile(String namespace, String fileName, File file, String contentType) throws Exception {
		return this.addFile(namespace, fileName, file, contentType, false);
	}

	private long addFile(String namespace, String fileName, File file, String contentType, boolean isOrphan) throws Exception {
		long fileId = addDummyFileEntry(namespace, fileName, isOrphan);
		byte[] contentInBytes = Files.readAllBytes(file.toPath());
		try {
			//addComppressedFile(namespace, fileId, fileName, file, contentType);
            return addFile(namespace, fileId, fileName, file, contentType);
		} catch (Exception e){
			log.info("Exception occurred ", e);
			return -1;
		}
	}

	@Override
	public long addFile(String namespace, String fileName, File file, String contentType, int[] resize) throws Exception {
		return addFile(namespace, fileName, file, contentType, resize, false);
	}

	private long addFile(String namespace, String fileName, File file, String contentType, int[] resize, boolean isOrphan) throws Exception {
		long fileId = this.addFile(namespace, fileName, file, contentType, isOrphan);
		/*for (int resizeVal : resize) {
			try(
					FileInputStream fis = new FileInputStream(file);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
				)
			{
				if (contentType.contains("image/")) {
					// Image resizing...

					BufferedImage imBuff = ImageIO.read(fis);
					BufferedImage out = ImageScaleUtil.resizeImage(imBuff, resizeVal, resizeVal);

					ImageIO.write(out, "png", baos);
					baos.flush();
					byte[] imageInByte = baos.toByteArray();

					String resizedFilePath = getRootPath(namespace) + File.separator + fileId+"-resized-"+resizeVal+"x"+resizeVal;

					Integer statusCode = postFile(namespace, fileId, fileName, contentType, imageInByte);
					if (statusCode == 200) {
						addResizedFileEntry(namespace, fileId, resizeVal, resizeVal, resizedFilePath, imageInByte.length, "image/png");
					}


				}
			} catch (Exception e) {
				log.info("Exception occurred ", e);
			}
		}*/
		return fileId;
	}

	@Override
	public long addOrphanedFile(String namespace, String fileName, File file, String contentType) throws Exception {
		return addFile(namespace, fileName, file, contentType, true);
	}

	@Override
	public long addFile(String namespace, String fileName, String content, String contentType) throws Exception {
		long fileId = addDummyFileEntry(namespace, fileName, false);
		try {
			return addFile(namespace, fileId, fileName, content.getBytes(), contentType);
		} catch (Exception e){
			return -1;
		}
	}
	private long addSecretFile(long fileId, String fileName, byte[] content, String contentType) throws Exception
	{
		HttpUtil httpConn;
		httpConn = new HttpUtil(FacilioProperties.getConfig("files.url") + "/api/file/secrets/put");


		httpConn.addFormField("fileId", fileId + "");
		httpConn.addFormField("fileName", fileName);
		httpConn.addFormField("contentType", contentType);

		httpConn.addFilePart("fileContent", fileName, content);

		Map<String, Object> response = httpConn.finish();

		Integer statusCode = (Integer) response.get("status");

		if (statusCode == 200) {
			String filePath;

				filePath = "secrets"+ File.separator + "files" + File.separator + fileName;
				updateSecretFileEntry(fileId, fileName, filePath, content.length, contentType);
				return fileId;


		} else {

				deleteSecretFileEntry(fileId);
			return -1;
		}
	}

	private long addFile(String namespace, long fileId, String fileName, byte[] content, String contentType) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}

		Integer statusCode = postFile(namespace, fileId, fileName, contentType, content);
		log.info("StatusCode for add -- "+statusCode);
		if (statusCode == 200) {
			String filePath = getRootPath(namespace) + File.separator + fileId + "-" + fileName;
			updateFileEntry(namespace, fileId, fileName, filePath, content.length, contentType);
			return fileId;
		} else {
			deleteFileEntry(namespace, fileId);
			return -1;
		}
	}

    private long addFile(String namespace, long fileId, String fileName, File content, String contentType) throws Exception {
        if (contentType == null) {
            throw new IllegalArgumentException("Content type is mandtory");
        }

        Integer statusCode = postFile(namespace, fileId, fileName, contentType, content);
        log.info("StatusCode for add -- " + statusCode);
        if (statusCode == 200) {
            String filePath = getRootPath(namespace) + File.separator + fileId + "-" + fileName;
            updateFileEntry(namespace, fileId, fileName, filePath, content.length(), contentType);
            return fileId;
        } else {
            deleteFileEntry(namespace, fileId);
            return -1;
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

	private String getFileUrl(FileInfo fileInfo, String mode) throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder(FacilioProperties.getConfig("files.url"))
				.append("/api/file/get?orgId=").append(getOrgId());
		if (!DEFAULT_NAMESPACE.equals(fileInfo.getNamespace())) {
			url.append("&namespace=").append(fileInfo.getNamespace());
		}
		url.append("&fileName=").append(URLEncoder.encode(fileInfo.getFileName(), "UTF-8"))
				.append("&fileId=").append(fileInfo.getFileId())
				.append("&contentType=").append(fileInfo.getContentType());
		if (StringUtils.isNotEmpty(mode)) {
			url.append("&mode=").append(mode);
		}
		return url.toString();
	}

	@Override
	public InputStream readFile(FileInfo fileInfo) throws Exception {
		// TODO Auto-generated method stub
		URL obj = new URL(getFileUrl(fileInfo, null));
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		if(responseCode == 200) {
			BufferedInputStream bos = new BufferedInputStream(con.getInputStream());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int readBytes = 0;
			while ((readBytes = bos.read()) != -1) {
				baos.write(readBytes);
			}
			return new ByteArrayInputStream(baos.toByteArray());
		}
		return null;
	}

	@Override
	public boolean deleteFile(String namespace, long fileId) throws Exception {
		return deleteFiles(namespace, Collections.singletonList(fileId));
	}

	@Override
	public boolean deleteFiles(String namespace, List<Long> fileId) throws Exception {
		return markAsDeleted(namespace, fileId) > 0;
	}

	@Override
	public boolean renameFile(String namespace, long fileId, String newName) throws Exception {
		return false;
	}

	private String getFileUrl(String namespace, long fileId, String mode) throws Exception {
		FileInfo fileInfo = getFileInfo(namespace, fileId);
		return getFileUrl(fileInfo, mode);
	}

	public static class HttpUtil {

        private HttpURLConnection httpConn;
		private DataOutputStream request;
		private final String boundary =  "*****";
		private final String crlf = "\r\n";
		private final String twoHyphens = "--";

		public HttpUtil(String requestURL) throws IOException {

			URL url = new URL(requestURL);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setUseCaches(false);
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("Cache-Control", "no-cache");
			httpConn.setRequestProperty(
					"Content-Type", "multipart/form-data;boundary=" + this.boundary);

			request =  new DataOutputStream(httpConn.getOutputStream());
		}

		public void addFormField(String name, String value)throws IOException  {
			request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""+ this.crlf);
			request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
			request.writeBytes(this.crlf);
			request.writeBytes(value+ this.crlf);
			request.flush();
		}

		public void addFilePart(String fieldName, File uploadFile)
				throws IOException {
			String fileName = uploadFile.getName();
			request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" +
					fieldName + "\";filename=\"" +
					fileName + "\"" + this.crlf);
			request.writeBytes(this.crlf);

            FileInputStream fis = new FileInputStream(uploadFile);
            IOUtils.copy(fis, request);
            IOUtils.closeQuietly(fis);
        }

		public void addFilePart(String fieldName, String fileName, byte[] fileContent)
				throws IOException {
			request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" +
					fieldName + "\";filename=\"" +
					fileName + "\"" + this.crlf);
			request.writeBytes(this.crlf);

			request.write(fileContent);
		}

		public Map<String, Object> finish() throws IOException {
			String response ="";

			request.writeBytes(this.crlf);
			request.writeBytes(this.twoHyphens + this.boundary +
					this.twoHyphens + this.crlf);

			request.flush();
			request.close();

			int status = httpConn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				InputStream responseStream = new
						BufferedInputStream(httpConn.getInputStream());

				BufferedReader responseStreamReader =
						new BufferedReader(new InputStreamReader(responseStream));

				String line = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((line = responseStreamReader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}
				responseStreamReader.close();

				response = stringBuilder.toString();
				httpConn.disconnect();
			} else {
				throw new IOException("Server returned non-OK status: " + status);
			}

			Map<String, Object> resp = new HashMap<>();
			resp.put("status", status);
			resp.put("response", response);

            return resp;
		}
	}

	@Override
	public String getOrgiFileUrl(String namespace, long fileId) throws Exception {
		// TODO Auto-generated method stub
		return getFileUrl(namespace, fileId, "perview");
	}

	@Override
	public String getOrgiDownloadUrl(String namespace, long fileId) throws Exception {
		// TODO Auto-generated method stub
		return getFileUrl(namespace, fileId, "download");
	}

	@Override
	public boolean isFileExists(String newVersion) {
		//TODO implement is file exists..
		return true;
	}

	@Override
	public long addSecretFile(String fileName, File file, String contentType) throws Exception {
		long fileId = addDummySecretFileEntry(fileName);
		return addSecretFile(fileId,fileName,Files.readAllBytes(file.toPath()),contentType);

	}

	@Override
	public InputStream getSecretFile(String fileName) throws Exception {
		FileInfo fileInfo = SecretFileUtils.getSecretFileInfo(fileName);
		String url = FacilioProperties.getConfig("files.url")+"/api/file/secret/get?"+"&fileName="+URLEncoder.encode(fileName, "UTF-8")+"&fileId="+fileInfo.getFileId()+"&contentType="+fileInfo.getContentType();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		if(responseCode == 200) {
			BufferedInputStream bos = new BufferedInputStream(con.getInputStream());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int readBytes = 0;
			while ((readBytes = bos.read()) != -1) {
				baos.write(readBytes);
			}
			return new ByteArrayInputStream(baos.toByteArray());
		}
		return null;
	}


	@Override
	public boolean removeSecretFile(String tag) throws Exception {
		FileInfo fileInfo = SecretFileUtils.getSecretFileInfo(tag);
		HttpUtil httpConn;
		httpConn = new HttpUtil(FacilioProperties.getConfig("files.url") + "/api/file/secrets/delete");


		httpConn.addFormField("fileId", fileInfo.getFileId() + "");
		Map<String, Object> response = httpConn.finish();
		if (response.get("status").toString().equals("200")){
			deleteSecretFileEntry(fileInfo.getFileId());
			return true;
		}
		return false;
	}

	@Override
	public boolean isSecretFileExists(String fileName) {
		return false;
	}

    @Override
	public void addCompressedFile(String namespace, long fileId, FileInfo fileInfo) throws Exception {
    		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			String resizedFilePath = getRootPath(namespace) + File.separator + fileId + "-compressed";
			byte[] imageInByte = writeCompressedFile(namespace, fileId, fileInfo, baos, resizedFilePath);
			if (imageInByte != null) {
				postFile(namespace, fileId, fileInfo.getFileName(), fileInfo.getContentType(), imageInByte);
			}
        }
	}

    private int postFile(String namespace, long fileId, String fileName, String contentType, byte[] imageInByte) throws Exception {
        HttpUtil httpConn;

        httpConn = new HttpUtil(FacilioProperties.getConfig("files.url") + "/api/file/put");
        httpConn.addFormField("orgId", String.valueOf(getOrgId()));
        if (!DEFAULT_NAMESPACE.equals(namespace)) {
            httpConn.addFormField("namespace", namespace);
        }
        httpConn.addFormField("fileId", String.valueOf(fileId));
        httpConn.addFormField("fileName", fileName);
        httpConn.addFormField("contentType", contentType);
        httpConn.addFilePart("fileContent", fileName, imageInByte);
        Map<String, Object> response = httpConn.finish();

        Integer statusCode = (Integer) response.get("status");
        return statusCode;

    }

    private int postFile(String namespace, long fileId, String fileName, String contentType, File file) throws Exception {
        HttpUtil httpConn;

        httpConn = new HttpUtil(FacilioProperties.getConfig("files.url") + "/api/file/put");
        httpConn.addFormField("orgId", String.valueOf(getOrgId()));
        if (!DEFAULT_NAMESPACE.equals(namespace)) {
            httpConn.addFormField("namespace", namespace);
        }
        httpConn.addFormField("fileId", String.valueOf(fileId));
        httpConn.addFormField("fileName", fileName);
        httpConn.addFormField("contentType", contentType);
        httpConn.addFilePart("fileContent", file);
        Map<String, Object> response = httpConn.finish();

        Integer statusCode = (Integer) response.get("status");
        return statusCode;

    }

	@Override
	public boolean deleteFilePermanently(String namespace, long fileId) throws Exception {

        // TODO call api to delete file
//		return deleteFileEntry(fileId);
		return false;
	}

	@Override
	public boolean deleteFilesPermanently(String namespace, List<Long> fileIds) throws Exception {

        // TODO call api to delete file
//		return deleteFileEntries(fileIds);
		return false;

    }

}
