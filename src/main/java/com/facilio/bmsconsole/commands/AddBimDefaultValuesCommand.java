package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BimDefaultValuesContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class AddBimDefaultValuesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ImportProcessContext importProcessContext = (ImportProcessContext)context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		if(importProcessContext!=null){
			
			boolean isBim = BimAPI.isBIM(importProcessContext.getId());
			
			if(isBim){
				FacilioModule module = ModuleFactory.getBimImportProcessMappingModule();
				List<FacilioField> fields =  FieldFactory.getBimImportProcessMappingFields();
				BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(module,fields,importProcessContext.getId());
				
				FileStore fs = FacilioFactory.getFileStore();
				
				long bimImportId = bimImport.getBimId();
				
				module = ModuleFactory.getBimIntegrationLogsModule();
				fields =  FieldFactory.getBimIntegrationLogsFields();
				
				BimIntegrationLogsContext bimIntegrationLog = BimAPI.getBimIntegrationLog(module,fields,bimImportId);
				
				InputStream inputStream = fs.readFile(bimIntegrationLog.getFileId());
				
				Workbook workbook = WorkbookFactory.create(inputStream);
				
				String moduleName = bimImport.getModuleName();
				
				addBimDefaultValues(workbook, moduleName, bimImportId,importProcessContext);
				inputStream.close();
			}
			
		}
		
		return false;
	}
	
	public void addBimDefaultValues(Workbook workbook,String moduleName,long bimImportId,ImportProcessContext importProcessContext) throws Exception
	{
		List<BimDefaultValuesContext> bimDefaultValues = new ArrayList<>();
		if(moduleName.equals("site")){
			long siteId = -1;
			
			if(importProcessContext.getSiteId()>0){
				siteId = importProcessContext.getSiteId();
			}else{
				JSONObject firstRow = BimAPI.getFirstRow(workbook, workbook.getSheet("Facility"));
				String siteName = firstRow.get("Name").toString();
				
				ResourceContext resource = BimAPI.getResource(siteName);
				siteId = resource.getId();
			}
			
			BimDefaultValuesContext bimDefaultValue = new BimDefaultValuesContext();
			
			bimDefaultValue.setBimId(bimImportId);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule assetModule = modBean.getModule("asset");
			bimDefaultValue.setModuleId(assetModule.getModuleId());
			
			FacilioField siteField = modBean.getField("site", "basespace");
			bimDefaultValue.setFieldId(siteField.getFieldId());
			bimDefaultValue.setDefaultValue(String.valueOf(siteId));
			
			bimDefaultValues.add(bimDefaultValue);
			
			BimDefaultValuesContext bimDefaultValue1 = (BimDefaultValuesContext) bimDefaultValue.clone();
			FacilioModule buildingModule = modBean.getModule("building");
			bimDefaultValue1.setModuleId(buildingModule.getModuleId());
			bimDefaultValues.add(bimDefaultValue1);
			
			bimDefaultValue1 = (BimDefaultValuesContext) bimDefaultValue.clone();
			FacilioModule floorModule = modBean.getModule("floor");
			bimDefaultValue1.setModuleId(floorModule.getModuleId());
			bimDefaultValues.add(bimDefaultValue1);
			
			bimDefaultValue1 = (BimDefaultValuesContext) bimDefaultValue.clone();
			FacilioModule spaceModule = modBean.getModule("space");
			bimDefaultValue1.setModuleId(spaceModule.getModuleId());
			bimDefaultValues.add(bimDefaultValue1);
			
			bimDefaultValue1 = (BimDefaultValuesContext) bimDefaultValue.clone();
			FacilioModule zoneModule = modBean.getModule("zone");
			bimDefaultValue1.setModuleId(zoneModule.getModuleId());
			bimDefaultValues.add(bimDefaultValue1);
			
			
		}else if(moduleName.equals("building")){
			JSONObject firstRow = BimAPI.getFirstRow(workbook, workbook.getSheet("Facility"));
			String buildingName = firstRow.get("ProjectName").toString();
			long buildingId = -1;
			
			ResourceContext resource = BimAPI.getResource(buildingName);
			buildingId = resource.getId();
			BimDefaultValuesContext bimDefaultValue = new BimDefaultValuesContext();
			
			bimDefaultValue.setBimId(bimImportId);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule floorModule = modBean.getModule("floor");
			bimDefaultValue.setModuleId(floorModule.getModuleId());
			
			FacilioField buildingField = modBean.getField("building", "basespace");
			bimDefaultValue.setFieldId(buildingField.getFieldId());
			bimDefaultValue.setDefaultValue(String.valueOf(buildingId));
			
			bimDefaultValues.add(bimDefaultValue);
			
			BimDefaultValuesContext bimDefaultValue1 = (BimDefaultValuesContext) bimDefaultValue.clone();
			FacilioModule spaceModule = modBean.getModule("space");
			bimDefaultValue1.setModuleId(spaceModule.getModuleId());
			bimDefaultValues.add(bimDefaultValue1);
			
		}
		
		FacilioModule module = ModuleFactory.getBimDefaultValuesModule();
		List<FacilioField> fields = FieldFactory.getBimDefaultValuesFields();
		for(BimDefaultValuesContext bimDefault : bimDefaultValues){
			BimAPI.DeleteBimDefaultValue(module,fields,bimDefault);
			BimAPI.addBimDefaultValue(module, fields, bimDefault);
		}
		
	}
	

}
