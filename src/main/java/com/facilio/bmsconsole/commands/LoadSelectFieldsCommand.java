package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class LoadSelectFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		FacilioReportContext repContext = (FacilioReportContext)context;
		ArrayList<FacilioField> fieldList = new ArrayList<FacilioField>();
		ArrayList<String> selectFieldNames = new ArrayList<String>();
		JSONArray xAxis = repContext.getXAxis();
		if(xAxis != null && !xAxis.isEmpty()) {
			for(int i=0;i<xAxis.size();i++) {
				JSONObject thisFieldJSON = (JSONObject)xAxis.get(i);
				addToFieldList(thisFieldJSON,selectFieldNames,fieldList);
			}
		}

		JSONArray yAxis = repContext.getYAxis();
		if(yAxis != null && !yAxis.isEmpty()) {
			for(int i=0;i<yAxis.size();i++) {
				JSONObject thisFieldJSON = (JSONObject)yAxis.get(i);
				addToFieldList(thisFieldJSON,selectFieldNames,fieldList);
			}
		}
		
		if(fieldList.isEmpty()) {
			throw new IllegalArgumentException("Report Error - No X Axis defined");
		}
		context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fieldList);
		repContext.put(FacilioConstants.ContextNames.FIELD_NAME_LIST,selectFieldNames);
		
		return false;
	}
	public static String addToFieldList(JSONObject thisFieldJSON,ArrayList<String> selectFieldNames,ArrayList<FacilioField> fieldList) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String fieldName = (String) thisFieldJSON.get(FacilioConstants.Reports.REPORT_FIELD); 
		String fieldModule = (String) thisFieldJSON.get(FacilioConstants.Reports.FIELD_MODULE); 
		String fieldAlias = (String) thisFieldJSON.get(FacilioConstants.Reports.FIELD_ALIAS); 
		String fieldAggregation = (String) thisFieldJSON.get(FacilioConstants.Reports.AGG_FUNC); 
		FacilioField thisField = null;
		if(!selectFieldNames.contains(fieldAlias)) { //CHECK THIS
			if(fieldName.equals(FacilioConstants.Reports.ALL_COLUMN)) {
				thisField = new FacilioField();
				thisField.setColumnName("*");
				thisField.setDataType(FieldType.NUMBER);
			}
			else if(fieldName.equals(FacilioConstants.ContextNames.ID)) {
				FacilioModule thisModule = modBean.getModule(fieldModule);
				thisField = FieldFactory.getIdField(thisModule);
			}
			else if(fieldName.equals(FacilioConstants.Ticket.DUE_STATUS)) {
				FacilioField dueDate = modBean.getField(FacilioConstants.Ticket.DUE_DATE, fieldModule);
				FacilioField actualWorkEnd = modBean.getField(FacilioConstants.Ticket.ACTUAL_WORK_END, fieldModule);
				thisField = new FacilioField();
				if(dueDate!=null && actualWorkEnd!=null) {
					String columnString = "IF(("+dueDate.getColumnName()+"-"+actualWorkEnd.getColumnName()+")>=0, \""+FacilioConstants.Ticket.ON_TIME+"\",\""+FacilioConstants.Ticket.OVERDUE+"\")";
					thisField.setColumnName(columnString);
					thisField.setDataType(dueDate.getDataType());
				}
			}
			else if(fieldName.equals(FacilioConstants.Ticket.RESOLUTION_TIME)) {
				FacilioField actualWorkStart = modBean.getField(FacilioConstants.Ticket.ACTUAL_WORK_START, fieldModule);
				FacilioField actualWorkEnd = modBean.getField(FacilioConstants.Ticket.ACTUAL_WORK_END, fieldModule);
				thisField = new FacilioField();
				if(actualWorkStart!=null && actualWorkEnd!=null) {
					String columnString = actualWorkEnd.getColumnName()+"-"+actualWorkStart.getColumnName();
					thisField.setColumnName(columnString);
					thisField.setDataType(actualWorkStart.getDataType());
				}
			}
			else if(fieldName.equals(FacilioConstants.Ticket.FIRST_ACTION_TIME)) {
				FacilioField actualWorkStart = modBean.getField(FacilioConstants.Ticket.ACTUAL_WORK_START, fieldModule);
				FacilioField createdTime = modBean.getField(FacilioConstants.Ticket.CREATED_TIME, fieldModule);
				thisField = new FacilioField();
				if(actualWorkStart!=null && createdTime!=null) {
					String columnString = actualWorkStart.getColumnName()+"-"+createdTime.getColumnName();
					thisField.setColumnName(columnString);
					thisField.setDataType(actualWorkStart.getDataType());
				}
			}
			else {
				thisField = modBean.getField(fieldName, fieldModule);
			}
			if(thisField!=null) {
				thisField.setName(fieldAlias);
				if(fieldAggregation!=null && !fieldAggregation.isEmpty()) {
					setAggregationColumn(fieldAggregation, thisField);
				}
				fieldList.add(thisField);
				selectFieldNames.add(fieldAlias);
			}
		}
		return fieldAlias;
	}
	private static void setAggregationColumn(String aggFunc, FacilioField thisField) {
		String aggrColStr = "";
		if(aggFunc.equals(FacilioConstants.Reports.UNIQUE_COLUMN)) {
			aggrColStr = aggFunc + " " + thisField.getColumnName();
		}
		else if(aggFunc.equals(FacilioConstants.Reports.DAILY) || aggFunc.equals(FacilioConstants.Reports.HOURLY) || aggFunc.equals(FacilioConstants.Reports.WEEKLY) || aggFunc.equals(FacilioConstants.Reports.MONTHLY)) {
			String columnName = thisField.getColumnName();
			String conversion = "";
			switch(aggFunc) {
			case FacilioConstants.Reports.DAILY:
				conversion = "(1000*60*60*24)";
				break;
			case FacilioConstants.Reports.HOURLY:
				conversion = "(1000*60*60)";
				break;
			case FacilioConstants.Reports.WEEKLY:
				conversion = "(1000*60*60*24*7)";
				break;
			}
			if(conversion.length()>0) {
				aggrColStr = "FLOOR("+columnName+"/"+conversion+")";
			}
		}
		else {
			aggrColStr = aggFunc + "(" + thisField.getColumnName() + ")";
		}
		if(aggrColStr.length()>0) {
			thisField.setColumnName(aggrColStr);
			thisField.setModule(null);
//			thisField.setExtendedModule(null);
		}
	}
}
