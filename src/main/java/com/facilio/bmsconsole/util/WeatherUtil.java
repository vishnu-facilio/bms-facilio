package com.facilio.bmsconsole.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.AwsUtil;


public class WeatherUtil {
	
	private static String weatherURL=AwsUtil.getConfig("weather.url")+AwsUtil.getConfig("weather.key")+"/";
	private static String weatherParams="?units=si&exclude=flags,daily,hourly,alerts";
	
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
	
	
	public static  HttpURLConnection getHttpURLConnection (String requestURL) throws Exception{
		

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
	
	public static String getForecastURL(double lat, double longitude) {
		
		return getForecastURL(lat,longitude,null);
	}
	
	
	public static String getForecastURL(double lat, double longitude,Long time) {

		StringBuilder url = new StringBuilder(weatherURL);
		url.append(lat+","+longitude);
		if(time!=null) {
			url.append(","+time);
		}
		url.append(weatherParams);
		return url.toString();
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
			e.printStackTrace();
		}
	}
	
}
