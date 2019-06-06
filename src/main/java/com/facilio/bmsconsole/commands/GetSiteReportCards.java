package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
			woCount.put("data", SpaceAPI.getWorkOrdersCount(campusId));
			
			JSONObject faCount = new JSONObject();
			faCount.put("type", "count");
			faCount.put("name", "fire_alarms");
			faCount.put("label", "Alarms");
			faCount.put("data", SpaceAPI.getFireAlarmsCount(campusId));
			
			JSONObject assetCount = new JSONObject();
			assetCount.put("type", "count");
			assetCount.put("name", "assets");
			assetCount.put("label", "Assets");
			assetCount.put("data", SpaceAPI.getAssetsCount(campusId));
			
			JSONObject energyUsage= new JSONObject();
			energyUsage.put("type", "count");
			energyUsage.put("name", "energy");
			energyUsage.put("label", "ENERGY CONSUMED");
			energyUsage.put("data", "--");
			
			JSONArray reportCards = new JSONArray();
			reportCards.add(woCount);
			reportCards.add(faCount);
			reportCards.add(assetCount);
			reportCards.add(energyUsage);
			
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

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
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
			return ((Number) rs.get(0).get("count")).longValue();
		}
	}
}
