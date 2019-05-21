package com.facilio.bmsconsole.commands;

import com.facilio.modules.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class SetOrderByCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext)context;
		JSONArray orderByCols = repContext.getOrderByCols();
		String orderType = repContext.getOrderType();
		String orderByQuery = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		StringBuilder thisOrderBy =  null;
		if(orderByQuery != null && !orderByQuery.isEmpty()) {
			thisOrderBy = new StringBuilder(orderByQuery);
		}
		else {
			thisOrderBy = new StringBuilder();
		}
		if(orderByCols!=null && !orderByCols.isEmpty()) {
			ArrayList<FacilioField> selectFields = (ArrayList<FacilioField>) repContext.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			ArrayList<String> selectFieldNames = (ArrayList<String>) repContext.get(FacilioConstants.ContextNames.FIELD_NAME_LIST);
			if(selectFields==null) {
				selectFields = new ArrayList<FacilioField>();
				selectFieldNames = new ArrayList<String>();
				repContext.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST,selectFields);
				repContext.put(FacilioConstants.ContextNames.FIELD_NAME_LIST,selectFieldNames);
			}
			BeanFactory.lookup("ModuleBean");
			StringBuilder orderByStr = new StringBuilder();
			for(int i=0;i<orderByCols.size();i++) {
				JSONObject thisOrderByCol = (JSONObject) orderByCols.get(i);
				String fieldAlias = LoadSelectFieldsCommand.addToFieldList(thisOrderByCol, selectFieldNames, selectFields);
				if(fieldAlias!=null && !fieldAlias.isEmpty()) {
					if(orderByStr.length()>0) {
						orderByStr.append(",");
					}
					orderByStr.append(fieldAlias);
				}
			}
			if(orderByStr.length()>0) {
				if(thisOrderBy.length()>0) {
					thisOrderBy.append(",");
				}
				thisOrderBy.append(orderByStr.toString());
			}
		}
		if(thisOrderBy.length()>0) {
			if(orderType!=null && !orderType.isEmpty()) {
				thisOrderBy.append(" " + orderType);
			}
			context.put(FacilioConstants.ContextNames.SORTING_QUERY, thisOrderBy.toString());	
		}
		
		return false;
	}
}


