package com.facilio.services.filestore;

import java.awt.image.BufferedImage;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ImageScaleUtil;
import com.facilio.fs.FileInfo;

public class FacilioFileStore extends FileStore {
	private static Logger log = LogManager.getLogger(FacilioFileStore.class.getName());

	public FacilioFileStore(long orgId, long userId) {
		super(orgId, userId);
	}

	private String rootPath;

	
	@Override
	protected String getRootPath() {
		if(rootPath == null ) {
			 this.rootPath = getOrgId() + File.separator + "files";
		}
		return rootPath;
	}

	@Override
	public long addFile(String fileName, File file, String contentType) throws Exception {
		return this.addFile(fileName, file, contentType, false);
	}

	private long addFile(String fileName, File file, String contentType, boolean isOrphan) throws Exception {
		long fileId = addDummyFileEntry(fileName, isOrphan);
		byte[] contentInBytes = Files.readAllBytes(file.toPath());
		try {
			addComppressedFile(fileId, fileName, file, contentType);
			return addFile(fileId, fileName, contentInBytes, contentType);
		} catch (Exception e){
			return -1;
		}
	}

	@Override
	public long addFile(String fileName, File file, String contentType, int[] resize) throws Exception {
		return addFile(fileName, file, contentType, resize, false);
	}

	private long addFile(String fileName, File file, String contentType, int[] resize, boolean isOrphan) throws Exception {
		long fileId = this.addFile(fileName, file, contentType, isOrphan);
		for (int resizeVal : resize) {
			try {
				if (contentType.contains("image/")) {
					// Image resizing...

					FileInputStream fis = new FileInputStream(file);
					BufferedImage imBuff = ImageIO.read(fis);
					BufferedImage out = ImageScaleUtil.resizeImage(imBuff, resizeVal, resizeVal);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(out, "png", baos);
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					baos.close();

					String resizedFilePath = getRootPath() + File.separator + fileId+"-resized-"+resizeVal+"x"+resizeVal;

					Integer statusCode = postFile(fileId, fileName, contentType, imageInByte);
					if (statusCode == 200) {
						addResizedFileEntry(fileId, resizeVal, resizeVal, resizedFilePath, imageInByte.length, "image/png");
					}


				}
			} catch (Exception e) {
				log.info("Exception occurred ", e);
			}
		}
		return fileId;
	}

	@Override
	public long addOrphanedFile(String fileName, File file, String contentType, int[] resize) throws Exception {
		return addFile(fileName, file, contentType, resize, true);
	}

	@Override
	public long addFile(String fileName, String content, String contentType) throws Exception {
		long fileId = addDummyFileEntry(fileName, false);
		try {
			return addFile(fileId, fileName, content.getBytes(), contentType);
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

	private long addFile(long fileId, String fileName, byte[] content, String contentType) throws Exception {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type is mandtory");
		}
		
		Integer statusCode = postFile(fileId, fileName, contentType, content);
		if (statusCode == 200) {
			String filePath;

				filePath = getOrgId() + File.separator + "files" + File.separator + fileName;
				updateFileEntry(fileId, fileName, filePath, content.length, contentType);
				return fileId;

			
		} else {

				deleteFileEntry(fileId);

			return -1;
		}
	}

	@Override
	public InputStream readFile(long fileId) throws Exception {
		return readFile(fileId, false);
	}
	
	@Override
	public InputStream readFile(long fileId, boolean fetchOriginal) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId, fetchOriginal);
		return readFile(fileInfo);
	}
	
	@Override
	public InputStream readFile(FileInfo fileInfo) throws Exception {
		// TODO Auto-generated method stub
		String url = FacilioProperties.getConfig("files.url")+"/api/file/get?orgId="+getOrgId()+"&fileName="+URLEncoder.encode(fileInfo.getFileName(), "UTF-8")+"&fileId="+fileInfo.getFileId()+"&contentType="+fileInfo.getContentType();
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
	public boolean deleteFile(long fileId) throws Exception {
		ArrayList<Long> fileIds = new ArrayList<>();
		fileIds.add(fileId);
		return deleteFiles(fileIds);
	}

	@Override
	public boolean deleteFiles(List<Long> fileId) throws Exception {
		return markAsDeleted(fileId) > 0;
	}

	@Override
	public boolean renameFile(long fileId, String newName) throws Exception {
		return false;
	}
	
	private String getFileUrl(long fileId, String mode) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId);
		return FacilioProperties.getConfig("files.url") + "/api/file/get?orgId=" + getOrgId() + "&fileName=" + URLEncoder.encode(fileInfo.getFileName(), "UTF-8") + "&mode=" + mode + "&fileId=" + fileId + "&contentType=" + fileInfo.getContentType();
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

			byte[] bytes = Files.readAllBytes(uploadFile.toPath());
			request.write(bytes);
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
	public String getOrgiFileUrl(long fileId) throws Exception {
		// TODO Auto-generated method stub
		return getFileUrl(fileId, "perview");
	}

	@Override
	public String getOrgiDownloadUrl(long fileId) throws Exception {
		// TODO Auto-generated method stub
		return getFileUrl(fileId, "download");
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
	public void addComppressedFile(long fileId, String fileName, File file, String contentType) throws Exception {
		if (contentType.contains("image/")) {
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
				String resizedFilePath = getRootPath() + File.separator + fileId + "-compressed";
				byte[] imageInByte = writeCompressedFile(fileId, file, contentType, baos, resizedFilePath);
				if (imageInByte != null) {
					postFile(fileId, fileName, contentType, imageInByte);
				}
			} 
		}
	}
	
	private int postFile(long fileId, String fileName, String contentType, byte[] imageInByte) throws Exception {
		HttpUtil httpConn;

		httpConn = new HttpUtil(FacilioProperties.getConfig("files.url") + "/api/file/put");
		httpConn.addFormField("orgId", getOrgId() + "");


		httpConn.addFormField("fileId", fileId + "");
		httpConn.addFormField("fileName", fileName);
		httpConn.addFormField("contentType", contentType);

		httpConn.addFilePart("fileContent", fileName, imageInByte);

		Map<String, Object> response = httpConn.finish();

		Integer statusCode = (Integer) response.get("status");
		return statusCode;
		
	}

	@Override
	public boolean deleteFilePermenantly(long fileId) throws Exception {
		
		// TODO call api to delete file
//		return deleteFileEntry(fileId);
		return false;
	}

	@Override
	public boolean deleteFilesPermanently(List<Long> fileIds) throws Exception {
		
		// TODO call api to delete file
//		return deleteFileEntries(fileIds);
		return false;
		
	}
	
}
