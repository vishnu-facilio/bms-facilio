package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.util.JobPlanApi;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;

public class JobPlanAction extends FacilioAction {
	private static final long serialVersionUID = 1L;
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private JobPlanContext jobPlan;
	public JobPlanContext getJobPlan() {
		return jobPlan;
	}
	public void setJobPlan(JobPlanContext jobPlan) {
		this.jobPlan = jobPlan;
	}

	
	public String jobPlanDetails() throws Exception {

		JobPlanContext jobPlan = JobPlanApi.getJobPlan(id);
		setResult(ContextNames.JOB_PLAN, jobPlan);
		
		return SUCCESS;
	}
	
	public String jobPlanList() throws Exception {

		List<JobPlanContext> jobPlans = JobPlanApi.getJobPlans(null);
		setResult(ContextNames.JOB_PLAN_LIST, jobPlans != null ? jobPlans : Collections.EMPTY_LIST);
		
		return SUCCESS;
	}

	public String addJobPlan() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.JOB_PLAN, jobPlan);
		TransactionChainFactory.getAddJobPlanChain().execute(context);
		
		setId(jobPlan.getId());
		jobPlanDetails();
		
		setResult(ContextNames.JOB_PLAN, jobPlan);
		
		return SUCCESS;
	}
	
	public String updateJobPlan() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.JOB_PLAN, jobPlan);
		TransactionChainFactory.getUpdateJobPlanChain().execute(context);
		
		setId(jobPlan.getId());
		jobPlanDetails();
		
		setResult(ContextNames.JOB_PLAN, jobPlan);
		
		return SUCCESS;
	}

}
