package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.WorkbookUtil;
import org.bouncycastle.math.raw.Mod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExportUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(ExportUtil.class.getName());
	
	public static String exportData(FileFormat fileFormat, FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records, boolean isS3Url) throws Exception {
		String fileUrl = null;
		if(fileFormat == FileFormat.XLS){
			fileUrl=ExportUtil.exportDataAsXLS(facilioModule, fields, records, isS3Url);
		}
		else if(fileFormat == FileFormat.CSV){
			fileUrl=ExportUtil.exportDataAsCSV(facilioModule, fields, records, isS3Url);
		}
		return fileUrl;
	}
	
	public static long exportDataAsFileId(FileFormat fileFormat, FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records) throws Exception {
		long fileId = -1;
		if(fileFormat == FileFormat.XLS){
			fileId=ExportUtil.exportDataAsXLSFileId(facilioModule, fields, records, null);
		}
		else if(fileFormat == FileFormat.CSV){
			fileId=ExportUtil.exportDataAsCSVFileId(facilioModule, fields, records, null);
		}
		return fileId;
	}
	
	public static String exportData(FileFormat fileFormat,String name, Map<String,Object> table, boolean isS3Url) throws Exception {
		String fileUrl = null;
		if(fileFormat == FileFormat.XLS){
			fileUrl=ExportUtil.exportDataAsXLS(WorkbookUtil.createSafeSheetName(name,'_'), table, isS3Url);
		}
		else if(fileFormat == FileFormat.CSV){
			fileUrl=ExportUtil.exportDataAsCSV(name, table, isS3Url);
		}
		return fileUrl;
	}
	
	public static void writeToFile(FileFormat fileFormat,String name, Map<String,Object> table, boolean isS3Url, String rootPath) throws Exception {
		if(fileFormat == FileFormat.XLS){
			writeAsXls(WorkbookUtil.createSafeSheetName(name,'_'), table, isS3Url, rootPath);
		}
		else if(fileFormat == FileFormat.CSV){
			writeAsCSV(name, table, isS3Url, rootPath);
		}
	}
	
	public static String exportDataAsXLS(FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records, boolean isS3Url) throws Exception
	{
		FileStore fs = FacilioFactory.getFileStore();
		long fileId = exportDataAsXLSFileId(facilioModule, fields, records, fs);

		if (isS3Url) {
			return fs.getOrgiDownloadUrl(fileId);
		}

		return fs.getDownloadUrl(fileId);
	}
	public static Map<Long,SiteContext> getSiteIdVsSitesMap(List<? extends ModuleBaseWithCustomFields> records, List<ViewField> fields) throws Exception {
		Map<Long, SiteContext> siteIdVsSiteMap = new HashMap<>();
		if(isSiteIdFieldPresent(fields)){
			Set<Long> siteIds = getAllRecordSiteIds(records);
			List<SiteContext> siteContextList=SpaceAPI.getSitesWithoutScoping(new ArrayList<>(siteIds));
			for(SiteContext siteContext:siteContextList){
				Long siteId=siteContext.getSiteId();
				siteIdVsSiteMap.put(siteId,siteContext);
			}
		}
		return siteIdVsSiteMap;
	}
	@SuppressWarnings("resource")
	public static long exportDataAsXLSFileId(FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records, FileStore fs) throws Exception
	{
		try(HSSFWorkbook workbook = new HSSFWorkbook();){
			HSSFSheet sheet = workbook.createSheet(facilioModule.getDisplayName());
			HSSFRow rowhead = sheet.createRow((short) 0);

			Map<Long, SiteContext> siteIdVsSiteMap = getSiteIdVsSitesMap(records,fields);

			int i = 0;
			for(ViewField vf : fields)
			{
				String displayName = vf.getColumnDisplayName() != null && !vf.getColumnDisplayName().isEmpty() ? vf.getColumnDisplayName() : vf.getField().getDisplayName();
				rowhead.createCell((short) i).setCellValue(displayName);
				i++;
			}
			
			Map<String, List<Long>> modVsIds= getModuleVsLookupIds(fields, records);
			Map<String, Map<Long,Object>> modVsData= getModuleData(modVsIds);
	
	
			int rowCount = 1;
			for(ModuleBaseWithCustomFields record : records)
			{
				HSSFRow row = sheet.createRow(rowCount);
				i = 0;
				for(ViewField vf : fields)
				{
					FacilioField field = vf.getField();
					Map<String, Object> recordProp = FieldUtil.getAsProperties(record);
					Object value = null;
					if (field.getName().equals("siteId") && siteIdVsSiteMap.get(record.getSiteId()) != null) {
						value = siteIdVsSiteMap.get(record.getSiteId()).getName();
					}
					else {
						value =	getValue(recordProp, vf);
						if(IsLookUpRecordDeleted(modVsData, field, value)) {
							value = null;
						}
					}
					if(value != null)
					{
						Object val = getFormattedValue(modVsData, field, value);
						if(val!=null) {
							value=val;
						}
	
						row.createCell((short) i).setCellValue(value.toString());
					}
					else
					{
						row.createCell((short) i).setCellValue("");
					}
					i++;
				}
				rowCount++;
			}
			autoSizeColumns(sheet);
			
			String fileName = facilioModule.getDisplayName() + ".xls";
			String localFileName = facilioModule.getDisplayName()+"_"+DateTimeUtil.getCurrenTime()+".xls";
			String filePath = getFilePath(localFileName);
	
			try(FileOutputStream fileOut = new FileOutputStream(filePath);) {
				workbook.write(fileOut);
			}
	
			File file = new File(filePath);
			if (fs == null) {
				fs = FacilioFactory.getFileStore();
			}
			long fileId = fs.addFile(fileName, file, "application/xls");
			file.delete();
			return fileId;
		}
	}
	
	public static boolean IsLookUpRecordDeleted(Map<String, Map<Long, Object>> modVsData, FacilioField field, Object value) {
		
		if(value != null && field.getDataTypeEnum().equals(FieldType.LOOKUP)){
			LookupField lookupField= (LookupField)field;
			String moduleName=lookupField.getSpecialType();
			if(!LookupSpecialTypeUtil.isSpecialType((moduleName))) {
				moduleName= lookupField.getLookupModule().getName();
			}
			Map<Long,Object> idMap= modVsData.get(moduleName);
			if(idMap!=null) {
				
				long val = 0;
				if (value instanceof Map) {
					val = (long)((Map) value).get("id");
				}
				else if (value instanceof String){
					try {
						val = Long.parseLong(value.toString());
					}
					catch(NumberFormatException e) {
					}
				}
				else {
					val = (long) value;
				}
				
				if(idMap.get(val) == null) {
					return true;
				}
				
			}else {
				return true;
			}
		}
		
		return false;
	}
	
	public static String exportDataAsXLS(String name, Map<String,Object> table, boolean isS3Url) throws Exception {
		String filePath = writeAsXls(name, table, isS3Url, null);
		File file = new File(filePath);
		FileStore fs = FacilioFactory.getFileStore();
		long fileId = fs.addFile(file.getName(), file, "application/xls");
		
		file.delete();
		
		if (isS3Url) {
			return fs.getOrgiDownloadUrl(fileId);
		}

		return fs.getDownloadUrl(fileId);
	}
	
	public static String writeAsXls(String name, Map<String,Object> table, boolean isS3Url, String rootPath) throws Exception 
	{
		try(HSSFWorkbook workbook = new HSSFWorkbook();) {
			HSSFSheet sheet = workbook.createSheet(name);
			HSSFRow rowhead = sheet.createRow((short) 0);
			
			List<String> headers = (ArrayList<String>) table.get("headers");
			Map<String, FacilioField> headerFields = new HashMap<>();
		    	Map<String, Map<Long,Object>> modVsData = new HashMap<>();
		    	if (table.containsKey("modVsIds")) {
		    		Map<String, List<Long>> modVsIds = (Map<String, List<Long>>) table.get("modVsIds");
		    		if (modVsIds != null) {
		    			modVsData = getModuleData(modVsIds);
		    		}
		    		headerFields = (HashMap<String, FacilioField>) table.get("headerFields");
		    	}
			for(int i = 0, len = headers.size(); i < len; i++) {
				String header = headers.get(i).toString();
				if (i != 0 && headerFields.containsKey(header)) {
					FacilioField field = headerFields.get(header);
					if(field.getDataTypeEnum() == FieldType.LOOKUP) {
						Object val = getFormattedValue(modVsData, field, header);
		    				if(val!=null) {
		    					header=(String) val;
		    				}
					}
				}
				if(header != null && header.contains("___")){
					header = header.split("___")[0];
				}
				rowhead.createCell((short) i).setCellValue(header);
			}
			
			List<List> records = (ArrayList<List>) table.get("records");
			int rowCount = 1;
			for(int i = 0, len = records.size(); i < len; i++) {
				HSSFRow row = sheet.createRow(rowCount);
				Object recordObj = records.get(i);
				if (recordObj instanceof List) {
					List<Object> record = (List<Object>) recordObj;
					for (int j = 0, rowLen = record.size(); j < rowLen; j++) {
						Object value = record.get(j);
						if(value instanceof Double) {
							value = BigDecimal.valueOf((Double)value).toPlainString();
						}
						row.createCell((short) j).setCellValue(value != null ? value.toString() : "");
					}
				}
				else if (recordObj instanceof Map) {
					Map<String,Object> record = (Map<String,Object>) recordObj;
					for(int j = 0, headerLen = headers.size(); j < headerLen; j++) {
						String header = headers.get(j).toString();
						Object value = record.containsKey(header) ? record.get(header) : "";
						if (j == 0 && modVsData != null && !modVsData.isEmpty() && headerFields.containsKey(header)) {
							FacilioField field = headerFields.get(header);
		    					if(field.getDataTypeEnum() == FieldType.LOOKUP) {
		    						Object val = getFormattedValue(modVsData, field, value);
		    	    	    				if(val!=null) {
		    	    	    					value=val;
		    	    	    				}
		    					}
						}
						if(value instanceof Double) {
							row.createCell((short) j).setCellValue((Double) (value != null ? value : null));
						}
						else{
							row.createCell((short) j).setCellValue(value != null ? value.toString() : "");
						}
					}
				}
				
				rowCount++;
			}
			
			autoSizeColumns(sheet);
		
			String fileName = name + ".xls";
			String filePath = getFilePath(fileName, rootPath); 
			try(FileOutputStream fileOut = new FileOutputStream(filePath);) {
				workbook.write(fileOut);
			}
			return filePath;
		}
	}
	
	private static void autoSizeColumns(HSSFSheet sheet) {
		try {
			if (sheet.getPhysicalNumberOfRows() > 0) {
				HSSFRow row = sheet.getRow(0);
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					sheet.autoSizeColumn(columnIndex);
					int maxWidth = 10000;
					if (sheet.getColumnWidth(columnIndex) > maxWidth) {
						CellStyle style = cell.getCellStyle();
						style.setWrapText(true); 
						cell.setCellStyle(style);
						sheet.setColumnWidth(columnIndex, maxWidth);
					}
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Error while auto sizing column", e);
		}
	}
	
	public static String exportDataAsCSV(FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records, boolean isS3Url) throws Exception
    {
		FileStore fs = FacilioFactory.getFileStore();
		long fileId = exportDataAsCSVFileId(facilioModule, fields, records, fs);
		
	    if (isS3Url) {
	    	return fs.getOrgiDownloadUrl(fileId);
		}
	    return fs.getDownloadUrl(fileId);
    }
	
	public static long exportDataAsCSVFileId(FacilioModule facilioModule, List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records, FileStore fs) throws Exception {
		String fileName = facilioModule.getDisplayName() + ".csv";
		String localFileName = facilioModule.getDisplayName()+"_"+DateTimeUtil.getCurrenTime()+".xls";
		String filePath = getFilePath(localFileName);
        	try(FileWriter writer = new FileWriter(filePath, false);) {
        	
	        	StringBuilder str = new StringBuilder();
	        	for(ViewField vf : fields)
	    		{
	    			String displayName = vf.getColumnDisplayName() != null && !vf.getColumnDisplayName().isEmpty() ? vf.getColumnDisplayName() : vf.getField().getDisplayName();
	        		str.append(escapeCsv(displayName));
	        		str.append(',');
	        	}
	        	writer.append(StringUtils.stripEnd(str.toString(), ","));
	        	writer.append('\n');
	        	
	        	Map<String, List<Long>> modVsIds= getModuleVsLookupIds(fields, records);
	    		Map<String, Map<Long,Object>> modVsData= getModuleData(modVsIds);


				Map<Long, SiteContext> siteIdVsSiteMap = getSiteIdVsSitesMap(records,fields);
	        
	        	for(ModuleBaseWithCustomFields record : records)
	        	{
	        		str = new StringBuilder();
		    		for(ViewField vf : fields)
				{
		    			FacilioField field = vf.getField();
					Map<String, Object> recordProp = FieldUtil.getAsProperties(record);
					Object value = null;
					if (field.getName().equals("siteId") && siteIdVsSiteMap.get(record.getSiteId()) != null) {
						value = siteIdVsSiteMap.get(record.getSiteId()).getName();
					}
					else {
						value =	getValue(recordProp, vf);
						if(IsLookUpRecordDeleted(modVsData, field, value)) {
							value = null;
						}
					}
		    			if(value != null)
		    			{
		    				Object val = getFormattedValue(modVsData, field, value);
		    				if(val!=null) {
		    					value=val;
		    				}
		    				str.append(escapeCsv(value.toString()));
		    			}
		    			else
		    			{
		    				str.append("");
		    			}
		    			str.append(',');
				}
		    		writer.append(StringUtils.stripEnd(str.toString(), ","));
		    		writer.append('\n');
	        	}
	        	writer.flush();
	        	writer.close();
	        	
	        	File file = new File(filePath);
	        	if (fs == null) {
	        		fs = FacilioFactory.getFileStore();
	        	}
		    long fileId = fs.addFile(fileName, file, "application/csv");
		    file.delete();
		   return fileId;
        	}
	}
	
	private static String escapeCsv(String value) {
		if (value != null) {
			value = value.trim();
			try {
				StringBuilder sb = new StringBuilder(value);
				if (sb.charAt(0) == '=' || sb.charAt(0) == '@' || sb.charAt(0) == '+' || sb.charAt(0) == '-') {
					sb.insert(0,'\\');
				}
				value = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			value = StringEscapeUtils.escapeCsv(value);
		}
		return value;
	}
	
	public static String exportDataAsCSV(String name, Map<String,Object> table, boolean isS3Url) throws Exception {
		String filePath = writeAsCSV(name, table, isS3Url, null);
		File file = new File(filePath);
	    FileStore fs = FacilioFactory.getFileStore();
	    long fileId = fs.addFile(file.getName(), file, "application/csv");
	    
	    file.delete();
	    if (isS3Url) {
			return fs.getOrgiDownloadUrl(fileId);
		}
	    return fs.getDownloadUrl(fileId);
	}
	
	@SuppressWarnings("unchecked")
	public static String writeAsCSV(String name, Map<String,Object> table, boolean isS3Url, String rootPath) throws Exception
    {
		String fileName = name + ".csv";
		String filePath = getFilePath(fileName, rootPath);
        	try(FileWriter writer = new FileWriter(filePath, false);) {
	        	StringBuilder str = new StringBuilder();
	        	List<String> headers = (ArrayList<String>) table.get("headers");
	        	Map<String, FacilioField> headerFields = new HashMap<>();
	        	Map<String, Map<Long,Object>> modVsData = new HashMap<>();
	        	if (table.containsKey("modVsIds")) {
	        		Map<String, List<Long>> modVsIds = (Map<String, List<Long>>) table.get("modVsIds");
	        		if (modVsIds != null) {
	        			modVsData = getModuleData(modVsIds);
	        		}
	        		headerFields = (HashMap<String, FacilioField>) table.get("headerFields");
	        	}
	    		for(int i = 0, len = headers.size(); i < len; i++) {
	    			String header = headers.get(i).toString();
	    			if (i != 0 && headerFields.containsKey(header)) {
	    				FacilioField field = headerFields.get(header);
	    				if(field.getDataTypeEnum() == FieldType.LOOKUP) {
	    					Object val = getFormattedValue(modVsData, field, header);
	        				if(val!=null) {
	        					header=(String) val;
	        				}
	    				}
	    			}
					if(header != null && header.contains("___")){
						header = header.split("___")[0];
					}
	    			str.append(escapeCsv(header));
	    			str.append(',');
	    		}
	        	writer.append(StringUtils.stripEnd(str.toString(), ","));
	        	writer.append('\n');
	        	
	        	List<Object> records = (ArrayList<Object>) table.get("records");
	       
	    		for(int i = 0, len = records.size(); i < len; i++) {
	    			str = new StringBuilder();
	    			Object recordObj = records.get(i);
	    			if (recordObj instanceof List) {
	    				List<Object> record = (List<Object>) recordObj;
	    				for (int j = 0, rowLen = record.size(); j < rowLen; j++) {
	        				Object value = record.get(j);
	        				if(value instanceof Double) {
	    						value = BigDecimal.valueOf((Double)value).toPlainString();
	    					}
	        				str.append(value != null ? value.toString() : "").append(',');
	        			}
	    			}
	    			else if (recordObj instanceof Map) {
	    				Map<String,Object> record = (Map<String,Object>) recordObj;
	    				for(int j = 0, headerLen = headers.size(); j < headerLen; j++) {
	    					String header = headers.get(j).toString();
	    					Object value = record.containsKey(header) ? record.get(header) : "";
	    					if (j == 0 && modVsData != null && !modVsData.isEmpty() && headerFields.containsKey(header)) {
	    						FacilioField field = headerFields.get(header);
	    	    					if(field.getDataTypeEnum() == FieldType.LOOKUP) {
	    	    						Object val = getFormattedValue(modVsData, field, value);
		    	    	    				if(val!=null) {
		    	    	    					value=val;
		    	    	    				}
	    	    					}
	    					}
	    					if(value instanceof Double) {
	    						value = BigDecimal.valueOf((Double)value).toPlainString();
	    					}
	    					if(value != null) {
								str.append(escapeCsv(value.toString()));
							}
	    					str.append(',');
	    	    			}
	    			}
	    			
	    			writer.append(StringUtils.stripEnd(str.toString(), ","));
		    		writer.append('\n');
	    		}
	    		
	        	writer.flush();
	        	writer.close();
        	}
	        
        	return filePath;
    }

	private static Object getFormattedValue(Map<String, Map<Long, Object>> modVsData, FacilioField field, Object value) throws Exception {
		if (value == null) {
			return null;
		}
		
		switch(field.getDataTypeEnum()) {

		case LOOKUP:{
			
			LookupField lookupField= (LookupField)field;
			String moduleName=lookupField.getSpecialType();
			if(!LookupSpecialTypeUtil.isSpecialType((moduleName))) {
				moduleName= lookupField.getLookupModule().getName();
			}
			Map<Long,Object> idMap= modVsData.get(moduleName);
			if(idMap!=null) {
				long val;
				if (value instanceof Map) {
					val = (long)((Map) value).get("id");
				}
				else if (value instanceof String){
					try {
						val = Long.parseLong(value.toString());
					}
					catch(NumberFormatException e) {
						return value;
					}
				}
				else {
					val = (long) value;
				}
				return idMap.get(val);
			}
			
			break;
		}
			case MULTI_LOOKUP: {
				if (!(value instanceof Collection)) {
					return null;
				}
				if (CollectionUtils.isNotEmpty((Collection) value)) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					MultiLookupField multiLookupField = (MultiLookupField) field;
					FacilioField primaryField = modBean.getPrimaryField(multiLookupField.getLookupModule().getName());
					primaryField = primaryField == null ? FieldFactory.getIdField(multiLookupField.getLookupModule()) : primaryField;
					StringJoiner sj = new StringJoiner(",");
					for (Object obj : (Collection) value) {
						if (obj instanceof Map) {
							if (((Map<?, ?>) obj).containsKey(primaryField.getName())) {
								sj.add(((Map<?, ?>) obj).get(primaryField.getName()).toString());
							}
						}
					}
					return sj.toString();
				}
				break;
			}
		case DATE:
		case DATE_TIME:{
			return DateTimeUtil.getFormattedTime((long)value);
		}
		case NUMBER: {
			 if (field.getDisplayType() == FacilioField.FieldDisplayType.DURATION) {
					return handleDurationField(Double.parseDouble(value+""));
				}
			 else if(value instanceof Double) {
				return BigDecimal.valueOf((Double)value).toPlainString();
			}
			return getValueFromEnum(field, value);
		}
		case BOOLEAN:case DECIMAL:case MISC:case STRING:
			break;
			case ENUM:
            case SYSTEM_ENUM:
				return ((BaseEnumField)field).getValue((int) value);
			case MULTI_ENUM:
				return ((MultiEnumField)field).getValue((List) value);
			case FILE:
				break;
			case COUNTER:
				break;
			case ID:
				break;
		}
		return null;
	}
	
	public static String handleDurationField(double time) {
		
				Double days = Math.floor(time / 86400);
				time -= days * 86400;
				
				Double hours = Math.floor(time / 3600);
				time -= hours * 3600;
				
				Double minutes = Math.floor(time / 60);
				time -= minutes * 60;

		if (days == 1 || days > 1) {
		      if (days == 1) {
		    	  return  hours > 0.099 ?  Math.round(days) + " Day " +  Math.round(hours) + " Hrs " :  Math.round(days) + " Day";
		      }
		      return  hours > 0.099 ?  Math.round(days) + " Days " +  Math.round(hours) + " Hrs " :  Math.round(days) + " Days";
		} else if (hours > 0.99) {
		    	 return  minutes > 0.99 ?  Math.round(hours) + " Hrs " +  Math.round(minutes) + " Mins " :  Math.round(hours) + " Hrs";
		} else if (minutes > 0.99) {
		      return   Math.round(minutes) + " Mins";
		} else {
		      return time + " Secs ";
	    }
	}

	private static Map<String, List<Long>> getModuleVsLookupIds(List<ViewField> fields, List<? extends ModuleBaseWithCustomFields> records) throws Exception {
		
		Map<String, List<Long>> modVsIds= new HashMap<String, List<Long>>();
		for(ModuleBaseWithCustomFields record : records){
			for(ViewField vf : fields)
			{
				FacilioField field = vf.getField();
				if (field != null && field.getDataTypeEnum()==FieldType.LOOKUP) {

					vf.getParentField();
					Map<String, Object> recordProp = FieldUtil.getAsProperties(record);
					Map value = (Map) getValue(recordProp, vf);
					if(value == null || !value.containsKey("id") || (long)value.get("id") == -1){
						continue;
					}

					LookupField lookupField= (LookupField)field;
					String moduleName=lookupField.getSpecialType();
					if(!LookupSpecialTypeUtil.isSpecialType((moduleName))) {
						moduleName= lookupField.getLookupModule().getName();
					}
					List<Long> ids= modVsIds.get(moduleName);
					if(ids==null) {
						ids=new ArrayList<Long>();
						modVsIds.put(moduleName, ids);
					}
					ids.add((long)value.get("id"));
				}
			}
		}
		return modVsIds;
	}

	private static Map<String, Map<Long, Object>> getModuleData(Map<String, List<Long>> modVsIds)
			throws Exception, InstantiationException, IllegalAccessException {
		
		Map<String, Map<Long, Object>> modVsData =new HashMap<String,Map<Long,Object>>();;
		for(Map.Entry<String, List<Long>> entry:modVsIds.entrySet()) {
			
			String moduleName=entry.getKey();
			List<Long> ids=entry.getValue();
			if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				
				Map<Long,Object> idMap= LookupSpecialTypeUtil.getPickList(moduleName,ids);
				if(idMap!=null) {
					modVsData.put(moduleName,idMap);
				}
			}
			else {
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				Map<Long,Object> idMap= CommonCommandUtil.getPickList(ids, bean.getModule(moduleName));
				if(idMap!=null) {
					modVsData.put(moduleName,idMap);
				}
			}
		}
		return modVsData;
	}
	
	private static Object getValue(Map<String, Object> prop, ViewField viewField) {
		FacilioField field = viewField.getField();
		FacilioField parentField = viewField.getParentField();
		Object value = null;
		if(parentField != null) {
			Object parentObj = prop.get(parentField.getName());
			if(parentObj != null) {
				value = ((Map) parentObj).get(field.getName());
			}
		} else {
			value = prop.get(field.getName());
		}
		return value;
	}
	
	private static String getValueFromEnum(FacilioField field, Object value) {
		switch(field.getName()) {
			case "alarmType": 
				return AlarmType.getType((int)value).getStringVal();
			case "sourceType":
				return getValueForSourceTypeByModule(field,value);
		}
		return null;
	}
	private static String getValueForSourceTypeByModule(FacilioField field,Object value){
		FacilioModule module=field.getModule();
		String moduleName=module.getName();
		switch (moduleName){
			case "ticket":
				return SourceType.getType((int)value).getValue();
		}
		return null;
	}
	
	public static String exportModule(FileFormat fileFormat, String moduleName, String viewName, String filters,Criteria criteria, boolean isS3Value, boolean specialFields, Integer viewLimit) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<ViewField> viewFields = new ArrayList<ViewField>();
		List<ModuleBaseWithCustomFields> records = new ArrayList<ModuleBaseWithCustomFields>();
		prepareExportModuleConfig(fileFormat, moduleName, viewName, filters, criteria, specialFields, viewLimit, viewFields, records);
		return exportData(fileFormat, modBean.getModule(moduleName), viewFields, records, isS3Value);
	}
	
	public static long exportModuleAsFileId(FileFormat fileFormat, String moduleName, String viewName, String filters,Criteria criteria, boolean specialFields, Integer viewLimit) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<ViewField> viewFields = new ArrayList<ViewField>();
		List<ModuleBaseWithCustomFields> records = new ArrayList<ModuleBaseWithCustomFields>();
		prepareExportModuleConfig(fileFormat, moduleName, viewName, filters, criteria, specialFields, viewLimit, viewFields, records);
		return exportDataAsFileId(fileFormat, modBean.getModule(moduleName), viewFields, records);
	}

	public static void prepareExportModuleConfig(FileFormat fileFormat, String moduleName, String viewName, String filters,Criteria criteria, boolean specialFields, Integer viewLimit, List<ViewField> viewFields, List<ModuleBaseWithCustomFields> records) throws Exception {
		FacilioChain listChain = ChainUtil.getListChain(moduleName);
		FacilioContext context = listChain.getContext();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		V3Config v3Config = ChainUtil.getV3Config(moduleName);
		Class beanClass = ChainUtil.getBeanClass(v3Config, module);
		context.put(Constants.BEAN_CLASS, beanClass);

		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
		context.put(Constants.WITHOUT_CUSTOMBUTTONS, true);
		
		int limit = 5000;
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.MODULE_EXPORT_LIMIT);
		String orgLimit = orgInfo.get(FacilioConstants.OrgInfoKeys.MODULE_EXPORT_LIMIT);
		List<FacilioField> fieldsList = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldsList);
		if (moduleName.equals("asset")) {
			FacilioChain viewDetailsChain = FacilioChainFactory.getViewDetailsChain();
			viewDetailsChain.execute(context);
			FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
			viewFields.addAll(view.getFields());
			List<LookupField> fetchLookup = new ArrayList<LookupField>();
			LookupFieldMeta spaceLookupField = null ;
			for(ViewField vf : viewFields)
			{
				if (vf.getParentField() != null && vf.getParentField().getName().equals("space")) {
					if(spaceLookupField == null) {
						spaceLookupField = new LookupFieldMeta((LookupField)vf.getParentField());
					}
					if (vf.getField() instanceof LookupField) {
						LookupField field = (LookupField) vf.getField();
						spaceLookupField.addChildSupplement(field);
					}

				}

			}
			if (spaceLookupField != null) {
				fetchLookup.add(spaceLookupField);
				context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,fetchLookup);
			}
		}

		if (orgLimit != null && !orgLimit.isEmpty()) {
			limit = Integer.parseInt(orgLimit);
		}
		if (viewLimit != null) {
			limit = viewLimit;
		}

		JSONObject pagination = new JSONObject();
		pagination.put("page", 1);
		pagination.put("perPage", limit);
		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);

