package com.facilio.readingkpi.commands.delete;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.readingkpi.context.KpiLoggerContext;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;

public class DeleteKpiLoggersCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> kpiIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        deleteKpiLoggersForKpi(kpiIds.get(0));
        return false;
    }

    private void deleteKpiLoggersForKpi(Long kpiId) throws Exception {
        deleteKpiResourceLoggersForKpi(kpiId);
        FacilioModule kpiModule = Constants.getModBean().getModule(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE);
        DeleteRecordBuilder<KpiLoggerContext> deleteRecordBuilder = new DeleteRecordBuilder<KpiLoggerContext>()
                .module(kpiModule)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getField("kpi","KPI_ID",kpiModule, FieldType.NUMBER),kpiId.toString(), NumberOperators.EQUALS));
        deleteRecordBuilder.markAsDelete();
    }

    private void deleteKpiResourceLoggersForKpi(Long kpiId) throws Exception {
        FacilioModule kpiModule = Constants.getModBean().getModule(FacilioConstants.ReadingKpi.KPI_RESOURCE_LOGGER_MODULE);
        DeleteRecordBuilder<KpiResourceLoggerContext> deleteRecordBuilder = new DeleteRecordBuilder<KpiResourceLoggerContext>()
                .module(kpiModule)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getField("kpi","KPI_ID",kpiModule, FieldType.NUMBER),kpiId.toString(), NumberOperators.EQUALS));
        deleteRecordBuilder.markAsDelete();
    }
}
