package com.facilio.bmsconsole.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class GetTrendLineCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GetTrendLineCommand.class.getName());
	private JSONObject trendLinePropObj = new JSONObject();
	private String[] trendLineProp = new String[] {"rmse", "rSquare", "coef"};
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		String chartState = report.getChartState();
		if(chartState != null){
			JSONObject chartObj = (JSONObject)new JSONParser().parse(chartState);
			JSONObject trendLineObj = (JSONObject)chartObj.get("trendLine");
			if((boolean) trendLineObj.get("enable")){
				JSONObject reportData = (JSONObject)context.get(FacilioConstants.ContextNames.REPORT_DATA);
				Map<String, List<String>> trendLineMap = getTrendLinePoints(reportData, String.valueOf(trendLineObj.get("degree")));
				
				if(trendLineMap != null && trendLineMap.size() != 0){
					setTrendLinePoints(reportData, trendLineMap);
					setDataPoints(report.getDataPoints(), trendLineMap);
					reportData.put("trendLineProp", trendLinePropObj);
				}
			}
		}
		return false;
	}
	
	private Map<String, List<String>> getTrendLinePoints(JSONObject reportData, String degree) throws Exception{
		ArrayList<Object> dataPoints = (ArrayList<Object>) reportData.get("data");
		
		if(dataPoints != null && dataPoints.size() != 0){
			Map<String, List<String>> trendLineMap =  new HashMap<>();
			String xAxis = "X", yAxis = null;
		
			Map<String, Long> data = (Map<String, Long>) dataPoints.get(0);
			for(Object key : data.keySet())
		    {
				if(!key.equals(xAxis)){
					yAxis = (String) key;
			    	String filePath = createTempCSV(dataPoints, xAxis, yAxis);
					runOnCommandLine(filePath, trendLineMap, yAxis, degree);
				}
			};
			return trendLineMap;
		}
		return null;
	}
	
	private String createTempCSV(ArrayList<Object> dataPoints, String xAxis, String yAxis) {
    	FileFormat format = FileFormat.CSV;
    	Long orgId = AccountUtil.getCurrentOrg().getOrgId();
    	
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
            		
			        str.append(((Map) data).get(xAxis)).append(',');
			        str.append(((Map) data).get(yAxis)).append(',');
    			        
    				writer.append(StringUtils.stripEnd(str.toString(), ","));
                	writer.append('\n');
        		}
            	writer.flush();
            	writer.close();
            	LOGGER.info("temp CSV file path" + filePath);
        	  }catch(IOException e) {
        		  LOGGER.info("Exception occurred", e);
        	  }
          }
          return filePath;
          
    }
	
	private void runOnCommandLine(String filePath, Map<String, List<String>> trendLineMap, String trendLine, String degree) throws Exception{
		ClassLoader classLoader = GetTrendLineCommand.class.getClassLoader();
		String ransacFilePath = classLoader.getResource("conf/AI/ransac.py").getPath();
		
		String[] command = new String[]{"/usr/local/bin/python3", ransacFilePath, filePath, degree};
		
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        process.waitFor();
        
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        List<String> trendLineList = new ArrayList<String>();
        String line = reader.readLine();
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
            if(trendLineList != null){
            	trendLineMap.put(trendLine, trendLineList);
            }
        }
	}
	
	private void prepareTrendLineProperties(String trendLine, String points){
		String[] props = points.replaceAll("[\\[\\],]","").split("\\s+");
		
		JSONObject dataProp = new JSONObject();
		dataProp.put(trendLineProp[0], props[0]);
		dataProp.put(trendLineProp[1], props[1]);
		dataProp.put(trendLineProp[2], props[2]);
		
		trendLinePropObj.put(trendLine+"_TrendLine", dataProp);
	}

	private void setTrendLinePoints(JSONObject reportData, Map<String, List<String>> trendLineMap){
		ArrayList<Map> dataPoints = (ArrayList<Map>) reportData.get("data");
		String xAxis = "X";
				
		for(int index = 0; index < dataPoints.size(); index++)
	    {
			Map<String, Long> data = (Map<String, Long>) dataPoints.get(index);
			for(Object key : trendLineMap.keySet())
		    {
				if(!key.equals(xAxis)){
					List<String> trendLinePoints = trendLineMap.get(key);
					((Map) data).put(key+"_TrendLine", trendLinePoints.get(index));
				}
			};
		};
	}
	
	private void setDataPoints(List<ReportDataPointContext> dataPoints, Map<String, List<String>> trendLineMap){
		List<ReportDataPointContext> trendLinePoints = new ArrayList<>();
		for(ReportDataPointContext dataPoint : dataPoints){
			String alias = dataPoint.getAliases().get("actual");
			if(trendLineMap.containsKey(alias)){
				ReportDataPointContext trendLinePoint = new ReportDataPointContext();
				
				alias+="_TrendLine";
				Map<String, String> aliases = new HashMap<>();
				aliases.put("actual", alias);
				
				trendLinePoint.setAliases(aliases);
				trendLinePoint.setName(alias);
				trendLinePoint.setType(ReportDataPointContext.DataPointType.TRENDLINE);
				trendLinePoint.setxAxis(dataPoint.getxAxis());
				trendLinePoint.setyAxis(dataPoint.getyAxis());
				trendLinePoint.setCriteria(dataPoint.getCriteria());
				trendLinePoint.setGroupByFields(dataPoint.getGroupByFields());
				trendLinePoint.setDateField(dataPoint.getDateField());
				trendLinePoints.add(trendLinePoint);
			}
		}
		dataPoints.addAll(trendLinePoints);
	}
	
	private String prepareCoeff(String[] coeffArray){
		return null;
	}
}