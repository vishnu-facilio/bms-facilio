package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.AddOrUpdateDashboardFieldMappingCommand;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WidgetVsWorkflowContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.bmsconsoleV3.context.dashboard.WidgetDashboardFilterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class UpdateWidgetCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        List<DashboardWidgetContext> updatedWidgets = (List<DashboardWidgetContext>) context.get(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST);

        GenericUpdateRecordBuilder updateBuilder = null;
        GenericUpdateRecordBuilder updateWidgetChart = null;
        if (updatedWidgets != null && updatedWidgets.size() > 0) {
            for (DashboardWidgetContext updatewidget : updatedWidgets) {

                if (updatewidget.getId() > 0) {
                    updateBuilder = new GenericUpdateRecordBuilder()
                            .table(ModuleFactory.getWidgetModule().getTableName())
                            .fields(FieldFactory.getWidgetFields())
                            .andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetModule()))
                            .ignoreSplNullHandling();

                    Long widgetId = updatewidget.getId();

                    updatewidget.setOrgId(AccountUtil.getCurrentOrg().getId());
                    if(updatewidget.getCustomActions() != null && updatewidget.getCustomActions().size() > 0) {
                        DashboardUtil.addWidgetCustomActions(updatewidget.getCustomActions(), widgetId);
                    }
                    if (updatewidget.getType() == DashboardWidgetContext.WidgetType.CHART.getValue()) {
                        updateWidgetChart = new GenericUpdateRecordBuilder()
                                .table(ModuleFactory.getWidgetChartModule().getTableName())
                                .fields(FieldFactory.getWidgetChartFields())
                                .andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetChartModule()));
                        Map<String, Object> props = FieldUtil.getAsProperties(updatewidget);
                        updateWidgetChart.update(props);
                    }


                    Map<String, Object> props1 = FieldUtil.getAsProperties(updatewidget);
                    if(!props1.containsKey("sectionId") || (props1.containsKey("sectionId") && props1.get("sectionId") == null))
                    {
                        props1.put("sectionId", null);
                    }
                    updateBuilder.update(props1);

                    if (updatewidget.getType().equals(DashboardWidgetContext.WidgetType.STATIC.getValue())) {

                        updateBuilder = new GenericUpdateRecordBuilder()
                                .table(ModuleFactory.getWidgetStaticModule().getTableName())
                                .fields(FieldFactory.getWidgetStaticFields())
                                .andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetStaticModule()));

                        updateBuilder.update(props1);

                        if (updatewidget.getWidgetVsWorkflowContexts() != null && !updatewidget.getWidgetVsWorkflowContexts().isEmpty()) {
                            DashboardUtil.deleteWidgetVsWorkflowContext(updatewidget.getId());
                            for (WidgetVsWorkflowContext widgetVsWorkflowContext : updatewidget.getWidgetVsWorkflowContexts()) {
                                widgetVsWorkflowContext.setWidgetId(updatewidget.getId());
                                DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
                            }
                        }
                    }

                    if (updatewidget.getType().equals(DashboardWidgetContext.WidgetType.GRAPHICS.getValue())) {
                        GenericUpdateRecordBuilder updateWidgetGraphics = new GenericUpdateRecordBuilder()
                                .table(ModuleFactory.getWidgetGraphicsModule().getTableName())
                                .fields(FieldFactory.getWidgetGraphicsFields())
                                .andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetGraphicsModule()));
                        Map<String, Object> props = FieldUtil.getAsProperties(updatewidget);
                        updateWidgetGraphics.update(props);
                    }

                    if (updatewidget.getType().equals(DashboardWidgetContext.WidgetType.CARD.getValue())) {
                        WidgetCardContext widgetCardContext = (WidgetCardContext) updatewidget;

                        if (widgetCardContext.getScriptModeInt() == 3) {
                            widgetCardContext.setCustomScriptId((long) -99);
                            widgetCardContext.setCustomScript(null);
                            widgetCardContext.setScriptModeInt(3);
                        } else if (widgetCardContext.getCustomScript() != null && !widgetCardContext.getCustomScript().trim().isEmpty()) {
                            Long customScriptId = WorkflowUtil.addWorkflow(widgetCardContext.getCustomScript());
                            widgetCardContext.setCustomScriptId(customScriptId);
                        }
                        Long presentCriteriaId = null;
                        long criteriaId = -1;
                        WidgetCardContext cardContext = (WidgetCardContext) DashboardUtil.getWidget(updatewidget.getId());
                        JSONObject cardParams = widgetCardContext.getCardParams();
                        if(cardContext.getCriteriaId() != null && cardContext.getCriteriaId() > 0) {
                            presentCriteriaId = cardContext.getCriteriaId();
                            Criteria presentCriteria = CriteriaAPI.getCriteria(cardContext.getCriteriaId());
                            if(cardParams != null && cardParams.get("criteria") != null) {
                                Criteria criteria = FieldUtil.getAsBeanFromMap((Map<String, Object>) cardParams.get("criteria"),Criteria.class);
                                if(!presentCriteria.getConditions().equals(criteria.getConditions())){
                                    criteriaId = DashboardUtil.generateCriteriaId(criteria, (String) cardParams.get("parentModuleName"));
                                    widgetCardContext.setCriteriaId(criteriaId);
                                }
                            }else {
                                widgetCardContext.setCriteriaId(null);
                                CriteriaAPI.deleteCriteria(cardContext.getCriteriaId());
                            }
                        } else if(cardParams != null && cardParams.get("criteria") != null){
                            Criteria criteriaObj = FieldUtil.getAsBeanFromMap((Map<String, Object>) cardParams.get("criteria"), Criteria.class);
                            criteriaId = DashboardUtil.generateCriteriaId(criteriaObj, (String) cardParams.get("parentModuleName"));
                            widgetCardContext.setCriteriaId(criteriaId);
                        }

                        if(cardParams != null){
                            widgetCardContext.setCategoryId((Long) cardParams.get("categoryId"));
                        }
                        GenericUpdateRecordBuilder updateWidgetCard = new GenericUpdateRecordBuilder()
                                .table(ModuleFactory.getWidgetCardModule().getTableName())
                                .fields(FieldFactory.getWidgetCardFields())
                                .andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetCardModule()));
                        Map<String, Object> props = FieldUtil.getAsProperties(widgetCardContext, true);
                        updateWidgetCard.update(props);
                        if(presentCriteriaId != null && presentCriteriaId > 0){
                            CriteriaAPI.deleteCriteria(presentCriteriaId);
                        }
                    }
                    else if(updatewidget.getType().equals(DashboardWidgetContext.WidgetType.FILTER.getValue())){

                        WidgetDashboardFilterContext filterWidgetContext = (WidgetDashboardFilterContext) updatewidget;
                        if(filterWidgetContext.getDashboardFilterId() > 0){
                            filterWidgetContext.setWidget_id(filterWidgetContext.getId());
                            GenericUpdateRecordBuilder updateWidgetFilter = new GenericUpdateRecordBuilder()
                                    .table(ModuleFactory.getDashboardUserFilterModule().getTableName())
                                    .fields(FieldFactory.getDashboardUserFilterFields())
                                    .andCondition(CriteriaAPI.getCondition("WIDGET_ID","widget_id", String.valueOf(filterWidgetContext.getId()), NumberOperators.EQUALS));

                            Map<String, Object> props = FieldUtil.getAsProperties(filterWidgetContext, true);
                            updateWidgetFilter.update(props);
                            DashboardUserFilterContext userFilter = DashboardFilterUtil.getDashboardUserFiltersForWidgetId((Long) props.get("userFilterId"));
                            AddOrUpdateDashboardFieldMappingCommand fieldMappingCommand = new AddOrUpdateDashboardFieldMappingCommand();
                            context.put(FacilioConstants.ContextNames.DASHBOARD_USER_FILTER_ID,userFilter.getId());
                            context.put("fieldMappings",filterWidgetContext.getFieldMappingMap());
                            fieldMappingCommand.executeCommand(context);
                        }
                    }
                    else if (updatewidget.getType() == DashboardWidgetContext.WidgetType.SECTION.getValue()) {
                        WidgetSectionContext widget_section = (WidgetSectionContext) updatewidget;
                        GenericUpdateRecordBuilder updateWidgetSection = new GenericUpdateRecordBuilder()
                                .table(ModuleFactory.getWidgetSectionModule().getTableName())
                                .fields(FieldFactory.getWidgetSectionFields())
                                .andCondition(CriteriaAPI.getIdCondition(updatewidget.getId(), ModuleFactory.getWidgetSectionModule()));

                        Map<String, Object> props = FieldUtil.getAsProperties(widget_section, true);
                        updateWidgetSection.update(props);
                    }
                }
                if(updatewidget.getWidgetFieldMapping() != null && updatewidget.getWidgetFieldMapping().size() > 0) {
                    DashboardUtil.addWidgetFieldMapping(updatewidget);
                }
                if(updatewidget.getReadingWidgetFieldMapping() != null && updatewidget.getReadingWidgetFieldMapping().size() > 0) {
                    DashboardUtil.addReadingWidgetFieldMapping(updatewidget);
                }
            }
        }
        return false;
    }
}
