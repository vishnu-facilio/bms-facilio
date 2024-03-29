package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class InsertImportDataIntoLogCommand extends FacilioCommand {

	private boolean hasDuplicates = false;
	private static Logger LOGGER = Logger.getLogger(InsertImportDataIntoLogCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		LOGGER.info("----Inserting into Import Log Table-- start");
		// TODO Auto-generated method stub
		HashMap<String, List<ImportRowContext>> groupedContext = 
				(HashMap<String, List<ImportRowContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT);
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		Integer row_count = (Integer) context.get(ImportAPI.ImportProcessConstants.ROW_COUNT);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getImportProcessLogModule().getTableName())
				.fields(FieldFactory.getImportProcessLogFields());
		
		
		for(String uniqueString : groupedContext.keySet()) {
			
			ImportProcessLogContext logContext= new ImportProcessLogContext();
			logContext.setImportMode(importProcessContext.getImportMode());
			
			if(importProcessContext.getImportMode() == ImportProcessContext.ImportMode.READING.getValue()) {
				logContext.setParentId(groupedContext.get(uniqueString).get(0).getParentId(), importProcessContext.getModule());
				logContext.setTtime(groupedContext.get(uniqueString).get(0).getTtime());
				logContext.setTemplateId(importProcessContext.getTemplateId());
			}
			
			logContext.setImportId(importProcessContext.getId());
			logContext.setOrgId(importProcessContext.getOrgId());
			importProcessContext.setTotalRows(row_count.longValue());
			
			
			if (importProcessContext.getImportMode() == ImportProcessContext.ImportMode.NORMAL.getValue()) {
				if (groupedContext.get(uniqueString).get(0).getError_resolved() != null && groupedContext
						.get(uniqueString).get(0)
						.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.FOUND_IN_DB.getValue()) {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.FOUND_IN_DB.getValue());
				} else if (groupedContext.get(uniqueString).size() > 1) {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.UNRESOLVED.getValue());
				} else {
					logContext.setError_resolved(
							ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue());
				}
			} else {
				if (logContext.getParentId() == null || logContext.getParentId() < 0) {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.OTHER_ERRORS.getValue());
				} else if (groupedContext.get(uniqueString).size() == 1) {
					logContext.setError_resolved(
							ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue());
				} else {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.UNRESOLVED.getValue());
				}
			}

			logContext.setRowContexts(groupedContext.get(uniqueString));
			if(hasDuplicates == false) {
				hasDuplicates = hasDuplicate(logContext, importProcessContext);
			}
			JSONObject props = new JSONObject();
			props = FieldUtil.getAsJSON(logContext);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();

		context.put(ImportAPI.ImportProcessConstants.HAS_DUPLICATE_ENTRIES, hasDuplicates);
		LOGGER.info("HAS DUPLICATES" + hasDuplicates);
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		
		LOGGER.info("----Inserting into Import Log Table----- end");
		return false;
	}
	
	private boolean hasDuplicate(ImportProcessLogContext logContext, ImportProcessContext importProcessContext) throws Exception {
		// TODO Auto-generated method stub
		if (importProcessContext.getImportMode() == ImportProcessContext.ImportMode.NORMAL.getValue()) {
			if (logContext.getRowContexts().size() > 1) {
				return true;
			} else if (logContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.FOUND_IN_DB
					.getValue()) {
				return true;
			} else {
				return false;
			}
		} else if (logContext.getRowContexts().size() > 1) {
			if (logContext.getParentId() < 0 || logContext.getParentId() == null) {
				return false;
			} else {
				return true;
			}
		}
			
		return false;
	}

	

}
