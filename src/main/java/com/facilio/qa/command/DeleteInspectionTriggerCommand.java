package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteInspectionTriggerCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modbean = Constants.getModBean();
        List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(inspections)) {
            for (InspectionTemplateContext inspection : inspections) {
                if (inspection.getId() != 0) {
                    SelectRecordsBuilder<InspectionTriggerContext> selectBuilder = new SelectRecordsBuilder<InspectionTriggerContext>()
                            .moduleName(FacilioConstants.Inspection.INSPECTION_TRIGGER)
                            .beanClass(InspectionTriggerContext.class)
                            .select(modbean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER))
                            .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parent", String.valueOf(inspection.getId()), NumberOperators.EQUALS));

                    List<InspectionTriggerContext> triggerContextList = selectBuilder.get();

                    if (CollectionUtils.isNotEmpty(triggerContextList)) {
                        List<Long> triggerIdList = triggerContextList.stream().map(InspectionTriggerContext::getId).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(triggerIdList)){
                            Map<String, Object> recordId = new HashMap<>();
                            recordId.put(FacilioConstants.Inspection.INSPECTION_TRIGGER, triggerIdList);
                            V3Util.deleteRecords(FacilioConstants.Inspection.INSPECTION_TRIGGER,recordId,null,null,false);
                        }
                    }
                }
            }
        }
        return false;
    }
}
