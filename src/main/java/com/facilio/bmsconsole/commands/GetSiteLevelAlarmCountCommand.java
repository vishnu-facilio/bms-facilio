package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSiteLevelAlarmCountCommand extends FacilioCommand {

    private String[] colors = {"#00cc66", "#ffff00", "#0066ff", "#ff3300"};

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> siteIds = (List<Long>) context.get("siteIds");
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
        FacilioModule assetModule = modBean.getModule(moduleName);


        List<FacilioField> fields = new ArrayList<>();
        FacilioField siteIdField = FieldFactory.getSiteIdField(assetModule);
        fields.add(siteIdField);

        fields.addAll(FieldFactory.getCountField(module));

        AlarmSeverityContext clearSeverity = AlarmAPI.getAlarmSeverity("Clear");

        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(assetModule)
                .table(module.getTableName())
                .select(fields)
                .innerJoin(module.getTableName())
                    .on(module.getTableName() + ".RESOURCE_ID = " + assetModule.getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition("SEVERITY_ID", "severity", String.valueOf(clearSeverity.getId()), NumberOperators.NOT_EQUALS))
                .groupBy(siteIdField.getCompleteColumnName())
                ;
        if (CollectionUtils.isNotEmpty(siteIds)) {
            builder.andCondition(CriteriaAPI.getCondition(siteIdField, siteIds, NumberOperators.EQUALS));
        }
        List<Map<String, Object>> maps = builder.getAsProps();

        Map<Long, Integer> siteAssetMap = null;
        if (StringUtils.isNotEmpty(moduleName)) {
            List<FacilioField> assetFields = new ArrayList<>();
            assetFields.add(siteIdField);
            assetFields.addAll(FieldFactory.getCountField(assetModule));
            SelectRecordsBuilder assetSiteBuilder = new SelectRecordsBuilder()
                    .module(assetModule)
                    .select(assetFields)
                    .groupBy(siteIdField.getCompleteColumnName())
                    ;
            List<Map<String, Object>> siteAssetList = assetSiteBuilder.getAsProps();
            siteAssetMap = new HashMap<>();
            for (Map<String, Object> siteAsset : siteAssetList) {
                long siteId = ((Number) siteAsset.get("siteId")).longValue();
                int count = ((Number) siteAsset.get("count")).intValue();
                siteAssetMap.put(siteId, count);
            }
        }

        SelectRecordsBuilder<SiteContext> siteBuilder = new SelectRecordsBuilder<SiteContext>()
                .beanClass(SiteContext.class)
                .moduleName(FacilioConstants.ContextNames.SITE)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.SITE))
                ;
        if (CollectionUtils.isNotEmpty(siteIds)) {
            siteBuilder.andCondition(CriteriaAPI.getIdCondition(siteIds, modBean.getModule(FacilioConstants.ContextNames.SITE)));
        }
        List<SiteContext> siteContexts = siteBuilder.get();

        Map<Long, Map<String, Object>> siteMap = new HashMap();
        for (Map<String, Object> map : maps) {
            long siteId = ((Number) map.get("siteId")).longValue();
            siteMap.put(siteId, map);
        }

        int totalCount = 0;
        for (SiteContext site : siteContexts) {
            long siteId = site.getSiteId();
            Map<String, Object> map = siteMap.get(siteId);
            if (map == null) {
                map = new HashMap<>();
                map.put("count", 0);
                map.put("siteId", siteId);
                maps.add(map);
            }

            Integer assetCount = siteAssetMap.get(siteId);
            if (assetCount == null) {
                assetCount = 0;
            }
            map.put("assetCount", assetCount);

            int count = ((Number) map.get("count")).intValue();
            totalCount += count;
        }

        for (Map<String, Object> map : maps) {
            int count = ((Number) map.get("count")).intValue();
            float per = 0;
            if (totalCount != 0) {
                per = count / totalCount;
            }
            map.put("colorCode", getColorCode(per));
        }

        context.put("siteAlarmCount", maps);
        return false;
    }

    private String getColorCode(float per) {
        if (per == 0) {
            return colors[0];
        }
        else if (per < 0.33) {
            return colors[1];
        }
        else if (per < 0.66) {
            return colors[2];
        }
        return colors[3];
    }
}
