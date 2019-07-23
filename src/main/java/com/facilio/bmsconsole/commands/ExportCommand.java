package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;

public class ExportCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String viewName = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		String filters = (String) context.get(FacilioConstants.ContextNames.FILTERS);
		boolean isS3Value = (boolean) context.get(FacilioConstants.ContextNames.IS_S3_VALUE);
		boolean specialFields = (boolean) context.get(FacilioConstants.ContextNames.SPECIAL_FIELDS);
		Integer viewLimit = (Integer) context.get(FacilioConstants.ContextNames.VIEW_LIMIT);
		String fileUrl = ExportUtil.exportModule(fileFormat, moduleName, viewName, filters,null, isS3Value, specialFields, viewLimit);
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		
		return false;
	}

}
