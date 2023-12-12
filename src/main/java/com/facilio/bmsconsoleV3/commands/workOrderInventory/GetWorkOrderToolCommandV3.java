package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetWorkOrderToolCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long toolId = (Long) context.get(FacilioConstants.ContextNames.TOOL);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkorderToolsContext workorderTool = new V3WorkorderToolsContext();
        if(toolId != null) {
            LookupField toolTypeField = (LookupField) Constants.getModBean().getField("toolType", FacilioConstants.ContextNames.TOOL);
            List<V3ToolContext> tools = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TOOL, Collections.singletonList(toolId), V3ToolContext.class, Collections.singletonList(toolTypeField));
            if(CollectionUtils.isNotEmpty(tools)){
                V3ToolContext tool = tools.get(0);
                if(tool != null) {
                    workorderTool.setTool(tool);
                    workorderTool.setRate(tool.getToolType().getSellingPrice());
                    workorderTool.setStoreRoom(tool.getStoreRoom());
                    workorderTool.setBin(tool.getDefaultBin());
                }
            }
         
        }
        if(workOrderId != null){
            V3WorkOrderContext workOrder = V3RecordAPI.getRecord(FacilioConstants.ContextNames.WORK_ORDER, workOrderId, V3WorkOrderContext.class);
            workorderTool.setWorkorder(workOrder);
            if(workOrder.getAssignedTo() != null){
                workorderTool.setIssuedTo(workOrder.getAssignedTo());
            }
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_TOOLS,workorderTool);
        return false;
    }
}
