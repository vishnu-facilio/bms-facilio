package com.facilio.bmsconsole.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.device.Device;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.transaction.FacilioConnectionPool;

public class ReportsUtil 
{
	private static Logger logger = Logger.getLogger(ReportsUtil.class.getName());
	
	private static StringBuilder select = new StringBuilder(" SELECT ");

	private static StringBuilder fieldsDevice= new StringBuilder("DEVICE_ID");

	private  static StringBuilder fieldsTime= new StringBuilder("ADDED_TIME");

	private  static StringBuilder fieldsDate= new StringBuilder("ADDED_DATE");

	private  static StringBuilder fieldsMonth= new StringBuilder("ADDED_MONTH");

	private  static  StringBuilder fieldsHour= new StringBuilder("ADDED_HOUR");

	private  static StringBuilder fieldsWeek= new StringBuilder("ADDED_WEEK");

	private  static StringBuilder from = new StringBuilder(" FROM ");
	
	private  static  StringBuilder where = new StringBuilder(" WHERE ");

	private static StringBuilder between = new StringBuilder(" BETWEEN ? AND  ? ");
	
	private  static StringBuilder groupBy = new StringBuilder(" GROUP BY ");

	private  static StringBuilder andOperator = new StringBuilder(" AND ");

	//private  static StringBuilder orOperator = new StringBuilder(" OR ");

	private  static StringBuilder separator = new StringBuilder(" , ");

	
   //generic
	private  static StringBuilder getQuery(StringBuilder query, StringBuilder groupByColumn, boolean groupFields)
	{
		if(!groupFields)
		{
			return new StringBuilder(select).append(groupByColumn).append(separator).append(query);
		}
		return new StringBuilder(select).append(groupByColumn).append(separator).
		append(query).append(groupByColumn);
	}
	
	
	//generic..
	private  static StringBuilder getBaseQuery(HashMap<String,StringBuilder> queryFields, boolean groupFields)
	{
		StringBuilder selectFields=queryFields.get("selectFields");
		StringBuilder table=queryFields.get("table");
		StringBuilder betweenCol=queryFields.get("betweenColumn");
		StringBuilder inCol=queryFields.get("inColumn");
		StringBuilder inClause=queryFields.get("inClause");
		StringBuilder baseQuery=selectFields.append(from).append(table).
				                append(where).append(betweenCol).append(between);
		
		if(inCol!=null)
		{
			baseQuery= baseQuery.append(andOperator).append(inCol).append(inClause); 
		}
		if(groupFields)
		{
			baseQuery= baseQuery.append(groupBy);
		}
		return baseQuery;
	}
	
