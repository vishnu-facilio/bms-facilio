package com.facilio.bmsconsole.commands;

import java.util.ArrayList;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class SetOrderByCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext)context;
		JSONArray orderByCols = repContext.getOrderByCols();
		String orderType = repContext.getOrderType() + "";
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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			StringBuilder orderByStr = new StringBuilder();
			for(int i=0;i<orderByCols.size();i++) {
				JSONObject thisOrderByCol = (JSONObject) orderByCols.get(i);
				String fieldAlias = LoadSelectFieldsCommand.addToFieldList(thisOrderByCol, selectFieldNames, selectFields);
				orderByStr.append(fieldAlias);
			}
			if(orderByStr.length()>0) {
				thisOrderBy.append(orderByStr);
			}
		}
		if(orderType.length()>0) {
			thisOrderBy.append(" " + orderType);
		}
		if(thisOrderBy.length()>0) {
			context.put(FacilioConstants.ContextNames.SORTING_QUERY, thisOrderBy.toString());	
		}
		
		return false;
	}
}


