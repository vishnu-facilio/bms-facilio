
        package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

        import com.facilio.bmsconsoleV3.context.CraftContext;
        import com.facilio.bmsconsoleV3.context.labour.LabourContextV3;
        import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourContext;
        import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourPlanContext;
        import com.facilio.bmsconsoleV3.util.V3RecordAPI;
        import com.facilio.chain.FacilioContext;
        import com.facilio.command.FacilioCommand;
        import com.facilio.constants.FacilioConstants;
        import com.facilio.modules.ModuleBaseWithCustomFields;
        import com.facilio.v3.context.Constants;
        import com.facilio.v3.util.V3Util;
        import org.apache.commons.chain.Context;
        import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;
import com.facilio.bmsconsoleV3.context.labour.LabourCraftAndSkillContext;

public class WorkOrderActualsLabourCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

     if(recordId != null) {

       if (moduleName.equals(FacilioConstants.ContextNames.LABOUR)) {
           LabourContextV3   labour = (LabourContextV3) V3Util.getRecord(moduleName, recordId,null);
       // LabourContextV3 labour = V3RecordAPI.getRecord(moduleName, recordId);

        V3WorkOrderLabourContext woLabour = new V3WorkOrderLabourContext();
        woLabour.setLabour(labour);
        woLabour.setRate(labour.getCost());
           if (labour.getLabourCrafts() != null) {
            List<LabourCraftAndSkillContext> labourCraftAndSkills = labour.getLabourCrafts();
            for (LabourCraftAndSkillContext labourCraftAndSkill : labourCraftAndSkills) {
                if (labourCraftAndSkill.getIsDefault() == true) {
                    woLabour.setCraft(labourCraftAndSkill.getCraft());
                    if (labourCraftAndSkill.getSkill() != null) {
                        woLabour.setSkill(labourCraftAndSkill.getSkill());
                    }
                }

            }
        }
        context.put(FacilioConstants.ContextNames.WO_LABOUR, woLabour);
    }

        if (moduleName.equals(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN)) {
        V3WorkOrderLabourPlanContext woLabourplan = V3RecordAPI.getRecord(moduleName, recordId);
        V3WorkOrderLabourContext woLabour = new V3WorkOrderLabourContext();
        woLabour.setCraft(woLabourplan.getCraft());
        woLabour.setSkill(woLabourplan.getSkill());
        woLabour.setDuration(woLabourplan.getDuration());
        woLabour.setRate(woLabourplan.getRate());
        woLabour.setCost(woLabourplan.getTotalPrice());
        context.put(FacilioConstants.ContextNames.WO_LABOUR, woLabour);
    }
}

        return false;
    }
}