package com.facilio.mv.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class ValidateMVProjectCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectContext mvProject = mvProjectWrapper.getMvProject();
		
		List<MVBaseline> baseLines = mvProjectWrapper.getBaselines();
		
		
		List<MVAdjustment> adjustments =  mvProjectWrapper.getAdjustments();
		
		if(adjustments != null && adjustments.size() > 0) {
			
			for(int i=0;i<adjustments.size();i++) {
				MVAdjustment adjustment = adjustments.get(i);
				if(adjustment.getStartTime() < mvProject.getReportingPeriodStartTime() || adjustment.getEndTime() > mvProject.getReportingPeriodEndTime()) {
					throw new IllegalArgumentException("adjustment - "+adjustment.getName() +"'s range should lies between reporting period");					//Adjustments always within reporting period
				}
				// Adjustments should not overlap themselves
				for(int j=0;j<adjustments.size();j++) {
					if(i != j) {
						MVAdjustment adjustmentTemp = adjustments.get(j);
						if(adjustment.getStartTime() <= adjustmentTemp.getStartTime() && adjustment.getEndTime() >=  adjustmentTemp.getStartTime()) {
							throw new IllegalArgumentException("adjustment - "+adjustmentTemp.getName() +"'s start time lies between adjustment - "+adjustment.getName() +"'s range");
						}
						if(adjustment.getStartTime() <= adjustmentTemp.getEndTime() && adjustment.getEndTime() >=  adjustmentTemp.getEndTime()) {
							throw new IllegalArgumentException("adjustment - "+adjustmentTemp.getName() +"'s end time lies between adjustment - "+adjustment.getName() +"'s range");
						}
					}
				}
			}
		}
		
		//Reporting period should overlap baseline period 
		
		long baselineMinStartTime = 0;
		long baselineMaxEndTime = 0;
		for(int i=0;i<baseLines.size();i++) {
			
			MVBaseline baseLine  = baseLines.get(i);
			if(i ==0) {
				baselineMinStartTime = baseLine.getStartTime();
				baselineMaxEndTime = baseLine.getEndTime();
				continue;
			}
			if(baseLine.getStartTime() < baselineMinStartTime) {
				baselineMinStartTime = baseLine.getStartTime();
			}
			if(baseLine.getEndTime() > baselineMaxEndTime) {
				baselineMaxEndTime = baseLine.getEndTime();
			}
		}
		
		if(mvProject.getReportingPeriodStartTime() < baselineMinStartTime || mvProject.getReportingPeriodEndTime() > baselineMaxEndTime) {
			throw new IllegalArgumentException("Reporting period should lies between baseline period");
		}
		return false;
	}

}
