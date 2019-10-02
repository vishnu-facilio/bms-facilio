package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetSiteLevelAlarmCountCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> siteIds = (List<Long>) context.get("siteIds");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);

        List<FacilioField> fields = new ArrayList<>();
        FacilioField siteIdField = FieldFactory.getSiteIdField(resourceModule);
        fields.add(siteIdField);

        fields.addAll(FieldFactory.getCountField(module));

        AlarmSeverityContext clearSeverity = AlarmAPI.getAlarmSeverity("Clear");

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .innerJoin(resourceModule.getTableName())
                .on(module.getTableName() + ".RESOURCE_ID = " + resourceModule.getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition("SEVERITY_ID", "severity", String.valueOf(clearSeverity.getId()), NumberOperators.NOT_EQUALS))
                .groupBy(siteIdField.getCompleteColumnName())
                ;

        if (CollectionUtils.isNotEmpty(siteIds)) {
            builder.andCondition(CriteriaAPI.getCondition(siteIdField, siteIds, NumberOperators.EQUALS));
        }
        List<Map<String, Object>> maps = builder.get();
        context.put("siteAlarmCount", maps);
        return false;
    }
}
