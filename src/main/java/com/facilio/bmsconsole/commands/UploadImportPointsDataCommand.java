/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.facilio.bmsconsole.actions.PointsProcessContext;
import com.facilio.bmsconsole.util.ImportPointsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;

/**
 * @author facilio
 *
 */
public class UploadImportPointsDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FileStore fs = FacilioFactory.getFileStore();

		String fileUploadFileName = (String) context.get(FacilioConstants.ContextNames.FILE_NAME);
		File fileUpload = (File) context.get(FacilioConstants.ContextNames.FILE);
		String fileUploadContentType = (String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);
		PointsProcessContext importPointsContext = (PointsProcessContext) context
				.get(FacilioConstants.ContextNames.POINTS_PROCESS_CONTEXT);
		Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		PointsProcessContext processContext = new PointsProcessContext();

		long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);

		Workbook workbook = WorkbookFactory.create(fileUpload);
		importPointsContext = ImportPointsAPI.getColumnHeadings(workbook, importPointsContext);
		List<Map<String, Object>> firstRow = ImportPointsAPI.getFirstRow(workbook);
		workbook.close();

//		FacilioContext importContext = new FacilioContext();
		context.put("IMPORT_POINTS_DATA", firstRow);
		context.put("CONTROLLER_ID", controllerId);

		return false;
	}

}
