package com.facilio.fs;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class FacilioFileStore extends FileStore {

	public FacilioFileStore(long orgId, long userId) {
		super(orgId, userId);
		// TODO Auto-generated constructor stub
	}

	private String rootPath;

	
	@Override
	protected String getRootPath() {
		// TODO Auto-generated method stub
		if(rootPath==null )
		{
			 this.rootPath = getOrgId() + File.separator + "files";
		}
		return rootPath;
	}

	@Override
	public long addFile(String fileName, File file, String contentType) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long addFile(String fileName, File file, String contentType, int[] resize) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long addFile(String fileName, String content, String contentType) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InputStream readFile(long fileId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteFile(long fileId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteFiles(List<Long> fileId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean renameFile(long fileId, String newName) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPrivateUrl(long fileId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrivateUrl(long fileId, int width) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDownloadUrl(long fileId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
