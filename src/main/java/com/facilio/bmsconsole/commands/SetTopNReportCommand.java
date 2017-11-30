package com.facilio.bmsconsole.commands;

import java.util.ArrayList;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class SetTopNReportCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext)context;
		String topNString = (String) repContext.get(FacilioConstants.Reports.TOP_N_DATA);
		if(topNString!=null && !topNString.isEmpty()) {
			String[] topNArr = topNString.split(":");
			if(topNArr.length==3) {
				if(topNArr[0]!=null && topNArr[0].equals(FacilioConstants.Reports.TOP_N)) {
					repContext.setOrderType("desc");
				}
				else if(topNArr[0]!=null && topNArr[0].equals(FacilioConstants.Reports.BOTTOM_N)) {
					repContext.setOrderType("asc");
				}
				if(topNArr[1]!=null && !topNArr[1].isEmpty()) {
					int limit = Integer.parseInt(topNArr[1]);
					repContext.setLimit(limit);
				}
				if(topNArr[2]!=null && !topNArr[2].isEmpty()) {
					repContext.put(FacilioConstants.ContextNames.SORTING_QUERY, topNArr[2]);	
				}
			}
		}
		
		return false;
	}
}

