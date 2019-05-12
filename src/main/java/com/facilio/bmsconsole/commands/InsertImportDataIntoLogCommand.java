package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class InsertImportDataIntoLogCommand implements Command {

	private boolean hasDuplicates = false;
	private static Logger LOGGER = Logger.getLogger(InsertImportDataIntoLogCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		LOGGER.severe("----Inserting into Import Log Table-- start");
		// TODO Auto-generated method stub
		HashMap<String, List<ImportRowContext>> groupedContext = 
				(HashMap<String, List<ImportRowContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT);
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		Integer row_count = (Integer) context.get(ImportAPI.ImportProcessConstants.ROW_COUNT);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getImportProcessLogModule().getTableName())
				.fields(FieldFactory.getImportProcessLogFields());
		
		LOGGER.severe("Grouped Context");
		LOGGER.severe(groupedContext.toString());
		
		
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
			logContext.setTotal_rows(row_count);
			
			
			if(importProcessContext.getImportMode() == ImportProcessContext.ImportMode.NORMAL.getValue()) {
				if(groupedContext.get(uniqueString).size() > 1) {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.UNRESOLVED.getValue());
				}
				else {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue());
				}
			}
			else {
				if(logContext.getParentId() == null || logContext.getParentId() < 0) {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.OTHER_ERRORS.getValue());
				}
				else if(groupedContext.get(uniqueString).size() == 1) {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue());
				}
				else {
					logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.UNRESOLVED.getValue());
				}
			}
			
			logContext.setRowContexts(groupedContext.get(uniqueString));
			if(hasDuplicates == false) {
				hasDuplicates = hasDuplicate(logContext, importProcessContext);
			}
			JSONObject props = FieldUtil.getAsJSON(logContext);
			LOGGER.severe("props" + props.toString());
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
		context.put(ImportAPI.ImportProcessConstants.HAS_DUPLICATE_ENTRIES, hasDuplicates);
		LOGGER.severe("HAS DUPLICATES" + hasDuplicates);
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		
		LOGGER.severe("----Inserting into Import Log Table----- end");
		return false;
	}
	
	private boolean hasDuplicate(ImportProcessLogContext logContext, ImportProcessContext importProcessContext) throws Exception {
		// TODO Auto-generated method stub
		if(logContext.getRowContexts().size() > 1) {
			if(importProcessContext.getImportMode() == ImportProcessContext.ImportMode.NORMAL.getValue()) {
				if(logContext.getRowContexts().size() > 1) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				if(logContext.getParentId() < 0 || logContext.getParentId() == null) {
					return false;
				}
				else {
					return true;
				}
			}
			
		}
		return false;
	}

	

}
