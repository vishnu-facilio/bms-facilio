package com.facilio.leed.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.leed.constants.LeedConstants;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.context.FuelContext;
import com.facilio.leed.context.LeedEnergyMeterContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;

public class FetchMeterListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long buildingId =(long)context.get(LeedConstants.ContextNames.BUILDINGID);
		String meterType = (String)context.get(LeedConstants.ContextNames.METERTYPE);
		long leedId = LeedAPI.getLeedId(buildingId);
		List<LeedEnergyMeterContext> meterList = LeedAPI.fetchMeterListForBuilding(buildingId,meterType);
		context.put(LeedConstants.ContextNames.LEEDID, leedId);
		
		if(meterList == null || meterList.isEmpty())
		{
			
			ArcContext arccontext = LeedAPI.getArcContext();
			LeedIntegrator integ = new LeedIntegrator(arccontext);
			JSONObject meterJSON = integ.getMeters(leedId+"");
			JSONObject meterMsgJSON = (JSONObject)meterJSON.get("message");
			System.out.println(">>>>> meterMsgJSON : "+meterMsgJSON);
			meterList = getLeedEnergyMeterList(meterMsgJSON);
			LeedAPI.addLeedEnergyMeters(meterList,buildingId);
			
		}
		//context.put(LeedConstants.ContextNames.METERLIST, meterList);
		meterList = LeedAPI.fetchMeterListForBuilding(buildingId,meterType);
		context.put(LeedConstants.ContextNames.METERLIST, meterList);
		return false;
	}
		
	public List<LeedEnergyMeterContext> getLeedEnergyMeterList(JSONObject meterJSON)
	{
		List<LeedEnergyMeterContext> meterList = new ArrayList();
		List<String> reqArray = new ArrayList();
		reqArray.add("operating_hours");
		reqArray.add("gross_area");
		reqArray.add("occupancy");
		JSONArray meterArr = (JSONArray)meterJSON.get("results");
		Iterator itr = meterArr.iterator();
		while(itr.hasNext())
		{
			JSONObject mJSON = (JSONObject)itr.next();			
			long meterId = (long)mJSON.get("id");
			String meterName = (String)mJSON.get("name");
			mJSON.get("type");
			String unit = (String)mJSON.get("native_unit");
			
			JSONObject fuel_type = (JSONObject)mJSON.get("fuel_type");
			long fuelId = (long)fuel_type.get("id");
			String type = (String)fuel_type.get("type");
			String subType = (String)fuel_type.get("subtype");
			String fuelKind = (String)fuel_type.get("kind");
			String resource = (String)fuel_type.get("resource");
			if(reqArray.contains(fuelKind))
			{
				continue;
			}
			FuelContext fcontext = new FuelContext();
			fcontext.setFuelId(fuelId);
			fcontext.setKind(fuelKind);
			fcontext.setResource(resource);
			fcontext.setSubType(subType);
			fcontext.setFuelType(type);
			
			LeedEnergyMeterContext context = new LeedEnergyMeterContext();
			context.setName(meterName);
			context.setMeterId(meterId);
			context.setFuelContext(fcontext);
			context.setServiceProvider(type);
			context.setUnit(unit);
			meterList.add(context);
		}
		return meterList;
	}

}
