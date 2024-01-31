package com.facilio.ocr.aws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.fs.FileInfo;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.DocumentLocation;
import software.amazon.awssdk.services.textract.model.FeatureType;
import software.amazon.awssdk.services.textract.model.GetDocumentAnalysisRequest;
import software.amazon.awssdk.services.textract.model.GetDocumentAnalysisResponse;
import software.amazon.awssdk.services.textract.model.S3Object;
import software.amazon.awssdk.services.textract.model.StartDocumentAnalysisRequest;
import software.amazon.awssdk.services.textract.model.StartDocumentAnalysisResponse;
import software.amazon.awssdk.services.textract.model.TextractException;

public class TextractUtil {

	private static final String LOCAL_BUCKET_NAME = "faclocaltextractbucket";

	public static void main(String[] args) {

		String docName = "Dewa1.pdf";
        
        uploadFileToS3Bucket("/Users/krishna/Downloads/Dewa1.pdf", docName);

        String jobId = startDocAnalysisS3( LOCAL_BUCKET_NAME, docName);
        System.out.println("Getting results for job " + jobId);
        Map<String, Object> result = getJobResults(jobId);
        System.out.println("The job status is " + result.get("status"));
    }
	
	
	public static Map<String, Object> getParsedBillContext(Long facilioFileId) throws Exception {
    	
    	FileStore fs = FacilioFactory.getFileStore();
    	
    	FileInfo fileInfo = fs.getFileInfo(facilioFileId);
        String fileName = fileInfo.getFileName();
        String filePath = fileInfo.getFilePath();
    	
        TextractUtil.uploadFileToS3Bucket(filePath, fileName);
        return TextractUtil.getParsedBill(fileName);
    }
	
	public static Map<Integer, Map<String, String>> getKeyValueSearchMap(List<TextractContext.FormContext> keyValuePair) throws Exception {
		return TextractExtractor.KeyValueExtractor.getKeyValuePairRelationshipAsMap(keyValuePair);
	}
	
	public static Map<String, Map<Integer, Map<Integer, String>>> geTableValueSearchMap(List<TextractContext.TableContext> tableList) throws Exception {
		return TextractExtractor.TableValueExtractor.getTableResultAsMap(tableList);
	}

    private static  Map<String, Object> getParsedBill(String docName){

        String jobId = startDocAnalysisS3( LOCAL_BUCKET_NAME, docName);
        System.out.println("Getting results for job " + jobId);

        return getJobResults(jobId);
    }

    private static void uploadFileToS3Bucket(String filePath, String fileName){
        try {
            Region region = Region.US_WEST_2;

            S3Client s3Client = S3Client.builder()
                    .region(region)
                    .build();

            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(LOCAL_BUCKET_NAME)
                    .key(fileName)
                    .build();

            PutObjectResponse putObjectResult = s3Client.putObject(putOb, RequestBody.fromFile(new File(filePath)));
            System.out.println(putObjectResult);
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String startDocAnalysisS3(String bucketName, String docName) {
        try {
        	
        	TextractClient textractClient = TextractClient.builder().region(Region.US_WEST_2).build();
        	
            List<FeatureType> myList = new ArrayList<>();
            myList.add(FeatureType.TABLES);
            myList.add(FeatureType.FORMS);

            S3Object s3Object = S3Object.builder()
                .bucket(bucketName)
                .name(docName)
                .build();

            DocumentLocation location = DocumentLocation.builder()
                .s3Object(s3Object)
                .build();

            StartDocumentAnalysisRequest documentAnalysisRequest = StartDocumentAnalysisRequest.builder()
                .documentLocation(location)
                .featureTypes(myList)
                .build();

            StartDocumentAnalysisResponse response = textractClient.startDocumentAnalysis(documentAnalysisRequest);

            // Get the job ID
            String jobId = response.jobId();
            return jobId;

        } catch (TextractException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    private static Map<String, Object> getJobResults(String jobId) {
        boolean finished = false;
        int index = 0;
        String status = "";

        try {
            TextractClient textractClient = TextractClient.builder().region(Region.US_WEST_2).build();
            Map<String, Object> result = new HashMap<>();
            while (!finished) {
                GetDocumentAnalysisRequest analysisRequest = GetDocumentAnalysisRequest.builder()
                    .jobId(jobId)
                    .maxResults(1000)
                    .build();

                GetDocumentAnalysisResponse response = textractClient.getDocumentAnalysis(analysisRequest);
                status = response.jobStatus().toString();

                if (status.compareTo("SUCCEEDED") == 0) {
                    finished = true;

                    System.out.println(response.responseMetadata());

                    List<TextractContext.FormContext> keyValuePair = TextractExtractor.KeyValueExtractor.getKeyValues(response.blocks());
                    List<TextractContext.TableContext> tableResult = TextractExtractor.TableValueExtractor.getTableResults(response.blocks());

                    result.put("forms",keyValuePair);
                    result.put("tables",tableResult);
                    result.put("rawText", TextractExtractor.RawTextExtractor.getRawText(response.blocks()));
                }
                else {
                    System.out.println(index + " status is: " + status);
                    Thread.sleep(1000);
                }
                index++;
            }
            result.put("status",status);
            return result;

        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
