package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;

import com.amazonaws.services.rekognition.model.TextDetection;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fs.FileInfo;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;

public class PhotosAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = LogManager.getLogger(PhotosAction.class.getName());

	public String addBaseSpacePhotos() throws Exception {
		return addPhotos(FacilioConstants.ContextNames.BASE_SPACE_PHOTOS);
	}
	
	public String addAssetPhotos() throws Exception {
		return addPhotos(FacilioConstants.ContextNames.ASSET_PHOTOS);
	}
	
	public String addStoreRoomPhotos() throws Exception {
		return addPhotos(FacilioConstants.ContextNames.STORE_ROOM_PHOTOS);
	}
	
	public String addPhotos() throws Exception {
		return addPhotos(module);
	}

	
	private String addPhotos(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PHOTOS, getPhotos());
		
		FacilioChain addPhotosChain = FacilioChainFactory.getAddPhotosChain();
		addPhotosChain.execute(context);
		
		return SUCCESS;
	}
	
	public String getBaseSpacePhotos() throws Exception {
		return getPhotos(FacilioConstants.ContextNames.BASE_SPACE_PHOTOS);
	}
	
	public String getAssetPhotos() throws Exception {
		return getPhotos(FacilioConstants.ContextNames.ASSET_PHOTOS);
	}
	
	public String getStoreRoomPhotos() throws Exception {
		return getPhotos(FacilioConstants.ContextNames.STORE_ROOM_PHOTOS);
	}
	
	public String getPhotosList() throws Exception {
		return getPhotos(module);
	}
	
	public String v2getPhotosList() throws Exception {
		getPhotos(module);
		setResult(ContextNames.PHOTOS, getPhotos());
		return SUCCESS;
	}
	
	private String getPhotos(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);

		FacilioChain getPhotosChain = FacilioChainFactory.getPhotosChain();
		getPhotosChain.execute(context);
		
		setPhotos((List<PhotosContext>) context.get(FacilioConstants.ContextNames.PHOTOS));
		
		return SUCCESS;
	}
	
	private List<File> file;
	public List<File> getFile() {
		return file;
	}
	public void setFile(List<File> file) {
		this.file = file;
	}
	
	private List<String> fileContentType;
	public List<String> getFileContentType() {
		return fileContentType;
	}
	public void setFileContentType(List<String> fileContentType) {
		this.fileContentType = fileContentType;
	}
	
	private List<String> fileFileName;
	public List<String> getFileFileName() {
		return fileFileName;
	}
	public void setFileFileName(List<String> fileFileName) {
		this.fileFileName = fileFileName;
	}
	
	public String uploadBaseSpacePhotos() throws Exception {
		return uploadPhotos(FacilioConstants.ContextNames.BASE_SPACE_PHOTOS);
	}
	
	public String uploadAssetPhotos() throws Exception {
		return uploadPhotos(FacilioConstants.ContextNames.ASSET_PHOTOS);
	}
	
	public String uploadStoreRoomPhotos() throws Exception {
		return uploadPhotos(FacilioConstants.ContextNames.STORE_ROOM_PHOTOS);
	}
	
	public String uploadPhotos() throws Exception {
		return uploadPhotos(module);
	}
	
	public String v2uploadPhotos() throws Exception {
		uploadPhotos(module);
		setResult(ContextNames.PHOTOS, getPhotos());
		return SUCCESS;
	}
	public String v2deletePhotos() throws Exception {
		deletePhotos(id,module);
		setResult(ContextNames.ID, id);
		return SUCCESS;
	}
	
	public String justUploadPhotos() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.file);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.fileFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.fileContentType);
 		
 		FacilioChain justUpload = FacilioChainFactory.justUploadPhotosChain();
 		justUpload.execute(context);
 		
 		setPhotos((List<PhotosContext>) context.get(FacilioConstants.ContextNames.PHOTOS));
 		
		return SUCCESS;
	}
	
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	
	private double previousValue = -1;
	public double getPreviousValue() {
		return previousValue;
	}
	public void setPreviousValue(double previousValue) {
		this.previousValue = previousValue;
	}
	
	private boolean filterValues = true;
	public boolean isFilterValues() {
		return filterValues;
	}
	public void setFilterValues(boolean filterValues) {
		this.filterValues = filterValues;
	}

	public String getReadingFromImage() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PHOTO, file.get(0));
		context.put(FacilioConstants.ContextNames.READING_FIELD, readingFieldId);
		context.put(FacilioConstants.ContextNames.PREVIOUS_VALUE, previousValue);
		context.put(FacilioConstants.ContextNames.FILTERS, filterValues);
		
		FacilioChain getTexts = FacilioChainFactory.getReadingFromImageChain();
		getTexts.execute(context);
		
		detectedTexts = (List<TextDetection>) context.get(FacilioConstants.ContextNames.PHOTO_TEXTS);
		return SUCCESS;
	}
	
	public String getReadingFromImageFile() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PHOTO_ID, id);
		context.put(FacilioConstants.ContextNames.READING_FIELD, readingFieldId);
		context.put(FacilioConstants.ContextNames.PREVIOUS_VALUE, previousValue);
		context.put(FacilioConstants.ContextNames.FILTERS, filterValues);
		
		FacilioChain getTexts = FacilioChainFactory.getReadingFromImageChain();
		getTexts.execute(context);
		
		detectedTexts = (List<TextDetection>) context.get(FacilioConstants.ContextNames.PHOTO_TEXTS);
		return SUCCESS;
	}
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private List<TextDetection> detectedTexts;
	public List<TextDetection> getDetectedTexts() {
		return detectedTexts;
	}
	public void setDetectedTexts(List<TextDetection> detectedTexts) {
		this.detectedTexts = detectedTexts;
	}
	private String deletePhotos(long id,String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.CURRENT_WO_ACTIVITY ,WorkOrderActivityType.REMOVE_PREERQUISITE_PHOTO);
		FileStore fs = FacilioFactory.getFileStore();
		FileInfo file = fs.getFileInfo(photoId);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, Collections.singletonList(file.getFileName()));
		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, Collections.singletonList(file.getContentType()));
		FacilioChain addPhotosChain = FacilioChainFactory.getDeletePhtotosChain();
		addPhotosChain.execute(context);
		if (FacilioConstants.ContextNames.BASE_SPACE_PHOTOS.equalsIgnoreCase(moduleName) || FacilioConstants.ContextNames.ASSET_PHOTOS.equalsIgnoreCase(moduleName)) {
			try {
				FacilioContext updateContext = new FacilioContext();
				updateContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
				updateContext.put(FacilioConstants.ContextNames.RECORD_ID, parentId);
				updateContext.put(FacilioConstants.ContextNames.PHOTO_ID, photoId);
					
					FacilioChain updateDefaultPhotoChain = FacilioChainFactory.getDeleteDefaultResourcePhotoChain();
					updateDefaultPhotoChain.execute(updateContext);
			}
			catch (Exception e) {
				log.info("Exception occurred ", e);
			}
		}
		return SUCCESS;
	}
	
	private String uploadPhotos(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.file);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.fileFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.fileContentType);
 		context.put(FacilioConstants.ContextNames.CURRENT_WO_ACTIVITY ,WorkOrderActivityType.ADD_PREERQUISITE_PHOTO);
 		
		FacilioChain addPhotosChain = FacilioChainFactory.getUploadPhotosChain();
		addPhotosChain.execute(context);
		
		setPhotos((List<PhotosContext>) context.get(FacilioConstants.ContextNames.PHOTOS));
		
		if (FacilioConstants.ContextNames.BASE_SPACE_PHOTOS.equalsIgnoreCase(moduleName) || FacilioConstants.ContextNames.ASSET_PHOTOS.equalsIgnoreCase(moduleName)) {
			try {
				FacilioContext updateContext = new FacilioContext();
				updateContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
				updateContext.put(FacilioConstants.ContextNames.RECORD_ID, parentId);
					
					FacilioChain updateDefaultPhotoChain = FacilioChainFactory.getUpdateDefaultResourcePhotoChain();
					updateDefaultPhotoChain.execute(updateContext);
			}
			catch (Exception e) {
				log.info("Exception occurred ", e);
			}
		}
		
		return SUCCESS;
	}
	
	private String module;
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
    private long photoId = -1;

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	private long parentId = -1;
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
