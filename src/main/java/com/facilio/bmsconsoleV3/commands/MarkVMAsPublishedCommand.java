package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class MarkVMAsPublishedCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vmTemplateModule = modBean.getModule("virtualMeterTemplate");
        List<FacilioField> vmTemplateFields = modBean.getAllFields("virtualMeterTemplate");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(vmTemplateFields);

        Long templateId = (Long) context.get("vmTemplateId");


        Map<String, Object> valMap = new HashMap<>();
        valMap.put("vmTemplateStatus", VirtualMeterTemplateContext.VMTemplateStatus.PUBLISHED.getVal());

        UpdateRecordBuilder<VirtualMeterTemplateContext> updateRecordBuilder = new UpdateRecordBuilder<>();
        updateRecordBuilder.module(vmTemplateModule)
                .fields(Collections.singletonList(fieldMap.get("vmTemplateStatus")))
                .andCondition(CriteriaAPI.getIdCondition(templateId, vmTemplateModule))
                .updateViaMap(valMap);

        return false;
    }
}
