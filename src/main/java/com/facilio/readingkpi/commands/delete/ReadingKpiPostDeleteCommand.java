package com.facilio.readingkpi.commands.delete;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NSType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.sql.SQLException;
import java.util.*;

import static com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType.HIDDEN_FORMULA_FIELD;

public class ReadingKpiPostDeleteCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        ReadingKPIContext kpi = list.get(0);

        deleteNamespaceForKpi(kpi);
        makeRdmEntriesHidden(kpi.getReadingFieldId());

        return false;
    }

    private static void deleteNamespaceForKpi(ReadingKPIContext kpi) throws Exception {
        List<NSType> nsList =new ArrayList<>();
        nsList.add(NSType.KPI_RULE);
        Constants.getNsBean().deleteNameSpacesFromRuleId(kpi.getId(),nsList);
    }

    private void makeRdmEntriesHidden(Long fieldId) throws SQLException {
        FacilioModule module = ModuleFactory.getReadingDataMetaModule();
        List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        Map<String, Object> updateProps = new HashMap<>();
        updateProps.put("inputType", HIDDEN_FORMULA_FIELD.getValue());

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Collections.singletonList(fieldsMap.get("inputType")))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("fieldId"), Collections.singleton(fieldId), NumberOperators.EQUALS));
        builder.update(updateProps);
    }
}
