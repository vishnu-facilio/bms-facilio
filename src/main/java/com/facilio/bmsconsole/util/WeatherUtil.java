package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.DateOperators;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.*;
import com.facilio.weather.context.WeatherStationContext;
import org.apache.commons.chain.Chain;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;


public class WeatherUtil {
	private static final Logger LOGGER = LogManager.getLogger(WeatherUtil.class.getName());
	private static String weatherURL=AwsUtil.getConfig("weather.url");
	private static String weatherParams="?units=si&exclude=flags,alerts";


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
			LOGGER.log(Level.INFO,"Weather API call count: "+apiCallCount);
			
		} catch (IOException e) {
			LOGGER.error("Exception ",e);		
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
	public static Map<String,Object>getWeatherData(SiteContext site, Long time) throws Exception {
		
		Map<String, Object> cordinates=getLocation(site);
		if(cordinates==null) {
			return null;
		}
		Double lat=(Double)cordinates.get("latitude");
		Double lng=(Double)cordinates.get("longtitude");
		if(lat==null || lng==null) {
			return null;
		}
		String weatherURL=WeatherUtil.getForecastURL(lat, lng,time);
		HttpURLConnection connection= WeatherUtil.getHttpURLConnection(weatherURL);
		String response=WeatherUtil.getResponse(connection);
		if(response==null){
			LOGGER.log(Level.INFO,"The response is null from the weather server");
			return null;
		}
		JSONObject weatherData=null;
		JSONParser parser = new JSONParser();
		
		try {
		weatherData= (JSONObject) parser.parse(response);
		}
		catch(Exception e) {
			throw new Exception(response, e);
		}
		return weatherData;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object>getWeatherData(WeatherStationContext weatherStation, Long time) throws Exception {
		
		
		Double lat=weatherStation.getLat();
		Double lng=weatherStation.getLng();
		if(lat==null || lng==null) {
			return null;
		}
		String weatherURL=WeatherUtil.getForecastURL(lat, lng,time);
		HttpURLConnection connection= WeatherUtil.getHttpURLConnection(weatherURL);
		String response=WeatherUtil.getResponse(connection);
		if(response==null){
			LOGGER.log(Level.INFO,"The response is null from the weather server");
			return null;
		}
		JSONObject weatherData=null;
		JSONParser parser = new JSONParser();
		try {
			weatherData= (JSONObject) parser.parse(response);
		}
		catch(Exception e) {
			throw new Exception(response, e);
		}
		return weatherData;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Long,Map<String,Object>>getWeatherDataMap(List<WeatherStationContext> weatherStations, Long time) throws Exception {
		
		Map<Long,Map<String,Object>> dataMap= new HashMap<Long,Map<String,Object>> ();
		for(WeatherStationContext weatherStation:weatherStations) {
			Long stationId=weatherStation.getId();
			Map<String,Object> weatherData=  getWeatherData(weatherStation,time);
			if(weatherData!=null) {
				dataMap.put(stationId, weatherData);
			}
		}
		return dataMap;
	}
	
	public static Map<String, Object> getLocation(SiteContext site) throws Exception {
		
		List<Map<String, Object>> weatherStation= getWeatherStationLocation(site.getWeatherStation());
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
		if(AccountUtil.getCurrentOrg().getOrgId()==88) {
			return getReadings("wbt");
		}	
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
	
	
	public static List<Map<String, Object>> getWeatherStationLocation(Long stationId) throws Exception {
		
		if(stationId==null) {
			return null;
		}

		 GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getWeatherStationsFields())
					.table("Weather_Stations")
					.andCustomWhere("Weather_Stations.ID=?",stationId );
				return builder.get();
			
	}
	
	
	public static List<WeatherStationContext> getAllWeatherStations() throws Exception {

		FacilioModule module = ModuleFactory.getWeatherStationModule();
		List<FacilioField> fields = FieldFactory.getWeatherStationsFields();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields);
		List<Map<String, Object>> props=selectBuilder.get();
		List<WeatherStationContext> weatherStations= new ArrayList<WeatherStationContext>();
		for (Map<String, Object> prop : props) {
			WeatherStationContext station = FieldUtil.getAsBeanFromMap(prop, WeatherStationContext.class);
			weatherStations.add(station);
		}
		return weatherStations;
	}
	
	public static Map<Long,List<ReadingContext>> getWeatherReading(String moduleName, Map<Long,List<ReadingContext>> readingsMap ) throws Exception {
		
		
		for(Map.Entry<Long, List<ReadingContext>> siteReadings : readingsMap.entrySet()) {
			
			Long siteId=siteReadings.getKey();
			List<ReadingContext> readingsList= siteReadings.getValue();
			Map<Long,ReadingContext> ttimeVsReading= getWeatherReadingForSite(moduleName,siteId,readingsList);
			for (ReadingContext reading: readingsList) {
				
				Long ttime=(Long)reading.getReading("forecastTime");
				reading.setTtime(ttime);
				ReadingContext ttimeReading = ttimeVsReading.get(ttime);
				if(ttimeReading!=null) {
					reading.setId(ttimeReading.getId());
				}
				else {
					reading.setId(-1);
				}
			}
		}
		return readingsMap;
	}
	
	private static Map<Long,ReadingContext> getWeatherReadingForSite(String moduleName, long siteId, List<ReadingContext> readingList) throws Exception
	{
		Condition parentCondition=CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(siteId),NumberOperators.EQUALS);
		Condition ttimeCondition=CriteriaAPI.getCondition("TTIME","ttime", getTtimeList(readingList),NumberOperators.EQUALS);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		String tableName=module.getTableName();
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																	.select(fields)
																	.table(tableName)
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(ReadingContext.class)
																	.andCondition(parentCondition)
																	.andCondition(ttimeCondition);
		List<ReadingContext> props = selectBuilder.get();
		LOGGER.info("Matching reading from DB: "+props);
		Map<Long,ReadingContext> ttimeVsReading = new HashMap<Long,ReadingContext>();
		for(ReadingContext reading : props) {
			
			Long ttime=reading.getTtime();
			ttimeVsReading.put(ttime, reading);
		}
		LOGGER.info("ttimeVsReading map for moduleName: "+ttimeVsReading);
		return ttimeVsReading;
	}
	
	private static String getTtimeList(List<ReadingContext> readingList) {

		StringJoiner ttimeCriteria = new StringJoiner(",");
		for(ReadingContext reading:readingList) {
			Object ttime= reading.getReading("forecastTime");
			if(ttime==null) {
				continue;
			}
			//need to adjust the ttime based on the roundoff..
			ttimeCriteria.add(String.valueOf(ttime));
		}
		LOGGER.info("ttimeCriteria >> "+ttimeCriteria);

		return ttimeCriteria.toString();
	}
public static void addReading(String moduleName,List<ReadingContext> readings) throws Exception {
		
		if(readings==null || readings.isEmpty()) {
			return;
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.READINGS, readings);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
		Chain addReading = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
		addReading.execute(context);
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
			LOGGER.error("Exception occurred ", e);
		}
	}
	
	
