package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.plannedmaintenance.ExecuteNowExecutor;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class PMExecuteNowContextCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long plannerId = (long) context.get("plannerId");
        long resourceId = (long) context.get("resourceId");

        LOGGER.error("[execute now] planner Id" + plannerId);
        LOGGER.error("[execute now] resource Id" + resourceId);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> pmResourcePlanner = modBean.getAllFields("pmResourcePlanner");
        Map<String, FacilioField> resourcePlannerFieldMap = FieldFactory.getAsMap(pmResourcePlanner);
        FacilioField plannerIdField = resourcePlannerFieldMap.get("planner");
        FacilioField resourceIdField = resourcePlannerFieldMap.get("resource");

        List<LookupField> lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) resourceIdField);

        SelectRecordsBuilder<PMResourcePlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.beanClass(PMResourcePlanner.class);
        selectRecordsBuilder.select(pmResourcePlanner);
        selectRecordsBuilder.module(modBean.getModule("pmResourcePlanner"));
        selectRecordsBuilder.fetchSupplements(lookUpfields);
        selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(plannerIdField, plannerId+"",NumberOperators.EQUALS));
        selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(resourceIdField, resourceId+"", NumberOperators.EQUALS));

        List<PMResourcePlanner> pmResourcePlanners = selectRecordsBuilder.get();
        if (CollectionUtils.isEmpty(pmResourcePlanners)) {
            throw new IllegalArgumentException("No resource plans available");
        }

        PMResourcePlanner resourcePlanner = pmResourcePlanners.get(0);
        long pmId = resourcePlanner.getPmId();

        PlannedMaintenance plannedmaintenance = (PlannedMaintenance) V3Util.getRecord("plannedmaintenance", pmId, new HashMap<>());
        PMPlanner pmPlanner = (PMPlanner) V3Util.getRecord("pmPlanner", plannerId, new HashMap<>());

        ExecuteNowExecutor executeNowExecutor = new ExecuteNowExecutor();
        pmPlanner.setResourcePlanners(pmResourcePlanners);

        List<V3WorkOrderContext> workOrderContexts = executeNowExecutor.execute(context, plannedmaintenance, pmPlanner);
        FacilioModule workorderModule = modBean.getModule("workorder");
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = workOrderContexts.stream().map(i -> (ModuleBaseWithCustomFields) i).collect(Collectors.toList());
        V3Util.createRecord(workorderModule, moduleBaseWithCustomFields);
        return false;
    }
}
