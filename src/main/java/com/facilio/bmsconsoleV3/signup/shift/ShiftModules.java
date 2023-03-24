package com.facilio.bmsconsoleV3.signup.shift;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class ShiftModules extends SignUpData {
    public final int CASCADE_DELETE = 2;

    @Override
    public void addData() throws Exception {

        createShiftModule();
        createShiftChildModules();
        createDefaultStateFlow();
        createDefaultShit();
    }

    public void createDefaultShit() throws Exception {
        FacilioModule shiftMod = Constants.getModBean().getModule(FacilioConstants.Shift.SHIFT);
        long orgID = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        Shift defaultShift = new Shift();
        defaultShift.setOrgId(orgID);
        defaultShift.setName("Default");
        defaultShift.setStartTime(32400L);
        defaultShift.setEndTime(64800L);
        defaultShift.setDefaultShift(true);
        defaultShift.setIsActive(true);
        defaultShift.setWeekend("{\"1\":[6,7],\"2\":[6,7],\"3\":[6,7],\"4\":[6,7],\"5\":[6,7]}");
        defaultShift.setColorCode("#EEF4F4");
        defaultShift.setModuleState(TicketAPI.getStatus(shiftMod, "active"));

        List<WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRules(shiftMod.getModuleId());
        StateFlowRuleContext stateFlowRule = filterStateFlowRule(rules);
        defaultShift.setStateFlowId(stateFlowRule.getId());

        Map<String, Object> props = FieldUtil.getAsProperties(defaultShift);
        props.put(FacilioConstants.Shift.CREATING_DEFAULT_SHIFT, true);
        V3Util.createRecord(shiftMod, props);
    }

    public StateFlowRuleContext filterStateFlowRule(List<WorkflowRuleContext> rules) {
        return (StateFlowRuleContext) rules.stream()
                .filter(r -> r instanceof StateFlowRuleContext)
                .collect(Collectors.toList())
                .get(0);
    }

    public void createShiftChildModules() throws Exception {

        FacilioModule shiftMod = Constants.getModBean().getModule(FacilioConstants.Shift.SHIFT);

        List<FacilioModule> childModules = new ArrayList<>();
        childModules.add(composeShiftActivityModule());
        childModules.add(composeShiftRichTextModule());
        FacilioChain chain = TransactionChainFactory.addSystemModuleChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, childModules);
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, shiftMod.getName());
        chain.getContext().put(FacilioConstants.ContextNames.DELETE_TYPE, CASCADE_DELETE);
        chain.execute();

        FacilioModule shiftRichTextMod = Constants.getModBean().getModule(FacilioConstants.Shift.SHIFT_RICH_TEXT);
        persistRichTextDescriptionFieldForShift(shiftRichTextMod, shiftMod);
    }

    public void createShiftModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        modules.add(composeShiftModule());
        FacilioChain chain = TransactionChainFactory.addSystemModuleChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        chain.execute();
    }

    /**
     * creates description field for Shift
     **/
    public void persistRichTextDescriptionFieldForShift(FacilioModule shiftRichTextMod, FacilioModule shiftMod) throws Exception {

        LargeTextField richTextField = FieldFactory.getDefaultField("description", "Description", null, FieldType.LARGE_TEXT);
        richTextField.setModule(shiftMod);
        richTextField.setRelModuleId(shiftRichTextMod.getModuleId());
        Constants.getModBean().addField(richTextField);
    }


    public void createDefaultStateFlow() throws Exception {
        FacilioModule shiftMod = Constants.getModBean().getModule(FacilioConstants.Shift.SHIFT);

        FacilioStatus.StatusType openType = FacilioStatus.StatusType.OPEN;
        FacilioStatus activeStatus =
                SignupUtil.createUntimedFacilioStatus(shiftMod, "active", "Active", openType);

        FacilioStatus.StatusType closedType = FacilioStatus.StatusType.CLOSED;
        FacilioStatus inActiveStatus =
                SignupUtil.createUntimedFacilioStatus(shiftMod, "inactive", "Inactive", closedType);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(shiftMod.getModuleId());
        stateFlowRuleContext.setModule(shiftMod);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

        AbstractStateTransitionRuleContext.TransitionType normalTransition =
                AbstractStateTransitionRuleContext.TransitionType.NORMAL;

        ActionContext deactivateAction = new ActionContext();
        deactivateAction.setActionType(ActionType.FIELD_CHANGE);
        deactivateAction.setTemplateJson(composeTemplateToDeActivate());
        SignupUtil.createStateflowTransition(shiftMod, stateFlowRuleContext, "Deactivate", activeStatus, inActiveStatus, normalTransition, Collections.singletonList(deactivateAction));

        ActionContext activateAction = new ActionContext();
        activateAction.setActionType(ActionType.FIELD_CHANGE);
        activateAction.setTemplateJson(composeTemplateToActivate());
        SignupUtil.createStateflowTransition(shiftMod, stateFlowRuleContext, "Activate", inActiveStatus, activeStatus, normalTransition, Collections.singletonList(activateAction));
    }

    private static JSONObject composeTemplateToActivate() {
        JSONObject obj = new JSONObject();
        obj.put("fieldMatcher", getActionToSetIsActive(true));
        return obj;
    }

    private static JSONObject composeTemplateToDeActivate() {
        JSONObject obj = new JSONObject();
        obj.put("fieldMatcher", getActionToSetIsActive(false));
        return obj;
    }

    private static Object getActionToSetIsActive(boolean value) {
        JSONObject obj = new JSONObject();
        obj.put("field", "isActive");
        obj.put("value", Boolean.toString(value));
        ArrayList list = new ArrayList();
        list.add(obj);
        return list;
    }

    public FacilioModule composeShiftModule() throws Exception {
        FacilioModule mod = new FacilioModule(
                FacilioConstants.Shift.SHIFT,
                "Shift",
                "Shift",
                FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();
        FacilioField nameField = FieldFactory.getStringField("name", "NAME", mod);
        nameField.setMainField(true);
        fields.add(nameField);
        fields.add(FieldFactory.getNumberField("startTime", "START_TIME", mod));
        fields.add(FieldFactory.getNumberField("endTime", "END_TIME", mod));
        fields.add(FieldFactory.getBooleanField("defaultShift", "IS_DEFAULT", mod));
        fields.add(FieldFactory.getBooleanField("isActive", "IS_ACTIVE", mod));
        fields.add(FieldFactory.getStringField("weekend", "WEEKEND", mod));
        fields.add(FieldFactory.getStringField("colorCode", "COLOR_CODE", mod));

        FacilioModule ticketStatusMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.TICKET_STATUS);
        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState",
                "Module State", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setLookupModule(ticketStatusMod);
        fields.add(moduleStateField);

        fields.add(FieldFactory.getNumberField("stateFlowId", "STATE_FLOW_ID", mod));
        for (FacilioField f : fields) {
            f.setDefault(true);
        }

        mod.setFields(fields);
        return mod;
    }

    public FacilioModule composeShiftActivityModule() {
        FacilioModule mod = new FacilioModule(
                FacilioConstants.Shift.SHIFT_ACTIVITY,
                "Shift Activity",
                "Shift_Activity",
                FacilioModule.ModuleType.ACTIVITY);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getNumberField("parentId", "PARENT_ID", mod));
        fields.add(FieldFactory.getDateField("ttime", "TTIME", mod));
        fields.add(FieldFactory.getNumberField("type", "ACTIVITY_TYPE", mod));
        fields.add(FieldFactory.getStringField("infoJsonStr", "INFO", mod));

        long orgID = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        LookupField doneByField = SignupUtil.getLookupField(mod, null,
                "doneBy", "Done By", "DONE_BY_ID", "users",
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgID);
        fields.add(doneByField);

        mod.setFields(fields);
        return mod;
    }

    public FacilioModule composeShiftRichTextModule() throws Exception {
        FacilioModule shiftMod = Constants.getModBean().getModule(FacilioConstants.Shift.SHIFT);

        FacilioModule mod = new FacilioModule(FacilioConstants.Shift.SHIFT_RICH_TEXT,
                "Shift Rich Text",
                "Shift_Rich_Text",
                FacilioModule.ModuleType.SUB_ENTITY
        );
        List<FacilioField> fields = new ArrayList<>();
        LookupField parentField = FieldFactory.getDefaultField("parentId", "Parent ID", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(shiftMod);
        fields.add(parentField);
        fields.add(FieldFactory.getDefaultField("fileId", "File ID", "FILE_ID", FieldType.NUMBER));
        mod.setFields(fields);
        return mod;
    }

}
