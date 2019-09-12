/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.PointsProcessContext;
import com.facilio.bmsconsole.context.ImportPointsDataLogContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ImportPointsAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

/**
 * @author facilio
 *
 */
public class GenericImportDataIntoPointsLogCommand implements Command {

	/* (non-Javadoc)
	 * @see org.apache.commons.chain.Command#execute(org.apache.commons.chain.Context)
	 */
	
	private boolean hasDuplicates = false;
	private static Logger LOGGER = Logger.getLogger(InsertImportDataIntoLogCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.severe("----Inserting into Import Log Table-- start");
		// TODO Auto-generated method stub
		HashMap<String, List<ImportRowContext>> groupedContext = 
				(HashMap<String, List<ImportRowContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT);
		PointsProcessContext importProcessContext = (PointsProcessContext) context.get(ImportPointsAPI.ImportPointsConstants.POINTS_PROCESS_CONTEXT);
		Long row_count = (Long) context.get(ImportPointsAPI.ImportPointsConstants.ROW_COUNT);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getImportPointsLogModule().getTableName())
				.fields(FieldFactory.getImportPointsLogFields());
		
		
		for(String uniqueString : groupedContext.keySet()) {
			
			ImportPointsDataLogContext logContext= new ImportPointsDataLogContext();
			logContext.setImportMode(importProcessContext.getImportMode());
			
			if(importProcessContext.getImportMode() == PointsProcessContext.ImportMode.READING.getValue()) {
				logContext.setTtime(groupedContext.get(uniqueString).get(0).getTtime());
				logContext.setTemplateId(importProcessContext.getTemplateId());
			}
			
			logContext.setImportId(importProcessContext.getId());
			logContext.setOrgId(importProcessContext.getOrgId());
			importProcessContext.setTotalRows(row_count);
			
			
			if(importProcessContext.getImportMode() == PointsProcessContext.ImportMode.NORMAL.getValue()) {
				if(groupedContext.get(uniqueString).size() > 1) {
					logContext.setError_resolved(PointsProcessContext.ImportLogErrorStatus.UNRESOLVED.getValue());
				}
				else {
					logContext.setError_resolved(PointsProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue());
				}
			}
//			else {
//				if(logContext.get == null || logContext.getParentId() < 0) {
//					logContext.setError_resolved(PointsProcessContext.ImportLogErrorStatus.OTHER_ERRORS.getValue());
//				}
//				else if(groupedContext.get(uniqueString).size() == 1) {
//					logContext.setError_resolved(PointsProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue());
//				}
//				else {
//					logContext.setError_resolved(PointsProcessContext.ImportLogErrorStatus.UNRESOLVED.getValue());
//				}
//			}
			
			logContext.setRowContexts(groupedContext.get(uniqueString));
			if(hasDuplicates == false) {
				hasDuplicates = hasDuplicate(logContext, importProcessContext);
			}
			JSONObject props = new JSONObject();
			props = FieldUtil.getAsJSON(logContext);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();

		context.put(ImportPointsAPI.ImportPointsConstants.HAS_DUPLICATE_ENTRIES, hasDuplicates);
		LOGGER.severe("HAS DUPLICATES" + hasDuplicates);
		importProcessContext.setStatus(PointsProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());
		ImportPointsAPI.updateImportProcess(importProcessContext);
		
		LOGGER.severe("----Inserting into Import Log Table----- end");
		
		return false;
	}
	
	private boolean hasDuplicate(ImportPointsDataLogContext logContext, PointsProcessContext importProcessContext) throws Exception {
		// TODO Auto-generated method stub
		if(logContext.getRowContexts().size() > 1) {
			if(importProcessContext.getImportMode() == PointsProcessContext.ImportMode.NORMAL.getValue()) {
				if(logContext.getRowContexts().size() > 1) {
					return true;
				}
				else {
					return false;
				}
			}
			
			
		}
		return false;
	}

}

