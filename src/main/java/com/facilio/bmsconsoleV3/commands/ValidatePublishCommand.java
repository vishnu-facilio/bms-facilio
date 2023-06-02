package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class ValidatePublishCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long pmId = (Long) context.get("pmId");
        List<Long> pmIds = new ArrayList<>();
        if (pmId != null) {
            pmIds.add(pmId);
        } else {
            pmIds = (List<Long>) context.get("pmIds");
        }

        if (CollectionUtils.isEmpty(pmIds)) {
            throw new IllegalArgumentException("Pm ids cannot be empty");
        }

        validatePlanners(pmIds);

        return false;
    }

    private void validatePlanners(List<Long> pmIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("pmPlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmPlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);
        FacilioField pmIdField = fieldMap.get("pmId");

        FacilioModule pmResourcePlannerModule = modBean.getModule("pmResourcePlanner");

        List<SupplementRecord> supplementFields = new ArrayList<>();
        supplementFields.add((SupplementRecord) fieldMap.get("trigger"));


        SelectRecordsBuilder<PMPlanner> records = new SelectRecordsBuilder<>();
        records.select(pmPlannerFields)
                .module(pmPlannerModule)
                .fetchSupplements(supplementFields)
                .beanClass(PMPlanner.class)
                .andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
        List<PMPlanner> pmPlanners = records.get();

        for (PMPlanner pmPlanner: pmPlanners) {
            if (pmPlanner.getTrigger() == null || pmPlanner.getTrigger().getId() <= 0) {
                throw new IllegalArgumentException("Trigger is missing for " + pmPlanner.getName()+" pm - #"+pmPlanner.getPmId());
            }

            if (pmPlanner.getResourceCount() == null || pmPlanner.getResourceCount() <= 0) {
                throw new IllegalArgumentException("Asset/Space is missing in the planner " + pmPlanner.getName()+" pm - #"+pmPlanner.getPmId());
            }
        }
    }
}
