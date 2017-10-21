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
		if (sortObj != null && !sortObj.isEmpty()) {

			String orderByColName = (String) sortObj.get("orderBy");
			String orderType = (String) sortObj.get("orderType");
			
			String orderBy = null;
			for (FacilioField field : fields) {
				if (orderByColName.equalsIgnoreCase(field.getName())) {
					orderBy = field.getColumnName();
				}
			}
			orderType = ("asc".equalsIgnoreCase(orderType) || "desc".equalsIgnoreCase(orderType)) ? orderType.toLowerCase() : "desc";
			
			if (orderBy != null) {
				String orderByQuery = orderBy + " " + orderType;
				context.put(FacilioConstants.ContextNames.SORTING_QUERY, orderByQuery);	
			}
		}
		return false;
	}

}