public static List<ReadingContext> getDailyForecastReadings(long siteId,String moduleName,Map<String, Object> weatherData, boolean forecast) {
		
		List<ReadingContext> dailyForecastReadings= new ArrayList<ReadingContext>();
		
		Map<String,Object> dailyWeather= (JSONObject)weatherData.get("daily");
		
		if(dailyWeather==null) {
			return dailyForecastReadings;
		}
		JSONArray dailyData= (JSONArray) dailyWeather.get("data");
		
		if(dailyData== null || dailyData.isEmpty()) {
			return dailyForecastReadings;
		}
		
		if(forecast) {
			dailyData.remove(0);
		}
		
        ListIterator< JSONObject> dataIterator= dailyData.listIterator();	
        while(dataIterator.hasNext()) {
        
        	JSONObject dailyWeatherReading=	dataIterator.next();
        	ReadingContext reading=WeatherUtil.getDailyReadingOld(siteId,moduleName,dailyWeatherReading,forecast);
        	if(reading!=null) {
        		dailyForecastReadings.add(reading);
        	}
        }
		
		return dailyForecastReadings;
	}


public static void populateMap(long siteId, ReadingContext reading, Map<Long, List<ReadingContext>> readingMap) {
	List<ReadingContext> readingsList=readingMap.get(siteId);
	if(readingsList==null) {
		readingsList= new ArrayList<ReadingContext>();
	}
	readingMap.put(siteId, readingsList);
	readingsList.add(reading);
}

