package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

public class NodeScoringContext extends BaseScoringContext {

    private long recordModuleId;
    public long getRecordModuleId() {
        return recordModuleId;
    }
    public void setRecordModuleId(long recordModuleId) {
        this.recordModuleId = recordModuleId;
    }

    private long recordId;
    public long getRecordId() {
        return recordId;
    }
    public void setRecordId(long recordId) {
        this.recordId = recordId;
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
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(recordModuleId);
        FacilioField scoringField = modBean.getField(scoringFieldId, recordModuleId);

        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(module)
                .select(Collections.singletonList(scoringField))
                .andCondition(CriteriaAPI.getIdCondition(recordId, module));
        Map<String, Object> map = builder.getAsMap();
        Object o = map.get(scoringField.getName());
        if (o instanceof Number) {
            return ((Number) o).floatValue();
        }
        return 0;
    }
}
