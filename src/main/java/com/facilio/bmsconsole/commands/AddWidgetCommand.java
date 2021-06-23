package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetGraphicsContext;
import com.facilio.bmsconsole.context.WidgetListViewContext;
import com.facilio.bmsconsole.context.WidgetStaticContext;
import com.facilio.bmsconsole.context.WidgetVsWorkflowContext;
import com.facilio.bmsconsole.context.WidgetWebContext;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.cards.util.CardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BaseLineContext.RangeType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.util.WorkflowUtil;

public class AddWidgetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardWidgetContext widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
		if(widget != null) {
			
			widget.setOrgId(AccountUtil.getCurrentOrg().getId());
			
			List<FacilioField> fields = FieldFactory.getWidgetFields();
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getWidgetModule().getTableName())
															.fields(fields);

			Map<String, Object> props = FieldUtil.getAsProperties(widget);
			insertBuilder.addRecord(props);
			insertBuilder.save();

			widget.setId((Long) props.get("id"));
			
			if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(WidgetType.STATIC)) {
				WidgetStaticContext widgetStaticContext = (WidgetStaticContext) widget;
				
						insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getWidgetStaticModule().getTableName())
						.fields(FieldFactory.getWidgetStaticFields());

				props = FieldUtil.getAsProperties(widgetStaticContext);
				insertBuilder.addRecord(props);
				insertBuilder.save();
				
				if(CardUtil.isDynamicWorkflowGenCard(widgetStaticContext.getStaticKey()) && (widget.getWidgetVsWorkflowContexts() == null || widget.getWidgetVsWorkflowContexts().isEmpty())) {
					throw new Exception("Widget With Static Key "+widgetStaticContext.getStaticKey()+" should have workflow assoiciated with it");
				}
				
				if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD_MINI) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_CARBON_CARD_MINI)) {
					
					if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD_MINI)) {
						
						Long workflowId = WorkflowUtil.addWorkflow(DashboardUtil.WEATHER_WIDGET_WORKFLOW_STRING);
						WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();
						
						widgetVsWorkflowContext.setWidgetId(widget.getId());
						widgetVsWorkflowContext.setWorkflowId(workflowId);
						widgetVsWorkflowContext.setWorkflowName("weather");
						
						if(widgetStaticContext.getBaseSpaceId() != null) {
							widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
						}
						
						DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					}
					
					if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_CARBON_CARD_MINI)) {
						Long workflowId = null;
						
						if(AccountUtil.getCurrentOrg().getId() == 116l) {
							workflowId = WorkflowUtil.addWorkflow(DashboardUtil.CARBON_EMISSION_CARBON_MODULE_CARD);
						}
						else {
							workflowId = WorkflowUtil.addWorkflow(DashboardUtil.CARBON_EMISSION_CARD);
						}
						WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();
						
						widgetVsWorkflowContext.setWidgetId(widget.getId());
						widgetVsWorkflowContext.setWorkflowId(workflowId);
						widgetVsWorkflowContext.setWorkflowName("carbonEmission");
						
						if(widgetStaticContext.getBaseSpaceId() != null) {
							widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
						}
						
						DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					}
				}
				else if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_COST_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_COST_CARD_MINI)) {
					
					Long workflowId = null;
					
					if(AccountUtil.getCurrentOrg().getId() == 116l) {
						workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_COST_THIS_MONTH_CONSUMPTION_COST_MODULE_WORKFLOW);
					}
					else {
						workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_COST_THIS_MONTH_CONSUMPTION_WORKFLOW);
					}
					 
					WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();
					
					widgetVsWorkflowContext.setWidgetId(widget.getId());
					widgetVsWorkflowContext.setWorkflowId(workflowId);
					widgetVsWorkflowContext.setWorkflowName("currentMonth");
					
					if(widgetStaticContext.getBaseSpaceId() != null) {
						widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
					}
					
					DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					
					if(AccountUtil.getCurrentOrg().getId() == 116l) {
						workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_COST_LAST_MONTH_CONSUMPTION_COST_MODULE_WORKFLOW);
					}
					else {
						workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_COST_LAST_MONTH_CONSUMPTION_WORKFLOW);
					}
					
					widgetVsWorkflowContext = new WidgetVsWorkflowContext();
					
					widgetVsWorkflowContext.setWidgetId(widget.getId());
					widgetVsWorkflowContext.setWorkflowId(workflowId);
					widgetVsWorkflowContext.setWorkflowName("lastMonth");
					if(widgetStaticContext.getBaseSpaceId() != null) {
						widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
					}
					DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					
					BaseLineContext baseline = BaseLineAPI.getBaseLine(RangeType.PREVIOUS_MONTH);
					
					String energyCostLastMonth = null;
					
					if(AccountUtil.getCurrentOrg().getId() == 116l) {
						energyCostLastMonth = DashboardUtil.ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_COST_MODULE_WORKFLOW;
					}
					else {
						energyCostLastMonth = DashboardUtil.ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_WORKFLOW;
					}
					
					energyCostLastMonth = energyCostLastMonth.replaceAll("\\$\\$BASELINE_ID\\$\\$", baseline.getId()+"");
					workflowId = WorkflowUtil.addWorkflow(energyCostLastMonth);
					widgetVsWorkflowContext = new WidgetVsWorkflowContext();
					
					widgetVsWorkflowContext.setWidgetId(widget.getId());
					widgetVsWorkflowContext.setWorkflowId(workflowId);
					widgetVsWorkflowContext.setWorkflowName("lastMonthThisDate");
					if(widgetStaticContext.getBaseSpaceId() != null) {
						widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
					}
					DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					
					energyCostLastMonth = DashboardUtil.LAST_MONTH_THIS_DATE.replaceAll("\\$\\$BASELINE_ID\\$\\$", baseline.getId()+"");
					workflowId = WorkflowUtil.addWorkflow(energyCostLastMonth);
					widgetVsWorkflowContext = new WidgetVsWorkflowContext();
					
					widgetVsWorkflowContext.setWidgetId(widget.getId());
					widgetVsWorkflowContext.setWorkflowId(workflowId);
					widgetVsWorkflowContext.setWorkflowName("lastMonthDate");
					if(widgetStaticContext.getBaseSpaceId() != null) {
						widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
					}
					DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
				}
				else if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_CARD_MINI)) {
					
					Long workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_CONSUMPTION_THIS_MONTH_WORKFLOW);
					WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();
					
					widgetVsWorkflowContext.setWidgetId(widget.getId());
					widgetVsWorkflowContext.setWorkflowId(workflowId);
					widgetVsWorkflowContext.setWorkflowName("currentMonth");
					if(widgetStaticContext.getBaseSpaceId() != null) {
						widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
					}
					DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					
					workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_CONSUMPTION_LAST_MONTH_WORKFLOW);
					widgetVsWorkflowContext = new WidgetVsWorkflowContext();
					
					widgetVsWorkflowContext.setWidgetId(widget.getId());
					widgetVsWorkflowContext.setWorkflowId(workflowId);
					widgetVsWorkflowContext.setWorkflowName("lastMonth");
					if(widgetStaticContext.getBaseSpaceId() != null) {
						widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
					}
					DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					
				}
				
				else if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_PROFILE_CARD_MINI)) {
					
					WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();
					
					widgetVsWorkflowContext.setWidgetId(widget.getId());
					widgetVsWorkflowContext.setWorkflowId(null);
					widgetVsWorkflowContext.setWorkflowName("currentMonth");
					if(widgetStaticContext.getBaseSpaceId() != null) {
						widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
					}
					DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					
				}
				else if (widget.getWidgetVsWorkflowContexts() != null && !widget.getWidgetVsWorkflowContexts().isEmpty()) {
					for(WidgetVsWorkflowContext widgetVsWorkflowContext :widget.getWidgetVsWorkflowContexts()) {
						widgetVsWorkflowContext.setWidgetId(widget.getId());
						DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
					}
				}
			}
			else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(WidgetType.WEB)) {
				WidgetWebContext widgetWebContext = (WidgetWebContext) widget;
						insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getWidgetWebModule().getTableName())
						.fields(FieldFactory.getWidgetWebFields());

				props = FieldUtil.getAsProperties(widgetWebContext);
				insertBuilder.addRecord(props);
				insertBuilder.save();
			}
			else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(WidgetType.GRAPHICS)) {
				WidgetGraphicsContext widgetGraphicsContext = (WidgetGraphicsContext) widget;
						insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getWidgetGraphicsModule().getTableName())
						.fields(FieldFactory.getWidgetGraphicsFields());

				props = FieldUtil.getAsProperties(widgetGraphicsContext);
				insertBuilder.addRecord(props);
				insertBuilder.save();
			}
			else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(WidgetType.CARD)) {
				WidgetCardContext widgetCardContext = (WidgetCardContext) widget;
				
				if (widgetCardContext.getCustomScript() != null && !widgetCardContext.getCustomScript().trim().isEmpty()) {
					Long customScriptId = WorkflowUtil.addWorkflow(widgetCardContext.getCustomScript());
					widgetCardContext.setCustomScriptId(customScriptId);
				}
				
						insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getWidgetCardModule().getTableName())
						.fields(FieldFactory.getWidgetCardFields());

				props = FieldUtil.getAsProperties(widgetCardContext);
				insertBuilder.addRecord(props);
				insertBuilder.save();
			}
			else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(WidgetType.CHART)) {
				WidgetChartContext widgetChartContext = (WidgetChartContext) widget;
						insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getWidgetChartModule().getTableName())
						.fields(FieldFactory.getWidgetChartFields());

				props = FieldUtil.getAsProperties(widgetChartContext);
				insertBuilder.addRecord(props);
				insertBuilder.save();
			}
			else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(WidgetType.LIST_VIEW)) {
				
				WidgetListViewContext widgetListViewContext = (WidgetListViewContext) widget;
				insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getWidgetListViewModule().getTableName())
						.fields(FieldFactory.getWidgetListViewFields());

				props = FieldUtil.getAsProperties(widgetListViewContext);
				insertBuilder.addRecord(props);
				insertBuilder.save();
			}
		}
		context.put(FacilioConstants.ContextNames.WIDGET, widget);
		return false;
	}

}
