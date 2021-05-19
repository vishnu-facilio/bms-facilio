package com.facilio.bmsconsole.jobs;

import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.qa.context.ResponseContext;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class OpenScheduledInspection extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		long inspectionResponseId = jc.getJobId();
		
		FacilioModule module = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);
		
		SelectRecordsBuilder<InspectionResponseContext> selectRecordsBuilder = new SelectRecordsBuilder<InspectionResponseContext>();
        selectRecordsBuilder.select(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE))
                .module(module)
                .beanClass(InspectionResponseContext.class)
                .andCondition(CriteriaAPI.getIdCondition(inspectionResponseId, module))
                .skipModuleCriteria();
        
        InspectionResponseContext inspectionResponse = selectRecordsBuilder.fetchFirst();
        
        if(inspectionResponse != null) {
        	inspectionResponse.setStatus(InspectionResponseContext.Status.OPEN.getIndex());
    		inspectionResponse.setResStatus(ResponseContext.ResponseStatus.NOT_ANSWERED);
    		
    		V3RecordAPI.updateRecord(inspectionResponse, modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE), modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE));
        }
	}

}
