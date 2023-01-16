package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PMPlannerOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.recordcustomization.RecordCustomizationValuesContext;
import org.apache.commons.chain.Context;

import java.util.*;

/**
 * AddTimelineViewForPMPlannerCommand adds the following Timeline Views,
 * - Asset/Space
 * - Staff
 * and updates the PM_Planner with its IDs.
 * <p>
 * Format of the view name:
 * planner_timelineView_{field_name}_{pm_id}_{planner_id}
 */
public class AddTimelineViewForPMPlannerCommand extends FacilioCommand {

    private ApplicationContext getAppContext() throws Exception {
        ApplicationContext app = AccountUtil.getCurrentApp();
        if (app != null){
            return app;
        }
        return ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId();
        ApplicationContext app = getAppContext();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (moduleName == null) {
            throw new Exception("Module name cannot be null.");
        }

        Map<String, Object> recordMap = ((Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP));
        List<PMPlanner> pmPlannerList = (List<PMPlanner>) recordMap.get(moduleName);

        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        FacilioModule pmPlannerModule = modBean.getModule(moduleName);
        List<FacilioField> pmPlannerFields = modBean.getAllFields(moduleName);

        for (PMPlanner pmPlanner : pmPlannerList) {

            // Create Asset/Space TimelineView
            StringBuilder stringBuilder = new StringBuilder("planner_timelineView_");
            stringBuilder.append("resource_")
                    .append(pmPlanner.getPmId()).append("_")
                    .append(pmPlanner.getId());

            TimelineViewContext resourceTimelineView = getDefaultTimelineViewContext(stringBuilder.toString(),
                    "Asset",
                    app.getId(), module);

            resourceTimelineView.setGroupByFieldId(fieldsMap.get("resource").getFieldId());
            resourceTimelineView.setStartDateFieldId(fieldsMap.get("scheduledStart").getFieldId());
            resourceTimelineView.setEndDateFieldId(fieldsMap.get("estimatedEnd").getFieldId());
            
            Criteria resourceCriteria = new Criteria();
            resourceCriteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", pmPlanner.getId()+"", PMPlannerOperator.PLANNER_RESOURCE_IS));
            resourceTimelineView.setGroupCriteria(resourceCriteria);

            //addRecordCustomizationWithTicketPriority(fieldsMap, resourceTimelineView);
            setPreOpenWorOrderCriteriaForTimelineView(module, fieldsMap, pmPlanner, resourceTimelineView);

            long resourceTimelineViewId = ViewAPI.addView(resourceTimelineView, orgId);
            pmPlanner.setResourceTimelineViewId(resourceTimelineViewId);
            pmPlanner.setResourceTimelineView(ViewAPI.getView(resourceTimelineViewId));

            // Create Staff TimelineView
            stringBuilder = new StringBuilder("planner_timelineView_");
            stringBuilder.append("assignedTo_")
                    .append(pmPlanner.getPmId()).append("_")
                    .append(pmPlanner.getId());

            TimelineViewContext assignedToTimelineView = getDefaultTimelineViewContext(stringBuilder.toString(),
                    "Staff", app.getId(), module);

            assignedToTimelineView.setGroupByFieldId(fieldsMap.get("assignedTo").getFieldId());
            assignedToTimelineView.setStartDateFieldId(fieldsMap.get("scheduledStart").getFieldId());
            assignedToTimelineView.setEndDateFieldId(fieldsMap.get("estimatedEnd").getFieldId());
            
            Criteria assignedToCriteria = new Criteria();
            assignedToCriteria.addAndCondition(CriteriaAPI.getCondition("ORG_Users.ORG_USERID", "ouid", pmPlanner.getId()+"", PMPlannerOperator.PLANNER_ASSIGNED_IS));
            assignedToTimelineView.setGroupCriteria(assignedToCriteria);

            //addRecordCustomizationWithTicketPriority(fieldsMap, assignedToTimelineView);
            setPreOpenWorOrderCriteriaForTimelineView(module, fieldsMap, pmPlanner, assignedToTimelineView);

            long staffTimelineViewId = ViewAPI.addView(assignedToTimelineView, orgId);
            pmPlanner.setStaffTimelineViewId(staffTimelineViewId);
            pmPlanner.setStaffTimelineView(ViewAPI.getView(staffTimelineViewId));

            // Update PM_Planner with the `resourceTimelineViewId` and `assignedToTimelineViewId`
            V3RecordAPI.updateRecord(pmPlanner, pmPlannerModule, pmPlannerFields);
        }

        recordMap.put(moduleName, pmPlannerList);
        context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);

