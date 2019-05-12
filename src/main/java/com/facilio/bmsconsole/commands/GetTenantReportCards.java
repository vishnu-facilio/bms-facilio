package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FieldType;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetTenantReportCards implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		long tenantId = (long) context.get(FacilioConstants.ContextNames.ID);
		long zoneId = (long) context.get(FacilioConstants.ContextNames.ZONE_ID);

		if(tenantId > 0) {
			
			JSONObject reports = new JSONObject();
			reports.put("independent_spaces", getIndependentSpaces(tenantId));
			
			List<Map<String,Object>> utilityAssets = TenantsAPI.getUtilityAssetsCount(tenantId);
			
			JSONObject assetCount = new JSONObject();
			assetCount.put("type", "count");
			assetCount.put("name", "assets");
			assetCount.put("label", "Assets");
			assetCount.put("result",utilityAssets );
			assetCount.put("data",utilityAssets.size() );
			
			List<Long> assetIdList = new ArrayList<Long>();
			
			for(int i=0;i<utilityAssets.size();i++) {
			  Map<String,Object> asset = utilityAssets.get(i);
			  Long assetId = (Long) asset.get("assetId");
			  assetIdList.add(assetId);
			}
			JSONObject woCount = new JSONObject();
			woCount.put("type", "count");
			woCount.put("name", "work_orders");
			woCount.put("label", "Work Orders");
			List<WorkOrderContext> list = TenantsAPI.getWorkOrdersCount(tenantId);
			woCount.put("result",list);
			woCount.put("data",list.size());
			
			JSONObject pmCount = new JSONObject();
			pmCount.put("type", "count");
			pmCount.put("name", "pm");
			pmCount.put("label", "Planned Maintenance");
			List pmList = TenantsAPI.getPmCount(tenantId);
			pmCount.put("result",pmList);
			pmCount.put("data",pmList.size());
			
			
			JSONObject faCount = new JSONObject();
			faCount.put("type", "count");
			faCount.put("name", "fire_alarms");
			faCount.put("label", "Alarms");
			List<Map<String,Object>> alarmList = TenantsAPI.getFireAlarmsCount(assetIdList);
			faCount.put("result", alarmList);
			faCount.put("data",alarmList.size()); 
			
			List<Long> idList = new ArrayList<Long>();
			idList.add(zoneId);
			
			List<BaseSpaceContext> zoneSpaces = SpaceAPI.getZoneChildren(idList);
			JSONObject spaceCount= new JSONObject();
			spaceCount.put("type", "count");
			spaceCount.put("name", "spaces");
			spaceCount.put("label", "Spaces");
			spaceCount.put("data", zoneSpaces.size());
			spaceCount.put("result", zoneSpaces);
			
			
			JSONArray reportCards = new JSONArray();
			reportCards.add(woCount);
			reportCards.add(faCount);
			reportCards.add(assetCount);
			reportCards.add(spaceCount);
			reportCards.add(pmCount);
			
			context.put(FacilioConstants.ContextNames.REPORTS, reports);
			context.put(FacilioConstants.ContextNames.REPORT_CARDS, reportCards);
		}
		else {
			throw new IllegalArgumentException("Invalid Tenant ID : "+tenantId);
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
