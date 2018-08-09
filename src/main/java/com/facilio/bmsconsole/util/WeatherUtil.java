package com.facilio.bmsconsole.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;


public class WeatherUtil {
	private static final Logger LOGGER = LogManager.getLogger(WeatherUtil.class.getName());
	private static String weatherURL=AwsUtil.getConfig("weather.url");
	private static String weatherParams="?units=si&exclude=flags,daily,hourly,alerts";
	private static org.apache.log4j.Logger log = LogManager.getLogger(WeatherUtil.class.getName());


	private static String[] apiKeys=AwsUtil.getConfig("weather.key").trim().split(",");
	
	private static int currentKey=0;
	private static int apiCallCount=0;
	
	public static String getResponse (HttpURLConnection connection) throws Exception {

		String response = "";

		try {
			
			connection.connect();
			String responseTime = connection.getHeaderField("X-Response-Time");
			System.err.println("response Time: "+responseTime);
			
			int responseCode=connection.getResponseCode();

			InputStream iStream=null;
			if(responseCode == HttpURLConnection.HTTP_OK){
				iStream=getInputStream(connection);
			}
			else {
				iStream=connection.getErrorStream();
			}

			try (BufferedReader reader=new BufferedReader(new InputStreamReader(iStream,"UTF-8"))) {
				String rawResponse="";
				while( (rawResponse = reader.readLine()) != null ) {
					response = rawResponse;
				}
			}
			
			String apiCount=connection.getHeaderField("X-Forecast-API-Calls");
			if(apiCount!=null) {
				apiCallCount=Integer.valueOf(apiCount);
			}
			System.err.println("Weather API call count: "+apiCallCount);
			
		} catch (IOException e) {
			System.err.println("Error: "+e.getMessage());		
			response = null;
		} finally {		
			connection.disconnect();
		}
		return response;
	}

	
	public static synchronized HttpURLConnection getHttpURLConnection (String requestURL) throws Exception{
		

		URL request = new URL(requestURL);
		HttpURLConnection connection = (HttpURLConnection) request.openConnection();

		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		connection.setConnectTimeout(30000);
		connection.setReadTimeout(30000);
		return connection;
	}
	
	
	
	private static InputStream getInputStream(HttpURLConnection connection) throws Exception{
		
		String encoding = connection.getContentEncoding();
		InputStream iStream=connection.getInputStream();
		
		if("gzip".equalsIgnoreCase(encoding)) {
			 return new GZIPInputStream( iStream );
		}
		else if("deflate".equalsIgnoreCase(encoding)){
			return new InflaterInputStream(iStream, new Inflater(true) );
		}
		return iStream;
	}
	
	private static String getAPIKey() {

		if (apiCallCount>960) {
			int temp=currentKey+1;
			currentKey=(temp<apiKeys.length)?temp:0;
		}
		return apiKeys[currentKey];
	}
	
