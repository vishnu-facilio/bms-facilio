package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetExportModuleCommand implements Command  {

	@Override
	public boolean execute(Context context) throws Exception {
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
