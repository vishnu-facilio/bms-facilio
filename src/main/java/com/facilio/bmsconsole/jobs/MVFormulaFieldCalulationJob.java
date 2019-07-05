package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class MVFormulaFieldCalulationJob extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(ScheduledWorkflowJob.class.getName());

	public void execute(JobContext jc)throws Exception  {
		try {
			long id = jc.getJobId();
			
			MVProjectWrapper projectWrapper = MVUtil.getMVProject(id);
			if(projectWrapper != null) {
				MVProjectContext mvProject = projectWrapper.getMvProject();
				if(projectWrapper.getBaselines() != null) {
					for(MVBaseline baseline :projectWrapper.getBaselines()) {
						
						DateRange range = new DateRange(baseline.getStartTime(), DateTimeUtil.getCurrenTime() < mvProject.getReportingPeriodEndTime() ? DateTimeUtil.getCurrenTime() : mvProject.getReportingPeriodEndTime());
						FormulaFieldAPI.historicalCalculation(baseline.getFormulaField(), range, false);
					}
				}
				if(projectWrapper.getAdjustments() != null) {
					for( MVAdjustment adjustment :projectWrapper.getAdjustments()) {
						if(adjustment.getFormulaField() != null) {
							DateRange range =  new DateRange(adjustment.getStartTime(),  DateTimeUtil.getCurrenTime() <  adjustment.getEndTime() ? DateTimeUtil.getCurrenTime() : adjustment.getEndTime());
							FormulaFieldAPI.historicalCalculation(adjustment.getFormulaField(), range, false);
						}
					}
				}
				if(projectWrapper.getBaselines() != null) {
					for(MVBaseline baseline :projectWrapper.getBaselines()) {
						DateRange range = new DateRange(baseline.getStartTime(), DateTimeUtil.getCurrenTime() < mvProject.getReportingPeriodEndTime() ? DateTimeUtil.getCurrenTime() : mvProject.getReportingPeriodEndTime());
						FormulaFieldAPI.historicalCalculation(baseline.getFormulaFieldWithAjustment(), range, false);
					}
				}
				mvProject.setIsLocked(Boolean.FALSE);
				MVUtil.updateMVProject(mvProject);
			}
		}
		catch (Exception e) {
			CommonCommandUtil.emailException("MV Formula Calculation Failed", "Orgid -- "+AccountUtil.getCurrentOrg().getId() + " jobid -- "+jc.getJobId(), e);
			LOGGER.log(Priority.ERROR, e.getMessage(), e);
		}
	}
}
