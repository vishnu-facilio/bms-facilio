package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class PhotosAction extends ActionSupport {
	
	public String addBaseSpacePhotos() throws Exception {
		return addPhotos(FacilioConstants.ContextNames.BASE_SPACE_PHOTOS);
	}
	
	public String addAssetPhotos() throws Exception {
		return addPhotos(FacilioConstants.ContextNames.ASSET_PHOTOS);
	}
	
	public String addPhotos() throws Exception {
		return addPhotos(module);
	}
	
	private String addPhotos(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PHOTOS, getPhotos());
		
		Chain addPhotosChain = FacilioChainFactory.getAddPhotosChain();
		addPhotosChain.execute(context);
		
		return SUCCESS;
	}
	
	public String getBaseSpacePhotos() throws Exception {
		return getPhotos(FacilioConstants.ContextNames.BASE_SPACE_PHOTOS);
	}
	
	public String getAssetPhotos() throws Exception {
		return getPhotos(FacilioConstants.ContextNames.ASSET_PHOTOS);
	}
	
	public String getPhotosList() throws Exception {
		return getPhotos(module);
	}
	
	private String getPhotos(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		Chain getPhotosChain = FacilioChainFactory.getPhotosChain();
		getPhotosChain.execute(context);
		
		setPhotos((List<PhotosContext>) context.get(FacilioConstants.ContextNames.PHOTOS));
		
		return SUCCESS;
	}
	
	private String module;
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	private long parentId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private List<PhotosContext> photos;
	public List<PhotosContext> getPhotos() {
		return photos;
	}
	public void setPhotos(List<PhotosContext> photos) {
		this.photos = photos;
	}

}
