package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateBimIntegrationLogCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ImportProcessContext importProcessContext = (ImportProcessContext)context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		if(importProcessContext!=null){
			
			boolean isBim = BimAPI.isBIM(importProcessContext.getId());
			
			if(isBim){
				FacilioModule module = ModuleFactory.getBimImportProcessMappingModule();
				List<FacilioField> fields = FieldFactory.getBimImportProcessMappingFields();
				
				BimImportProcessMappingContext bimImport = new BimImportProcessMappingContext();
				bimImport.setStatus(BimImportProcessMappingContext.Status.COMPLETED);
				
				Condition condition = CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("importProcessId"), importProcessContext.getId().toString(), NumberOperators.EQUALS);
				BimAPI.updateBimImportProcessMapping(module,fields,bimImport,condition);
				
				bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(module,fields,importProcessContext.getId());
				
				List<BimImportProcessMappingContext> bimImports = BimAPI.getBimImportProcessMapping(module, fields, bimImport.getBimId());
				
				int noOfModulesImported = Integer.parseInt(String.valueOf(bimImports.stream().filter(b->b.getStatus() == BimImportProcessMappingContext.Status.COMPLETED.getValue()).count()));
				
				module = ModuleFactory.getBimIntegrationLogsModule();
				fields =  FieldFactory.getBimIntegrationLogsFields();
				BimIntegrationLogsContext bimIntegrationLog=new BimIntegrationLogsContext();
				bimIntegrationLog = BimAPI.getBimIntegrationLog(module, fields, bimImport.getBimId());
				if(noOfModulesImported == bimIntegrationLog.getNoOfModules()){
					bimIntegrationLog.setStatus(BimIntegrationLogsContext.Status.COMPLETED);
					bimIntegrationLog.setId(bimImport.getBimId());
					bimIntegrationLog.setImportedTime(System.currentTimeMillis());
					BimAPI.updateBimIntegrationLog(module, fields, bimIntegrationLog);
				}else{
					BimImportProcessMappingContext bimImportNew = bimImports.stream().filter(b->b.getStatus() != BimImportProcessMappingContext.Status.COMPLETED.getValue()).findFirst().get();
					ImportProcessContext importProcess = ImportAPI.getImportProcessContext(bimImportNew.getImportProcessId());
					
					String moduleName = bimImportNew.getModuleName();
					
					BimAPI.ScheduleBimGenericImportJob(moduleName, bimImportNew.getSheetName(), importProcess);
					
				}
			}
			
		}
		
		return false;
	}

}
