package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateReservedQuantityForToolsCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3WorkorderToolsContext> workOrderTools = (List<V3WorkorderToolsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

        if(CollectionUtils.isNotEmpty(workOrderTools)){
            for(V3WorkorderToolsContext workOrderTool : workOrderTools){
                if(workOrderTool.getInventoryReservation()!=null && workOrderTool.getInventoryReservation().getId()>0){
                    Double woQuantity = workOrderTool.getQuantity();
                    V3ToolContext tool = V3ToolsApi.getTool(workOrderTool.getTool().getId());
                    Double reservedQuantity = tool.getReservedQuantity();
                    Double newReservedQuantity = reservedQuantity - woQuantity;

                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String toolModuleName = FacilioConstants.ContextNames.TOOL;
                    FacilioModule module = modBean.getModule(toolModuleName);
                    List<FacilioField> fields = modBean.getAllFields(toolModuleName);

                    List<FacilioField> updatedFields = new ArrayList<>();
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    updatedFields.add(fieldsMap.get("reservedQuantity"));

                    Map<String, Object> map = new HashMap<>();
                    map.put("reservedQuantity", newReservedQuantity);

                    UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
                            .module(module).fields(updatedFields)
                            .andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
                    updateBuilder.updateViaMap(map);

                }
            }
        }
        return false;
    }
}

