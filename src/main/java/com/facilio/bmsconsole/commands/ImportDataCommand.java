package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportLookupModuleValueNotFoundException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportDataCommand extends FacilioCommand implements PostTransactionCommand {

	private static final Logger LOGGER = Logger.getLogger(ImportDataCommand.class.getName());

	private ImportProcessContext importProcessContext = null;
	private Long jobId;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	private boolean sendExceptionMail = false;

	@Override
	public boolean executeCommand(Context commandContext) throws Exception {
		// TODO Auto-generated method stub
		try {
			jobId = (Long) commandContext.get(ImportAPI.ImportProcessConstants.JOB_ID);

			LOGGER.info("IMPORT DATA JOB COMMAND CALLED -- " + jobId);

			importProcessContext = ImportAPI.getImportProcessContext(jobId);

			if (importProcessContext.getStatus().intValue() > ImportProcessContext.ImportStatus.IN_PROGRESS.getValue()) {
				LOGGER.severe("Old job is picked this should not Happen");
				throw new IllegalArgumentException("Exiting Import - Running old job this should not happen");
			}

			if (importProcessContext.getImportJobMeta() != null) {
				if (!importProcessContext.getImportJobMetaJson().isEmpty()) {
					Long assetId = (Long) importProcessContext.getImportJobMetaJson().get("assetId");
					if (assetId != null) {
						importProcessContext.setAssetId(assetId);
					}
				}
			}
			String moduleName = "";
			if(importProcessContext.getModule() == null){
				FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
				List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
				
				BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());
				
				boolean isBim = (bimImport!=null);
				if(isBim){
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(importProcessContext.getImportJobMeta());
					moduleName = ((JSONObject)json.get("moduleInfo")).get("module").toString();
				}
			}else{
				moduleName = importProcessContext.getModule().getName();
			}

				FacilioChain importChain = TransactionChainFactory.getImportChain();
				importChain.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				importChain.execute();

		} catch (Exception exception) {
			if (exception instanceof ImportParseException) {
				ImportParseException importParseException = (ImportParseException) exception;
				exceptionMessage = importParseException.getClientMessage();
			} else if (exception instanceof ImportFieldValueMissingException) {
				ImportFieldValueMissingException importFieldValueMissingException = (ImportFieldValueMissingException) exception;
				exceptionMessage = importFieldValueMissingException.getClientMessage();
			} else if (exception instanceof ImportMandatoryFieldsException) {
				ImportMandatoryFieldsException importAssetMandExp = (ImportMandatoryFieldsException) exception;
				exceptionMessage = importAssetMandExp.getClientMessage();
			} else if (exception instanceof ImportLookupModuleValueNotFoundException) {
				ImportLookupModuleValueNotFoundException importModMissing = (ImportLookupModuleValueNotFoundException) exception;
				exceptionMessage = importModMissing.getClientMessage();
			} else {
				sendExceptionMail = true;
				exceptionMessage = exception.getMessage();
			}
			stack = exception.getStackTrace();
			LOGGER.severe("Import Data Error Catch -- " + jobId + " " + exceptionMessage);
			throw exception;
		}
		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		// TODO Auto-generated method stub
		if (exceptionMessage != null) {
			constructErrorMessage();
		} else {
			importProcessContext.setStatus(ImportProcessContext.ImportStatus.IMPORTED.getValue());
			LOGGER.info("importProcessContext -- " + importProcessContext);
			ImportAPI.updateImportProcess(importProcessContext);
			LOGGER.info("IMPORT DATA JOB COMMAND COMPLETED -- " + jobId);
		}
		return false;
	}
	public void onError() throws Exception {
		constructErrorMessage();
	}
	public void constructErrorMessage() throws Exception {

		try {
			Exception mailExp = new Exception(exceptionMessage);
			if (stack != null) {
				mailExp.setStackTrace(stack);
			}
			if (sendExceptionMail) {
				CommonCommandUtil.emailException("Import Failed",
						"Import failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), mailExp);
			}
			LOGGER.log(Level.SEVERE, exceptionMessage);

			
			if (importProcessContext != null) {
				
				FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
				List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
				BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());
				boolean isBim = (bimImport!=null);
				
				JSONObject meta = importProcessContext.getImportJobMetaJson();
				if (meta != null && !meta.isEmpty()) {
					if(isBim){
						meta.put("errorMessage", "Module Name :: "+ bimImport.getModuleName() +" , "+exceptionMessage);
					}else{
						meta.put("errorMessage", exceptionMessage);
					}
					
				} else {
					meta = new JSONObject();
					if(isBim){
						meta.put("errorMessage", "Module Name :: "+ bimImport.getModuleName() +" , "+exceptionMessage);
					}else{
						meta.put("errorMessage", exceptionMessage);
					}
				}
				importProcessContext.setImportJobMeta(meta.toJSONString());
				importProcessContext.setStatus(ImportProcessContext.ImportStatus.FAILED.getValue());
				ImportAPI.updateImportProcess(importProcessContext);
				LOGGER.severe("Import failed: " + exceptionMessage);
				
				if(isBim){
					bimImport.setStatus(BimImportProcessMappingContext.Status.FAILED.getValue());
					Condition condition = CriteriaAPI.getIdCondition(bimImport.getId(), bimModule);
					BimAPI.updateBimImportProcessMapping(bimModule, bimFields, bimImport, condition);
					
					FacilioModule module = ModuleFactory.getBimIntegrationLogsModule();
					List<FacilioField> fields =  FieldFactory.getBimIntegrationLogsFields();
					BimIntegrationLogsContext bimIntegration = BimAPI.getBimIntegrationLog(module, fields, bimImport.getBimId());
					bimIntegration.setStatus(BimIntegrationLogsContext.Status.FAILED);
					BimAPI.updateBimIntegrationLog(module, fields, bimIntegration);
					
				}
			}
		} catch (Exception e1) {
			CommonCommandUtil.emailException("Import Exception Handling failed",
					"Import Exception Handling failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), e1);
			LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
		}

	}
}
