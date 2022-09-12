package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetWorkOrderPlannedItemsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(workOrderPlannedItems)){
            for(WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems){
                if(workOrderPlannedItem.getDescription()==null){
                    String description = getItemType(workOrderPlannedItem.getItemType().getId());
                    workOrderPlannedItem.setDescription(description);
                }
            }
        }

        return false;
    }
    public String getItemType(Long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        String moduleName = FacilioConstants.ContextNames.ITEM_TYPES;
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        SelectRecordsBuilder<V3ItemTypesContext> builder = new SelectRecordsBuilder<V3ItemTypesContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(V3ItemTypesContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id,itemTypesModule));
        List<V3ItemTypesContext> list = builder.get();
        if(list.get(0).getDescription()!=null){
            return list.get(0).getDescription();
        }
      return null;
    }
}
