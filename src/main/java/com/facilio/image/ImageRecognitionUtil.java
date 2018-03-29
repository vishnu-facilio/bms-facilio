package com.facilio.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fs.S3FileStore;

public class ImageRecognitionUtil {
	public static final float MINIMUM_CONFIDENCE = 70;
	
	public static List<TextDetection> getTexts (File image) throws IOException {
		try(FileInputStream imageStream = new FileInputStream(image)) {
			byte[] imageBytes = IOUtils.toByteArray(imageStream);
			return getText(new Image()
							.withBytes(ByteBuffer.wrap(imageBytes)));
		}
	}
	
	public static List<TextDetection> getTexts (long fileId) throws Exception {
		String environment = AwsUtil.getConfig("environment");
		if (!"development".equalsIgnoreCase(environment)) {
			User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
			S3FileStore fileStore = (S3FileStore) FileStoreFactory.getInstance().getFileStore(superAdmin.getId());
			FileInfo imageInfo = fileStore.getFileInfo(fileId);
			return getText(new Image()
							.withS3Object(new S3Object()
							.withName(imageInfo.getFilePath())
							.withBucket(AwsUtil.getConfig("s3.bucket.name"))));
		}
		return null;
	}
	
	private static List<TextDetection> getText(Image image) {
		DetectTextRequest request = new DetectTextRequest().withImage(image);
		DetectTextResult result = AwsUtil.getAmazonRekognitionClient().detectText(request);
		return result.getTextDetections();
	}
}