	//generic
	private static StringBuilder getInClause(int length) {
		StringBuilder inClause = new StringBuilder(" IN ( ");
		for(int i=0;i<length;i++)
		{
			inClause.append("?,");
		}
		length=inClause.length();
		inClause=inClause.replace(length-1, length, ")");
		return inClause;
	}
	
	
	//generic
	private  static  HashMap<String,Object> getQueryParams(int category)
	{
		Long fromRange=0L,endRange=0L;
		StringBuilder groupByCol=new StringBuilder();

		switch(category)
		{
		case FacilioConstants.Reports.TODAY:
		{
			fromRange=DateTimeUtil.getDayStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			groupByCol.append(fieldsHour);
			break;
		}
		case FacilioConstants.Reports.THIS_YEAR:
		{
			fromRange=DateTimeUtil.getYearStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			groupByCol.append(fieldsMonth);
			break;
		}
		case FacilioConstants.Reports.LAST_YEAR:
		{
			fromRange=DateTimeUtil.getYearStartTime(-1);
			endRange=DateTimeUtil.getYearStartTime()-1;
			groupByCol.append(fieldsMonth);
			break;
		}
		case FacilioConstants.Reports.THIS_YEAR_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getYearStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			groupByCol.append(fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.LAST_YEAR_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getYearStartTime(-1);
			endRange=DateTimeUtil.getYearStartTime()-1;
			groupByCol.append(fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.THIS_HOUR:
		{
			fromRange=DateTimeUtil.getHourStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			groupByCol.append(fieldsHour);
			break;
		}
		case FacilioConstants.Reports.LAST_HOUR:
		{
			fromRange=DateTimeUtil.getHourStartTime(-1);
			endRange=DateTimeUtil.getCurrenTime();
			groupByCol.append(fieldsHour);
			break;
		}
		case FacilioConstants.Reports.THIS_MONTH_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getMonthStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			groupByCol.append(fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.LAST_MONTH_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getMonthStartTime(-1);
			endRange=DateTimeUtil.getMonthStartTime()-1;
			groupByCol.append(fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.CUSTOM_WITH_WEEK:
		{
			groupByCol.append(fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.CUSTOM_WITH_MONTH:
		{
			groupByCol.append(fieldsMonth);
			break;
		}
		case FacilioConstants.Reports.THIS_MONTH:
		{
			fromRange=DateTimeUtil.getMonthStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			break;
		}
		case FacilioConstants.Reports.LAST_MONTH:
		{
			fromRange=DateTimeUtil.getMonthStartTime(-1);
			endRange=DateTimeUtil.getMonthStartTime()-1;
			break;
		}
		case FacilioConstants.Reports.YESTERDAY:
		{
			fromRange=DateTimeUtil.getDayStartTime(-1);
			endRange=DateTimeUtil.getDayStartTime()-1;
			groupByCol.append(fieldsHour);
			break;
		}
		case FacilioConstants.Reports.LAST_7_DAYS:
		{
			fromRange=DateTimeUtil.getDayStartTime(7);
			endRange=DateTimeUtil.getDayStartTime()-1;
			break;
		}
		case FacilioConstants.Reports.LAST_30_DAYS:
		{
			fromRange=DateTimeUtil.getDayStartTime(-30);
			endRange=DateTimeUtil.getDayStartTime()-1;
			break;
		}
		case FacilioConstants.Reports.THIS_WEEK:
		{
			fromRange=DateTimeUtil.getWeekStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			break;
		}
		case FacilioConstants.Reports.LAST_WEEK:
		{
			fromRange=DateTimeUtil.getWeekStartTime(-1);
			endRange=DateTimeUtil.getWeekStartTime() - 1;
			break;
		}
		}
		//CUSTOM_WITH_DATE not handled at that will have the groupByCol as ADDED_DATE
		if(groupByCol.length()==0)
		{
		  groupByCol=fieldsDate;
		}

		HashMap <String,Object> hMap = new HashMap <String,Object>  ();
		hMap.put(FacilioConstants.Reports.RANGE_FROM, fromRange);
		hMap.put(FacilioConstants.Reports.RANGE_END, endRange);
		hMap.put(FacilioConstants.Reports.GROUPBY_COLUMN, groupByCol);
		return hMap;
	}
	
	//generic
	private static String getDeviceName(long deviceId)
	{
		try 
		{
			Device device = DeviceAPI.getDevice(deviceId);
			if (device!=null)
			{
				return device.getName();
			}
		} 
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Error while fetching device with id: "+deviceId, e);
		}
		return null;
	}
	
	
	//generic
	@SuppressWarnings("unchecked")
	private  static JSONObject getData (String fetchQuery, String from, String end, Long... devices)
	{
		HashMap<String, JSONArray> map =null;
		String key_1=null;
		
		logger.log(Level.INFO, "The pstmt with "+from+" & "+end+" is \n"+fetchQuery);
		
		
		try(Connection conn = FacilioConnectionPool.getInstance().getConnection();
				PreparedStatement psmt=conn.prepareStatement(fetchQuery))
		{
			psmt.setObject(1, from);
			psmt.setObject(2, end);
			
			if(devices!=null)
			{
				int position=3;
				for(Long device:devices)
				{
					psmt.setObject(position, device);
					position++;
				}
			}
			try(ResultSet rs=psmt.executeQuery())
			{
				
			    map = new LinkedHashMap <String, JSONArray> ();

				while(rs.next())
				{
					
					JSONObject data  = new JSONObject ();
					
					
					ResultSetMetaData meta = rs.getMetaData();
					key_1=meta.getColumnLabel(1);
					String key_2=meta.getColumnLabel(2);
					String key_3=meta.getColumnLabel(3);
					
					String timeKey =rs.getObject(key_1).toString();
					long deviceId = (Long)rs.getObject(key_2);
					String reqData =rs.getObject(key_3).toString();
					
					//currently this is expensive as we are hitting the db.. later it will return from cache..
					String deviceName=getDeviceName(deviceId);
					
					data.put(key_1, timeKey);
					data.put(key_2, deviceName);
					data.put(key_3, reqData);
					
					logger.log(Level.INFO, rs.getRow()+": Row data: "+data);
					JSONArray array =(JSONArray) map.get(timeKey);
					
					if(array==null) 
					{
						array= new JSONArray();
						map.put(timeKey,array);
					}	
					array.add(data);
				}
				
			}
			catch(SQLException e)
			{
				logger.log(Level.SEVERE, "Error while fetching data with query:\n "+fetchQuery, e);
			}
		}
		catch(SQLException e)
		{
			logger.log(Level.SEVERE, "Error while fetching data with query:\n "+fetchQuery, e);
		}
		
		return getResultJson(map, key_1);
	}
	
	
	@SuppressWarnings("unchecked")
	private static JSONObject getResultJson(HashMap<String, JSONArray> map, String keyParam)
	{
		if (map==null || map.isEmpty())
		{
			return null;
		}
		JSONObject resultJson = new JSONObject();
		
		JSONArray keys = new JSONArray();
		keys.addAll(map.keySet());
		resultJson.put("keys", keys);
		
		JSONArray data = new JSONArray();
		data.addAll(map.values());
		resultJson.put("data", data);
		
		resultJson.put("keyParam", keyParam);
			
		return resultJson;
	}
	
	

	//generic..
	private static JSONObject getData(int category, HashMap<String,StringBuilder> queryFields,
			String fromDate, String endDate, boolean groupFields, Long... deviceId)
	{
		StringBuilder baseQuery=getBaseQuery( queryFields, groupFields);
		HashMap<String,Object> hMap=getQueryParams(category);
		
		StringBuilder groupByCol=(StringBuilder)hMap.remove(FacilioConstants.Reports.GROUPBY_COLUMN);
		String fromRange=((Long)hMap.remove(FacilioConstants.Reports.RANGE_FROM)).toString();
		String endRange=((Long)hMap.remove(FacilioConstants.Reports.RANGE_END)).toString();
		
		fromRange=fromRange.equals("0")?fromDate:fromRange;
		endRange=endRange.equals("0")?endDate:endRange;
		
		StringBuilder finalQuery=getQuery(baseQuery, groupByCol, groupFields);

		if(groupFields)
		{
			finalQuery=finalQuery.append(separator).append(fieldsDevice);
		}
		return getData(finalQuery.toString(), fromRange, endRange, deviceId);
	}
	
	//generic..
	private static int getDeviceCount(Long...deviceId)
	{
		int deviceCount=0;
		if(deviceId!=null){
			deviceCount=deviceId.length;
		}
		return deviceCount;
	}
	
	//generic..
	@SuppressWarnings("unchecked")
	private static void setUnits(int dataCategory,JSONObject json)
	{
		if(json==null){
			return;
		}
		
		switch(dataCategory)
		{
		case FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA : 
		case FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA_SUM :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_R_DELTA :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_R_DELTA_SUM :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_Y_DELTA :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_Y_DELTA_SUM :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_B_DELTA :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_B_DELTA_SUM :
		{
			json.put("units", "kWh");
			break;
		}
		case FacilioConstants.Reports.Energy.FREQUENCY_R :
		case FacilioConstants.Reports.Energy.FREQUENCY_R_AVERAGE :
		case FacilioConstants.Reports.Energy.FREQUENCY_Y :
		case FacilioConstants.Reports.Energy.FREQUENCY_Y_AVERAGE :
		case FacilioConstants.Reports.Energy.FREQUENCY_B :
		case FacilioConstants.Reports.Energy.FREQUENCY_B_AVERAGE :
		{
			json.put("units", "Hz");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_R :
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_R_AVERAGE :
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_Y :
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_Y_AVERAGE :
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_B :
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_B_AVERAGE :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_R :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_R_AVERAGE :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_Y :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_Y_AVERAGE :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_B :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_B_AVERAGE :
		{
			json.put("units", "volts");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_CURRENT_R :
		case FacilioConstants.Reports.Energy.LINE_CURRENT_R_AVERAGE :
		case FacilioConstants.Reports.Energy.LINE_CURRENT_Y :
		case FacilioConstants.Reports.Energy.LINE_CURRENT_Y_AVERAGE :
		case FacilioConstants.Reports.Energy.LINE_CURRENT_B :
		case FacilioConstants.Reports.Energy.LINE_CURRENT_B_AVERAGE :
		{
			json.put("units", "ampere");
			break;
		}
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_R :
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_R_SUM :
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_Y :
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_Y_SUM :
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_B :
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_B_SUM :
		{
			json.put("units", "kW");
			break;
		}
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_R :
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_R_SUM :
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_Y :
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_Y_SUM :
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_B :
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_B_SUM :
		{
			json.put("units", "kVAr");
			break;
		}
		case FacilioConstants.Reports.Energy.APPARANT_POWER_R :
		case FacilioConstants.Reports.Energy.APPARANT_POWER_R_SUM :
		case FacilioConstants.Reports.Energy.APPARANT_POWER_Y :
		case FacilioConstants.Reports.Energy.APPARANT_POWER_Y_SUM :
		case FacilioConstants.Reports.Energy.APPARANT_POWER_B :
		case FacilioConstants.Reports.Energy.APPARANT_POWER_B_SUM :
		{
			json.put("units", "kVA");
			break;
		}
		
		default :
		{//power Factor no unit..
			json.put("units", "Value");
			break;
		}
		
		}
		
	}
	
	
	private static boolean isGroupBy(int dataCategory)
	{
		switch(dataCategory)
		{
		case FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA : 
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_R_DELTA :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_Y_DELTA :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_B_DELTA :
		case FacilioConstants.Reports.Energy.FREQUENCY_R :
		case FacilioConstants.Reports.Energy.FREQUENCY_Y :
		case FacilioConstants.Reports.Energy.FREQUENCY_B :
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_R :
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_Y :
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_B :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_R :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_Y :
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_B :
		case FacilioConstants.Reports.Energy.LINE_CURRENT_R :
		case FacilioConstants.Reports.Energy.LINE_CURRENT_Y :
		case FacilioConstants.Reports.Energy.LINE_CURRENT_B :
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_R :
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_Y :
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_B :
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_R :
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_Y :
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_B :
		case FacilioConstants.Reports.Energy.APPARANT_POWER_R :
		case FacilioConstants.Reports.Energy.APPARANT_POWER_Y :
		case FacilioConstants.Reports.Energy.APPARANT_POWER_B :
		case FacilioConstants.Reports.Energy.POWER_FACTOR_R :
		case FacilioConstants.Reports.Energy.POWER_FACTOR_Y :
		case FacilioConstants.Reports.Energy.POWER_FACTOR_B :
		{
			return false;
		}
		
		}
		return true;	
	}
	
	
	private static StringBuilder getFetchColumn(int dataCategory)
	{
		StringBuilder fetchColumn= new StringBuilder();
		
		switch(dataCategory)
		{
		case FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA :
		{
			fetchColumn.append(" TOTAL_ENERGY_CONSUMPTION_DELTA AS ENERGY_CONSUMPTION ");
			break;
		}
		case FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA_SUM :
		case FacilioConstants.Reports.Energy.TOTAL_ENERGY_CONSUMPTION_DELTA_COST :
		{
			fetchColumn.append(" ROUND(SUM(TOTAL_ENERGY_CONSUMPTION_DELTA),2) AS ENERGY_CONSUMPTION ");
			break;
		}
		
		
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_R_DELTA_SUM :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_R_DELTA_COST :
		{
			fetchColumn.append(" ROUND(SUM(PHASE_ENERGY_R_DELTA),2) AS PHASE_ENERGY_R_DELTA ");
			break;
		}
		
		
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_R_DELTA :
		{
			fetchColumn.append(" PHASE_ENERGY_R_DELTA AS PHASE_ENERGY_R_DELTA ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_Y_DELTA_SUM :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_Y_DELTA_COST :
		{
			fetchColumn.append(" ROUND(SUM(PHASE_ENERGY_Y_DELTA),2) AS PHASE_ENERGY_Y_DELTA ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_Y_DELTA :
		{
			fetchColumn.append(" PHASE_ENERGY_Y_DELTA AS PHASE_ENERGY_Y_DELTA ");
			break;
		}
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_B_DELTA_SUM :
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_B_DELTA_COST :
		{
			fetchColumn.append(" ROUND(SUM(PHASE_ENERGY_B_DELTA),2) AS PHASE_ENERGY_B_DELTA ");
			break;
		}
		case FacilioConstants.Reports.Energy.PHASE_ENERGY_B_DELTA :
		{
			fetchColumn.append(" PHASE_ENERGY_B_DELTA AS PHASE_ENERGY_B_DELTA ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_R :
		{
			fetchColumn.append(" ACTIVE_POWER_R AS ACTIVE_POWER_R ");
			break;
		}
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_R_SUM :
		{
			fetchColumn.append(" ROUND(SUM(ACTIVE_POWER_R),2) AS ACTIVE_POWER_R ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_Y :
		{
			fetchColumn.append(" ACTIVE_POWER_Y AS ACTIVE_POWER_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_Y_SUM :
		{
			fetchColumn.append(" ROUND(SUM(ACTIVE_POWER_Y),2) AS ACTIVE_POWER_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_B :
		{
			fetchColumn.append(" ACTIVE_POWER_B AS ACTIVE_POWER_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.ACTIVE_POWER_B_SUM :
		{
			fetchColumn.append(" ROUND(SUM(ACTIVE_POWER_B),2) AS ACTIVE_POWER_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_R :
		{
			fetchColumn.append(" REACTIVE_POWER_R AS REACTIVE_POWER_R ");
			break;
		}
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_R_SUM :
		{
			fetchColumn.append(" ROUND(SUM(REACTIVE_POWER_R),2) AS REACTIVE_POWER_R ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_Y :
		{
			fetchColumn.append(" REACTIVE_POWER_Y AS REACTIVE_POWER_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_Y_SUM :
		{
			fetchColumn.append(" ROUND(SUM(REACTIVE_POWER_Y),2) AS REACTIVE_POWER_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_B :
		{
			fetchColumn.append(" REACTIVE_POWER_B AS REACTIVE_POWER_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.REACTIVE_POWER_B_SUM :
		{
			fetchColumn.append(" ROUND(SUM(REACTIVE_POWER_B),2) AS REACTIVE_POWER_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.APPARANT_POWER_R :
		{
			fetchColumn.append(" APPARANT_POWER_R AS APPARANT_POWER_R ");
			break;
		}
		case FacilioConstants.Reports.Energy.APPARANT_POWER_R_SUM :
		{
			fetchColumn.append(" ROUND(SUM(APPARANT_POWER_R),2) AS APPARANT_POWER_R ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.APPARANT_POWER_Y :
		{
			fetchColumn.append(" APPARANT_POWER_Y AS APPARANT_POWER_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.APPARANT_POWER_Y_SUM :
		{
			fetchColumn.append(" ROUND(SUM(APPARANT_POWER_Y),2) AS APPARANT_POWER_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.APPARANT_POWER_B :
		{
			fetchColumn.append(" APPARANT_POWER_B AS APPARANT_POWER_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.APPARANT_POWER_B_SUM :
		{
			fetchColumn.append(" ROUND(SUM(APPARANT_POWER_B),2) AS APPARANT_POWER_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.POWER_FACTOR_R :
		{
			fetchColumn.append(" POWER_FACTOR_R AS POWER_FACTOR_R ");
			break;
		}
		case FacilioConstants.Reports.Energy.POWER_FACTOR_R_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(POWER_FACTOR_R),2) AS POWER_FACTOR_R ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.POWER_FACTOR_Y :
		{
			fetchColumn.append(" POWER_FACTOR_Y AS POWER_FACTOR_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.POWER_FACTOR_Y_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(POWER_FACTOR_Y),2) AS POWER_FACTOR_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.POWER_FACTOR_B :
		{
			fetchColumn.append(" POWER_FACTOR_B AS POWER_FACTOR_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.POWER_FACTOR_B_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(POWER_FACTOR_B),2) AS POWER_FACTOR_B ");
			break;
		}

		case FacilioConstants.Reports.Energy.FREQUENCY_R :
		{
			fetchColumn.append(" FREQUENCY_R AS FREQUENCY_R ");
			break;
		}
		case FacilioConstants.Reports.Energy.FREQUENCY_R_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(FREQUENCY_R),2) AS FREQUENCY_R ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.FREQUENCY_Y :
		{
			fetchColumn.append(" FREQUENCY_Y AS FREQUENCY_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.FREQUENCY_Y_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(FREQUENCY_Y),2) AS FREQUENCY_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.FREQUENCY_B :
		{
			fetchColumn.append(" FREQUENCY_B AS FREQUENCY_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.FREQUENCY_B_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(FREQUENCY_B),2) AS FREQUENCY_B ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_R :
		{
			fetchColumn.append(" LINE_VOLTAGE_R AS LINE_VOLTAGE_R ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_R_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(LINE_VOLTAGE_R),2) AS LINE_VOLTAGE_R ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_Y :
		{
			fetchColumn.append(" LINE_VOLTAGE_Y AS LINE_VOLTAGE_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_Y_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(LINE_VOLTAGE_Y),2) AS LINE_VOLTAGE_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_B :
		{
			fetchColumn.append(" LINE_VOLTAGE_B AS LINE_VOLTAGE_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_VOLTAGE_B_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(LINE_VOLTAGE_B),2) AS LINE_VOLTAGE_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_CURRENT_R :
		{
			fetchColumn.append(" LINE_CURRENT_R AS LINE_CURRENT_R ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_CURRENT_R_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(LINE_CURRENT_R),2) AS LINE_CURRENT_R ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.LINE_CURRENT_Y :
		{
			fetchColumn.append(" LINE_CURRENT_Y AS LINE_CURRENT_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_CURRENT_Y_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(LINE_CURRENT_Y),2) AS LINE_CURRENT_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_CURRENT_B :
		{
			fetchColumn.append(" LINE_CURRENT_B AS LINE_CURRENT_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.LINE_CURRENT_B_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(LINE_CURRENT_B),2) AS LINE_CURRENT_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_R :
		{
			fetchColumn.append(" PHASE_VOLTAGE_R AS PHASE_VOLTAGE_R ");
			break;
		}
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_R_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(PHASE_VOLTAGE_R),2) AS PHASE_VOLTAGE_R ");
			break;
		}
		
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_Y :
		{
			fetchColumn.append(" PHASE_VOLTAGE_Y AS PHASE_VOLTAGE_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_Y_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(PHASE_VOLTAGE_Y),2) AS PHASE_VOLTAGE_Y ");
			break;
		}
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_B :
		{
			fetchColumn.append(" PHASE_VOLTAGE_B AS PHASE_VOLTAGE_B ");
			break;
		}
		case FacilioConstants.Reports.Energy.PHASE_VOLTAGE_B_AVERAGE :
		{
			fetchColumn.append(" ROUND(AVG(PHASE_VOLTAGE_B),2) AS PHASE_VOLTAGE_B ");
			break;
		}
		
		}
		return fetchColumn;
	}
	
	private static HashMap<String, StringBuilder> getFields(int size,StringBuilder fetchColumn, StringBuilder betweenCol)
	{
		HashMap<String, StringBuilder> queryFields = new HashMap<String, StringBuilder>();
		StringBuilder dataSelect = new StringBuilder(fieldsDevice).append(separator).append(fetchColumn);
		queryFields.put("selectFields",dataSelect);
		queryFields.put("table", FacilioConstants.Reports.ENERGY_TABLE);
		queryFields.put("betweenColumn", betweenCol);
		if(size!=0)
		{
			queryFields.put("inColumn", fieldsDevice);
			queryFields.put("inClause", getInClause(size));	
		}
		return queryFields;
	}
	
	public static JSONObject getEnergyData(int dataCategory,int timeCategory, String fromDate, String endDate,Long... deviceId)
	{
		HashMap<String,StringBuilder> queryFields= getFields(getDeviceCount(deviceId),
												  getFetchColumn(dataCategory),fieldsDate);
		boolean groupField=isGroupBy(dataCategory);
		JSONObject resultJson= getData(timeCategory,queryFields,fromDate, endDate,groupField,deviceId);
		setUnits(dataCategory,resultJson);
		logger.log(Level.INFO, "Final result: "+resultJson);
		return resultJson;
	}

	public static JSONObject getEnergyData(int dataCategory,int timeCategory,Long... deviceId)
	{
		HashMap<String,StringBuilder> powerFields=getFields(getDeviceCount(deviceId),
				                                  getFetchColumn(dataCategory),fieldsTime);
		boolean groupField=isGroupBy(dataCategory);
		JSONObject resultJson=getData(timeCategory,powerFields,"0","0",groupField,deviceId);
		setUnits(dataCategory,resultJson);
		logger.log(Level.INFO, "Final result: "+resultJson);
		return resultJson;
	}
	
	public static StringBuilder getAdditionalTimeSql()
	{
		return new StringBuilder("ADDED_DATE=?,ADDED_MONTH=?,ADDED_WEEK=?,ADDED_DAY=?,ADDED_HOUR=?"); 
	}
	
	public static List<String> getAdditionalTimeCols()
	{
		List<String> dbCols = new ArrayList<>();
		dbCols.add("date");
		dbCols.add("month");
		dbCols.add("week");
		dbCols.add("day");
		dbCols.add("hour");
		return dbCols;
		
	}
	
}