public static void populateMap(long siteId, List<ReadingContext> list, Map<Long, List<ReadingContext>> readingMap) {
	List<ReadingContext> readingsList=readingMap.get(siteId);
	if(readingsList==null) {
		readingsList= new ArrayList<ReadingContext>();
		readingMap.put(siteId, readingsList);
	}
	readingsList.addAll(list);
}

public static List<ReadingContext> getReadingList(Map<Long,List<ReadingContext>> readingsMap){
	
	List<ReadingContext> readingsList=new ArrayList<ReadingContext>();
	for(Map.Entry<Long, List<ReadingContext>> readings : readingsMap.entrySet()) {
		 readingsList.addAll( readings.getValue());
	}
	return readingsList;
}

	public static List<ReadingContext> getHourlyForecastReadings(long siteId,String moduleName,Map<String, Object> weatherData,boolean forecast) {
		List<ReadingContext> hourlyForecastReadings= new ArrayList<ReadingContext>();

		Map<String,Object> hourlyWeather= (JSONObject)weatherData.get("hourly");
		
		if(hourlyWeather==null) {
			return hourlyForecastReadings;
		}
		
		JSONArray hourlyData= (JSONArray) hourlyWeather.get("data");
		if(hourlyData== null || hourlyData.isEmpty()) {
			return hourlyForecastReadings;
		}
		if(forecast) {
			hourlyData.remove(0);
		}
		
        ListIterator< JSONObject> dataIterator= hourlyData.listIterator();	
        while(dataIterator.hasNext()) {
        
        	JSONObject hourlyWeatherReading=dataIterator.next();
        	ReadingContext reading=WeatherUtil.getHourlyReadingOld(siteId,moduleName,hourlyWeatherReading);
        	if(reading!=null) {
        		hourlyForecastReadings.add(reading);
        	}
        }
		return hourlyForecastReadings;
	}
	
	public static ReadingContext getHourlyReadingOld(long siteId,String moduleName, Map<String,Object> hourlyWeather) {


		if(hourlyWeather==null) {
			return null;
		}

		ReadingContext reading= new ReadingContext();
		reading.setParentId(siteId);
		reading.addReading("temperature", hourlyWeather.get("temperature"));
		reading.addReading("icon", hourlyWeather.get("icon"));
		reading.addReading("summary", hourlyWeather.get("summary"));
		reading.addReading("humidity",hourlyWeather.get("humidity"));
		reading.addReading("dewPoint", hourlyWeather.get("dewPoint"));
		reading.addReading("pressure", hourlyWeather.get("pressure"));
		reading.addReading("apparentTemperature", hourlyWeather.get("apparentTemperature"));
		reading.addReading("precipitationIntensity", hourlyWeather.get("precipIntensity"));
		reading.addReading("precipitationIntensityError", hourlyWeather.get("precipIntensityError"));
		reading.addReading("precipitationProbability", hourlyWeather.get("precipProbability"));
		reading.addReading("precipitationType", hourlyWeather.get("precipType"));		
		reading.addReading("windSpeed", hourlyWeather.get("windSpeed"));
		reading.addReading("windGust", hourlyWeather.get("windGust"));
		reading.addReading("windBearing", hourlyWeather.get("windBearing"));
		reading.addReading("cloudCover", hourlyWeather.get("cloudCover"));
		reading.addReading("uvIndex", hourlyWeather.get("uvIndex"));
		reading.addReading("visibility", hourlyWeather.get("visibility"));
		reading.addReading("ozone", hourlyWeather.get("ozone"));
		reading.addReading("nearestStormDistance", hourlyWeather.get("nearestStormDistance"));
		reading.addReading("nearestStormBearing", hourlyWeather.get("nearestStormBearing"));
		//will be used for forecast alone..
		Long ttime=getAdjustedTtime(moduleName,(Long)hourlyWeather.get("time"));
		reading.addReading("forecastTime", ttime);
		return reading;
	}


