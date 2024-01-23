package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Priority;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class AttachResourcePlannerViaScriptCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> resourcePlannerList = Constants.getRecordMap(context).get(Constants.getModuleName(context));
        if(CollectionUtils.isEmpty(resourcePlannerList)){
            return false;
        }

        HashSet<Long> resourceIdsSet = new HashSet<>();
        for(ModuleBaseWithCustomFields resourcePlanner: resourcePlannerList){
            resourceIdsSet.add(((PMResourcePlanner)resourcePlanner).getResourceId());
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule resourcePlannerModule = modBean.getModule(Constants.getModuleName(context));
        List<FacilioField> resourcePlannerFields = modBean.getAllFields(Constants.getModuleName(context));

        // Insert into resource planner
        InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<>()
                .module(resourcePlannerModule)
                .fields(resourcePlannerFields)
                .addRecords(resourcePlannerList);
        insertRecordBuilder.save();

        // Update RP Count
        long plannerId = (long) context.get("plannerId");
        Map<String, FacilioField> resourcePlannerFieldsMap = FieldFactory.getAsMap(resourcePlannerFields);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(resourcePlannerFieldsMap.get("planner"), plannerId + "", NumberOperators.EQUALS));

        SelectRecordsBuilder<PMResourcePlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(resourcePlannerFields)
                .module(resourcePlannerModule)
                .beanClass(PMResourcePlanner.class)
                .andCriteria(criteria);
        List<PMResourcePlanner> pmResourcePlannerList = selectRecordsBuilder.get();

        if(CollectionUtils.isEmpty(pmResourcePlannerList)){
            return false;
        }

        List<PMResourcePlanner> insertedPmResourcePlannerList = pmResourcePlannerList.stream().filter(pmResourcePlanner -> {
            return pmResourcePlanner.getPlanner().getId() == plannerId && resourceIdsSet.contains(pmResourcePlanner.getResourceId());
        }).collect(Collectors.toList());

        context.put("insertedPMResourcePlannerList", insertedPmResourcePlannerList.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList()));

        List<FacilioField> plannerFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_PLANNER);
        Map<String, FacilioField> plannerFieldsMap = FieldFactory.getAsMap(plannerFields);
        FacilioModule plannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_PLANNER);

        PMPlanner pmPlanner = new PMPlanner();
        pmPlanner.setResourceCount((long)pmResourcePlannerList.size());

        Map<String, Object> pmPlannerMap = FieldUtil.getAsProperties(pmPlanner);

        UpdateRecordBuilder<ModuleBaseWithCustomFields> updateRecordBuilder = new UpdateRecordBuilder<>()
                .module(plannerModule)
                .fields(Collections.singletonList(plannerFieldsMap.get("resourceCount")))
                .andCondition(CriteriaAPI.getIdCondition(plannerId, plannerModule));
        int updated = updateRecordBuilder.updateViaMap(pmPlannerMap);
        LOGGER.log(Priority.INFO, "Updated resource count to " + pmResourcePlannerList.size());
        LOGGER.log(Priority.INFO, "Updated: " + updated);

        return false;
    }
}
