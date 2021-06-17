package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.Vlookup;

public class VisitorLogScheduler extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		List<PMTriggerContext> neverEndingTriggers = getNeverEndingTriggers();
		if (CollectionUtils.isNotEmpty(neverEndingTriggers)) {
			for (PMTriggerContext vLogTriggerContext : neverEndingTriggers) {
				long lastGeneratedTime = vLogTriggerContext.getLastGeneratedTime() / 1000;			
				long endTime = VisitorManagementAPI.getEndTime(-1, Collections.singletonList(vLogTriggerContext));
				VisitorLoggingContext parentLog = VisitorManagementAPI.getVisitorLoggingTriggers(vLogTriggerContext.getPmId(), null, false);
				
				if (lastGeneratedTime < endTime) {
					long startTime = vLogTriggerContext.getSchedule().nextExecutionTime(lastGeneratedTime);
					long generatedUpto = 0;
					List<VisitorLoggingContext> children = new ArrayList<VisitorLoggingContext>();
					while (startTime <= endTime) {
						generatedUpto = startTime;
						VisitorLoggingContext childLog = parentLog.getChildLog(startTime);
						children.add(childLog);
						startTime = vLogTriggerContext.getSchedule().nextExecutionTime(startTime);
					}
					parentLog.setLogGeneratedUpto(generatedUpto * 1000);
					vLogTriggerContext.setLastGeneratedTime(generatedUpto * 1000);
					VisitorManagementAPI.updateGeneratedUptoInLogAndAddChildren(vLogTriggerContext, parentLog, children);
			
				}
			}
		}
		
//		List<VisitorLoggingContext> vLogs = VisitorManagementAPI.getRecurringVisitorLogs();
//		if(CollectionUtils.isNotEmpty(vLogs)) {
//			 
//		}
	}

	private List<PMTriggerContext> getNeverEndingTriggers() throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getVisitorLogTriggersModule().getTableName())
				.select(FieldFactory.getVisitorLogTriggerFields())
				.andCondition(CriteriaAPI.getCondition("END_TIME", "endTime", "-1", CommonOperators.IS_EMPTY));
		List<Map<String, Object>> list = builder.get();
		return FieldUtil.getAsBeanListFromMapList(list, PMTriggerContext.class);
	}

}
