package com.facilio.report.customreport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportFieldContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

;

public class CustomReportCultfit1 implements CustomReport {

	@Override
	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter,
                             JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		
		FacilioStatus closedStatus = TicketAPI.getStatus("Closed");
		
		FacilioField statusFieldOrginal = fieldsMap.get("status");
		
		FacilioField statusField = statusFieldOrginal.clone();
		
		List<FacilioField> selectFields = new ArrayList<>();
		
		FacilioField countField = BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(statusField);
		countField.setName("value");
		
		ReportFieldContext reportFieldContext = new ReportFieldContext();
		reportFieldContext.setModuleField(countField);
		report.setY1AxisField(reportFieldContext);
		
		statusField.setColumnName("IF(STATUS_ID = "+closedStatus.getId()+",'Closed','Open')");
		statusField.setModule(null);
//		statusField.setExtendedModule(null);
		
		selectFields.add(statusField);
		selectFields.add(countField);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table("Tickets")
				.innerJoin("WorkOrders")
				.on("Tickets.ID = WorkOrders.ID")
				.andCustomWhere("Tickets.ORGID = ?",AccountUtil.getCurrentOrg().getId())
				.groupBy("STATUS_ID");
				;
				
		if(report.getDateFilter() != null) {
			Condition dateCondition = DashboardUtil.getDateCondition(report, dateFilter, module);
			builder.andCondition(dateCondition);
		}
		
		List<Map<String, Object>> props = builder.get();
		
		int openCount = 0;
		int closedCount = 0;
		for(Map<String, Object> prop :props) {
			
			String statusString = (String) prop.get("status");
			
			if(statusString.equals("Open")) {
				openCount += Integer.parseInt(prop.get("value").toString());
			}
			
			if(statusString.equals("Closed")) {
				closedCount += Integer.parseInt(prop.get("value").toString());
			}
		}
		
		JSONArray ticketData = new JSONArray();
		
		JSONObject res = new JSONObject();
		
		res.put("label", "Open");
		res.put("value", openCount);
		
		ticketData.add(res);
		
		res = new JSONObject();
		
		res.put("label", "Closed");
		res.put("value", closedCount);
		
		ticketData.add(res);
		
		statusField.setDataType(FieldType.STRING);
		report.getxAxisField().setModuleField(statusField);
		
		return ticketData;
	}

}
