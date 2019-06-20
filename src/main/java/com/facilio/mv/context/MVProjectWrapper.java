package com.facilio.mv.context;

import java.util.List;

public class MVProjectWrapper {		// only for client

	public MVProjectContext getMvProject() {
		return mvProject;
	}

	public void setMvProject(MVProjectContext mvProject) {
		this.mvProject = mvProject;
	}

	MVProjectContext mvProject;
	List<MVBaseline> baselines;
	List<MVAdjustment> adjustments;
	
	public List<MVBaseline> getBaselines() {
		return baselines;
	}

	public void setBaselines(List<MVBaseline> baselines) {
		this.baselines = baselines;
	}

	public List<MVAdjustment> getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(List<MVAdjustment> adjustments) {
		this.adjustments = adjustments;
	}
}
