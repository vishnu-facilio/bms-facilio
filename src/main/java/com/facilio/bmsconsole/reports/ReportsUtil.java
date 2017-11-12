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

	private static StringBuilder fieldsDevice= new StringBuilder("PARENT_METER_ID");

	private  static StringBuilder fieldsTime= new StringBuilder("TTIME");

	private  static StringBuilder fieldsDate= new StringBuilder("TTIME_DATE");

	private  static StringBuilder fieldsMonth= new StringBuilder("TTIME_MONTH");

	private  static  StringBuilder fieldsHour= new StringBuilder("TTIME_HOUR");

	private  static StringBuilder fieldsWeek= new StringBuilder("TTIME_WEEK");

	private  static StringBuilder from = new StringBuilder(" FROM ");
	
	private  static  StringBuilder where = new StringBuilder(" WHERE ");

	private static StringBuilder between = new StringBuilder(" BETWEEN ? AND  ? ");
	
	private  static StringBuilder groupBy = new StringBuilder(" GROUP BY ");

	private  static StringBuilder andOperator = new StringBuilder(" AND ");

	private  static StringBuilder fieldsDay = new StringBuilder("TTIME_DAY");

	private  static StringBuilder separator = new StringBuilder(",");

	
   //generic
	private  static StringBuilder getQuery(StringBuilder query, StringBuilder groupByColumn)
	{
		return new StringBuilder(select).append(groupByColumn).append(separator).append(query);
	}
	
	
	//generic..
	private  static StringBuilder getBaseQuery(HashMap<String,StringBuilder> queryFields)
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
		StringBuilder orderByCol=new StringBuilder(" ORDER BY ");

		switch(category)
		{
		case FacilioConstants.Reports.TODAY:
		{
			fromRange=DateTimeUtil.getDayStartTime(true);
			endRange=DateTimeUtil.getCurrenTime(true);
			groupByCol.append(fieldsHour);
			orderByCol.append(fieldsHour).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.THIS_YEAR:
		{
			fromRange=DateTimeUtil.getYearStartTime(true);
			endRange=DateTimeUtil.getCurrenTime(true);
			groupByCol.append(fieldsMonth);
			orderByCol.append(fieldsMonth).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.LAST_YEAR:
		{
			fromRange=DateTimeUtil.getYearStartTime(-1,true);
			endRange=DateTimeUtil.getYearStartTime(true)-1;
			groupByCol.append(fieldsMonth);
			orderByCol.append(fieldsMonth).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.THIS_YEAR_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getYearStartTime(true);
			endRange=DateTimeUtil.getCurrenTime(true);
			groupByCol.append(fieldsWeek);
			orderByCol.append(fieldsWeek).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.LAST_YEAR_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getYearStartTime(-1,true);
			endRange=DateTimeUtil.getYearStartTime(true)-1;
			groupByCol.append(fieldsWeek);
			orderByCol.append(fieldsWeek).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.THIS_HOUR:
		{
			fromRange=DateTimeUtil.getHourStartTime(true);
			endRange=DateTimeUtil.getCurrenTime(true);
			groupByCol.append(fieldsHour);
			orderByCol.append(fieldsHour).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.LAST_HOUR:
		{
			fromRange=DateTimeUtil.getHourStartTime(-1,true);
			endRange=DateTimeUtil.getCurrenTime(true);
			groupByCol.append(fieldsHour);
			orderByCol.append(fieldsHour).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.THIS_MONTH_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getMonthStartTime(true);
			endRange=DateTimeUtil.getCurrenTime(true);
			groupByCol.append(fieldsWeek);
			orderByCol.append(fieldsWeek).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.LAST_MONTH_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getMonthStartTime(-1,true);
			endRange=DateTimeUtil.getMonthStartTime(true)-1;
			groupByCol.append(fieldsWeek);
			orderByCol.append(fieldsWeek).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.THIS_WEEK:
		{
			fromRange=DateTimeUtil.getWeekStartTime(true);
			endRange=DateTimeUtil.getCurrenTime(true);
			groupByCol.append(fieldsDay).append(separator).append(fieldsDate);
			orderByCol.append(fieldsDate).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.LAST_WEEK:
		{
			fromRange=DateTimeUtil.getWeekStartTime(-1,true);
			endRange=DateTimeUtil.getWeekStartTime(true) - 1;
			groupByCol.append(fieldsDay).append(separator).append(fieldsDate);
			orderByCol.append(fieldsDate).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.CUSTOM_WITH_WEEK:
		{
			groupByCol.append(fieldsWeek);
			orderByCol.append(fieldsWeek).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.CUSTOM_WITH_MONTH:
		{
			groupByCol.append(fieldsMonth);
			orderByCol.append(fieldsMonth).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.THIS_MONTH:
		{
			fromRange=DateTimeUtil.getMonthStartTime(true);
			endRange=DateTimeUtil.getCurrenTime(true);
			break;
		}
		case FacilioConstants.Reports.LAST_MONTH:
		{
			fromRange=DateTimeUtil.getMonthStartTime(-1,true);
			endRange=DateTimeUtil.getMonthStartTime(true)-1;
			break;
		}
		case FacilioConstants.Reports.YESTERDAY:
		{
			fromRange=DateTimeUtil.getDayStartTime(-1,true);
			endRange=DateTimeUtil.getDayStartTime(true)-1;
			groupByCol.append(fieldsHour);
			orderByCol.append(fieldsHour).append(" ASC");
			break;
		}
		case FacilioConstants.Reports.LAST_7_DAYS:
		{
			fromRange=DateTimeUtil.getDayStartTime(7,true);
			endRange=DateTimeUtil.getDayStartTime(true)-1;
			break;
		}
		case FacilioConstants.Reports.LAST_30_DAYS:
		{
			fromRange=DateTimeUtil.getDayStartTime(-30,true);
			endRange=DateTimeUtil.getDayStartTime(true)-1;
			break;
		}
		}
		//CUSTOM_WITH_DATE not handled at that will have the groupByCol as ADDED_DATE
		if(groupByCol.length()==0)
		{
		  groupByCol=fieldsDate;
		  orderByCol.append(fieldsDate).append(" ASC");
		}

		HashMap <String,Object> hMap = new HashMap <String,Object>  ();
		hMap.put(FacilioConstants.Reports.RANGE_FROM, fromRange);
		hMap.put(FacilioConstants.Reports.RANGE_END, endRange);
		hMap.put(FacilioConstants.Reports.GROUPBY_COLUMN, groupByCol);
		hMap.put(FacilioConstants.Reports.ORDERBY_COLUMN, orderByCol);
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
					int selectCount=meta.getColumnCount();
					
					//under the assumption deviceid will be the first select column..
					long deviceId = (Long)rs.getObject(1);
					//currently this is expensive as we are hitting the db.. later it will return from cache..
					String deviceName=getDeviceName(deviceId);
					data.put(meta.getColumnLabel(1), deviceName);

					for(int i=2;i<=selectCount;i++)
					{
						String key=meta.getColumnLabel(i);
						data.put(key,rs.getObject(key).toString() );
					}
					
					logger.log(Level.INFO, rs.getRow()+": Row data: "+data);
					JSONArray array =(JSONArray) map.get(deviceName);
					if(array==null) 
					{
						array= new JSONArray();
						map.put(deviceName,array);
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
		
		return getResultJson(map);
	}
	
	
	@SuppressWarnings("unchecked")
	private static JSONObject getResultJson(HashMap<String, JSONArray> map)
	{
		if (map==null || map.isEmpty())
		{
			return null;
		}
		JSONObject resultJson = new JSONObject();
		JSONArray data = new JSONArray();
		data.addAll(map.values());
		resultJson.put("data", data);
		return resultJson;
	}
	
	

	//generic..
	private static JSONObject getData(int category, HashMap<String,StringBuilder> queryFields,
			String fromDate, String endDate, boolean groupFields, Long... deviceId)
	{
		StringBuilder baseQuery=getBaseQuery( queryFields);
		HashMap<String,Object> hMap=getQueryParams(category);
		
		StringBuilder groupByCol=(StringBuilder)hMap.remove(FacilioConstants.Reports.GROUPBY_COLUMN);
		StringBuilder orderByCol=(StringBuilder)hMap.remove(FacilioConstants.Reports.ORDERBY_COLUMN);
		String fromRange=((Long)hMap.remove(FacilioConstants.Reports.RANGE_FROM)).toString();
		String endRange=((Long)hMap.remove(FacilioConstants.Reports.RANGE_END)).toString();
		
		fromRange=fromRange.equals("0")?fromDate:fromRange;
		endRange=endRange.equals("0")?endDate:endRange;
		
		StringBuilder groupByColumns= new StringBuilder().append(fieldsDevice).append(separator).append(groupByCol);
		
		StringBuilder finalQuery=getQuery(baseQuery, groupByColumns);

		if(groupFields)
		{
			finalQuery=finalQuery.append(groupBy).append(groupByColumns);
		}
		finalQuery=finalQuery.append(orderByCol);
		
		JSONObject resultJson= getData(finalQuery.toString(), fromRange, endRange, deviceId);
		if(resultJson==null)
		{
			return null;
		}
		String groupBy=groupByCol.toString();
		
		int end=groupBy.indexOf(",");
		if(end>0)
		{
			groupBy=groupBy.substring(0, end);
		}
		
		resultJson.put("axisKey", groupBy);
		return resultJson;
	}
	
	//generic..
	private static int getDeviceCount(Long...deviceId)
	{
		return (deviceId==null)?0:deviceId.length;
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
		queryFields.put("selectFields",fetchColumn);
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
