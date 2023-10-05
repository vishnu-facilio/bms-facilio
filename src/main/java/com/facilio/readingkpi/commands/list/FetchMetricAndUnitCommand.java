
package com.facilio.readingkpi.commands.list;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.NumberField;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FetchMetricAndUnitCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ReadingKPIContext kpi : list) {
                if (kpi.getKpiTypeEnum() == KPIType.DYNAMIC) {
                    continue;
                }
                Long fieldId = kpi.getReadingFieldId();
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                NumberField readingField = (NumberField) modBean.getField(fieldId);
                Integer metricId = readingField.getMetric();
                if (metricId != -1) {
                    kpi.setMetricId(metricId);
                    kpi.setUnitId(readingField.getUnitId());
                } else {
                    String customUnit = readingField.getUnit();
                    kpi.setCustomUnit(customUnit);
                }

                ReadingKpiAPI.setCategory(kpi);
            }
        }
        return false;
    }
}