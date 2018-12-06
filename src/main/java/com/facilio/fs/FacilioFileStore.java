package com.facilio.fs;

import com.facilio.aws.util.AwsUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FacilioFileStore extends FileStore {

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

		long fileId = addDummyFileEntry(fileName);
		fileName = fileId + "-" + fileName;
		byte[] contentInBytes = Files.readAllBytes(file.toPath());
		try {
			return addFile(fileId, fileName, contentInBytes, contentType);
		} catch (Exception e){
			return -1;
		}
	}

	@Override
	public long addFile(String fileName, File file, String contentType, int[] resize) throws Exception {
		return 0;
	}

	@Override
	public long addFile(String fileName, String content, String contentType) throws Exception {
		long fileId = addDummyFileEntry(fileName);
		fileName = fileId + "-" + fileName;
		try {
			return addFile(fileId, fileName, content.getBytes(), contentType);
		} catch (Exception e){
			return -1;
		}
	}

	private long addFile(long fileId, String fileName, byte[] content, String contentType) throws Exception {

		String filePath = getOrgId() + File.separator + "files" + File.separator;
		String request = AwsUtil.getConfig("files.url")+"/api/file/put?orgId="+getOrgId()+"&fileName="+fileName;
		if(request != null) {
			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", contentType);
			conn.setRequestProperty("charset", StandardCharsets.UTF_8.displayName());
			byte[] postData = content;

			conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
			conn.setUseCaches(false);

			try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
				wr.write(postData);
			}
			if(conn.getResponseCode() == 200 ) {
				updateFileEntry(fileId, fileName, filePath, postData.length, contentType);
			} else {
				deleteFileEntry(fileId);
				return -1;
			}
		}
		return -1;
	}

	@Override
	public InputStream readFile(long fileId) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId);
		String url = AwsUtil.getConfig("files.url")+ "/api/file/get?orgId="+getOrgId()+"&fileName="+fileInfo.getFileName();
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

	@Override
	public String getPrivateUrl(long fileId) throws Exception {
		return getDownloadUrl(fileId);
	}

	@Override
	public String getPrivateUrl(long fileId, int width) throws Exception {

		return getDownloadUrl(fileId);
	}

	@Override
	public String getDownloadUrl(long fileId) throws Exception {
		FileInfo fileInfo = getFileInfo(fileId);
		return AwsUtil.getConfig("files.url")+File.separator +"/api/file/get?orgId="+getOrgId()+"&fileName="+fileInfo.getFileName();
	}

}
