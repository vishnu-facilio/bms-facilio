package com.facilio.aws.util;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.facilio.fs.FileInfo;

public class FaceRekognitionUtil {

	private static final Logger LOGGER = LogManager.getLogger(FaceRekognitionUtil.class.getName());

	public static boolean createCollection(String collectionId) {

		long start = System.currentTimeMillis();
		
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		CreateCollectionRequest request = new CreateCollectionRequest()
				.withCollectionId(collectionId);

		CreateCollectionResult createCollectionResult = rekognitionClient.createCollection(request);
		LOGGER.log(Level.INFO, "Collection created.... cid: "+collectionId + " timetaken: "+(System.currentTimeMillis()-start));
		if (createCollectionResult != null && createCollectionResult.getStatusCode() == 200) {
			return true;
		}
		return false;
	}

	public static List<FaceRecord> addFacesToCollection(String collectionId, FileInfo imageFile) {
		
		long start = System.currentTimeMillis();
		
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		String bucketName = FacilioProperties.getConfig("s3.bucket.name");
		Image image = new Image()
				.withS3Object(new S3Object()
						.withBucket(bucketName)
						.withName(imageFile.getFilePath()));

		IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
				.withImage(image)
				.withCollectionId(collectionId)
				.withExternalImageId("Magesh")
				.withDetectionAttributes("DEFAULT");

		IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);

		LOGGER.log(Level.INFO, "Faces indexed.. collectionId: "+collectionId+" timetaken: "+(System.currentTimeMillis()-start));
		List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
		return faceRecords;
	}
	
	public static List<FaceRecord> addFacesToCollection(String collectionId, long fileId, byte[] bytes) {
		long start = System.currentTimeMillis();
		
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		Image image = new Image()
				.withBytes(ByteBuffer.wrap(bytes));

		IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
				.withImage(image)
				.withCollectionId(collectionId)
				.withExternalImageId(fileId + "")
				.withDetectionAttributes("DEFAULT");

		IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);

		LOGGER.log(Level.INFO, "Faces indexed... collectionId: "+collectionId+" timetaken: "+(System.currentTimeMillis()-start));
		List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
		return faceRecords;
	}

	public static List<FaceMatch> searchFacesByImage(String collectionId, File imageFile) throws Exception {
		return searchFacesByImage(collectionId, FileUtils.readFileToByteArray(imageFile));
	}
	
	public static List<FaceMatch> searchFacesByImage(String collectionId, byte[] bytes) {
		long start = System.currentTimeMillis();
		
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		Image image = new Image()
				.withBytes(ByteBuffer.wrap(bytes));

		// Search collection for faces similar to the largest face in the image.
		SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
				.withCollectionId(collectionId)
				.withImage(image)
				.withFaceMatchThreshold(70F)
				.withMaxFaces(2);

		SearchFacesByImageResult searchFacesByImageResult = 
				rekognitionClient.searchFacesByImage(searchFacesByImageRequest);

		List<FaceMatch> faceImageMatches = searchFacesByImageResult.getFaceMatches();
		
		LOGGER.log(Level.INFO, "Faces matched for search... collectionId: "+collectionId+" timetaken: "+(System.currentTimeMillis()-start));
		return faceImageMatches;
	}
}