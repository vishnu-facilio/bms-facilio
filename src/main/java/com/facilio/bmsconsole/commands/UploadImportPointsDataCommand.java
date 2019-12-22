/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.actions.PointsProcessContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.ImportPointsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;

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
