package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaceBudIntegrationCommand implements Command {
	
	private static final Logger logger = LogManager.getLogger(SpaceBudIntegrationCommand.class.getName());

	private static final String POST_URL = "http://13.228.175.41:8086/onboarding/eventcontroller/event";
	
	class Attribute extends JSONObject
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Attribute(String meterid,Object reading)
		{
			super.put("meter_id",meterid);
			super.put("meter_reading",reading);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (AccountUtil.getCurrentOrg().getOrgId() == 78) {
			
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");
			headers.put("access_token", "f4800a0a30b0e00484ae1f7f65f45956");
			
			JSONObject postdata = new JSONObject();
			try {
				Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
				JSONArray attributes = new JSONArray();
				if (readingMap!=null && readingMap.containsKey(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
					postdata.put("zone_id", 1);
					postdata.put("hardware_type", "ENERGY_METER");
					JSONObject eventInfo = new JSONObject();
					eventInfo.put("eventType", "ENERGY_CONSUMPTION");
				
					/**
					 *  TODO
					 * Query to get Name from parentID
					 * 
					// select r.ID,r.NAME from Energy_Meter em,Resources r where r.ORGID=78 and r.ID=em.ID  and em.IS_VIRTUAL !=1 and r.SYS_DELETED is NULL order by ID;
					*/
					List<ReadingContext> readings = readingMap.get(FacilioConstants.ContextNames.ENERGY_DATA_READING);
					for (ReadingContext reading : readings) {
						if (reading.getParentId() == 16855 && reading.getReading("totalEnergyConsumptionDelta") != null) {
						//	content.put("EM_101_EB_KWH", reading.getReading("totalEnergyConsumptionDelta"));
							attributes.add(new Attribute("5B RM1 GF1 EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16856 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 GF2 EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16857 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 1F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16858 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 2F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16859 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 3F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16860 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 4F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16861 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 5F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16862 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 6F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16863 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 7F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16865 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 8F1 EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16866 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 8F2 EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16867 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 9F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16868 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM1 10F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16869 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM2 GF1 EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16870 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM2 GF2 EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16871 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM2 1F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16872 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM2 2F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16873 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM2 3F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16874 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM2 4F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16875 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM2 5F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 16876 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("5B RM2 6F EB_EM", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2309 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_602_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2311 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_603_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2313 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_604_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2315 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_701A_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2317 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_702A_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2319 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_703_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2321 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_704_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2323 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_801_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2325 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_802_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2327 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_803_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2329 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_804_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2331 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_901B_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2333 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_902B_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2335 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_903A_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2337 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_903B_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2340 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_1001B_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2342 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_1002_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2344 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_1003A_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
						else if (reading.getParentId() == 2346 && reading.getReading("totalEnergyConsumptionDelta") != null) {
							attributes.add(new Attribute("EM_1003B_EB_KWH", reading.getReading("totalEnergyConsumptionDelta")));
						}
					

					} // end of for loop
					eventInfo.put("attributes", attributes); // List of meters for energy consumption
					JSONArray eventinfoarray = new JSONArray();
					eventinfoarray.add(eventInfo);
					postdata.put("eventInfo", eventinfoarray); // eventInfo always array of one element
					postdata.put("timestamp", System.currentTimeMillis()); 
					logger.log(Level.INFO, "SpaceBudIntegrationCommand POST content " + postdata.toJSONString());

				}
				else {
					logger.log(Level.FATAL, "SpaceBudIntegrationCommand Fails.readingMap is NULL for "+readingMap );
				}
				if (! attributes.isEmpty()) {
					String result = AwsUtil.doHttpPost(POST_URL, headers, null, postdata.toJSONString());
					logger.log(Level.INFO, "SpaceBudIntegrationCommand POST DONE " + result);
				}
			} catch (Exception e) {
				logger.log(Level.FATAL, "SpaceBudIntegrationCommand POST DONE " ,e);
			}
		}
		return false;
	}
}