public static ReadingContext getDailyReadingOld(long siteId,String moduleName, Map<String,Object> dailyWeather, boolean forecast) {
		
	
	if(dailyWeather==null) {
		return null;
	}
	
	ReadingContext reading= new ReadingContext();
	reading.setParentId(siteId);
	
	reading.addReading("icon", dailyWeather.get("icon"));
	reading.addReading("summary", dailyWeather.get("summary"));
	reading.addReading("humidity",dailyWeather.get("humidity"));
	reading.addReading("dewPoint", dailyWeather.get("dewPoint"));
	reading.addReading("pressure", dailyWeather.get("pressure"));
	reading.addReading("precipitationIntensity", dailyWeather.get("precipIntensity"));
	reading.addReading("precipitationIntensityError", dailyWeather.get("precipIntensityError"));
	reading.addReading("precipitationProbability", dailyWeather.get("precipProbability"));
	reading.addReading("precipitationType", dailyWeather.get("precipType"));
	reading.addReading("windSpeed", dailyWeather.get("windSpeed"));
	reading.addReading("windGust", dailyWeather.get("windGust"));
	reading.addReading("windBearing", dailyWeather.get("windBearing"));
	reading.addReading("cloudCover", dailyWeather.get("cloudCover"));
	reading.addReading("uvIndex", dailyWeather.get("uvIndex"));
	reading.addReading("visibility", dailyWeather.get("visibility"));
	reading.addReading("ozone", dailyWeather.get("ozone"));
	
	reading.addReading("moonPhase", dailyWeather.get("moonPhase"));
	reading.addReading("apparentTemperatureLow", dailyWeather.get("apparentTemperatureLow"));
	reading.addReading("apparentTemperatureHigh", dailyWeather.get("apparentTemperatureHigh"));
	reading.addReading("temperatureLow", dailyWeather.get("temperatureLow"));
	reading.addReading("temperatureHigh", dailyWeather.get("temperatureHigh"));
	reading.addReading("precipitationIntensityMax", dailyWeather.get("precipIntensityMax"));

	reading.addReading("apparentTemperatureLowTime", getTimeinMillis(dailyWeather.get("apparentTemperatureLowTime")));
	reading.addReading("apparentTemperatureHighTime", getTimeinMillis(dailyWeather.get("apparentTemperatureHighTime")));
	reading.addReading("temperatureLowTime", getTimeinMillis(dailyWeather.get("temperatureLowTime")));
	reading.addReading("temperatureHighTime", getTimeinMillis(dailyWeather.get("temperatureHighTime")));
	reading.addReading("precipitationIntensityMaxTime", getTimeinMillis(dailyWeather.get("precipIntensityMaxTime")));
	
	reading.addReading("sunriseTime", getTimeinMillis(dailyWeather.get("sunriseTime")));
	reading.addReading("sunsetTime", getTimeinMillis(dailyWeather.get("sunsetTime")));
	reading.addReading("windGustTime", getTimeinMillis(dailyWeather.get("windGustTime")));
	reading.addReading("uvIndexTime", getTimeinMillis(dailyWeather.get("uvIndexTime")));
	Long ttime=getAdjustedTtime(moduleName,(Long)dailyWeather.get("time"));
	//will be used for forecast alone..
	reading.addReading("forecastTime", ttime);
	if(!forecast) {
		reading.setTtime(ttime);
	}
	return reading;
}


public static ReadingContext getHourlyReading(String moduleName, Map<String,Object> hourlyWeather) {


	if(hourlyWeather==null) {
		return null;
	}

	ReadingContext reading= new ReadingContext();
	//reading.setParentId(siteId);
	reading.addReading("temperature", hourlyWeather.get("temperature"));
	reading.addReading("icon", hourlyWeather.get("icon"));
	reading.addReading("summary", hourlyWeather.get("summary"));
	reading.addReading("humidity",hourlyWeather.get("humidity"));
	reading.addReading("dewPoint", hourlyWeather.get("dewPoint"));
	reading.addReading("pressure", hourlyWeather.get("pressure"));
	reading.addReading("apparentTemperature", hourlyWeather.get("apparentTemperature"));
	reading.addReading("precipitationIntensity", hourlyWeather.get("precipIntensity"));
	reading.addReading("precipitationIntensityError", hourlyWeather.get("precipIntensityError"));
	reading.addReading("precipitationProbability", hourlyWeather.get("precipProbability"));
	reading.addReading("precipitationType", hourlyWeather.get("precipType"));		
	reading.addReading("windSpeed", hourlyWeather.get("windSpeed"));
	reading.addReading("windGust", hourlyWeather.get("windGust"));
	reading.addReading("windBearing", hourlyWeather.get("windBearing"));
	reading.addReading("cloudCover", hourlyWeather.get("cloudCover"));
	reading.addReading("uvIndex", hourlyWeather.get("uvIndex"));
	reading.addReading("visibility", hourlyWeather.get("visibility"));
	reading.addReading("ozone", hourlyWeather.get("ozone"));
	reading.addReading("nearestStormDistance", hourlyWeather.get("nearestStormDistance"));
	reading.addReading("nearestStormBearing", hourlyWeather.get("nearestStormBearing"));
	//will be used for forecast alone..
	Long ttime=getAdjustedTtime(moduleName,(Long)hourlyWeather.get("time"));
	reading.addReading("forecastTime", ttime);
	return reading;
}


