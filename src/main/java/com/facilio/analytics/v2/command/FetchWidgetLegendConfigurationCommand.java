package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.analytics.v2.context.WidgetLegendGroupContext;
import com.facilio.analytics.v2.context.WidgetLegendVarianceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.util.FacilioUtil;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class FetchWidgetLegendConfigurationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long reportId = (Long) context.get("reportId");
        List<WidgetLegendGroupContext> widgetLegends = getWidgetLegends(reportId);
        ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        if(reportContext != null) {
            context.put(FacilioConstants.ContextNames.REPORT,reportContext);
        }
        context.put("widgetLegends", widgetLegends);
        return false;
    }

    private List<WidgetLegendGroupContext> getWidgetLegends(Long oldReportId) throws Exception {
        Long reportId = ReportUtil.getNewReportId(oldReportId);
        if(reportId != null) {
            List<FacilioField> fields = new ArrayList<>();
            fields.addAll(FieldFactory.getWidgetLegendGroupFields());
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(ModuleFactory.getWidgetLegendGroupModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", String.valueOf(reportId), NumberOperators.EQUALS));
            List<Map<String, Object>> propsList = selectRecordBuilder.get();
            if (CollectionUtils.isNotEmpty(propsList)) {
                List<WidgetLegendGroupContext> widgetLegendGroups = FieldUtil.getAsBeanListFromMapList(propsList, WidgetLegendGroupContext.class);
                List<Long> ids = widgetLegendGroups.stream().map(WidgetLegendGroupContext::getId).collect(Collectors.toList());
                List<WidgetLegendVarianceContext> varianceContexts = getVarianceList(ids);
                if (CollectionUtils.isNotEmpty(varianceContexts)) {
                    constructWidgetLegendGroup(widgetLegendGroups, varianceContexts);
                }
                return widgetLegendGroups;
            }
        }
        return null;
    }

    private void constructWidgetLegendGroup(List<WidgetLegendGroupContext> widgetLegendGroups, List<WidgetLegendVarianceContext> varianceContexts) {
        if (CollectionUtils.isNotEmpty(widgetLegendGroups)) {
            widgetLegendGroups.forEach(widgetLegendGroup -> {
                List<WidgetLegendVarianceContext> varianceContextList = varianceContexts.stream().filter(varianceContext -> varianceContext.getGroupId().equals(widgetLegendGroup.getId())).collect(Collectors.toList());
                widgetLegendGroup.setVariances(varianceContextList);
            });
        }
    }

    private List<WidgetLegendVarianceContext> getVarianceList(List<Long> widgetLegendGroupIds) throws Exception {
        if (CollectionUtils.isNotEmpty(widgetLegendGroupIds)) {
            List<FacilioField> fields = new ArrayList<>();
            fields.addAll(FieldFactory.getWidgetLegendVarianceFields());
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(ModuleFactory.getWidgetLegendVarianceModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", StringUtils.join(widgetLegendGroupIds, ","), NumberOperators.EQUALS));
            List<Map<String, Object>> propsList = selectRecordBuilder.get();
            if (CollectionUtils.isNotEmpty(propsList)) {
                List<WidgetLegendVarianceContext> widgetLegendVarianceContexts = FieldUtil.getAsBeanListFromMapList(propsList, WidgetLegendVarianceContext.class);
                return widgetLegendVarianceContexts;
            }
        }
        return null;
    }
}
