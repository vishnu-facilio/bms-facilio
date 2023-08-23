package com.facilio.wmsv2.bean;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.relation.util.RelationshipDataUtil;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.weather.commands.WeatherTransactionChainFactory;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class LongTasksBeanImpl implements LongTasksBean {

    @Override
    public void addBulkWeatherStationMigration(JSONObject data) throws Exception {
        FacilioChain chain = WeatherTransactionChainFactory.addWeatherStationMigratinChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        //Send email
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject json = new JSONObject();
        json.put("sender", EmailClient.getFromEmail("info"));
        json.put("to", data.get("emailAddress"));
        json.put("_tracking", false);
        json.put("subject" , String.valueOf(orgId) + " - Bulk weather station migration status");
        StringBuilder body = new StringBuilder();
        body.append("Details : \nStart Time : ").append(data.get("startTime"))
                .append("\nCompleted Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
                .append("\nStatus : ")
                .append(context.get("message"));
        json.put("message", body.toString());
        FacilioFactory.getEmailClient().sendMailWithoutTracking(json, null);
    }

	@Override
	public void populateVMData(JSONObject data) throws Exception {
		// TODO Auto-generated method stub
		
		Long vmTemplateId = FacilioUtil.parseLong(data.get(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_ID));
		
		VirtualMeterTemplateContext vmTemplate = (VirtualMeterTemplateContext) V3Util.getRecord(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE, vmTemplateId, null);
		
		RelationContext relation = RelationUtil.getRelation(vmTemplate.getRelationShipId(),true);
		
		Map<Long,V3MeterContext> vmParentMap = vmTemplate.constructParentVsVirtualMeters();
		
		for(Long parentId : vmParentMap.keySet()) {
			
			V3MeterContext vmMeter = vmParentMap.get(parentId);
			
			Map<String, Object>  meterProp = FieldUtil.getAsProperties(vmMeter);
			
			FacilioContext context = V3Util.createRecord(Constants.getModBean().getModule(FacilioConstants.Meter.METER), meterProp);
			
			List<ModuleBaseWithCustomFields> records = Constants.getRecordList(context);
			
			vmMeter.setId(records.get(0).getId());
			
			String parentModuleName = vmTemplate.getScopeEnum().getModuleName();
			String relationLinkName = relation.getMapping1().getMappingLinkName();

			Map<String, List<Object>> queryParameters = new HashMap<>();
			queryParameters.put("relationName", new ArrayList(){{add(relationLinkName);}});
			queryParameters.put("parentId", new ArrayList(){{add(parentId);}});
			
			List<Object> dataList = new ArrayList<>();
			
			dataList.add(vmMeter.getId());
			
			JSONObject dataMap = new JSONObject();
			
			dataMap.put(FacilioConstants.Meter.METER, dataList);

			RelationshipDataUtil.associateRelation(parentModuleName, dataMap, queryParameters, null);
			
		}
		
	}

}
