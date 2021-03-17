package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ImportFieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.Map;

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
		Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);
		
		long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
				
		Workbook workbook = WorkbookFactory.create(fileUpload);
		if (workbook.getNumberOfSheets() > 1) {
			throw new IllegalArgumentException("Uploaded File contains more than one Sheet");
		}
		if (siteId != null && siteId > 0) {
	        importProcessContext.setSiteId(siteId);
		}
		importProcessContext = ImportAPI.getColumnHeadings(workbook, importProcessContext);
		
		JSONObject firstRow = ImportAPI.getFirstRow(workbook);	
		workbook.close();
		
		importProcessContext.setfirstRow(firstRow);
		importProcessContext.setFirstRowString(firstRow.toString());
		
		Integer importMode = (Integer) context.get(FacilioConstants.ContextNames.IMPORT_MODE);
		
		importProcessContext.setImportMode(importMode);
		if (importMode == ImportProcessContext.ImportMode.READING.getValue()) {
			importProcessContext.setImportSetting(ImportProcessContext.ImportSetting.INSERT.getValue());
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule facilioModule = modBean.getModule(moduleName);
        
		if(facilioModule ==  null) {
			throw new IllegalArgumentException("Module cannot be null");
		}
		if (facilioModule.getName().equals(FacilioConstants.ContextNames.ASSET) && (assetCategory != null && assetCategory != -1)) {
			Map<String, String> moduleInfo = AssetsAPI.getAssetModuleName(assetCategory);
			if (!moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
				facilioModule = modBean.getModule(moduleName);
				importProcessContext.setModuleId(facilioModule.getModuleId());
			} else {
				importProcessContext.setModuleId(facilioModule.getModuleId());
			}
		}
        else {
        	if(facilioModule.getModuleId() > 0) {
        		importProcessContext.setModuleId(facilioModule.getModuleId());
        	}
        	else {
        		importProcessContext.setModuleName(facilioModule.getName());
        	}
        }
        importProcessContext.setStatus(ImportProcessContext.ImportStatus.UPLOAD_COMPLETE.getValue());
        
        importProcessContext.setFileId(fileId);
        
        importProcessContext.setImportTime(DateTimeUtil.getCurrenTime());
        
        importProcessContext.setImportType(ImportProcessContext.ImportType.EXCEL.getValue());
        
        importProcessContext.setUploadedBy(AccountUtil.getCurrentUser().getOuid());
        
        long assetId = (long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        
        if(assetId > 0) {
        	importProcessContext.setAssetId(assetId);
        }
        
        long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);
        
        if(templateId > 0) {
        	importProcessContext.setTemplateId(templateId);
        }
		String moduleMeta = (String) context.get(ImportAPI.ImportProcessConstants.MODULE_META);
		if (moduleMeta != null) {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(moduleMeta);
			JSONObject moduleMetaJson = new JSONObject();
			String baseModule = (String) json.get("baseModule");
			if (baseModule == "asset") {
				Long parentId = (Long) json.get("parentId");
				Map<String, String> moduleInfo = AssetsAPI.getAssetModuleName(parentId);
				String modName = moduleInfo.get(FacilioConstants.ContextNames.MODULE_NAME);
				json.put("module", modName);
				moduleMetaJson.put("moduleInfo", json);
				importProcessContext.setImportJobMeta(moduleMetaJson.toString());
			} else {
				json.put("module", moduleName);
				moduleMetaJson.put("moduleInfo", json);
				importProcessContext.setImportJobMeta(moduleMetaJson.toString());
			}
		}
        
        ImportAPI.addImportProcess(importProcessContext);
		
        if(moduleName.equals("purchasedTool") || moduleName.equals("purchasedItem") || ImportFieldFactory.isFieldsFromFieldFactoryModule(moduleName)) {
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
