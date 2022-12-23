package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.ConsumptionAPI;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.*;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.CONSUMPTION_FUNCTION)
public class FacilioConsumptionFunctions {
	public Object getEnergyDataByBuilding(Map<String, Object> globalParam, Object... objects) throws Exception {

		Map<String,Object> totalConsumptionByBuilding = ConsumptionAPI.getTotalConsumptionBySites(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), "energydata","totalEnergyConsumptionDelta");

		List<Map<String,Object>> consumption_resp =(List<Map<String,Object>>)totalConsumptionByBuilding.get("total_consumption");
		for(int i=0;i<consumption_resp.size();i++)
		{
			Map<String,Object> consumptionMap = consumption_resp.get(i);
			Long energyVal = (Long)consumptionMap.get("energyConsumption");
			if(energyVal<0)
			{
				consumptionMap.put("energyConsumption","--");
				consumptionMap.put("energyUnit","");
			}
			else if(energyVal>1000)
			{
				consumptionMap.put("energyConsumption",energyVal*0.001);

				consumptionMap.put("energyUnit","MWh");
			}
			else
			{

				consumptionMap.put("energyUnit","kWh");

			}
			Long waterVal = (Long)consumptionMap.get("waterConsumption");
			if(waterVal<0)
			{
				consumptionMap.put("waterConsumption","--");
				consumptionMap.put("waterUnit", "");
			}
			else
			{
				consumptionMap.put("waterunit", "l");
			}

		}

		return totalConsumptionByBuilding;

	}

	public Object getWaterDataByBuilding(Map<String, Object> globalParam, Object... objects) throws Exception {

		Map<String,Object> totalConsumptionByBuilding = ConsumptionAPI.getTotalConsumptionBySites(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), "waterreading","waterConsumptionDelta");
		return totalConsumptionByBuilding;

	}

	public Object getDataBySites(Map<String, Object> globalParam, Object... objects) throws Exception {

		Map<String,Object> totalEnergyConsumptionBySites = ConsumptionAPI.getTotalConsumptionBySites(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), "energydata", "totalEnergyConsumptionDelta");
		Map<String,Object> totalWaterConsumptionBySites = ConsumptionAPI.getTotalConsumptionBySites(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), "waterreading", "waterConsumptionDelta");

		Map<String,Object> finalResp = new HashMap<String, Object>();
		finalResp.put("energyUnit",totalEnergyConsumptionBySites.get("unit"));
		finalResp.put("waterUnit",totalWaterConsumptionBySites.get("unit"));
		Map<Long,Object> energyData = (Map<Long, Object>) totalEnergyConsumptionBySites.get("totalConsumptionData");
		Map<Long,Object> waterData = (Map<Long, Object>) totalWaterConsumptionBySites.get("totalConsumptionData");

		Iterator<Map.Entry<Long, Object>> itr = energyData.entrySet().iterator();
		Map<Long, Map<String, Object>> consumptionData = new HashMap<Long, Map<String,Object>>();


		while(itr.hasNext())
		{
			Map.Entry<Long, Object> entry = itr.next();
			Map<String,Object> siteData = new HashMap<String, Object>();
			Map<String,Object> siteEnergyData = (Map<String, Object>) entry.getValue();

			siteData.put("siteId", entry.getKey());
			siteData.put("siteName", siteEnergyData.get("name"));
			siteData.put("energyConsumption", siteEnergyData.get("total_consumption"));
			if((Double)siteEnergyData.get("total_consumption")<0)
			{
				siteData.put("energyConsumption","--");
				siteData.put("energyUnit","--");
			}
			else if((Double)siteEnergyData.get("total_consumption")>1000)
			{
				siteData.put("energyConsumption",(Double)siteEnergyData.get("total_consumption")*0.001);
				siteData.put("energyUnit","MWh");

			}
			else
			{
				siteData.put("energyUnit","KWh");

			}
			Map<String,Object> siteWaterData = (Map<String,Object>)waterData.remove(entry.getKey());
			if(siteWaterData!=null)
			{
				siteData.put("waterConsumption", siteWaterData.get("total_consumption"));
				siteData.put("waterUnit","Litres");

			}
			else
			{
				siteData.put("waterConsumption","--");
				siteData.put("waterUnit","--");
			}


			consumptionData.put(entry.getKey(), siteData);

		}

		Iterator<Map.Entry<Long, Object>> itrWater = waterData.entrySet().iterator();


		while(itrWater.hasNext())
		{
			Map.Entry<Long, Object> entry = itrWater.next();
			Map<String,Object> siteData = new HashMap<String, Object>();
			Map<String,Object> siteWaterData = (Map<String, Object>) entry.getValue();

			siteData.put("siteId", entry.getKey());
			siteData.put("siteName", siteWaterData.get("name"));
			siteData.put("energyConsumption","--");
			siteData.put("energyUnit","--");

			siteData.put("waterConsumption", siteWaterData.get("total_consumption"));
			siteData.put("waterUnit", "Litres");

			if(siteWaterData.get("total_consumption")==null || ((Number)siteWaterData.get("total_consumption")).doubleValue() < 0)
			{
				siteData.put("waterConsumption","--");
				siteData.put("waterUnit","--");
			}

			consumptionData.put(entry.getKey(), siteData);



		}


		List<Map<String,Object>> listResp = new ArrayList<Map<String,Object>>(consumptionData.values());
		finalResp.put("total_consumption",listResp);


		return finalResp;

	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
