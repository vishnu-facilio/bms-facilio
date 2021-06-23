/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.util.ExportPointsDataAPI;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.modules.ModuleFactory;

/**
 * @author facilio
 *
 */
public class ExportPointsCommand extends FacilioCommand {
	
	/* (non-Javadoc)
	 * @see org.apache.commons.chain.Command#execute(org.apache.commons.chain.Context)
	 */
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Boolean isS3Url = (Boolean) context.get("isS3Url");
		if (isS3Url == null) {
			isS3Url = false;
		}
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		String fileUrl = null;

		if(fileFormat != FileFormat.PDF && fileFormat != FileFormat.IMAGE) {
			List<String> headers = new ArrayList<>();

			String name = ModuleFactory.getPointsModule().getDisplayName();
			List<Map<String ,Object>> records= ExportPointsDataAPI.getPointsData(controllerId);
			if(!CollectionUtils.isNotEmpty(records)) {
				throw new IllegalArgumentException("##Select record data is empty in ExportPoints..  ");
			}else {
				Map<String,Object> table = ExportPointsDataAPI.getTableData(headers ,records);
				fileUrl = ExportUtil.exportData(fileFormat, name, table, false);
			}
			
		}
		
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		
		return false;
	}

}
