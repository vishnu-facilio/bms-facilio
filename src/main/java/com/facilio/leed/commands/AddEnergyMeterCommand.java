package com.facilio.leed.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.constants.LeedConstants;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.context.FuelContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AddEnergyMeterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		context.get(LeedConstants.ContextNames.BUILDINGID);
		long leedId = (long) context.get(LeedConstants.ContextNames.LEEDID);
		String meterName = (String)context.get(LeedConstants.ContextNames.METERNAME);
		String meterTypeAdded = (String)context.get(LeedConstants.ContextNames.METERTYPE);
		JSONObject meterInfo = new JSONObject();
		meterInfo.put("name", meterName);
		meterInfo.put("included", "true");
		new FuelContext();
		
		if(meterTypeAdded.equalsIgnoreCase("water"))
		{	
			meterInfo.put("native_unit", "gal");
			meterInfo.put("type", "47");
		
		}
		else
		{
		meterInfo.put("native_unit", "kWh");
		meterInfo.put("type", "16"); //fuel_type 16 for { "id": 16, "type": "AZNM", "subtype": "WECC Southwest", "kind": "electricity", "resource": "Non-Renewable"}
		}
		ArcContext arccontext = LeedAPI.getArcContext();
		LeedIntegrator integ = new LeedIntegrator(arccontext);
		JSONObject meter_Id_Info = integ.createMeter(leedId, meterInfo);
		System.out.println(">>>>>>>>> meter_Id_Info : "+meter_Id_Info);
		JSONObject meterMessages = (JSONObject)meter_Id_Info.get("message");
		long meter_Id = (long)meterMessages.get("id");
		context.put(FacilioConstants.ContextNames.METERID, meter_Id);
		long fuel_type = (long)meterMessages.get("type");
		context.put(FacilioConstants.ContextNames.FUELTYPE, fuel_type);
		JSONObject fuelJSON = (JSONObject)meterMessages.get("fuel_type");
		String meterType = (String)fuelJSON.get("kind");
		context.put(FacilioConstants.ContextNames.METERTYPE, meterType);
		LeedAPI.addLeedEnergyMeter((FacilioContext)context);
		
		return false;
	}
	
	

}
