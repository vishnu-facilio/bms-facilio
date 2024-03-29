package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.trigger.context.ScoringRuleTrigger;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class NodeScoringContext extends BaseScoringContext {

    private static Logger LOGGER = Logger.getLogger(NodeScoringContext.class.getSimpleName());

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private NodeType nodeType;
    public int getNodeType() {
        if (nodeType != null) {
            return nodeType.getIndex();
        }
        return -1;
    }
    public void setNodeType(int nodeType) {
        this.nodeType = NodeType.valueOf(nodeType);
    }
    public NodeType getNodeTypeEnum() {
        return nodeType;
    }
    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    private long fieldId = -1;
    public long getFieldId() {
        return fieldId;
    }
    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    private long fieldModuleId = -1;
    public long getFieldModuleId() {
        return fieldModuleId;
    }
    public void setFieldModuleId(long fieldModuleId) {
        this.fieldModuleId = fieldModuleId;
    }

    private long scoreRuleId = -1;
    public long getScoreRuleId() {
        return scoreRuleId;
    }
    public void setScoreRuleId(long scoreRuleId) {
        this.scoreRuleId = scoreRuleId;
    }

    private Boolean shouldBePropagated;
    public Boolean getShouldBePropagated() {
        return shouldBePropagated;
    }
    public void setShouldBePropagated(Boolean shouldBePropagated) {
        this.shouldBePropagated = shouldBePropagated;
    }
    public Boolean shouldBePropagated() {
        if (shouldBePropagated == null) {
            return false;
        }
        return shouldBePropagated;
    }

    @Override
    public String getScoreType() {
        return "dependency";
    }

    @Override
    public boolean shouldUpdateParent() {
        return true;
    }

    @Override
    public void afterSave(ScoringRuleContext rule) throws Exception {
        if (!shouldBePropagated()) {
            return;
        }

        if (nodeType == NodeType.CURRENT_MODULE) {
            return;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(rule.getModuleId());

        // add trigger to be called parent rule
        ScoringRuleTrigger trigger = new ScoringRuleTrigger();
        trigger.setName("scoring_rule_" + scoreRuleId + "_dependency_" + getId());
        trigger.setEventType(EventType.INVOKE_TRIGGER);
        trigger.setType(TriggerType.SCORING_RULE_TRIGGER);
        trigger.setInternal(true);
        trigger.setModuleId(module.getModuleId());
        trigger.setStatus(true);
        rule.addTrigger(trigger);

        FacilioChain chain = TransactionChainFactoryV3.getTriggerAddOrUpdateChain();
        chain.getContext().put(TriggerUtil.TRIGGER_CONTEXT, trigger);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.execute();

        ScoringRuleAPI.addTriggersToBeExecuted(scoreRuleId, trigger, getId());
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (nodeType == null) {
            throw new IllegalArgumentException("Dependency type cannot be null");
        }

        if (scoreRuleId < 0) {
            throw new IllegalArgumentException("Score rule cannot be empty");
        }

        switch (nodeType) {
            case SUB_MODULE:
//            case PARENT_MODULE:
                if (fieldId < 0 || fieldModuleId < 0) {
                    throw new IllegalArgumentException("Field and module cannot be null");
                }
        }
    }

    @Override
    public float evaluatedScore(Object record, Context context, Map<String, Object> placeHolders, long moduleId) throws Exception {
        if (record instanceof ModuleBaseWithCustomFields) {
            ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule scoreModule = ScoringRuleAPI.getScoreModule(moduleId);

            switch (nodeType) {
                case CURRENT_MODULE: {
                    ScoreContext scoreRecord = ScoringRuleAPI.getScoreRecord(scoreModule, scoreRuleId, moduleRecord);
                    if (scoreRecord != null) {
                        return scoreRecord.getScore();
                    }
                    return 0;
                }

//                case PARENT_MODULE: {
//                    FacilioModule fieldModule = modBean.getModule(fieldModuleId);
//                    LookupField lookupField = (LookupField) modBean.getField(fieldId, fieldModule.getModuleId());
//
//                    Object value = FieldUtil.getValue(moduleRecord, lookupField);
//                    if (value instanceof ModuleBaseWithCustomFields) {
//                        // get parent module score field
//                        FacilioModule parentScoreModule = ScoringRuleAPI.getScoreModule(lookupField.getLookupModuleId());
//                        ScoreContext scoreRecord = ScoringRuleAPI.getScoreRecord(parentScoreModule, scoreRuleId, (ModuleBaseWithCustomFields) value);
//
//                        if (scoreRecord == null) {
//                            return scoreRecord.getScore();
//                        }
//                    }
//                    return 0;
//                }

                case SUB_MODULE: {
                    FacilioModule fieldModule = modBean.getModule(fieldModuleId);
                    LookupField lookupField = (LookupField) modBean.getField(fieldId, fieldModule.getModuleId());

                    FacilioModule subModuleScore = ScoringRuleAPI.getScoreModule(fieldModuleId);
                    List<FacilioField> allFields = modBean.getAllFields(subModuleScore.getName());
                    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
                    FacilioField scoreField = fieldMap.get("score");
                    SelectRecordsBuilder<ScoreContext> selectBuilder = new SelectRecordsBuilder<ScoreContext>()
                            .module(fieldModule)
                            .innerJoin(subModuleScore.getTableName())
                                .on(fieldMap.get("parent").getCompleteColumnName() + " = " + fieldModule.getTableName() + ".ID")
                            .beanClass(ScoreContext.class)
                            .aggregate(BmsAggregateOperators.NumberAggregateOperator.AVERAGE, scoreField)
                            .andCondition(CriteriaAPI.getCondition(lookupField, String.valueOf(moduleRecord.getId()), NumberOperators.EQUALS))
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("scoreRuleId"), String.valueOf(scoreRuleId), NumberOperators.EQUALS))
                            ;
                    List<Map<String, Object>> props = selectBuilder.getAsProps();
                    if (CollectionUtils.isNotEmpty(props)) {
                        Map<String, Object> map = props.get(0);
                        Object o = map.get(scoreField.getName());
                        if (o instanceof Number) {
                            return ((Number) o).floatValue();
                        }
                    }
                    return 0;
                }

                default:
                    throw new IllegalArgumentException("Invalid dependency type");
            }
        }
        return 0;
    }

    public enum NodeType implements FacilioIntEnum {
//        PARENT_MODULE("Parent Module"),
        SUB_MODULE("Sub Module"),
        CURRENT_MODULE("Current Module")
        ;

        private String name;

        NodeType(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static NodeType valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}
