package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.logging.Logger;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.sql.GenericInsertRecordBuilder;

public class InsertImportDataIntoLogCommand implements Command {

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
			LOGGER.severe(uniqueString);
			ImportProcessLogContext logContext= new ImportProcessLogContext();
			System.out.println(groupedContext.get(uniqueString).get(0).getParentId());
			logContext.setParentId(groupedContext.get(uniqueString).get(0).getParentId());
			LOGGER.severe("parentId set" + logContext.getParentId());
			logContext.setTtime(groupedContext.get(uniqueString).get(0).getTtime());
			LOGGER.severe("ttime set");
			logContext.setImportId(importProcessContext.getId());
			LOGGER.severe("process id set");
			logContext.setOrgId(importProcessContext.getOrgId());
			LOGGER.severe("ord id set");
			logContext.setTemplateId(importProcessContext.getTemplateId());
			LOGGER.severe("templat id set");
			logContext.setTotal_rows(row_count);
			LOGGER.severe("row count set");
			if(logContext.getParentId() == null || logContext.getParentId() < 0) {
				logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.OTHER_ERRORS.getValue());
			}
			else if(groupedContext.get(uniqueString).size() == 1) {
				LOGGER.severe("no validation set");
				logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue());
			}
			else {
				LOGGER.severe("unresolved set");
				logContext.setError_resolved(ImportProcessContext.ImportLogErrorStatus.UNRESOLVED.getValue());
			}
			logContext.setRowContexts(groupedContext.get(uniqueString));
			LOGGER.severe("logcontext");
			LOGGER.severe(logContext.toString());
			JSONObject props = FieldUtil.getAsJSON(logContext);
			LOGGER.severe("props" + props.toString());
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		LOGGER.severe("----Inserting into Import Log Table----- end");
		return false;
	}

}
