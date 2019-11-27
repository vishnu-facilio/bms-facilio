package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

public class SLAWorkflowRuleContext extends WorkflowRuleContext {

    public SLAWorkflowRuleContext() {
        setRuleType(RuleType.SLA_WORKFLOW_RULE);
    }

    @Override
    @JsonInclude
    public int getRuleType() {
        return super.getRuleType();
    }

    private long baseFieldId = -1;
    public long getBaseFieldId() {
        return baseFieldId;
    }
    public void setBaseFieldId(long baseFieldId) {
        this.baseFieldId = baseFieldId;
    }

    private long dueFieldId = -1;
    public long getDueFieldId() {
        return dueFieldId;
    }
    public void setDueFieldId(long dueFieldId) {
        this.dueFieldId = dueFieldId;
    }

    private long compareFieldId = -1;
    public long getCompareFieldId() {
        return compareFieldId;
    }
    public void setCompareFieldId(long compareFieldId) {
        this.compareFieldId = compareFieldId;
    }

    private long addDuration = -1;
    public long getAddDuration() {
        return addDuration;
    }
    public void setAddDuration(long addDuration) {
        this.addDuration = addDuration;
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField baseField = modBean.getField(baseFieldId);
        FacilioField dueField = modBean.getField(dueFieldId);

        Long timeValue;
        if (baseField.isDefault()) {
            timeValue = (Long) PropertyUtils.getProperty(moduleRecord, baseField.getName());
        }
        else {
            timeValue = (Long) moduleRecord.getDatum(baseField.getName());
        }
        if (timeValue == null) {
            timeValue = DateTimeUtil.getCurrenTime();
        }
        timeValue += getAddDuration() * 1000;

        if (dueField.isDefault()) {
            PropertyUtils.setProperty(moduleRecord, dueField.getName(), timeValue);
        }
        else {
            moduleRecord.setDatum(dueField.getName(), timeValue);
        }

        FacilioModule module = modBean.getModule(getModuleId());
        UpdateRecordBuilder<ModuleBaseWithCustomFields> update = new UpdateRecordBuilder<>()
                .module(module)
                .fields(Collections.singletonList(dueField))
                .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), module));
        update.update(moduleRecord);

        super.executeTrueActions(record, context, placeHolders);
    }
}