	public static String getForecastURL(double lat, double longitude) {
		
		return getForecastURL(lat,longitude,null);
	}
	
	
	public static String getForecastURL(double lat, double longitude,Long time) {

		StringBuilder url = new StringBuilder(weatherURL);
		url.append(getAPIKey());
		url.append("/");
		url.append(lat);
		url.append(",");
		url.append(longitude);
		if(time!=null) {
			url.append(","+time);
		}
		url.append(weatherParams);
		return url.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object>getWeatherData(SiteContext site) throws Exception {
		
		Map<String, Object> cordinates=getLocation(site);
		if(cordinates==null) {
			return null;
		}
		Double lat=(Double)cordinates.get("latitude");
		Double lng=(Double)cordinates.get("longtitude");
		if(lat==null || lng==null) {
			return null;
		}
		String weatherURL=WeatherUtil.getForecastURL(lat, lng);
		HttpURLConnection connection= WeatherUtil.getHttpURLConnection(weatherURL);
		String response=WeatherUtil.getResponse(connection);
		if(response==null){
			System.err.println("The response is null from the weather server");
			return null;
		}
		JSONParser parser = new JSONParser();
		JSONObject weatherData= (JSONObject) parser.parse(response);
		return (JSONObject)weatherData.get("currently");
		
	}
	
	public static Map<String, Object> getLocation(SiteContext site) throws Exception {
		
		List<Map<String, Object>> weatherStation= getWeatherStation(site.getId());
		if(weatherStation!=null && !weatherStation.isEmpty()) {
			Map<String, Object> prop = weatherStation.get(0);
			LOGGER.info("Weather Util::ORGID::"+AccountUtil.getCurrentOrg().getId()+"::Location::"+prop);
			return prop;
		}
		
		LocationContext location= site.getLocation();
		if(location==null) {
			return null;
		}
		Double lat=location.getLat();
		Double lng=location.getLng();
		if(lat==null || lng==null) {
			return null;
		}
		Map<String,Object> cordinates= new HashMap<String,Object>();
		cordinates.put("latitude", lat);
		cordinates.put("longtitude", lng);
		return cordinates;
	}
	
	
	public static Map<Long,List<Map<String,Object>>> getWeatherReadings() throws Exception {
		return getReadings(FacilioConstants.ContextNames.WEATHER_READING);
	}
	
	public static Map<Long,List<Map<String,Object>>> getWetBulbReadings() throws Exception {
		return getReadings(FacilioConstants.ContextNames.PSYCHROMETRIC_READING );
	}
	
public static Map<Long,List<Map<String,Object>>> getReadings(String moduleName) throws Exception {
		
		
		ModuleBean modBean= (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(moduleName))
				.moduleName(moduleName)
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition("TTIME", "ttime",null, DateOperators.YESTERDAY));
		List<Map<String,Object>> weatherReadings= builder.getAsProps();
		
		Map<Long,List<Map<String,Object>>> siteVsWeatherData= new HashMap<Long,List<Map<String,Object>>>();
		for(Map<String,Object> weatherReading:weatherReadings) {
			
			Long siteId=(Long)weatherReading.remove("parentId");
			List<Map<String,Object>> siteData= siteVsWeatherData.get(siteId);
			if(siteData==null) {
				siteData= new ArrayList<Map<String,Object>>();
				siteVsWeatherData.put(siteId, siteData);
			}
			siteData.add(weatherReading);
		}
		return siteVsWeatherData;
	}
	
	
	public static Double getDegreeDays(String ddName,String temperatureField, Double ddBaseTemp,List<Map<String,Object>> weatherReadings) {
		
		int size=weatherReadings.size();
		Double totalTemp= new Double(0);
		for(Map<String,Object> reading: weatherReadings){
			
			Double temperature=(Double)reading.get(temperatureField);
			if(temperature==null ){
				continue;
			}
			if(ddName.equalsIgnoreCase("heat")) {
				if(temperature > ddBaseTemp) {
					continue;
				}
				totalTemp+=(ddBaseTemp-temperature);
			}
			else {
				if(temperature < ddBaseTemp) {
					continue;
				}
				totalTemp+=(temperature - ddBaseTemp);
			}
		}
		Double dd=totalTemp/size; //if the data is missing, we have to calculate with available data.. hence dividing with size.
		return dd;
	}
	
	
	public static List<Map<String, Object>> getWeatherStation(long siteId) throws Exception {
		
		 List<FacilioField> fields = new ArrayList<>();
		 fields.add( FieldFactory.getField("latitude","LAT",FieldType.NUMBER));
		 fields.add(FieldFactory.getField("longtitude","LNG",FieldType.NUMBER));

		 GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(fields)
					.table("Weather_Stations")
					.innerJoin("Locations")
					.on("Weather_Stations.LOCATION_ID=Locations.ID")
	                .andCustomWhere("Weather_Stations.ORGID=?",AccountUtil.getCurrentOrg().getOrgId())
					.andCustomWhere("Weather_Stations.SITE_ID=?",siteId );
				return builder.get();
			
	}
	
	public static void main (String args[]) {
		//for unit testing..
		try {

			String weatherURL="https://api.darksky.net/forecast/21acf692b2952cac20b8ecdc56bea27c/"
					+ "25.13226640,55.38819770"+weatherParams;
			HttpURLConnection connection= WeatherUtil.getHttpURLConnection(weatherURL);
			String response=WeatherUtil.getResponse(connection);
			if(response==null){
				System.err.println("The response is null from the weather server");
			}
			JSONParser parser = new JSONParser();
			JSONObject weatherData= (JSONObject) parser.parse(response);
			JSONObject currentWeather=(JSONObject)weatherData.get("currently");
			System.err.println(currentWeather);

		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
	}
	
}
