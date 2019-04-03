package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.AggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.report.context.ReportContext.ReportType;

public class ConstructReportDataForPM implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long pmId = (long) context.get("pmId");
		long resourceId = (long) context.get("resourceId");
		
		if(resourceId == -1) {
			throw new Exception("Resource Id cannot be empty");
		}
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule pmModule = bean.getModule(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		ReportContext reportContext = new ReportContext();
		
		context.put(FacilioConstants.ContextNames.MODULE, pmModule);
		
		reportContext.setxAggr(AggregateOperator.DateAggregateOperator.MONTHANDYEAR.getValue());
		reportContext.setxAlias("X");
		reportContext.setModule(pmModule);
		reportContext.setType(ReportType.WORKORDER_REPORT);
		reportContext.setDateOperator(DateOperators.CURRENT_MONTH);
		
		PreventiveMaintenance pm  = PreventiveMaintenanceAPI.getPM(pmId, true);
		if(pm == null) {
			throw new Exception("The corresponding PM does not exist");
		}
		Template template = TemplateAPI.getTemplate(pm.getTemplateId());
		WorkorderTemplate woTemplate = (WorkorderTemplate) template;
		List<TaskSectionTemplate> taskSectionTemplate = woTemplate.getSectionTemplates();
		List<TaskTemplate> taskTemplates = new ArrayList<TaskTemplate>();
		if(taskSectionTemplate.size() == 0) {
			new Exception("The given PM does not have any associated tasks");
		}
		else {
			
			for(int i =0 ;i < taskSectionTemplate.size(); i++) {
				TaskSectionTemplate section = taskSectionTemplate.get(i);
				if(section.getTaskTemplates().size() != 0) {
					taskTemplates.addAll(section.getTaskTemplates());
				}
				
			}
		}
		JSONArray yField = new JSONArray();
		for(TaskTemplate taskTemplate : taskTemplates) {
			JSONObject y_Field = new JSONObject();
			addDataPoints(bean, reportContext, taskTemplate, resourceId);
		}
		
		
		
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		return false;
	}
	
	private void addDataPoints(ModuleBean modBean, ReportContext reportContext, TaskTemplate taskTemplate, long resourceId) throws Exception{
		
		FacilioField readingField = modBean.getField(taskTemplate.getReadingFieldId());
		FacilioModule module = readingField.getModule();
		
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		FacilioField parentIdField = new FacilioField();
		Criteria criteria = new Criteria();
		
		for(FacilioField field: fields) {
			if(field.getName().equals(FacilioConstants.ContextNames.PARENT_ID)) {
				parentIdField = field;
				break;
			}
		}
		
		if(parentIdField.getFieldId() != -1) {
			Condition condition = new Condition();
			condition.setField(parentIdField);
			condition.setOperator(NumberOperators.EQUALS);
			condition.setValue(new Long(resourceId).toString());
			
			criteria.addAndCondition(condition);
		}
		
		ReportDataPointContext dataPointContext = new ReportDataPointContext();
		ReportFieldContext xAxis = new ReportFieldContext();
		ReportYAxisContext yAxis = new ReportYAxisContext();
		ReportFieldContext dateField = new ReportFieldContext();
		FacilioField tTimeField = modBean.getField("ttime", module.getName());
		
		dateField.setField(module, tTimeField);
		dataPointContext.setDateField(dateField);
		dataPointContext.setCriteria(criteria);
		
		xAxis.setField(module, tTimeField);
		yAxis.setField(module, readingField);
		yAxis.setAggr(AggregateOperator.NumberAggregateOperator.SUM.getValue());
		dataPointContext.setxAxis(xAxis);
		dataPointContext.setyAxis(yAxis);
		
		
		Map<String, String> aliases = new HashMap<>();
		aliases.put("actual", readingField.getDisplayName());
		dataPointContext.setAliases(aliases);
		dataPointContext.setName(tTimeField.getName());
		reportContext.addDataPoint(dataPointContext);
		
	}

}
