package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class GenerateGrpByQueryCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub	
		FacilioReportContext repContext = (FacilioReportContext)context;
		JSONArray groupByCols = repContext.getGroupByCols();
		if(groupByCols!=null && !groupByCols.isEmpty()) {
			ArrayList<FacilioField> selectFields = (ArrayList<FacilioField>) repContext.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			ArrayList<String> selectFieldNames = (ArrayList<String>) repContext.get(FacilioConstants.ContextNames.FIELD_NAME_LIST);
			if(selectFields==null) {
				selectFields = new ArrayList<FacilioField>();
				selectFieldNames = new ArrayList<String>();
				repContext.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST,selectFields);
				repContext.put(FacilioConstants.ContextNames.FIELD_NAME_LIST,selectFieldNames);
			}
			BeanFactory.lookup("ModuleBean");
			StringBuilder groupByStr = new StringBuilder();
			for(int i=0;i<groupByCols.size();i++) {
				JSONObject thisGroupBy = (JSONObject) groupByCols.get(i);
				String fieldAlias = LoadSelectFieldsCommand.addToFieldList(thisGroupBy, selectFieldNames, selectFields);
				if(fieldAlias!=null && !fieldAlias.isEmpty()) {
					if(groupByStr.length()>0) {
						groupByStr.append(",");
					}
					groupByStr.append(fieldAlias);
				}
			}
			if(groupByStr.length()>0) {
				repContext.put(FacilioConstants.Reports.GROUPBY,groupByStr.toString());
			}
		}
		return false;
	}
}
