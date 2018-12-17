package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;

public class GetExportModuleCommand implements Command  {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String viewName = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		FileFormat type = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		
		String fileUrl = ExportUtil.exportModule(type, moduleName, viewName);
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		return false;
	}

}
