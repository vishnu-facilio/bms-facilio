package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;

public class GetExportModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String viewName = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		String filters = (String) context.get(FacilioConstants.ContextNames.FILTERS);
		FileFormat type = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		
		String fileUrl = ExportUtil.exportModule(type, moduleName, viewName, filters,null, false, false, null);
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		return false;
	}

}
