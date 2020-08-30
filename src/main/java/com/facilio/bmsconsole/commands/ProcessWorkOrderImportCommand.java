package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProcessWorkOrderImportCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(ProcessWorkOrderImportCommand.class.getName());
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, List<ReadingContext>>groupedContext = (HashMap<String, List<ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		List<ReadingContext>readingsContext = (List<ReadingContext>) context.get(ImportAPI.ImportProcessConstants.READINGS_CONTEXT_LIST);;
		
		BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext();
		for(ReadingContext readingContext: readingsContext) {
			if((AccountUtil.getCurrentOrg().getId() == 78l)) {
				LOGGER.info("ProcessWorkOrderImportCommand readingContext: -- " +readingContext.getData());
			}
			WorkOrderContext tempWo = FieldUtil.getAsBeanFromMap(readingContext.getData(), WorkOrderContext.class);
			if (readingContext.getModuleState() != null) {
				tempWo.setModuleState(readingContext.getModuleState());
				tempWo.setStatus(readingContext.getModuleState());
			}
			if (readingContext.getStateFlowId() > 0) {
				tempWo.setStateFlowId(readingContext.getStateFlowId());
			}
			if (tempWo.getSourceType() < 1) {
				int contextSourceType = readingContext.getSourceType();
				if (readingContext.getSourceType() > 0) {
					tempWo.setSourceType(contextSourceType);
				}
				else {
					tempWo.setSourceType(SourceType.IMPORT);
				}
			}
			if (tempWo.getDueDate() == 0) {
				tempWo.setDueDate(-1);
			}
			if (tempWo.getCreatedTime() <= 0) {
				tempWo.setCreatedTime(System.currentTimeMillis());
			}
			tempWo.setApprovalRuleId(-1);
			tempWo.setSiteId(readingContext.getSiteId());
			bulkWorkOrderContext.addContexts(tempWo, null,null,null);
		}
		Integer Setting = importProcessContext.getImportSetting();
		if(Setting == ImportProcessContext.ImportSetting.INSERT.getValue()) {
			Integer totalSize = 0;
			JSONObject meta = new JSONObject();	
			List<String> keys = new ArrayList(groupedContext.keySet());
			
			for(int i=0; i<keys.size(); i++) {
				totalSize =  totalSize + groupedContext.get(keys.get(i)).size();
			}
			if(!importProcessContext.getImportJobMetaJson().isEmpty()) {
				meta = importProcessContext.getFieldMappingJSON();
				meta.put("Inserted", totalSize + "");
			}
			else {
				meta.put("Inserted", totalSize + "");
			}
			importProcessContext.setImportJobMeta(meta.toJSONString());
			ImportAPI.updateImportProcess(importProcessContext);
		}
		context.put(ImportAPI.ImportProcessConstants.IS_FROM_IMPORT, true);
		context.put(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT, bulkWorkOrderContext);

		
		return false;
	}

}
