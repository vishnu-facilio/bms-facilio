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

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NodeScoringContext extends BaseScoringContext {

    private long fieldId;
    public long getFieldId() {
        return fieldId;
    }
    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    private long fieldModuleId;
    public long getFieldModuleId() {
        return fieldModuleId;
    }
    public void setFieldModuleId(long fieldModuleId) {
        this.fieldModuleId = fieldModuleId;
    }

    private long scoringFieldId;
    public long getScoringFieldId() {
        return scoringFieldId;
    }
    public void setScoringFieldId(long scoringFieldId) {
        this.scoringFieldId = scoringFieldId;
    }

    @Override
    public float evaluatedScore(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        if (record instanceof ModuleBaseWithCustomFields) {
            ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule module = modBean.getModule(moduleRecord.getModuleId());
            FacilioModule fieldModule = modBean.getModule(fieldModuleId);
            LookupField lookupField = (LookupField) modBean.getField(fieldId, fieldModule.getModuleId());

            if (module.isParentOrChildModule(lookupField.getModule())) {
                Object value = FieldUtil.getValue(moduleRecord, lookupField);
                if (value instanceof ModuleBaseWithCustomFields) {
                    long recordId = ((ModuleBaseWithCustomFields) value).getId();
                    FacilioField scoringField = modBean.getField(scoringFieldId, lookupField.getLookupModuleId());

                    SelectRecordsBuilder builder = new SelectRecordsBuilder()
                            .module(lookupField.getLookupModule())
                            .beanClass(FacilioConstants.ContextNames.getClassFromModule(lookupField.getLookupModule()))
                            .select(Collections.singletonList(scoringField))
                            .andCondition(CriteriaAPI.getIdCondition(recordId, lookupField.getLookupModule()));
                    List<Map<String, Object>> props = builder.getAsProps();
                    if (CollectionUtils.isNotEmpty(props)) {
                        Map<String, Object> map = props.get(0);
                        Object o = map.get(scoringField.getName());
                        if (o instanceof Number) {
                            return ((Number) o).floatValue();
                        }
                    }
                }
            }
            else if (module.isParentOrChildModule(lookupField.getLookupModule())) {
                FacilioField scoringField = modBean.getField(scoringFieldId, fieldModuleId);
                SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
                        .module(fieldModule)
                        .beanClass(FacilioConstants.ContextNames.getClassFromModule(fieldModule))
                        .aggregate(BmsAggregateOperators.NumberAggregateOperator.AVERAGE, scoringField)
                        .andCondition(CriteriaAPI.getCondition(lookupField, String.valueOf(moduleRecord.getId()), NumberOperators.EQUALS));
                List<Map<String, Object>> props = selectBuilder.getAsProps();
                if (CollectionUtils.isNotEmpty(props)) {
                    Map<String, Object> map = props.get(0);
                    Object o = map.get(scoringField.getName());
                    if (o instanceof Number) {
                        return ((Number) o).floatValue();
                    }
                }
            }
        }
        return 0;
    }
}
