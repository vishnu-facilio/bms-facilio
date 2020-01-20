package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessWorkOrderImportCommand extends FacilioCommand {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, List<ReadingContext>>groupedContext = (HashMap<String, List<ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		List<ReadingContext>readingsContext = (List<ReadingContext>) context.get(ImportAPI.ImportProcessConstants.READINGS_CONTEXT_LIST);;
		
		BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext();
		for(ReadingContext readingContext: readingsContext) {
			Map<String, Object> temp = readingContext.getData();
			WorkOrderContext tempWo = FieldUtil.getAsBeanFromMap(temp, WorkOrderContext.class);
			if (tempWo.getSourceType() < 1) {
				int contextSourceType = readingContext.getSourceType();
				if (readingContext.getSourceType() > 0) {
					tempWo.setSourceType(contextSourceType);
				}
				else {
					tempWo.setSourceType(SourceType.WEB_ORDER);
				}
			}
			if (tempWo.getDueDate() == 0) {
				tempWo.setDueDate(-1);
			}
			if (tempWo.getStatus() != null && tempWo.getStatus().getId() > 0) {
				tempWo.setModuleState(tempWo.getStatus());
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			tempWo.setStateFlowId(StateFlowRulesAPI.getDefaultStateFlow(woModule).getId());
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

		context.put(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT, bulkWorkOrderContext);

		
		return false;
	}

}
