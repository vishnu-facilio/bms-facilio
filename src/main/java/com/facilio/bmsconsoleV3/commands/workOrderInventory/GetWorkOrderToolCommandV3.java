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
import org.apache.commons.chain.Context;

import java.util.*;

public class GetWorkOrderToolCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long toolId = (Long) context.get(FacilioConstants.ContextNames.TOOL);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkorderToolsContext workorderTool = new V3WorkorderToolsContext();
        if(toolId != null && workOrderId != null) {
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            V3ToolContext tool = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TOOL,toolId,V3ToolContext.class);
            if(tool != null) {
                workorderTool.setTool(tool);
                workorderTool.setWorkorder(workOrder);
                workorderTool.setRate(tool.getRate());
                workorderTool.setStoreRoom(tool.getStoreRoom());
            }
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_TOOLS,workorderTool);
        return false;
    }
}
