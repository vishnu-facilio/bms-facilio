/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.io.File;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.PointsProcessContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.ImportPointsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.time.DateTimeUtil;

/**
 * @author facilio
 *
 */
public class UploadImportPointsDataCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
	FileStore fs = FileStoreFactory.getInstance().getFileStore();
		
		String fileUploadFileName = (String) context.get(FacilioConstants.ContextNames.FILE_NAME);
		File fileUpload = (File) context.get(FacilioConstants.ContextNames.FILE);
		String fileUploadContentType = (String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);
		PointsProcessContext importPointsContext = (PointsProcessContext) context.get(FacilioConstants.ContextNames.POINTS_PROCESS_CONTEXT);
		Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		PointsProcessContext processContext = new PointsProcessContext();

		long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
		
		Workbook workbook = WorkbookFactory.create(fileUpload);
		importPointsContext = ImportPointsAPI.getColumnHeadings(workbook, importPointsContext);
		
		JSONObject firstRow = ImportPointsAPI.getFirstRow(workbook);	
		workbook.close();
		
		importPointsContext.setfirstRow(firstRow);
		
		Integer importMode = (Integer) context.get(FacilioConstants.ContextNames.IMPORT_MODE);
		
		importPointsContext.setImportMode(importMode);
        
        if(controllerId != -1 && controllerId !=null) {
        	ControllerContext controllerDetails=ControllerAPI.getController(controllerId);
        	if(controllerDetails!= null) {
        		processContext.setControllerId(controllerId);
        	}
        }
        importPointsContext.setStatus(PointsProcessContext.ImportStatus.UPLOAD_COMPLETE.getValue());

        importPointsContext.setFileId(fileId);
        
        importPointsContext.setImportTime(DateTimeUtil.getCurrenTime());
        
        importPointsContext.setImportType(PointsProcessContext.ImportType.EXCEL.getValue());

        importPointsContext.setFieldMapping(ImportPointsAPI.getFieldMapping());
        
        
        ImportPointsAPI.addImportPoints(importPointsContext);
		
        ImportPointsAPI.getFieldMapping(importPointsContext);
        
        context.put(FacilioConstants.ContextNames.POINTS_PROCESS_CONTEXT, importPointsContext);
        
		return false;
	}

}
