package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ImportFieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.time.DateTimeUtil;

public class UploadImportFileCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FileStore fs = FacilioFactory.getFileStore();
		
		String fileUploadFileName = (String) context.get(FacilioConstants.ContextNames.FILE_NAME);
		File fileUpload = (File) context.get(FacilioConstants.ContextNames.FILE);
		String fileUploadContentType = (String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(FacilioConstants.ContextNames.IMPORT_PROCESS_CONTEXT);
		Long assetCategory = (Long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
		
		long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
				
		Workbook workbook = WorkbookFactory.create(fileUpload);
		importProcessContext = ImportAPI.getColumnHeadings(workbook, importProcessContext);
		
		JSONObject firstRow = ImportAPI.getFirstRow(workbook);	
		workbook.close();
		
		importProcessContext.setfirstRow(firstRow);
		
		Integer importMode = (Integer) context.get(FacilioConstants.ContextNames.IMPORT_MODE);
		
		importProcessContext.setImportMode(importMode);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule facilioModule = modBean.getModule(moduleName);
        
		if(facilioModule ==  null) {
			throw new IllegalArgumentException("Module cannot be null");
		}
        if(facilioModule.getName().equals(FacilioConstants.ContextNames.ASSET) && (assetCategory != -1)) {
        	Map<String,String> moduleInfo = AssetsAPI.getAssetModuleName(assetCategory);
        	if(!moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
        		facilioModule = modBean.getModule(moduleName);
        		importProcessContext.setModuleId(facilioModule.getModuleId());
        	}
        	else {
            	importProcessContext.setModuleId(facilioModule.getModuleId());
            }
            
        }
        else {
        	importProcessContext.setModuleId(facilioModule.getModuleId());
        }
        importProcessContext.setStatus(ImportProcessContext.ImportStatus.UPLOAD_COMPLETE.getValue());
        
        importProcessContext.setFileId(fileId);
        
        importProcessContext.setImportTime(DateTimeUtil.getCurrenTime());
        
        importProcessContext.setImportType(ImportProcessContext.ImportType.EXCEL.getValue());
        
        long assetId = (long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        
        if(assetId > 0) {
        	importProcessContext.setAssetId(assetId);
        }
        
        ImportAPI.addImportProcess(importProcessContext);
		
        if(moduleName.equals("purchasedTool") || moduleName.equals("purchasedItem")) {
        	importProcessContext.setFacilioFieldMapping(ImportFieldFactory.getFacilioFieldMapping(moduleName));
        	importProcessContext.setFieldMapping(ImportFieldFactory.getFieldMapping(moduleName));

        }
        else {
        	ImportAPI.getFieldMapping(importProcessContext);
        }
        context.put(FacilioConstants.ContextNames.IMPORT_PROCESS_CONTEXT, importProcessContext);
        
		return false;
	}

}
