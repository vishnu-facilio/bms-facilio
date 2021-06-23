package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class ValidateBimFileSheetsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		File fileUpload = (File) context.get(FacilioConstants.ContextNames.FILE);
		Workbook wb = WorkbookFactory.create(fileUpload);

		HashMap<String,String> validSheetsMap = new LinkedHashMap<String,String>();
		for (int i=0; i<wb.getNumberOfSheets(); i++) {
			String sheetName = wb.getSheetName(i);
			String moduleName = BimAPI.getModuleNameBySheetName(sheetName);
			boolean isContentsEmpty = BimAPI.isContentsEmpty(wb, wb.getSheet(sheetName));
			if(moduleName !=null && !isContentsEmpty){
				String[] moduleNames = moduleName.split("&&");
				for(int j=0;j<moduleNames.length;j++){
					FacilioModule facilioModule = modBean.getModule(moduleNames[j]);
					if(facilioModule != null){
						if(validSheetsMap.containsKey(sheetName)){
							String name = (String)validSheetsMap.get(sheetName);
							validSheetsMap.put(sheetName, name+" , "+ facilioModule.getDisplayName());
						}else{
							validSheetsMap.put(sheetName,facilioModule.getDisplayName());
						}
					}
				}
				
			}
		}
		JSONArray validSheets = new JSONArray();
		for(Entry<String,String> en:validSheetsMap.entrySet()){
			JSONObject json = new JSONObject();
			json.put("sheetName", en.getKey());
			json.put("moduleName", en.getValue());
			validSheets.add(json);
		}
		
		context.put(FacilioConstants.ContextNames.VALID_SHEETS, validSheets);
		
		return false;
	}

}
