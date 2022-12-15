package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.*;

public class WorkOrderToolsUnsavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> toolIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);

        if(toolIds != null && workOrderId != null) {
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Collection<SupplementRecord> supplementFields = new ArrayList<>();
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            supplementFields.add((LookupField) fieldsMap.get("toolType"));
            supplementFields.add((LookupField) fieldsMap.get("storeRoom"));

            List<V3ToolContext> tools = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TOOL,toolIds,V3ToolContext.class,supplementFields);
            List<V3WorkorderToolsContext> workorderTools = new ArrayList<>();
            for(V3ToolContext tool : tools){
                V3WorkorderToolsContext workorderTool = new V3WorkorderToolsContext();
                workorderTool.setTool(tool);
                workorderTool.setWorkorder(workOrder);
                workorderTool.setRate(tool.getRate());
                workorderTool.setQuantity(1.0);
                workorderTool.setStoreRoom(tool.getStoreRoom());
                workorderTool.setDuration(1.0);
                Double cost = null;
                if (tool.getRate()!=null && tool.getRate() > 0) {
                    cost = workorderTool.getDuration() * workorderTool.getQuantity() * workorderTool.getRate();
                }
                workorderTool.setCost(cost);
                workorderTools.add(workorderTool);
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.WORKORDER_TOOLS,workorderTools);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.WORKORDER_TOOLS);
            context.put(FacilioConstants.ContextNames.WORKORDER_TOOLS,workorderTools);
        }
        return false;
    }
}