        return false;
    }

    /**
     * Helper function to add PreOpen WorkOrder Criteria For Timeline View.
     * @param module
     * @param fieldsMap
     * @param pmPlanner
     * @param timelineView
     * @throws Exception
     */
    private static void setPreOpenWorOrderCriteriaForTimelineView(FacilioModule module, Map<String,
            FacilioField> fieldsMap, PMPlanner pmPlanner, TimelineViewContext timelineView) throws Exception {

        Criteria criteria = new Criteria();
        List<Condition> conditions = new ArrayList<>();
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get("pmV2"), String.valueOf(pmPlanner.getPmId()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get("pmPlanner"), String.valueOf(pmPlanner.getId()), NumberOperators.EQUALS));
//        List<FacilioStatus> statusList = TicketAPI.getStatusOfStatusType(module, FacilioStatus.StatusType.PRE_OPEN);
//        for (FacilioStatus status: statusList){
//            if(status.getType() == FacilioStatus.StatusType.PRE_OPEN && status.getStatus().equals("preopen")) {
//                conditions.add(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(status.getId()), NumberOperators.EQUALS));
//            }
//        }
        criteria.addAndConditions(conditions);
        timelineView.setCriteria(criteria);
    }

    /**
     * Helper function to construct the TimelineView Context with default values set.
     *
     * @param name
     * @param displayName
     * @param appId
     * @param module
     * @return
     */
    private TimelineViewContext getDefaultTimelineViewContext(String name, String displayName, Long appId, FacilioModule module) {
        TimelineViewContext context = new TimelineViewContext();
        context.setName(name); // link name
        context.setDisplayName(displayName);
        context.setHidden(true);
        context.setAppId(appId);
        context.setModuleName(module.getName());
        context.setModuleId(module.getModuleId());

        context.setAllowRescheduling(false);
        context.setAllowGroupAssignment(true);
        context.setAllowReAssignment(true);
        context.setAllowPastAssignment(true);
        context.setViewType(FacilioView.ViewType.TIMELINE.getIntVal());
        context.setType(FacilioView.ViewType.TIMELINE);
        context.setDefault(false);
        context.setExcludeModuleCriteria(true);
        return context;
    }

    /**
     * Helper method to add color customization based on Ticket Priority.
     *
     * @param fieldsMap
     * @param timelineViewContext
     * @throws Exception
     */
    private void addRecordCustomizationWithTicketPriority(Map<String, FacilioField> fieldsMap, TimelineViewContext timelineViewContext) throws Exception {
        List<RecordCustomizationValuesContext> customizations = new ArrayList<>();

        FacilioChain statusListChain = FacilioChainFactory.getTicketPriorityListChain();
        statusListChain.execute();
        List<TicketPriorityContext> priorities = (List<TicketPriorityContext>) statusListChain.getContext().get(FacilioConstants.ContextNames.TICKET_PRIORITY_LIST);
        for(TicketPriorityContext priorityField : priorities) {
            RecordCustomizationValuesContext customizationValue = new RecordCustomizationValuesContext();
            customizationValue.setCustomization("{\"eventColor\":\""+priorityField.getColour()+"\"}");
            customizationValue.setFieldValue(String.valueOf(priorityField.getId()));
            customizations.add(customizationValue);
        }
        RecordCustomizationContext customization = new RecordCustomizationContext();
        customization.setCustomizationType(RecordCustomizationContext.CustomizationType.FIELD.getIntVal());
        customization.setCustomizationFieldId(fieldsMap.get("priority").getFieldId());
        customization.setDefaultCustomization("{\"eventColor\":\"#4d95ff\"}");
        customization.setValues(customizations);
        timelineViewContext.setRecordCustomization(customization);
    }
}
