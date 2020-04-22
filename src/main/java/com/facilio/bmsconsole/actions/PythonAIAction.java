package com.facilio.bmsconsole.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.fs.FileInfo.FileFormat;

public class PythonAIAction extends FacilioAction {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(PythonAIAction.class.getName());
	
	private JSONObject trendLinePropObj = new JSONObject();
	private String[] trendLineProp = new String[] {"rmse", "rSquare", "coef"};
	
	
	private JSONObject reportData = new JSONObject();
	public void setReportData(JSONObject data){
		this.reportData = data;
	}
	public JSONObject getReportData(){
		return this.reportData;
	}
	
	private JSONObject trendLineObj = new JSONObject();
	public void setTrendLineObj(JSONObject trendLine){
		this.trendLineObj = trendLine;
	}
	public JSONObject getTrendLineObj(){
		return this.trendLineObj;
	}
	
	private String xaxis = "X";
	public void setXaxis(String xaxis){
		this.xaxis = xaxis;
	}
	public String getXaxis(){
		return this.xaxis;
	}
	
	private JSONArray yaxis = new JSONArray();
	public void setYaxis(JSONArray yaxis){
		this.yaxis = yaxis;
	}
	public JSONArray getYaxis(){
		return this.yaxis;
	}
	
	private String orgId;
	
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public String getOrgId() {
		return this.orgId;
	}
	
			
	public String getTrendLine() throws Exception {
		LOGGER.info("trendline request landed");
		if(trendLineObj != null && !trendLineObj.isEmpty() && (boolean) trendLineObj.get("enable")){
			String degree = "1";
			if(trendLineObj.get("type").equals("2")){
				degree = String.valueOf(trendLineObj.get("degree"));
			}
			Map<String, List<String>> trendLineMap = getTrendLineData(reportData, degree);
			
			if(trendLineMap != null && trendLineMap.size() != 0){
				setTrendLineData(reportData, trendLineMap);
				reportData.put("trendLineProp", trendLinePropObj);
				setResult("reportData", reportData);
			}
		}
		return SUCCESS;
	}
	
	private Map<String, List<String>> getTrendLineData(JSONObject reportData, String degree) throws Exception{
		ArrayList<Object> datas = (ArrayList<Object>) reportData.get("data");
		
		if(datas != null && datas.size() != 0){
			Map<String, List<String>> trendLineMap =  new HashMap<>();
			String yAxis = null;
		
			Map<String, Long> data = (Map<String, Long>) datas.get(0);
			for(Object key : data.keySet())
		    {
				if(!key.equals(xaxis) && this.yaxis.contains(key)){
					yAxis = (String) key;
			    	String filePath = createTempCSV(datas, xaxis, yAxis);
			    	LOGGER.info("tempfile path---->"+filePath);
					runOnCommandLine(filePath, trendLineMap, yAxis, degree);
				}
			};
			LOGGER.info("trendline mp"+trendLineMap.toString());
			return trendLineMap;
		}
		return null;
	}
	
	private String createTempCSV(ArrayList<Object> dataPoints, String xAxis, String yAxis) {
    	FileFormat format = FileFormat.CSV;
    	
    	File directory = new File(System.getProperty("java.io.tmpdir")+"/"+orgId+"/");
    	String filePath = null;
        boolean directoryExist = (directory.exists() && directory.isDirectory()) ? Boolean.TRUE : directory.mkdirs();
          
        if(directoryExist) {
        	try {
        		File tempFile = File.createTempFile("inputRansac", format.getExtention(), directory);
        		filePath = tempFile.getAbsolutePath();
        		
            	FileWriter writer = new FileWriter(tempFile, true);
            	StringBuilder headerStr = new StringBuilder();
            	headerStr.append("x").append(',');
            	headerStr.append("y").append(',');
            	writer.append(StringUtils.stripEnd(headerStr.toString(), ","));
            	writer.append('\n');
            	for(Object data : dataPoints){
            		StringBuilder str = new StringBuilder();
            		Object xValue = ((Map) data).get(xAxis);
            		Object yValue = ((Map) data).get(yAxis);
			        str.append(xValue != null ? xValue : 0).append(',');
			        str.append(yValue != null ? yValue : 0).append(',');
    			        
    				writer.append(StringUtils.stripEnd(str.toString(), ","));
                	writer.append('\n');
        		}
            	writer.flush();
            	writer.close();
            	LOGGER.info("temp CSV file path" + filePath);
        	  }catch(IOException e) {
        		  LOGGER.info("Exception occurred"+e);
        	  }
          }
          return filePath;
          
    }
	
	private void runOnCommandLine(String filePath, Map<String, List<String>> trendLineMap, String trendLine, String degree) throws Exception{
		ClassLoader classLoader = PythonAIAction.class.getClassLoader();
		String ransacFilePath = classLoader.getResource("conf/AI/ransac.py").getPath();
		
		String pythonPath = FacilioProperties.getPythonPath();
		String[] command = new String[]{pythonPath, ransacFilePath, filePath, degree};
        ProcessBuilder builder = new ProcessBuilder(command);
        
        List<String> trendLineList = new ArrayList<String>();
        try{
	        Process process = builder.start();
	        
	        BufferedReader reader;
	        String line = null;
	        try (InputStream stdout = process.getInputStream()){
	        	reader = new BufferedReader(new InputStreamReader(stdout));
	        	line = reader.readLine();
	            if(line.equals("Error")){
	            	LOGGER.info("Error occurred while execute python Script");
	            }else{
		            boolean first = true;
		            while (line != null) {
		            	String points = line;
		            	line = reader.readLine();
		            	if(first){
		            		first = false;
		            	}
		            	else if(line == null){
		            		prepareTrendLineProperties(trendLine, points);
		                }
		            	else{
		                	trendLineList.add(points.trim().split("\\s+")[2]);
		                }
		            }
		            process.waitFor();
		            if(trendLineList != null){
		            	trendLineMap.put(trendLine, trendLineList);
		            }
	            }
	        }
	        catch (Exception e) {
	        	LOGGER.info("Exception occurred "+e);
			}
	        
	        try (InputStream stderr = process.getErrorStream()){
	        	reader = new BufferedReader(new InputStreamReader(stderr));
	            line = null;
	            StringBuilder sb = new StringBuilder();
	            while ((line = reader.readLine()) != null) {
	            		sb.append(line);
	            }
	            LOGGER.info("Commmand line ERROR STREAM: " + sb.toString());
	        }
	        process.destroy();
        } catch (IOException e) {
        	LOGGER.info("Exception occurred "+e);
        }
	}
	
	private void prepareTrendLineProperties(String trendLine, String points){
		String[] props = points.replaceAll("[\\[\\],]","").split("\\s+");
		
		JSONObject dataProp = new JSONObject();
		dataProp.put(trendLineProp[0], props[0]);
		dataProp.put(trendLineProp[1], props[1]);
		dataProp.put(trendLineProp[2], Arrays.copyOfRange(props, 2, props.length));
		
		trendLinePropObj.put(trendLine+"_TrendLine", dataProp);
	}

	private void setTrendLineData(JSONObject reportData, Map<String, List<String>> trendLineMap){
		ArrayList<Map> datas = (ArrayList<Map>) reportData.get("data");
				
		for(int index = 0; index < datas.size(); index++)
	    {
			Map<String, Long> data = (Map<String, Long>) datas.get(index);
			for(Object key : trendLineMap.keySet())
		    {
				if(!key.equals(xaxis)){
					List<String> trendLineData = trendLineMap.get(key);
					((Map) data).put(key+"_TrendLine", trendLineData.get(index));
				}
			};
		};
	}
}
