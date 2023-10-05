package com.facilio.fsm.commands.serviceTasks;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class PhotoMandatoryValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        List<ServiceTaskContext> serviceTasks = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            for(ServiceTaskContext serviceTask : serviceTasks){
                if (serviceTask != null && serviceTask.getIsPhotoMandatory() && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("completeTask") && (boolean) bodyParams.get("completeTask")){
                    ModuleBean modBean = Constants.getModBean();
                    List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_ATTACHMENTS);
                    GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                            .select(fields)
                            .table("Service_Task_Attachments")
                            .andCondition(CriteriaAPI.getCondition("PARENT", "parentId", String.valueOf(serviceTask.getId()), NumberOperators.EQUALS));
                    List<Map<String,Object>> attachments = selectRecordBuilder.get();
                    if(CollectionUtils.isNotEmpty(attachments) && attachments.size()>0){
                        continue;
                    }
                    throw new FSMException(FSMErrorCode.PHOTO_MANDATORY);
                }
            }
        }
        return false;
    }
}
