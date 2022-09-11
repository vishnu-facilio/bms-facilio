package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;

import java.util.*;

public class BuildingModule extends BaseModuleConfig {

    public BuildingModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.BUILDING);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);

        FacilioStatus activeStatus = new FacilioStatus();
        activeStatus.setStatus("active");
        activeStatus.setDisplayName("Active");
        activeStatus.setTypeCode(1);
//        TicketAPI.addStatus(activeStatus, buildingModule);

        FacilioStatus inactiveStatus = new FacilioStatus();
        inactiveStatus.setStatus("inactive");
        inactiveStatus.setDisplayName("In Active");
        inactiveStatus.setTypeCode(2);
//        TicketAPI.addStatus(inactiveStatus, buildingModule);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(buildingModule.getModuleId());
        stateFlowRuleContext.setModule(buildingModule);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
//        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

        StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
        activeToInactive.setName("Mark as Inactive");
        activeToInactive.setModule(buildingModule);
        activeToInactive.setModuleId(buildingModule.getModuleId());
        activeToInactive.setActivityType(EventType.STATE_TRANSITION);
        activeToInactive.setExecutionOrder(1);
        activeToInactive.setButtonType(1);
        activeToInactive.setFromStateId(activeStatus.getId());
        activeToInactive.setToStateId(inactiveStatus.getId());
        activeToInactive.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        activeToInactive.setType(AbstractStateTransitionRuleContext.TransitionType.NORMAL);
        activeToInactive.setStateFlowId(stateFlowRuleContext.getId());
//        WorkflowRuleAPI.addWorkflowRule(activeToInactive);

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> building = new ArrayList<FacilioView>();
        building.add(getAllSpaces().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BUILDING);
        groupDetails.put("views", building);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSpaces() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Spaces");
        allView.setSortFields(sortFields);

        return allView;
    }
}
