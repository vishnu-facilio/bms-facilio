package com.facilio.readingkpi.commands.update;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.NumberField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;


public class UpdateNamespaceAndFieldsCommand extends FacilioCommand {
    ReadingKPIContext readingKpi;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.readingKpi = (ReadingKPIContext) context.get(FacilioConstants.ReadingKpi.READING_KPI);

        updateNamespace();
        updateFields(context);
        return false;
    }

    private void updateNamespace() throws Exception {
        NameSpaceContext ns = readingKpi.getNs();
        if (ns != null) {
            Constants.getNsBean().updateNamespace(ns);
        }
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
}

