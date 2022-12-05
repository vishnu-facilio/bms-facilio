package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

import io.jsonwebtoken.lang.Collections;

public class CreateBulkActionRecordsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Map<String,Object>> propsList = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.MODULE_DATA_LIST);
		
		String dataModuleName = (String) context.get(FacilioConstants.ContextNames.DATA_MODULE_NAME);
		if(!Collections.isEmpty(propsList)) {
			V3Util.createRecordList(Constants.getModBean().getModule(dataModuleName), propsList, null, null);
		}
		context.put(FacilioConstants.ContextNames.ROWS_ADDED, propsList == null ? 0 : propsList.size());
		return false;
	}

}
