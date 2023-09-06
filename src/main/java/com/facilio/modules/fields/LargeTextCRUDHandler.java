package com.facilio.modules.fields;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.amazonaws.util.IOUtils;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;

@Log4j
public class LargeTextCRUDHandler extends BaseSingleRelRecordCRUDHandler<String> {

    private static final String FILE_ID_FIELD_NAME = "fileId";
	private static final int LARGE_TEXT_MAX_SIZE = 65536;

	public LargeTextCRUDHandler(LargeTextField largeTextField) {
		// TODO Auto-generated constructor stub
		super(largeTextField);
	}

	@Override
	protected FacilioModule getRelModule() {
		// TODO Auto-generated method stub
		return ((LargeTextField)getField()).getRelModule();
	}

	@Override
	protected void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, List<FacilioField> relFields) throws Exception {
		List<Map<String, Object>> props = relBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
        	for(Map<String, Object> record : props) {
        		Long recordId = (Long) ((Map<String, Object>)record.get(getParentFieldName())).get("id");
                Long fileId = (Long) record.get(FILE_ID_FIELD_NAME);
                FileStore fs = FacilioFactory.getFileStore();
                
                LOGGER.trace("Large Text File ID : "+ fileId);
                String value = null;
                try (InputStream is = fs.readFile(fileId)) {
                	if(is!=null) {
                		value = IOUtils.toString(is);
                	}
    	        }
                addToRecordMap(recordId, value);
        	}
        }
		
	}

    @Override
    protected boolean isNull(String value) throws Exception {
        return StringUtils.isBlank(value);
    }

	private static long lastLoggedTime = 0;
	private static int BUFFER_INTERVAL = 1800000;
    protected Map<String, Object> createRelRecord(long parentId, String fileContent) throws Exception {
		if(!(((LargeTextField)getField()).getSkipSizeCheck()) && fileContent.length() > LARGE_TEXT_MAX_SIZE) {
			throw new Exception("large text content is greater that max size "+LARGE_TEXT_MAX_SIZE);
		}

        Map<String, Object> relRecord = new HashMap<>();
		String fileName = "largeTextFile_"+System.currentTimeMillis()+".txt";
        File newFile = File.createTempFile(fileName,null);
		try(FileOutputStream fileOut = new FileOutputStream(newFile);) {
			fileOut.write(fileContent.getBytes());
		}
		FileStore fs = FacilioFactory.getFileStore();
		long fileID = fs.addFile(fileName, newFile, "text");

		try {
			long startTime = System.currentTimeMillis();
			newFile.delete();
			long currentTime = System.currentTimeMillis();
			long timeTakenToDeleteTempFile = currentTime - startTime;
			if ( (currentTime - lastLoggedTime) > BUFFER_INTERVAL ) { // Printing this log only at specified intervals to understand how costly is this
				lastLoggedTime = currentTime;
				LOGGER.info("Time taken to delete temp file "+fileName+" is "+timeTakenToDeleteTempFile);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while deleting temp file. But this is not critical", e);
		}

        relRecord.put(PARENT_ID_FIELD_NAME, parentId);
        relRecord.put(FILE_ID_FIELD_NAME, fileID);
        return relRecord;
    }
}
