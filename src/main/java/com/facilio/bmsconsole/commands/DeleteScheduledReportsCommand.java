package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteScheduledReportsCommand implements Command {
	
	boolean isV2Report;
	public DeleteScheduledReportsCommand(Boolean... isV2Report) {
		this.isV2Report = isV2Report.length == 1 ? isV2Report[0] : false;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (recordIds == null || recordIds.isEmpty()) {
			return false;
		}
		
		FacilioModule module = ModuleFactory.getReportScheduleInfoModule();
		List<FacilioField> fields;
		if (isV2Report) {
			module = ModuleFactory.getReportScheduleInfo();
			fields = FieldFactory.getReportScheduleInfo1Fields();
		}
		else {
			module = ModuleFactory.getReportScheduleInfoModule();
			fields = FieldFactory.getReportScheduleInfoFields();
		}
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ReportInfo> reports = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				ReportInfo report = FieldUtil.getAsBeanFromMap(prop, ReportInfo.class);
				reports.add(report);
			}
		}
		
		List<Long> templateIds = reports.stream().map(ReportInfo::getTemplateId).collect(Collectors.toList());
		
		EventType type = null;
		if (context.containsKey(FacilioConstants.ContextNames.EVENT_TYPE)) {
			type = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		}
		
		if (type == null || type != EventType.EDIT) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			
			int count = builder.delete();
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
			
			TemplateAPI.deleteTemplates(templateIds);
		}
		else {
			context.put(FacilioConstants.ContextNames.TEMPLATE_ID, templateIds.get(0));
		}
		
		FacilioTimer.deleteJobs(recordIds, isV2Report ? "ReportEmailScheduler" : "ReportScheduler");
		
		return false;
	}

}
