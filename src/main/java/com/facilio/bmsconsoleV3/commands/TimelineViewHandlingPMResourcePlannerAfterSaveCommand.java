package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

/**
 * TimelineViewHandlingPMResourcePlannerAfterSaveCommand
 * - Updates the Group Criteria of,
 * - Resource Timeline View (with Resource IDs)
 * - Staff Timeline View ( with User's ORG_USERIDs)
 * for a single planner with ID `plannerId`.
 */
public class TimelineViewHandlingPMResourcePlannerAfterSaveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);

        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordMap(context).get(Constants.getModuleName(context));
        StringJoiner resourceIds = new StringJoiner(",");
        Set<String> userIds = new HashSet<>();
        long plannerId = -1L;

        if (moduleBaseWithCustomFields == null && !moduleBaseWithCustomFields.isEmpty()) {
            return false;
        }

        PMResourcePlanner pmresourcePlanner = (PMResourcePlanner) moduleBaseWithCustomFields.get(0);
        plannerId = pmresourcePlanner.getPlanner().getId();

        List<PMResourcePlanner> pmResourcePlanners = getPMResourcePlanner(plannerId);

        for (PMResourcePlanner pmResourcePlanner : pmResourcePlanners) {
            resourceIds.add(String.valueOf(pmResourcePlanner.getResource().getId()));
            User user = pmResourcePlanner.getAssignedTo();
            if(user != null) {
                userIds.add(String.valueOf(user.getOuid()));
            }
        }

        PMPlanner pmPlanner = V3RecordAPI.getRecord("pmPlanner", plannerId);

        // Resource Timeline View
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        TimelineViewContext resourceTimelineViewContext = (TimelineViewContext) ViewAPI.getView(pmPlanner.getResourceTimelineViewId());
        if (resourceIds.length() > 0) {
            Criteria criteria = new Criteria();
            /* TODO: Seems to be `resourceIds.toString()` won't scale for huge number of resource IDs in IN() statement */
            criteria.addAndCondition(CriteriaAPI.getIdCondition(resourceIds.toString(), resourceModule));
            updateCriteriaForTimelineView(resourceTimelineViewContext, criteria, pmPlanner.getResourceTimelineViewId());
        } else {
            updateCriteriaForTimelineView(resourceTimelineViewContext, null, pmPlanner.getResourceTimelineViewId());
        }

        // Staff Timeline View
        //FacilioModule usersModule = modBean.getModule(FacilioConstants.ContextNames.USERS);
        TimelineViewContext staffTimelineViewContext = (TimelineViewContext) ViewAPI.getView(pmPlanner.getStaffTimelineViewId());
        if (!userIds.isEmpty()) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI
                    .getCondition("ORG_USERID", "ouid", String.join(",", userIds), NumberOperators.EQUALS));
            updateCriteriaForTimelineView(staffTimelineViewContext, criteria, pmPlanner.getStaffTimelineViewId());
        } else {
            updateCriteriaForTimelineView(staffTimelineViewContext, null, pmPlanner.getStaffTimelineViewId());
        }

        return false;
    }

    /**
     * Helper function to,
     * - Update the {@param timelineView} with the new {@param criteria} and
     * - delete the existing criteria.
     *
     * @param timelineView
     * @param criteria
     * @param viewId
     * @throws Exception
     */
    private void updateCriteriaForTimelineView(TimelineViewContext timelineView, Criteria criteria,
                                               Long viewId) throws Exception {
        long criteriaIdToBeDeleted = timelineView.getGroupCriteriaId();

        timelineView.setGroupCriteria(criteria);
        ViewAPI.updateView(viewId, timelineView);

        if (criteriaIdToBeDeleted > 0) {
            CriteriaAPI.deleteCriteria(criteriaIdToBeDeleted);
        }
    }

    /**
     * Helper function to get PMResourcePlanner(s) of the planner with ID {@param pmPlannerId}
     * TODO: This function might require few optimization while having huge number of resources in the planner.
     *
     * @param pmPlannerId
     * @return
     * @throws Exception
     */
    private List<PMResourcePlanner> getPMResourcePlanner(Long pmPlannerId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("pmResourcePlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmResourcePlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);

        SelectRecordsBuilder<PMResourcePlanner> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(pmPlannerFields)
                .beanClass(PMResourcePlanner.class)
                .module(pmPlannerModule)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("planner"), pmPlannerId + "", NumberOperators.EQUALS));

        // add supplements to be fetched
        List<SupplementRecord> supplementList = new ArrayList<>();
        supplementList.add((LookupField) fieldMap.get("resource"));
        selectRecordsBuilder.fetchSupplements(supplementList);

        return selectRecordsBuilder.get();
    }
}
