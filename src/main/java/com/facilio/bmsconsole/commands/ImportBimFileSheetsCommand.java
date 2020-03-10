package com.facilio.bmsconsole.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class ImportBimFileSheetsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		FileStore fs = FacilioFactory.getFileStore();
		
		long bimImportId = (Long) context.get(FacilioConstants.ContextNames.BIM_IMPORT_ID);
		
		FacilioModule module = ModuleFactory.getBimIntegrationLogsModule();
		List<FacilioField> fields =  FieldFactory.getBimIntegrationLogsFields();
		
		BimIntegrationLogsContext bimIntegrationLog = BimAPI.getBimIntegrationLog(module,fields,bimImportId);
		
		//comment out
//		InputStream inputStream = fs.readFile(bimIntegrationLog.getFileId());
		String home = System.getProperty("user.home");
		File fileUpload=new File(home+"/Downloads/SampleCOBieSpreadsheet copy.xls");
		InputStream inputStream = new FileInputStream(fileUpload);
		//commentout
		
		
		Workbook workbook = WorkbookFactory.create(inputStream);
		
		Set<String> selectedSheets = (Set<String>)context.get(FacilioConstants.ContextNames.SELECTED_SHEET_NAMES);
		
		module = ModuleFactory.getBimImportProcessMappingModule();
		fields =  FieldFactory.getBimImportProcessMappingFields();
		
		List<BimImportProcessMappingContext> sheetImports = BimAPI.getBimImportProcessMapping(module, fields, bimImportId);
		
		Set<String> completedModules = sheetImports.stream().filter(im -> im.getStatus()== BimImportProcessMappingContext.Status.COMPLETED.getValue()).map(BimImportProcessMappingContext::getModuleName).collect(Collectors.toSet());
		Set<String> notCompletedModules = sheetImports.stream().filter(im -> im.getStatus() != BimImportProcessMappingContext.Status.COMPLETED.getValue()).map(BimImportProcessMappingContext::getModuleName).collect(Collectors.toSet());
		List<ImportProcessContext> importList = new ArrayList<ImportProcessContext>();
		String firstSheetName = null;
		for(int i=0;i<workbook.getNumberOfSheets();i++){
			Sheet sheet = workbook.getSheetAt(i);
			if(selectedSheets.contains(sheet.getSheetName())){

				List<ImportProcessContext> importProcessContextList = BimAPI.uploadSheet(workbook,sheet,bimIntegrationLog.getFileId(),completedModules);
				importList.addAll(importProcessContextList);
				if(!importProcessContextList.isEmpty() && firstSheetName == null){
					firstSheetName = sheet.getSheetName();
				}
				
				for(ImportProcessContext importProcessContext:importProcessContextList){
					
					if(notCompletedModules.contains(importProcessContext.getModule().getName())){
						BimAPI.DeleteInBimImportProcessMapping(bimIntegrationLog.getId(),sheet.getSheetName(), importProcessContext.getModule().getName());
					}
					
					module = ModuleFactory.getBimImportProcessMappingModule();
					fields = FieldFactory.getBimImportProcessMappingFields();
					
					BimImportProcessMappingContext bim = new BimImportProcessMappingContext();
					bim.setBimId(bimIntegrationLog.getId());
					bim.setImportProcessId(importProcessContext.getId());
					bim.setSheetName(sheet.getSheetName());
					bim.setModuleName(importProcessContext.getModule().getName());
					bim.setStatus(BimImportProcessMappingContext.Status.INPROGRESS);
					
					BimAPI.addBimImportProcessMapping(module,fields,bim);
				}
			}
		}
		
		if(!importList.isEmpty()){
			if(!"Job".equals(firstSheetName)){
				BimAPI.ScheduleGenericImportJob(importList.get(0),firstSheetName);
			}else{
				BimAPI.SchedulePMImportJob(importList.get(0),firstSheetName);
			}
		}
		
		return false;
	}
	
}
