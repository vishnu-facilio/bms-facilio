package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypeVendorContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrUpdateToolVendorCommandV3 extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<V3ToolTypeVendorContext> toolTypeVendorsList = (List<V3ToolTypeVendorContext>) context.get(FacilioConstants.ContextNames.TOOL_VENDORS_LIST);
        if(toolTypeVendorsList!=null){
            long vendorId = (long) context.get(FacilioConstants.ContextNames.VENDOR_ID);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule toolVendorModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_VENDORS);
            List<FacilioField> toolVendorFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_VENDORS);
            Map<String, FacilioField> toolVendorFieldMap = FieldFactory.getAsMap(toolVendorFields);
            List<Long> toolTypesId = new ArrayList<>();
            Map<Long, Long> toolTypeVsVendor = new HashMap<>();

            SelectRecordsBuilder<V3ToolTypeVendorContext> toolVendorselectBuilder = new SelectRecordsBuilder<V3ToolTypeVendorContext>().select(toolVendorFields)
                    .table(toolVendorModule.getTableName()).moduleName(toolVendorModule.getName()).beanClass(V3ToolTypeVendorContext.class)
                    .andCondition(CriteriaAPI.getCondition(toolVendorFieldMap.get("vendor"), String.valueOf(vendorId),
                            NumberOperators.EQUALS));

            List<V3ToolTypeVendorContext> toolVendorsList = toolVendorselectBuilder.get();
            if (toolVendorsList != null && !toolVendorsList.isEmpty()) {
                for (V3ToolTypeVendorContext toolVendor : toolVendorsList) {
                    toolTypesId.add(toolVendor.getToolType().getId());
                    toolTypeVsVendor.put(toolVendor.getToolType().getId(), toolVendor.getId());
                }
            }

            List<V3ToolTypeVendorContext> toolVendorsToBeAdded = new ArrayList<>();
            for (V3ToolTypeVendorContext toolVendors : toolTypeVendorsList) {
                if (!toolTypesId.contains(toolVendors.getToolType().getId())) {
                    toolVendorsToBeAdded.add(toolVendors);
                } else {
                    toolVendors.setId(toolTypeVsVendor.get(toolVendors.getToolType().getId()));
                    updateToolVendor(toolVendorModule, toolVendorFields, toolVendors);
                }
            }

            if (CollectionUtils.isNotEmpty(toolVendorsToBeAdded)) {
                addTool(toolVendorModule, toolVendorFields, toolVendorsToBeAdded);
            }
        }
        return false;
    }

    private void addTool(FacilioModule module, List<FacilioField> fields, List<V3ToolTypeVendorContext> toolVendor) throws Exception {
        InsertRecordBuilder<V3ToolTypeVendorContext> readingBuilder = new InsertRecordBuilder<V3ToolTypeVendorContext>().module(module)
                .fields(fields).addRecords(toolVendor);
        readingBuilder.save();
    }

    private void updateToolVendor(FacilioModule module, List<FacilioField> fields, V3ToolTypeVendorContext toolVendor) throws Exception {
        UpdateRecordBuilder<V3ToolTypeVendorContext> builder = new UpdateRecordBuilder<V3ToolTypeVendorContext>().module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(toolVendor.getId(), module));
        builder.update(toolVendor);
    }
}
