package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.modules.FacilioStatus;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetAssetReportCards implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		long assetId = (long) context.get(FacilioConstants.ContextNames.ID);

		if(assetId > 0) {
			
			JSONObject reports = new JSONObject();
			
			JSONObject woCount = new JSONObject();
			woCount.put("type", "count");
			woCount.put("name", "work_orders");
			woCount.put("label", "Work Orders");
			woCount.put("data", getWorkOrdersCount(assetId));
			
			JSONObject faCount = new JSONObject();
			faCount.put("type", "count");
			faCount.put("name", "fire_alarms");
			faCount.put("label", "Alarms");
			faCount.put("data", getFireAlarmsCount(assetId));
			
			JSONObject pmCount = new JSONObject();
			pmCount.put("type", "count");
			pmCount.put("name", "pm");
			pmCount.put("label", "Preventive Maintenance");
			pmCount.put("data", getPMCount(assetId));
			
			JSONArray reportCards = new JSONArray();
			reportCards.add(woCount);
			reportCards.add(faCount);
			reportCards.add(pmCount);
			
			context.put(FacilioConstants.ContextNames.REPORTS, reports);
			context.put(FacilioConstants.ContextNames.REPORT_CARDS, reportCards);
		}
		else {
			throw new IllegalArgumentException("Invalid Asset ID : "+assetId);
		}

		return false;
	}
	
	public static long getWorkOrdersCount(long assetId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		FacilioField assetIdFld = new FacilioField();
		assetIdFld.setName("asset_id");
		assetIdFld.setColumnName("RESOURCE_ID");
		assetIdFld.setModule(ModuleFactory.getTicketsModule());
		assetIdFld.setDataType(FieldType.NUMBER);

		Condition assetCond = new Condition();
		assetCond.setField(assetIdFld);
		assetCond.setOperator(NumberOperators.EQUALS);
		assetCond.setValue(assetId+"");

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrders")
				.innerJoin("Tickets")
				.on("WorkOrders.ID = Tickets.ID")
				.innerJoin("TicketStatus")
				.on("Tickets.STATUS_ID = TicketStatus.ID")
				.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.ORGID = ? AND TicketStatus.STATUS_TYPE = ?", orgId, orgId, orgId, FacilioStatus.StatusType.OPEN.getIntVal())
				.andCondition(assetCond);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
		}
	}
	
	public static long getFireAlarmsCount(long assetId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		FacilioField assetIdFld = new FacilioField();
		assetIdFld.setName("asset_id");
		assetIdFld.setColumnName("RESOURCE_ID");
		assetIdFld.setModule(ModuleFactory.getTicketsModule());
		assetIdFld.setDataType(FieldType.NUMBER);

		Condition assetCond = new Condition();
		assetCond.setField(assetIdFld);
		assetCond.setOperator(NumberOperators.EQUALS);
		assetCond.setValue(assetId+"");
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.andCondition(ViewFactory.getAlarmSeverityCondition(FacilioConstants.Alarm.CLEAR_SEVERITY, false))
				.andCondition(assetCond);  
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
		}
	}
	
	private static long getPMCount(long assetId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkOrderTemplateFields());
		FacilioField assetField = fieldMap.get("resourceId");
		Condition condition = CriteriaAPI.getCondition(assetField, String.valueOf(assetId), NumberOperators.EQUALS);
		return PreventiveMaintenanceAPI.getPMCount(Collections.singletonList(assetField), Collections.singletonList(condition));
		
	}
}
