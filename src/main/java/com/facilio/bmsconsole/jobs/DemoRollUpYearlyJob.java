package com.facilio.bmsconsole.jobs;

import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class DemoRollUpYearlyJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpYearlyJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {

		ZonedDateTime currentZdt = DateTimeUtil.getDateTime();
		ZonedDateTime thisYearWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(currentZdt);
		int currentWeek = thisYearWeekStartZdt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		ZonedDateTime lastYearWeekStartZdt = DateTimeUtil.getWeekStartTimeOf(thisYearWeekStartZdt.minusYears(1).with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, currentWeek));
		ZonedDateTime lastYearWeekEndZdt = DateTimeUtil.getWeekEndTimeOf(lastYearWeekStartZdt);
		long lastYearWeekStart = lastYearWeekStartZdt.toInstant().toEpochMilli();
		long lastYearWeekEnd = lastYearWeekEndZdt.toInstant().toEpochMilli();
		long thisYearWeekStart = thisYearWeekStartZdt.toInstant().toEpochMilli();
		long weekDiff = (thisYearWeekStart - lastYearWeekStart);
		try {
			System.out.println("DemorollupYearlyJob started in facilioJob");
			LOGGER.info("DemorollupYearlyJob started in facilioJob");
			FacilioChain context = TransactionChainFactory.demoRollUpYearlyChain();
			context.getContext().put(ContextNames.START_TIME, lastYearWeekStart);
			context.getContext().put(ContextNames.END_TIME, lastYearWeekEnd);
			context.getContext().put(ContextNames.TIME_DIFF, weekDiff);
			context.getContext().put(ContextNames.DEMO_ROLLUP_JOB_ORG, jc.getOrgId());
			context.execute();
		} catch (Exception e) {
			CommonCommandUtil.emailException("DemoRollUpYearly", "DemoRollUpYearly Failed - orgid -- " + jc.getOrgId(), e);
				FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
		}
	}
	
}
