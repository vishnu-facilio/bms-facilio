package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.analytics.v2.context.WidgetLegendGroupContext;
import com.facilio.analytics.v2.context.WidgetLegendVarianceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class AddOrUpdateWidgetLegendCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V2ReportContext v2_report = (V2ReportContext) context.get("report_v2");
        if(v2_report != null) {
            deleteWidgetLegendGroup(v2_report.getReportId());
            List<WidgetLegendGroupContext> widgetLegendGroups = v2_report.getWidgetLegendGroup();
            if(CollectionUtils.isNotEmpty(widgetLegendGroups)) {
                for(WidgetLegendGroupContext widgetLegendGroup : widgetLegendGroups) {
                    if(widgetLegendGroup != null) {
                        widgetLegendGroup.setReportId(v2_report.getReportId());
                        long id = addWidgetLegendGroup(widgetLegendGroup);
                        List<WidgetLegendVarianceContext> varianceList = widgetLegendGroup.getVariances();
                        if(CollectionUtils.isNotEmpty(varianceList)) {
                            for(WidgetLegendVarianceContext variance : varianceList) {
                                if(variance != null) {
                                    variance.setGroupId(id);
                                }
                            }
                            addWidgetLegendVariance(varianceList);
                        }
                    }
                }
            }
        }
        return false;
    }

    private Long addWidgetLegendGroup(WidgetLegendGroupContext widgetLegendGroup) throws Exception {
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getWidgetLegendGroupFields())
                .table(ModuleFactory.getWidgetLegendGroupModule().getTableName());
        return insertRecordBuilder.insert(FieldUtil.getAsProperties(widgetLegendGroup));
    }

    private void deleteWidgetLegendGroup(Long reportId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("REPORT_ID","reportId",String.valueOf(reportId), NumberOperators.EQUALS));
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getWidgetLegendGroupModule().getTableName())
                        .andCriteria(criteria);
        deleteBuilder.delete();
    }
    private void addWidgetLegendVariance(List<WidgetLegendVarianceContext> widgetLegendVariance) throws Exception {
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getWidgetLegendVarianceFields())
                .table(ModuleFactory.getWidgetLegendVarianceModule().getTableName());
        insertRecordBuilder.addRecords(FieldUtil.getAsMapList(widgetLegendVariance,WidgetLegendVarianceContext.class));
        insertRecordBuilder.save();
    }
}