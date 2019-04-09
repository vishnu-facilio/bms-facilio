package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class DeleteWoScheduledReportsCommand implements Command {

	boolean isV2Report;
	public DeleteWoScheduledReportsCommand(Boolean... isV2Report) {
		this.isV2Report = isV2Report.length == 1 ? isV2Report[0] : false;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		Long recordIds = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (recordIds == null || recordIds != -1) {
			return false;
		}
		
		FacilioModule module = ModuleFactory.getViewScheduleInfoModule();
		List<FacilioField> fields;
		if (isV2Report) {
			module = ModuleFactory.getReportScheduleInfo();
			fields = FieldFactory.getReportScheduleInfo1Fields();
		}
		else {
			module = ModuleFactory.getViewScheduleInfoModule();
			fields = FieldFactory.getViewScheduleInfoFields();
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
			System.out.println("1232333" + templateIds);
			context.put(FacilioConstants.ContextNames.TEMPLATE_ID, templateIds.get(0));
		}
		
		FacilioTimer.deleteJob(recordIds, isV2Report ? "ReportEmailScheduler" : "ViewEmailScheduler");
		
		return false;
	}

}
