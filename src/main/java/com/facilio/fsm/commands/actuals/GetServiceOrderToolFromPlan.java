package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import com.facilio.fsm.context.ServiceOrderToolsContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import org.apache.commons.chain.Context;

public class GetServiceOrderToolFromPlan extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long plannedToolId = (Long) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOL_ID);
        if(plannedToolId!=null && plannedToolId>0){
           ServiceOrderPlannedToolsContext plannedTool = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS,plannedToolId, ServiceOrderPlannedToolsContext.class);
           if(plannedTool!=null){
               if(plannedTool.getToolType()==null){
                   throw new FSMException(FSMErrorCode.TOOL_TYPE_REQUIRED);
               }
               if(plannedTool.getStoreRoom()==null){
                   throw new FSMException(FSMErrorCode.STORE_REQUIRED);
               }
               V3ToolContext tool = V3ToolsApi.getTool(plannedTool.getToolType().getId(), plannedTool.getStoreRoom().getId());
               if(tool==null){
                   throw new FSMException(FSMErrorCode.TOOL_NOT_PRESENT);
               }
               V3ToolContext toolContext=  new V3ToolContext();
               toolContext.setId(tool.getId());
               ServiceOrderToolsContext serviceOrderTool = new ServiceOrderToolsContext();
               serviceOrderTool.setTool(toolContext);
               serviceOrderTool.setStoreRoom(plannedTool.getStoreRoom());
               if(plannedTool.getQuantity()!=null){
                   serviceOrderTool.setQuantity(plannedTool.getQuantity());
               }
               if(plannedTool.getDuration()!=null){
                   serviceOrderTool.setDuration(plannedTool.getDuration());
               }
               context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS,serviceOrderTool);
           }
        }
        return false;
    }
}
