package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class DeleteScheduledReportsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (recordIds == null || recordIds.isEmpty()) {
			return false;
		}
		
		FacilioModule module = ModuleFactory.getReportScheduleInfoModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportScheduleInfoFields())
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
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
		
		int count = builder.delete();
		
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		
		TemplateAPI.deleteTemplates(templateIds);
		
		FacilioTimer.deleteJobs(recordIds, "ReportScheduler");
		
		// TODO Auto-generated method stub
		return false;
	}

}
