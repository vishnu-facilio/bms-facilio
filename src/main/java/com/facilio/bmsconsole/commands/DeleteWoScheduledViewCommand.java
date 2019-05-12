package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class DeleteWoScheduledViewCommand implements Command {

	
	@Override
	public boolean execute(Context context) throws Exception {
		
		Long id = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (id == null || id == -1) {
			return false;
		}
		
		FacilioModule module = ModuleFactory.getViewScheduleInfoModule();
		List<FacilioField> fields;

			module = ModuleFactory.getViewScheduleInfoModule();
			fields = FieldFactory.getViewScheduleInfoFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		
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
					.andCondition(CriteriaAPI.getIdCondition(id, module));
			
			int count = builder.delete();
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
			
			TemplateAPI.deleteTemplates(templateIds);
		}
		
		else {
			context.put(FacilioConstants.ContextNames.TEMPLATE_ID, templateIds.get(0));
		}
		
		FacilioTimer.deleteJob(id, "ViewEmailScheduler");
		
		return false;
	}

}