public static ReadingContext getDailyReading(String moduleName, Map<String,Object> dailyWeather, boolean forecast) {
	

if(dailyWeather==null) {
	return null;
}

ReadingContext reading= new ReadingContext();
//reading.setParentId(siteId);

reading.addReading("icon", dailyWeather.get("icon"));
reading.addReading("summary", dailyWeather.get("summary"));
reading.addReading("humidity",dailyWeather.get("humidity"));
reading.addReading("dewPoint", dailyWeather.get("dewPoint"));
reading.addReading("pressure", dailyWeather.get("pressure"));
reading.addReading("precipitationIntensity", dailyWeather.get("precipIntensity"));
reading.addReading("precipitationIntensityError", dailyWeather.get("precipIntensityError"));
reading.addReading("precipitationProbability", dailyWeather.get("precipProbability"));
reading.addReading("precipitationType", dailyWeather.get("precipType"));
reading.addReading("windSpeed", dailyWeather.get("windSpeed"));
reading.addReading("windGust", dailyWeather.get("windGust"));
reading.addReading("windBearing", dailyWeather.get("windBearing"));
reading.addReading("cloudCover", dailyWeather.get("cloudCover"));
reading.addReading("uvIndex", dailyWeather.get("uvIndex"));
reading.addReading("visibility", dailyWeather.get("visibility"));
reading.addReading("ozone", dailyWeather.get("ozone"));

reading.addReading("moonPhase", dailyWeather.get("moonPhase"));
reading.addReading("apparentTemperatureLow", dailyWeather.get("apparentTemperatureLow"));
reading.addReading("apparentTemperatureHigh", dailyWeather.get("apparentTemperatureHigh"));
reading.addReading("temperatureLow", dailyWeather.get("temperatureLow"));
reading.addReading("temperatureHigh", dailyWeather.get("temperatureHigh"));
reading.addReading("precipitationIntensityMax", dailyWeather.get("precipIntensityMax"));

reading.addReading("apparentTemperatureLowTime", getTimeinMillis(dailyWeather.get("apparentTemperatureLowTime")));
reading.addReading("apparentTemperatureHighTime", getTimeinMillis(dailyWeather.get("apparentTemperatureHighTime")));
reading.addReading("temperatureLowTime", getTimeinMillis(dailyWeather.get("temperatureLowTime")));
reading.addReading("temperatureHighTime", getTimeinMillis(dailyWeather.get("temperatureHighTime")));
reading.addReading("precipitationIntensityMaxTime", getTimeinMillis(dailyWeather.get("precipIntensityMaxTime")));

reading.addReading("sunriseTime", getTimeinMillis(dailyWeather.get("sunriseTime")));
reading.addReading("sunsetTime", getTimeinMillis(dailyWeather.get("sunsetTime")));
reading.addReading("windGustTime", getTimeinMillis(dailyWeather.get("windGustTime")));
reading.addReading("uvIndexTime", getTimeinMillis(dailyWeather.get("uvIndexTime")));
Long ttime=getAdjustedTtime(moduleName,(Long)dailyWeather.get("time"));
//will be used for forecast alone..
reading.addReading("forecastTime", ttime);
if(!forecast) {
	reading.setTtime(ttime);
}
return reading;
}

private static long getAdjustedTtime(String moduleName,Long ttime)  {
	
	
	try {
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	FacilioModule module = modBean.getModule(moduleName);
	ZonedDateTime zdt = DateTimeUtil.getDateTime(ttime, true);
	zdt = zdt.truncatedTo(module.getDateIntervalUnit());
	ttime= DateTimeUtil.getMillis(zdt, true);
	}
	catch(Exception e) {
		
		ttime=getTimeinMillis(ttime);
	}
	return ttime;
}
	
private static Long getTimeinMillis(Object time) {
	
	return time!=null? (Long)time*1000 : null;
}

}
