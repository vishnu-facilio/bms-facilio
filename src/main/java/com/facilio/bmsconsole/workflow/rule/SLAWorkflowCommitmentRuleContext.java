package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SLAWorkflowCommitmentRuleContext extends WorkflowRuleContext {

    private static final Logger LOGGER = LogManager.getLogger(SLAWorkflowCommitmentRuleContext.class.getName());

    private List<SLAEntityDuration> slaEntities;
    public List<SLAEntityDuration> getSlaEntities() {
        return slaEntities;
    }
    public void setSlaEntities(List<SLAEntityDuration> slaEntities) {
        this.slaEntities = slaEntities;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

        if (moduleRecord.getSlaPolicyId() <= 0) {
            return false;
        }

        log("commitment rule id: " + getId() + "; policy id: " + getParentRuleId() + "; record: " + moduleRecord.getSlaPolicyId());
        // If the record is matched in another sla policy, execute false
        if (moduleRecord.getSlaPolicyId() > 0 && moduleRecord.getSlaPolicyId() != getParentRuleId()) {
            return false;
        }

        return super.evaluateMisc(moduleName, record, placeHolders, context);
    }

    private void log(Object log) {
//        if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 324l) {
            LOGGER.debug(log);
//        }
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (CollectionUtils.isEmpty(slaEntities)) {
            slaEntities = SLAWorkflowAPI.getSLAEntitiesForCommitment(getId());
        }

        log("Sla entities: " + slaEntities);
        if (CollectionUtils.isNotEmpty(slaEntities)) {
            SLAPolicyContext slaPolicy = (SLAPolicyContext) WorkflowRuleAPI.getWorkflowRule(getParentRuleId());
            List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalations = slaPolicy.getEscalations();
            Map<Long, SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationMap = null;
            if (CollectionUtils.isNotEmpty(escalations)) {
                escalationMap = new HashMap<>();
                for (SLAPolicyContext.SLAPolicyEntityEscalationContext escalation : escalations) {
                    escalationMap.put(escalation.getSlaEntityId(), escalation);
                }
            }

            FacilioModule module = modBean.getModule(getModuleId());
            for (SLAEntityDuration slaEntityDuration : slaEntities) {
                SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(slaEntityDuration.getSlaEntityId());

                FacilioField baseField = modBean.getField(slaEntity.getBaseFieldId());
                FacilioField dueField = modBean.getField(slaEntity.getDueFieldId());

                log("base field: " + baseField.getName() + "; due field: " + dueField.getName());
                Long timeValue = (Long) FieldUtil.getValue(moduleRecord, baseField);
                log("Time value: " + timeValue);
                if (timeValue == null) {
                    continue;
                }
                if (slaEntityDuration.getTypeOrDefault() == 1) {
                    timeValue += slaEntityDuration.getAddDuration() * 1000;
                }
                else {
                    // retrieve from business hour
                    BusinessHoursList businessHours = BusinessHoursAPI.getCorrespondingBusinessHours(moduleRecord.getSiteId());
                    timeValue = businessHours.getNextPossibleTime(timeValue, (int) slaEntityDuration.getAddDuration());
                }
                log("Updated Time value: " + timeValue);

                Long oldTime = (Long) FieldUtil.getValue(moduleRecord, dueField);
                if (oldTime != null && timeValue != null) {
                    if ((oldTime / 1000) == (timeValue / 1000)) { // checking in second level
                        // skip updating
                        log("skip updated: " + oldTime + "; timevalue: " + timeValue);
                        continue;
                    }
                }
                addSLATriggeredActivity(context, moduleRecord, slaPolicy, oldTime, timeValue, slaEntity);
                FieldUtil.setValue(moduleRecord, dueField, timeValue);

                UpdateRecordBuilder<ModuleBaseWithCustomFields> update = new UpdateRecordBuilder<>()
                        .module(module)
                        .fields(Collections.singletonList(dueField))
                        .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), module));
                update.update(moduleRecord);

                if (MapUtils.isNotEmpty(escalationMap)) {
                    SLAPolicyContext.SLAPolicyEntityEscalationContext slaPolicyEntityEscalationContext = escalationMap.get(slaEntityDuration.getSlaEntityId());
                    if (slaPolicyEntityEscalationContext != null && CollectionUtils.isNotEmpty(slaPolicyEntityEscalationContext.getLevels())) {
                        slaPolicyEntityEscalationContext.setLevels(SLAWorkflowAPI.getEscalations(slaPolicy.getId(), slaPolicyEntityEscalationContext.getSlaEntityId()));
                        addEscalationJobs(getParentRuleId(), slaPolicyEntityEscalationContext.getLevels(), module, dueField, slaEntity.getCriteria(), moduleRecord, slaEntity);
                    }
                }
            }
        }

        super.executeTrueActions(record, context, placeHolders);
    }

    public static void addEscalationJobs(Long parentRuleId, List<SLAWorkflowEscalationContext> escalations,
                                         FacilioModule module, FacilioField dueField, Criteria criteria,
                                         ModuleBaseWithCustomFields moduleRecord, SLAEntityContext slaEntity) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.PARENT_RULE_ID, parentRuleId);
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST, escalations);
        context.put(FacilioConstants.ContextNames.MODULE, module);
        context.put(FacilioConstants.ContextNames.DATE_FIELD, dueField);
        context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
        context.put(FacilioConstants.ContextNames.MODULE_DATA, moduleRecord);
        context.put(FacilioConstants.ContextNames.SLA_ENTITY, slaEntity);
        FacilioTimer.scheduleInstantJob("AddSLAEscalation", context);
    }

    private void addSLATriggeredActivity(Context context, ModuleBaseWithCustomFields record, SLAPolicyContext slaPolicy, Long oldDate, Long newDate, SLAEntityContext slaEntity) throws Exception {
        JSONObject infoJson = new JSONObject();
        infoJson.put("name", slaPolicy.getName());
        infoJson.put("slaEntityName", slaEntity.getName());
        infoJson.put("oldDate", oldDate);
        infoJson.put("newDate", newDate);

        CommonCommandUtil.addActivityToContext(record.getId(), System.currentTimeMillis(), WorkOrderActivityType.SLA_ACTIVATED, infoJson,
                (FacilioContext) context);
    }

    public static class SLAEntityDuration {
        private long slaEntityId = -1;
        public long getSlaEntityId() {
            return slaEntityId;
        }
        public void setSlaEntityId(long slaEntityId) {
            this.slaEntityId = slaEntityId;
        }

        private long slaCommitmentId = -1;
        public long getSlaCommitmentId() {
            return slaCommitmentId;
        }
        public void setSlaCommitmentId(long slaCommitmentId) {
            this.slaCommitmentId = slaCommitmentId;
        }

        private long addDuration = -1;
        public long getAddDuration() {
            return addDuration;
        }
        public void setAddDuration(long addDuration) {
            this.addDuration = addDuration;
        }

        private int type = -1;
        public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        public int getTypeOrDefault() {
            if (type == -1) {
                return 1;
            }
            return type;
        }
    }
}
