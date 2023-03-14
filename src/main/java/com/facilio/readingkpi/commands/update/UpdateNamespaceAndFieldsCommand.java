package com.facilio.readingkpi.commands.update;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class UpdateNamespaceAndFieldsCommand extends FacilioCommand {
    ReadingKPIContext readingKpi;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.readingKpi = (ReadingKPIContext) context.get(FacilioConstants.ReadingKpi.READING_KPI);

        updateFields(context);
        updateReadingModuleName(context);
        return false;
    }

    private void updateFields(Context context) throws Exception {

        String customUnit = this.readingKpi.getCustomUnit();
        Integer unitId = this.readingKpi.getUnitId();

        context.put(FacilioConstants.ContextNames.READING_FIELD_ID, readingKpi.getReadingFieldId());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        NumberField field = new NumberField();

        if (StringUtils.isNotEmpty(readingKpi.getName())) {
            field.setDisplayName(readingKpi.getName());
            module.setDisplayName(readingKpi.getName());
        }

        if (customUnit != null) {
            field.setUnit(customUnit);
            field.setUnitId(-99);
            field.setMetric(0);
        } else if (unitId!=null && unitId > 0) {
            field.setUnitId(unitId);
            field.setMetric(this.readingKpi.getMetricId());
        }

        field.setId(this.readingKpi.getReadingFieldId());
        modBean.updateField(field);
    }
    private void updateReadingModuleName(Context context) throws Exception {
        List<ReadingKPIContext> kpis = (List<ReadingKPIContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ReadingKpi.READING_KPI));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for(ReadingKPIContext kpi : kpis) {
            FacilioModule module = new FacilioModule();
            module.setModuleId(kpi.getReadingModuleId());
            module.setDisplayName(kpi.getName());
            modBean.updateModule(module);
            FacilioField field = new FacilioField();
            field.setFieldId(kpi.getReadingFieldId());
            field.setDisplayName(kpi.getName());
            modBean.updateField(field);
        }
    }
}

