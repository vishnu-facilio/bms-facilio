package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
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

        if (CollectionUtils.isNotEmpty(records)) {

            for (ModuleBaseWithCustomFields record : records) {

                V3WorkOrderLabourContext workOrderLabour = (V3WorkOrderLabourContext) record;

                LabourContextV3 labour = workOrderLabour.getLabour();

                Objects.requireNonNull(labour, "Labour Should not be Empty while adding Workorder labour");

                FacilioUtil.throwIllegalArgumentException(isLabourAssignedJobForGivenDuration(workOrderLabour.getStartTime(), labour.getId(), bean), "Job was assigned for this Labour for given Time.");

            }
        }

        return false;
    }

    private boolean isLabourAssignedJobForGivenDuration(long currentStartTime,long labourId, ModuleBean bean) throws Exception {

        FacilioModule module = bean.getModule(FacilioConstants.ContextNames.WO_LABOUR);

        SelectRecordsBuilder<V3WorkOrderLabourContext> builder = new SelectRecordsBuilder<V3WorkOrderLabourContext>()
                .module(module)
                .select(bean.getAllFields(module.getName()))
                .beanClass(V3WorkOrderLabourContext.class)
                .andCondition(CriteriaAPI.getCondition("LABOUR","labour",labourId+"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("END_TIME","endTime",currentStartTime+"", DateOperators.IS_AFTER));

        List<V3WorkOrderLabourContext> props = builder.get();

        return CollectionUtils.isNotEmpty(props);
    }
}
