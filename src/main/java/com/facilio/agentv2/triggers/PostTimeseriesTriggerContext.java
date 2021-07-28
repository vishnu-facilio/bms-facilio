package com.facilio.agentv2.triggers;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostTimeseriesTriggerContext extends BaseTriggerContext {

    private long siteId;

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    @Override
    public long getModuleId() {
        return 0;
    }

    @Override
    public int getType() {
        return TriggerType.TIMESERIES_COMPLETED_TRIGGER.getValue();
    }

    @Override
    public TriggerType getTypeEnum() {
        return TriggerType.TIMESERIES_COMPLETED_TRIGGER;
    }

    @Override
    public long getEventType() {
        return EventType.TIMESERIES_COMPLETE.getValue();
    }

    @Override
    public void validateTrigger() {

    }

    public List<Long> getResourceIds() throws Exception {

        FacilioModule module = ModuleFactory.getPostTimeseriesTriggerVsResourcesModule();
        Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getPostTimeseriesTriggerVsResourcesFields());
        List<Long> values = new ArrayList<>();
        values.add(this.getId());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fields.get("triggerId"), values, NumberOperators.EQUALS));
        List<FacilioField> toSelect = new ArrayList<>();
        toSelect.add(fields.get("resourceId"));
        List<Map<String, Object>> props = builder.select(toSelect).get();
        List<Long> resourceIds = new ArrayList<>();
        props.forEach(row -> {
            resourceIds.add((Long) row.get("resourceId"));
        });

        return resourceIds;
    }
}
