package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.AlarmContext.AlarmStatus;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetSiteReportCards implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		long campusId = (long) context.get(FacilioConstants.ContextNames.ID);

		if(campusId > 0) {
			
			JSONObject reports = new JSONObject();
			reports.put("independent_spaces", getIndependentSpaces(campusId));
			
			JSONObject woCount = new JSONObject();
			woCount.put("type", "count");
			woCount.put("name", "work_orders");
			woCount.put("label", "Work Orders");
			woCount.put("data", getWorkOrdersCount(campusId));
			
			JSONObject faCount = new JSONObject();
			faCount.put("type", "count");
			faCount.put("name", "fire_alarms");
			faCount.put("label", "Fire Alarms");
			faCount.put("data", getFireAlarms(campusId));
			
			JSONArray reportCards = new JSONArray();
			reportCards.add(woCount);
			reportCards.add(faCount);
			
			context.put(FacilioConstants.ContextNames.REPORTS, reports);
			context.put(FacilioConstants.ContextNames.REPORT_CARDS, reportCards);
		}
		else {
			throw new IllegalArgumentException("Invalid Campus ID : "+campusId);
		}

		return false;
	}

	private long getIndependentSpaces (long siteId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);

		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Site")
				.innerJoin("BaseSpace")
				.on("Site.ID = BaseSpace.ID")
				.andCustomWhere("Site.ORGID=? AND BaseSpace.ORGID = ? AND BaseSpace.SITE_ID = ? AND BaseSpace.BUILDING_ID = -1 AND BaseSpace.FLOOR_ID = -1", orgId, orgId, siteId);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return (Long) rs.get(0).get("count");
		}
	}

	private long getWorkOrdersCount (long siteId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		FacilioField spaceIdFld = new FacilioField();
		spaceIdFld.setName("space_id");
		spaceIdFld.setColumnName("SPACE_ID");
		spaceIdFld.setModule(ModuleFactory.getTicketsModule());
		spaceIdFld.setDataType(FieldType.NUMBER);

		Condition spaceCond = new Condition();
		spaceCond.setField(spaceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(siteId+"");

		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrders")
				.innerJoin("Tickets")
				.on("WorkOrders.ID = Tickets.ID")
				.innerJoin("TicketStatus")
				.on("Tickets.STATUS_ID = TicketStatus.ID")
				.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.ORGID = ? AND TicketStatus.STATUS_TYPE = ?", orgId, orgId, orgId, TicketStatusContext.StatusType.OPEN.getIntVal())
				.andCondition(spaceCond);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return (Long) rs.get(0).get("count");
		}
	}
	
	private long getFireAlarms (long siteId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		FacilioField spaceIdFld = new FacilioField();
		spaceIdFld.setName("space_id");
		spaceIdFld.setColumnName("SPACE_ID");
		spaceIdFld.setModule(ModuleFactory.getTicketsModule());
		spaceIdFld.setDataType(FieldType.NUMBER);

		Condition spaceCond = new Condition();
		spaceCond.setField(spaceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(siteId+"");

		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.andCustomWhere("Alarms.ORGID=? AND Tickets.ORGID = ? AND Alarms.STATUS=?", orgId, orgId, AlarmStatus.ACTIVE.getIntVal())
				.andCondition(spaceCond);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return (Long) rs.get(0).get("count");
		}
	}
}
