package com.facilio.bmsconsole.util;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.facilio.bmsconsole.actions.ImportMetaInfo;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.constants.FacilioConstants;

public class ImportAPI {

	
	public static void importPasteParsedData(List<Map<Integer,String>> parsedDatas, Map<Integer,String> columnMaping,ImportMetaInfo metainfo) throws Exception {
		
		List<ReadingContext> readingsList = new ArrayList<ReadingContext>();
		
		for(Map<Integer, String> parsedData:parsedDatas) {
			
			HashMap <String, Object> props = new LinkedHashMap<String,Object>();
			columnMaping.forEach((key,value) ->
			{
				Object cellValue = parsedData.get(key);
				boolean isfilledByLookup = false;
				
				if(cellValue != null && !cellValue.toString().equals("")) {
					
					FacilioField facilioField = metainfo.getFacilioFieldMapping(metainfo.getModule().getName()).get(value);
					if(facilioField != null && facilioField.getDataTypeEnum().equals(FieldType.LOOKUP)) {
						LookupField lookupField = (LookupField) facilioField;
						List<Map<String, Object>> lookupPropsList = ProcessXLS.getLookupProps(lookupField,cellValue);
						if(lookupPropsList != null) {
							Map<String, Object> lookupProps = lookupPropsList.get(0);
							props.put(value, lookupProps);
							isfilledByLookup = true;
						}
					}
				}
				if(!isfilledByLookup) {
					props.put(value, cellValue);
				}
			});
			
			if(metainfo.getModule().getName().equals(FacilioConstants.ContextNames.ASSET)) {
				
				Long spaceId = ProcessXLS.getSpaceIDforAssets(props);
				
				props.put("space", spaceId);
				props.put("resourceType", ResourceType.ASSET.getValue());
				 
			}
			
			ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
			reading.setParentId(metainfo.getAssetId());
			readingsList.add(reading);
		}
		ProcessXLS.populateData(metainfo, readingsList);
	}
	public static List<Map<Integer,String>> parseRawString(String rawString,String rawStringType) throws Exception {
		
		if(rawStringType.equals("tsv")) {
			
			ICsvListReader listReader = null;
	        try {
	            Reader rawStringReaded = new StringReader(rawString);
                listReader = new CsvListReader(rawStringReaded, CsvPreference.TAB_PREFERENCE);
                
//	            listReader.getHeader(true);
                
                List<String> rowDatas;
    			List<Map<Integer,String>> parsedData = new ArrayList<>(); 
                while( (rowDatas = listReader.read()) != null ) {
                	
                	Map<Integer,String> posVsData = new HashMap<>();
                	for(int i=0;i<rowDatas.size();i++) {
                		
                		String data = rowDatas.get(i);
                		posVsData.put(i+1, data);
                	}
                	parsedData.add(posVsData);
                }
                System.out.println("parsedData --- "+parsedData);
                return parsedData;
	        }
	        finally {
                if( listReader != null ) {
                     listReader.close();
                }
	        }
	        
		}
		else if(rawStringType.equals("csv")) {
			ICsvListReader listReader = null;
	        try {
	            Reader rawStringReaded = new StringReader(rawString);
                listReader = new CsvListReader(rawStringReaded, CsvPreference.STANDARD_PREFERENCE);
                
//	            listReader.getHeader(true);
                
                List<String> rowDatas;
    			List<Map<Integer,String>> parsedData = new ArrayList<>(); 
                while( (rowDatas = listReader.read()) != null ) {
                	
                	Map<Integer,String> posVsData = new HashMap<>();
                	for(int i=0;i<rowDatas.size();i++) {
                		
                		String data = rowDatas.get(i);
                		posVsData.put(i+1, data);
                	}
                	parsedData.add(posVsData);
                }
                System.out.println("parsedData --- "+parsedData);
                return parsedData;
	        }
	        finally {
                if( listReader != null ) {
                     listReader.close();
                }
	        }
		}
		return null;
	}
}
