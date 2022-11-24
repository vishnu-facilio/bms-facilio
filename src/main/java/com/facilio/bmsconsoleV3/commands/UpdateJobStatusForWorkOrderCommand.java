package com.facilio.bmsconsoleV3.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class UpdateJobStatusForWorkOrderCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        
        V3WorkOrderContext workorder = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
    	
        workorder.setJobStatus(WorkOrderContext.JobsStatus.COMPLETED.getValue());

        UpdateRecordBuilder<V3WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<>();
        updateRecordBuilder.module(module)
                .fields(Arrays.asList(fieldMap.get("status"), fieldMap.get("jobStatus")))
                .andCondition(CriteriaAPI.getIdCondition(workorder.getId(), module))
                .skipModuleCriteria();
        updateRecordBuilder.update(workorder);
    	
		return false;
	}

}
