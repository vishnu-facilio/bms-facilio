
package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourPlanContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.*;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;
import com.facilio.bmsconsoleV3.context.labour.LabourCraftAndSkillContext;

public class WorkOrderActualsLabourCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            if(recordId != null) {

       if (moduleName.equals(FacilioConstants.CraftAndSKills.LABOUR_CRAFT)) {
           FacilioModule module = modBean.getModule(FacilioConstants.CraftAndSKills.LABOUR_CRAFT);
           List<FacilioField> fields = modBean.getAllFields(module.getName());
           Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

           List<LookupField> lookupFields = Arrays.asList((LookupField) fieldsAsMap.get("craft"), (LookupField) fieldsAsMap.get("skill"));

           SelectRecordsBuilder<LabourCraftAndSkillContext> select = new SelectRecordsBuilder<LabourCraftAndSkillContext>().moduleName(FacilioConstants.CraftAndSKills.LABOUR_CRAFT)
                   .select(fields)
                   .beanClass(LabourCraftAndSkillContext.class)
                   .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(recordId), NumberOperators.EQUALS));
           select.fetchSupplements(lookupFields);
           List<LabourCraftAndSkillContext> labourCraftAndSkillContext = select.get();
            if (CollectionUtils.isNotEmpty(labourCraftAndSkillContext)) {
               for (LabourCraftAndSkillContext labourCraftAndSkillContexts : labourCraftAndSkillContext) {
                   Map<String , Object> valueMap = new HashMap<>();
                   valueMap.put("currencyValue",labourCraftAndSkillContexts.getRate());
                   V3WorkOrderLabourContext woLabour = new V3WorkOrderLabourContext();
                   woLabour.setLabour(labourCraftAndSkillContexts.getLabour());

                   if (labourCraftAndSkillContexts.getCraft() != null) {
                       woLabour.setCraft(labourCraftAndSkillContexts.getCraft());
                   }
                   if (labourCraftAndSkillContexts.getSkill() != null) {
                       woLabour.setSkill(labourCraftAndSkillContexts.getSkill());
                   }
                   if(labourCraftAndSkillContexts.getRate()!= null){

                       woLabour.setRate(valueMap);
                   }

                   context.put(FacilioConstants.ContextNames.WO_LABOUR, woLabour);
               }
           }
       }

        if (moduleName.equals(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN)) {
        V3WorkOrderLabourPlanContext woLabourplan = V3RecordAPI.getRecord(moduleName, recordId);
        V3WorkOrderLabourContext woLabour = new V3WorkOrderLabourContext();

            woLabour.setCraft(woLabourplan.getCraft());
            woLabour.setSkill(woLabourplan.getSkill());
            woLabour.setDuration(woLabourplan.getDuration());
            woLabour.setRate(woLabourplan.getRate());
            woLabour.setTotalAmount(woLabourplan.getTotalPrice());

        context.put(FacilioConstants.ContextNames.WO_LABOUR, woLabour);
    }
}

        return false;
    }
}