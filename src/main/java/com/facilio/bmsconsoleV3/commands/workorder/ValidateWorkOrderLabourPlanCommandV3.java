package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ValidateWorkOrderLabourPlanCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        ModuleBean bean =Constants.getModBean();
        FacilioModule module = bean.getModule(FacilioConstants.ContextNames.WO_LABOUR);
        List<FacilioField> fields = bean.getAllFields(module.getName());

        if (CollectionUtils.isNotEmpty(records)) {

            for (ModuleBaseWithCustomFields record : records) {

                V3WorkOrderLabourContext workOrderLabour = (V3WorkOrderLabourContext) record;

                LabourContextV3 labour = workOrderLabour.getLabour();

                Objects.requireNonNull(labour.getId(), "Labour Should not be Empty while adding Workorder labour");

                FacilioUtil.throwIllegalArgumentException(isLabourAssignedJobForGivenDuration(workOrderLabour, labour.getId(), module,fields), "Job was assigned for this Labour for given Time.");

            }
        }

        return false;
    }

    private boolean isLabourAssignedJobForGivenDuration(V3WorkOrderLabourContext workOrderLabour, long labourId, FacilioModule module, List<FacilioField> fields) throws Exception {

        String startTime = workOrderLabour.getStartTime()+"";
        String endTime = workOrderLabour.getEndTime()+"";

        Criteria criteria = new Criteria();

        criteria.addOrCondition(CriteriaAPI.getCondition("START_TIME",FacilioConstants.ContextNames.START_TIME,startTime,DateOperators.IS));
        criteria.addOrCondition(CriteriaAPI.getCondition("END_TIME",FacilioConstants.ContextNames.END_TIME,endTime,DateOperators.IS));
        criteria.addOrCondition(CriteriaAPI.getCondition("START_TIME",FacilioConstants.ContextNames.START_TIME,startTime+","+endTime,DateOperators.BETWEEN));
        criteria.addOrCondition(CriteriaAPI.getCondition("END_TIME",FacilioConstants.ContextNames.END_TIME,startTime+","+endTime,DateOperators.BETWEEN));

        Criteria criteria1 = new Criteria();
        criteria1.addAndCondition(CriteriaAPI.getCondition("START_TIME",FacilioConstants.ContextNames.START_TIME,startTime,NumberOperators.LESS_THAN_EQUAL));
        criteria1.addAndCondition(CriteriaAPI.getCondition("END_TIME",FacilioConstants.ContextNames.END_TIME,endTime,NumberOperators.GREATER_THAN_EQUAL));

        SelectRecordsBuilder<V3WorkOrderLabourContext> builder = new SelectRecordsBuilder<V3WorkOrderLabourContext>()
                .module(module)
                .select(fields)
                .beanClass(V3WorkOrderLabourContext.class)
                .andCondition(CriteriaAPI.getCondition("LABOUR","labour",labourId+"", NumberOperators.EQUALS))
                .andCriteria(criteria1)
                .orCriteria(criteria);

        List<V3WorkOrderLabourContext> props = builder.get();

        return CollectionUtils.isNotEmpty(props);
    }
}
