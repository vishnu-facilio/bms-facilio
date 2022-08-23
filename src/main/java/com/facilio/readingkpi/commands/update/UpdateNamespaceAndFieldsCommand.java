package com.facilio.readingkpi.commands.update;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.NumberField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;


public class UpdateNamespaceAndFieldsCommand extends FacilioCommand {
    ReadingKPIContext readingKpi;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.readingKpi = (ReadingKPIContext) context.get(FacilioConstants.ReadingKpi.READING_KPI);

        updateNamespace();
        updateNamespaceFields();
        updateFields(context);
        updateNamespaceInclusions(context);
        return false;
    }

    private void updateNamespace() throws Exception {
        NameSpaceContext ns = readingKpi.getNs();
        if (ns != null) {
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .fields(NamespaceModuleAndFieldFactory.getNamespaceFields())
                    .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(ns.getId()), NumberOperators.EQUALS));
            updateBuilder.update(FieldUtil.getAsProperties(ns));
        }
    }

    private void updateNamespaceFields() throws Exception {
        List<NameSpaceField> fields = readingKpi.getNs().getFields();
        NewReadingRuleAPI.addNamespaceFields(readingKpi.getNs().getId(),null, fields);
    }

    private void updateFields(Context context) throws Exception {

        String customUnit = this.readingKpi.getCustomUnit();
        Integer unitId = this.readingKpi.getUnitId();

        context.put(FacilioConstants.ContextNames.READING_FIELD_ID, readingKpi.getReadingFieldId());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();;
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
    private void updateNamespaceInclusions(Context context) throws Exception {
        this.readingKpi.getNs().setStatus(this.readingKpi.getStatus());
        ReadingKPIContext existingReadingKpi = (ReadingKPIContext) context.get("oldReadingKpi");
        List<Long> oldIncludedAssetIds = existingReadingKpi.getNs().getIncludedAssetIds();
        NameSpaceContext ns = this.readingKpi.getNs();
        List<Long> includedAssetIds = ns.getIncludedAssetIds();
        if(CollectionUtils.isEmpty(includedAssetIds) && CollectionUtils.isNotEmpty(oldIncludedAssetIds)) {
                NamespaceAPI.deleteExistingInclusionRecords(ns);
        }
        NamespaceAPI.addInclusions(ns);
    }
}

