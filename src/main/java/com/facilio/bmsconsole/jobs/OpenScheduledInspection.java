package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class OpenScheduledInspection extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		long inspectionResponseId = jc.getJobId();
		
		InspectionResponseContext inspectionResponse = (InspectionResponseContext)V3RecordAPI.getRecord(FacilioConstants.Inspection.INSPECTION_RESPONSE, inspectionResponseId, InspectionResponseContext.class);
		
		inspectionResponse.setStatus(InspectionResponseContext.Status.OPEN.getIndex());
		
		V3RecordAPI.updateRecord(inspectionResponse, modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE), modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE));
	}

}
