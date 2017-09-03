package com.facilio.bmsconsole.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.transaction.FacilioConnectionPool;

public class ReportsUtil 
{
	private StringBuilder select = new StringBuilder(" SELECT ");

	private StringBuilder fieldsForPower = new StringBuilder (" DEVICE_ID, ROUND(SUM(TOTAL_ENERGY_CONSUMPTION_DELTA),2)");

	private StringBuilder fieldsDate= new StringBuilder("ADDED_DATE");

	private StringBuilder fieldsMonth= new StringBuilder("ADDED_MONTH");

	private StringBuilder fieldsHour= new StringBuilder("ADDED_HOUR");

	private StringBuilder fieldsWeek= new StringBuilder("ADDED_WEEK");

	private StringBuilder from = new StringBuilder(" FROM ");
	
	private StringBuilder energy= new StringBuilder(" ENERGY_DATA ");
	
	private StringBuilder where = new StringBuilder(" WHERE ");

	private StringBuilder timeBetween = new StringBuilder(" ADDED_TIME BETWEEN ? AND ? ");
	
	private StringBuilder dateBetween = new StringBuilder(" ADDED_DATE BETWEEN ? AND ? ");

	private StringBuilder device_id= new StringBuilder(" DEVICE_ID = ? ");

	private StringBuilder groupBy = new StringBuilder(" GROUP BY DEVICE_ID ");

	private StringBuilder andOperator = new StringBuilder(" AND ");

	private StringBuilder orOperator = new StringBuilder(" OR ");

	private StringBuilder separator = new StringBuilder(" , ");

	
	private StringBuilder getQuery(StringBuilder query, StringBuilder groupByColumn)
	{
		return new StringBuilder().append(select).append(groupByColumn).append(separator).
		append(query).append(separator).append(groupByColumn);
	}
	
	private StringBuilder getQuery(StringBuilder selectFields, StringBuilder table, StringBuilder betweenCol, Long deviceId)
	{
		StringBuilder baseQuery=new StringBuilder();
		if(deviceId==null)
		{
			baseQuery=baseQuery.append(selectFields).append(from).append(table).append(where).
					append(betweenCol).append(groupBy);
		}
		else
		{
			baseQuery= baseQuery.append(selectFields).append(from).append(table).append(where).
					append(betweenCol).append(andOperator).append(device_id).append(groupBy); 
		}
		return baseQuery;
	}

	public HashMap<String,Object> getQueryObject(int category, Long deviceId)
	{
		StringBuilder baseQuery=getQuery(fieldsForPower, energy, timeBetween,deviceId);
		return getQueryObject(category, deviceId, baseQuery);
	}
	
	public HashMap<String,Object> getQueryObject(int category, Long deviceId, String fromDate, String endDate)
	{
		StringBuilder baseQuery=getQuery(fieldsForPower, energy,dateBetween,deviceId);
		HashMap <String,Object> hMap = getQueryObject(category, deviceId, baseQuery);
		hMap.put(FacilioConstants.Reports.RANGE_FROM, fromDate);
		hMap.put(FacilioConstants.Reports.RANGE_END, endDate);
		return hMap;
	}

	public HashMap<String,Object> getQueryObject(int category, Long deviceId, StringBuilder baseQuery)
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
			fromRange=DateTimeUtil.getYearStartTime(1);
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
			fromRange=DateTimeUtil.getYearStartTime(1);
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
			fromRange=DateTimeUtil.getHourStartTime(1);
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
			fromRange=DateTimeUtil.getMonthStartTime(1);
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
			fromRange=DateTimeUtil.getMonthStartTime(1);
			endRange=DateTimeUtil.getMonthStartTime()-1;
			break;
		}
		case FacilioConstants.Reports.YESTERDAY:
		{
			fromRange=DateTimeUtil.getDayStartTime(1);
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
			fromRange=DateTimeUtil.getDayStartTime(30);
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
			fromRange=DateTimeUtil.getWeekStartTime(1);
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

	
	


	public JSONObject getData (HashMap<String,Object> queryObj)
	{
		JSONObject result =null;
		
		HashMap <String,Object> hMap =queryObj;
		String from = (String)hMap.get(FacilioConstants.Reports.RANGE_FROM);
		String end = (String)hMap.get(FacilioConstants.Reports.RANGE_END);
		Long deviceId= (Long)hMap.get(FacilioConstants.Reports.DEVICE_ID);
		String fetchQuery=(String)hMap.get(FacilioConstants.Reports.QUERY_STRING);
		
		System.out.println("The pstmt with "+from+" & "+end+" is \n"+fetchQuery);

		try(Connection conn = FacilioConnectionPool.getInstance().getConnection();
				PreparedStatement psmt=conn.prepareStatement(fetchQuery))
		{
			psmt.setObject(1, from);
			psmt.setObject(2, end);
			if(deviceId!=null)
			{
				psmt.setObject(3, deviceId);	
			}
			try(ResultSet rs=psmt.executeQuery())
			{
				result = new JSONObject();

				while(rs.next())
				{
					JSONObject json = new JSONObject();

					String key =rs.getObject(1).toString();
					
					
					
					long device_id = (Long)rs.getObject(2);
					//TODO get the deviceName for this device_id here..
					String deviceName= ""+device_id;
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
				e.printStackTrace();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		System.out.println("The result: "+result);
		return result;

	}

}
