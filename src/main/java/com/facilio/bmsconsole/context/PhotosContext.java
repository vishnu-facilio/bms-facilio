package com.facilio.bmsconsole.context;

import com.facilio.fs.FileStoreFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;

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
	public String getUrl() throws Exception {
	
//		if (this.url == null && this.parentId > 0) {
//			if (!AccountUtil.getCurrentAccount().isFromMobile() && parentId > 0) {
//				StringBuffer url = ServletActionContext.getRequest().getRequestURL();
//				StringBuilder builder = new StringBuilder(url.substring(0, url.indexOf("/api")))
//						.append(AwsUtil.getConfig("clientapp.url") + "/files/preview/" + photoId);
//				return builder.toString();
//			}
//			FileStore fs = FileStoreFactory.getInstance().getFileStore();
//			url = fs.getPrivateUrl(this.parentId);
//		}
		if (this.photoId > 0) {
			return FileStoreFactory.getInstance().getFileStore().getPrivateUrl(photoId);
		}
		return null;
	}

	public String getOriginalUrl() throws Exception {
		return getUrl();
	}
	private long ttime = -1;
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
}