//		context.put(FacilioConstants.ContextNames.MAX_LEVEL, 2);

		if (filters != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(filters);
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, true);
		}
		if (criteria != null) {
			context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
		}

		// TODO handle in view info
		if (moduleName.equals("workorder") && viewName.startsWith("upcoming")) {
			context.put(ContextNames.SKIP_MODULE_CRITERIA, true);
		}

		context.put("checkPermission", true);
		listChain.execute();

		records.addAll(Constants.getRecordListFromContext(context, moduleName));

		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

		if (moduleName.equals("workorder")) {
			ViewField serialNumber = new ViewField("serialNumber", "ID");
			serialNumber.setField(modBean.getField("serialNumber", moduleName));
			viewFields.add(serialNumber);
		} else if (!moduleName.equals("asset") &&
				   !moduleName.equals("tenant") &&
					!moduleName.equals(ContextNames.ITEM) &&
					!moduleName.equals(ContextNames.ITEM_TYPES) &&
					!moduleName.equals(ContextNames.TOOL) &&
					!moduleName.equals(ContextNames.TOOL_TYPES) &&
				   !moduleName.equals("serviceRequest") &&  // Asset module has local Id
				   fieldsMap.containsKey("localId")) {

			ViewField localId = new ViewField("localId", "Id");
			localId.setField(fieldsMap.get("localId"));
			viewFields.add(localId);
		} else {
			ViewField id = new ViewField("id", "Id");
			FacilioField idField = FieldFactory.getIdField(module);
			id.setField(idField);
			viewFields.add(id);
		}
		if(!moduleName.equals("asset")) {
			viewFields.addAll(view.getFields());
		}
		if (moduleName.equals("alarm")) {
			Iterator<ViewField> it = viewFields.iterator();
			while (it.hasNext()) {
				ViewField field = it.next();
				if (field.getField().getName().equals("modifiedTime")) {
					it.remove();
				}
			}

			ViewField createdTime = new ViewField("createdTime", "Created Time");
			createdTime.setField(modBean.getField("createdTime", moduleName));
			viewFields.add(createdTime);
			ViewField modifiedTime = new ViewField("modifiedTime", "Modified Update");
			modifiedTime.setField(modBean.getField("modifiedTime", moduleName));
			viewFields.add(modifiedTime);
			ViewField acknowledgedTime = new ViewField("acknowledgedTime", "Acknowledged Time");
			acknowledgedTime.setField(modBean.getField("acknowledgedTime", moduleName));
			viewFields.add(acknowledgedTime);
			ViewField noOfEvents = new ViewField("noOfEvents", "No Of Events");
			noOfEvents.setField(modBean.getField("noOfEvents", moduleName));
			viewFields.add(noOfEvents);
		}
		if (specialFields) {
			List<Long> ids = records.stream().map(a -> a.getId()).collect(Collectors.toList());
			viewFields.removeIf(viewField -> viewField.getField() != null && viewField.getField().getName().equals("noOfNotes"));

			ViewField comment = new ViewField("comment", "Comment");
			FacilioField commentField = new FacilioField();
			commentField.setName("comment");
			commentField.setDataType(FieldType.STRING);
			commentField.setColumnName("COMMENTS");
			commentField.setModule(modBean.getModule(moduleName));
			comment.setField(commentField);
			viewFields.add(comment);
			Map<Long, List<String>> map = new HashMap<>();
			if (ids.size() > 0) {
				List<NoteContext> notes = NotesAPI.fetchNote(ids, "ticketnotes");
				if (!(notes.isEmpty())) {
					for (int j = 0; j < notes.size(); j++) {
						if (map.containsKey(notes.get(j).getParentId())) {
							map.get(notes.get(j).getParentId()).add(notes.get(j).getBody());
						} else {
							List<String> temp = new ArrayList<>();
							temp.add(notes.get(j).getBody());
							map.put(notes.get(j).getParentId(), temp);
						}
					}
					for (int i = 0; i < records.size(); i++) {

						if (fileFormat == FileFormat.CSV && map.containsKey(records.get(i).getId())) {
							Map<String, Object> props = new HashMap<>();
							if (map.get(records.get(i).getId()).size() > 1) {
								props.put("comment",
										"\"" + StringUtils.join(map.get(records.get(i).getId()), "\n") + "\"");
							} else {
								props.put("comment", StringUtils.join(map.get(records.get(i).getId()), "\n"));
							}
							records.get(i).addData(props);
						} else {
							Map<String, Object> props = new HashMap<>();
							props.put("comment", StringUtils.join(map.get(records.get(i).getId()), "\n"));
							records.get(i).addData(props);
						}
					}

				}
			}

		}

		if (moduleName.equals("task")) {
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> props = new HashMap<>();
				TaskContext task = (TaskContext) records.get(i);
				if (task != null && task.getSectionId() > 0) {
					TaskSectionContext taskSection = TicketAPI.getTaskSection( task.getSectionId());
					if (taskSection != null && taskSection.getName() != null) {
						props.put("sectionId", taskSection.getName());
						records.get(i).addData(props);
					}
				}
			}
		}

		boolean includeNoOfClosedTasks = false;
		for (int j = 0; j < viewFields.size(); j++) {
			ViewField viewField = viewFields.get(j);
			FacilioField field = viewField.getField();
			if (field != null && field.getDataTypeEnum() != null && field.getDataTypeEnum() == FieldType.FILE) {
				viewFields.remove(viewFields.get(j));
			} else if (viewField.getFieldName() != null && viewField.getFieldName().equals("siteId")) {
				viewField.setField(FieldFactory.getSiteIdField(module));
			} else if (field == null) {
				if (FieldFactory.isSystemField(viewField.getFieldName())) {
					viewField.setField(FieldFactory.getSystemField(viewField.getFieldName(), module));
				}
			}
			// Add noOfClosedTasks only if noOfTasks is added to view
			if (moduleName.equals("workorder") && field != null && field.getName().equals("noOfTasks")) {
				includeNoOfClosedTasks = true;
			}
		}
		// could have added in the above if check for WO;
		// but the field has to be appended at the end of the list
		if (moduleName.equals("workorder") && includeNoOfClosedTasks) {
			ViewField tasksComplete = new ViewField("noOfClosedTasks", "Tasks Complete");
			tasksComplete.setField(modBean.getField("noOfClosedTasks", moduleName));
			viewFields.add(tasksComplete);

		}
		if(CollectionUtils.isNotEmpty(viewFields)){
			viewFields.removeIf(viewField -> viewField.getField()!=null &&!viewField.getField().isExportable());
		}
	}
	
	public static File getTempFolder(String rootFolderName) {
		ClassLoader classLoader = ExportUtil.class.getClassLoader();
		String path = classLoader.getResource("").getFile() + File.separator + "facilio-temp-files" + File.separator + AccountUtil.getCurrentOrg().getOrgId();
		if (rootFolderName != null) {
			path += File.separator + rootFolderName;
		}
		
		File file = new File(path);
		if (!(file.exists() && file.isDirectory())) {
			file.mkdirs();
		}
		
		return file;
	}
	
	private static String getFilePath(String fileName) {
		return getFilePath(fileName, null);
	}
	
	private static String getFilePath(String fileName, String path) {
		if (path == null) {
			path = getTempFolder(null).getPath();
		}
		return path + File.separator + fileName;
	}
	
	public static String getZipFileUrl(File rootFolder) throws Exception {
		File zipFile = new File(rootFolder.getPath()+".zip");
		
		ZipUtil.pack(rootFolder, zipFile, true);
		
		FileStore fs = FacilioFactory.getFileStore();
		
		long fileId = fs.addFile(rootFolder.getName()+".zip", zipFile, "application/zip");
		
		FileUtils.deleteDirectory(rootFolder);
		zipFile.delete();
		
		return fs.getDownloadUrl(fileId);
	}
	public static Set<Long> getAllRecordSiteIds(List<? extends ModuleBaseWithCustomFields> records){
		Set<Long> siteIds=new HashSet<>();
		for(ModuleBaseWithCustomFields record:records){
			if(record.getSiteId()!=-1){
				siteIds.add(record.getSiteId());
			}
		}
		return siteIds;
	}
	public static boolean isSiteIdFieldPresent(List<ViewField> viewFields){
		for(ViewField vf :viewFields){
			FacilioField field = vf.getField();
			if(field.getName().equals("siteId")){
				return true;
			}
		}
		return false;
	}
}
