package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractStateTransitionRuleContext extends ApproverWorkflowRuleContext implements FormInterface {
    private static final long serialVersionUID = 1L;

    private long fromStateId = -1;
    public long getFromStateId() {
        return fromStateId;
    }
    public void setFromStateId(long fromStateId) {
        this.fromStateId = fromStateId;
    }

    private long toStateId = -1;
    public long getToStateId() {
        return toStateId;
    }
    public void setToStateId(long toStateId) {
        this.toStateId = toStateId;
    }

    private long stateFlowId = -1;
    public long getStateFlowId() {
        return stateFlowId;
    }
    public void setStateFlowId(long stateFlowId) {
        this.stateFlowId = stateFlowId;
    }

    private FacilioForm form;
    public FacilioForm getForm() {
        return form;
    }
    public void setForm(FacilioForm form) {
        this.form = form;
    }

    private long formId = -1; // check whether it is good to have
    public long getFormId() {
        return formId;
    }
    public void setFormId(long formId) {
        this.formId = formId;
    }

    private boolean shouldFormInterfaceApply = true;
    public void setShouldFormInterfaceApply(boolean shouldFormInterfaceApply) {
        this.shouldFormInterfaceApply = shouldFormInterfaceApply;
    }
    @Override
    public boolean shouldFormInterfaceApply() {
        return shouldFormInterfaceApply;
    }

    private String formModuleName;
    public String getFormModuleName() {
        return formModuleName;
    }
    public void setFormModuleName(String formModuleName) {
        this.formModuleName = formModuleName;
    }

    private DialogType dialogType;
    public int getDialogType() {
        if (dialogType != null) {
            return dialogType.getIndex();
        }
        return -1;
    }
    public void setDialogType(int dialogType) {
        this.dialogType = DialogType.valueOf(dialogType);
    }
    public DialogType getDialogTypeEnum() {
        return dialogType;
    }
    public void setDialogType(DialogType dialogType) {
        this.dialogType = dialogType;
    }

    private int buttonType = -1;
    public int getButtonType() {
        return buttonType;
    }
    public void setButtonType(int buttonType) {
        this.buttonType = buttonType;
    }

    private TransitionType type;
    public int getType() {
        if (type != null) {
            return type.getValue();
        }
        return -1;
    }
    public TransitionType getTypeEnum() {
        return type;
    }
    public void setType(TransitionType type) {
        this.type = type;
    }
    public void setType(int type) {
        this.type = TransitionType.valueOf(type);
    }

    private int scheduleTime = -1;
    public int getScheduleTime() {
        return scheduleTime;
    }
    public void setScheduleTime(int scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    private Boolean shouldExecuteFromPermalink;
    public Boolean getShouldExecuteFromPermalink() {
        return shouldExecuteFromPermalink;
    }
    public void setShouldExecuteFromPermalink(Boolean shouldExecuteFromPermalink) {
        this.shouldExecuteFromPermalink = shouldExecuteFromPermalink;
    }
    public Boolean isShouldExecuteFromPermalink() {
        if (shouldExecuteFromPermalink == null) {
            return false;
        }
        return shouldExecuteFromPermalink;
    }

    private Boolean parallelTransition;
    public Boolean isParallelTransition() {
        if (parallelTransition == null) {
            return false;
        }
        return parallelTransition;
    }
    public Boolean getParallelTransition() {
        return parallelTransition;
    }
    public void setParallelTransition(Boolean parallelTransition) {
        this.parallelTransition = parallelTransition;
    }

    // temporary fix
    private Boolean showInVendorPortal;
    public Boolean getShowInVendorPortal() {
        return showInVendorPortal;
    }
    public void setShowInVendorPortal(Boolean showInVendorPortal) {
        this.showInVendorPortal = showInVendorPortal;
    }
    public Boolean isShowInVendorPortal() {
        if (this.showInVendorPortal == null) {
            return false;
        }
        return showInVendorPortal;
    }

    private Boolean showInTenantPortal;
    public Boolean getShowInTenantPortal() {
        return showInTenantPortal;
    }
    public void setShowInTenantPortal(Boolean showInTenantPortal) {
        this.showInTenantPortal = showInTenantPortal;
    }
    public Boolean isShowInTenantPortal() {
        if (this.showInTenantPortal == null) {
            return false;
        }
        return showInTenantPortal;
    }

    private Boolean showInOccupantPortal;
    public Boolean getShowInOccupantPortal() {
        return showInOccupantPortal;
    }
    public void setShowInOccupantPortal(Boolean showInOccupantPortal) {
        this.showInOccupantPortal = showInOccupantPortal;
    }
    public Boolean isShowInOccupantPortal() {
        if (this.showInOccupantPortal == null) {
            return false;
        }
        return showInOccupantPortal;
    }
    private Boolean showInClientPortal;
    public Boolean getShowInClientPortal() {
        return showInClientPortal;
    }
    public void setShowInClientPortal(Boolean showInClientPortal) {
        this.showInClientPortal = showInClientPortal;
    }
    public Boolean isShowInClientPortal() {
        if (this.showInClientPortal == null) {
            return false;
        }
        return showInClientPortal;
    }


    protected abstract void executeTrue(Object record, Context context, Map<String, Object> placeHolders) throws Exception;

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        boolean shouldExecuteTrueActions = super.validateApproversForTrueAction(record);

        if (shouldExecuteTrueActions) {
            boolean isValid = true;

            if (type == TransitionType.NORMAL) {
                isValid = super.validationCheck(moduleRecord, context, placeHolders);
            }

            if (isValid) {
                boolean shouldExecute = true;
                if (isParallelTransition()) {
                    shouldExecute = addCurrentAndCheckParallelTransitionStatus(moduleRecord.getId());
                }

                if (shouldExecute) {
                    if (isParallelTransition()) {
                        clearStatusTable(moduleRecord.getId());
                    }
                    executeTrue(record, context, placeHolders);
                    super.executeTrueActions(record, context, placeHolders);
                }
            }
        }
    }

    private void clearStatusTable(long parentId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getParallelStateTransitionsStatusModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
        builder.delete();
    }

    private List<Long> getAlreadyDoneParallelExecution(long parentId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getParallelStateTransitionsStatusModule().getTableName())
                .select(FieldFactory.getParallelStateTransitionsStatusFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("FROM_STATE_ID", "fromStateId", String.valueOf(getFromStateId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TO_STATE_ID", "toStateId", String.valueOf(getToStateId()), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            List<Long> completedIds = new ArrayList<>();
            for (Map<String, Object> m : maps) {
                completedIds.add((Long) m.get("stateTransitionId"));
            }

            return completedIds;
        }
        return null;
    }

    private boolean addCurrentAndCheckParallelTransitionStatus(long parentId) throws Exception {
        List<Map<String, Long>> stateIds = new ArrayList<>();
        Map<String, Long> map = new HashMap<>();
        map.put("fromStateId", getFromStateId());
        map.put("toStateId", getToStateId());
        map.put("stateFlowId", getStateFlowId());
        stateIds.add(map);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PARALLEL_TRANSITION", "parallelTransition", String.valueOf(true), BooleanOperators.IS));
        List<WorkflowRuleContext> stateTransitions = StateFlowRulesAPI.getStateTransitions(stateIds, criteria, TransitionType.NORMAL);
        List<Long> parallelTransitionIds = null;
        if (CollectionUtils.isNotEmpty(stateTransitions)) {
            parallelTransitionIds = stateTransitions.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(parallelTransitionIds)) {
            return true;
        }

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getParallelStateTransitionsStatusModule().getTableName())
                .fields(FieldFactory.getParallelStateTransitionsStatusFields());
        Map<String, Object> value = new HashMap<>();
        value.put("parentId", parentId);
        value.put("fromStateId", getFromStateId());
        value.put("toStateId", getToStateId());
        value.put("stateTransitionId", getId());
        insertRecordBuilder.insert(value);

        List<Long> completedIds = getAlreadyDoneParallelExecution(parentId);
        if (CollectionUtils.isNotEmpty(completedIds)) {
            return completedIds.containsAll(parallelTransitionIds);
        }

        return false;
    }

    protected final boolean evaluateStateFlow(long stateFlowId, FacilioStatus moduleState, String moduleName, Object record,
                                              Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        Boolean shouldCheckOnlyConditioned = (Boolean) context.get(FacilioConstants.ContextNames.STATE_TRANSITION_ONLY_CONDITIONED_CHECK);
        if (shouldCheckOnlyConditioned == null) {
            shouldCheckOnlyConditioned = false;
        }

        if (shouldCheckOnlyConditioned && getTypeEnum() != TransitionType.CONDITIONED) {
            return false;
        }

        // this is old records
        if (moduleState == null || stateFlowId <= 0) {
            return false;
        }

        if (moduleState != null && stateFlowId > 0) {
            // don't execute if it different stateflow
            if (stateFlowId != getStateFlowId()) {
                return false;
            }

            // don't execute if fromStateId is different from record module state
            if (getFromStateId() != moduleState.getId()) {
                return false;
            }
        }

        if (isParallelTransition()) {
            ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
            List<Long> alreadyDoneParallelExecution = getAlreadyDoneParallelExecution(moduleRecord.getId());
            if (CollectionUtils.isNotEmpty(alreadyDoneParallelExecution)) {
                if (alreadyDoneParallelExecution.contains(getId())) {
                    // return false, if this stateflow is already done
                    return false;
                }
            }
        }

        Boolean getPermalinkTransition = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.STATE_TRANSITION_GET_PERMALINK_ONLY, false);
        boolean isFromPermalink = AccountUtil.getAuthMethod() == AccountUtil.AuthMethod.PERMALINK;
        if (getPermalinkTransition == null) {
            getPermalinkTransition = false;
        }
        if (getPermalinkTransition || isFromPermalink) {    // skip only user check part, for the transitions that comes from permalink..
            return isShouldExecuteFromPermalink();
        }

        return super.evaluateMisc(moduleName, record, placeHolders, context);
    }

    public enum DialogType implements FacilioIntEnum {
        MODULE ("Module"),
        SUB_MODULE ("Sub Module")
        ;

        private String name;

        DialogType(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public String getValue() {
            return name;
        }

        public static DialogType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public enum TransitionType {
        NORMAL,
        SCHEDULED,
        CONDITIONED,
        FIELD_SCHEDULED,
        ;

        public int getValue() {
            return ordinal() + 1;
        }

        public static TransitionType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
