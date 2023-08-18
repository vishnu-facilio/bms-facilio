package com.facilio.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.context.*;
import com.facilio.storm.InstructionType;
import com.facilio.time.DateRange;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.bmsconsole.util.BmsJobUtil.scheduleOneTimeJobWithProps;
import static com.facilio.readingkpi.ReadingKpiAPI.*;

@Getter
@Setter
@Log4j
public class ReadingKpiAction extends V3Action {
    private Long recordId;
    private Long startTime; // used both for history and analytics
    private Long endTime; // used both for history and analytics
    private List<Long> assets;
    private Long parentLoggerId;

    // analytics
    private Long categoryId;
    private List<Long> buildingIds;
    private List<Long> parentIds; // resource Id
    private List<Long> spaceIds;
    private AggregateOperator xAggr;
    private Long assetId;

    public int getxAggr() {
        if (xAggr != null) {
            return xAggr.getValue();
        }
        return -1;
    }

    public void setxAggr(int xAggr) {
        this.xAggr = AggregateOperator.getAggregateOperator(xAggr);
    }

    public String getDynamicKpisForAsset() throws Exception {
        setData("dynamicKpis", ReadingKpiAPI.getDynamicKpisForAsset(assetId));
        return SUCCESS;
    }

    public String fetchReadingsData() throws Exception {
        ReadingKPIContext dynKpi = getReadingKpi(recordId);
        Map<Long, List<Map<String, Object>>> resultForDynamicKpi = getResultForDynamicKpi(parentIds, new DateRange(startTime, endTime), xAggr, dynKpi.getNs());
        setData("readings", resultForDynamicKpi);

        return SUCCESS;
    }

    public String fetchAssetCategoriesContainingKpis() throws Exception {

        Map<Long, String> listOfAssetCategoriesWithKpis = getListOfAssetCategoriesOfAllKpis()
                .stream()
                .collect(Collectors.toMap(
                                map -> (Long) map.get("assetCategory"),
                                map -> (String) map.get("name")
                        )
                );

        setData(FieldUtil.getAsJSON(listOfAssetCategoriesWithKpis));
        return SUCCESS;
    }

    public String getReadingsForSpecificAssetCategory() throws Exception {
        String ASSETS = "assets";
        String FIELDS = "fields";
        List<ReadingKPIContext> kpisOfCategory = ReadingKpiAPI.getKpisOfCategory(categoryId);
        Map<String, Object> data = new HashMap<>();

        Set<Long> assets = new HashSet<>();
        HashMap<Long, Set<Long>> kpiIdVsAssetIds = new HashMap<>(); // kpi vs assets
        HashMap<Long, Set<Long>> assetIdVsKpiIds = new HashMap<>(); // asset vs kpis

        List<AssetContext> assetsInBuilding = AssetsAPI.getAssetListOfCategory(categoryId, buildingIds);
        Map<Long, AssetContext> assetIdVsAssetContext = new HashMap<>();
        Map<Long, KpiContextWrapper> kpiIdVsKpiContext = new HashMap<>();

        for (ReadingKPIContext kpiContext : kpisOfCategory) {
            List<Long> includedAssetIds = kpiContext.getNs().getIncludedAssetIds();
            kpiIdVsKpiContext.put(kpiContext.getId(), new KpiContextWrapper(kpiContext));

            if (CollectionUtils.isNotEmpty(includedAssetIds)) {
                Set<Long> includedAssetsInBuilding = assetsInBuilding.stream()
                        .map(ModuleBaseWithCustomFields::getId)
                        .filter(includedAssetIds::contains)
                        .collect(Collectors.toSet());

                if (CollectionUtils.isNotEmpty(includedAssetsInBuilding)) {
                    kpiIdVsAssetIds.put(
                            kpiContext.getId(),
                            includedAssetsInBuilding
                    );
                    assets.addAll(includedAssetsInBuilding);
                }

            } else { // no included assets => all assets in category in building
                if (MapUtils.isEmpty(assetIdVsAssetContext)) {
                    assetIdVsAssetContext = assetsInBuilding.stream().collect(Collectors.toMap(ModuleBaseWithCustomFields::getId, asset -> asset));
                }
                if (CollectionUtils.isNotEmpty(assetsInBuilding)) {
                    kpiIdVsAssetIds.put(kpiContext.getId(), assetIdVsAssetContext.keySet());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(assets)) {
            assetIdVsAssetContext.putAll(AssetsAPI.getAssetInfo(new ArrayList<>(assets))
                    .stream()
                    .collect(Collectors.toMap(ModuleBaseWithCustomFields::getId, asset -> asset, (first, second) -> first)));
        }

        for (Long assetId : assetIdVsAssetContext.keySet()) {
            Set<Long> kpis = assetIdVsKpiIds.get(assetId) != null ? assetIdVsKpiIds.get(assetId) : new HashSet<>();
            kpiIdVsAssetIds.forEach((key, value) -> {
                if (value.contains(assetId)) {
                    kpis.add(key);
                }
            });
            assetIdVsKpiIds.put(assetId, kpis);
        }

        data.put(ASSETS, assetIdVsAssetContext.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getName())));
        data.put(FIELDS, kpiIdVsKpiContext);

        HashMap<Long, Map<Long, Set<Long>>> categoryWithFields = new HashMap<>();
        categoryWithFields.put(categoryId, kpiIdVsAssetIds);

        HashMap<Long, Map<Long, Set<Long>>> categoryWithAssets = new HashMap<>();
        categoryWithAssets.put(categoryId, assetIdVsKpiIds);

        data.put("categoryWithFields", categoryWithFields);
        data.put("categoryWithAssets", categoryWithAssets);

        setData(FieldUtil.getAsJSON(data));
        return SUCCESS;
    }

    public String runHistorical() throws Exception {
        ReadingKPIContext kpi = validatePayload();
        ReadingKpiAPI.setNamespaceAndMatchedResources(Collections.singletonList(kpi));

        try {
            switch (Objects.requireNonNull(KPIType.valueOf(kpi.getKpiType()))) {
                case SCHEDULED:
                    beginSchKpiHistorical(kpi);
                    break;
                case LIVE:
                    beginLiveKpiHistorical(kpi);
                    break;
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
        return ReadingKpiAPI.getReadingKpi(getRecordId());
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
