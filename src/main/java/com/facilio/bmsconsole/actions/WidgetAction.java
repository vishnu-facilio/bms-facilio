package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class WidgetAction extends ActionSupport {

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
	
	private List<File> file;
	private List<String> fileContentType;
	private List<String> fileFileName;
	
	public List<File> getFile() {
		return file;
	}

	public void setFile(List<File> file) {
		this.file = file;
	}

	public List<String> getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(List<String> fileContentType) {
		this.fileContentType = fileContentType;
	}

	public List<String> getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(List<String> fileFileName) {
		this.fileFileName = fileFileName;
	}
	PhotosContext photo;
	
	public PhotosContext getPhoto() {
		return photo;
	}

	public void setPhoto(PhotosContext photo) {
		this.photo = photo;
	}

	public String addPhoto() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.file);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.fileFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.fileContentType);
		
		Chain addPhotosChain = TransactionChainFactory.getAddPhotoChain();
		addPhotosChain.execute(context);
		
		List<PhotosContext> photos = (List<PhotosContext>) context.get(FacilioConstants.ContextNames.PHOTOS);
		
		if(photos != null && !photos.isEmpty()) {
			photo = photos.get(0);
		}
		
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
