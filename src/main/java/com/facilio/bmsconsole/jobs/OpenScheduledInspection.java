package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.qa.context.ResponseContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;

@Log4j
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
        
     // check for inspection Template delete and proceed
        
        ModuleBaseWithCustomFields record = RecordAPI.getRecord(FacilioConstants.Inspection.INSPECTION_TEMPLATE, inspectionResponse.getTemplate().getId());

    	if(record == null) {
    		return;
    	}
		ResourceContext resource = ResourceAPI.getResource(inspectionResponse.getResource().getId());
		if(resource != null && resource.isDecommission()){
			FacilioModule qandaResponseModule = modBean.getModule(FacilioConstants.QAndA.RESPONSE);

			DeleteRecordBuilder<InspectionResponseContext> delete = new DeleteRecordBuilder<InspectionResponseContext>()
					.module(qandaResponseModule)
					.andCondition(CriteriaAPI.getIdCondition(inspectionResponseId, qandaResponseModule))
					.skipModuleCriteria();
			delete.markAsDelete();

			LOGGER.log(Level.ERROR, "Inspection has been deleted as the corresponding resource is decommissioned: Inspection_ID = " + inspectionResponseId);

			return;
		}
        
        if(inspectionResponse != null) {
        	inspectionResponse.setStatus(InspectionResponseContext.Status.OPEN.getIndex());
    		inspectionResponse.setResStatus(ResponseContext.ResponseStatus.NOT_ANSWERED);
    		
    		V3RecordAPI.updateRecord(inspectionResponse, modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE), modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE));
    		V3Util.postCreateRecord(FacilioConstants.Inspection.INSPECTION_RESPONSE, Collections.singletonList(inspectionResponseId), null, null, null);
        }
	}

}
