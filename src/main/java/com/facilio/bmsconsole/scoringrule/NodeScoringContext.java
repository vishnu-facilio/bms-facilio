package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
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

    @Override
    public String getScoreType() {
        return "dependency";
    }

    @Override
    public boolean shouldUpdateParent() {
        return true;
    }

    @Override
    public void validate() {
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
            case PARENT_MODULE:
                if (fieldId < 0 || fieldModuleId < 0) {
                    throw new IllegalArgumentException("Field and module cannot be null");
                }
        }
    }

    @Override
    public float evaluatedScore(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        if (record instanceof ModuleBaseWithCustomFields) {
            ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule module = modBean.getModule(moduleRecord.getModuleId());
            FacilioModule scoreModule = ScoringRuleAPI.getScoreModule(moduleRecord.getModuleId());

            switch (nodeType) {
                case CURRENT_MODULE: {
                    ScoreContext scoreRecord = ScoringRuleAPI.getScoreRecord(scoreModule, scoreRuleId, moduleRecord);
                    if (scoreRecord != null) {
                        return scoreRecord.getScore();
                    }
                    return 0;
                }

                case PARENT_MODULE: {
//                    FacilioModule fieldModule = modBean.getModule(fieldModuleId);
//                    LookupField lookupField = (LookupField) modBean.getField(fieldId, fieldModule.getModuleId());
//
//                    Object value = FieldUtil.getValue(moduleRecord, lookupField);
//                    if (value instanceof ModuleBaseWithCustomFields) {
//                        // get parent module score field
//                        long recordId = ((ModuleBaseWithCustomFields) value).getId();
//                        FacilioField scoringField = modBean.getField(scoringFieldId, lookupField.getLookupModuleId());
//
//                        SelectRecordsBuilder builder = new SelectRecordsBuilder()
//                                .module(lookupField.getLookupModule())
//                                .beanClass(FacilioConstants.ContextNames.getClassFromModule(lookupField.getLookupModule()))
//                                .select(Collections.singletonList(scoringField))
//                                .andCondition(CriteriaAPI.getIdCondition(recordId, lookupField.getLookupModule()));
//                        List<Map<String, Object>> props = builder.getAsProps();
//                        if (CollectionUtils.isNotEmpty(props)) {
//                            Map<String, Object> map = props.get(0);
//                            Object o = map.get(scoringField.getName());
//                            if (o instanceof Number) {
//                                return ((Number) o).floatValue();
//                            }
//                        }
//                    }
                    return 0;
                }
                case SUB_MODULE: {
//                    FacilioModule fieldModule = modBean.getModule(fieldModuleId);
//                    LookupField lookupField = (LookupField) modBean.getField(fieldId, fieldModule.getModuleId());
//
//                    FacilioField scoringField = modBean.getField(scoringFieldId, fieldModuleId);
//                    SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
//                            .module(fieldModule)
//                            .beanClass(FacilioConstants.ContextNames.getClassFromModule(fieldModule))
//                            .aggregate(BmsAggregateOperators.NumberAggregateOperator.AVERAGE, scoringField)
//                            .andCondition(CriteriaAPI.getCondition(lookupField, String.valueOf(moduleRecord.getId()), NumberOperators.EQUALS));
//                    List<Map<String, Object>> props = selectBuilder.getAsProps();
//                    if (CollectionUtils.isNotEmpty(props)) {
//                        Map<String, Object> map = props.get(0);
//                        Object o = map.get(scoringField.getName());
//                        if (o instanceof Number) {
//                            return ((Number) o).floatValue();
//                        }
//                    }
                    return 0;
                }

                default:
                    throw new IllegalArgumentException("Invalid dependency type");
            }
        }
        return 0;
    }

    public enum NodeType implements FacilioEnum {
        PARENT_MODULE("Parent Module"),
        SUB_MODULE("Sub Module"),
        CURRENT_MODULE("Current Module")
        ;

        private String name;

        NodeType(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
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
