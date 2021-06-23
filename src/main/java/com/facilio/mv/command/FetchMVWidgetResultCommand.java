package com.facilio.mv.command;

import java.util.regex.Pattern;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.V2ReportAction;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class FetchMVWidgetResultCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Integer widgetID = (Integer)context.get(MVUtil.MV_PROJECTS_WIDGET_ID);
		MVProjectWrapper projectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectContext project = projectWrapper.getMvProject();
		MVBaseline baseline = projectWrapper.getBaselines().get(0);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField energyField = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING)).get("totalEnergyConsumptionDelta");
		switch (widgetID) {
		case 1:
			
			JSONArray fieldArray = new JSONArray();
			
			JSONObject dataPointJson = new JSONObject();
			
			dataPointJson.put("parentId", FacilioUtil.getSingleTonJsonArray(project.getMeter().getId()));
			dataPointJson.put("name", project.getMeter().getName() +" (Adjusted Baseline)");
			dataPointJson.put("type", DataPointType.MODULE.getValue());
			
			JSONObject aliaseJson = new JSONObject();
			aliaseJson.put("actual", "A");
			dataPointJson.put("aliases", aliaseJson);
			
			JSONObject yAxisJson = new JSONObject();
			yAxisJson.put("label", project.getMeter().getName() +" (Adjusted Baseline)");
			yAxisJson.put("aggr", BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
			yAxisJson.put("fieldId", baseline.getFormulaFieldWithAjustment().getReadingFieldId());
			dataPointJson.put("yAxis", yAxisJson);
			
			fieldArray.add(dataPointJson);
			
			
			dataPointJson = new JSONObject();
			
			dataPointJson.put("parentId", FacilioUtil.getSingleTonJsonArray(project.getMeter().getId()));
			dataPointJson.put("name", project.getMeter().getName() +" (Actual)");
			dataPointJson.put("type", DataPointType.MODULE.getValue());
			
			aliaseJson = new JSONObject();
			aliaseJson.put("actual", "B");
			dataPointJson.put("aliases", aliaseJson);
			
			yAxisJson = new JSONObject();
			yAxisJson.put("label", project.getMeter().getName() +" (Actual)");
			yAxisJson.put("aggr", BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
			yAxisJson.put("fieldId", energyField.getFieldId());
			dataPointJson.put("yAxis", yAxisJson);
			
			fieldArray.add(dataPointJson);
			
			
			dataPointJson = new JSONObject();
			
//			dataPointJson.put("parentId", null);
			dataPointJson.put("name", project.getMeter().getName() +" (Actual)");
			dataPointJson.put("type", DataPointType.DERIVATION.getValue());
			
			aliaseJson = new JSONObject();
			aliaseJson.put("actual", "C");
			dataPointJson.put("aliases", aliaseJson);
			
			yAxisJson = new JSONObject();
			yAxisJson.put("label", project.getMeter().getName() +" (Actual)");
			yAxisJson.put("dataType", FieldType.DECIMAL.getTypeAsInt());
			dataPointJson.put("yAxis", yAxisJson);
			
			fieldArray.add(dataPointJson);
			
			V2ReportAction reportAction = new V2ReportAction();
			reportAction.setStartTime(baseline.getStartTime());
			reportAction.setEndTime(project.getReportingPeriodEndTime());
			reportAction.setNewFormat(true);
			reportAction.setxAggr(BmsAggregateOperators.DateAggregateOperator.FULLDATE.getValue());		// need to be handled
			reportAction.setMode(ReadingAnalysisContext.ReportMode.TIME_CONSOLIDATED.getValue());
			reportAction.setFields(fieldArray.toJSONString());
			
			WorkflowV2Util.getWorkflowTemplate(1);
			
			JSONObject workflowJson = (JSONObject) WorkflowV2Util.getWorkflowTemplate(1);
			String workflowString = (String) workflowJson.get("workflow");
			
			workflowString = workflowString.replaceAll(Pattern.quote("${reportingStartTime}"), ""+project.getReportingPeriodStartTime());
			
			WorkflowContext workflowContext = new WorkflowContext();
			workflowContext.setWorkflowV2String(workflowString);
			workflowContext.setIsV2Script(true);
			
			reportAction.setTransformWorkflow(workflowContext);
			
			reportAction.fetchReadingsData();
			
			JSONObject resultJSON = reportAction.getResult();
			
			context.put(MVUtil.RESULT_JSON, resultJSON);
			break;
			
		case 2:
			
			fieldArray = new JSONArray();
			
			dataPointJson = new JSONObject();
			
			dataPointJson.put("parentId", FacilioUtil.getSingleTonJsonArray(project.getMeter().getId()));
			dataPointJson.put("type", DataPointType.MODULE.getValue());
			
			aliaseJson = new JSONObject();
			aliaseJson.put("actual", "A");
			dataPointJson.put("aliases", aliaseJson);
			
			yAxisJson = new JSONObject();
			yAxisJson.put("aggr", BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
			yAxisJson.put("fieldId", energyField.getFieldId());
			dataPointJson.put("yAxis", yAxisJson);
			
			fieldArray.add(dataPointJson);
			
			
			dataPointJson = new JSONObject();
			
			dataPointJson.put("parentId", FacilioUtil.getSingleTonJsonArray(project.getMeter().getId()));
			dataPointJson.put("type", DataPointType.MODULE.getValue());
			
			aliaseJson = new JSONObject();
			aliaseJson.put("actual", "B");
			dataPointJson.put("aliases", aliaseJson);
			
			yAxisJson = new JSONObject();
			yAxisJson.put("aggr", BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
			yAxisJson.put("fieldId", baseline.getFormulaFieldWithAjustment().getReadingFieldId());
			dataPointJson.put("yAxis", yAxisJson);
			
			fieldArray.add(dataPointJson);
			
			dataPointJson = new JSONObject();
			
//			dataPointJson.put("parentId", null);
			dataPointJson.put("name", project.getMeter().getName() +" (Actual)");
			dataPointJson.put("type", DataPointType.DERIVATION.getValue());
			
			aliaseJson = new JSONObject();
			aliaseJson.put("actual", "C");
			dataPointJson.put("aliases", aliaseJson);
			
			yAxisJson = new JSONObject();
			yAxisJson.put("label", project.getMeter().getName() +" (Actual)");
			yAxisJson.put("dataType", FieldType.DECIMAL.getTypeAsInt());
			dataPointJson.put("yAxis", yAxisJson);
			
			fieldArray.add(dataPointJson);
			
			reportAction = new V2ReportAction();
			reportAction.setStartTime(project.getReportingPeriodStartTime());
			reportAction.setEndTime(project.getReportingPeriodEndTime());
			reportAction.setNewFormat(true);
			reportAction.setxAggr(BmsAggregateOperators.DateAggregateOperator.FULLDATE.getValue());		// need to be handled
			reportAction.setMode(ReadingAnalysisContext.ReportMode.TIME_CONSOLIDATED.getValue());
			reportAction.setFields(fieldArray.toJSONString());
			
			WorkflowV2Util.getWorkflowTemplate(1);
			
			workflowJson = (JSONObject) WorkflowV2Util.getWorkflowTemplate(1);
			workflowString = (String) workflowJson.get("workflow");
			
			workflowString = workflowString.replaceAll(Pattern.quote("${reportingStartTime}"), ""+project.getReportingPeriodStartTime());
			
			workflowContext = new WorkflowContext();
			workflowContext.setWorkflowV2String(workflowString);
			workflowContext.setIsV2Script(true);
			
			reportAction.setTransformWorkflow(workflowContext);
			
			reportAction.fetchReadingsData();
			
			resultJSON = reportAction.getResult();
			
			context.put(MVUtil.RESULT_JSON, resultJSON);
			break;
		case 3:
	
			break;
		case 4:
		
		break;

		default:
			break;
		}
		
		return false;
	}

}
