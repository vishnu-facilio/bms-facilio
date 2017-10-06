package com.facilio.leed.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyDataContext;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.UtilityProviderContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.google.gson.JsonObject;

public class AddConsumptionForLeed implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long buildingId = (long)context.get(FacilioConstants.ContextNames.ID);
		String LeedId = (String) context.get(FacilioConstants.ContextNames.LeedID);
		String MeterId = (String) context.get(FacilioConstants.ContextNames.MeterID);
		List<EnergyDataContext> energyDataList = (List<EnergyDataContext>)context.get(FacilioConstants.ContextNames.COMSUMPTIONDATA_LIST);
		Iterator data = energyDataList.iterator();
		while(data.hasNext())
		{
			EnergyDataContext energyData = (EnergyDataContext)data.next();
			long addedTime = energyData.getAddedTime();
			HashMap<String, Object> timeData = DateTimeUtil.getTimeData(addedTime); 	
			float consumption = energyData.getTotalEnergyConsumptionDelta();
			long startTime  = energyData.getStartTime();
			JSONObject consumptionJSON = new JSONObject();
			String stdate_str = DateTimeUtil.getDateTime(startTime).toString();
			String endate_Str = DateTimeUtil.getDateTime(addedTime).toString();
			consumptionJSON.put("start_date", stdate_str);
			consumptionJSON.put("end_date", endate_Str);
			consumptionJSON.put("reading", consumption);
			JSONObject response = LeedIntegrator.createConsumption(LeedId, MeterId, consumptionJSON);
			System.out.println("$$$$$$$$$$$ : "+response);
			long consumptionId = -1;
			if(response != null)
			{
			 consumptionId = (long)response.get("id");
			 
			}
			
			
			
		}
		
		if(buildingId > 0)
		{
			List<UtilityProviderContext> utilityproviders = LeedAPI.getAllUtilityProviders(buildingId);
			
			BuildingContext buildingcontext = (BuildingContext)context.get(FacilioConstants.ContextNames.BUILDING);
			if(utilityproviders != null && !utilityproviders.isEmpty() && buildingcontext != null) {
				buildingcontext.setUtilityProviders(utilityproviders);
			}
						
		}
		return false;
	}
	
	public static void addEnergyData(long deviceId,long added_time, double kwh_delta,HashMap<String,Object> timeData) throws SQLException 
	{
		/*
		 * ADDED_DATE DATE,
	ADDED_MONTH INT,
	ADDED_WEEK INT,
	ADDED_DAY VARCHAR(100),
	ADDED_HOUR INT,
	
	columnVals.put("date", date);
		columnVals.put("month", month);
		columnVals.put("week", week);
		columnVals.put("day", day);
		columnVals.put("hour",hour);
		columnVals.put("year", year);
		 */
		String added_date = (String)timeData.get("date");
		int added_month = (int)timeData.get("month");
		int added_week = (int)timeData.get("week");
		String added_day = (String)timeData.get("day");
		int added_hour = (int)timeData.get("hour");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> utilityproviders = new LinkedHashMap<>();
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Energy_Data (DEVICE_ID, ADDED_TIME, TOTAL_ENERGY_CONSUMPTION_DELTA, ADDED_DATE, ADDED_MONTH, ADDED_WEEK, ADDED_DAY, ADDED_HOUR) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			
			pstmt.setLong(1, deviceId);
			pstmt.setLong(2, added_time);
			pstmt.setDouble(3, kwh_delta);
			pstmt.setObject(4, added_date);
			pstmt.setObject(5, added_month);
			pstmt.setObject(6, added_week);
			pstmt.setObject(7, added_day);
			pstmt.setObject(8, added_hour);
			
			rs = pstmt.executeQuery();
				
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
	}

}
