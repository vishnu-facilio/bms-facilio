package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;

public class GenerateSortingQueryCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject sortObj = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		StringBuilder orderBy = new StringBuilder();
		if (sortObj != null && !sortObj.isEmpty()) {

			String orderByColName = (String) sortObj.get("orderBy");
			String orderType = (String) sortObj.get("orderType");
			String[] orderByStr = orderByColName.split(",");
			for(int i=0;i<orderByStr.length;i++) {
				for (FacilioField field : fields) {
					if (orderByStr[i].equalsIgnoreCase(field.getName())) {
						String thisField = field.getColumnName();
						orderBy.append(thisField);
					}
				}
			}
			orderType = ("asc".equalsIgnoreCase(orderType) || "desc".equalsIgnoreCase(orderType)) ? orderType.toLowerCase() : "desc";
			
			if (orderBy.length()>0) {
				String orderByQuery = orderBy + " " + orderType;
				context.put(FacilioConstants.ContextNames.SORTING_QUERY, orderByQuery);	
			}
		}
		return false;
	}

}
