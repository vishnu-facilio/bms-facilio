package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PMAfterPatchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Constants.getModuleName(context);
        Map<Long, ModuleBaseWithCustomFields> oldRecordMap = Constants.getOldRecordMap(context);
        List<ModuleBaseWithCustomFields> values = Constants.getRecordMap(context).get(Constants.getModuleName(context));


        for (ModuleBaseWithCustomFields value: values) {
            ModuleBaseWithCustomFields oldRecord = oldRecordMap.get(value.getId());
            if(value instanceof  PlannedMaintenance && oldRecord instanceof PlannedMaintenance) {
                PlannedMaintenance currentVal = (PlannedMaintenance) value;
                PlannedMaintenance oldVal = (PlannedMaintenance) oldRecord;

                if (currentVal.getAssignmentTypeEnum() == oldVal.getAssignmentTypeEnum()) {
                    continue;
                }

                clearResources(currentVal.getId());
            }
        }
        return false;
    }

    private void clearResources(long plannedMaintenanceId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("pmResourcePlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmResourcePlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

        DeleteRecordBuilder<PMPlanner> builder = new DeleteRecordBuilder<>();
        builder.module(pmPlannerModule)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), plannedMaintenanceId+"",  NumberOperators.EQUALS));
        builder.delete();
    }
}
