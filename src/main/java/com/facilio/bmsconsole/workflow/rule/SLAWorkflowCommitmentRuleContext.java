package com.facilio.bmsconsole.workflow.rule;

import com.amazonaws.regions.Regions;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.instant.jobs.AddSLAEscalationJob;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
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

                Long timeValue = (Long) FieldUtil.getValue(moduleRecord, baseField);
                if (timeValue == null) {
                    continue;
                }

                long slaDurationInSeconds = getSlaEntityDuration(slaEntityDuration, moduleRecord);
                if (slaDurationInSeconds < 0) {
                    continue;
                }

                if (slaEntityDuration.getTypeOrDefault() == 1) {
                    timeValue += slaDurationInSeconds * 1000;
                }
                else {
                    // retrieve from business hour
                    BusinessHoursList businessHours = BusinessHoursAPI.getCorrespondingBusinessHours(moduleRecord.getSiteId());
                    timeValue = businessHours.getNextPossibleTime(timeValue, (int) slaDurationInSeconds);
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

                if (DBConf.getInstance().getCurrentOrgId() == 592) {
                    try {
                        LOGGER.info("Current transaction while updating due (target) date => " + FacilioTransactionManager.INSTANCE.getTransactionManager().getTransaction());
                    }
                    catch (Exception e) {

                    }
                }
                LOGGER.info("Updating Date field '"+dueField.getName()+"' to "+FieldUtil.getAsProperties(moduleRecord)+"\nQuery => "+update);
                
            }
        }

        super.executeTrueActions(record, context, placeHolders);
    }

    private long getSlaEntityDuration(SLAEntityDuration slaEntityDuration, ModuleBaseWithCustomFields moduleRecord) throws Exception {
        if (StringUtils.isNotEmpty(slaEntityDuration.getDurationPlaceHolder())) {
            Map<String, Object> placeHolders = WorkflowRuleAPI.getRecordPlaceHolders(getModuleName(), moduleRecord, null);
            String replace = StringSubstitutor.replace(slaEntityDuration.getDurationPlaceHolder(), placeHolders);
            try {
                return Integer.parseInt(replace);
            } catch (NumberFormatException ex) {
                return -1;
            }
        }
        return slaEntityDuration.getAddDuration();
    }

    public static void addEscalationJobs(Long parentRuleId, List<SLAWorkflowEscalationContext> escalations,
                                         FacilioModule module, FacilioField dueField, Criteria criteria,
                                         ModuleBaseWithCustomFields moduleRecord, SLAEntityContext slaEntity) throws Exception {
        if (FacilioProperties.getRegion().equals(Regions.US_WEST_2.getName()) && DBConf.getInstance().getCurrentOrgId() == 592 && !isUserRequestThread()) {
            LOGGER.info("Adding Escalation job directly");
            AddSLAEscalationJob.addSLAEscalationJobs(parentRuleId, escalations, module, dueField, criteria, moduleRecord, slaEntity);
        }
        else {
            FacilioContext instantJobContext = new FacilioContext();
            instantJobContext.put(FacilioConstants.ContextNames.PARENT_RULE_ID, parentRuleId);
            instantJobContext.put(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST, escalations);
            instantJobContext.put(FacilioConstants.ContextNames.MODULE, module);
            instantJobContext.put(FacilioConstants.ContextNames.DATE_FIELD, dueField);
            instantJobContext.put(FacilioConstants.ContextNames.CRITERIA, criteria);
            instantJobContext.put(FacilioConstants.ContextNames.MODULE_DATA, moduleRecord);
            instantJobContext.put(FacilioConstants.ContextNames.SLA_ENTITY, slaEntity);
            FacilioTimer.scheduleInstantJobInPostTransaction("AddSLAEscalation", instantJobContext);
        }
    }

    private void addSLATriggeredActivity(Context context, ModuleBaseWithCustomFields record, SLAPolicyContext slaPolicy, Long oldDate, Long newDate, SLAEntityContext slaEntity) throws Exception {
        JSONObject infoJson = new JSONObject();
        infoJson.put("name", slaPolicy.getName());
        infoJson.put("slaEntityName", slaEntity.getName());
        infoJson.put("oldDate", oldDate);
        infoJson.put("newDate", newDate);

        User user = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
        user = user == null ? AccountUtil.getCurrentUser() : user;
        CommonCommandUtil.addActivityToContext(record.getId(), System.currentTimeMillis(), WorkOrderActivityType.SLA_ACTIVATED, infoJson,
                (FacilioContext) context,user);
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

        @Deprecated
        private long addDuration = -1;
        @Deprecated
        public long getAddDuration() {
            return addDuration;
        }
        @Deprecated
        public void setAddDuration(long addDuration) {
            this.addDuration = addDuration;
        }

        private String durationPlaceHolder;
        public String getDurationPlaceHolder() {
            if (StringUtils.isNotEmpty(durationPlaceHolder)) {
                return durationPlaceHolder;
            }
            if (addDuration > 0) {
                return String.valueOf(addDuration);
            }
            return null;
        }
        public void setDurationPlaceHolder(String durationPlaceHolder) {
            this.durationPlaceHolder = durationPlaceHolder;
        }

        // 1 is calendar hours, 2 is business hours
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

    private static boolean isUserRequestThread() {
        try {
            HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
            return request != null;
        }
        catch (Exception e) {
            return false;
        }
    }
}
