package com.facilio.fsm.commands.servicePMTemplate;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateServicePMTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePMTemplateContext> servicePMTemplateList = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePMTemplateList)){
            for(ServicePMTemplateContext servicePMTemplate : servicePMTemplateList){
                if(servicePMTemplate.getEstimatedDuration()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Estimated duration is required");
                }
            }
        }
        return false;
    }
}
