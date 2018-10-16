package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.opensymphony.xwork2.ActionSupport;

public class WidgetAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Long ouid;

	public Long getOuid() {
		return ouid;
	}

	public void setOuid(Long ouid) {
		this.ouid = ouid;
	}
	
	JSONObject result;
	
	
	public JSONObject getResult() {
		return result;
	}

	public void setResult(JSONObject result) {
		this.result = result;
	}
	User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	private File avatar;
	
	public File getAvatar() {
		return avatar;
	}
	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}
	
	private String avatarFileName;

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}
	
	private String avatarContentType;
	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	String url;
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	Long photoId;
	public Long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}

	public String addPhoto() throws Exception {
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		long fileId = fs.addFile(getAvatarFileName(), getAvatar(), getAvatarContentType());
		
		setPhotoId(fileId);
		setUrl(fs.getPrivateUrl(fileId));
		
		return SUCCESS;
	}

	public String getUserCardData() throws Exception {
		
		user = AccountUtil.getUserBean().getUser(ouid);
		
		List<WorkOrderContext> openWorkOrders = WorkOrderAPI.getOpenWorkOrderForUser(user.getOuid());
		
		List<WorkOrderContext> dueTodayWorkOrders = WorkOrderAPI.getDueTodayWorkOrders(openWorkOrders);
		
		List<WorkOrderContext> overDueWorkOrders = WorkOrderAPI.getOverdueWorkOrders(openWorkOrders);
		
		result = result == null ? new JSONObject() :result;
		
		result.put("openWorkOrdersCount", openWorkOrders == null ? 0 :openWorkOrders.size());
		result.put("dueTodayWorkOrdersCount", dueTodayWorkOrders == null ? 0 :dueTodayWorkOrders.size());
		result.put("overDueWorkOrdersCount", overDueWorkOrders == null ? 0 :overDueWorkOrders.size());
		
		return SUCCESS;
	}
}
