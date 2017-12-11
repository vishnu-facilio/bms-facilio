package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class PhotosContext extends ModuleBaseWithCustomFields {
	private long parentId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private long photoId;
	public long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}
	
	public String getUrl() throws Exception {
		if (this.photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.photoId, 120);
		}
		return null;
	}

	public String getOriginalUrl() throws Exception {
		if (this.photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.photoId);
		}
		return null;
	}
	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
}
