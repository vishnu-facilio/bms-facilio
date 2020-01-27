package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.util.IOUtils;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FaceRekognitionUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.filestore.FileStoreFactory;

public class VisitorFaceAPI {
	
	private static final Logger LOGGER = Logger.getLogger(VisitorFaceAPI.class.getName());

	public static String AWS_VISITOR_COLLECTION_ID() {
		long orgId = AccountUtil.getCurrentOrg().getId();
		return orgId + "_visitors";
	}
	
	private static Long getVisitorCollectionId(String awsCollectionId) throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getFaceCollectionsModule().getTableName())
				.select(FieldFactory.getFaceCollectionsFields())
				.andCondition(CriteriaAPI.getCondition("COLLECTION_ID", "collectionId", awsCollectionId, StringOperators.IS));
		
		Map<String, Object> prop = builder.fetchFirst();
		if (prop != null) {
			return (Long) prop.get("id");
		}
		
		Boolean status = FaceRekognitionUtil.createCollection(awsCollectionId);
		if (status) {
			Map<String, Object> collectionProp = new HashMap<String, Object>();
			collectionProp.put("collectionId", awsCollectionId);
			collectionProp.put("createdTime", System.currentTimeMillis());
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getFaceCollectionsModule().getTableName())
					.fields(FieldFactory.getFaceCollectionsFields());
			insertBuilder.insert(collectionProp);
			
			return (Long) collectionProp.get("id");
		}
		return null;
	}
	
	public static Long indexVisitorFace(FacilioModule faceModule, String awsCollectionId, Long parentId, Long photoId) throws Exception {
		
		Long collectionId = getVisitorCollectionId(awsCollectionId);
		
		List<FaceRecord> faceRecords = FaceRekognitionUtil.addFacesToCollection(awsCollectionId, photoId, IOUtils.toByteArray(FileStoreFactory.getInstance().getFileStore().readFile(photoId)));
		
		if (collectionId != null) {
			if (faceRecords == null || faceRecords.isEmpty()) {
				LOGGER.log(Level.WARNING, "No faces found in the visitor photo. parentId: "+parentId+" photoId: "+photoId);
				return null;
			}
			
			String faceId = faceRecords.get(0).getFace().getFaceId();
			
			Map<String, Object> visitorFaceProp = new HashMap<String, Object>();
			visitorFaceProp.put("parentId", parentId);
			visitorFaceProp.put("photoId", photoId);
			visitorFaceProp.put("collectionId", collectionId);
			visitorFaceProp.put("faceId", faceId);
			visitorFaceProp.put("createdTime", System.currentTimeMillis());
		
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(faceModule.getTableName())
					.fields(FieldFactory.getVisitorFacesFields(faceModule));
			insertBuilder.insert(visitorFaceProp);
			return (Long) visitorFaceProp.get("id");
		}
		
		return null;
	}
	
	public static Long searchVisitorByPhoto(FacilioModule faceModule, String awsCollectionId, byte[] bytes) throws Exception {
		
		List<FaceMatch> faceMatches = FaceRekognitionUtil.searchFacesByImage(awsCollectionId, bytes);
		if (faceMatches == null || faceMatches.isEmpty()) {
			return null;
		}
		
		String faceId = faceMatches.get(0).getFace().getFaceId();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(faceModule.getTableName())
				.select(FieldFactory.getVisitorFacesFields(faceModule))
				.andCondition(CriteriaAPI.getCondition("FACE_ID", "faceId", faceId, StringOperators.IS));
				
		Map<String, Object> prop = builder.fetchFirst();
		if (prop != null) {
			return (Long) prop.get("parentId");
		}
		return null;
	}
}