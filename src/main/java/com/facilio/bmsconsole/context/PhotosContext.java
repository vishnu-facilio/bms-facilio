package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class PhotosContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long parentId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private long photoId = -1;
	public long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}
	private String url;
	
	public String getUrl() throws Exception {
		if (this.url == null && this.photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			url = fs.getPrivateUrl(this.photoId, 120);
		}
		return url;
	}
	
	private String originalUrl;

	public String getOriginalUrl() throws Exception {
		if (this.originalUrl == null && this.photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			originalUrl = fs.getPrivateUrl(this.photoId);
		}
		return originalUrl;
	}
	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
}
