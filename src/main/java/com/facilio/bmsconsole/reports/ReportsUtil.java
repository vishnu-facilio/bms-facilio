package com.facilio.bmsconsole.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

	private  static StringBuilder fieldsForPower = new StringBuilder (" DEVICE_ID, ROUND(SUM(TOTAL_ENERGY_CONSUMPTION_DELTA),2)");

	private  static StringBuilder fieldsDate= new StringBuilder("ADDED_DATE");

	private  static StringBuilder fieldsMonth= new StringBuilder("ADDED_MONTH");

	private  static  StringBuilder fieldsHour= new StringBuilder("ADDED_HOUR");

	private  static StringBuilder fieldsWeek= new StringBuilder("ADDED_WEEK");

	private  static StringBuilder from = new StringBuilder(" FROM ");
	
	private  static StringBuilder energy= new StringBuilder(" ENERGY_DATA ");
	
	private  static  StringBuilder where = new StringBuilder(" WHERE ");

	private  static StringBuilder timeBetween = new StringBuilder(" ADDED_TIME BETWEEN ? AND ? ");
	
	private static  StringBuilder dateBetween = new StringBuilder(" ADDED_DATE BETWEEN ? AND ? ");

	private  static StringBuilder device_id= new StringBuilder(" DEVICE_ID = ? ");

	private  static StringBuilder groupBy = new StringBuilder(" GROUP BY DEVICE_ID ");

	private  static StringBuilder andOperator = new StringBuilder(" AND ");

	private  static StringBuilder orOperator = new StringBuilder(" OR ");

	private  static StringBuilder separator = new StringBuilder(" , ");

	
	private  static StringBuilder getQuery(StringBuilder query, StringBuilder groupByColumn)
	{
		return new StringBuilder().append(select).append(groupByColumn).append(separator).
		append(query).append(separator).append(groupByColumn);
	}
	
	private  static StringBuilder getQuery(StringBuilder selectFields, StringBuilder table, StringBuilder betweenCol, Long deviceId)
	{
		StringBuilder baseQuery=new StringBuilder().append(selectFields).append(from).append(table).
				                append(where).append(betweenCol);
		if(deviceId==null)
		{
			baseQuery=baseQuery.append(groupBy);
		}
		else
		{
			baseQuery= baseQuery.append(andOperator).append(device_id).append(groupBy); 
		}
		return baseQuery;
	}

	private  static  HashMap<String,Object> getQueryObject(int category, Long deviceId)
	{
		StringBuilder baseQuery=getQuery(fieldsForPower, energy, timeBetween,deviceId);
		return getQueryObject(category, deviceId, baseQuery);
	}
	
	private  static  HashMap<String,Object> getQueryObject(int category, Long deviceId, String fromDate, String endDate)
	{
		StringBuilder baseQuery=getQuery(fieldsForPower, energy,dateBetween,deviceId);
		HashMap <String,Object> hMap = getQueryObject(category, deviceId, baseQuery);
		hMap.put(FacilioConstants.Reports.RANGE_FROM, fromDate);
		hMap.put(FacilioConstants.Reports.RANGE_END, endDate);
		return hMap;
	}

	private  static  HashMap<String,Object> getQueryObject(int category, Long deviceId, StringBuilder baseQuery)
	{
		Long fromRange=0L,endRange=0L;
		
		StringBuilder modifiedQuery=null;

		switch(category)
		{
		case FacilioConstants.Reports.TODAY:
		{
			fromRange=DateTimeUtil.getDayStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			modifiedQuery=getQuery(baseQuery,fieldsHour);
			break;
		}
		case FacilioConstants.Reports.THIS_YEAR:
		{
			fromRange=DateTimeUtil.getYearStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			modifiedQuery=getQuery(baseQuery,fieldsMonth);
			break;
		}
		case FacilioConstants.Reports.LAST_YEAR:
		{
			fromRange=DateTimeUtil.getYearStartTime(-1);
			endRange=DateTimeUtil.getYearStartTime()-1;
			modifiedQuery=getQuery(baseQuery,fieldsMonth);
			break;
		}
		case FacilioConstants.Reports.THIS_YEAR_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getYearStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			modifiedQuery=getQuery(baseQuery,fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.LAST_YEAR_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getYearStartTime(-1);
			endRange=DateTimeUtil.getYearStartTime()-1;
			modifiedQuery=getQuery(baseQuery,fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.THIS_HOUR:
		{
			fromRange=DateTimeUtil.getHourStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			modifiedQuery=new StringBuilder().append(select).append(baseQuery);
			break;
		}
		case FacilioConstants.Reports.LAST_HOUR:
		{
			fromRange=DateTimeUtil.getHourStartTime(-1);
			endRange=DateTimeUtil.getCurrenTime();
			modifiedQuery=new StringBuilder().append(select).append(baseQuery);
			break;
		}
		case FacilioConstants.Reports.THIS_MONTH_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getMonthStartTime();
			endRange=DateTimeUtil.getCurrenTime();
			modifiedQuery=getQuery(baseQuery,fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.LAST_MONTH_WITH_WEEK:
		{
			fromRange=DateTimeUtil.getMonthStartTime(-1);
			endRange=DateTimeUtil.getMonthStartTime()-1;
			modifiedQuery=getQuery(baseQuery,fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.CUSTOM_WITH_WEEK:
		{
			modifiedQuery=getQuery(baseQuery,fieldsWeek);
			break;
		}
		case FacilioConstants.Reports.CUSTOM_WITH_MONTH:
		{
			modifiedQuery=getQuery(baseQuery,fieldsMonth);
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

		if(modifiedQuery==null)
		{//this will also take care of FacilioConstants.Reports.CUSTOM_WITH_DATE
			baseQuery=getQuery(baseQuery,fieldsDate);
		}
		else
		{
			baseQuery=modifiedQuery;
		}

		HashMap <String,Object> hMap = new HashMap <String,Object>  ();
		hMap.put(FacilioConstants.Reports.RANGE_FROM, fromRange.toString());
		hMap.put(FacilioConstants.Reports.RANGE_END, endRange.toString());
		hMap.put(FacilioConstants.Reports.QUERY_STRING, baseQuery.toString());
		hMap.put(FacilioConstants.Reports.DEVICE_ID, deviceId);
		return hMap;
	}

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
	
	private static Long getDeviceID(String deviceName)
	{
		try 
		{
			Device device = DeviceAPI.getDevice(deviceName);
			if (device!=null)
			{
				return device.getId();
			}
		} 
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Error while fetching deviceName: "+deviceName, e);
		}
		return null;
	}

	private  static JSONObject getData (HashMap<String,Object> hMap)
	{
		JSONObject result =null;
		String from = (String)hMap.get(FacilioConstants.Reports.RANGE_FROM);
		String end = (String)hMap.get(FacilioConstants.Reports.RANGE_END);
		Long in_deviceId= (Long)hMap.get(FacilioConstants.Reports.DEVICE_ID);
		String fetchQuery=(String)hMap.get(FacilioConstants.Reports.QUERY_STRING);
		String deviceName=null;
		
		logger.log(Level.INFO, "The pstmt with "+from+" & "+end+" is \n"+fetchQuery);
		
		if(in_deviceId!=null)
		{
			deviceName=getDeviceName(in_deviceId);
		}

		try(Connection conn = FacilioConnectionPool.getInstance().getConnection();
				PreparedStatement psmt=conn.prepareStatement(fetchQuery))
		{
			psmt.setObject(1, from);
			psmt.setObject(2, end);
			if(in_deviceId!=null)
			{
				psmt.setObject(3, in_deviceId);
			}
			try(ResultSet rs=psmt.executeQuery())
			{
				result = new JSONObject();

				while(rs.next())
				{
					JSONObject json = new JSONObject();

					String key =rs.getObject(1).toString();
					long deviceId = (Long)rs.getObject(2);
					if(in_deviceId==null)
					{
						deviceName=getDeviceName(deviceId);
					}
					json.put("DEVICE_NAME", deviceName);
					json.put("POWER_CONSUMPTION", rs.getObject(3).toString());

					JSONArray array =(JSONArray) result.get(key);
					if(array==null) 
					{
						array= new JSONArray();
						result.put(key,array);
					}	
					array.add(json);
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
		logger.log(Level.INFO, "The result: "+result);
		return result;
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
	
	
	public static JSONObject getPowerData(int category, String deviceName)
	{
		
		return getData(getQueryObject(category, getDeviceID(deviceName)));
	}
	
	public static JSONObject getPowerData(int category, String deviceName,String fromDate, String endDate)
	{
		return getData(getQueryObject(category, getDeviceID(deviceName), fromDate, endDate));
	}

}
