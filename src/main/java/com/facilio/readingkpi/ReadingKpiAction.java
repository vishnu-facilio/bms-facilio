package com.facilio.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.KpiLoggerContext;
import com.facilio.readingkpi.context.KpiResourceLoggerContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.storm.InstructionType;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.facilio.bmsconsole.util.BmsJobUtil.scheduleOneTimeJobWithProps;

@Getter
@Setter
@Log4j
public class ReadingKpiAction extends V3Action {
    private Long recordId;
    private Long startTime;
    private Long endTime;
    private List<Long> assets;
    private Long parentLoggerId;

    public String runHistorical() throws Exception {
        ReadingKPIContext kpi = validatePayload();
        ReadingKpiAPI.setNamespaceAndMatchedResources(Collections.singletonList(kpi));

        try {
            switch (Objects.requireNonNull(KPIType.valueOf(kpi.getKpiType()))) {
                case SCHEDULED:
                    beginSchKpiHistorical(kpi);
                case LIVE:
                    beginLiveKpiHistorical(kpi);
            }
            setData(SUCCESS, "Historical KPI Calculation has started");

            return SUCCESS;
        } catch (Exception userException) {
            setData("Failed", userException.getMessage());
            throw userException;
        }
    }

    private void beginLiveKpiHistorical(ReadingKPIContext kpi) throws Exception {
        FacilioChain runStormHistorical = TransactionChainFactory.initiateStormInstructionExecChain();
        FacilioContext context = runStormHistorical.getContext();
        context.put("type", InstructionType.LIVE_KPI_HISTORICAL.getIndex());

        JSONObject instructionData = new JSONObject();
        instructionData.put("recordId", getRecordId());
        instructionData.put("startTime", getStartTime());
        instructionData.put("endTime", getEndTime());
        instructionData.put("assetIds", getAssets());

        Long parentLoggerId = ReadingKpiLoggerAPI.insertLog(kpi.getId(), kpi.getKpiType(), startTime, endTime, false, CollectionUtils.isNotEmpty(getAssets()) ? getAssets().size() : kpi.getMatchedResourcesIds().size());
        instructionData.put("parentLoggerId", parentLoggerId);

        context.put("data", instructionData);
        runStormHistorical.execute();
    }

    private void beginSchKpiHistorical(ReadingKPIContext kpi) throws Exception {
        JSONObject props = new JSONObject();
        props.put(FacilioConstants.ContextNames.START_TIME, getStartTime());
        props.put(FacilioConstants.ContextNames.END_TIME, getEndTime());
        props.put(FacilioConstants.ReadingKpi.IS_HISTORICAL, true);
        props.put(FacilioConstants.ContextNames.RESOURCE_LIST, getAssets());
        props.put(FacilioConstants.ReadingKpi.READING_KPI, getRecordId());

        parentLoggerId = ReadingKpiLoggerAPI.insertLog(kpi.getId(), KPIType.SCHEDULED.getIndex(), startTime, endTime, false, CollectionUtils.isNotEmpty(getAssets()) ? getAssets().size() : kpi.getMatchedResourcesIds().size());
        props.put(FacilioConstants.ReadingKpi.PARENT_LOGGER_ID, parentLoggerId);

        scheduleOneTimeJobWithProps(ReadingKpiLoggerAPI.getNextJobId(), FacilioConstants.ReadingKpi.READING_KPI_HISTORICAL_JOB, 1, "facilio", props);
    }


    public ReadingKPIContext validatePayload() throws Exception {
        if (getId() == -1 || getStartTime() == -1 || getEndTime() == -1) {
            throw new IllegalArgumentException("Insufficient parameters for Historical Kpi calculation");
        }
        ReadingKPIContext kpi = ReadingKpiAPI.getReadingKpi(getRecordId());
        if (kpi == null) {
            throw new IllegalArgumentException("Invalid kpi ID for historical kpi calculation");
        }
//        if (LogsInProgress()) {
//            throw new IllegalArgumentException("Historical cannot be started, other historical in progress");
//        }
        return kpi;
    }

    public Boolean LogsInProgress() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule loggerModule = modBean.getModule(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ReadingKpi.KPI_LOGGER_MODULE));
        SelectRecordsBuilder<KpiLoggerContext> builder = new SelectRecordsBuilder<KpiLoggerContext>()
                .select(Collections.singleton(FieldFactory.getIdField(loggerModule)))
                .module(loggerModule)
                .beanClass(KpiLoggerContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(KpiResourceLoggerContext.KpiLoggerStatus.IN_PROGRESS.getIndex()), NumberOperators.EQUALS));
        List<KpiLoggerContext> loggersInProgress = builder.get();
        return CollectionUtils.isNotEmpty(loggersInProgress);
    }

    public String fetchAssetDetails() throws Exception {
        FacilioChain runStormHistorical = ReadOnlyChainFactory.getAssetNamesForKpiHistory();
        FacilioContext context = runStormHistorical.getContext();
        context.put("parentLoggerId", getParentLoggerId());
        runStormHistorical.execute();
        setData("assetList", context.get("assetList"));
        return SUCCESS;
    }
}
